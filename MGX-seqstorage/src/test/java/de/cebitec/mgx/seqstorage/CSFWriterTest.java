/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.seqstorage;

import de.cebitec.mgx.osgiutils.MGXOptions;
import de.cebitec.mgx.sequence.DNASequenceI;
import de.cebitec.mgx.sequence.SeqStoreException;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import static org.ops4j.pax.exam.CoreOptions.bundle;
import static org.ops4j.pax.exam.CoreOptions.junitBundles;
import static org.ops4j.pax.exam.CoreOptions.options;
import static org.ops4j.pax.exam.CoreOptions.systemProperty;
import static org.ops4j.pax.exam.CoreOptions.url;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;

/**
 *
 * @author sj
 */
@RunWith(PaxExam.class)
public class CSFWriterTest {

    @Configuration
    public static Option[] configuration() {
        return options(
                junitBundles(),
                url("link:classpath:de.cebitec.mgx.MGX-isequences.link"),
                url("link:classpath:de.cebitec.mgx.Trove-OSGi.link"),
                url("link:classpath:de.cebitec.mgx.MGX-BufferedRandomAccessFile.link"),
                url("link:classpath:de.cebitec.mgx.SFFReader.link"),
                url("link:classpath:org.apache.commons.math3.link"),
                MGXOptions.serviceLoaderBundles(),
                systemProperty("org.ops4j.pax.logging.DefaultServiceLog.level").value("WARN"),
                bundle("reference:file:target/classes")
        );
    }

    @Test
    public void testCSFWriter() throws IOException {
        System.out.println("testCSFWriter");
        File f = copyTestData();
        File target = File.createTempFile("testCSFWriter", "xx");
        target.delete();
        try (FastaReader fr = new FastaReader(f.getAbsolutePath(), false)) {
            try (CSFWriter csf = new CSFWriter(target)) {
                while (fr.hasMoreElements()) {
                    DNASequenceI holder = fr.nextElement();
                    assertNotNull(holder);
                    assertNotNull(holder.getSequence());
                    csf.addSequence(holder);
                }
            }
        } catch (SeqStoreException ex) {
            fail(ex.getMessage());
        } finally {
            try {
                new CSFReader(target.getAbsolutePath(), false).delete();
            } catch (SeqStoreException ex) {
            }
            //target.delete();
            f.delete();
        }
    }

    @Test
    public void testCSFReader() throws IOException {
        System.out.println("testCSFReader");
        File f = copyTestData();
        File target = File.createTempFile("testCSFReader", "xx");
        target.delete();
        try (FastaReader fr = new FastaReader(f.getAbsolutePath(), false)) {
            try (CSFWriter csf = new CSFWriter(target)) {
                while (fr.hasMoreElements()) {
                    DNASequenceI holder = fr.nextElement();
                    assertNotNull(holder);
                    assertNotNull(holder.getSequence());
                    csf.addSequence(holder);
                }
            }

            try (CSFReader r = new CSFReader(target.getAbsolutePath(), false)) {
                while (r.hasMoreElements()) {
                    DNASequenceI s = r.nextElement();
                    assertNotNull(s);
                    String seq = new String(s.getSequence());
                    // all sequences have to be uppercase
                    assertEquals(seq.toUpperCase(), seq);
                }
                r.delete();
            }

        } catch (SeqStoreException ex) {
            fail(ex.getMessage());
        } finally {
            f.delete();
            target.delete();
        }
    }

    private File copyTestData() {
        File f = null;
        try (BufferedInputStream is = new BufferedInputStream(CSFWriter.class.getClassLoader().getResourceAsStream("de/cebitec/mgx/seqstorage/test.fas"))) {
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
