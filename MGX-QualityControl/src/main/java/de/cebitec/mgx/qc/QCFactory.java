/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.qc;

import de.cebitec.mgx.qc.impl.*;
import de.cebitec.mgx.sequence.DNASequenceI;

/**
 *
 * @author sjaenick
 */
public class QCFactory {

    @SuppressWarnings("rawtypes")
    public static <T extends DNASequenceI> Analyzer<T>[] getQCAnalyzers(boolean hasQuality, boolean isPaired) {
        
        if (hasQuality) {
            if (isPaired) {
                return new Analyzer[]{new FirstReadQualityDistribution(),new SecondReadQualityDistribution(),
                    new LengthDistribution(), new NucleotideDistribution(), new GCDistribution()};
            } else {
                return new Analyzer[]{new FirstReadQualityDistribution(), new LengthDistribution(), new NucleotideDistribution(), new GCDistribution()};
            }
        } else {
            return new Analyzer[]{new LengthDistribution(), new NucleotideDistribution(), new GCDistribution()};
        }
    }

}
