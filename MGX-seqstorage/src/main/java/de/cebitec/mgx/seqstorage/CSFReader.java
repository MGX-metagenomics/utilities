package de.cebitec.mgx.seqstorage;

import de.cebitec.mgx.braf.BufferedRandomAccessFile;
import de.cebitec.mgx.seqstorage.encoding.*;
import de.cebitec.mgx.sequence.DNASequenceI;
import de.cebitec.mgx.sequence.SeqReaderI;
import de.cebitec.mgx.sequence.SeqStoreException;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.GZIPInputStream;

/**
 *
 * @author sjaenick
 */
public class CSFReader implements SeqReaderI<DNASequenceI> {

    private final ByteStreamTokenizer seqin;
    private final InputStream namein;
    private final String csffile;
    private final String namefile;
    private DNASequenceI holder = null;
    private NMSReader idx = null;
    private BufferedRandomAccessFile raf = null;
    private final byte[] record = new byte[16];

    public CSFReader(String filename, boolean gzipCompressed) throws SeqStoreException {
        if (filename == null) {
            throw new SeqStoreException("No filename.");
        }

        if (gzipCompressed) {
            throw new SeqStoreException("Compressed CSF format unsupported.");
        }
        csffile = filename + ".csf";
        namefile = filename;
        try {
            FileMagic.validateMagic(namefile, FileMagic.NMS_MAGIC);
            FileMagic.validateMagic(csffile, FileMagic.CSF_MAGIC);
            seqin = new ByteStreamTokenizer(csffile, gzipCompressed, FourBitEncoder.RECORD_SEPARATOR, FileMagic.CSF_MAGIC.length);
            if (gzipCompressed) {
                InputStream gzstream = new GZIPInputStream(new FileInputStream(namefile));
                namein = new BufferedInputStream(gzstream);
            } else {
                namein = new BufferedInputStream(new FileInputStream(namefile));
            }
            if (namein.skip(FileMagic.CSF_MAGIC.length) < FileMagic.CSF_MAGIC.length) {
                throw new SeqStoreException("Corrupted file " + csffile);
            }
        } catch (IOException ex) {
            throw new SeqStoreException(ex.getMessage());
        }
    }

    @Override
    public synchronized boolean hasMoreElements() throws SeqStoreException {

        if (holder != null) {
            // element in holder not yet retrieved
            return true;
        }

        /*
         * read new element
         */
        try {
            if (namein.read(record) != 16) {
                return false;
            }
        } catch (IOException ex) {
            throw new SeqStoreException(ex.getMessage());
        }

        // extract sequence id and convert
        long sequence_id = ByteUtils.bytesToLong(record, 0);

        if (!seqin.hasNext()) {
            return false;
        }

        byte[] dnasequence = seqin.next();

        if (dnasequence != null) {
            DNASequenceI seq = new DNASequence(sequence_id);
            seq.setSequence(FourBitEncoder.decode(dnasequence));
            holder = seq;
            return true;
        }
        return false;
    }

    @Override
    public synchronized DNASequenceI nextElement() {
        DNASequenceI ret = holder;
        holder = null;
        return ret;
    }

    @Override
    public void close() {
        try {
            if (seqin != null) {
                seqin.close();
            }
            if (namein != null) {
                namein.close();
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

    @Override
    public void delete() {
        close();
        File f = new File(csffile);
        File g = new File(namefile);
        if (f.exists() && f.isFile()) {
            f.delete();
        }
        if (g.exists() && g.isFile()) {
            g.delete();
        }
    }

    @Override
    public Set<DNASequenceI> fetch(long[] ids) throws SeqStoreException {
        Set<DNASequenceI> result = new HashSet<>(ids.length);
        if (ids.length == 0) {
            return result;
        }
        Arrays.sort(ids);

        try {
            if (idx == null) {
                idx = new NMSReader(namein);
            }
            if (raf == null) {
                raf = new BufferedRandomAccessFile(csffile, "r");
            }
        } catch (IOException ex) {
            throw new SeqStoreException("Could not parse index: " + ex);
        }

        try {
            byte[] buf = new byte[200];
            int bytesRead;
            for (long id : ids) {
                long offset = idx.getOffset(id);
                if (offset == -1) {
                    throw new SeqStoreException("Sequence ID " + id + " not present in index.");
                }
                raf.seek(offset);
                bytesRead = raf.read(buf);
                while (-1 == ByteUtils.indexOf(buf, FourBitEncoder.RECORD_SEPARATOR) && bytesRead != -1) {
                    byte newbuf[] = new byte[buf.length * 2];
                    System.arraycopy(buf, 0, newbuf, 0, buf.length);
                    bytesRead = raf.read(newbuf, buf.length, buf.length);
                    buf = newbuf;
                }
                int sepPos = ByteUtils.indexOf(buf, FourBitEncoder.RECORD_SEPARATOR);
                byte[] encoded = ByteUtils.substring(buf, 0, sepPos - 1);

                DNASequenceI seq = new DNASequence(id);
                seq.setSequence(FourBitEncoder.decode(encoded));
                result.add(seq);
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
        return false;
    }

}
