package de.cebitec.mgx.seqstorage;

import de.cebitec.mgx.sequence.DNASequenceI;
import de.cebitec.mgx.sequence.SeqStoreException;
import de.cebitec.mgx.sequence.SeqWriterI;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 *
 * @author sjaenick
 */
public class FastaWriter implements SeqWriterI<DNASequenceI> {

    private final BufferedWriter seqout;

    public FastaWriter(String filename) throws SeqStoreException {
        try {
            seqout = new BufferedWriter(new FileWriter(filename));
        } catch (IOException ex) {
            throw new SeqStoreException("Cannot write to " + filename);
        }
    }

    @Override
    public synchronized void addSequence(DNASequenceI seq) throws SeqStoreException {
        if (seqout == null) {
            throw new SeqStoreException("Writer has already been closed.");
        }
        StringBuilder sb = new StringBuilder(">");
        sb.append(new String(seq.getName()));
        sb.append(System.lineSeparator());
        sb.append(new String(seq.getSequence()).toUpperCase());
        sb.append(System.lineSeparator());
        try {
            seqout.write(sb.toString());
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
