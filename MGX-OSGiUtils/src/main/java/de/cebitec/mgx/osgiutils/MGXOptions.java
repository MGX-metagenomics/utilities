/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.osgiutils;

import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import org.ops4j.pax.exam.options.CompositeOption;
import org.ops4j.pax.exam.options.DefaultCompositeOption;

/**
 *
 * Utility class with predefined aggregations for PAX Exam-based
 * Unit/integration testing
 *
 * @author sj
 */
public class MGXOptions {

    public static CompositeOption clientBundles() {
        return new DefaultCompositeOption(
                testUtils(),
                gpmsBundles(),
                seqIOBundles(),
                serviceLoaderBundles(),
                mavenBundle().groupId("de.cebitec.mgx").artifactId("Trove-OSGi").version("1.0"),
                mavenBundle().groupId("de.cebitec.mgx").artifactId("BioJava-OSGi").version("1.0"),
                mavenBundle().groupId("de.cebitec.mgx").artifactId("MGX-DTO")
        );
    }

    public static CompositeOption testUtils() {
        return new DefaultCompositeOption(
                mavenBundle().groupId("de.cebitec.mgx").artifactId("MGX-TestUtils")
        );
    }

    public static CompositeOption gpmsBundles() {
        return new DefaultCompositeOption(
                // 1.19.x will additionally need javax.ws.rs:jsr311-api:1.1.1
                mavenBundle().groupId("com.sun.jersey").artifactId("jersey-client").version("1.18.2"),
                mavenBundle().groupId("com.sun.jersey").artifactId("jersey-core").version("1.18.2"),
                mavenBundle().groupId("com.google.protobuf").artifactId("protobuf-java").version("2.5.0"),
                mavenBundle().groupId("de.cebitec.gpms").artifactId("GPMS-DTO"),
                mavenBundle().groupId("de.cebitec.gpms").artifactId("GPMS-core-api"),
                mavenBundle().groupId("de.cebitec.gpms").artifactId("GPMS-rest-api"),
                mavenBundle().groupId("de.cebitec.mgx").artifactId("MGX-restgpms"),
                mavenBundle().groupId("de.cebitec.mgx").artifactId("ProtoBuf-Serializer").version("1.0")
        );
    }

    public static CompositeOption seqIOBundles() {
        return new DefaultCompositeOption(
                mavenBundle().groupId("de.cebitec.mgx").artifactId("MGX-BufferedRandomAccessFile").version("1.0"),
                mavenBundle().groupId("de.cebitec.mgx").artifactId("MGX-isequences").version("1.0"),
                mavenBundle().groupId("de.cebitec.mgx").artifactId("MGX-seqstorage").version("1.0-SNAPSHOT"),
                mavenBundle().groupId("de.cebitec.mgx").artifactId("SFFReader").version("1.0"),
                mavenBundle().groupId("de.cebitec.mgx").artifactId("Trove-OSGi").version("1.0"),
                mavenBundle().groupId("org.apache.commons").artifactId("commons-math3")
        );
    }

    public static CompositeOption serviceLoaderBundles() {
        return new DefaultCompositeOption(
                mavenBundle().groupId("org.apache.aries.spifly").artifactId("org.apache.aries.spifly.dynamic.bundle").version("1.0.1"),
                mavenBundle().groupId("org.apache.aries").artifactId("org.apache.aries.util").version("1.0.0"),
                mavenBundle().groupId("org.ow2.asm").artifactId("asm-debug-all").version("5.0.3"));
    }

}
