package de.cebitec.mgx.seqstorage;

import de.cebitec.mgx.seqstorage.encoding.ByteUtils;
import de.cebitec.mgx.seqstorage.encoding.FileMagic;
import de.cebitec.mgx.sequence.SeqStoreException;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sjaenick
 */
public class NMSIndex {

    private final RandomAccessFile raf;
    private MappedByteBuffer mapped;
    private final byte[] buf = new byte[16];
    private final int numRecords;

    private final static int RECORD_SIZE = 16; // 2x long

    public NMSIndex(String filename) throws IOException, SeqStoreException {
        this(new File(filename));
    }

    public NMSIndex(File nmsFile) throws IOException, SeqStoreException {
        FileMagic.validateMagic(nmsFile.getAbsolutePath(), FileMagic.NMS_MAGIC);
        raf = new RandomAccessFile(nmsFile, "r");
        mapped = raf.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, nmsFile.length());
        numRecords = (int) ((nmsFile.length() - FileMagic.NMS_MAGIC.length) / RECORD_SIZE);
        
        // skip after file magic
        mapped.position(FileMagic.NMS_MAGIC.length);
    }

    public int read(byte[] buf) throws IOException {
        int numBytes = buf.length < mapped.remaining() ? buf.length : mapped.remaining();
        mapped.get(buf, 0, numBytes);
        return numBytes;
    }

    public synchronized long getOffset(final long id) throws SeqStoreException {
        int index = binarySearch(0, numRecords - 1, id);
        if (index == -1) {
            return -1;
        }

        if (index > numRecords) {
            throw new SeqStoreException("Index " + index + " bigger than number of sequences (" + numRecords + ")");
        }

        // read and convert record
        mapped.position(index * RECORD_SIZE + FileMagic.NMS_MAGIC.length);
        mapped.get(buf, 0, buf.length);
//        long readId = ByteUtils.bytesToLong(buf);
//        if (readId != id) {
//            throw new SeqStoreException("ERROR.");
//        }
        long offset = ByteUtils.bytesToLong(buf, 8);
        return offset;
    }

    // returns record index
    private int binarySearch(int l, int r, long x) {
        if (r >= l) {
            int midIdx = l + (r - l) / 2;

            int midOffset = FileMagic.NMS_MAGIC.length + (midIdx * RECORD_SIZE);
            mapped.position(midOffset);
            mapped.get(buf, 0, buf.length);
            long readId = ByteUtils.bytesToLong(buf);

            // If the element is present at the middle 
            // itself 
            if (readId == x) {
                return midIdx;
            }

            // If element is smaller than mid, then 
            // it can only be present in left subarray 
            if (readId > x) {
                return binarySearch(l, midIdx - 1, x);
            }

            // Else the element can only be present 
            // in right subarray 
            return binarySearch(midIdx + 1, r, x);
        }

        // We reach here when element is not 
        // present in array 
        return -1;
    }

    public int numRecords() {
        return numRecords;
    }

    public void close() {
        mapped = null;
        try {
            raf.close();
        } catch (IOException ex) {
            Logger.getLogger(NMSIndex.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
