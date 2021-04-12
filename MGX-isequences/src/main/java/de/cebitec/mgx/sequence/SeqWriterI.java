package de.cebitec.mgx.sequence;

import de.cebitec.mgx.seqcompression.SequenceException;

/**
 *
 * @author sjaenick
 */
public interface SeqWriterI<T extends DNASequenceI> extends AutoCloseable {
    
    public void addSequence(T seq) throws SequenceException;

}
