package de.cebitec.mgx.seqstorage.encoding;

import de.cebitec.mgx.sequence.SeqStoreException;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;

/**
 *
 * @author Patrick Blumenkamp
 */
public class FileMagic {

    public static final byte[] CSF_MAGIC = {'C', 'S', 'F', '\n'};
    public static final byte[] NMS_MAGIC = {'N', 'M', 'S', '\n'};
    public static final byte[] CSQ_MAGIC = {'C', 'S', 'Q', '\n'};
    public final static byte[] lineSeparator = System.lineSeparator().getBytes();

    private FileMagic() {
    }

    public static void validateMagic(String filename, final byte[] magic) throws SeqStoreException {
        // validate magic
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(filename);
            byte[] tmp = new byte[magic.length];
            if (fis.read(tmp, 0, magic.length) < magic.length) {
                throw new SeqStoreException("Truncated file " + filename + "?");
            }
            if (!Arrays.equals(magic, tmp)) {
                throw new SeqStoreException(filename + ": Invalid magic: " + new String(tmp));
            }
        } catch (IOException e) {
            throw new SeqStoreException(filename + ": Invalid magic");
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException ex) {
                    throw new SeqStoreException(ex.getMessage());
                }
            }
        }
    }

}
