package de.cebitec.mgx.seqstorage;

import de.cebitec.mgx.seqholder.DNAQualitySequenceHolder;
import de.cebitec.mgx.sequence.SeqReaderI;
import de.cebitec.mgx.sequence.SeqStoreException;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author sjaenick
 */
public class FASTQReader implements SeqReaderI<DNAQualitySequenceHolder> {

    private QualityDNASequence seq = null;
    private ByteStreamTokenizer stream = null;
    private String fastqfile = null;
    public static final byte LINEBREAK = '\n';

    public FASTQReader(String filename, boolean gzipCompressed) throws SeqStoreException {
        fastqfile = filename;
        try {
            stream = new ByteStreamTokenizer(fastqfile, gzipCompressed, LINEBREAK, 0);
        } catch (IOException ex) {
            throw new SeqStoreException("File not found or unreadable: " + fastqfile + "\n" + ex.getMessage());
        }
    }

    @Override
    public boolean hasMoreElements() {

        byte[] l1, l2, l3, l4;

        try {
            l1 = stream.hasMoreElements() ? stream.nextElement() : null; // header
            l2 = stream.hasMoreElements() ? stream.nextElement() : null; // dna sequence
            l3 = stream.hasMoreElements() ? stream.nextElement() : null; // quality header
            l4 = stream.hasMoreElements() ? stream.nextElement() : null; // quality string
        } catch (Exception e) {
            return false;
        }

        if ((l1 == null) || (l2 == null)) {
            return false;
        }

        // remove leading '@' from sequence name
        byte[] seqname = new byte[l1.length - 1];
        System.arraycopy(l1, 1, seqname, 0, l1.length - 1);

        seq = new QualityDNASequence();
        seq.setName(seqname);
        seq.setSequence(l2);
        seq.setQuality(convertQuality(l4));

        return true;
    }

    @Override
    public DNAQualitySequenceHolder nextElement() {
        return new DNAQualitySequenceHolder(seq);
    }

    @Override
    public void close() {
        if (stream != null) {
            stream.close();
            stream = null;
        }
    }

    @Override
    public void delete() {
        close();
        File f = new File(fastqfile);
        if (f.exists() && f.isFile()) {
            f.delete();
        }
    }

    @Override
    public Set<DNAQualitySequenceHolder> fetch(long[] ids) throws SeqStoreException {
        Set<DNAQualitySequenceHolder> res = new HashSet<>(ids.length);
        Set<Long> idList = new HashSet<>(ids.length);
        for (int i =0; i< ids.length; i++) {
            idList.add(ids[i]);
        }
        while (hasMoreElements() && !idList.isEmpty()) {
            DNAQualitySequenceHolder elem = nextElement();
            if (idList.contains(elem.getSequence().getId())) {
                res.add(elem);
                idList.remove(elem.getSequence().getId());
            }
        }

        if (!idList.isEmpty()) {
            throw new SeqStoreException("Could not retrieve all sequences.");
        }
        return res;
    }
    
    private byte[] convertQuality(byte[] in) {
        // FIXME convert to transport encoding
        return in;
    }
}
