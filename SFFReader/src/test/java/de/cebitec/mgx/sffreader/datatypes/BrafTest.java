/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.sffreader.datatypes;

import com.sun.tools.javac.util.Assert;
import de.cebitec.mgx.braf.BufferedRandomAccessFile;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 *
 * @author sj
 */
public class BrafTest {

    @TempDir
    public Path folder;
    private File f;

    public BrafTest() {
    }

    @BeforeEach
    public void setUp() {
        try {
            f = folder.resolve("oneread.sff").toFile();
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

    @AfterEach
    public void tearDown() {
        f.delete();
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
            assertEquals(d1, d2);
            assertEquals(raf.getFilePointer(), raf2.getFilePointer());
            read2++;
            read++;
        }
        assertEquals(read, read2);
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
            assertEquals(d1, d2);
            assertEquals(raf.getFilePointer(), raf2.getFilePointer());
            read2++;
            read++;
        }
        assertEquals(read, read2);
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
            assertEquals(d1, d2);
            assertEquals(raf.getFilePointer(), raf2.getFilePointer());
            read2++;
            read++;
        }
        assertEquals(read, read2);
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
            assertEquals(d1, d2);
            assertEquals(raf.getFilePointer(), raf2.getFilePointer());
            read2++;
            read++;
        }
        assertEquals(read, read2);
    }
}
