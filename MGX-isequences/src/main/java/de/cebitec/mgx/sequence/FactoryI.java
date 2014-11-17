package de.cebitec.mgx.sequence;

/**
 *
 * @author sjaenick
 */
public interface FactoryI<T> {

    public SeqReaderI<T> getReader(String uri) throws SeqStoreException;
}
