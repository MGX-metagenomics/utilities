/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.sffreader;

import de.cebitec.mgx.braf.BufferedRandomAccessFile;
import de.cebitec.mgx.sffreader.datatypes.ReadData;
import de.cebitec.mgx.sffreader.datatypes.ReadHeader;
import de.cebitec.mgx.sffreader.datatypes.SFFHeader;
import de.cebitec.mgx.sffreader.datatypes.SFFIndex;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.Set;

/**
 *
 * @author sj
 */
public class SFFReader {

    private final SFFHeader header;
    private final SFFIndex idx;
    private final RandomAccessFile raf;

    public SFFReader(String file) throws IOException {
        this(new BufferedRandomAccessFile(file, "r"));
    }

    public SFFReader(RandomAccessFile raf) throws IOException {
        this.raf = raf;
        header = SFFHeader.readFrom(raf);

        if (header.getIndexOffset() != 0 || header.getIndexLength() != 0) {
            idx = SFFIndex.readFrom(raf, header);
        } else {
            idx = null;
        }
    }

    public long getIndexOffset() {
        return header.getIndexOffset();
    }

    public long getNumberOfReads() {
        return header.getNumberOfReads();
    }

    public String getKeySequence() {
        return header.getKeySequence();
    }

    public String getFlowChars() {
        return header.getFlowChars();
    }

    public int size() {
        return idx.size();
    }

    public Set<String> keySet() {
        return idx.keySet();
    }

    public long getOffset(String s) {
        return idx.getOffSet(s);
    }

    public String getRead(String name) throws IOException {
        long offset = idx.getOffSet(name);
        raf.seek(offset);
        ReadHeader rh = ReadHeader.readFrom(raf);
        ReadData rd = ReadData.readFrom(raf, header, rh);
        String dna = rd.getBases();
        return dna.substring(rh.getClipLeft() - 1, rh.getClipRight() );
    }

    public void close() throws IOException {
        raf.close();
    }

    public byte[] getQuality(String name) throws IOException {
        long offset = idx.getOffSet(name);
        raf.seek(offset);
        ReadHeader rh = ReadHeader.readFrom(raf);
        ReadData rd = ReadData.readFrom(raf, header, rh);
        int[] qScores = rd.getQualityScores();
        int[] trimmed = Arrays.copyOfRange(qScores, rh.getClipLeft() - 1, rh.getClipRight() );
        byte[] ret = new byte[trimmed.length];
        for (int i =0; i< ret.length; i++) {
            ret[i] = (byte)trimmed[i];
        }
        return ret;
    }

}
