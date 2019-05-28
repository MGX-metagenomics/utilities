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
                mavenBundle().groupId("de.cebitec.mgx").artifactId("MGX-parallelPropChange").version("2.0"),
                mavenBundle().groupId("de.cebitec.mgx").artifactId("Trove-OSGi").version("2.0"),
                mavenBundle().groupId("de.cebitec.mgx").artifactId("BioJava-OSGi").version("2.0"),
                mavenBundle().groupId("de.cebitec.mgx").artifactId("MGX-DTO").version("2.0")
        );
    }

    public static CompositeOption testUtils() {
        return new DefaultCompositeOption(
                mavenBundle().groupId("de.cebitec.mgx").artifactId("MGX-TestUtils").version("2.0")
        );
    }

    public static CompositeOption gpmsBundles() {
        return new DefaultCompositeOption(
                // 1.19.x will additionally need javax.ws.rs:jsr311-api:1.1.1
                mavenBundle().groupId("javax.annotation").artifactId("javax.annotation-api").version("1.3.2"),
                mavenBundle().groupId("org.glassfish.jersey.core").artifactId("jersey-client").version("2.28"),
                mavenBundle().groupId("org.glassfish.jersey.core").artifactId("jersey-common").version("2.28"),
                mavenBundle().groupId("org.glassfish.jersey.inject").artifactId("jersey-hk2").version("2.28"),
                mavenBundle().groupId("org.glassfish.hk2").artifactId("hk2-api").version("2.5.0"),
                mavenBundle().groupId("org.glassfish.hk2").artifactId("hk2-utils").version("2.5.0"),
                mavenBundle().groupId("org.glassfish.hk2").artifactId("hk2-locator").version("2.5.0"),
                mavenBundle().groupId("org.glassfish.hk2").artifactId("osgi-resource-locator").version("1.0.1"),
                mavenBundle().groupId("org.glassfish.hk2.external").artifactId("aopalliance-repackaged").version("2.5.0"),
                mavenBundle().groupId("org.javassist").artifactId("javassist").version("3.22.0-CR2"),
                mavenBundle().groupId("jakarta.ws.rs").artifactId("jakarta.ws.rs-api").version("2.1.5"),
                //mavenBundle().groupId("jakarta.annotation").artifactId("jakarta.annotation-api").version("1.3.4"),
                //mavenBundle().groupId("org.glassfish.jersey.connectors").artifactId("jersey-grizzly-connector").version("2.28"),
                mavenBundle().groupId("com.google.protobuf").artifactId("protobuf-java").version("3.7.1"),
                mavenBundle().groupId("com.google.guava").artifactId("failureaccess").version("1.0.1"),
                mavenBundle().groupId("com.google.guava").artifactId("guava").version("27.1-jre"),
                mavenBundle().groupId("de.cebitec.gpms").artifactId("GPMS-DTO").version("2.0"),
                mavenBundle().groupId("de.cebitec.gpms").artifactId("GPMS-core-api").version("2.0"),
                mavenBundle().groupId("de.cebitec.gpms").artifactId("GPMS-rest-api").version("2.0"),
                mavenBundle().groupId("de.cebitec.gpms").artifactId("GPMS-model").version("2.0"),
                mavenBundle().groupId("de.cebitec.mgx").artifactId("MGX-restgpms").version("2.0"),
                mavenBundle().groupId("de.cebitec.mgx").artifactId("ProtoBuf-Serializer").version("2.0")
        );
    }

    public static CompositeOption seqIOBundles() {
        return new DefaultCompositeOption(
                mavenBundle().groupId("de.cebitec.mgx").artifactId("MGX-BufferedRandomAccessFile").version("2.0"),
                mavenBundle().groupId("de.cebitec.mgx").artifactId("MGX-isequences").version("2.0"),
                mavenBundle().groupId("de.cebitec.mgx").artifactId("MGX-seqstorage").version("2.0"),
                mavenBundle().groupId("de.cebitec.mgx").artifactId("SFFReader").version("2.0"),
                mavenBundle().groupId("de.cebitec.mgx").artifactId("Trove-OSGi").version("2.0"),
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
