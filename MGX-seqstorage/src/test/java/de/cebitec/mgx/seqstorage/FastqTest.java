package de.cebitec.mgx.seqstorage;

import de.cebitec.mgx.osgiutils.MGXOptions;
import de.cebitec.mgx.sequence.DNAQualitySequenceI;
import de.cebitec.mgx.sequence.SeqStoreException;
import de.cebitec.mgx.testutils.TestInput;
import java.io.*;
import org.junit.*;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
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
 * @author patrick
 */
@RunWith(PaxExam.class)
public class FastqTest {

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
                MGXOptions.testUtils(),
                systemProperty("org.ops4j.pax.logging.DefaultServiceLog.level").value("WARN"),
                bundle("reference:file:target/classes")
        );
    }

    @Test
    public void testReadFastq() throws Exception {
        System.out.println("readFastq");
        File f = TestInput.copyTestData(FASTQReader.class, "de/cebitec/mgx/seqstorage/sample_1.fq");
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
    public void testFirstReadInFastq() throws Exception {
        System.out.println("FirstReadInFastq");
        File f = TestInput.copyTestData(FASTQReader.class, "de/cebitec/mgx/seqstorage/sample_1.fq");
        try (FASTQReader fr = new FASTQReader(f.getAbsolutePath(), false)) {
            fr.hasMoreElements();
            DNAQualitySequenceI entry = fr.nextElement();
            Assert.assertArrayEquals("IRIS:7:1:17:394#0/1".getBytes(), entry.getName());
            Assert.assertArrayEquals("GTCAGGACAAGAAAGACAANTCCAATTNACATTATG".getBytes(), entry.getSequence());
            byte[] quality = "aaabaa`]baaaaa_aab]D^^`b`aYDW]abaa`^".getBytes();
            for (int i = 0; i < quality.length; i++) {
                quality[i] -= 33;
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
        File f = TestInput.copyTestData(FASTQReader.class, "de/cebitec/mgx/seqstorage/incomplete_last_line.fq");
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
        File f = TestInput.copyTestData(FASTQReader.class, "de/cebitec/mgx/seqstorage/broken_seq.fq");
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
}
