package de.cebitec.mgx.sequence;

import de.cebitec.mgx.seqcompression.SequenceException;
import java.io.Serial;

/**
 *
 * @author sjaenick
 */
public class SeqStoreException extends SequenceException {

    @Serial
    private static final long serialVersionUID = 6401253773779951803L;

    public SeqStoreException(String s) {
        super(s);
    }
}
