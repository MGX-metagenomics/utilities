package de.cebitec.mgx.seqstorage.encoding;

/**
 *
 * @author Patrick
 * Blumenkamp<patrick.blumenkamp@computational.bio.uni-giessen.de>
 */
public final class RingBuffer {

    private byte[] buf;
    private final int capacity;
    //
    private int getOffset = 0;
    private int addOffset = 0;

    private boolean addOffsetRound = false;
    private boolean getOffsetRound = false;

    public RingBuffer(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Cannot create empty or negative-sized RingBuffer.");
        }

        this.capacity = capacity;
    }

    public synchronized byte next() {
        if (getOffset == addOffset && getOffsetRound == addOffsetRound) {
            throw new IllegalArgumentException("No next value.");
        }
        // avoid int overflow
        if (getOffset >= capacity) {
            getOffset %= capacity;
            getOffsetRound = !getOffsetRound;
        }
        return buf[getOffset++];
    }

//        public Iterator<T> iterator() {
//            if (capacity != addOffset) {
//                throw new IllegalArgumentException("RingBuffer not completely filled.");
//            }
//            return new RingIterator<>(buf, capacity);
//        }
    public synchronized byte peek() {
        return buf[getOffset];
    }

    @SuppressWarnings("unchecked")
    public synchronized void add(byte b) {
        if (buf == null) {
            buf = new byte[capacity];
//            buf[addOffset++] = b;
//            return;
        }
        if (addOffset == getOffset && addOffsetRound != getOffsetRound) {
            throw new IllegalArgumentException("Buffer overflow.");
        }
        if (addOffset >= capacity) {
            addOffset %= capacity;
            addOffsetRound = !addOffsetRound;
        }
        buf[addOffset++] = b;
    }

    public byte[] flush() {
        byte[] flushedValues = new byte[this.getUnreadLength()];
        for (int i = 0; i < flushedValues.length; i++) {
            flushedValues[i] = this.next();
        }
        return flushedValues;
    }

    public int getUnreadLength() {
        if (getOffset > addOffset) {
            return capacity - getOffset + addOffset;
        } else if (getOffset < addOffset) {
            return addOffset - getOffset;
        } else if (getOffset == addOffset && getOffsetRound != addOffsetRound) {
            return capacity;
        } else {
            return 0;
        }
    }

    public boolean empty() {
        return getUnreadLength() == 0;
    }

}
