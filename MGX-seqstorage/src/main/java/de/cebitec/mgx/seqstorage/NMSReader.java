//package de.cebitec.mgx.seqstorage;
//
//import de.cebitec.mgx.seqstorage.encoding.ByteUtils;
//import de.cebitec.mgx.seqstorage.encoding.FileMagic;
//import de.cebitec.mgx.sequence.SeqStoreException;
//import gnu.trove.map.TLongLongMap;
//import gnu.trove.map.hash.TLongLongHashMap;
//import java.io.BufferedInputStream;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.IOException;
//import java.io.InputStream;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//
///**
// *
// * @author patrick
// */
//public class NMSReader {
//
//    private final TLongLongMap idx;
//    private final InputStream nmsStream;
//    private final byte[] buf = new byte[16];
//
//    public NMSReader(String filename) throws FileNotFoundException, IOException, SeqStoreException {
//        FileMagic.validateMagic(filename, FileMagic.NMS_MAGIC);
//        nmsStream = new BufferedInputStream(new FileInputStream(new File(filename)));
//        if (nmsStream.skip(FileMagic.NMS_MAGIC.length) < FileMagic.NMS_MAGIC.length) {
//            throw new SeqStoreException("Corrupted file");
//        }
//        this.idx = new TLongLongHashMap(10_000_000, 1.0F, -1, -1);
//    }
//    
//    public int read(byte[] buf) throws IOException {
//        return nmsStream.read(buf);
//    }
//
//    public int numRecords() {
//        return idx.size();
//    }
//
//    public long getOffset(long id) throws SeqStoreException {
//        if (!idx.containsKey(id)) {
//            try {
//                readRequired(id);
//            } catch (IOException ex) {
//                throw new SeqStoreException(ex.getMessage());
//            }
//        }
//        return idx.get(id);
//
//    }
//
//    private void readRequired(final long id) throws IOException, SeqStoreException {
//        while (16 == nmsStream.read(buf)) {
//            long curId = ByteUtils.bytesToLong(buf);
//            long offset = ByteUtils.bytesToLong(buf, 8);
//
//            if (curId < 0) {
//                throw new SeqStoreException("id less than 0: " + curId);
//            }
//
//            if (offset < 0) {
//                throw new SeqStoreException("offset less than 0");
//            }
//
//            idx.put(curId, offset);
//
//            if (curId == id) {
//                return;
//            }
//        }
//    }
//
//    public void close() {
//        try {
//            nmsStream.close();
//            idx.clear();
//        } catch (IOException ex) {
//            Logger.getLogger(CSFReader.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
//}
