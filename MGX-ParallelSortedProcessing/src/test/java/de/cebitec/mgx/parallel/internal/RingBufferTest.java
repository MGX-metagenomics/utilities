/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.parallel.internal;

import java.util.Iterator;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.Test;

/**
 *
 * @author sj
 */
public class RingBufferTest {

    public RingBufferTest() {
    }

    @Test
    public void testCapacity() {
        RingBuffer<String> rb = new RingBuffer<>(2);
        rb.add("foo");
        rb.add("bar");
        try {
            rb.add("baz");
        } catch (IllegalArgumentException ex) {
            return;
        }
        fail();
    }

    @Test
    public void testWrapAround() {
        RingBuffer<String> rb = new RingBuffer<>(2);
        rb.add("foo");
        rb.add("bar");

        String first = rb.next();
        assertEquals("foo", first);

        String second = rb.next();
        assertEquals("bar", second);

        String third = rb.next();
        assertEquals("foo", third);
    }

    @Test
    public void testMulti() {
        RingBuffer<String> rb = new RingBuffer<>(2);
        rb.add("foo");
        rb.add("bar");

        String s = rb.next();
        for (int i = 0; i < 100000; i++) {
            String s2 = rb.next();
            assertNotNull(s2);
            assertNotSame(s, s2);
            s = s2;
        }
    }

    @Test
    public void testIterator() {
        RingBuffer<String> rb = new RingBuffer<>(2);
        rb.add("foo");
        rb.add("bar");

        Iterator<String> iter = rb.iterator();
        int i = 0;
        String s = iter.next();
        assertNotNull(s);

        while (iter.hasNext() && i < 100000) {
            String s2 = iter.next();
            assertNotSame(s, s2);
            s = s2;
            i++;
        }
    }

    @Test
    public void testIterator2() {
        RingBuffer<Integer> rb = new RingBuffer<>(100);
        for (int i = 0; i < 100; i++) {
            rb.add(i);
        }

        Iterator<Integer> iter = rb.iterator();
        int i = -1;

        while (iter.hasNext() && i < 100000) {
            Integer ii = iter.next();
            i++;
            int j = i;
            if (j >= 100) {
                j %= 100;
            }
            assertTrue(j == ii.intValue(), "expected " + j + ", got " + ii);
        }
    }
}
