package de.cebitec.mgx.sequence;

import java.util.Iterator;
import java.util.ServiceLoader;

/**
 *
 * @author sjaenick
 */
public class SeqReaderFactory {

    private static final ServiceLoader<FactoryI> loader = ServiceLoader.load(FactoryI.class);

    public static <T> SeqReaderI<T> getReader(String filename) throws SeqStoreException {
        FactoryI fac = get();
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

    private static <T> FactoryI<T> get() {
        Iterator<FactoryI> ps = loader.iterator();
        while (ps.hasNext()) {
            FactoryI<T> sr = ps.next();
            if (sr != null) {
                return sr;
            }
        }
        return null;
    }
}
