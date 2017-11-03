package de.cebitec.mgx.seqstorage;

import de.cebitec.mgx.braf.BufferedRandomAccessFile;
import de.cebitec.mgx.seqstorage.encoding.*;
import de.cebitec.mgx.sequence.*;
import java.io.*;
import java.util.*;
import java.util.zip.GZIPInputStream;

/**
 *
 * @author Patrick Blumenkamp
 */
public class CSQFReader implements SeqReaderI<DNAQualitySequenceI> {

    private InputStream seqin;
    private InputStream namein;
    private final String csqfile;
    private final String namefile;
    private DNAQualitySequenceI holder = null;
    private NMSReader idx = null;
    private BufferedRandomAccessFile raf = null;
    private byte[] record = new byte[16];

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
        try {
            FileMagic.validateMagic(namefile, FileMagic.NMS_MAGIC);
            FileMagic.validateMagic(csqfile, FileMagic.CSQ_MAGIC);
            seqin = new BufferedInputStream(new FileInputStream(csqfile));
            if (gzipCompressed) {
                InputStream gzstream = new GZIPInputStream(new FileInputStream(namefile));
                namein = new BufferedInputStream(gzstream);
            } else {
                namein = new BufferedInputStream(new FileInputStream(namefile));
            }
            if (namein.skip(FileMagic.CSQ_MAGIC.length) < FileMagic.CSF_MAGIC.length) {
                throw new SeqStoreException("Corrupted file " + csqfile);
            }
        } catch (SeqStoreException | IOException ex) {
            throw new SeqStoreException(ex.getMessage());
        }
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
            if (idx == null) {
                idx = new NMSReader(namein);
            }
            if (raf == null) {
                raf = new BufferedRandomAccessFile(csqfile, "r");
            }
        } catch (IOException ex) {
            throw new SeqStoreException("Could not parse index: " + ex);
        }

        try {
            for (long id : ids) {
                long offset = idx.getOffset(id);
                if (offset == -1) {
                    throw new SeqStoreException("Sequence ID " + id + " not present in index.");
                }
                result.add(getEntry(id, offset));
            }
        } catch (IOException ex) {
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

            /*
             * read new element
             */
            if (namein.read(record) != 16) {
                return false;
            }

            // extract sequence id and convert
            long sequence_id = ByteUtils.bytesToLong(record);
            holder = getEntry(sequence_id, ByteUtils.bytesToLong(record, 8));
            return true;
        } catch (IOException ex) {
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
            if (seqin != null) {
                seqin.close();
                seqin = null;
            }
            if (namein != null) {
                namein.close();
                namein = null;
            }
            if (idx != null) {
                idx.close();
                idx = null;
            }
            if (raf != null) {
                raf.close();
                raf = null;
            }
        } catch (IOException ex) {
        }
    }

    private synchronized DNAQualitySequenceI getEntry(long id, long offset) throws IOException, SeqStoreException {
        raf.seek(offset);
        byte[] buf = new byte[600];
        int bytesRead = raf.read(buf);
        int sepPos;
        // double buffer size until we 
        //   * encounter the record separator,
        //   * reach EOF,
        //   * 
        while ((sepPos = ByteUtils.indexOf(buf, FourBitEncoder.RECORD_SEPARATOR)) == -1 && bytesRead != -1 && buf.length < sepPos + 1) {
            byte[] newbuf = new byte[buf.length * 2];
            System.arraycopy(buf, 0, newbuf, 0, buf.length);
            bytesRead = raf.read(newbuf, buf.length, buf.length);
            buf = newbuf;
        }
        if (sepPos != 0) {
            byte[] encoded = ByteUtils.substring(buf, 0, sepPos - 1);
            byte[] decoded = FourBitEncoder.decode(encoded);
            int encodedQualLen = (int) Math.ceil(decoded.length * buf[sepPos + 1] / 8.0 + 2);
            if (sepPos + encodedQualLen + 1 > buf.length) {
                byte newbuf[] = new byte[sepPos + encodedQualLen + 1];
                System.arraycopy(buf, 0, newbuf, 0, buf.length);
                raf.read(newbuf, buf.length, sepPos + encodedQualLen - buf.length + 1);
                buf = newbuf;
            }
            byte[] quality = ByteUtils.substring(buf, sepPos + 1, sepPos + encodedQualLen);

            QualityDNASequence seq = new QualityDNASequence(id);
            seq.setSequence(decoded);
            seq.setQuality(QualityEncoder.decode(quality, decoded.length));
            return seq;
        } else {
            byte[] sequence = new byte[0];
            byte[] quality = new byte[0];
            QualityDNASequence seq = new QualityDNASequence(id);
            seq.setSequence(sequence);
            seq.setQuality(quality);
            return seq;
        }
    }
}
