package de.cebitec.mgx.seqstorage;

import de.cebitec.mgx.seqholder.DNAQualitySequenceHolder;
import de.cebitec.mgx.sequence.SeqStoreException;
import java.io.*;
import org.junit.*;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

/**
 *
 * @author patrick
 */
public class SFFReaderTest {
    
    private File f;
    
    public SFFReaderTest() {
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
    public void testOneReadSFF() {
        System.out.println("OneReadSFF");
        f = copyTestData("de/cebitec/mgx/seqstorage/oneread.sff");
        try (SFFReader sr = new SFFReader(f.getAbsolutePath())) {
            while (sr.hasMoreElements()) {
                DNAQualitySequenceHolder entry = sr.nextElement();
                Assert.assertArrayEquals("EM7RWTF01EXJBZ".getBytes(), entry.getSequence().getName());
                Assert.assertArrayEquals("GATCGCGCGCCGAGGCATTCGCCGCCGTACCCTGGCCAACGCTCGAGCCCAGCGGTCAGTCGCGTCGGATGGTCAGACACGACAACGAGGGAGTAGGACGAAGGCAACACGGAGGGGAGTAGG".getBytes(), entry.getSequence().getSequence());
                byte[] quality = "+-,<<717<<70:7/;;=:+-<70;9<;1?>1;:3=:@<100<:3<4><*<3081<:<3<86:9<8:401;59<2<2062<.7@;408?>10)<;<789,?<>903*00<930=<2\"0)<<=7".getBytes();
                for (int i=0; i<quality.length; i++)
                    quality[i]-=33;
                assertArrayEquals(quality, entry.getSequence().getQuality());
            }
        } catch (SeqStoreException | IOException ex) {
            fail(ex.getMessage());
        }
        f.delete();        
    }

    @Test
    public void testMultipleReadSFF() {
        System.out.println("MultipleReadSFF");
        f = copyTestData("de/cebitec/mgx/seqstorage/multipleRead.sff");
        int seqCnt = 0;
        try (SFFReader sr = new SFFReader(f.getAbsolutePath())) {
            while (sr.hasMoreElements()) {
                sr.nextElement();
                seqCnt++;
            }
        } catch (SeqStoreException | IOException ex) {
            fail(ex.getMessage());
        }
        
        f.delete();
        System.out.println(seqCnt);
        assertEquals(3546, seqCnt);
    }

    private File copyTestData(String uri) {
        File f = null;
        try (BufferedInputStream is = new BufferedInputStream(getClass().getClassLoader().getResourceAsStream(uri))) {
            f = File.createTempFile("seq", ".sff");
            try (FileOutputStream fos = new FileOutputStream(f)) {
                byte[] buffer = new byte[1024];
                int bytesRead = is.read(buffer);
                while (bytesRead >= 0) {
                    fos.write(buffer, 0, bytesRead);
                    bytesRead = is.read(buffer);
                }

            }
        } catch (Exception ex) {
            fail(ex.getMessage());
        }
        assertNotNull(f);
        return f;
    }

}

