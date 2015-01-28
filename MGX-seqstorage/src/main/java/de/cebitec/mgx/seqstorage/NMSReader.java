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

        final TLongLongMap idx;
        final InputStream nmsStream;

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
            byte[] buf = new byte[16];
            while (16 == nmsStream.read(buf)) {
                long curId = ByteUtils.bytesToLong(ByteUtils.substring(buf, 0, 7));
                long offset = ByteUtils.bytesToLong(ByteUtils.substring(buf, 8, 15));
                idx.put(curId, offset);

                if (curId == id) {
                    return;
                }
            }
        }

        public void close() {
            try {
                if (nmsStream != null) {
                    nmsStream.close();
                }
                if (idx != null) {
                    idx.clear();
                }
            } catch (IOException ex) {
                Logger.getLogger(CSFReader.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

