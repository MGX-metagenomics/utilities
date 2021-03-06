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
import de.cebitec.mgx.sequence.SeqStoreException;
import java.io.File;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Assume;

/**
 *
 * @author sjaenick
 */
public class LengthDistributionTest {

    public LengthDistributionTest() {
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
        LengthDistribution gc = new LengthDistribution();
        QCResult qc = gc.get();
        assertNotNull(qc);
        DataRowI[] data = qc.getData();
        assertNotNull(data);
        DataRowI d = data[0];
        float[] f = d.getData();

        assertEquals(1, f.length);
        assertEquals(0.0, f[0], 0.001);

    }

    @Test
    public void testAdd() throws SequenceException {
        System.out.println("add");
        FastaReader r = new FastaReader("src/test/resources/de/cebitec/mgx/qc/testdata.fas", false);
        LengthDistribution ld = new LengthDistribution();
        while (r.hasMoreElements()) {
            ld.add(r.nextElement());
        }
        ld.get();
    }

}
