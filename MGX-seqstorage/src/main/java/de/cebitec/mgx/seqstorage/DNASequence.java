package de.cebitec.mgx.seqstorage;

import de.cebitec.mgx.sequence.DNASequenceI;

/**
 *
 * @author sjaenick
 */
public class DNASequence implements DNASequenceI {

    private byte[] name = null;
    private byte[] sequence = null;
    private long id;

    public DNASequence() {
    }

    public DNASequence(long seqid) {
        id = seqid;
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public void setId(long id) {
        this.id = id;
    }

    @Override
    public byte[] getName() {
        return name;
    }

    @Override
    public void setName(byte[] seqname) {
        name = new byte[seqname.length];
        System.arraycopy(seqname, 0, name, 0, seqname.length);
    }

    @Override
    public byte[] getSequence() {
        return sequence;
    }

    @Override
    public void setSequence(byte[] dnaseq) {
        sequence = new byte[dnaseq.length];
        System.arraycopy(dnaseq, 0, sequence, 0, dnaseq.length);
    }
}
