/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.seqstorage;

import java.io.File;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;

/**
 *
 * @author sjaenick
 */
public class ByteStreamTokenizerTest {

//    @Configuration
//    public static Option[] configuration() {
//        return options(
//                junitBundles(),
//                mavenBundle().groupId("de.cebitec.mgx").artifactId("MGX-isequences").versionAsInProject(),
//                mavenBundle().groupId("de.cebitec.mgx").artifactId("MGX-seqcompression").versionAsInProject(),
//                mavenBundle().groupId("de.cebitec.mgx").artifactId("SFFReader").versionAsInProject(),
//                mavenBundle().groupId("de.cebitec.mgx").artifactId("MGX-BufferedRandomAccessFile").versionAsInProject(),
//                mavenBundle().groupId("de.cebitec.mgx").artifactId("Trove-OSGi").versionAsInProject(),
//                mavenBundle().groupId("org.apache.commons").artifactId("commons-math3").versionAsInProject(),
//                //MGXOptions.serviceLoaderBundles(),
//                MGXOptions.testUtils(),
//                systemProperty("org.ops4j.pax.logging.DefaultServiceLog.level").value("WARN"),
//                bundle("reference:file:target/classes")
//        );
//    }

    @Test
    public void testNoLineBreak() throws Exception {
        System.out.println("testNoLineBreak");
        // file has no line break after last line
        File f = TestInput.copyTestResource(getClass(), "de/cebitec/mgx/seqstorage/incomplete_last_line.fq");
        byte LINEBREAK = '\n';
        ByteStreamTokenizer bst = new ByteStreamTokenizer(f.getAbsolutePath(), false, LINEBREAK, 0);
        int cnt = 0;
        byte[] foo = null;
        while (bst.hasNext()) {
            foo = bst.next();
            assertNotNull(foo);
            cnt++;
        }
        assertEquals(4, cnt);
    }
//  @Test
//    public void testEmptyLines() throws Exception {
//        System.out.println("testEmptyLines");
//        File f = new File("src/test/resources/de/cebitec/mgx/seqstorage/4emptylines.txt");
//        ByteStreamTokenizer bst =  new ByteStreamTokenizer(f.getAbsolutePath(), false, LINEBREAK, 0);
//        List<String> data = new ArrayList<>();
//        byte[] foo = null;
//        while (bst.hasNext()) {
//            foo = bst.next();
//            String s = new String(foo);
//            assertEquals("", s);
//            data.add(s);
//        }
//        bst.close();
//        assertEquals(4, data.size());
//    }
}
