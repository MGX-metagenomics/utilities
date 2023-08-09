package de.cebitec.mgx.seqstorage;

import de.cebitec.mgx.seqcompression.SequenceException;
import de.cebitec.mgx.sequence.DNAQualitySequenceI;
import de.cebitec.mgx.sequence.SeqStoreException;
import java.io.*;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.Test;

/**
 *
 * @author patrick
 */
//@RunWith(PaxExam.class)
public class FastqTest {

//    @Configuration
//    public static Option[] configuration() {
//        return options(
//                junitBundles(),
//                mavenBundle().groupId("de.cebitec.mgx").artifactId("MGX-isequences").versionAsInProject(),
//                mavenBundle().groupId("de.cebitec.mgx").artifactId("Trove-OSGi").versionAsInProject(),
//                mavenBundle().groupId("de.cebitec.mgx").artifactId("MGX-BufferedRandomAccessFile").versionAsInProject(),
//                mavenBundle().groupId("de.cebitec.mgx").artifactId("SFFReader").versionAsInProject(),
//                mavenBundle().groupId("org.apache.commons").artifactId("commons-math3").versionAsInProject(),
//                MGXOptions.serviceLoaderBundles(),
//                MGXOptions.testUtils(),
//                systemProperty("org.ops4j.pax.logging.DefaultServiceLog.level").value("WARN"),
//                bundle("reference:file:target/classes")
//        );
//    }
    @Test
    public void testReadFastq() throws Exception {
        System.out.println("readFastq");
        File f = TestInput.copyTestResource(getClass(), "de/cebitec/mgx/seqstorage/sample_1.fq");
        int seqCnt = 0;
        try ( FASTQReader fr = new FASTQReader(f.getAbsolutePath(), false)) {
            while (fr.hasMoreElements()) {
                fr.nextElement();
                seqCnt++;
            }
        } catch (SeqStoreException ex) {
            fail(ex.getMessage());
        }
        f.delete();

        assertEquals(20, seqCnt);
    }

    @Test
    public void testWriteFastq() throws Exception {
        System.out.println("testWriteFastq");
        File f = TestInput.copyTestResource(getClass(), "de/cebitec/mgx/seqstorage/sample_1.fq");
        int seqCnt = 0;
        File target = File.createTempFile("testFQWriter", "xx");
        FASTQWriter fw = new FASTQWriter(target.getAbsolutePath(), QualityEncoding.Illumina5);
        try ( FASTQReader fr = new FASTQReader(f.getAbsolutePath(), false)) {
            while (fr.hasMoreElements()) {
                DNAQualitySequenceI qseq = fr.nextElement();
                fw.addSequence(qseq);
                seqCnt++;
            }
        } catch (SeqStoreException ex) {
            fail(ex.getMessage());
        } finally {
            fw.close();
            //target.delete();
        }
        f.delete();

        assertEquals(20, seqCnt);
    }

    @Test
    public void testReadDOSFastq() throws Exception {
        System.out.println("testReadDOSFastq");
        File f = TestInput.copyTestResource(getClass(), "de/cebitec/mgx/seqstorage/sample_1_dos.fq");
        int seqCnt = 0;
        DNAQualitySequenceI qseq = null;
        try ( FASTQReader fr = new FASTQReader(f.getAbsolutePath(), false)) {
            while (fr.hasMoreElements()) {
                qseq = fr.nextElement();
                assertNotNull(qseq);
                seqCnt++;
            }
        } catch (SeqStoreException ex) {
            fail(ex.getMessage());
        }
        f.delete();

        assertEquals(20, seqCnt);

        assertNotNull(qseq);
    }

    @Test
    public void testFirstReadInFastq() throws Exception {
        System.out.println("FirstReadInFastq");
        File f = TestInput.copyTestResource(getClass(), "de/cebitec/mgx/seqstorage/sample_1.fq");
        try ( FASTQReader fr = new FASTQReader(f.getAbsolutePath(), false)) {
            fr.hasMoreElements();
            DNAQualitySequenceI entry = fr.nextElement();
            assertArrayEquals("IRIS:7:1:17:394#0/1".getBytes(), entry.getName());
            assertArrayEquals("GTCAGGACAAGAAAGACAANTCCAATTNACATTATG".getBytes(), entry.getSequence());
            byte[] quality = "aaabaa`]baaaaa_aab]D^^`b`aYDW]abaa`^".getBytes();
            for (int i = 0; i < quality.length; i++) {
                quality[i] -= 64;
            }
            assertArrayEquals(quality, entry.getQuality());
        } catch (SeqStoreException ex) {
            fail(ex.getMessage());
        }
        f.delete();
    }

    @Test
    public void testIncompleteLastLine() throws Exception {
        System.out.println("testIncompleteLastLine");
        // file has no line break after last line
        File f = TestInput.copyTestResource(getClass(), "de/cebitec/mgx/seqstorage/incomplete_last_line.fq");
        int seqCnt = 0;
        DNAQualitySequenceI seq = null;
        try ( FASTQReader fr = new FASTQReader(f.getAbsolutePath(), false)) {
            while (fr.hasMoreElements()) {
                seq = fr.nextElement();
                seqCnt++;
            }
        } catch (SeqStoreException ex) {
            fail(ex.getMessage());
        } finally {
            f.delete();
        }
        assertEquals(1, seqCnt);
        assertNotNull(seq);
        assertEquals(seq.getSequence().length, seq.getQuality().length);
    }

    @Test
    public void testBrokenSeq() throws Exception {
        System.out.println("testBrokenSeq");
        // file has no line break after last line
        File f = TestInput.copyTestResource(getClass(), "de/cebitec/mgx/seqstorage/broken_seq.fq");
        DNAQualitySequenceI seq = null;
        try ( FASTQReader fr = new FASTQReader(f.getAbsolutePath(), false)) {
            while (fr.hasMoreElements()) {
                seq = fr.nextElement();
            }
        } catch (SeqStoreException ex) {
            if (ex.getMessage().contains("length differs between sequence and qualit")) {
                return;
            }
        } finally {
            f.delete();
        }
        fail("FASTQReader produced sequence with differing sequence and quality length.");
    }

    @Test
    public void testLowerCaseInput() throws Exception {
        System.out.println("testLowerCaseInput");
        File f = TestInput.copyTestResource(getClass(), "de/cebitec/mgx/seqstorage/lowercase.fq");
        try ( FASTQReader fr = new FASTQReader(f.getAbsolutePath(), false)) {
            fr.hasMoreElements();
            DNAQualitySequenceI entry = fr.nextElement();
            assertEquals("TCGGT", new String(entry.getSequence()));
        } catch (SeqStoreException ex) {
            fail(ex.getMessage());
        }
        f.delete();
    }

    @Test
    public void testIUPAC() throws Exception {
        System.out.println("testIUPAC");
        File f = TestInput.copyTestResource(getClass(), "de/cebitec/mgx/seqstorage/regression_iupac.fq");
        int seqCnt = 0;
        try ( FASTQReader fr = new FASTQReader(f.getAbsolutePath(), false)) {
            while (fr.hasMoreElements()) {
                fr.nextElement();
                seqCnt++;
            }
        } catch (SeqStoreException ex) {
            fail(ex.getMessage());
        }
        f.delete();

        assertEquals(1, seqCnt);
    }

    @Test
    public void testSeqLength() throws IOException {
        System.out.println("testSeqLength");
        File target = File.createTempFile("testIncreasingSeqLength", "fq");
        assertNotNull(target);

        try ( FASTQWriter fw = new FASTQWriter(target.getAbsolutePath(), QualityEncoding.Sanger)) {
            assertNotNull(fw);
            for (int seqCnt = 1; seqCnt <= 2000; seqCnt++) {
                DNAQualitySequenceI qseq = generateSequence(seqCnt);
                assertNotNull(qseq);
                assertEquals(seqCnt, qseq.getSequence().length);
                assertEquals(seqCnt, qseq.getQuality().length);
                fw.addSequence(qseq);
            }
        } catch (SequenceException | IOException ex) {
            fail(ex.getMessage());
        }

        int seqCnt = 1;
        try ( FASTQReader fr = new FASTQReader(target.getAbsolutePath(), false)) {
            while (fr.hasMoreElements()) {
                DNAQualitySequenceI qseq = fr.nextElement();
                assertEquals(seqCnt, qseq.getSequence().length);
                assertEquals(seqCnt, qseq.getQuality().length);
                seqCnt++;
            }
        } catch (SequenceException ex) {
            fail(ex.getMessage());
        }
        target.delete();
    }

    @Test
    public void testLongRead() throws Exception {
        System.out.println("testLongRead");
        File f = TestInput.copyTestResource(getClass(), "de/cebitec/mgx/seqstorage/nptest.fq");
        File target = File.createTempFile("testFQWriterxx", null);
        target.delete();
        DNAQualitySequenceI seq = null;
        try ( FASTQReader fr = new FASTQReader(f.getAbsolutePath(), false)) {
            try ( CSQFWriter csq = new CSQFWriter(target.getAbsolutePath())) {
                while (fr.hasMoreElements()) {
                    seq = fr.nextElement();
                    fr.hasMoreElements();
                    seq = fr.nextElement();
                    System.out.println(seq.getSequence().length + " " + seq.getQuality().length);
                    csq.addSequence(seq);
                }
            }
        } catch (SeqStoreException ex) {
            fail(ex.getMessage());
        }

        // read back result
        CSQFReader r = new CSQFReader(target.getAbsolutePath());
        int cnt = 0;
        while (r.hasMoreElements()) {
            DNAQualitySequenceI s = r.nextElement();
            System.out.println(s.getSequence().length + " " + s.getQuality().length);

            //System.out.println(cnt);
            assertNotNull(s);
            cnt++;
        }
        assertEquals(1, cnt);
    }

//    @Test
//    public void testExternalFile() throws Exception {
//        System.out.println("testExternalFile");
//        File f = new File("/home/sj/SRR7158629_2.fastq.gz");
//        assertTrue(f.exists());
//        assertTrue(f.canRead());
//
//        int seqCnt = 0;
//        try ( FASTQReader fr = new FASTQReader(f.getAbsolutePath(), true)) {
//            while (fr.hasMoreElements()) {
//                DNAQualitySequenceI seq = fr.nextElement();
//                assertNotNull(seq);
//                seqCnt++;
//            }
//        } catch (SeqStoreException ex) {
//            fail(ex.getMessage());
//        }
//        assertEquals(62_779_079, seqCnt);
//    }

    private static DNAQualitySequenceI generateSequence(int len) throws SequenceException {
        byte[] seqName = String.valueOf(len).getBytes();
        byte[] s = new byte[len];
        byte[] q = new byte[len];
        int quality = 2;

        for (int i = 0; i < len; i++) {
            s[i] = 'A';
            q[i] = (byte) quality;
        }
        DNAQualitySequenceI seq = new QualityDNASequence(s, q, true);
        seq.setName(seqName);
        return seq;
    }
}
