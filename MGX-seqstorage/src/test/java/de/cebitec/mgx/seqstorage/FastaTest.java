package de.cebitec.mgx.seqstorage;

import de.cebitec.mgx.osgiutils.MGXOptions;
import de.cebitec.mgx.sequence.SeqStoreException;
import de.cebitec.mgx.testutils.TestInput;
import java.io.File;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import static org.ops4j.pax.exam.CoreOptions.bundle;
import static org.ops4j.pax.exam.CoreOptions.junitBundles;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.options;
import static org.ops4j.pax.exam.CoreOptions.systemProperty;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;

/**
 *
 * @author sj
 */
@RunWith(PaxExam.class)
public class FastaTest {

    public FastaTest() {
    }

    @Configuration
    public static Option[] configuration() {
        return options(
                junitBundles(),
                mavenBundle().groupId("de.cebitec.mgx").artifactId("MGX-isequences"),
                mavenBundle().groupId("de.cebitec.mgx").artifactId("Trove-OSGi"),
                mavenBundle().groupId("de.cebitec.mgx").artifactId("MGX-BufferedRandomAccessFile"),
                mavenBundle().groupId("de.cebitec.mgx").artifactId("SFFReader"),
                mavenBundle().groupId("org.apache.commons").artifactId("commons-math3"),
                MGXOptions.serviceLoaderBundles(),
                MGXOptions.testUtils(),
                systemProperty("org.ops4j.pax.logging.DefaultServiceLog.level").value("WARN"),
                bundle("reference:file:target/classes")
        );
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testreadFasta() throws Exception {
        System.out.println("readFasta");
        File f = TestInput.copyTestData(FastaReader.class, "de/cebitec/mgx/seqstorage/test.fas");
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

}
