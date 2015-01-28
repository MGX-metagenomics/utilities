/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.parallel.internal;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Iterator;

/**
 *
 * @author sj
 */
public class RingBuffer<T> {

    private T[] buf;
    private final int capacity;
    //
    private int getOffset = 0;
    private int addOffset = 0;

    public RingBuffer(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Cannot create empty or negative-sized RingBuffer.");
        }

        this.capacity = capacity;
    }

    public synchronized T next() {
        if (capacity != addOffset) {
            throw new IllegalArgumentException("RingBuffer not completely filled.");
        }
        // avoid int overflow
        if (getOffset >= capacity) {
            getOffset %= capacity;
        }
        return buf[getOffset++];
    }

    public Iterator<T> iterator() {
        if (capacity != addOffset) {
            throw new IllegalArgumentException("RingBuffer not completely filled.");
        }
        return new RingIterator<>(buf, capacity);
    }

    public synchronized T peek() {
        return buf[getOffset];
    }

    public synchronized void add(T t) {
        if (buf == null) {
            buf = (T[]) Array.newInstance(t.getClass(), capacity);
        }
        if (addOffset >= capacity) {
            throw new IllegalArgumentException("RingBuffer is already filled.");
        }
        buf[addOffset++] = t;
    }

    public T[] fetchall() {
        return Arrays.copyOf(buf, buf.length);
    }

    private final class RingIterator<T> implements Iterator<T> {

        private int curPos = 0;
        private final T[] b;
        private final int capa;

        public RingIterator(T[] buf, int capacity) {
            this.b = buf;
            this.capa = capacity;
        }

        @Override
        public boolean hasNext() {
            return true;
        }

        @Override
        public T next() {
            if (curPos >= capa) {
                curPos %= capa;
            }
            return b[curPos++];
        }

        @Override
        public void remove() {
            return;
        }

    }

}
