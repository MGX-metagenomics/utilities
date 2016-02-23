package de.cebitec.mgx.sequence;

import java.util.Iterator;
import java.util.ServiceLoader;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

/**
 *
 * @author sjaenick
 */
public class SeqReaderFactory<T extends DNASequenceI> {

    private static final ServiceLoader<FactoryI> loader = ServiceLoader.<FactoryI>load(FactoryI.class);

    @SuppressWarnings("unchecked")
    public static <T extends DNASequenceI> SeqReaderI<? extends DNASequenceI> getReader(String filename) throws SeqStoreException {
        if (!OSGiContext.isOSGi()) {
            // fallback to serviceloader
            if (filename == null || filename.isEmpty()) {
                throw new SeqStoreException("Unable to determine reader for empty/null filename");
            }

            FactoryI<T> fac = SeqReaderFactory.<T>get();
            if (fac == null) {
                throw new SeqStoreException("No SeqReaderFactory found.");
            }
            SeqReaderI<? extends DNASequenceI> reader = fac.<T>getReader(filename);
            return reader;
        } else {
            BundleContext context = FrameworkUtil.getBundle(SeqReaderFactory.class).getBundleContext();
            ServiceReference<FactoryI<T>> serviceReference = (ServiceReference<FactoryI<T>>) context.getServiceReference(FactoryI.class.getName());
            FactoryI<T> service = context.<FactoryI<T>>getService(serviceReference);
            SeqReaderI<? extends DNASequenceI> ret = service.<T>getReader(filename);
            return ret;
        }
    }

    public static void delete(String dBFile) {
        try {
            getReader(dBFile).delete();
        } catch (SeqStoreException ex) {
        }
    }

    @SuppressWarnings("unchecked")
    private static <T extends DNASequenceI> FactoryI<T> get() {
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
