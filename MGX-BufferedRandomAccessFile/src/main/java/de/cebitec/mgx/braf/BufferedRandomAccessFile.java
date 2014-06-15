/*
 * Based on code from http://www.javaworld.com/article/2077523/build-ci-sdlc/java-tip-26--how-to-improve-java-s-i-o-performance.html
 */
package de.cebitec.mgx.braf;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 *
 * @author sj
 */
public class BufferedRandomAccessFile extends RandomAccessFile {

    private final byte buffer[];
    private int buf_end = 0;
    private int buf_pos = 0;
    private long real_pos = 0;
    private final int bufsize;
    private final static int DEFAULT_BUF_SIZE = 8192;

    public BufferedRandomAccessFile(String filename, String mode) throws IOException {
        this(filename, mode, DEFAULT_BUF_SIZE);
    }

    public BufferedRandomAccessFile(String filename, String mode, int bufsize) throws IOException {
        super(filename, mode);
        invalidate();
        this.bufsize = bufsize;
        buffer = new byte[bufsize];
    }

    @Override
    public int read() throws IOException {
        if (buf_pos >= buf_end) {
            if (fillBuffer() < 0) {
                return -1;
            }
        }
        if (buf_end == 0) {
            return -1;
        } else {
            return buffer[buf_pos++] & 0xFF;
        }
    }

    private int fillBuffer() throws IOException {
        int n = super.read(buffer, 0, bufsize);
        if (n >= 0) {
            real_pos += n;
            buf_end = n;
            buf_pos = 0;
        }
        return n;
    }

    private void invalidate() throws IOException {
        buf_end = 0;
        buf_pos = 0;
        real_pos = super.getFilePointer();
    }

    @Override
    public int read(byte b[], int off, int len) throws IOException {
        int leftover = buf_end - buf_pos;
        if (len <= leftover) {
            System.arraycopy(buffer, buf_pos, b, off, len);
            buf_pos += len;
            return len;
        }
        for (int i = 0; i < len; i++) {
            int c = this.read();
            if (c != -1) {
                b[off + i] = (byte) c;
            } else {
                return i;
            }
        }
        return len;
    }

    @Override
    public int read(byte[] b) throws IOException {
        return read(b, 0, b.length);
    }

    @Override
    public long getFilePointer() throws IOException {
        long l = real_pos;
        return (l - buf_end + buf_pos);
    }

    @Override
    public void seek(long pos) throws IOException {
        int n = (int) (real_pos - pos);
        if (n >= 0 && n <= buf_end) {
            buf_pos = buf_end - n;
        } else {
            super.seek(pos);
            invalidate();
        }
    }
}
