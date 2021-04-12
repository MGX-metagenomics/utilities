package de.cebitec.mgx.sequence;

import de.cebitec.mgx.seqcompression.SequenceException;
import java.util.Set;

/**
 *
 * @author sjaenick
 */
public interface SeqReaderI<T extends DNASequenceI> extends AutoCloseable {

    public void delete();

    public Set<T> fetch(long[] ids) throws SequenceException;
    
    public boolean hasQuality();
    
    T nextElement();
    
    public boolean hasMoreElements() throws SequenceException;

    @Override
    public void close() throws SeqStoreException;
    
}
