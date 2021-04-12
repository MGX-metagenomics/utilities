package de.cebitec.mgx.seqstorage;

import de.cebitec.mgx.seqcompression.SequenceException;
import de.cebitec.mgx.sequence.DNAQualitySequenceI;

public class QualityDNASequence extends DNASequence implements DNAQualitySequenceI {

    private byte[] quality = null;

    public QualityDNASequence() {
        super();
    }

    public QualityDNASequence(long seqid) {
        super(seqid);
    }

    @Override
    public final byte[] getQuality() {
        return quality;
    }

    @Override
    public void setQuality(byte[] qual) throws SequenceException {
        if (qual != null && getSequence() != null && getSequence().length != qual.length) {
            throw new SequenceException("Length of quality values does not match sequence length");
        }
        if (qual != null) {
            quality = new byte[qual.length];
            System.arraycopy(qual, 0, quality, 0, qual.length);
        } else {
            quality = new byte[0];
        }
    }

    @Override
    public void setSequence(byte[] dnasequence) throws SequenceException {
        if (getQuality() != null && getQuality().length != dnasequence.length) {
            throw new SequenceException("Length of sequence does not match quality length");
        }
        super.setSequence(dnasequence);
    }

}
