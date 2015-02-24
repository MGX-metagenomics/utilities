package de.cebitec.mgx.seqstorage;

import de.cebitec.mgx.sequence.*;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author sj
 */
public class CSQFWriterTest {

    public CSQFWriterTest() {
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
    public void testCSFWriter() throws Exception {
        System.out.println("testCSQFWriter");
        File f = copyTestData();
        File target = File.createTempFile("testout", "");
        target.delete();
        try (FASTQReader fr = new FASTQReader(f.getAbsolutePath(), false)) {
            try (CSQFWriter csq = new CSQFWriter(target)) {
                while (fr.hasMoreElements()) {
                    DNAQualitySequenceI holder = fr.nextElement();
                    assertNotNull(holder);
                    assertNotNull(holder.getSequence());
                    csq.addSequence(holder);
                }
            }
        } catch (SeqStoreException ex) {
            fail(ex.getMessage());
        }
        f.delete();
    }

    @Test
    public void testCSQFReader() throws Exception {
        System.out.println("testCSQFReader");
        File f = copyTestData();
        File target = File.createTempFile("testout", "");
        target.delete();
        List<DNAQualitySequenceI> writer = new ArrayList<>();
        try (FASTQReader fr = new FASTQReader(f.getAbsolutePath(), false)) {
            try (CSQFWriter csq = new CSQFWriter(target)) {
                while (fr.hasMoreElements()) {
                    DNAQualitySequenceI holder = fr.nextElement();
                    assertNotNull(holder);
                    assertNotNull(holder.getSequence());
                    csq.addSequence(holder);
                    writer.add(holder);
                }
            }
            List<DNAQualitySequenceI> reader = new ArrayList<>();
            try (CSQFReader r = new CSQFReader(target.getAbsolutePath(), false)) {
                int i=0;
                while (r.hasMoreElements()) {
                    DNAQualitySequenceI s = r.nextElement();
                    reader.add(s);
                    assertNotNull(s);
                    String seq = new String(s.getSequence());
                    // all sequences have to be uppercase
                    assertEquals(seq.toUpperCase(), seq);                    
                }
                r.delete();
            }
            for(int i=0; i<writer.size(); i++){
                Assert.assertArrayEquals(writer.get(i).getSequence(), reader.get(i).getSequence());
                Assert.assertArrayEquals(writer.get(i).getQuality(), reader.get(i).getQuality());
            }
            
        } catch (SeqStoreException ex) {
            fail(ex.getMessage());
        }
        f.delete();
    }

    private File copyTestData() {
        File f = null;
        try (BufferedInputStream is = new BufferedInputStream(getClass().getClassLoader().getResourceAsStream("de/cebitec/mgx/seqstorage/sample_1.fq"))) {
            f = File.createTempFile("seq", "fq");
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
