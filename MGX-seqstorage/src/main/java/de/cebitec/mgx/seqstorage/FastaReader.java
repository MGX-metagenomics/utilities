package de.cebitec.mgx.seqstorage;

import de.cebitec.mgx.seqstorage.encoding.ByteUtils;
import de.cebitec.mgx.sequence.DNASequenceI;
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
public class FastaReader implements SeqReaderI<DNASequenceI> {

    private ByteStreamTokenizer stream = null;
    private byte[] buf = null;
    private DNASequenceI seq = null;
    private String fastafile = null;
    public static final byte LINEBREAK = '\n';

    public FastaReader(String filename, boolean gzipCompressed) throws SeqStoreException {
        fastafile = filename;
        try {
            stream = new ByteStreamTokenizer(fastafile, gzipCompressed, LINEBREAK, 0);
        } catch (IOException ex) {
            throw new SeqStoreException("File not found or unreadable: " + fastafile);
        }

        // read first line
        buf = stream.hasMoreElements() ? stream.nextElement() : null; // header
    }

    @Override
    public boolean hasMoreElements() throws SeqStoreException {
        if (stream == null) {
            return false;
        }

        if (buf.length == 0) {
            while (buf.length == 0 && stream.hasMoreElements()) {
                buf = stream.nextElement();
            }
        }

        // sequence header has to start with '>'
        if (buf.length == 0 || buf[0] != '>') {
            return false;
        }

        // process sequence header
        byte[] seqname = new byte[buf.length - 1];
        System.arraycopy(buf, 1, seqname, 0, buf.length - 1);

        // check sequence name for whitespaces and trim
        int trimPos = 0;
        for (int i = 0; i < seqname.length; i++) {
            if (seqname[i] == ' ' || seqname[i] == '\t') {
                trimPos = i;
                break;
            }
        }
        if (trimPos > 0) {
            byte[] tmp = new byte[trimPos];
            System.arraycopy(seqname, 0, tmp, 0, trimPos);
            seqname = tmp;
        }

        seq = new DNASequence();
        seq.setName(seqname);

        byte[] dnasequence = null;

        while (stream.hasMoreElements()) {
            buf = stream.nextElement();

            if (buf.length > 0 && buf[0] == '>') {
                // we have reached the next sequence
                seq.setSequence(dnasequence);
                return true;
            }

            if (dnasequence == null) {
                // start new sequence
                dnasequence = new byte[buf.length];
                System.arraycopy(buf, 0, dnasequence, 0, buf.length);
            } else {
                dnasequence = ByteUtils.concat(dnasequence, buf);
            }
        }

        // validate nucleotide sequence, convert to uppercase if necessary
        for (int i = 0; i < dnasequence.length; i++) {
            switch (dnasequence[i]) {
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
                    dnasequence[i] = 'A';
                    break;
                case 't':
                    dnasequence[i] = 'T';
                    break;
                case 'g':
                    dnasequence[i] = 'G';
                    break;
                case 'c':
                    dnasequence[i] = 'C';
                    break;
                case 'r':
                    dnasequence[i] = 'R';
                    break;
                case 'y':
                    dnasequence[i] = 'Y';
                    break;
                case 's':
                    dnasequence[i] = 'S';
                    break;
                case 'w':
                    dnasequence[i] = 'W';
                    break;
                case 'k':
                    dnasequence[i] = 'K';
                    break;
                case 'm':
                    dnasequence[i] = 'M';
                    break;
                case 'b':
                    dnasequence[i] = 'B';
                    break;
                case 'd':
                    dnasequence[i] = 'D';
                    break;
                case 'h':
                    dnasequence[i] = 'H';
                    break;
                case 'v':
                    dnasequence[i] = 'V';
                    break;
                case 'n':
                    dnasequence[i] = 'N';
                    break;
                default:
                    throw new SeqStoreException("Illegal nucleotide " + dnasequence[i] + " at position " + i + " of sequence " + new String(seqname));
            }
        }

        seq.setSequence(dnasequence);
        return true;
    }

    @Override
    public DNASequenceI nextElement() {
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
        File f = new File(fastafile);
        if (f.exists() && f.isFile()) {
            f.delete();
        }
    }

    @Override
    public Set<DNASequenceI> fetch(long[] ids) throws SeqStoreException {
        Set<DNASequenceI> res = new HashSet<>(ids.length);
        Set<Long> idList = new HashSet<>(ids.length);
        for (int i = 0; i < ids.length; i++) {
            idList.add(ids[i]);
        }
        while (hasMoreElements() && !idList.isEmpty()) {
            DNASequenceI elem = nextElement();
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

    @Override
    public boolean hasQuality() {
        return false;
    }
}
