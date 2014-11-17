package de.cebitec.mgx.sequence;

import java.util.Enumeration;
import java.util.Set;

/**
 *
 * @author sjaenick
 */
public interface SeqReaderI<T> extends Enumeration<T>, AutoCloseable {

    public void delete();

    public Set<T> fetch(long[] ids) throws SeqStoreException;
}
