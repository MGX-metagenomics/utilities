/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.seqstorage;

/**
 *
 * @author sj
 */
public class CSQFReaderTest {

//    @Test
//    public void testFetchRegression() throws Exception {
//        System.out.println("testFetchRegression");
//        long[] ids = new long[]{8437460};
//        CSQFReader r = new CSQFReader("/tmp/55");
//        Set<DNAQualitySequenceI> result = r.fetch(ids);
//        assertEquals(1, result.size());
//    }
//
//    @Test
//    public void testFetchRegression2() throws Exception {
//        System.out.println("testFetchRegression2");
//        long[] ids = new long[]{8437459, 8437461};
//        CSQFReader r = new CSQFReader("/tmp/55");
//        Set<DNAQualitySequenceI> result = r.fetch(ids);
//        assertEquals(2, result.size());
//    }
//
//    @Test
//    public void testIterate() throws Exception {
//        System.out.println("testIterate");
//        
//        Assume.assumeTrue(new File("/home/sj/error/20").exists());
//        
//        CSQFReader r = new CSQFReader("/home/sj/error/20");
//        int cnt = 0;
//        while (r.hasMoreElements()) {
//            //System.err.print(".");
//            DNAQualitySequenceI seq = r.nextElement();
//            assertNotNull(seq);
//            cnt++;
//        }
//
//        assertEquals(165235, cnt);
//    }

    //TODO: write test writing/reading sequences w/ qual in increasing sizes
//
//    @Test
//    public void testBench() throws Exception {
//        System.out.println("testBench");
//        long time = System.currentTimeMillis();
//        for (int i=0; i<10;i++) {
//            testIterate();
//        }
//        time = System.currentTimeMillis() - time;
//        System.err.println(time + "ms");
////    }
//    @Test
//    public void testLongRead() throws Exception {
//        System.out.println("testLongRead");
//        File f = new File("/home/sj/error/Marburg.fastq");
//        DNAQualitySequenceI seq = null;
//        int numSeqs = 0;
//        try (FASTQReader fr = new FASTQReader(f.getAbsolutePath(), false)) {
//            try (CSQFWriter csq = new CSQFWriter("/tmp/foo")) {
//                while (fr.hasMoreElements()) {
//                    seq = fr.nextElement();
//                    csq.addSequence(seq);
//                    numSeqs ++;
//                }
//            }
//        } catch (SeqStoreException ex) {
//            fail(ex.getMessage() + " after sequence " + new String(seq.getName()));
//        }
//
//        // read back result
//        CSQFReader r = new CSQFReader("/tmp/foo");
//        int cnt = 0;
//        while (r.hasMoreElements()) {
//            DNAQualitySequenceI s = r.nextElement();
//            //System.out.println(s.getSequence().length + " " + s.getQuality().length);
//
//            //System.out.println(cnt);
//            assertNotNull(s);
//            cnt++;
//        }
//        assertEquals(165235, cnt);
//
//        new File("/tmp/foo").delete();
//        new File("/tmp/foo.csq").delete();
//    }
    
}
