package de.cebitec.mgx.seqstorage;

import de.cebitec.mgx.seqcompression.ByteUtils;
import de.cebitec.mgx.braf.BufferedRandomAccessFile;
import de.cebitec.mgx.seqcompression.FourBitEncoder;
import de.cebitec.mgx.seqcompression.QualityEncoder;
import de.cebitec.mgx.seqcompression.SequenceException;
import de.cebitec.mgx.seqstorage.encoding.*;
import de.cebitec.mgx.sequence.*;
import java.io.*;
import java.util.*;

/**
 *
 * @author Patrick Blumenkamp
 */
public class CSQFReader implements SeqReaderI<DNAQualitySequenceI> {

    //private InputStream seqin;
    private final String csqfile;
    private final String namefile;
    private DNAQualitySequenceI holder = null;
    private NMSIndex idx = null;
    private BufferedRandomAccessFile raf = null;
    private final byte[] record = new byte[16];

    public CSQFReader(String filename) throws SeqStoreException {
        this(filename, false);
    }

    public CSQFReader(String filename, boolean gzipCompressed) throws SeqStoreException {
        if (filename == null) {
            throw new SeqStoreException("No filename.");
        }

        if (gzipCompressed) {
            throw new SeqStoreException("Compressed CSQ format unsupported.");
        }

        csqfile = filename + ".csq";
        namefile = filename;
        FileMagic.validateMagic(csqfile, FileMagic.CSQ_MAGIC);
    }

    @Override
    public void delete() {
        close();
        File f = new File(csqfile);
        File g = new File(namefile);
        if (f.exists() && f.isFile()) {
            f.delete();
        }
        if (g.exists() && g.isFile()) {
            g.delete();
        }
    }

    @Override
    public Set<DNAQualitySequenceI> fetch(long[] ids) throws SeqStoreException {
        Set<DNAQualitySequenceI> result = new HashSet<>(ids.length);
        if (ids.length == 0) {
            return result;
        }
        Arrays.sort(ids);

        try {
            if (raf == null) {
                raf = new BufferedRandomAccessFile(csqfile, "r");
            }
        } catch (IOException ex) {
            throw new SeqStoreException("Could not parse index: " + ex);
        }

        try {
            for (long id : ids) {
                if (idx == null) {
                    idx = new NMSIndex(namefile);
                }

                long offset = idx.getOffset(id);
                if (offset == -1) {
                    throw new SeqStoreException("Sequence ID " + id + " not present in index.");
                }
                result.add(getEntry(id, offset));
            }
        } catch (SequenceException | IOException ex) {
            throw new SeqStoreException("Internal error: " + ex.getMessage());
        }

        if (result.size() != ids.length) {
            throw new SeqStoreException("Could not retrieve all sequences.");
        }
        return result;
    }

    @Override
    public final boolean hasQuality() {
        return true;
    }

    @Override
    public boolean hasMoreElements() throws SeqStoreException {
        try {
            if (holder != null) {
                // element in holder not yet retrieved
                return true;
            }

            if (raf == null) {
                raf = new BufferedRandomAccessFile(csqfile, "r");
            }

            if (idx == null) {
                idx = new NMSIndex(namefile);
            }

            /*
             * read new element
             */
            if (idx.read(record) != 16) {
                return false;
            }

            // extract sequence id and convert
            long sequence_id = ByteUtils.bytesToLong(record);
            holder = getEntry(sequence_id, ByteUtils.bytesToLong(record, 8));
            return true;
        } catch (SequenceException | IOException ex) {
            return false;
        }
    }

    @Override
    public DNAQualitySequenceI nextElement() {
        DNAQualitySequenceI ret = holder;
        holder = null;
        return ret;
    }

    @Override
    public void close() {
        try {
            if (idx != null) {
                idx.close();
            }
            if (raf != null) {
                raf.close();
                raf = null;
            }
        } catch (IOException ex) {
        }
    }

    private synchronized DNAQualitySequenceI getEntry(long id, long offset) throws IOException, SequenceException, SeqStoreException {
        raf.seek(offset);
        byte[] buf = new byte[600];
        int bytesRead = raf.read(buf);
        int sepPos;

        // double buffer size until we 
        //   * encounter the record separator,
        //   * reach EOF,
        //   * separator is not in the lastmost position (access to buf[sepPos+1] is needed)
        //
        while (((sepPos = ByteUtils.indexOf(buf, FourBitEncoder.RECORD_SEPARATOR)) == -1 && bytesRead != -1) || (sepPos == buf.length - 1)) {
            byte[] newbuf = new byte[buf.length * 2];
            System.arraycopy(buf, 0, newbuf, 0, buf.length);
            bytesRead = raf.read(newbuf, buf.length, buf.length);
            buf = newbuf;
        }

        if (sepPos > 0) {
            byte[] encodedSeq = ByteUtils.substring(buf, 0, sepPos - 1);
            byte[] decodedSeq = FourBitEncoder.decode(encodedSeq);

            // bytes needed to store quality; two initial bytes are used to store 
            // bits per value and an offset to be added to all encoded values
            int encodedQualLen = (int) Math.ceil(decodedSeq.length * buf[sepPos + 1] / 8.0 + 2);

            // quality exceeds beyond buffer end; enlarge buffer and read 
            // missing bytes from file
            if (sepPos + encodedQualLen + 1 > buf.length) {
                int partialQlen = buf.length - sepPos - 1; // excluding separator
                int bytesMissing = encodedQualLen - partialQlen;

                byte[] newbuf = Arrays.copyOf(buf, buf.length + bytesMissing);
                //assert newbuf[sepPos] == FourBitEncoder.RECORD_SEPARATOR;

                raf.read(newbuf, sepPos + partialQlen + 1, bytesMissing);
                buf = newbuf;
            }

            byte[] encodedQual = ByteUtils.substring(buf, sepPos + 1, sepPos + encodedQualLen);
            byte[] decodedQual = QualityEncoder.decode(encodedQual, decodedSeq.length);

            QualityDNASequence seq = new QualityDNASequence(id);
            seq.setSequence(decodedSeq);
            seq.setQuality(decodedQual);
            return seq;
        } else if (sepPos == 0) {
            // empty sequence
            QualityDNASequence seq = new QualityDNASequence(id);
            seq.setSequence(new byte[0]);
            seq.setQuality(new byte[0]);
            return seq;
        } else { // sepPos == -1
            assert false;
            return null;
        }
    }
}
