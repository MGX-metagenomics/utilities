/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.sffreader.datatypes;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 *
 * @author sj
 */
public class ReadData {

    public static ReadData readFrom(RandomAccessFile raf, SFFHeader sh, ReadHeader rh) throws IOException {
        int[] flowgram_values = new int[sh.getNumberOfFlows() * sh.getFlowgramBytesPerFlow()];
        Util.readUint8s(raf, flowgram_values);

        int[] flow_index_per_base = new int[rh.getNumberOfBases()];
        Util.readUint8s(raf, flow_index_per_base);

        byte[] bases = new byte[rh.getNumberOfBases()];
        raf.read(bases);

        int[] quality_scores = new int[rh.getNumberOfBases()];
        Util.readUint8s(raf, quality_scores);

        Util.pad8(raf);
        return new ReadData(flowgram_values, flow_index_per_base, bases, quality_scores);
    }

    private final int[] flowgram_values;
    private final int[] flow_index_per_base;
    private final byte[] bases;
    private final int[] quality_scores;

    public ReadData(int[] flowgram_values, int[] flow_index_per_base, byte[] bases, int[] quality_scores) {
        this.flowgram_values = flowgram_values;
        this.flow_index_per_base = flow_index_per_base;
        this.bases = bases;
        this.quality_scores = quality_scores;
    }

    public String getBases() {
        return new String(bases);
    }

    public int[] getQualityScores() {
        return quality_scores;
    }
}
