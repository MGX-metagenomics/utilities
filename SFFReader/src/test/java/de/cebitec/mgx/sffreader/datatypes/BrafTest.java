/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.sffreader.datatypes;

import de.cebitec.mgx.braf.BufferedRandomAccessFile;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 *
 * @author sj
 */
public class BrafTest {
 @Rule
    public TemporaryFolder folder = new TemporaryFolder();
    private File f;

    public BrafTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

   @Before
    public void setUp() {
        try {
            f = folder.newFile();
            InputStream is = getClass().getClassLoader().getResourceAsStream("de/cebitec/mgx/oneread.sff");
            FileOutputStream fos = new FileOutputStream(f);
            int i;
            while ((i = is.read()) != -1) {
                fos.write(i);
            }
            fos.close();
        } catch (IOException ex) {
            Logger.getLogger(BrafTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @After
    public void tearDown() {
        f.delete();
        folder.delete();
    }

    @Test
    public void testReadUint8() throws IOException {
        System.err.println("testReadUint8");
        RandomAccessFile raf = new RandomAccessFile(f.getAbsolutePath(), "r");
        long read = 0;
        RandomAccessFile raf2 = new BufferedRandomAccessFile(f.getAbsolutePath(), "r", 100);
        long read2 = 0;

        int d1, d2;

        while ((d1 = Util.readUint8(raf)) != 255) {
            d2 = Util.readUint8(raf2);
            Assert.assertEquals(d1, d2);
            Assert.assertEquals(raf.getFilePointer(), raf2.getFilePointer());
            read2++;
            read++;
        }
        Assert.assertEquals(read, read2);
    }

    @Test
    public void testReadUint16() throws IOException {
        System.err.println("testReadUint16");
        RandomAccessFile raf = new RandomAccessFile(f.getAbsolutePath(), "r");
        long read = 0;
        RandomAccessFile raf2 = new BufferedRandomAccessFile(f.getAbsolutePath(), "r", 100);
        long read2 = 0;

        int d1, d2;

        while ((d1 = Util.readUint16(raf)) != 65535) {
            d2 = Util.readUint16(raf2);
            Assert.assertEquals(d1, d2);
            Assert.assertEquals(raf.getFilePointer(), raf2.getFilePointer());
            read2++;
            read++;
        }
        Assert.assertEquals(read, read2);
    }

    @Test
    public void testReadUint32() throws IOException {
        System.err.println("testReadUint32");
        RandomAccessFile raf = new RandomAccessFile(f.getAbsolutePath(), "r");
        long read = 0;
        RandomAccessFile raf2 = new BufferedRandomAccessFile(f.getAbsolutePath(), "r", 100);
        long read2 = 0;

        long d1, d2;

        while ((d1 = Util.readUint32(raf)) != 4294967295l) {
            d2 = Util.readUint32(raf2);
            Assert.assertEquals(d1, d2);
            Assert.assertEquals(raf.getFilePointer(), raf2.getFilePointer());
            read2++;
            read++;
        }
        Assert.assertEquals(read, read2);
    }

    @Test
    public void testReadUint64() throws IOException {
        System.err.println("testReadUint64");
        RandomAccessFile raf = new RandomAccessFile(f.getAbsolutePath(), "r");
        long read = 0;
        RandomAccessFile raf2 = new BufferedRandomAccessFile(f.getAbsolutePath(), "r", 100);
        long read2 = 0;

        long d1, d2;

        while ((d1 = Util.readUint64(raf)) != -1) {
            d2 = Util.readUint64(raf2);
            Assert.assertEquals(d1, d2);
            Assert.assertEquals(raf.getFilePointer(), raf2.getFilePointer());
            read2++;
            read++;
        }
        Assert.assertEquals(read, read2);
    }
}
