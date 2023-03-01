/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.seqstorage;

import de.cebitec.mgx.osgiutils.MGXOptions;
import de.cebitec.mgx.sequence.DNAQualitySequenceI;
import de.cebitec.mgx.sequence.DNASequenceI;
import de.cebitec.mgx.sequence.FactoryI;
import de.cebitec.mgx.sequence.SeqReaderI;
import de.cebitec.mgx.sequence.SeqStoreException;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.Test;
import org.ops4j.pax.exam.Configuration;
import static org.ops4j.pax.exam.CoreOptions.bundle;
import static org.ops4j.pax.exam.CoreOptions.junitBundles;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.options;
import static org.ops4j.pax.exam.CoreOptions.systemProperty;
import org.ops4j.pax.exam.Option;

/**
 *
 * @author sjaenick
 */
//@RunWith(PaxExam.class)
public class ReaderFactoryTest {

    @Configuration
    public static Option[] configuration() {
        return options(
                junitBundles(),
                mavenBundle().groupId("de.cebitec.mgx").artifactId("MGX-isequences").versionAsInProject(),
                mavenBundle().groupId("de.cebitec.mgx").artifactId("MGX-seqcompression").versionAsInProject(),
                mavenBundle().groupId("de.cebitec.mgx").artifactId("Trove-OSGi").versionAsInProject(),
                mavenBundle().groupId("de.cebitec.mgx").artifactId("MGX-BufferedRandomAccessFile").versionAsInProject(),
                mavenBundle().groupId("de.cebitec.mgx").artifactId("SFFReader").versionAsInProject(),
                mavenBundle().groupId("org.apache.commons").artifactId("commons-math3").versionAsInProject(),
                //MGXOptions.serviceLoaderBundles(),
                MGXOptions.testUtils(),
                systemProperty("org.ops4j.pax.logging.DefaultServiceLog.level").value("WARN"),
                bundle("reference:file:target/classes")
        );
    }

    @Test
    public void testGetCSQFReader() throws Exception {
        System.out.println("testGetCSQFReader");

        File temp = File.createTempFile("temp", Long.toString(System.nanoTime()));
        temp.delete();
        temp.mkdirs();

        File f1 = copyTestData(FastaReader.class, "de/cebitec/mgx/seqstorage/3", temp, "3");
        File f2 = copyTestData(FastaReader.class, "de/cebitec/mgx/seqstorage/3.csq", temp, "3.csq");

        FactoryI<DNASequenceI> rf = new ReaderFactory();
        SeqReaderI<? extends DNASequenceI> reader = rf.<DNAQualitySequenceI>getReader(f1.getAbsolutePath());
        assertNotNull(reader);
        assertTrue(reader instanceof CSQFReader);

        f1.delete();
        f2.delete();
        temp.delete();
    }

    @Test
    public void testFASTQGZReader() throws Exception {
        System.out.println("testFASTQGZReader");

        File temp = File.createTempFile("temp", Long.toString(System.nanoTime()));
        temp.delete();
        temp.mkdirs();

        File f1 = copyTestData(FastaReader.class, "de/cebitec/mgx/seqstorage/singleread.fq.gz", temp, "foo.fastq.gz");

        FactoryI<DNASequenceI> rf = new ReaderFactory();
        SeqReaderI<? extends DNASequenceI> reader = rf.<DNAQualitySequenceI>getReader(f1.getAbsolutePath());
        assertNotNull(reader);
        assertTrue(reader instanceof FASTQReader);

        assertTrue(reader.hasMoreElements());
        assertTrue(reader.hasQuality());
        DNASequenceI seq = reader.nextElement();
        assertNotNull(seq);
        assertNotNull(seq.getSequence());
        String nucl = new String(seq.getSequence());
        assertEquals("AAAAA", nucl);

        f1.delete();
        temp.delete();
    }

    @Test
    public void testSFFGZReader() throws Exception {
        System.out.println("testSFFGZReader");

        File temp = File.createTempFile("temp", Long.toString(System.nanoTime()));
        temp.delete();
        temp.mkdirs();

        File f1 = copyTestData(FastaReader.class, "de/cebitec/mgx/seqstorage/multipleRead.sff.gz", temp, "foo.sff.gz");

        FactoryI<DNASequenceI> rf = new ReaderFactory();
        SeqReaderI<? extends DNASequenceI> reader = null;
        try {
            reader = rf.<DNAQualitySequenceI>getReader(f1.getAbsolutePath());
        } catch (SeqStoreException ex) {
            if (!ex.getMessage().contains("Compressed SFF is not supported")) {
                fail(ex.getMessage());
            }
        }
        
        assertNull(reader);

        f1.delete();
        temp.delete();
    }

    private static File copyTestData(Class<?> clazz, String uri, File targetDir, String targetName) throws Exception {
        File f = new File(targetDir.getAbsolutePath() + File.separator + targetName);
        try (BufferedInputStream is = new BufferedInputStream(clazz.getClassLoader().getResourceAsStream(uri))) {
            f.deleteOnExit();
            try (FileOutputStream fos = new FileOutputStream(f)) {
                byte[] buffer = new byte[1024];
                int bytesRead = is.read(buffer);
                while (bytesRead >= 0) {
                    fos.write(buffer, 0, bytesRead);
                    bytesRead = is.read(buffer);
                }
            }
        }
        return f;
    }
}
