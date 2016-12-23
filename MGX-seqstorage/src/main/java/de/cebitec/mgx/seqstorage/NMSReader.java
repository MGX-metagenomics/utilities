package de.cebitec.mgx.seqstorage;

import de.cebitec.mgx.seqstorage.encoding.ByteUtils;
import de.cebitec.mgx.sequence.SeqStoreException;
import gnu.trove.map.TLongLongMap;
import gnu.trove.map.hash.TLongLongHashMap;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author patrick
 */
public class NMSReader {

    private final TLongLongMap idx;
    private final InputStream nmsStream;
    private final byte[] buf = new byte[16];

    public NMSReader(InputStream nmsStream) throws IOException {
        this.idx = new TLongLongHashMap(100, 1.0F, -1, -1);
        this.nmsStream = nmsStream;
    }

    public long getOffset(long id) throws SeqStoreException {
        if (!idx.containsKey(id)) {
            try {
                readRequired(id);
            } catch (IOException ex) {
                throw new SeqStoreException(ex.getMessage());
            }
        }
        return idx.get(id);
    }

    private void readRequired(long id) throws IOException {
        while (16 == nmsStream.read(buf)) {
            long curId = ByteUtils.bytesToLong(buf);
            long offset = ByteUtils.bytesToLong(buf, 8);
            idx.put(curId, offset);

            if (curId == id) {
                return;
            }
        }
    }

    public void close() {
        try {
            nmsStream.close();
            idx.clear();
        } catch (IOException ex) {
            Logger.getLogger(CSFReader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
