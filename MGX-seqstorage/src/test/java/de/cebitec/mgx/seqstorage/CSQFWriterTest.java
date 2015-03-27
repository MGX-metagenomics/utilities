package de.cebitec.mgx.seqstorage;

import de.cebitec.mgx.osgiutils.MGXOptions;
import de.cebitec.mgx.sequence.*;
import de.cebitec.mgx.testutils.TestInput;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.junit.*;
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
public class CSQFWriterTest {

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
    public void testCSQFWriter() throws Exception {
        System.out.println("testCSQFWriter");
        File f = TestInput.copyTestData(CSQFWriter.class, "de/cebitec/mgx/seqstorage/sample_1.fq");
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
        File f = TestInput.copyTestData(CSQFWriter.class, "de/cebitec/mgx/seqstorage/sample_1.fq");
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
}
