/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.braf;

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
public class BufferedRandomAccessFileTest {

    @TempDir
    public Path folder;
    
    private File f;

    public BufferedRandomAccessFileTest() {
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
            Logger.getLogger(BufferedRandomAccessFileTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @AfterEach
    public void tearDown() {
        f.delete();
    }

    @Test
    public void testRead() throws IOException {
        System.err.println("testRead");
        RandomAccessFile raf = new RandomAccessFile(f.getAbsolutePath(), "r");
        long read = 0;
        RandomAccessFile raf2 = new BufferedRandomAccessFile(f.getAbsolutePath(), "r", 100);
        long read2 = 0;

        int d1, d2;

        while ((d1 = raf.read()) != -1) {
            d2 = raf2.read();
            assertEquals(d1, d2);
            assertEquals(raf.getFilePointer(), raf2.getFilePointer());
            read2++;
            read++;
        }
        assertEquals(read, read2);
    }

    @Test
    public void testSeek() throws IOException {
        System.err.println("testSeek");
        RandomAccessFile raf = new RandomAccessFile(f.getAbsolutePath(), "r");
        long read = 0;
        RandomAccessFile raf2 = new BufferedRandomAccessFile(f.getAbsolutePath(), "r", 100);
        long read2 = 0;

        int d1, d2;

        raf.seek(7);
        raf2.seek(7);
        assertEquals(raf.getFilePointer(), raf2.getFilePointer());

        while ((d1 = raf.read()) != -1) {
            d2 = raf2.read();
            assertEquals(d1, d2);
            assertEquals(raf.getFilePointer(), raf2.getFilePointer());
            read2++;
            read++;
        }
        assertEquals(read, read2);
    }
}
