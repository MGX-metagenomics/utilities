package de.cebitec.mgx.seqstorage;

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
//@RunWith(PaxExam.class)
public class FastaTest {

    public FastaTest() {
    }

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
    public void testreadFasta() throws Exception {
        System.out.println("readFasta");
        File f = TestInput.copyTestResource(getClass(), "de/cebitec/mgx/seqstorage/test.fas");
        int seqCnt = 0;
        try (FastaReader fr = new FastaReader(f.getAbsolutePath(), false)) {
            while (fr.hasMoreElements()) {
                fr.nextElement();
                seqCnt++;
            }
        } catch (SeqStoreException ex) {
            fail(ex.getMessage());
        }
        f.delete();

        assertEquals(25, seqCnt);
    }

    @Test
    public void testLowerCaseInput() throws Exception {
        System.out.println("testLowerCaseInput");
        File f = TestInput.copyTestResource(getClass(), "de/cebitec/mgx/seqstorage/lowercase.fas");
        try (FastaReader fr = new FastaReader(f.getAbsolutePath(), false)) {
            fr.hasMoreElements();
            DNASequenceI entry = fr.nextElement();
            assertEquals("TCGGT", new String(entry.getSequence()));
        } catch (SeqStoreException ex) {
            fail(ex.getMessage());
        }
        f.delete();
    }

    @Test
    public void testSeqName() throws Exception {
        System.out.println("testSeqName");
        File f = TestInput.copyTestResource(getClass(), "de/cebitec/mgx/seqstorage/lowercase.fas");
        try (FastaReader fr = new FastaReader(f.getAbsolutePath(), false)) {
            fr.hasMoreElements();
            DNASequenceI entry = fr.nextElement();
            assertEquals("MISEQ:99:000000000-A736Y:1:1101:16109:1484 1:N:0:", new String(entry.getName()));
        } catch (SeqStoreException ex) {
            fail(ex.getMessage());
        }
        f.delete();
    }

    @Test
    public void testFastaWithWindowsLinebreaks() throws Exception {
        System.out.println("testFastaWithWindowsLinebreaks");
        File f = TestInput.copyTestResource(getClass(), "de/cebitec/mgx/seqstorage/win32linebreaks.fas");

        DNASequenceI seq1 = null;
        DNASequenceI seq2 = null;

        try (FastaReader fr = new FastaReader(f.getAbsolutePath(), false)) {
            assertTrue(fr.hasMoreElements());
            seq1 = fr.nextElement();
            assertTrue(fr.hasMoreElements());
            seq2 = fr.nextElement();
            assertFalse(fr.hasMoreElements());
        } catch (SeqStoreException ex) {
            fail(ex.getMessage());
        }
        f.delete();

        assertNotNull(seq1);
        assertNotNull(seq2);

        assertEquals("S10_1", new String(seq1.getName()));
        assertEquals("S10_2", new String(seq2.getName()));

    }

    @Test
    public void testEmptySeqs() throws Exception {
        System.out.println("testEmptySeqs");
        File f = TestInput.copyTestResource(getClass(), "de/cebitec/mgx/seqstorage/emptyseqs.fas");
        int seqCnt = 0;
        try (FastaReader fr = new FastaReader(f.getAbsolutePath(), false)) {
            while (fr.hasMoreElements()) {
                DNASequenceI seq = fr.nextElement();
                assertNotNull("Sequence name should not be null", seq.getName());
                assertNotNull("Sequence should not be null", seq.getSequence());
                assertEquals(0, seq.getSequence().length);
                seqCnt++;
            }
        } catch (SeqStoreException ex) {
            fail(ex.getMessage());
        }
        f.delete();

        assertEquals(5, seqCnt);
    }

}
