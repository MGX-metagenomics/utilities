/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.sffreader;

import de.cebitec.mgx.braf.BufferedRandomAccessFile;
import de.cebitec.mgx.sffreader.datatypes.Util;
import java.io.IOException;
import java.io.RandomAccessFile;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;

/**
 *
 * @author sj
 */
public class SFFReaderTest {

    public SFFReaderTest() {
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

    @org.junit.Test
    public void testIndex() throws IOException {
        System.err.println("testIndex");
        SFFReader r = new SFFReader("/home/sj/oneread.sff");
        assertEquals(1, r.getNumberOfReads());
        assertEquals("TCAG", r.getKeySequence());
        assertEquals(1, r.size());
        assertEquals(1656, r.getIndexOffset());
        long offset = r.getOffset("EM7RWTF01EXJBZ");
        assertEquals(440, offset);
    }

    @org.junit.Test
    public void testSomeMethod() throws IOException {
        System.err.println("readOneRead");
        SFFReader r = new SFFReader("/home/sj/oneread.sff");
        assertEquals(1, r.getNumberOfReads());
        assertEquals("TCAG", r.getKeySequence());
        assertEquals(1, r.size());
        for (String s : r.keySet()) {
            System.err.println(s);
            System.err.println(r.getRead(s));
            assertEquals("GATCGCGCGCCGAGGCATTCGCCGCCGTACCCTGGCCAACGCTCGAGCCCAGCGGTCAGTCGCGTCGGATGGTCAGACACGACAACGAGGGAGTAGGACGAAGGCAACACGGAGGGGAGTAGGN", r.getRead(s));
        }
    }

    @org.junit.Test
    public void testSomeMethod2() throws IOException {
        System.err.println("readManyReads");
        SFFReader r = new SFFReader("/home/sj/EM7RWTF01.sff");
        assertEquals(125446, r.getNumberOfReads());
        assertEquals("TCAG", r.getKeySequence());
        assertEquals(125446, r.size());

        for (String s : r.keySet()) {
            String read = r.getRead(s);
        }
    }

    @org.junit.Test
    public void testIdxOffset() throws IOException {
        System.err.println("testIdxOffset");

        RandomAccessFile raf = new RandomAccessFile("/home/sj/EM7RWTF01.sff", "r");
        RandomAccessFile raf2 = new BufferedRandomAccessFile("/home/sj/EM7RWTF01.sff", "r");

        raf.seek(0);
        long magic = Util.readUint32(raf);
        assert magic == 0x2E736666;
        byte[] version = new byte[4];
        raf.read(version);
//        long index_offset = Util.readUint64(raf);

        raf2.seek(0);
        long magic2 = Util.readUint32(raf2);
        assert magic2 == 0x2E736666;
        byte[] version2 = new byte[4];
        raf2.read(version2);
//        long index_offset2 = Util.readUint64(raf2);
        
        assertEquals(magic, magic2);
        assertArrayEquals(version, version2);
//        assertEquals(index_offset, index_offset2);
    }

    @org.junit.Test
    public void testTiming() throws IOException {
        System.err.println("testTiming");

        long d = System.currentTimeMillis();
        RandomAccessFile raf = new RandomAccessFile("/home/sj/EM7RWTF01.sff", "r");
        SFFReader r = new SFFReader(raf);
        assertEquals(160048984, r.getIndexOffset());
        assertEquals(125446, r.getNumberOfReads());
        assertEquals("TCAG", r.getKeySequence());
        assertEquals(125446, r.size());

        for (String s : r.keySet()) {
            String read = r.getRead(s);
        }
        d = System.currentTimeMillis() - d;
        System.err.println("RAF: " + d + " ms");
        //
        //
        long d1 = System.currentTimeMillis();
        RandomAccessFile raf2 = new BufferedRandomAccessFile("/home/sj/EM7RWTF01.sff", "r");
        SFFReader r2 = new SFFReader(raf2);
        assertEquals(160048984, r2.getIndexOffset());
        assertEquals(125446, r2.getNumberOfReads());
        assertEquals("TCAG", r2.getKeySequence());
        assertEquals(125446, r2.size());

        for (String s : r2.keySet()) {
            String read = r2.getRead(s);
        }
        d1 = System.currentTimeMillis() - d1;
        System.err.println("BRAF: " + d1 + " ms");
    }
}
