/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.seqstorage;

/**
 *
 * @author sj
 */
public enum QualityEncoding {
    //Illumina5 == Illumina 1.5; Illumina3 == Illumina 1.3
    
    Sanger(33),
    Illumina5(64),
    Illumina3(64), 
    Solexa(64),
    Unknown(0);

    private final int offset; // ASCII offset for quality scores

    private QualityEncoding(int c) {
        offset = c;
    }

    public int getOffset() {
        return offset;
    }

}
