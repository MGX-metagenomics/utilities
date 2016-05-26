package de.cebitec.mgx.seqstorage;

import static de.cebitec.mgx.seqstorage.encoding.FileMagic.lineSeparator;
import de.cebitec.mgx.sequence.DNASequenceI;
import de.cebitec.mgx.sequence.SeqStoreException;
import de.cebitec.mgx.sequence.SeqWriterI;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 *
 * @author sjaenick
 */
public class FastaWriter implements SeqWriterI<DNASequenceI> {

    private final BufferedOutputStream seqout;

    public FastaWriter(String filename) throws SeqStoreException {
        try {
            seqout = new BufferedOutputStream(new FileOutputStream(filename));
        } catch (IOException ex) {
            throw new SeqStoreException("Cannot write to " + filename);
        }
    }

    @Override
    public synchronized void addSequence(DNASequenceI seq) throws SeqStoreException {
        if (seqout == null) {
            throw new SeqStoreException("Writer has already been closed.");
        }
        if (seq == null) {
            throw new SeqStoreException("null seq!");
        }
        try {
            seqout.write('>');
            seqout.write(seq.getName());
            seqout.write(lineSeparator);
            seqout.write(seq.getSequence());
            seqout.write(lineSeparator);
        } catch (IOException ex) {
            throw new SeqStoreException(ex.getMessage());
        }
    }

    @Override
    public void close() throws IOException {
        if (seqout != null) {
            seqout.close();
        }
    }
}
