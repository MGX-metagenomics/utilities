package de.cebitec.mgx.seqstorage;

import de.cebitec.mgx.seqstorage.internal.FileUtil;
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

        // check for gzip compression
        boolean is_compressed;
        try {
            is_compressed = FileUtil.isGzip(file);
        } catch (IOException ex) {
            throw new SeqStoreException(ex.getMessage());
        }

        byte[] buf = new byte[4];
        if (!is_compressed) {
            try (InputStream in = new FileInputStream(uri)) {
                in.read(buf);
            } catch (FileNotFoundException ex) {
                throw new SeqStoreException("Sequence file " + uri + " missing");
            } catch (IOException ex) {
                throw new SeqStoreException("Could not read sequence file");
            }
        } else {
            try (GZIPInputStream gzis = new GZIPInputStream(new FileInputStream(file))) {
                gzis.read(buf);
            } catch (IOException ex) {
                throw new SeqStoreException("Could not read sequence file: " + ex.getMessage());
            }
        }

        switch (buf[0]) {
            case '>':
                return new FastaReader(uri, is_compressed);
            case '@':
                return new FASTQReader(uri, is_compressed);
            case 'N':
                if (new File(uri + ".csf").exists()) {
                    return new CSFReader(uri, is_compressed);
                } else if (new File(uri + ".csq").exists()) {
                    return new CSQFReader(uri, is_compressed);
                }
                break;
            case '.':
                if (buf[1] == 's' && buf[2] == 'f' && buf[3] == 'f') {
                    return new SFFReader(uri, is_compressed);
                }
        }
        
        throw new SeqStoreException("Unsupported file type (" + new String(buf) + ")");
    }
}
