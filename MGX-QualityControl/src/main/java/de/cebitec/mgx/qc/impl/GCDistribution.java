/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.qc.impl;

import de.cebitec.mgx.qc.Analyzer;
import de.cebitec.mgx.qc.DataRow;
import de.cebitec.mgx.qc.QCResult;
import de.cebitec.mgx.sequence.DNASequenceI;
import java.util.Arrays;

/**
 *
 * @author sj
 */
public class GCDistribution implements Analyzer<DNASequenceI> {

    private final static int LEN = 50;
    private int[] AT = new int[LEN];
    private int[] GC = new int[LEN];
    private int maxLen = 0;

    public GCDistribution() {
        Arrays.fill(AT, 0);
        Arrays.fill(GC, 0);
    }

    @Override
    public void add(DNASequenceI seq) {
        byte[] dna = seq.getSequence();
        if (dna.length > maxLen) {
            maxLen = dna.length;
        }

        // extend if necessary
        if (dna.length > AT.length) {
            AT = Arrays.copyOf(AT, dna.length);
            GC = Arrays.copyOf(GC, dna.length);
        }

        for (int i = 0; i < dna.length; i++) {
            switch (dna[i]) {
                case 'A':
                case 'T':
                    AT[i]++;
                    break;
                case 'G':
                case 'C':
                    GC[i]++;
                    break;
            }
        }
    }

    @Override
    public QCResult get() {
        float[] res = new float[maxLen];
        for (int i = 0; i < maxLen; i++) {
            res[i] = 1f * GC[i] / (GC[i] + AT[i]);
        }
        DataRow dr = new DataRow("GC", res);
        return new QCResult("GC", new DataRow[]{dr});
    }

}
