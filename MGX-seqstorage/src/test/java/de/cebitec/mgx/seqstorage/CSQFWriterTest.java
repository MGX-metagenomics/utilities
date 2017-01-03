package de.cebitec.mgx.seqstorage;

import de.cebitec.mgx.osgiutils.MGXOptions;
import de.cebitec.mgx.sequence.*;
import de.cebitec.mgx.testutils.TestInput;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.junit.*;
import static org.junit.Assert.*;
import org.ops4j.pax.exam.Configuration;
import static org.ops4j.pax.exam.CoreOptions.bundle;
import static org.ops4j.pax.exam.CoreOptions.junitBundles;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.options;
import static org.ops4j.pax.exam.CoreOptions.systemProperty;
import org.ops4j.pax.exam.Option;

/**
 *
 * @author sj
 */
//@RunWith(PaxExam.class)
public class CSQFWriterTest {

    @Configuration
    public static Option[] configuration() {
        return options(
                junitBundles(),
                mavenBundle().groupId("de.cebitec.mgx").artifactId("MGX-isequences").versionAsInProject(),
                mavenBundle().groupId("de.cebitec.mgx").artifactId("Trove-OSGi").versionAsInProject(),
                mavenBundle().groupId("de.cebitec.mgx").artifactId("MGX-BufferedRandomAccessFile").versionAsInProject(),
                mavenBundle().groupId("de.cebitec.mgx").artifactId("SFFReader").versionAsInProject(),
                mavenBundle().groupId("org.apache.commons").artifactId("commons-math3").versionAsInProject(),
                MGXOptions.serviceLoaderBundles(),
                MGXOptions.testUtils(),
                systemProperty("org.ops4j.pax.logging.DefaultServiceLog.level").value("WARN"),
                bundle("reference:file:target/classes")
        );
    }

    @Test
    public void testCSQRegression() throws Exception {
        System.out.println("testCSQRegression");
        File f = TestInput.copyTestResource(getClass(), "de/cebitec/mgx/seqstorage/csq_regression.fq");
        File target = File.createTempFile("testCSQRegression", "xx");
        target.delete();
        try (FASTQReader fr = new FASTQReader(f.getAbsolutePath(), false)) {
            try (CSQFWriter csq = new CSQFWriter(target)) {
                assertTrue(fr.hasMoreElements());
                DNAQualitySequenceI holder = fr.nextElement();
                assertNotNull(holder);
                byte[] seq = holder.getSequence();
                byte[] qual = holder.getQuality();

                assertEquals("length of sequence and qualities must not differ", seq.length, qual.length);
                assertEquals("GAGAGGGGAGGGAGAGGGAAAGAGAAGAGAAAGAAGGGGAGAGAGGGGGGGGGGGAAGAGGAAAGAGAAG"
                        + "TGAAAGAGAAGGAGAGAAGGGAGAGGGGAGGGGGAGAGAGAGGAGGGAAAAGAGGAGAGGAGGACGAGAGGAGA"
                        + "AGGCGAAGAACGAGAAAAAGGAGGAGAGGAGGAAGAGGTAAGATGGAGGCAGAGGGAAAAGAGAAGGAGAGGAG"
                        + "GGAGAGTGAAGATGGCGTCAGACGTGAAAGAGTAAGATAGTACGGCCCGTTCATATGATCACCTACGTCCGACT"
                        + "CTACGATTGTATGACCCGTTCATATGGTCAACTACCTCAAACACTCTGATATCACGGCCCCTTCATATGCCCTC"
                        + "CTACATTCTACACTATGTTACTACTGCCCGTTCTTCTGCCCTCCTACATCCCACCATACGATTCTATTCCCCCT"
                        + "TTCTTTTTCCTCCTCCCTCCCCCACTCCTATTCTCTCACCCCTTCTTTTTTCCCCCTTTCTCCCTCTCTCTCCT"
                        + "TTTCCCCCCCCTTCCTATCCCCTCCCTCCTCCCCCTCTTTC", new String(seq));
                csq.addSequence(holder);
            }
        } catch (SeqStoreException ex) {
            fail(ex.getMessage());
        }

        // read from CSQ
        try (CSQFReader reader = new CSQFReader(target.getAbsolutePath(), false)) {
            assertTrue(reader.hasMoreElements());
            DNAQualitySequenceI qseq = reader.nextElement();
            assertNotNull(qseq);
            assertEquals(qseq.getSequence().length, qseq.getQuality().length);
            assertEquals("GAGAGGGGAGGGAGAGGGAAAGAGAAGAGAAAGAAGGGGAGAGAGGGGGGGGGGGAAGAGGAAAGAGAAG"
                    + "TGAAAGAGAAGGAGAGAAGGGAGAGGGGAGGGGGAGAGAGAGGAGGGAAAAGAGGAGAGGAGGACGAGAGGAGA"
                    + "AGGCGAAGAACGAGAAAAAGGAGGAGAGGAGGAAGAGGTAAGATGGAGGCAGAGGGAAAAGAGAAGGAGAGGAG"
                    + "GGAGAGTGAAGATGGCGTCAGACGTGAAAGAGTAAGATAGTACGGCCCGTTCATATGATCACCTACGTCCGACT"
                    + "CTACGATTGTATGACCCGTTCATATGGTCAACTACCTCAAACACTCTGATATCACGGCCCCTTCATATGCCCTC"
                    + "CTACATTCTACACTATGTTACTACTGCCCGTTCTTCTGCCCTCCTACATCCCACCATACGATTCTATTCCCCCT"
                    + "TTCTTTTTCCTCCTCCCTCCCCCACTCCTATTCTCTCACCCCTTCTTTTTTCCCCCTTTCTCCCTCTCTCTCCT"
                    + "TTTCCCCCCCCTTCCTATCCCCTCCCTCCTCCCCCTCTTTC", new String(qseq.getSequence()));
            assertFalse(reader.hasMoreElements());
        }

        f.delete();
        try {
            new CSQFReader(target.getAbsolutePath(), false).delete();
        } catch (SeqStoreException ex) {
        }
    }

    @Test
    public void testCSQFWriter() throws Exception {
        System.out.println("testCSQFWriter");
        File f = TestInput.copyTestResource(getClass(), "de/cebitec/mgx/seqstorage/sample_1.fq");
        File target = File.createTempFile("testCSQFWriter", "xx");
        target.delete();
        try (FASTQReader fr = new FASTQReader(f.getAbsolutePath(), false)) {
            try (CSQFWriter csq = new CSQFWriter(target)) {
                while (fr.hasMoreElements()) {
                    DNAQualitySequenceI holder = fr.nextElement();
                    assertNotNull(holder);
                    assertNotNull(holder.getSequence());
                    csq.addSequence(holder);
                }
            }
        } catch (SeqStoreException ex) {
            fail(ex.getMessage());
        } finally {
            f.delete();
            //target.delete();
            try {
                new CSQFReader(target.getAbsolutePath(), false).delete();
            } catch (SeqStoreException ex) {
            }
        }
    }

    @Test
    public void testCSQFReader() throws Exception {
        System.out.println("testCSQFReader");
        File f = TestInput.copyTestResource(getClass(), "de/cebitec/mgx/seqstorage/sample_1.fq");
        File target = File.createTempFile("testCSQFReader", "");
        target.delete();
        List<DNAQualitySequenceI> writer = new ArrayList<>();
        try (FASTQReader fr = new FASTQReader(f.getAbsolutePath(), false)) {
            try (CSQFWriter csq = new CSQFWriter(target)) {
                while (fr.hasMoreElements()) {
                    DNAQualitySequenceI holder = fr.nextElement();
                    assertNotNull(holder);
                    assertNotNull(holder.getSequence());
                    csq.addSequence(holder);
                    writer.add(holder);
                }
            }
            List<DNAQualitySequenceI> reader = new ArrayList<>();
            try (CSQFReader r = new CSQFReader(target.getAbsolutePath(), false)) {
                int i = 0;
                while (r.hasMoreElements()) {
                    DNAQualitySequenceI s = r.nextElement();
                    reader.add(s);
                    assertNotNull(s);
                    String seq = new String(s.getSequence());
                    // all sequences have to be uppercase
                    assertEquals(seq.toUpperCase(), seq);
                }
                r.delete();
            }
            for (int i = 0; i < writer.size(); i++) {
                Assert.assertArrayEquals(writer.get(i).getSequence(), reader.get(i).getSequence());
                Assert.assertArrayEquals(writer.get(i).getQuality(), reader.get(i).getQuality());
            }

        } catch (SeqStoreException ex) {
            fail(ex.getMessage());
        } finally {
            target.delete();
            f.delete();
        }
    }
    
    @Test
    public void testWithEmptySequence() throws Exception {
        File f = TestInput.copyTestResource(getClass(), "de/cebitec/mgx/seqstorage/sampleWithEmptyEntry.fq");
        File target = File.createTempFile("testWithEmptySequence", "");
        target.delete();
        List<DNAQualitySequenceI> writer = new ArrayList<>();
        try (FASTQReader fr = new FASTQReader(f.getAbsolutePath(), false)) {
            try (CSQFWriter csq = new CSQFWriter(target)) {
                while (fr.hasMoreElements()) {
                    DNAQualitySequenceI holder = fr.nextElement();
                    assertNotNull(holder);
                    assertNotNull(holder.getSequence());
                    csq.addSequence(holder);
                    writer.add(holder);
                }
            }
            List<DNAQualitySequenceI> reader = new ArrayList<>();
            try (CSQFReader r = new CSQFReader(target.getAbsolutePath(), false)) {
                int i = 0;
                while (r.hasMoreElements()) {
                    DNAQualitySequenceI s = r.nextElement();
                    reader.add(s);
                    assertNotNull(s);
                    String seq = new String(s.getSequence());
                    // all sequences have to be uppercase
                    assertEquals(seq.toUpperCase(), seq);
                }
                r.delete();
            }
            for (int i = 0; i < writer.size(); i++) {
                Assert.assertArrayEquals(writer.get(i).getSequence(), reader.get(i).getSequence());
                Assert.assertArrayEquals(writer.get(i).getQuality(), reader.get(i).getQuality());
            }

        } catch (SeqStoreException ex) {
            fail(ex.getMessage());
        } finally {
            target.delete();
            f.delete();
        }
    }
}
