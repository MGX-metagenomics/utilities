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
public class NucleotideDistribution implements Analyzer<DNASequenceI> {

    private final static int LEN = 100;
    private final int[] A = new int[LEN];
    private final int[] T = new int[LEN];
    private final int[] G = new int[LEN];
    private final int[] C = new int[LEN];
    private final int[] N = new int[LEN];
    private int maxLen = 0;
    private long cnt = 0;

    public NucleotideDistribution() {
        Arrays.fill(A, 0);
        Arrays.fill(T, 0);
        Arrays.fill(G, 0);
        Arrays.fill(C, 0);
        Arrays.fill(N, 0);
    }

    @Override
    public synchronized void add(DNASequenceI seq) {
        byte[] dna = seq.getSequence();
        if (dna.length > maxLen) {
            maxLen = dna.length;
        }
        int len = Math.min(LEN, dna.length);
        for (int i = 0; i < len; i++) {
            switch (dna[i]) {
                case 'A':
                    A[i]++;
                    break;
                case 'T':
                    T[i]++;
                    break;
                case 'G':
                    G[i]++;
                    break;
                case 'C':
                    C[i]++;
                    break;
                case 'N':
                    N[i]++;
                    break;
                default:
                    assert false;
            }
        }
        cnt++;
    }

    @Override
    public QCResult get() {
        int max = Math.min(LEN, maxLen);

        float[] a = new float[max];
        float[] t = new float[max];
        float[] g = new float[max];
        float[] c = new float[max];
        float[] n = new float[max];

        for (int i = 0; i < max; i++) {
            int sum = A[i] + T[i] + G[i] + C[i] + N[i];

            a[i] = 1f * A[i] / sum;
            t[i] = 1f * T[i] / sum;
            g[i] = 1f * G[i] / sum;
            c[i] = 1f * C[i] / sum;
            n[i] = 1f * N[i] / sum;

        }
        DataRow[] dr = new DataRow[]{new DataRow("A", a),
            new DataRow("T", t),
            new DataRow("G", g),
            new DataRow("C", c),
            new DataRow("N", n)};
        return new QCResult(getName(), dr);
    }

    @Override
    public String getName() {
        return "Nucleotide distribution";
    }

    @Override
    public long getNumberOfSequences() {
        return cnt;
    }

}
