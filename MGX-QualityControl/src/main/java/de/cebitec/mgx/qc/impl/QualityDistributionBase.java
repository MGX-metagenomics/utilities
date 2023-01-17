package de.cebitec.mgx.qc.impl;

import de.cebitec.mgx.qc.*;
import de.cebitec.mgx.seqcompression.SequenceException;
import de.cebitec.mgx.sequence.DNAQualitySequenceI;
import de.cebitec.mgx.sequence.PhredUtil;
import java.util.Arrays;
import org.apache.commons.math3.util.FastMath;

/**
 *
 * @author Patrick Blumenkamp
 */
public abstract class QualityDistributionBase implements Analyzer<DNAQualitySequenceI> {

    private static final int STARTLENGTH = 2_000;

    private long[] count = new long[STARTLENGTH];
    private double[] mean = new double[STARTLENGTH];
    private double[] mk = new double[STARTLENGTH];
    private double[] mkOld = new double[STARTLENGTH];
    private double[] sk = new double[STARTLENGTH];
    private double[] skOld = new double[STARTLENGTH];
    private int maxLength = 0;

    protected QualityDistributionBase() {
        Arrays.fill(count, 0);
        Arrays.fill(mean, 0);
        Arrays.fill(mk, 0);
        Arrays.fill(mkOld, 0);
        Arrays.fill(sk, 0);
        Arrays.fill(skOld, 0);
    }

    @Override
    public void add(DNAQualitySequenceI seq) throws SequenceException {
        
        // since phred scores are log-scaled, we need to convert them
        // back to raw probabilities first before we're able to compute
        // averages
        //
        double[] rawProbs = PhredUtil.phredToRaw(seq.getQuality());

        maxLength = FastMath.max(maxLength, rawProbs.length);

        // resize buffers, if needed
        if (rawProbs.length > count.length) {
            count = Arrays.copyOf(count, rawProbs.length + 100);
            mean = Arrays.copyOf(mean, rawProbs.length + 100);
            mk = Arrays.copyOf(mk, rawProbs.length + 100);
            mkOld = Arrays.copyOf(mkOld, rawProbs.length + 100);
            sk = Arrays.copyOf(sk, rawProbs.length + 100);
            skOld = Arrays.copyOf(skOld, rawProbs.length + 100);
        }

        for (int i = 0; i < rawProbs.length; i++) {
            count[i]++;

            //standard deviation
            if (count[i] == 1) {             //first entry
                mk[i] = rawProbs[i];
            } else {
                mkOld[i] = mk[i];
                skOld[i] = sk[i];
                mk[i] = mkOld[i] + (rawProbs[i] - mkOld[i]) / count[i];
                sk[i] = skOld[i] + (rawProbs[i] - mkOld[i]) * (rawProbs[i] - mk[i]);
            }

            //average
            mean[i] = ((count[i] - 1) * mean[i] + rawProbs[i]) / count[i];
        }
    }

    @Override
    public void addPair(DNAQualitySequenceI seq1, DNAQualitySequenceI seq2) throws SequenceException {
        add(seq1);
        add(seq2);
    }

    @Override
    public QCResult get() {
        float[] meanList = new float[maxLength];
        float[] stdDevList = new float[maxLength];
        
        // convert back to log scale
        mean = PhredUtil.rawToPhred(mean);

        for (int i = 0; i < maxLength; i++) {
            meanList[i] = (float) mean[i];
            if (count[i] < 2) {
                stdDevList[i] = 0;
            } else {
                stdDevList[i] = (float) FastMath.sqrt(sk[i] / (count[i] - 1));
            }
        }

        DataRow[] dr = new DataRow[]{
            new DataRow("mean", meanList),
            new DataRow("standard deviation", stdDevList)};
        return new QCResult(getName(), getDescription(), dr);
    }

    @Override
    public long getNumberOfSequences() {
        return count[0];
    }

//    @Override
//    public String getDescription() {
//        return "Phred quality score distribution";
//    }

}
