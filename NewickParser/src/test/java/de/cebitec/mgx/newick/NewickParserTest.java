package de.cebitec.mgx.newick;

import java.io.InputStream;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests
 */
public class NewickParserTest {

    public NewickParserTest() {
    }

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
}
