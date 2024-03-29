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
                //                serviceLoaderBundles(),
                mavenBundle().groupId("de.cebitec.mgx").artifactId("MGX-parallelPropChange").version("2.0"),
                mavenBundle().groupId("de.cebitec.mgx").artifactId("MGX-common").version("2.0"),
                mavenBundle().groupId("de.cebitec.mgx").artifactId("Trove-OSGi").version("2.0"),
                mavenBundle().groupId("de.cebitec.mgx").artifactId("BioJava-OSGi").version("2.0"),
                mavenBundle().groupId("de.cebitec.mgx").artifactId("MGX-DTO").version("2.0")
        );
    }

    public static CompositeOption testUtils() {
        return new DefaultCompositeOption(
                mavenBundle().groupId("de.cebitec.mgx").artifactId("MGX-TestUtils").version("2.0")
                //mavenBundle().groupId("org.slf4j").artifactId("slf4j-api").version("1.7.35"),
                //mavenBundle().groupId("org.slf4j").artifactId("slf4j-simple").version("1.7.35")
        );
    }

    public static CompositeOption gpmsBundles() {
        return new DefaultCompositeOption(
                mavenBundle().groupId("jakarta.annotation").artifactId("jakarta.annotation-api").version("2.0.0"),
                mavenBundle().groupId("jakarta.validation").artifactId("jakarta.validation-api").version("3.0.0"),
                mavenBundle().groupId("jakarta.activation").artifactId("jakarta.activation-api").version("2.0.0"),
                mavenBundle().groupId("jakarta.xml.bind").artifactId("jakarta.xml.bind-api").version("3.0.1"),
                mavenBundle().groupId("jakarta.interceptor").artifactId("jakarta.interceptor-api").version("2.0.1"),
                mavenBundle().groupId("jakarta.inject").artifactId("jakarta.inject-api").version("2.0.1"),
                mavenBundle().groupId("de.cebitec.mgx").artifactId("RESTEasy-OSGi").version("2.0"),
                //mavenBundle().groupId("org.jboss.spec.javax.xml.bind").artifactId("jboss-jaxb-api_2.3_spec").version("1.0.1.Final"),
                mavenBundle().groupId("org.jboss.spec.javax.ws.rs").artifactId("jboss-jaxrs-api_3.0_spec").version("1.0.0.Final"),
                mavenBundle().groupId("jakarta.enterprise").artifactId("jakarta.enterprise.cdi-api").version("3.0.1"),
                mavenBundle().groupId("jakarta.el").artifactId("jakarta.el-api").version("4.0.0"),
                //mavenBundle().groupId("org.apache.geronimo.config").artifactId("geronimo-config-impl").version("1.0"),
                mavenBundle().groupId("org.eclipse.microprofile.config").artifactId("microprofile-config-api").version("3.0.1"),
                mavenBundle().groupId("com.google.protobuf").artifactId("protobuf-java").version("3.19.2"),
                mavenBundle().groupId("com.google.guava").artifactId("failureaccess").version("1.0.1"),
                mavenBundle().groupId("com.google.guava").artifactId("guava").version("31.0.1-jre"),
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
                mavenBundle().groupId("de.cebitec.mgx").artifactId("MGX-seqcompression").version("2.0"),
                mavenBundle().groupId("de.cebitec.mgx").artifactId("MGX-seqstorage").version("2.0"),
                mavenBundle().groupId("de.cebitec.mgx").artifactId("SFFReader").version("2.0"),
                mavenBundle().groupId("de.cebitec.mgx").artifactId("Trove-OSGi").version("2.0"),
                mavenBundle().groupId("org.apache.commons").artifactId("commons-math3")
        );
    }

//    public static CompositeOption serviceLoaderBundles() {
//        return new DefaultCompositeOption(
//                mavenBundle().groupId("org.apache.aries.spifly").artifactId("org.apache.aries.spifly.dynamic.bundle").version("1.0.1"),
//                mavenBundle().groupId("org.apache.aries").artifactId("org.apache.aries.util").version("1.0.0"),
//                mavenBundle().groupId("org.ow2.asm").artifactId("asm-debug-all").version("5.0.3"));
//    }
}
