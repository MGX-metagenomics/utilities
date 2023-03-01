/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.newick;

import java.io.Serial;

/**
 *
 * @author sjaenick
 */
public class ParserException extends Exception {

    @Serial
    private static final long serialVersionUID = 1L;

    public ParserException(Throwable cause) {
        super(cause);
    }

}
