package de.cebitec.mgx.seqstorage;

import de.cebitec.mgx.braf.BufferedRandomAccessFile;
import de.cebitec.mgx.seqholder.DNASequenceHolder;
import de.cebitec.mgx.seqstorage.encoding.ByteUtils;
import de.cebitec.mgx.seqstorage.encoding.FourBitEncoder;
import de.cebitec.mgx.sequence.DNASequenceI;
import de.cebitec.mgx.sequence.SeqReaderI;
import de.cebitec.mgx.sequence.SeqStoreException;
import gnu.trove.map.TLongLongMap;
import gnu.trove.map.hash.TLongLongHashMap;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;

/**
 *
 * @author sjaenick
 */
public class CSFReader implements SeqReaderI<DNASequenceHolder> {

    private ByteStreamTokenizer seqin;
    private InputStream namein;
    private final String csffile;
    private final String namefile;
    private DNASequenceHolder holder = null;

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
            validateMagic(namefile, FourBitEncoder.NMS_MAGIC);
            validateMagic(csffile, FourBitEncoder.CSF_MAGIC);
            seqin = new ByteStreamTokenizer(csffile, gzipCompressed, FourBitEncoder.RECORD_SEPARATOR, FourBitEncoder.CSF_MAGIC.length);
            if (gzipCompressed) {
                InputStream gzstream = new GZIPInputStream(new FileInputStream(namefile));
                namein = new BufferedInputStream(gzstream);
            } else {
                namein = new BufferedInputStream(new FileInputStream(namefile));
            }
            if (namein.skip(FourBitEncoder.CSF_MAGIC.length) < FourBitEncoder.CSF_MAGIC.length) {
                throw new SeqStoreException("Corrupted file " + csffile);
            }
        } catch (SeqStoreException | IOException ex) {
            throw new SeqStoreException(ex.getMessage());
        }
    }

    @Override
    public boolean hasMoreElements() {

        if (holder != null) {
            // element in holder not yet retrieved
            return true;
        }

        /*
         * read new element
         */

        // extract substring of element, removing last 8bytes (offset)
        byte[] record = new byte[16];
        byte[] seqId = new byte[8];

        try {
            if (namein.read(record) == -1) {
                return false;
            }
        } catch (IOException ex) {
            seqId = null;
        }

        // extract sequence id and convert
        System.arraycopy(record, 0, seqId, 0, 8);
        long sequence_id = ByteUtils.bytesToLong(seqId);

        if (!seqin.hasMoreElements()) {
            return false;
        }

        byte[] dnasequence = seqin.nextElement();

        if ((seqId != null) && (dnasequence != null)) {
            DNASequenceI seq = new DNASequence(sequence_id);
            seq.setSequence(FourBitEncoder.decode(dnasequence));

            holder = new DNASequenceHolder(seq);
            return true;
        }
        return false;
    }

    @Override
    public DNASequenceHolder nextElement() {
        assert holder != null;
        DNASequenceHolder ret = holder;
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

    private void validateMagic(String filename, final byte[] magic) throws SeqStoreException {
        // validate magic
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(filename);
            byte[] tmp = new byte[magic.length];
            if (fis.read(tmp, 0, magic.length) < magic.length) {
                throw new SeqStoreException("Truncated file " + filename + "?");
            };
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
    private NMSReader idx = null;
    private BufferedRandomAccessFile raf = null;

    @Override
    public Set<DNASequenceHolder> fetch(long[] ids) throws SeqStoreException {
        Set<DNASequenceHolder> result = new HashSet<>(ids.length);
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
            int bytesRead = 0;
            for (long id : ids) {
                long offset = idx.getOffset(id);
                if (offset == -1) {
                    throw new SeqStoreException("Sequence ID " + id + " not present in index.");
                }
                raf.seek(offset);
                bytesRead = raf.read(buf);
                while (-1 == getSeparatorPos(buf, FourBitEncoder.RECORD_SEPARATOR) && bytesRead != -1) {
                    byte newbuf[] = new byte[buf.length * 2];
                    System.arraycopy(buf, 0, newbuf, 0, buf.length);
                    bytesRead = raf.read(newbuf, buf.length, buf.length);
                    buf = newbuf;
                }
                int sepPos = getSeparatorPos(buf, FourBitEncoder.RECORD_SEPARATOR);
                byte[] encoded = ByteUtils.substring(buf, 0, sepPos - 1);

                DNASequenceI seq = new DNASequence(id);
                seq.setSequence(FourBitEncoder.decode(encoded));
                result.add(new DNASequenceHolder(seq));
            }
        } catch (IOException ex) {
            throw new SeqStoreException("Internal error: " + ex.getMessage());
        }

        if (result.size() != ids.length) {
            throw new SeqStoreException("Could not retrieve all sequences.");
        }
        return result;
    }

    private int getSeparatorPos(byte[] in, byte separator) {
        for (int i = 0; i <= in.length - 1; i++) {
            if (in[i] == separator) {
                return i;
            }
        }
        return -1;


    }

    private class NMSReader {

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
}
