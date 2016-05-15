package de.cebitec.mgx.seqstorage;

import de.cebitec.mgx.sequence.DNAQualitySequenceI;
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
public class FASTQReader implements SeqReaderI<DNAQualitySequenceI> {

    private QualityDNASequence seq = null;
    private final ByteStreamTokenizer stream;
    private final String fastqfile;
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
    public boolean hasMoreElements() throws SeqStoreException {

        byte[] l1, l2, l3, l4;

        try {
            l1 = stream.hasNext() ? stream.next() : null; // header
            l2 = stream.hasNext() ? stream.next() : null; // dna sequence
            l3 = stream.hasNext() ? stream.next() : null; // quality header
            l4 = stream.hasNext() ? stream.next() : null; // quality string
        } catch (Exception e) {
            return false;
        }

        if ((l1 == null) || (l2 == null)) {
            return false;
        }

        if (l1[0] != '@') {
            throw new SeqStoreException("FASTQ Error: missing \'@\' in line " + new String(l1));
        }
        if (l3 != null && l3[0] != '+') {
            throw new SeqStoreException("FASTQ Error: missing \'+\' in line " + new String(l3));
        }

        // remove leading '@' from sequence name
        int nameLen = l1.length - 1;
        if (l1[1 + nameLen - 1] == '\r') {
            nameLen--;
        }
        byte[] seqname = new byte[nameLen];
        System.arraycopy(l1, 1, seqname, 0, nameLen);

        if (l4 == null) {
            throw new SeqStoreException("Error for sequence " + new String(seqname) + ", no quality?");
        }

        if (l2.length != l4.length) {
            throw new SeqStoreException("Error in FASTQ file: length differs between sequence and quality for " + new String(seqname));
        }

        seq = new QualityDNASequence();
        seq.setName(seqname);

        //check and trim remainder of DOS line breaks
        int targetLen = l2.length > 0 && l2[l2.length - 1] == '\r' ? l2.length - 1 : l2.length;
        byte[] dnasequence = new byte[targetLen];
        System.arraycopy(l2, 0, dnasequence, 0, targetLen);
        seq.setSequence(dnasequence);

        int qLen = l4.length > 0 && l4[l4.length - 1] == '\r' ? l4.length - 1 : l4.length;
        byte[] qseq = new byte[qLen];
        System.arraycopy(l4, 0, qseq, 0, qLen);
        seq.setQuality(convertQuality(qseq));     //quality as phred scores

        return true;
    }

    @Override
    public DNAQualitySequenceI nextElement() {
        return seq;
    }

    @Override
    public void close() {
        if (stream != null) {
            stream.close();
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
    public Set<DNAQualitySequenceI> fetch(long[] ids) throws SeqStoreException {
        Set<DNAQualitySequenceI> res = new HashSet<>(ids.length);
        Set<Long> idList = new HashSet<>(ids.length);
        for (int i = 0; i < ids.length; i++) {
            idList.add(ids[i]);
        }
        while (hasMoreElements() && !idList.isEmpty()) {
            DNAQualitySequenceI elem = nextElement();
            if (idList.contains(elem.getId())) {
                res.add(elem);
                idList.remove(elem.getId());
            }
        }

        if (!idList.isEmpty()) {
            throw new SeqStoreException("Could not retrieve all sequences.");
        }
        return res;
    }

    private static byte[] convertQuality(byte[] in) throws SeqStoreException {
        if (in == null) {
            throw new SeqStoreException("Cannot convert null quality string.");
        }
        byte[] out = new byte[in.length];
        for (int i = 0; i < in.length; i++) {
            out[i] = (byte) (in[i] - 33);
        }

        return out;
    }

    @Override
    public final boolean hasQuality() {
        return true;
    }
}
