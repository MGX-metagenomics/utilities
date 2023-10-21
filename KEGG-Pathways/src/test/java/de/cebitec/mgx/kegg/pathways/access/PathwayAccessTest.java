package de.cebitec.mgx.kegg.pathways.access;

import de.cebitec.mgx.kegg.pathways.KEGGException;
import de.cebitec.mgx.kegg.pathways.KEGGMaster;
import de.cebitec.mgx.kegg.pathways.api.ECNumberI;
import de.cebitec.mgx.kegg.pathways.api.PathwayI;
import de.cebitec.mgx.kegg.pathways.model.ECNumberFactory;
import de.cebitec.mgx.kegg.pathways.model.Pathway;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.Test;

/**
 *
 * @author sj
 */
public class PathwayAccessTest {

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
    public void testFetchall() throws Exception {
        System.out.println("fetchall");
        Set<PathwayI> fetchall = getMaster().Pathways().fetchall();
        assertTrue(fetchall.size() >= 568, "Number of pathways should be at least 568");
    }

    @Test
    public void testGetCoords() throws Exception {
        System.out.println("getCoords");
        PathwayI pw = new Pathway("map00010", "xxxx");
        Map<ECNumberI, Collection<Rectangle>> coords = getMaster().Pathways().getCoords(pw);
        assertNotNull(coords);
        assertEquals(50, coords.size());
        assertTrue(coords.containsKey(ECNumberFactory.fromString("5.4.2.2")));
    }

    @Test
    public void testGetImage() throws Exception {
        System.out.println("getImage");
        PathwayI pw = new Pathway("map00010", "xxxx");
        BufferedImage image = getMaster().Pathways().getImage(pw);
        assertNotNull(image);
        assertEquals(1029, image.getHeight());
        assertEquals(720, image.getWidth());
    }

    @Test
    public void testGetMatchingPathways_ECNumberI() throws Exception {
        System.out.println("getMatchingPathways_forECNumberI");
        ECNumberI ec = ECNumberFactory.fromString("6.3.1.9");
        Set<PathwayI> result = getMaster().Pathways().getMatchingPathways(ec);
        assertNotNull(result);
        assertEquals(1, result.size());
        //
        for (PathwayI p : result) {
            // there is only one
            assertEquals("map00480", p.getMapNumber());
            assertEquals("Glutathione metabolism", p.getName());
        }
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
            m = KEGGMaster.getInstance("/tmp/kegg/");
            return m;
        } catch (KEGGException ex) {
            fail();
            return null;
        }
    }
    private KEGGMaster m = null;
}
