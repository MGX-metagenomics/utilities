package de.cebitec.mgx.seqstorage;

import de.cebitec.mgx.seqstorage.encoding.ByteUtils;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.GZIPInputStream;

public class ByteStreamTokenizer implements Enumeration {

    private final InputStream in;
    private final int DEFAULT_BUFSIZE = 49152;
    private final int bufferSize;
    private long bufferRefillPosition;
    private byte buffer[];
    private final byte separator;
    private int startpos = 0;
    private int endpos = 0;
    private byte elem[] = null;

    public ByteStreamTokenizer(String fname, boolean gzipCompressed, byte separatorChar, int skipBytes) throws FileNotFoundException, IOException {
        bufferSize = DEFAULT_BUFSIZE;
        buffer = new byte[bufferSize];
        separator = separatorChar;
        bufferRefillPosition = buffer.length * 8 / 10;

        // set up reader, skip over offset bytes, pre-fill buffer
        if (gzipCompressed) {
            InputStream gzstream = new GZIPInputStream(new FileInputStream(fname));
            in = new BufferedInputStream(gzstream);
        } else {
            in = new BufferedInputStream(new FileInputStream(fname));
        }
        in.skip(skipBytes);
        fillBuffer();
    }

    @Override
    public boolean hasMoreElements() {
        int breakpos;

        /* 
         * try to serve request from buffer
         */

        if ((breakpos = getSeparatorPos(startpos, endpos)) != -1) {
            elem = ByteUtils.substring(buffer, startpos, breakpos - 1);

            /* shift buffer to left if we're more than 80% done with the buffers contents
             * and fill remainder of buffer with fresh data
             */
            if (startpos > bufferRefillPosition) {
                moveBuffer(breakpos + 1);
                fillBuffer();
            } else {
                startpos = breakpos + 1;
            }
            return true;
        }

        /* 
         * buffer unable to handle request, need to fetch more data from file,
         * if possible
         */

        while ((breakpos = getSeparatorPos(startpos, endpos)) == -1) {
            // move buffer contents to front of buffer
            moveBuffer(startpos);
            enlargeBuffer();

            if (fillBuffer()) {
                break;
            }
        }

        /*
         * re-check buffer after reading - if we still don't see a valid element,
         * there aren't any
         */

        if ((breakpos = getSeparatorPos(startpos, endpos)) != -1) {
            elem = ByteUtils.substring(buffer, startpos, breakpos - 1);
            startpos = breakpos + 1;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public byte[] nextElement() {
        return elem;
    }

    public void close() {
        try {
            in.close();
        } catch (IOException ex) {
        }
    }

    private void moveBuffer(int start) {
        if (start == 0) {
            return;
        }
        System.arraycopy(buffer, start, buffer, 0, buffer.length - start);

        // update global start and end positions
        endpos -= start;
        startpos = 0;
    }

    private int getSeparatorPos(int start, int end) {
        for (int i = start; i <= end - 1; i++) {
            if (buffer[i] == separator) {
                return i;
            }
        }
        return -1;
    }

    /*
     * returns true if EOF is reached, false otherwise
     */
    private boolean fillBuffer() {
        int bytesRead;
        try {
            if ((bytesRead = in.read(buffer, endpos, buffer.length - endpos)) != -1) {
                //System.err.println("read " + bytesRead + " bytes");
                endpos += bytesRead;
            } else {
                return true;
            }
        } catch (IOException ex) {
            //System.err.println("EXCEPTION");
        }
        return false;
    }

    private void enlargeBuffer() {
        // double the buffer size and copy contents
        //System.err.println("buffer doubled to " + buffer.length * 2 + ", startpos " + startpos + ", endpos " + endpos);
        byte newbuf[] = new byte[buffer.length * 2];
        System.arraycopy(buffer, 0, newbuf, 0, buffer.length);
        buffer = newbuf;
        bufferRefillPosition = buffer.length * 8 / 10;
    }
}
