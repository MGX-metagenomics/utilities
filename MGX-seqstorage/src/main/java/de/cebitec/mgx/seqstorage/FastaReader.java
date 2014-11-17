package de.cebitec.mgx.seqstorage;

import de.cebitec.mgx.seqholder.DNASequenceHolder;
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
public class FastaReader implements SeqReaderI<DNASequenceHolder> {

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
    public boolean hasMoreElements() {
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
        seq.setSequence(dnasequence);
        return true;
    }

    @Override
    public DNASequenceHolder nextElement() {
        return new DNASequenceHolder(seq);
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
    public Set<DNASequenceHolder> fetch(long[] ids) throws SeqStoreException {
        Set<DNASequenceHolder> res = new HashSet<>(ids.length);
        Set<Long> idList = new HashSet<>(ids.length);
        for (int i = 0; i < ids.length; i++) {
            idList.add(ids[i]);
        }
        while (hasMoreElements() && !idList.isEmpty()) {
            DNASequenceHolder elem = nextElement();
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
}
