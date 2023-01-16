package de.cebitec.mgx.sequence;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author sj
 */
public class PhredUtilTest {

    @Test
    public void testPhredToRaw20() {
        System.out.println("testPhredToRaw20");
        double raw = PhredUtil.phredToRaw(20);
        assertEquals(0.01, raw);
    }

    @Test
    public void testPhredToRaw30() {
        System.out.println("testPhredToRaw30");
        double raw = PhredUtil.phredToRaw(30);
        assertEquals(0.001, raw);
    }

    @Test
    public void testRawToPhred20() {
        System.out.println("rawToPhred20");
        int phred = PhredUtil.rawToPhred(0.01); // 1%
        assertEquals(20, phred);
    }

    @Test
    public void testRawToPhred30() {
        System.out.println("rawToPhred30");
        int phred = PhredUtil.rawToPhred(0.001); // 0.1%
        assertEquals(30, phred);
    }

}
