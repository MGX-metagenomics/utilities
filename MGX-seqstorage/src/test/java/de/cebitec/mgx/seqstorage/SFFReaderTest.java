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
public class SFFReaderTest {

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

    public SFFReaderTest() {
    }

    @Test
    public void testOneReadSFF() {
        System.out.println("OneReadSFF");
        f = copyTestData("de/cebitec/mgx/seqstorage/oneread.sff");
        try (SFFReader sr = new SFFReader(f.getAbsolutePath())) {
            while (sr.hasMoreElements()) {
                DNAQualitySequenceI entry = sr.nextElement();
                Assert.assertArrayEquals("EM7RWTF01EXJBZ".getBytes(), entry.getName());
                Assert.assertArrayEquals("GATCGCGCGCCGAGGCATTCGCCGCCGTACCCTGGCCAACGCTCGAGCCCAGCGGTCAGTCGCGTCGGATGGTCAGACACGACAACGAGGGAGTAGGACGAAGGCAACACGGAGGGGAGTAGG".getBytes(), entry.getSequence());
                byte[] quality = "+-,<<717<<70:7/;;=:+-<70;9<;1?>1;:3=:@<100<:3<4><*<3081<:<3<86:9<8:401;59<2<2062<.7@;408?>10)<;<789,?<>903*00<930=<2\"0)<<=7".getBytes();
                for (int i = 0; i < quality.length; i++) {
                    quality[i] -= 33;
                }
                assertArrayEquals(quality, entry.getQuality());
            }
        } catch (SeqStoreException | IOException ex) {
            fail(ex.getMessage());
        }
        f.delete();
    }

    @Test
    public void testMultipleReadSFF() {
        System.out.println("MultipleReadSFF");
        f = copyTestData("de/cebitec/mgx/seqstorage/multipleRead.sff");
        int seqCnt = 0;
        try (SFFReader sr = new SFFReader(f.getAbsolutePath())) {
            while (sr.hasMoreElements()) {
                sr.nextElement();
                seqCnt++;
            }
        } catch (SeqStoreException | IOException ex) {
            fail(ex.getMessage());
        }

        f.delete();
        System.out.println(seqCnt);
        assertEquals(3546, seqCnt);
    }

    private File copyTestData(String uri) {
        File f = null;
        try (BufferedInputStream is = new BufferedInputStream(SFFReader.class.getClassLoader().getResourceAsStream(uri))) {
            f = File.createTempFile("seq", ".sff");
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
