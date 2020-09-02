/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.seqstorage.internal;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 *
 * a a bytebuffer that is able to map >2GB files into off-heap memory
 *
 * @author sj
 */
public class LargeMappedByteBuffer {

    private static final int MAPPING_SIZE = 1 << 30; // 1 GB per slice

    private final RandomAccessFile raf;
    private MappedByteBuffer[] mappings;
    //
    private int curSliceIdx;
    private long curOffset;
    private long fileLength;

    public LargeMappedByteBuffer(String fileName) throws IOException {
        this(new File(fileName));
    }

    public LargeMappedByteBuffer(File f) throws IOException {
        this(new RandomAccessFile(f, "r"));
    }

    public LargeMappedByteBuffer(RandomAccessFile ra) throws IOException {
        this.raf = ra;
        curSliceIdx = 0;
        curOffset = 0;
        fileLength = raf.length();
        
        if (fileLength == 0) {
            throw new IllegalArgumentException("Unable to map empty file.");
        }

        int numSlices = (int) (1 + fileLength / MAPPING_SIZE);
        mappings = new MappedByteBuffer[numSlices];

        int idx = 0;
        for (long offset = 0; offset < raf.length(); offset += MAPPING_SIZE) {
            long sliceSize = Math.min(raf.length() - offset, MAPPING_SIZE);
            mappings[idx++] = raf.getChannel().map(FileChannel.MapMode.READ_ONLY, offset, sliceSize);
        }
    }

    public final LargeMappedByteBuffer position(long newPosition) {
        int newSliceIdx = (int) (newPosition / MAPPING_SIZE);
        if (newSliceIdx < 0 || newSliceIdx >= mappings.length) {
            throw new IllegalArgumentException("Attempt to position outside of mapped file.");
        }
        curSliceIdx = newSliceIdx;
        MappedByteBuffer backBuffer = mappings[curSliceIdx];
        int targetPos = (int) (newPosition - (curSliceIdx * MAPPING_SIZE));
        backBuffer.position(targetPos);
        curOffset = newPosition;
        return this;
    }

    public final long remaining() {
        return fileLength - curOffset;
    }

    public final LargeMappedByteBuffer get(byte[] dest, int offset, int length) {

        int bytesNeeded = length;
        int targetOffset = offset;

        while (bytesNeeded > 0) {

            int canReadBytes = Math.min((bytesNeeded), mappings[curSliceIdx].remaining());

            if (canReadBytes > 0) {
                mappings[curSliceIdx].get(dest, targetOffset, canReadBytes);
                bytesNeeded -= canReadBytes;
                targetOffset += canReadBytes;
            } else {
                // advance to next slice, position at slice start
                curSliceIdx++;

                if (curSliceIdx >= mappings.length) {
                    throw new IllegalArgumentException("Attempt to read outside of mapped file.");
                }
                mappings[curSliceIdx].position(0);
            }

        }

        curOffset += length;
        return this;
    }

    public void close() throws IOException {
        mappings = null;
        raf.close();
    }
}
