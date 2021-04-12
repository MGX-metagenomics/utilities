package de.cebitec.mgx.sequence;

import de.cebitec.mgx.seqcompression.SequenceException;

/**
 *
 * @author sj
 */
public interface DNAQualitySequenceI extends DNASequenceI {

    public byte[] getQuality() throws SequenceException;

    public void setQuality(byte[] qual) throws SequenceException;
}
