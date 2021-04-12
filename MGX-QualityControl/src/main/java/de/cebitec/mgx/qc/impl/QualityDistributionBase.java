package de.cebitec.mgx.qc.impl;

import de.cebitec.mgx.qc.*;
import de.cebitec.mgx.seqcompression.SequenceException;
import de.cebitec.mgx.sequence.DNAQualitySequenceI;
import java.util.*;

/**
 *
 * @author Patrick Blumenkamp
 */
public abstract class QualityDistributionBase implements Analyzer<DNAQualitySequenceI>{

    private static final int STARTLENGTH = 2_000;
    
    //public static final String NAME = "Sequence quality";
    
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
    
//    @Override
//    public String getName() {
//        return NAME;
//    }

    @Override
    public void add(DNAQualitySequenceI seq) throws SequenceException {
        byte[] qual = seq.getQuality();
        
        maxLength = Math.max(maxLength, qual.length);
        
        if (qual.length > count.length){
            count = Arrays.copyOf(count, qual.length+100);
            mean = Arrays.copyOf(mean, qual.length+100);
            mk = Arrays.copyOf(mk, qual.length+100);
            mkOld = Arrays.copyOf(mkOld, qual.length+100);
            sk = Arrays.copyOf(sk, qual.length+100);
            skOld = Arrays.copyOf(skOld, qual.length+100);
        }
        
        for (int i=0; i<qual.length; i++){  
            count[i]++;
            
            //standard deviation
            if (count[i] == 1){             //first entry
                mk[i] = (double)qual[i];
            } else {
                mkOld[i] = mk[i];
                skOld[i] = sk[i];
                mk[i] = mkOld[i] + (qual[i] - mkOld[i])/count[i];
                sk[i] = skOld[i] + (qual[i] - mkOld[i]) * (qual[i] - mk[i]);
            }
            
            //average
            mean[i] = ((count[i]-1) * mean[i] + qual[i]) / count[i];
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
        
        for(int i=0; i<maxLength; i++){
            meanList[i] = (float)mean[i];
            if (count[i] < 2){
                stdDevList[i] = 0;
            } else {
                stdDevList[i] = (float) Math.sqrt(sk[i]/(count[i]-1));
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

    @Override
    public String getDescription() {
        return "Phred quality score distribution";
    }
    
}
