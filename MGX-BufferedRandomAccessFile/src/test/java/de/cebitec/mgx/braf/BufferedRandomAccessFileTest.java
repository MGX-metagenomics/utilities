/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.braf;

import java.io.IOException;
import java.io.RandomAccessFile;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author sj
 */
public class BufferedRandomAccessFileTest {

    public BufferedRandomAccessFileTest() {
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
    public void testRead() throws IOException {
        System.err.println("testRead");
        RandomAccessFile raf = new RandomAccessFile("/home/sj/oneread.sff", "r");
        long read = 0;
        RandomAccessFile raf2 = new BufferedRandomAccessFile("/home/sj/oneread.sff", "r", 100);
        long read2 = 0;

        int d1, d2;

        while ((d1 = raf.read()) != -1) {
            d2 = raf2.read();
            Assert.assertEquals(d1, d2);
            Assert.assertEquals(raf.getFilePointer(), raf2.getFilePointer());
            read2++;
            read++;
        }
        Assert.assertEquals(read, read2);
    }

    @Test
    public void testSeek() throws IOException {
        System.err.println("testSeek");
        RandomAccessFile raf = new RandomAccessFile("/home/sj/oneread.sff", "r");
        long read = 0;
        RandomAccessFile raf2 = new BufferedRandomAccessFile("/home/sj/oneread.sff", "r", 100);
        long read2 = 0;

        int d1, d2;

        raf.seek(7);
        raf2.seek(7);
        Assert.assertEquals(raf.getFilePointer(), raf2.getFilePointer());

        while ((d1 = raf.read()) != -1) {
            d2 = raf2.read();
            Assert.assertEquals(d1, d2);
            Assert.assertEquals(raf.getFilePointer(), raf2.getFilePointer());
            read2++;
            read++;
        }
        Assert.assertEquals(read, read2);
    }
}
