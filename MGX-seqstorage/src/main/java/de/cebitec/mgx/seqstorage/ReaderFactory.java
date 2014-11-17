package de.cebitec.mgx.seqstorage;

import de.cebitec.mgx.seqholder.ReadSequenceI;
import de.cebitec.mgx.sequence.FactoryI;
import de.cebitec.mgx.sequence.SeqReaderI;
import de.cebitec.mgx.sequence.SeqStoreException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;

/**
 *
 * @author sjaenick
 */
public class ReaderFactory implements FactoryI {

    public ReaderFactory() {
    }

    @Override
    public SeqReaderI<? extends ReadSequenceI> getReader(String uri) throws SeqStoreException {
        /*
         * try to open the file and read the first byte to determine
         * the file type and create the correct reader object
         */

        File file = new File(uri);
        if (!file.exists()) {
            throw new SeqStoreException("No such file: " + uri);
        }

        char[] cbuf = new char[4];
        try (FileReader fr = new FileReader(uri)) {
            fr.read(cbuf, 0, 4);
        } catch (FileNotFoundException ex) {
            throw new SeqStoreException("Sequence file " + uri + " missing");
        } catch (IOException ex) {
            throw new SeqStoreException("Could not read sequence file");
        }

        boolean is_compressed = false;

        // check for gzip magic
        if ((cbuf[0] == 0x1f) && (cbuf[1] == 0x8b)) {
            try {
                GZIPInputStream gzis = new GZIPInputStream(new FileInputStream(file));
                InputStreamReader isr = new InputStreamReader(gzis);
                isr.read(cbuf, 0, 4);
                isr.close();
                gzis.close();
                is_compressed = true;
            } catch (IOException ex) {
                throw new SeqStoreException("Could not read sequence file: " + ex.getMessage());
            }
        }

        SeqReaderI<? extends ReadSequenceI> ret = null;
        switch (cbuf[0]) {
            case '>':
                ret = new FastaReader(uri, is_compressed);
                break;
            case '@':
                ret = new FASTQReader(uri, is_compressed);
                break;
            case 'N':
                ret = new CSFReader(uri, is_compressed);
                break;
            case '.':
                if (cbuf[1] == 's' && cbuf[2] == 'f' && cbuf[3] == 'f') {
                    try {
                        ret = new SFFReader(uri);
                    } catch (IOException ex) {
                        Logger.getLogger(ReaderFactory.class.getName()).log(Level.SEVERE, null, ex);
                        throw new SeqStoreException(ex.getMessage());
                    }
                }
                break;
            default:
                throw new SeqStoreException("Unsupported file type (" + new String(cbuf) + ")");
        }

        return ret;
    }
}
