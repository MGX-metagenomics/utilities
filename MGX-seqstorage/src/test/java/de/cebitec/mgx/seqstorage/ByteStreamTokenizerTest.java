/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.seqstorage;

import de.cebitec.mgx.osgiutils.MGXOptions;
import de.cebitec.mgx.testutils.TestInput;
import java.io.File;
import org.junit.Test;
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
 * @author sjaenick
 */
@RunWith(PaxExam.class)
public class ByteStreamTokenizerTest {
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
    public void testNoLineBreak() throws Exception {
        System.out.println("testNoLineBreak");
        // file has no line break after last line
        File f = TestInput.copyTestData(ByteStreamTokenizer.class, "de/cebitec/mgx/seqstorage/incomplete_last_line.fq");
        byte LINEBREAK = '\n';
        ByteStreamTokenizer bst = new ByteStreamTokenizer(f.getAbsolutePath(), false, LINEBREAK, 0);
        int cnt=0;
        byte[] foo = null;
        while (bst.hasMoreElements()) {
            foo = bst.nextElement();
            assertNotNull(foo);
            cnt++;
        }
        assertEquals(4, cnt);
    }
    
}
