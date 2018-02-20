package de.cebitec.mgx.seqstorage;

import de.cebitec.mgx.sequence.DNAQualitySequenceI;
import de.cebitec.mgx.sequence.SeqStoreException;
import de.cebitec.mgx.testutils.TestInput;
import java.io.*;
import org.junit.*;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

/**
 *
 * @author patrick
 */
//@RunWith(PaxExam.class)
public class FastqTest {

//    @Configuration
//    public static Option[] configuration() {
//        return options(
//                junitBundles(),
//                mavenBundle().groupId("de.cebitec.mgx").artifactId("MGX-isequences").versionAsInProject(),
//                mavenBundle().groupId("de.cebitec.mgx").artifactId("Trove-OSGi").versionAsInProject(),
//                mavenBundle().groupId("de.cebitec.mgx").artifactId("MGX-BufferedRandomAccessFile").versionAsInProject(),
//                mavenBundle().groupId("de.cebitec.mgx").artifactId("SFFReader").versionAsInProject(),
//                mavenBundle().groupId("org.apache.commons").artifactId("commons-math3").versionAsInProject(),
//                MGXOptions.serviceLoaderBundles(),
//                MGXOptions.testUtils(),
//                systemProperty("org.ops4j.pax.logging.DefaultServiceLog.level").value("WARN"),
//                bundle("reference:file:target/classes")
//        );
//    }
    @Test
    public void testReadFastq() throws Exception {
        System.out.println("readFastq");
        File f = TestInput.copyTestResource(getClass(), "de/cebitec/mgx/seqstorage/sample_1.fq");
        int seqCnt = 0;
        try (FASTQReader fr = new FASTQReader(f.getAbsolutePath(), false)) {
            while (fr.hasMoreElements()) {
                fr.nextElement();
                seqCnt++;
            }
        } catch (SeqStoreException ex) {
            fail(ex.getMessage());
        }
        f.delete();

        assertEquals(20, seqCnt);
    }

    @Test
    public void testWriteFastq() throws Exception {
        System.out.println("testWriteFastq");
        File f = TestInput.copyTestResource(getClass(), "de/cebitec/mgx/seqstorage/sample_1.fq");
        int seqCnt = 0;
        File target = File.createTempFile("testFQWriter", "xx");
        FASTQWriter fw = new FASTQWriter(target.getAbsolutePath(), QualityEncoding.Illumina5);
        try (FASTQReader fr = new FASTQReader(f.getAbsolutePath(), false)) {
            while (fr.hasMoreElements()) {
                DNAQualitySequenceI qseq = fr.nextElement();
                fw.addSequence(qseq);
                seqCnt++;
            }
        } catch (SeqStoreException ex) {
            fail(ex.getMessage());
        } finally {
            fw.close();
            //target.delete();
        }
        f.delete();

        assertEquals(20, seqCnt);
    }

    @Test
    public void testReadDOSFastq() throws Exception {
        System.out.println("testReadDOSFastq");
        File f = TestInput.copyTestResource(getClass(), "de/cebitec/mgx/seqstorage/sample_1_dos.fq");
        int seqCnt = 0;
        DNAQualitySequenceI qseq = null;
        try (FASTQReader fr = new FASTQReader(f.getAbsolutePath(), false)) {
            while (fr.hasMoreElements()) {
                qseq = fr.nextElement();
                assertNotNull(qseq);
                seqCnt++;
            }
        } catch (SeqStoreException ex) {
            fail(ex.getMessage());
        }
        f.delete();

        assertEquals(20, seqCnt);

        assertNotNull(qseq);
    }

    @Test
    public void testFirstReadInFastq() throws Exception {
        System.out.println("FirstReadInFastq");
        File f = TestInput.copyTestResource(getClass(), "de/cebitec/mgx/seqstorage/sample_1.fq");
        try (FASTQReader fr = new FASTQReader(f.getAbsolutePath(), false)) {
            fr.hasMoreElements();
            DNAQualitySequenceI entry = fr.nextElement();
            Assert.assertArrayEquals("IRIS:7:1:17:394#0/1".getBytes(), entry.getName());
            Assert.assertArrayEquals("GTCAGGACAAGAAAGACAANTCCAATTNACATTATG".getBytes(), entry.getSequence());
            byte[] quality = "aaabaa`]baaaaa_aab]D^^`b`aYDW]abaa`^".getBytes();
            for (int i = 0; i < quality.length; i++) {
                quality[i] -= 64;
            }
            assertArrayEquals(quality, entry.getQuality());
        } catch (SeqStoreException ex) {
            fail(ex.getMessage());
        }
        f.delete();
    }

    @Test
    public void testIncompleteLastLine() throws Exception {
        System.out.println("testIncompleteLastLine");
        // file has no line break after last line
        File f = TestInput.copyTestResource(getClass(), "de/cebitec/mgx/seqstorage/incomplete_last_line.fq");
        int seqCnt = 0;
        DNAQualitySequenceI seq = null;
        try (FASTQReader fr = new FASTQReader(f.getAbsolutePath(), false)) {
            while (fr.hasMoreElements()) {
                seq = fr.nextElement();
                seqCnt++;
            }
        } catch (SeqStoreException ex) {
            fail(ex.getMessage());
        } finally {
            f.delete();
        }
        assertEquals(1, seqCnt);
        assertNotNull(seq);
        assertEquals(seq.getSequence().length, seq.getQuality().length);
    }

    @Test
    public void testBrokenSeq() throws Exception {
        System.out.println("testBrokenSeq");
        // file has no line break after last line
        File f = TestInput.copyTestResource(getClass(), "de/cebitec/mgx/seqstorage/broken_seq.fq");
        DNAQualitySequenceI seq = null;
        try (FASTQReader fr = new FASTQReader(f.getAbsolutePath(), false)) {
            while (fr.hasMoreElements()) {
                seq = fr.nextElement();
            }
        } catch (SeqStoreException ex) {
            if (ex.getMessage().contains("length differs between sequence and qualit")) {
                return;
            }
        } finally {
            f.delete();
        }
        fail("FASTQReader produced sequence with differing sequence and quality length.");
    }

    @Test
    public void testLowerCaseInput() throws Exception {
        System.out.println("testLowerCaseInput");
        File f = TestInput.copyTestResource(getClass(), "de/cebitec/mgx/seqstorage/lowercase.fq");
        try (FASTQReader fr = new FASTQReader(f.getAbsolutePath(), false)) {
            fr.hasMoreElements();
            DNAQualitySequenceI entry = fr.nextElement();
            Assert.assertEquals("TCGGT", new String(entry.getSequence()));
        } catch (SeqStoreException ex) {
            fail(ex.getMessage());
        }
        f.delete();
    }

    @Test
    public void testIUPAC() throws Exception {
        System.out.println("testIUPAC");
        File f = TestInput.copyTestResource(getClass(), "de/cebitec/mgx/seqstorage/regression_iupac.fq");
        int seqCnt = 0;
        try (FASTQReader fr = new FASTQReader(f.getAbsolutePath(), false)) {
            while (fr.hasMoreElements()) {
                fr.nextElement();
                seqCnt++;
            }
        } catch (SeqStoreException ex) {
            fail(ex.getMessage());
        }
        f.delete();

        assertEquals(1, seqCnt);
    }

//    @Test
//    public void testFile() throws Exception {
//        System.out.println("testFile");
//        File f = new File("/home/sj/all.fq");
//        int seqCnt = 0;
//        DNAQualitySequenceI seq = null;
//        try (FASTQReader fr = new FASTQReader(f.getAbsolutePath(), false)) {
//            while (fr.hasMoreElements()) {
//                seq = fr.nextElement();
//                seqCnt++;
//                        
//            }
//        } catch (SeqStoreException ex) {
//            fail(ex.getMessage()+ " after sequence "+ new String(seq.getName()));
//        }
//        assertEquals(1, seqCnt);
//    }
}
