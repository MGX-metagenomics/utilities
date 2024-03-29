/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.sffreader;

import de.cebitec.mgx.sffreader.datatypes.SFFRead;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.io.TempDir;

/**
 *
 * @author sj
 */
@TestInstance(Lifecycle.PER_CLASS)
public class SFFReaderTest {

    @TempDir
    public Path folder;
    
    private File fileWithIndex, fileWithoutIndex;

    public SFFReaderTest() {
    }

    @BeforeEach
    public void setUp() {
        try {
            fileWithIndex = folder.resolve("oneread.sff").toFile();
            InputStream is = getClass().getClassLoader().getResourceAsStream("de/cebitec/mgx/oneread.sff");
            FileOutputStream fos = new FileOutputStream(fileWithIndex);
            int i;
            while ((i = is.read()) != -1) {
                fos.write(i);
            }
            fos.close();
            fileWithoutIndex = folder.resolve("multipleRead.sff").toFile();
            is = getClass().getClassLoader().getResourceAsStream("de/cebitec/mgx/multipleRead.sff");
            fos = new FileOutputStream(fileWithoutIndex);
            while ((i = is.read()) != -1) {
                fos.write(i);
            }
            fos.close();
        } catch (IOException ex) {
            Logger.getLogger(SFFReaderTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @AfterAll
    public void tearDown() {
        fileWithIndex.delete();
        fileWithoutIndex.delete();
    }

//    Index is optional
//    @org.junit.Test
//    public void testIndex() throws IOException {
//        System.err.println("testIndex");
//        SFFReader r = new SFFReader(f.getAbsolutePath());
//        assertEquals(1, r.getNumberOfReads());
//        assertEquals("TCAG", r.getKeySequence());
//        assertEquals(1, r.size());
//        assertEquals(4256, r.getIndexOffset());
//        long offset = r.getOffset("FI5LW4G01DZDXZ");
//        assertEquals(840, offset);
//    }
    @Test
    public void testInvalidName() throws IOException {
        System.err.println("testInvalidName");
//        System.err.println("With index:");
        SFFReader r = new SFFReader(fileWithIndex.getAbsolutePath());
//        assertEquals(4256, r.getIndexOffset());
        SFFRead read = r.getRead("DoesNotExist");
        assertEquals(null, read);
//        System.err.println("Without index:");
        r = new SFFReader(fileWithoutIndex.getAbsolutePath());
//        assertEquals(4256, r.getIndexOffset());
        read = r.getRead("DoesNotExist");
        assertEquals(null, read);
    }

    @Test
    public void testReadOneRead() throws IOException {
        System.err.println("testReadOneRead");
        SFFReader r = new SFFReader(fileWithIndex.getAbsolutePath());
        assertEquals(1, r.getNumberOfReads());
        assertEquals("TCAG", r.getKeySequence());
        assertEquals(1, r.getNumberOfReads());
        while (r.hasMoreElements()) {
            SFFRead s = r.nextElement();
//            System.err.println(s.getName());
//            System.err.println(s.getBases());
            assertEquals("TTTGCCATCGGCGCAGTCCTACTTATGAAGTTTGCAGAATAGCGTCAAGGCACTACCAAGGGG", s.getBases());
        }
    }

    @Test
    public void testReadOneQuality() throws IOException {
        System.err.println("testReadOneQuality");
        SFFReader r = new SFFReader(fileWithIndex.getAbsolutePath());
        assertEquals(1, r.getNumberOfReads());
        assertEquals("TCAG", r.getKeySequence());
        assertEquals(1, r.getNumberOfReads());
        while (r.hasMoreElements()) {
            SFFRead s = r.nextElement();
//            System.err.println(s.getName());
//            System.err.println(s.getQuality().length);
            byte[] quality = "FFFFFFFFFFFIIIIIIIIIIIIIIIIIIIHHHIHB;:8@?GGGDB::88?==4/----,,,,".getBytes();
            for (int i = 0; i < quality.length; i++) {
                quality[i] -= 33;
            }
            assertArrayEquals(quality, s.getQuality());
        }
    }

    @Test
    public void testReadAll() throws IOException {
        System.err.println("testReadAll");
        SFFReader r = new SFFReader(fileWithIndex.getAbsolutePath());
        assertEquals(1, r.getNumberOfReads());
        assertEquals("TCAG", r.getKeySequence());
        assertEquals(1, r.getNumberOfReads());

        while (r.hasMoreElements()) {
            SFFRead read = r.nextElement();
            assertNotNull(read);
        }
    }

    @Test
    public void testGetSpecificRead() throws IOException {
        System.err.println("testGetSpecificRead");
//        System.err.println("With index:");
        SFFReader r = new SFFReader(fileWithIndex.getAbsolutePath());
        SFFRead s = r.getRead("FI5LW4G01DZDXZ");
//        System.err.println(s.getName());
//        System.err.println(s.getBases());
        assertEquals("TTTGCCATCGGCGCAGTCCTACTTATGAAGTTTGCAGAATAGCGTCAAGGCACTACCAAGGGG", s.getBases());
//        System.err.println("Without index:");
        r = new SFFReader(fileWithoutIndex.getAbsolutePath());
        s = r.getRead("E4AIXVY07D52JT");
//        System.err.println(s.getName());
//        System.err.println(s.getBases());
        assertEquals("CAGTTTGGACATAGCAAGAAGCGAATTGGCTATTAACGGTAAAGAAATTTTTAAAACAGTTATTAATATGGCAGACTATGCTAGAAAAGAAGTTAATAAAATAGGCGATTATTATGCATTCGGTGAAGAGATAATAAATAATGATGATATATATTCTTTTGATAATACAAAGCTATGCATACAT", s.getBases());
    }

//    @org.junit.Test
//    public void testIdxOffset() throws IOException {
//        System.err.println("testIdxOffset");
//
//        RandomAccessFile raf = new RandomAccessFile("/home/sj/EM7RWTF01.sff", "r");
//        RandomAccessFile raf2 = new BufferedRandomAccessFile("/home/sj/EM7RWTF01.sff", "r");
//
//        raf.seek(0);
//        long magic = Util.readUint32(raf);
//        assert magic == 0x2E736666;
//        byte[] version = new byte[4];
//        raf.read(version);
//
//        raf2.seek(0);
//        long magic2 = Util.readUint32(raf2);
//        assert magic2 == 0x2E736666;
//        byte[] version2 = new byte[4];
//        raf2.read(version2);
//
//        assertEquals(magic, magic2);
//        assertArrayEquals(version, version2);
//    }
//    @org.junit.Test
//    public void testTiming() throws IOException {
//        System.err.println("testTiming");
//
//        long d = System.currentTimeMillis();
//        RandomAccessFile raf = new RandomAccessFile("/home/sj/EM7RWTF01.sff", "r");
//        SFFReader r = new SFFReader(raf);
//        assertEquals(160048984, r.getIndexOffset());
//        assertEquals(125446, r.getNumberOfReads());
//        assertEquals("TCAG", r.getKeySequence());
//        assertEquals(125446, r.size());
//
//        for (String s : r.keySet()) {
//            String read = r.getRead(s);
//        }
//        d = System.currentTimeMillis() - d;
//        System.err.println("RAF: " + d + " ms");
//        //
//        //
//        long d1 = System.currentTimeMillis();
//        RandomAccessFile raf2 = new BufferedRandomAccessFile("/home/sj/EM7RWTF01.sff", "r");
//        SFFReader r2 = new SFFReader(raf2);
//        assertEquals(160048984, r2.getIndexOffset());
//        assertEquals(125446, r2.getNumberOfReads());
//        assertEquals("TCAG", r2.getKeySequence());
//        assertEquals(125446, r2.size());
//
//        for (String s : r2.keySet()) {
//            String read = r2.getRead(s);
//        }
//        d1 = System.currentTimeMillis() - d1;
//        System.err.println("BRAF: " + d1 + " ms");
//    }
}
