/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.seqstorage.encoding;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Patrick
 * Blumenkamp<patrick.blumenkamp@computational.bio.uni-giessen.de>
 */
public class RingBufferTest {

    /**
     * Test of next method, of class RingBuffer.
     */
    @Test
    public void testNext() {
        RingBuffer rb = new RingBuffer(4);
        rb.add((byte) 10);
        assertEquals(1, rb.getUnreadLength());
        assertEquals(10, rb.next());
        assertEquals(0, rb.getUnreadLength());
        try {
            rb.next();
            fail("Cannot use next() on empty Buffer.");
        } catch (Exception ex) {
        }
    }

    /**
     * Test of peek method, of class RingBuffer.
     */
    @Test
    public void testPeek() {
        RingBuffer rb = new RingBuffer(4);
        rb.add((byte) 10);
        rb.add((byte) 15);
        assertEquals(2, rb.getUnreadLength());
        assertEquals(10, rb.peek());
        assertEquals(2, rb.getUnreadLength());
        assertEquals(10, rb.peek());
    }

    /**
     * Test of add method, of class RingBuffer.
     */
    @Test
    public void testAdd() {
        RingBuffer rb = new RingBuffer(4);
        assertEquals(0, rb.getUnreadLength());
        rb.add((byte) 10);
        rb.add((byte) 15);
        assertEquals(2, rb.getUnreadLength());
        assertEquals(10, rb.next());
        assertEquals(15, rb.next());
        assertEquals(0, rb.getUnreadLength());
        rb.add((byte) 10);
        rb.add((byte) 15);
        rb.add((byte) 20);
        rb.add((byte) 25);
        assertEquals(4, rb.getUnreadLength());
        try {
            rb.add((byte) 30);
            fail("Buffer is full.");
        } catch (Exception ex) {
        }
    }

    /**
     * Test of flush method, of class RingBuffer.
     */
    @Test
    public void testFlush() {
        RingBuffer rb = new RingBuffer(4);
        rb.add((byte) 10);
        rb.add((byte) 15);
        rb.add((byte) 20);
        rb.add((byte) 25);
        assertArrayEquals(new byte[]{10, 15, 20, 25}, rb.flush());
    }

    /**
     * Test of getUnreadLength method, of class RingBuffer.
     */
    @Test
    public void testGetUnreadLength() {
        RingBuffer rb = new RingBuffer(4);
        assertEquals(0, rb.getUnreadLength());
        rb.add((byte) 10);
        assertEquals(1, rb.getUnreadLength());
        rb.add((byte) 15);
        assertEquals(2, rb.getUnreadLength());
        rb.add((byte) 20);
        assertEquals(3, rb.getUnreadLength());
        rb.add((byte) 25);
        assertEquals(4, rb.getUnreadLength());
        rb.next();
        assertEquals(3, rb.getUnreadLength());
        rb.next();
        assertEquals(2, rb.getUnreadLength());
        rb.next();
        assertEquals(1, rb.getUnreadLength());
        rb.next();
        assertEquals(0, rb.getUnreadLength());
    }

    /**
     * Test of empty method, of class RingBuffer.
     */
    @Test
    public void testEmpty() {
        RingBuffer rb = new RingBuffer(4);
        assertTrue(rb.empty());
        rb.add((byte) 2);
        assertFalse(rb.empty());
        rb.next();
        assertTrue(rb.empty());
    }

}
