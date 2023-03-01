package de.cebitec.mgx.kegg.pathways;

import java.io.Serial;

/**
 *
 * @author sj
 */
public class KEGGException extends Exception {

    @Serial
    private static final long serialVersionUID = 6401253773779951803L;

    public KEGGException(String message) {
        super(message);
    }

    public KEGGException(Throwable t) {
        super(t);
    }

}
