package de.cebitec.mgx.sequence;

import java.util.Set;

/**
 *
 * @author sjaenick
 */
public interface SeqReaderI<T> extends AutoCloseable {

    public void delete();

    public Set<T> fetch(long[] ids) throws SeqStoreException;
    
    public boolean hasQuality();
    
    T nextElement();
    
    public boolean hasMoreElements() throws SeqStoreException;
}
