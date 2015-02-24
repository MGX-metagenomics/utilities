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
    private int maxLen = 0;
    private long cnt = 0;

    public LengthDistribution() {
        Arrays.fill(l, 0);
    }

    @Override
    public synchronized void add(DNASequenceI seq) {
        int dnaLen = seq.getSequence().length;
        if (dnaLen > maxLen) {
            maxLen = dnaLen;
        }

        // extend if necessary
        if (dnaLen >= l.length) {
            l = Arrays.copyOf(l, dnaLen + 1);
        }

        l[dnaLen]++;
        cnt++;
    }

    @Override
    public QCResult get() {
        float[] res = new float[maxLen + 1];
        long sum = 0;
        for (int i : l) {
            sum += i;
        }
        if (sum == 0 && maxLen == 0) {
            res[0] = 0f;
        } else {
            for (int i = 0; i < res.length; i++) {
                res[i] = 1f * l[i] / sum;
            }
        }
        DataRow dr = new DataRow("Read length", res);
        return new QCResult(getName(), new DataRow[]{dr});
    }

    @Override
    public String getName() {
        return "Read length";
    }

    @Override
    public long getNumberOfSequences() {
        return cnt;
    }
}