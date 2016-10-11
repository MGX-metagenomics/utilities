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
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Assume;

/**
 *
 * @author sjaenick
 */
public class GCDistributionTest {

    public GCDistributionTest() {
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
        GCDistribution gc = new GCDistribution();
        QCResult qc = gc.get();
        assertNotNull(qc);
        DataRowI[] data = qc.getData();
        assertNotNull(data);
        DataRowI d = data[0];
        float[] f = d.getData();

        assertEquals(101, f.length);

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
        GCDistribution gc = new GCDistribution();
        while (r.hasMoreElements()) {
            gc.add(r.nextElement());
        }
        QCResult qc = gc.get();
        assertNotNull(qc);
        DataRowI[] data = qc.getData();
        assertEquals(1, data.length);
        DataRowI d = data[0];
        float[] f = d.getData();
        assertEquals(101, f.length);

        for (int i = 0; i < f.length; i++) {
            System.err.print(f[i] + ",");
        }
        System.err.println();
    }

//    @Test
//    public void testAdd2() throws SeqStoreException {
//        System.out.println("add2");
//        //Assume.assumeTrue(new File("/homes/sjaenick/Myco.fas").exists());
//        String fname = "/vol/mgx-data/MGX_Neandertal/seqruns/20";
//        //FastaReader r = new FastaReader("/homes/sjaenick/Myco.fas", false);
//        CSFReader r = new CSFReader(fname, false);
//        GCDistribution gc = new GCDistribution();
//        while (r.hasMoreElements()) {
//            gc.add(r.nextElement().getSequence());
//        }
//        assertEquals(22, gc.getNumberOfSequences());
//        QCResult qc = gc.get();
//        assertNotNull(qc);
//        DataRowI[] data = qc.getData();
//        assertEquals(1, data.length);
//        DataRowI d = data[0];
//        float[] f = d.getData();
//        assertEquals(101, f.length);
//
//        for (int i = 0; i < f.length; i++) {
//            System.err.print(f[i] + ",");
//        }
//        System.err.println();
//    }

}
