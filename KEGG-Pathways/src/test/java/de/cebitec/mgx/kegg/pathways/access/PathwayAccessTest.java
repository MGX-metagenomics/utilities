package de.cebitec.mgx.kegg.pathways.access;

import de.cebitec.mgx.kegg.pathways.KEGGException;
import de.cebitec.mgx.kegg.pathways.KEGGMaster;
import de.cebitec.mgx.kegg.pathways.api.ECNumberI;
import de.cebitec.mgx.kegg.pathways.api.PathwayI;
import de.cebitec.mgx.kegg.pathways.model.ECNumberFactory;
import java.awt.Rectangle;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

/**
 *
 * @author sj
 */
public class PathwayAccessTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testValid() throws Exception {
        System.out.println("testValid");
        getMaster().setValid("FOO");
        boolean valid = getMaster().isValid("FOO");
        assertTrue(valid);
    }

    @Test
    public void testInvalid() throws Exception {
        System.out.println("testInvalid");
        boolean notValid = getMaster().isValid("BAR");
        assertFalse(notValid);
    }

    @Test
    public void testFetchall() throws Exception {
        System.out.println("fetchall");
        Set<PathwayI> fetchall = getMaster().Pathways().fetchall();
        assertEquals(460, fetchall.size());
    }

    @Test
    public void testGetCoords() throws Exception {
        System.out.println("getCoords");
        Set<PathwayI> fetchall = getMaster().Pathways().fetchall();
        assertNotNull(fetchall);
        PathwayI pw = null;
        for (PathwayI p : fetchall) {
            if (p.getMapNum().equals("map00010")) {
                pw = p;
                break;
            }
        }
        assertNotNull(pw);
        Map<ECNumberI, Set<Rectangle>> coords = getMaster().Pathways().getCoords(pw);
        assertNotNull(coords);
        assertEquals(45, coords.size());
        assertTrue(coords.containsKey(ECNumberFactory.fromString("2.7.1.41")));
    }

    @Test
    public void testGetMatchingPathways_ECNumberI() throws Exception {
        System.out.println("getMatchingPathways_forECNumberI");
        ECNumberI ec = ECNumberFactory.fromString("6.3.1.9");
        Set<PathwayI> result = getMaster().Pathways().getMatchingPathways(ec);
        assertEquals(1, result.size());
        //
        ec = ECNumberFactory.fromString("5.5.1.1");
        result = getMaster().Pathways().getMatchingPathways(ec);
        assertEquals(4, result.size());
    }

    @Test
    public void testGetMatchingPathways_Set() throws Exception {
        System.out.println("getMatchingPathways_forSet");
        Set<ECNumberI> qry = new HashSet<>();
        qry.add(ECNumberFactory.fromString("6.3.1.9"));
        qry.add(ECNumberFactory.fromString("5.5.1.1"));
        Set<PathwayI> result = getMaster().Pathways().getMatchingPathways(qry);
        assertEquals(5, result.size());
        result = getMaster().Pathways().getMatchingPathways(qry);
        assertEquals(5, result.size());
    }

    private KEGGMaster getMaster() {
        if (m != null) {
            return m;
        }
        try {
            m = KEGGMaster.getInstance(folder.newFolder().getAbsolutePath());
            return m;
        } catch (IOException | KEGGException ex) {
            fail();
            return null;
        }
    }
    private KEGGMaster m = null;
}
