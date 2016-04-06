/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.restgpms;

import de.cebitec.gpms.core.DataSourceI;
import de.cebitec.gpms.core.GPMSException;
import de.cebitec.gpms.core.MasterI;
import de.cebitec.gpms.core.MembershipI;
import de.cebitec.gpms.core.ProjectClassI;
import de.cebitec.gpms.core.ProjectI;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Assume;
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
//@RunWith(PaxExam.class)
public class GPMSTest {

    @Configuration
    public static Option[] configuration() {
        return options(
                junitBundles(),
                mavenBundle().groupId("com.sun.jersey").artifactId("jersey-client").version("1.18.2"),
                mavenBundle().groupId("com.sun.jersey").artifactId("jersey-core").version("1.18.2"),
                mavenBundle().groupId("com.google.protobuf").artifactId("protobuf-java").version("2.6.1"),
                mavenBundle().groupId("de.cebitec.gpms").artifactId("GPMS-DTO").version("1.1"),
                mavenBundle().groupId("de.cebitec.gpms").artifactId("GPMS-core-api").version("1.1"),
                mavenBundle().groupId("de.cebitec.gpms").artifactId("GPMS-rest-api").version("1.1"),
                mavenBundle().groupId("de.cebitec.gpms").artifactId("GPMS-model").version("1.1"),
                mavenBundle().groupId("de.cebitec.mgx").artifactId("ProtoBuf-Serializer").version("1.0"),
                systemProperty("org.ops4j.pax.logging.DefaultServiceLog.level").value("WARN"),
                bundle("reference:file:target/classes")
        );
    }

    @Test
    public void testGetProjectClasses() {
        System.out.println("getProjectClasses");
        GPMSClient gpms = TestMaster.get();
        try {
            gpms.login("mgx_unittestRO", "gut-isM5iNt");
        } catch (GPMSException ex) {
            fail(ex.getMessage());
        }
        Iterator<ProjectClassI> projectClasses = gpms.getProjectClasses();
        assertNotNull(projectClasses);
        int cnt = 0;
        while (projectClasses.hasNext()) {
            ProjectClassI pc = projectClasses.next();
            assertEquals("MGX", pc.getName());
            assertEquals(3, pc.getRoles().size()); // user, admin, guest
            cnt++;
        }
        assertEquals(1, cnt);
    }

    @Test
    public void testGetMemberships() throws GPMSException {
        System.out.println("getMemberships");
        GPMSClient gpms = TestMaster.get();
        gpms.login("mgx_unittestRO", "gut-isM5iNt");
        Iterator<MembershipI> memberships = gpms.getMemberships();
        assertNotNull(memberships);
        int cnt = 0;
        DataSourceI restDS = null;
        while (memberships.hasNext()) {
            MembershipI m = memberships.next();
            ProjectI project = m.getProject();
            assertNotNull(project);
            assertEquals("MGX_Unittest", project.getName());
            assertNotNull(project.getDataSources());
            assertFalse(project.getDataSources().isEmpty());
            for (DataSourceI rds : project.getDataSources()) {
                restDS = rds;
            }
            cnt++;
        }
        assertEquals(1, cnt);

        assertNotNull(restDS);
        //assertNotEquals("", restDS.getURL().toASCIIString());

    }

    @Test
    public void testGetMembershipsRW() throws GPMSException {
        System.out.println("getMembershipsRW");
        GPMSClient gpms = TestMaster.get();
        gpms.login("mgx_unittestRW", "hL0amo3oLae");
        Iterator<MembershipI> memberships = gpms.getMemberships();
        assertNotNull(memberships);
        int cnt = 0;
        while (memberships.hasNext()) {
            MembershipI m = memberships.next();
            assertNotNull(m.getProject());
            System.err.println("  " + m.getProject().getName());
            assertNotNull(m.getRole());
            cnt++;
        }
        assertEquals(2, cnt);
    }

    @Test
    public void testRESTDataSource() throws GPMSException {
        System.out.println("testRESTDataSource");
        GPMSClient gpms = TestMaster.get();
        gpms.login("mgx_unittestRO", "gut-isM5iNt");
        Iterator<MembershipI> memberships = gpms.getMemberships();
        int cnt = 0;
        MasterI master = null;
        while (memberships.hasNext()) {
            MembershipI m = memberships.next();
            master = gpms.createMaster(m);
            break;
        }
        assertNotNull(master);
    }

    @Test
    public void testLogin() {
        System.out.println("testLogin");
        String login = "mgx_unittestRO";
        String password = "gut-isM5iNt";
        boolean result = false;
        try {
            result = TestMaster.get().login(login, password);
        } catch (GPMSException ex) {
            fail(ex.getMessage());
        }
        assertTrue(result);
    }

    @Test
    public void testLoginPropertyChange() {
        System.out.println("testLoginPropertyChange");
        String login = "mgx_unittestRO";
        String password = "gut-isM5iNt";
        boolean result = false;
        final GPMSClient cli = TestMaster.get();
        try {
            result = cli.login(login, password);
        } catch (GPMSException ex) {
            fail(ex.getMessage());
        }
        assertTrue(result);
        cli.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                assertFalse(cli.loggedIn());
            }
        });
        cli.logout();
        assertFalse(cli.loggedIn());
    }

    @Test
    public void testLoginGPMSInternal() {
        System.out.println("testLoginGPMSInternal");

        String login = null;
        String password = null;

        String config = System.getProperty("user.home") + "/.m2/mgx.junit";
        File f = new File(config);
        Assume.assumeTrue(f.exists() && f.canRead());
        Properties p = new Properties();
        try {
            p.load(new FileInputStream(f));
            login = p.getProperty("username");
            password = p.getProperty("password");
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        Assume.assumeNotNull(login);
        Assume.assumeNotNull(password);
        System.out.println("  using credentials for login " + login);
        boolean result = false;
        try {
            result = TestMaster.get().login(login, password);
        } catch (GPMSException ex) {
            fail(ex.getMessage());
        }
        assertTrue(result);
    }

    @Test
    public void testLoginTwice() {
        System.out.println("testLoginTwice");
        String login = "mgx_unittestRO";
        String password = "gut-isM5iNt";
        GPMSClient gpms = TestMaster.get();
        assertNotNull(gpms);
        boolean result = false;
        try {
            result = gpms.login(login, password);
        } catch (GPMSException ex) {
            fail(ex.getMessage());
        }
        assertTrue(result);
        gpms.logout();
        try {
            result = gpms.login(login, password);
        } catch (GPMSException ex) {
            fail(ex.getMessage());
        }
        assertTrue(result);
    }

    @Test
    public void testInvalidLogin() {
        System.out.println("testInvalidLogin");
        String login = "WRONG";
        String password = "WRONG";
        GPMSClient gpms = TestMaster.get();
        assertNotNull(gpms);
        gpms.logout();
        boolean result = false;
        try {
            result = gpms.login(login, password);
        } catch (GPMSException ex) {
            if (ex.getMessage().contains("Wrong username/passw")) {
            } else {
                fail(ex.getMessage());
            }
        }
    }

    @Test
    public void testInvalidLogin2() {
        System.out.println("testInvalidLogin2");
        GPMSClient gpms = TestMaster.get();
        assertNotNull(gpms);

        // call login() with wrong credentials on an instance that is already
        // logged in successfully
        String login = "WRONG";
        String password = "WRONG";
        boolean result = false;
        try {
            result = gpms.login(login, password);
        } catch (GPMSException ex) {
            if (ex.getMessage().contains("Wrong username/passw")) {
            } else {
                fail(ex.getMessage());
            }
        }
    }

    @Test
    public void testPing() {
        System.out.println("ping");
        GPMSClient gpms = TestMaster.get();
        try {
            gpms.login("mgx_unittestRO", "gut-isM5iNt");
        } catch (GPMSException ex) {
            fail(ex.getMessage());
        }
        long result = gpms.ping();
        assertTrue(result > 100000);
        gpms.logout();
        result = gpms.ping();
    }

    @Test
    public void testCreateMaster() throws GPMSException {
        System.out.println("createMaster");
        GPMSClient gpms = TestMaster.get();
        gpms.login("mgx_unittestRO", "gut-isM5iNt");
        Iterator<MembershipI> memberships = gpms.getMemberships();
        assertNotNull(memberships);
        int cnt = 0;
        String projNames = "";
        while (memberships.hasNext()) {
            MembershipI m = memberships.next();
            ProjectI project = m.getProject();
            assertNotNull(project);
            projNames += project.getName() + ", ";
            MasterI result = gpms.createMaster(m);
            assertNotNull(result);
            cnt++;
        }
        if (projNames.endsWith(", ")) {
            projNames = projNames.substring(0, projNames.length() - 2);
        }
        assertEquals("mgx_unittestRO should only be a member of \'MGX_Unittest\', actual project list: " + projNames, 1, cnt);
    }

//    @Test
//    public void testGetMembershipsPrivate() throws GPMSException {
//        System.out.println("testGetMembershipsPrivate");
//
//        String login = null;
//        String password = null;
//
//        String config = System.getProperty("user.home") + "/.m2/mgx.junit";
//        File f = new File(config);
//        Assume.assumeTrue(f.exists() && f.canRead());
//        Properties p = new Properties();
//        try {
//            p.load(new FileInputStream(f));
//            login = p.getProperty("username");
//            password = p.getProperty("password");
//        } catch (IOException ex) {
//            System.out.println(ex.getMessage());
//        }
//        Assume.assumeNotNull(login);
//        Assume.assumeNotNull(password);
//        System.out.println("  using credentials for login " + login);
//
//        GPMSClient gpms = TestMaster.get();
//        gpms.login(login, password);
//        Iterator<MembershipI> memberships = gpms.getMemberships();
//        assertNotNull(memberships);
//        int cnt = 0;
//        while (memberships.hasNext()) {
//            MembershipI m = memberships.next();
//            ProjectI project = m.getProject();
//            System.err.println("     got project " + project.getName());
//            cnt++;
//        }
//        assertTrue(cnt > 0);
//    }
//    @Test
//    public void testGetError() {
//        System.out.println("getError");
//        String result = TestMaster.get().getError();
//        assertNull(result);
//    }
}
