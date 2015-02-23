package de.cebitec.mgx.sequence;

import java.util.Iterator;
import java.util.ServiceLoader;

/**
 *
 * @author sjaenick
 */
public class SeqReaderFactory {

    private static final ServiceLoader<FactoryI> loader = ServiceLoader.<FactoryI>load(FactoryI.class);

    public static <T> SeqReaderI<T> getReader(String filename) throws SeqStoreException {
        FactoryI<T> fac = SeqReaderFactory.<T>get();
        if (fac == null) {
            throw new SeqStoreException("No SeqReaderFactory found.");
        }
        return fac.getReader(filename);
    }

    public static void delete(String dBFile) {
        try {
            getReader(dBFile).delete();
        } catch (SeqStoreException ex) {
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> FactoryI<T> get() {
        Iterator<FactoryI> ps = loader.iterator();
        while (ps != null && ps.hasNext()) {
            FactoryI<T> sr = ps.next();
            if (sr != null) {
                return sr;
            }
        }
        return null;
    }
}
