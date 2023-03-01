package de.cebitec.mgx.seqcompression;

import java.io.Serial;

/**
 *
 * @author sjaenick
 */
public class SequenceException extends Exception {

    @Serial
    private static final long serialVersionUID = 6401253773779951803L;

    public SequenceException(String s) {
        super(s);
    }
}
