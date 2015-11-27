package de.cebitec.mgx.seqstorage;

import de.cebitec.mgx.sequence.DNAQualitySequenceI;
import de.cebitec.mgx.sequence.SeqStoreException;

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
    public void setQuality(byte[] qual) throws SeqStoreException {
        if (getSequence() != null && getSequence().length != qual.length) {
            throw new SeqStoreException("Length of quality values does not match sequence length");
        }
        quality = new byte[qual.length];
        System.arraycopy(qual, 0, quality, 0, qual.length);
    }

    @Override
    public void setSequence(byte[] dnasequence) throws SeqStoreException {
        if (getQuality() != null && getQuality().length != dnasequence.length) {
            throw new SeqStoreException("Length of sequence does not match quality length");
        }
        super.setSequence(dnasequence);
    }

}
