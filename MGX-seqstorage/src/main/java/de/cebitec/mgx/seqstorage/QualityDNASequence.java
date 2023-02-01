package de.cebitec.mgx.seqstorage;

import de.cebitec.mgx.seqcompression.SequenceException;
import de.cebitec.mgx.seqstorage.internal.DNASequenceValidator;
import de.cebitec.mgx.sequence.DNAQualitySequenceI;
import java.util.Arrays;

public class QualityDNASequence extends DNASequence implements DNAQualitySequenceI {

    private final byte[] quality;
    
    public QualityDNASequence(byte[] dnasequence, byte[] qual) throws SequenceException {
        this(dnasequence, qual, true);
    }

    public QualityDNASequence(byte[] dnasequence, byte[] qual, boolean enableValidation) throws SequenceException {
        super(dnasequence, enableValidation);
        quality = Arrays.copyOf(qual, qual.length);

        if (enableValidation) {
            DNASequenceValidator.validateQuality(qual);
        }

        if (dnasequence.length != qual.length) {
            throw new SequenceException("DNA sequence and quality score length mismatch");
        }
    }

    public QualityDNASequence(long seqid, byte[] dnasequence, byte[] qual, boolean enableValidation) throws SequenceException {
        super(seqid, dnasequence, enableValidation);
        quality = Arrays.copyOf(qual, qual.length);

        if (enableValidation) {
            DNASequenceValidator.validateQuality(qual);
        }

        if (dnasequence.length != qual.length) {
            throw new SequenceException("DNA sequence and quality score length mismatch");
        }
    }

    @Override
    public final byte[] getQuality() {
        return Arrays.copyOf(quality, quality.length);
    }
}
