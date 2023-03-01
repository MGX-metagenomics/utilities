package de.cebitec.mgx.sequence;

import de.cebitec.mgx.seqcompression.SequenceException;
import java.io.IOException;

/**
 *
 * @author sjaenick
 */
public interface SeqWriterI<T extends DNASequenceI> extends AutoCloseable {
    
    public void addSequence(T seq) throws SequenceException;

    @Override
    public void close() throws SequenceException, IOException;

}
