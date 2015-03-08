/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.api.groups;

import de.cebitec.mgx.osgiutils.MGXOptions;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import static org.ops4j.pax.exam.CoreOptions.bundle;
import static org.ops4j.pax.exam.CoreOptions.junitBundles;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
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
public class FileTypeTest {

    @Configuration
    public static Option[] configuration() {
        return options(
                junitBundles(),
                MGXOptions.serviceLoaderBundles(),
                MGXOptions.gpmsBundles(),
                mavenBundle().groupId("de.cebitec.mgx").artifactId("MGX-BufferedRandomAccessFile"),
                mavenBundle().groupId("de.cebitec.mgx").artifactId("MGX-isequences"),
                mavenBundle().groupId("org.apache.commons").artifactId("commons-math3"),
                mavenBundle().groupId("de.cebitec.mgx").artifactId("Trove-OSGi"),
                url("link:classpath:de.cebitec.mgx.MGX-parallelPropChange.link"),
                systemProperty("org.ops4j.pax.logging.DefaultServiceLog.level").value("WARN"),
                bundle("reference:file:target/classes")
        );
    }

    @Test
    public void testLoadPackage() {
        System.err.println("testLoadPackage");
        FileType ft = FileType.FAS;
        assertNotNull(ft);
    }

}
