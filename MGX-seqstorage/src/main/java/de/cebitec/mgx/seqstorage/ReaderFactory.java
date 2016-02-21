package de.cebitec.mgx.seqstorage;

import de.cebitec.mgx.sequence.DNASequenceI;
import de.cebitec.mgx.sequence.FactoryI;
import de.cebitec.mgx.sequence.SeqReaderI;
import de.cebitec.mgx.sequence.SeqStoreException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

/**
 *
 * @author sjaenick
 */
public class ReaderFactory implements FactoryI<DNASequenceI> {

    private final static byte[] GZIP_MAGIC = new byte[]{31, -117}; // 0x8b 0x1f

    public ReaderFactory() {
    }

    @Override
    public SeqReaderI<? extends DNASequenceI> getReader(String uri) throws SeqStoreException {
        /*
         * try to open the file and read the first byte to determine
         * the file type and create the correct reader object
         */

        File file = new File(uri);
        if (!file.exists()) {
            throw new SeqStoreException("No such file: " + uri);
        }
        if (file.length() == 0) {
            throw new SeqStoreException(uri + " is empty.");
        }

        byte[] buf = new byte[4];
        try (InputStream in = new FileInputStream(uri)) {
            in.read(buf);
        } catch (FileNotFoundException ex) {
            throw new SeqStoreException("Sequence file " + uri + " missing");
        } catch (IOException ex) {
            throw new SeqStoreException("Could not read sequence file");
        }

        boolean is_compressed = false;

        // check for gzip magic
        if ((buf[0] == GZIP_MAGIC[0]) && (buf[1] == GZIP_MAGIC[1])) {
            try (GZIPInputStream gzis = new GZIPInputStream(new FileInputStream(file))) {
                gzis.read(buf);
            } catch (IOException ex) {
                throw new SeqStoreException("Could not read sequence file: " + ex.getMessage());
            }
            is_compressed = true;
        }

        SeqReaderI<? extends DNASequenceI> ret = null;
        switch (buf[0]) {
            case '>':
                ret = new FastaReader(uri, is_compressed);
                break;
            case '@':
                ret = new FASTQReader(uri, is_compressed);
                break;
            case 'N':
                if (new File(uri + ".csf").exists()) {
                    ret = new CSFReader(uri, is_compressed);
                } else if (new File(uri + ".csq").exists()) {
                    ret = new CSQFReader(uri, is_compressed);
                }
                break;
            case '.':
                if (buf[1] == 's' && buf[2] == 'f' && buf[3] == 'f') {
                    ret = new SFFReader(uri, is_compressed);
                }
                break;
            default:
                throw new SeqStoreException("Unsupported file type (" + new String(buf) + ")");
        }

        return ret;
    }
}
