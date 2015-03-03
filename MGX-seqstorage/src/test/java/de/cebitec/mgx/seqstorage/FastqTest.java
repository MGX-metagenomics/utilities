package de.cebitec.mgx.seqstorage;

import de.cebitec.mgx.osgiutils.MGXOptions;
import de.cebitec.mgx.sequence.DNAQualitySequenceI;
import de.cebitec.mgx.sequence.SeqStoreException;
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
                systemProperty("org.ops4j.pax.logging.DefaultServiceLog.level").value("WARN"),
                bundle("reference:file:target/classes")
        );
    }
    private File f;

    @Test
    public void testReadFastq() {
        System.out.println("readFastq");
        f = copyTestData();
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
    public void testFirstReadInFastq() throws SeqStoreException {
        System.out.println("FirstReadInFastq");
        f = copyTestData();
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

    private File copyTestData() {
        File f = null;
        try (BufferedInputStream is = new BufferedInputStream(FASTQReader.class.getClassLoader().getResourceAsStream("de/cebitec/mgx/seqstorage/sample_1.fq"))) {
            f = File.createTempFile("seq", "fq");
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
