package de.cebitec.mgx.sequence;

import java.io.IOException;

/**
 *
 * @author sjaenick
 */
public interface SeqWriterI<T extends DNASequenceI> extends AutoCloseable {
    
    public void addSequence(T seq) throws IOException;

}
