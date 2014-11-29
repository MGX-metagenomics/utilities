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
public class LengthDistribution implements Analyzer<DNASequenceI> {

    private final static int LEN = 50;
    private int[] l = new int[LEN];

    public LengthDistribution() {
        Arrays.fill(l, 0);
    }

    @Override
    public void add(DNASequenceI seq) {
        byte[] dna = seq.getSequence();

        // extend if necessary
        if (dna.length > l.length) {
            l = Arrays.copyOf(l, dna.length);
        }

        l[dna.length]++;
    }

    @Override
    public QCResult get() {
        float[] res = new float[l.length];
        long sum = 0;
        for (int i = 0; i < l.length; i++) {
            sum += l[i];
        }
        for (int i = 0; i < res.length; i++) {
            res[i] = 1f*l[i]/sum;
        }
        DataRow dr = new DataRow("Read length", res);
        return new QCResult("length", new DataRow[]{dr});
    }
}
