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
    public boolean hasMoreElements() throws SeqStoreException {

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

        if (l1[0] != '@') {
            throw new SeqStoreException("FASTQ Error: missing \'@\' in line " + new String(l1));
        }
        if (l3 != null && l3[0] != '+') {
            throw new SeqStoreException("FASTQ Error: missing \'+\' in line " + new String(l3));
        }

        // remove leading '@' from sequence name
        byte[] seqname = new byte[l1.length - 1];
        System.arraycopy(l1, 1, seqname, 0, l1.length - 1);

        if (l4 == null) {
            throw new SeqStoreException("Error for sequence " + new String(seqname) + ", no quality?");
        }

        if (l2.length != l4.length) {
            throw new SeqStoreException("Error in FASTQ file: length differs between sequence and quality for " + new String(seqname));
        }

//        R A or G
//        Y C or T
//        S G or C
//        W A or T
//        K G or T
//        M A or C
//        B C or G or T
//        D A or G or T
//        H A or C or T
//        V A or C or G 
        // validate nucleotide sequence, convert to uppercase if necessary
        for (int i = 0; i < l2.length; i++) {
            switch (l2[i]) {
                case 'A':
                case 'T':
                case 'G':
                case 'C':
                case 'R':
                case 'Y':
                case 'S':
                case 'W':
                case 'K':
                case 'M':
                case 'B':
                case 'D':
                case 'H':
                case 'V':
                case 'N':
                    break;
                case 'a':
                    l2[i] = 'A';
                    break;
                case 't':
                    l2[i] = 'T';
                    break;
                case 'g':
                    l2[i] = 'G';
                    break;
                case 'c':
                    l2[i] = 'C';
                    break;
                case 'r':
                    l2[i] = 'R';
                    break;
                case 'y':
                    l2[i] = 'Y';
                    break;
                case 's':
                    l2[i] = 'S';
                    break;
                case 'w':
                    l2[i] = 'W';
                    break;
                case 'k':
                    l2[i] = 'K';
                    break;
                case 'm':
                    l2[i] = 'M';
                    break;
                case 'b':
                    l2[i] = 'B';
                    break;
                case 'd':
                    l2[i] = 'D';
                    break;
                case 'h':
                    l2[i] = 'H';
                    break;
                case 'v':
                    l2[i] = 'V';
                    break;
                case 'n':
                    l2[i] = 'N';
                    break;
                default:
                    throw new SeqStoreException("Illegal nucleotide " + l2[i] + " at position " + i + " of sequence " + new String(seqname));
            }
        }

        seq = new QualityDNASequence();
        seq.setName(seqname);
        seq.setSequence(l2);
        seq.setQuality(convertQuality(l4));     //quality as phred scores

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

    private byte[] convertQuality(byte[] in) throws SeqStoreException {
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
    public boolean hasQuality() {
        return true;
    }
}
