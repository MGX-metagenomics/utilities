package de.cebitec.mgx.sequence;

import de.cebitec.mgx.seqcompression.SequenceException;

/**
 *
 * @author sj
 */
public interface DNAQualitySequenceI extends DNASequenceI {

    public byte[] getQuality() throws SequenceException;

}
