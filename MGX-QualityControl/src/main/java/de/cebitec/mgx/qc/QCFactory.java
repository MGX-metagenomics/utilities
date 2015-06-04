/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.qc;

import de.cebitec.mgx.qc.impl.*;

/**
 *
 * @author sj
 */
public class QCFactory {

    public static Analyzer[] getQCAnalyzers(boolean hasQuality) {
//        return new Analyzer[]{};
        if (hasQuality) {
            return new Analyzer[]{new LengthDistribution(), new NucleotideDistribution(), new GCDistribution(), new QualityDistribution()};
        } else {
            return new Analyzer[]{new LengthDistribution(), new NucleotideDistribution(), new GCDistribution()};
        }
    }

}
