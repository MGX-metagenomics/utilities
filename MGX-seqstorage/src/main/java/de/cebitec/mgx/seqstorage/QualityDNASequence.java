package de.cebitec.mgx.seqstorage;

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
    public byte[] getQuality() {
        return quality;
    }

    @Override
    public void setQuality(byte[] qual) {
        quality = new byte[qual.length];
        System.arraycopy(qual, 0, quality, 0, qual.length);
    }
}
