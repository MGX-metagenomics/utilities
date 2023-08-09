/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package de.cebitec.mgx.seqstorage;

import de.cebitec.mgx.sequence.DNAQualitySequenceI;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

/**
 *
 * @author sj
 */
public class AlternatingQReaderTest {

  
//    @Test
//    public void testReader() throws Exception {
//        System.out.println("testReader");
//
//        FASTQReader fr = new FASTQReader("/home/sj/SRR7158629_1.fastq.gz", true);
//        FASTQReader rr = new FASTQReader("/home/sj/SRR7158629_2.fastq.gz", true);
//
//        AlternatingQReader reader = new AlternatingQReader(fr, rr);
//        long count = 0;
//        
//        while (reader.hasMoreElements()) {
//            DNAQualitySequenceI seq = reader.nextElement();
//            assertNotNull(seq);
//            count++;
//        }
//        reader.close();
//        
//        assertTrue(count % 2 == 0);
//        assertEquals(125_558_158, count);
//    }
}
