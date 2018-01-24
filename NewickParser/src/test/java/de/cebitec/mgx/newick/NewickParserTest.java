package de.cebitec.mgx.newick;

import java.io.InputStream;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests
 */
public class NewickParserTest {

    @Test
    public void testparseHClustNewickNoDist() {
        System.out.println("parseHClustNewickNoDist");
        String data = "(grp1,grp2);";
        NodeI root = null;
        try {
            root = NewickParser.parse(data);
        } catch (ParserException ex) {
            fail(ex.getMessage());
        }
        assertNotNull(root);
        assertEquals(2, root.getChildren().size());
    }

    @Test
    public void testparseHClustNewickIntegerDist() {
        System.out.println("parseHClustNewickIntegerDist");
        String data = "(grp1:25,grp2:22);";
        NodeI root = null;
        try {
            root = NewickParser.parse(data);
        } catch (ParserException ex) {
            fail(ex.getMessage());
        }
        assertNotNull(root);
        assertEquals(2, root.getChildren().size());
    }

    @Test
    public void testparseHClustNewickIntegerDistWhiteSpace() {
        System.out.println("parseHClustNewickIntegerDistWhiteSpace");
        String data = "(grp 1:25,grp 2:22);";
        NodeI root = null;
        try {
            root = NewickParser.parse(data);
        } catch (ParserException ex) {
            fail(ex.getMessage());
        }
        assertNotNull(root);
        assertEquals(2, root.getChildren().size());
    }

    @Test
    public void testparseHClustNewickDoubleDist() {
        System.out.println("parseHClustNewickDoubleDist");
        String data = "(grp1:5.74456264653803,grp2:5.74456264653803);";
        NodeI root = null;
        try {
            root = NewickParser.parse(data);
        } catch (ParserException ex) {
            fail(ex.getMessage());
        }
        assertNotNull(root);
        assertEquals(2, root.getChildren().size());
    }

    @Test
    public void testparseNCBITaxNames() {
        System.out.println("testparseNCBITaxNames");
        InputStream is = getClass().getClassLoader().getResourceAsStream("de/cebitec/mgx/newick/ncbitax.nwk");
        assertNotNull(is);
        NodeI root = null;
        try {
            root = NewickParser.parse(is);
        } catch (ParserException ex) {
            fail(ex.getMessage());
        }
        assertNotNull(root);
        assertEquals(5, root.getChildren().size());
        assertEquals(817155, root.getLeaves().size());
    }

    @Test
    public void testparseNCBITaxIDs() {
        System.out.println("testparseNCBITaxIDs");
        InputStream is = getClass().getClassLoader().getResourceAsStream("de/cebitec/mgx/newick/ncbitaxids.nwk");
        assertNotNull(is);
        NodeI root = null;
        try {
            root = NewickParser.parse(is);
        } catch (ParserException ex) {
            fail(ex.getMessage());
        }
        assertNotNull(root);
        assertEquals(5, root.getChildren().size());
        assertEquals(817155, root.getLeaves().size());
    }

    @Test
    public void testRegression() {
        System.out.println("testRegression");
        String nwk = "(Group2:13.1,(Group3:0,Group1:0):13.2);";
        NodeI root = null;
        try {
            root = NewickParser.parse(nwk);
        } catch (ParserException ex) {
            fail(ex.getMessage());
        }
        assertNotNull(root);
        assertEquals(2, root.getChildren().size());
        //
        NodeI n = root.getChildren().get(0);
        assertEquals("Group2", n.getName());
        assertEquals(13.1, n.getWeight(), 0.0);
        //
        n = root.getChildren().get(1);
        assertEquals("", n.getName());
        assertEquals(13.2, n.getWeight(), 0.0);
        assertEquals(3, root.getLeaves().size());
    }

    @Test
    public void testLeafDistance() {
        System.out.println("testLeafDistance");
        String nwk
                = "((((ML19-1-apr:0.728096294704428,ML20-1-apr:0.728096294704428):0,(ML18-1-apr:0.109828616251945,(ML01-1-apr:0.469153320423688,(ML02-1-apr:1.1254975425337,ML17-1-apr:1.1254975425337):0.469153320423688):0.109828616251945):0):0,((ML19-0-apr:3.8394621168471,ML29-1-apr:3.8394621168471):0,(ML20-0-apr:1.53292509177335,(ML24-0-apr:0.196835281770088,((ML03-0-apr:1.25469139265139,ML18-0-apr:1.25469139265139):0,(ML02-0-apr:0.153782278965603,(ML01-0-apr:0.499866929884893,(ML08-1-apr:1.07890750194934,ML04-0-apr:1.07890750194934):0.499866929884893):0.153782278965603):0):0.196835281770088):1.53292509177335):0):0):0,(((((ML09-1-apr:1.34076568621791,(ML07-0-apr:1.16865557875768,(ML06-0-apr:3.2045199120432,ML05-1-apr:3.2045199120432):1.16865557875768):1.34076568621791):0,(((ML05-0-apr:3.06171692356428,ML12-1-apr:3.06171692356428):0,((ML28-1-apr:0.21756267051392,((ML11-1-apr:1.72235253478789,ML22-0_apr:1.72235253478789):0,(ML26-1-apr:1.9457191878341,ML27-1-apr:1.9457191878341):0):0.21756267051392):0,(ML06-1-apr:0.908581281718701,(ML30-1-apr:1.99083484132707,ML04-1-apr:1.99083484132707):0.908581281718701):0):0):0,(ML03-1-apr:0.251567730013335,(ML23-1-apr:5.34809870517008,ML21-1-apr:5.34809870517008):0.251567730013335):0):0):0,((ML08-0-apr:2.65691250101799,ML24-1-apr:2.65691250101799):0,(ML25-1-apr:1.28244970725926,(ML10-1-apr:3.04588370938805,ML22-1_apr:3.04588370938805):1.28244970725926):0):0):0,((ML31-1-apr:4.88183288598203,ML32-1-apr:4.88183288598203):0,(ML16-1-apr:6.20616573758076,ML17-0-apr:6.20616573758076):0):0):0,(ML14-1-apr:0.811274241464437,(ML13-1-apr:1.02137701891036,((ML07-1-apr:7.3694483983459,ML23-0-apr:7.3694483983459):0,(ML21-0-apr:8.1467353148675,ML15-1-apr:8.1467353148675):0):1.02137701891036):0.811274241464437):0):0);";
        NodeI root = null;
        try {
            root = NewickParser.parse(nwk);
        } catch (ParserException ex) {
            fail(ex.getMessage());
        }
        assertNotNull(root);
        NodeI ml03 = findChildByName(root, "ML03-1-apr");
        assertNotNull(ml03);
        assertEquals(0.251567730013335, ml03.getWeight(), 0.00000000001);
    }

    @Test
    public void testInnerNodeDistance() {
        System.out.println("testInnerNodeDistance");
        String nwk
                = "((((ML19-1-apr:0.728096294704428,ML20-1-apr:0.728096294704428):0,(ML18-1-apr:0.109828616251945,(ML01-1-apr:0.469153320423688,(ML02-1-apr:1.1254975425337,ML17-1-apr:1.1254975425337):0.469153320423688):0.109828616251945):0):0,((ML19-0-apr:3.8394621168471,ML29-1-apr:3.8394621168471):0,(ML20-0-apr:1.53292509177335,(ML24-0-apr:0.196835281770088,((ML03-0-apr:1.25469139265139,ML18-0-apr:1.25469139265139):0,(ML02-0-apr:0.153782278965603,(ML01-0-apr:0.499866929884893,(ML08-1-apr:1.07890750194934,ML04-0-apr:1.07890750194934):0.499866929884893):0.153782278965603):0):0.196835281770088):1.53292509177335):0):0):0,(((((ML09-1-apr:1.34076568621791,(ML07-0-apr:1.16865557875768,(ML06-0-apr:3.2045199120432,ML05-1-apr:3.2045199120432):1.16865557875768):1.34076568621791):0,(((ML05-0-apr:3.06171692356428,ML12-1-apr:3.06171692356428):0,((ML28-1-apr:0.21756267051392,((ML11-1-apr:1.72235253478789,ML22-0_apr:1.72235253478789):0,(ML26-1-apr:1.9457191878341,ML27-1-apr:1.9457191878341):0):0.21756267051392):0,(ML06-1-apr:0.908581281718701,(ML30-1-apr:1.99083484132707,ML04-1-apr:1.99083484132707):0.908581281718701):0):0):0,(ML03-1-apr:0.251567730013335,(ML23-1-apr:5.34809870517008,ML21-1-apr:5.34809870517008):0.251567730013335):0):0):0,((ML08-0-apr:2.65691250101799,ML24-1-apr:2.65691250101799):0,(ML25-1-apr:1.28244970725926,(ML10-1-apr:3.04588370938805,ML22-1_apr:3.04588370938805):1.28244970725926):0):0):0,((ML31-1-apr:4.88183288598203,ML32-1-apr:4.88183288598203):0,(ML16-1-apr:6.20616573758076,ML17-0-apr:6.20616573758076):0):0):0,(ML14-1-apr:0.811274241464437,(ML13-1-apr:1.02137701891036,((ML07-1-apr:7.3694483983459,ML23-0-apr:7.3694483983459):0,(ML21-0-apr:8.1467353148675,ML15-1-apr:8.1467353148675):0):1.02137701891036):0.811274241464437):0):0);";
        NodeI root = null;
        try {
            root = NewickParser.parse(nwk);
        } catch (ParserException ex) {
            fail(ex.getMessage());
        }
        assertNotNull(root);
        NodeI ml03p = findParentOfChild(root, "ML03-1-apr");
        assertNotNull(ml03p);
        assertEquals(0, ml03p.getWeight(), 0.00000000001);
        
        NodeI ml23p= findParentOfChild(root, "ML23-1-apr");
        assertNotNull(ml23p);
        assertEquals(0.251567730013335, ml23p.getWeight(), 0.00000000001);
    }

    private NodeI findParentOfChild(NodeI p, String name) {
        if (p.isLeaf()) {
            return null;
        }
        for (NodeI n : p.getChildren()) {
            if (hasChildWithName(n, name)) {
                return n;
            }
            NodeI x = findParentOfChild(n, name);
            if (x != null) {
                return x;
            }
        }
        return null;
    }

    private boolean hasChildWithName(NodeI n, String name) {
        if (!n.isLeaf()) {
            for (NodeI c : n.getChildren()) {
                if (c.getName().equals(name)) {
                    return true;
                }
            }
        }
        return false;
    }

    private NodeI findChildByName(NodeI p, String name) {
        if (p.getName().equals(name)) {
            return p;
        }
        if (!p.isLeaf()) {
            for (NodeI n : p.getChildren()) {
                NodeI c = findChildByName(n, name);
                if (c != null) {
                    return c;
                }
            }
        }
        return null;
    }
}
