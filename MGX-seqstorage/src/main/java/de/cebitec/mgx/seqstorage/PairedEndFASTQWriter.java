package de.cebitec.mgx.seqstorage;

import de.cebitec.mgx.seqcompression.SequenceException;
import de.cebitec.mgx.sequence.DNAQualitySequenceI;
import de.cebitec.mgx.sequence.SeqWriterI;
import java.io.IOException;

/**
 *
 * @author sjaenick
 */
public class PairedEndFASTQWriter implements SeqWriterI<DNAQualitySequenceI> {

    private final FASTQWriter first;
    private final FASTQWriter second;
    private boolean useFirst = true;

    public PairedEndFASTQWriter(String filename, QualityEncoding qualityEncoding) throws SequenceException {
        int idx = filename.lastIndexOf(".");
        String prefix = filename.substring(0, idx);
        String suffix = filename.substring(idx);
        first = new FASTQWriter(prefix + "_R1" + suffix, qualityEncoding);
        second = new FASTQWriter(prefix + "_R2" + suffix, qualityEncoding);
    }

    @Override
    public synchronized void addSequence(DNAQualitySequenceI seq) throws SequenceException {
        if (useFirst) {
            useFirst = false;
            first.addSequence(seq);
        } else {
            useFirst = true;
            second.addSequence(seq);
        }
    }

    @Override
    public void close() throws IOException {
        first.close();
        second.close();
    }
}
