package de.cebitec.mgx.sequence;

import org.apache.commons.math3.util.FastMath;

/**
 *
 * @author sj
 */
public class PhredUtil {

    private static final double[] phred_to_prob = new double[50];

    static {
        for (int i = 0; i < phred_to_prob.length; i++) {
            phred_to_prob[i] = FastMath.pow(10d, -1d * i / 10d);
        }
    }

    private PhredUtil() {
    }

    public static double[] phredToRaw(byte[] phred) {
        if (phred == null) {
            return null;
        }
        double[] ret = new double[phred.length];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = phredToRaw((int) phred[i]);
        }
        return ret;
    }

    public static double[] phredToRaw(int[] phred) {
        if (phred == null) {
            return null;
        }
        double[] ret = new double[phred.length];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = phredToRaw(phred[i]);
        }
        return ret;
    }

    public static double phredToRaw(int phredScore) {
        return phred_to_prob[phredScore];
    }

    public static double[] rawToPhred(double[] probs) {
        if (probs == null) {
            return null;
        }
        double[] ret = new double[probs.length];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = rawToPhred(probs[i]);
        }
        return ret;
    }

    public static double rawToPhred(double prob) {
        return -10d * FastMath.log10(prob);
    }

}
