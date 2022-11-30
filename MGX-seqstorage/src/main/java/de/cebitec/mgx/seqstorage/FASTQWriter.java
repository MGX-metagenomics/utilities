package de.cebitec.mgx.seqstorage;

import de.cebitec.mgx.seqcompression.SequenceException;
import static de.cebitec.mgx.seqstorage.encoding.FileMagic.lineSeparator;
import de.cebitec.mgx.sequence.DNAQualitySequenceI;
import de.cebitec.mgx.sequence.SeqStoreException;
import de.cebitec.mgx.sequence.SeqWriterI;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 *
 * @author sjaenick
 */
public class FASTQWriter implements SeqWriterI<DNAQualitySequenceI> {

    private final BufferedOutputStream seqout;
    private final QualityEncoding encoding;

    public FASTQWriter(String filename, QualityEncoding qualityEncoding) throws SequenceException {
        if (qualityEncoding == QualityEncoding.Unknown) {
            throw new SeqStoreException("Invalid quality encoding.");
        }
        
        encoding = qualityEncoding;
        
        try {
            seqout = new BufferedOutputStream(new FileOutputStream(filename));
        } catch (IOException ex) {
            throw new SeqStoreException("Cannot write to " + filename);
        }
    }

    @Override
    public synchronized void addSequence(DNAQualitySequenceI seq) throws SequenceException {
        if (seqout == null) {
            throw new SeqStoreException("Writer has already been closed.");
        }
        if (seq == null) {
            throw new SeqStoreException("null seq!");
        }
        if (seq.getSequence() == null || seq.getQuality() == null) {
            throw new SeqStoreException("Sequence lacks DNA sequence or quality information");
        }
        if (seq.getSequence().length != seq.getQuality().length) {
            throw new SeqStoreException("Error in quality sequence: length differs between sequence and quality.");
        }
        if (seq.getName() == null) {
            throw new SeqStoreException("Sequence has no name.");
        }
        try {
            seqout.write('@');
            seqout.write(seq.getName());
            seqout.write(lineSeparator);
            seqout.write(seq.getSequence());
            seqout.write(lineSeparator);
            seqout.write('+');
            seqout.write(lineSeparator);
            seqout.write(convertQuality(seq.getQuality()));
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

    private byte[] convertQuality(byte[] in) throws SequenceException {
        if (in == null) {
            throw new SeqStoreException("Cannot convert null quality string.");
        }
        
        int qualityOffset = encoding.getOffset();

        byte[] out = new byte[in.length];
        for (int i = 0; i < in.length; i++) {
            out[i] = (byte) (in[i] + qualityOffset);
        }

        return out;
    }
}
