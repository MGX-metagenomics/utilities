/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.seqstorage;

import de.cebitec.mgx.sequence.DNAQualitySequenceI;
import de.cebitec.mgx.sequence.DNASequenceI;
import de.cebitec.mgx.sequence.SeqStoreException;
import de.cebitec.mgx.testutils.TestInput;
import java.io.File;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author sj
 */
public class CSFWriterTest {

//    @Configuration
//    public static Option[] configuration() {
//        return options(
//                junitBundles(),
//                mavenBundle().groupId("de.cebitec.mgx").artifactId("MGX-isequences").versionAsInProject(),
//                mavenBundle().groupId("de.cebitec.mgx").artifactId("Trove-OSGi").versionAsInProject(),
//                mavenBundle().groupId("de.cebitec.mgx").artifactId("MGX-BufferedRandomAccessFile").versionAsInProject(),
//                mavenBundle().groupId("de.cebitec.mgx").artifactId("SFFReader").versionAsInProject(),
//                mavenBundle().groupId("org.apache.commons").artifactId("commons-math3").versionAsInProject(),
//                //MGXOptions.serviceLoaderBundles(),
//                MGXOptions.testUtils(),
//                systemProperty("org.ops4j.pax.logging.DefaultServiceLog.level").value("WARN"),
//                bundle("reference:file:target/classes")
//        );
//    }

    @Test
    public void testCSFWriter() throws Exception {
        System.out.println("testCSFWriter");
        File f = TestInput.copyTestResource(getClass(), "de/cebitec/mgx/seqstorage/test.fas");
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
    public void testCSFReader() throws Exception {
        System.out.println("testCSFReader");
        File f = TestInput.copyTestResource(getClass(), "de/cebitec/mgx/seqstorage/test.fas");
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

    @Test
    public void testLongRead() throws Exception {
        System.out.println("testLongRead");
        File f = TestInput.copyTestResource(getClass(), "de/cebitec/mgx/seqstorage/nptest.fq");
        File target = File.createTempFile("testCSFReader", "xxx");
        target.delete();

        int cnt = 0;
        DNAQualitySequenceI seq = null;
        try (FASTQReader fr = new FASTQReader(f.getAbsolutePath(), false)) {
            try (CSFWriter csq = new CSFWriter(target)) {
                while (fr.hasMoreElements()) {
                    seq = fr.nextElement();
                    csq.addSequence(seq);
                    cnt++;
                }
            }
        } catch (SeqStoreException ex) {
            fail(ex.getMessage() + " after sequence " + new String(seq.getName()));
        }
        assertEquals(2, cnt);

        // read back result
        CSFReader r = new CSFReader(target.getAbsolutePath(), false);
        cnt = 0;
        while (r.hasMoreElements()) {
            DNASequenceI s = r.nextElement();
            assertNotNull(s);
            cnt++;
        }
        assertEquals(2, cnt);
        target.delete();
    }

//    private File copyTestData() {
//        File f = null;
//        try (BufferedInputStream is = new BufferedInputStream(CSFWriter.class.getClassLoader().getResourceAsStream("de/cebitec/mgx/seqstorage/test.fas"))) {
//            f = File.createTempFile("seq", "fas");
//            try (FileOutputStream fos = new FileOutputStream(f)) {
//                byte[] buffer = new byte[1024];
//                int bytesRead = is.read(buffer);
//                while (bytesRead >= 0) {
//                    fos.write(buffer, 0, bytesRead);
//                    bytesRead = is.read(buffer);
//                }
//
//            }
//        } catch (Exception ex) {
//            fail(ex.getMessage());
//        }
//        assertNotNull(f);
//        return f;
//    }
}
