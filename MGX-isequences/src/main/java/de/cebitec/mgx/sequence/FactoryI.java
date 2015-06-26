package de.cebitec.mgx.sequence;

/**
 *
 * @author sjaenick
 */
public interface FactoryI<T extends DNASequenceI> {

    public SeqReaderI<? extends T> getReader(String uri) throws SeqStoreException;
}
