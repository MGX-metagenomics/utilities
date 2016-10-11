/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.qc.impl;

import de.cebitec.mgx.qc.DataRowI;
import de.cebitec.mgx.qc.QCResult;
import de.cebitec.mgx.seqstorage.FastaReader;
import de.cebitec.mgx.sequence.SeqStoreException;
import java.io.File;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author sjaenick
 */
public class NucleotideDistributionTest {

    public NucleotideDistributionTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
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
    public void testAdd() throws SeqStoreException {
        System.out.println("add");
        FastaReader r = new FastaReader("src/test/resources/de/cebitec/mgx/qc/testdata.fas", false);
        NucleotideDistribution ld = new NucleotideDistribution();
        while (r.hasMoreElements()) {
            ld.add(r.nextElement());
        }
        ld.get();

    }

}
