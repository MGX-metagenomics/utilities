package de.cebitec.mgx.sequence;

/**
 *
 * @author sjaenick
 */
public interface SeqWriterI<T extends DNASequenceI> extends AutoCloseable {
    
    public void addSequence(T seq) throws SeqStoreException;

}
