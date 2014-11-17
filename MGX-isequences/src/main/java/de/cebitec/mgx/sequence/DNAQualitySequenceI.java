package de.cebitec.mgx.sequence;

/**
 *
 * @author sj
 */
public interface DNAQualitySequenceI extends DNASequenceI {

    public byte[] getQuality();
    public void setQuality(byte[] qual);
}
