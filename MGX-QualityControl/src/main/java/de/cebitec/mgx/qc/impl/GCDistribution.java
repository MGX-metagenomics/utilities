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

    private final static int LEN = 101;  // 0-100
    private final float[] GC = new float[LEN];
    private long cnt = 0;

    public GCDistribution() {
        Arrays.fill(GC, 0);
    }

    @Override
    public synchronized void add(DNASequenceI seq) {
        byte[] dna = seq.getSequence();

        float at = 0;
        float gc = 0;
        for (int i = 0; i < dna.length; i++) {
            switch (dna[i]) {
                case 'A':
                    at++;
                    break;
                case 'T':
                    at++;
                    break;
                case 'G':
                    gc++;
                    break;
                case 'C':
                    gc++;
                    break;
                case 'N':
                    break;
                default:
                    System.err.println(new String(dna));
            }
        }
        float f = 100f * (gc / (at + gc));
        GC[Math.round(f)]++;
        cnt++;

    }

    @Override
    public QCResult get() {
        DataRow dr = new DataRow("GC", GC);
        return new QCResult(getName(), new DataRow[]{dr});
    }

    @Override
    public String getName() {
        return "GC";
    }

    @Override
    public long getNumberOfSequences() {
        return cnt;
    }

}
