/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.seqstorage;

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
                mavenBundle().groupId("de.cebitec.mgx").artifactId("MGX-BufferedRandomAccessFile").versionAsInProject(),
                mavenBundle().groupId("de.cebitec.mgx").artifactId("SFFReader").versionAsInProject(),
                systemProperty("org.ops4j.pax.logging.DefaultServiceLog.level").value("WARN"),
                bundle("reference:file:target/classes")
        );
    }

    public ReaderFactoryTest() {
    }

//    @Test
//    public void testGetSFFReader() throws Exception {
//        System.out.println("getSFFReader");
//        File sffFile = copyTestData();
//        
//        ReaderFactory rf = new ReaderFactory();
//        
//        SeqReaderI<? extends ReadSequenceI> reader = rf.getReader(sffFile.getAbsolutePath());
//        assertNotNull(reader);
//        assertTrue(reader instanceof SFFReader);
//        int numReads = 0;
//        while (reader.hasMoreElements()) {
//            ReadSequenceI next = reader.nextElement();
//            String sName = new String(next.getSequence().getName());
//            String dna = new String(next.getSequence().getSequence());
//            assertEquals("EM7RWTF01EXJBZ", sName);
//            assertEquals("GATCGCGCGCCGAGGCATTCGCCGCCGTACCCTGGCCAACGCTCGAGCCCAGCGGTCAGTCGCGTCGGATGGTCAGACACGACAACGAGGGAGTAGGACGAAGGCAACACGGAGGGGAGTAGG", dna);
//            numReads++;
//        }
//        assertEquals(1, numReads);
//        sffFile.delete();
//    }
//    private File copyTestData() {
//        File f = null;
//        try (BufferedInputStream is = new BufferedInputStream(getClass().getClassLoader().getResourceAsStream("de/cebitec/mgx/seqstorage/oneread.sff"))) {
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
