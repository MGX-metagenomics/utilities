package de.cebitec.mgx.seqstorage;

import de.cebitec.mgx.seqcompression.SequenceException;
import de.cebitec.mgx.seqstorage.internal.DNASequenceValidator;
import de.cebitec.mgx.sequence.DNASequenceI;
import java.util.Arrays;

/**
 *
 * @author sjaenick
 */
public class DNASequence implements DNASequenceI {

    private long id;
    private byte[] name = null;
    private final byte[] dnasequence;
    
    public DNASequence(byte[] sequence) throws SequenceException {
        this(sequence, true);
    }

    public DNASequence(byte[] sequence, boolean enableValidation) throws SequenceException {
        dnasequence = Arrays.copyOf(sequence, sequence.length);

        if (enableValidation) {
            DNASequenceValidator.validateSequence(dnasequence);
        }
    }

    public DNASequence(long seqid, byte[] sequence, boolean enableValidation) throws SequenceException {
        id = seqid;
        dnasequence = Arrays.copyOf(sequence, sequence.length);

        if (enableValidation) {
            DNASequenceValidator.validateSequence(dnasequence);
        }
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
        name = Arrays.copyOf(seqname, seqname.length);
    }

    @Override
    public byte[] getSequence() throws SequenceException {
        return Arrays.copyOf(dnasequence, dnasequence.length);
    }
}
