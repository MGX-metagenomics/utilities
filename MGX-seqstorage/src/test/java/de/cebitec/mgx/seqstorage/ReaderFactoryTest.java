/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.seqstorage;

import de.cebitec.mgx.seqholder.ReadSequenceI;
import de.cebitec.mgx.sequence.SeqReaderI;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author sjaenick
 */
public class ReaderFactoryTest {

    public ReaderFactoryTest() {
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

//    @Test
//    public void testGetSFFReader() throws Exception {
//        System.out.println("getSFFReader");
//        File sffFile = copyTestData();
//        
//        ReaderFactory rf = new ReaderFactory();
//        
//        SeqReaderI<? extends ReadSequenceI> reader = rf.getReader(sffFile.getAbsolutePath());
//        assertNotNull(reader);
//        assertTrue(reader instanceof SFFReader);
//        int numReads = 0;
//        while (reader.hasMoreElements()) {
//            ReadSequenceI next = reader.nextElement();
//            String sName = new String(next.getSequence().getName());
//            String dna = new String(next.getSequence().getSequence());
//            assertEquals("EM7RWTF01EXJBZ", sName);
//            assertEquals("GATCGCGCGCCGAGGCATTCGCCGCCGTACCCTGGCCAACGCTCGAGCCCAGCGGTCAGTCGCGTCGGATGGTCAGACACGACAACGAGGGAGTAGGACGAAGGCAACACGGAGGGGAGTAGG", dna);
//            numReads++;
//        }
//        assertEquals(1, numReads);
//        sffFile.delete();
//    }

    private File copyTestData() {
        File f = null;
        try (BufferedInputStream is = new BufferedInputStream(getClass().getClassLoader().getResourceAsStream("de/cebitec/mgx/seqstorage/oneread.sff"))) {
            f = File.createTempFile("seq", "fas");
            try (FileOutputStream fos = new FileOutputStream(f)) {
                byte[] buffer = new byte[1024];
                int bytesRead = is.read(buffer);
                while (bytesRead >= 0) {
                    fos.write(buffer, 0, bytesRead);
                    bytesRead = is.read(buffer);
                }

            }
        } catch (Exception ex) {
            fail(ex.getMessage());
        }
        assertNotNull(f);
        return f;
    }
}
