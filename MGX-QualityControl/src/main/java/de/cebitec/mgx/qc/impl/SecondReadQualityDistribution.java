/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.qc.impl;

import de.cebitec.mgx.sequence.DNAQualitySequenceI;

/**
 *
 * @author sj
 */
public class SecondReadQualityDistribution extends QualityDistributionBase {

    public SecondReadQualityDistribution() {
        super();
    }

    @Override
    public void addPair(DNAQualitySequenceI seq1, DNAQualitySequenceI seq2) {
        add(seq2);
    }

    @Override
    public String getDescription() {
        return "Read 2 Phred quality score distribution";
    }

}
