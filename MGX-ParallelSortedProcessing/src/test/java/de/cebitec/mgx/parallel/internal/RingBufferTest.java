/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.parallel.internal;

import java.util.Iterator;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author sj
 */
public class RingBufferTest {

    public RingBufferTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @org.junit.Test
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

    @org.junit.Test
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

    @org.junit.Test
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

    @org.junit.Test
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

    @org.junit.Test
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
            assertTrue("expected " + j + ", got " + ii, j == ii.intValue());
        }
    }
}
