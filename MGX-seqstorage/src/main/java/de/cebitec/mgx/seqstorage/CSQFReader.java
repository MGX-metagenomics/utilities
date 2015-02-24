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
public class CSQFReader implements SeqReaderI<DNAQualitySequenceI>{

    private InputStream seqin;
    private InputStream namein;
    private final String csqfile;
    private final String namefile;
    private DNAQualitySequenceI holder = null;
    private NMSReader idx = null;
    private BufferedRandomAccessFile raf = null;
    
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
            byte[] buf = new byte[600];
            int bytesRead = 0;
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
    public boolean hasQuality() {
        return true;
    }

    @Override
    public boolean hasMoreElements() {
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
            // extract substring of element, removing last 8bytes (offset)
            byte[] record = new byte[16];
            byte[] seqId = new byte[8];
            byte[] offset = new byte[8];
            
            if (namein.read(record) == -1) {
                return false;
            }
            
            // extract sequence id and convert
            System.arraycopy(record, 0, seqId, 0, 8);
            System.arraycopy(record, 8, offset, 0, 8);
            long sequence_id = ByteUtils.bytesToLong(seqId);
            
            holder = getEntry(sequence_id, ByteUtils.bytesToLong(offset));
            return true;
        } catch (IOException ex) {
            return false;
        }
    }

    @Override
    public DNAQualitySequenceI nextElement() {
        assert holder != null;
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

    private int getSeparatorPos(byte[] in, byte separator) {
        for (int i = 0; i <= in.length - 1; i++) {
            if (in[i] == separator) {
                return i;
            }
        }
        return -1;
    }
    
    private DNAQualitySequenceI getEntry(long id, long offset) throws IOException{
        raf.seek(offset);
        byte[] buf = new byte[600];
        int bytesRead = raf.read(buf);
        int sepPos;
        while ((sepPos = getSeparatorPos(buf, FourBitEncoder.RECORD_SEPARATOR)) == -1 && bytesRead != -1 && buf.length < sepPos*3+1) {
            byte newbuf[] = new byte[buf.length * 2];
            System.arraycopy(buf, 0, newbuf, 0, buf.length);
            bytesRead = raf.read(newbuf, buf.length, buf.length);
            buf = newbuf;
        }
        byte[] encoded = ByteUtils.substring(buf, 0, sepPos - 1);
        byte[] decoded = FourBitEncoder.decode(encoded);
        byte[] quality = ByteUtils.substring(buf, sepPos+1, (int)(decoded.length*buf[sepPos+1]/8.0+2.9)+sepPos);

        QualityDNASequence seq = new QualityDNASequence(id);
        seq.setSequence(decoded);
        seq.setQuality(QualityEncoder.decode(quality));
        return seq;
    }
}
