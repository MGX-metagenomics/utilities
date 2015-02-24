/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.restgpms;

import de.cebitec.gpms.core.MembershipI;
import de.cebitec.gpms.core.ProjectClassI;
import de.cebitec.gpms.core.ProjectI;
import de.cebitec.gpms.rest.RESTMasterI;
import de.cebitec.gpms.rest.RESTMembershipI;
import java.util.Iterator;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author sj
 */
public class GPMSTest {

    private GPMS gpms;

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        gpms = TestMaster.get();
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testGetProjectClasses() {
        System.out.println("getProjectClasses");
        Iterator<ProjectClassI> projectClasses = gpms.getProjectClasses();
        assertNotNull(projectClasses);
        int cnt = 0;
        while (projectClasses.hasNext()) {
            ProjectClassI pc = projectClasses.next();
            assertEquals("MGX", pc.getName());
            cnt++;
        }
        assertEquals(1, cnt);
    }

    @Test
    public void testGetMemberships() {
        System.out.println("getMemberships");
        Iterator<RESTMembershipI> memberships = gpms.getMemberships();
        assertNotNull(memberships);
        int cnt = 0;
        while (memberships.hasNext()) {
            MembershipI m = memberships.next();
            ProjectI project = m.getProject();
            assertNotNull(project);
            assertEquals("MGX_Unittest", project.getName());
            cnt++;
        }
        assertEquals(1, cnt);
    }

    @Test
    public void testLogin() {
        System.out.println("login");
        gpms.logout();
        String login = "mgx_unittestRO";
        String password = "gut-isM5iNt";
        boolean result = gpms.login(login, password);
        assertTrue(result);
    }

    @Test
    public void testPing() {
        System.out.println("ping");
        long result = gpms.ping();
        assertTrue(result > 100000);
    }

    @Test
    public void testCreateMaster() {
        System.out.println("createMaster");
        Iterator<RESTMembershipI> memberships = gpms.getMemberships();
        assertNotNull(memberships);
        int cnt = 0;
        while (memberships.hasNext()) {
            RESTMembershipI m = memberships.next();
            ProjectI project = m.getProject();
            assertNotNull(project);
            RESTMasterI result = gpms.createMaster(m);
            assertNotNull(result);
            cnt++;
        }
        assertEquals(1, cnt);
    }

    @Test
    public void testGetError() {
        System.out.println("getError");
        String result = gpms.getError();
        assertNull(result);
    }
}
