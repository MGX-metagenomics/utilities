package de.cebitec.mgx.seqstorage;

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
    private int qualityOffset = 0;

    public FASTQWriter(String filename, QualityEncoding qualityEncoding) throws SeqStoreException {
        if (qualityEncoding == QualityEncoding.Unknown) {
            throw new SeqStoreException("Invalid quality encoding.");
        }

        switch (qualityEncoding) {
            case Illumina3:
                qualityOffset = 64;
                break;
            case Illumina5:
                qualityOffset = 64;
                break;
            case Sanger:
                qualityOffset = 33;
                break;
            case Solexa:
                qualityOffset = 64;
                break;
        }
        try {
            seqout = new BufferedOutputStream(new FileOutputStream(filename));
        } catch (IOException ex) {
            throw new SeqStoreException("Cannot write to " + filename);
        }
    }

    @Override
    public synchronized void addSequence(DNAQualitySequenceI seq) throws SeqStoreException {
        if (seqout == null) {
            throw new SeqStoreException("Writer has already been closed.");
        }
        if (seq == null) {
            throw new SeqStoreException("null seq!");
        }
        if (seq.getSequence().length != seq.getQuality().length) {
            throw new SeqStoreException("Error in quality sequence: length differs between sequence and quality.");
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

    private byte[] convertQuality(byte[] in) throws SeqStoreException {
        if (in == null) {
            throw new SeqStoreException("Cannot convert null quality string.");
        }

        byte[] out = new byte[in.length];
        for (int i = 0; i < in.length; i++) {
            out[i] = (byte) (in[i] + qualityOffset);
        }

        return out;
    }
}
