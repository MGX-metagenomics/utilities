
package de.cebitec.mgx.sequence;

/**
 *
 * @author sjaenick
 */
public interface DNASequenceI {

    public long getId();

    public void setId(long seqId);

    byte[] getName();

    byte[] getSequence();

    void setName(byte[] seqname);

    void setSequence(byte[] dnaseq);
}
