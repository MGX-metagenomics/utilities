/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.common;

/**
 *
 * @author sj
 */
public enum ToolScope {
    
    READ(0), // single-read based analysis
    ASSEMBLY(1), // workflow that perform assembly
    GENE_ANNOTATION(2); // annotate predicted genes on assembled contigs

    private final int code;

    private ToolScope(int c) {
        code = c;
    }

    public int getValue() {
        return code;
    }
}
