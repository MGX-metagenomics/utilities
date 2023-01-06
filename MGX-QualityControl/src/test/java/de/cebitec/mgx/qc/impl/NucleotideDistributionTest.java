/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.qc.impl;

import de.cebitec.mgx.qc.DataRowI;
import de.cebitec.mgx.qc.QCResult;
import de.cebitec.mgx.seqcompression.SequenceException;
import de.cebitec.mgx.seqstorage.FastaReader;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;

/**
 *
 * @author sjaenick
 */
public class NucleotideDistributionTest {

    public NucleotideDistributionTest() {
    }

    @Test
    public void testEmpty() {
        System.out.println("testEmpty");
        NucleotideDistribution gc = new NucleotideDistribution();
        QCResult qc = gc.get();
        assertNotNull(qc);
        DataRowI[] data = qc.getData();
        assertNotNull(data);
        DataRowI d = data[0];
        float[] f = d.getData();

        assertEquals(0, f.length);

        for (int i = 0; i < f.length; i++) {
            assertEquals(0.0, f[i], 0.00001);
            System.err.print(f[i] + ",");
        }
        System.err.println();
    }

    @Test
    public void testAdd() throws SequenceException {
        System.out.println("add");
        FastaReader r = new FastaReader("src/test/resources/de/cebitec/mgx/qc/testdata.fas", false);
        NucleotideDistribution ld = new NucleotideDistribution();
        while (r.hasMoreElements()) {
            ld.add(r.nextElement());
        }
        ld.get();

    }

}
