package de.cebitec.mgx.seqstorage.internal;

import de.cebitec.mgx.seqcompression.SequenceException;

/**
 *
 * @author sj
 */
public class DNASequenceValidator {

    private DNASequenceValidator() {
    }

    public static void validateSequence(byte[] dnasequence) throws SequenceException {

        if (dnasequence == null) {
            throw new SequenceException("DNA sequence is null.");
        }

        // validate nucleotide sequence, convert to uppercase if necessary
        for (int i = 0; i < dnasequence.length; i++) {
            switch (dnasequence[i]) {
                case 'A':
                case 'T':
                case 'G':
                case 'C':
                case 'R':
                case 'Y':
                case 'S':
                case 'W':
                case 'K':
                case 'M':
                case 'B':
                case 'D':
                case 'H':
                case 'V':
                case 'N':
                    break;
                case 'a':
                    dnasequence[i] = 'A';
                    break;
                case 't':
                    dnasequence[i] = 'T';
                    break;
                case 'g':
                    dnasequence[i] = 'G';
                    break;
                case 'c':
                    dnasequence[i] = 'C';
                    break;
                case 'r':
                    dnasequence[i] = 'R';
                    break;
                case 'y':
                    dnasequence[i] = 'Y';
                    break;
                case 's':
                    dnasequence[i] = 'S';
                    break;
                case 'w':
                    dnasequence[i] = 'W';
                    break;
                case 'k':
                    dnasequence[i] = 'K';
                    break;
                case 'm':
                    dnasequence[i] = 'M';
                    break;
                case 'b':
                    dnasequence[i] = 'B';
                    break;
                case 'd':
                    dnasequence[i] = 'D';
                    break;
                case 'h':
                    dnasequence[i] = 'H';
                    break;
                case 'v':
                    dnasequence[i] = 'V';
                    break;
                case 'n':
                    dnasequence[i] = 'N';
                    break;
                default:
                    throw new SequenceException("Illegal nucleotide " + dnasequence[i] + " at position " + i + " of sequence");
            }
        }
    }

    public static void validateQuality(byte[] quality) throws SequenceException {

        if (quality == null) {
            throw new SequenceException("DNA quality is null.");
        }

        for (byte b : quality) {
            int i = (int) b;
            if (i < 0 || i > 45) {
                throw new SequenceException("Invalid Phred score: " + i);
            }
        }
    }
}
