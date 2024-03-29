package de.cebitec.mgx.seqstorage;

import de.cebitec.mgx.seqcompression.ByteUtils;
import de.cebitec.mgx.seqcompression.SequenceException;
import de.cebitec.mgx.sequence.DNASequenceI;
import de.cebitec.mgx.sequence.SeqReaderI;
import de.cebitec.mgx.sequence.SeqStoreException;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
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
    private final String fastafile;
    private final boolean gzipCompressed;
    public static final byte LINEBREAK = '\n';

    public FastaReader(String filename, boolean gzipCompressed) throws SeqStoreException {
        fastafile = filename;
        this.gzipCompressed = gzipCompressed;
    }

    @Override
    public synchronized boolean hasMoreElements() throws SequenceException {
        if (seq != null) {
            return true;
        }

        if (stream == null) {
            try {
                stream = new ByteStreamTokenizer(fastafile, gzipCompressed, LINEBREAK, 0);
                buf = stream.hasNext() ? stream.next() : null; // header
            } catch (IOException ex) {
                throw new SeqStoreException("File not found or unreadable: " + fastafile);
            }
        }

        if (buf == null || buf.length == 0) {
            while ((buf == null || buf.length == 0) && stream.hasNext()) {
                buf = stream.next();
            }
        }

        // sequence header has to start with '>'
        if (buf == null || buf.length == 0 || buf[0] != '>') {
            return false;
        }

        // process sequence header
        int nameLen = buf.length - 1; // subtract '>'
        if (buf[1 + nameLen - 1] == '\r') {
            nameLen--;
        }
        byte[] seqname = new byte[nameLen];
        System.arraycopy(buf, 1, seqname, 0, nameLen);

//        // check sequence name for whitespaces and trim
//        int trimPos = 0;
//        for (int i = 0; i < seqname.length; i++) {
//            if (seqname[i] == ' ' || seqname[i] == '\t') {
//                trimPos = i;
//                break;
//            }
//        }
//        if (trimPos > 0) {
//            byte[] tmp = new byte[trimPos];
//            System.arraycopy(seqname, 0, tmp, 0, trimPos);
//            seqname = tmp;
//        }

        byte[] dnasequence = null;

        while (stream.hasNext()) {
            buf = stream.next();

            if (buf.length > 0 && buf[0] == '>') {
                // we have reached the next sequence
                seq = new DNASequence(dnasequence == null ? new byte[0] : dnasequence, true);
                seq.setName(seqname);
                return true;
            }

            if (dnasequence == null) {
                // start new sequence

                //check and trim remainder of DOS line breaks
                int targetLen = buf.length > 0 && buf[buf.length - 1] == '\r' ? buf.length - 1 : buf.length;
                dnasequence = new byte[targetLen];
                System.arraycopy(buf, 0, dnasequence, 0, targetLen);
            } else {
                // extend current sequence
                dnasequence = buf.length > 0 && buf[buf.length - 1] == '\r'
                        ? ByteUtils.concat(dnasequence, Arrays.copyOf(buf, buf.length - 1))
                        : ByteUtils.concat(dnasequence, buf);
            }
        }

        seq = new DNASequence(dnasequence == null ? new byte[0] : dnasequence, true);
        seq.setName(seqname);
        return true;
    }

    @Override
    public synchronized DNASequenceI nextElement() {
        DNASequenceI ret = seq;
        seq = null;
        return ret;
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
    public Set<DNASequenceI> fetch(long[] ids) throws SequenceException {
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
    public final boolean hasQuality() {
        return false;
    }
}
