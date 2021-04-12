package de.cebitec.mgx.sequence;

import de.cebitec.mgx.seqcompression.SequenceException;

/**
 *
 * @author sjaenick
 */
public interface DNASequenceI {

    public long getId();

    public void setId(long seqId);

    byte[] getName();

    byte[] getSequence() throws SequenceException;

    void setName(byte[] seqname);

    void setSequence(byte[] dnaseq) throws SequenceException;
}
