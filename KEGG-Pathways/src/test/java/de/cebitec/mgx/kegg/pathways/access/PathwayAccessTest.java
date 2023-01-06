package de.cebitec.mgx.kegg.pathways.access;

import de.cebitec.mgx.kegg.pathways.KEGGException;
import de.cebitec.mgx.kegg.pathways.KEGGMaster;
import de.cebitec.mgx.kegg.pathways.api.ECNumberI;
import de.cebitec.mgx.kegg.pathways.api.PathwayI;
import de.cebitec.mgx.kegg.pathways.model.ECNumberFactory;
import java.awt.Rectangle;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 *
 * @author sj
 */
public class PathwayAccessTest {

    @TempDir
    Path folder;
    

    @AfterEach
    public void tearDown() {
        if (m != null) {
            try {
                m.close();
            } catch (Exception ex) {
                Logger.getLogger(PathwayAccessTest.class.getName()).log(Level.SEVERE, null, ex);
            }
            m = null;
        }
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
        assertTrue(fetchall.size() > 465);
    }

    @Test
    public void testGetCoords() throws Exception {
        System.out.println("getCoords");
        Set<PathwayI> fetchall = getMaster().Pathways().fetchall();
        assertNotNull(fetchall);
        PathwayI pw = null;
        for (PathwayI p : fetchall) {
            if (p.getMapNumber().equals("map00010")) {
                pw = p;
                break;
            }
        }
        assertNotNull(pw);
        Map<ECNumberI, Collection<Rectangle>> coords = getMaster().Pathways().getCoords(pw);
        assertNotNull(coords);
        assertTrue(coords.size() > 45);
        assertTrue(coords.containsKey(ECNumberFactory.fromString("5.4.2.2")));
    }

    @Test
    public void testGetMatchingPathways_ECNumberI() throws Exception {
        System.out.println("getMatchingPathways_forECNumberI");
        ECNumberI ec = ECNumberFactory.fromString("6.3.1.9");
        Collection<PathwayI> result = getMaster().Pathways().getMatchingPathways(ec);
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
        Collection<PathwayI> result = getMaster().Pathways().getMatchingPathways(qry);
        assertEquals(5, result.size());
        result = getMaster().Pathways().getMatchingPathways(qry);
        assertEquals(5, result.size());
    }

    private KEGGMaster getMaster() {
        if (m != null) {
            return m;
        }
        try {
            m = KEGGMaster.getInstance(folder.toAbsolutePath().toString());
            return m;
        } catch (KEGGException ex) {
            fail();
            return null;
        }
    }
    private KEGGMaster m = null;
}
