/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.restgpms;

import de.cebitec.gpms.core.ProjectClassI;
import de.cebitec.gpms.rest.RESTMasterI;
import de.cebitec.gpms.rest.RESTMembershipI;
import de.cebitec.gpms.rest.RESTProjectI;
import java.util.Iterator;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author sj
 */
public class GPMSTest {

    @Test
    public void testGetProjectClasses() {
        System.out.println("getProjectClasses");
        GPMS gpms = TestMaster.get();
        gpms.login("mgx_unittestRO", "gut-isM5iNt");
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
        GPMS gpms = TestMaster.get();
        gpms.login("mgx_unittestRO", "gut-isM5iNt");
        Iterator<RESTMembershipI> memberships = gpms.getMemberships();
        assertNotNull(memberships);
        int cnt = 0;
        while (memberships.hasNext()) {
            RESTMembershipI m = memberships.next();
            RESTProjectI project = m.getProject();
            assertNotNull(project);
            assertEquals("MGX_Unittest", project.getName());
            cnt++;
        }
        assertEquals(1, cnt);
    }

    @Test
    public void testLogin() {
        System.out.println("testLogin");
        String login = "mgx_unittestRO";
        String password = "gut-isM5iNt";
        boolean result = TestMaster.get().login(login, password);
        assertTrue(result);
    }

    @Test
    public void testLoginTwice() {
        System.out.println("testLoginTwice");
        String login = "mgx_unittestRO";
        String password = "gut-isM5iNt";
        GPMS gpms = TestMaster.get();
        assertNotNull(gpms);
        boolean result = gpms.login(login, password);
        assertTrue(result);
        gpms.logout();
        result = gpms.login(login, password);
        assertTrue(result);
    }

    @Test
    public void testInvalidLogin() {
        System.out.println("testInvalidLogin");
        String login = "WRONG";
        String password = "WRONG";
        GPMS gpms = TestMaster.get();
        assertNotNull(gpms);
        gpms.logout();
        boolean result = gpms.login(login, password);
        assertFalse(result);
    }

    @Test
    public void testInvalidLogin2() {
        System.out.println("testInvalidLogin2");
        GPMS gpms = TestMaster.get();
        assertNotNull(gpms);

        // call login() with wrong credentials on an instance that is already
        // logged in successfully
        String login = "WRONG";
        String password = "WRONG";
        boolean result = gpms.login(login, password);
        assertFalse(result);
    }

    @Test
    public void testPing() {
        System.out.println("ping");
        GPMS gpms = TestMaster.get();
        gpms.login("mgx_unittestRO", "gut-isM5iNt");
        long result = gpms.ping();
        assertTrue(result > 100000);
        gpms.logout();
        result = gpms.ping();
    }

    @Test
    public void testCreateMaster() {
        System.out.println("createMaster");
        GPMS gpms = TestMaster.get();
        gpms.login("mgx_unittestRO", "gut-isM5iNt");
        Iterator<RESTMembershipI> memberships = gpms.getMemberships();
        assertNotNull(memberships);
        int cnt = 0;
        while (memberships.hasNext()) {
            RESTMembershipI m = memberships.next();
            RESTProjectI project = m.getProject();
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
        String result = TestMaster.get().getError();
        assertNull(result);
    }
}
