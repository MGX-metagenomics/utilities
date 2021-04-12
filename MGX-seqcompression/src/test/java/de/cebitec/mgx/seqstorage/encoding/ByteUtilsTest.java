/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.seqstorage.encoding;

import de.cebitec.mgx.seqcompression.ByteUtils;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author sj
 */
public class ByteUtilsTest {

    @Test
    public void testIndexOf() {
        System.out.println("indexOf");
        byte[] in = new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 0};
        byte sep = 7;
        int result = ByteUtils.indexOf(in, sep);
        assertEquals(6, result);
    }

    @Test
    public void testIndexOfMissing() {
        System.out.println("testIndexOfMissing");
        byte[] in = new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 0};
        byte sep = 23;
        int result = ByteUtils.indexOf(in, sep);
        assertEquals(-1, result);
    }

    @Test
    public void testLongToBytes() {
        System.out.println("testLongToBytes");
        long l = 12345;
        byte[] result = ByteUtils.longToBytes(l);
        assertEquals(8, result.length);
        assertArrayEquals(new byte[]{0, 0, 0, 0, 0, 0, 48, 57}, result);
    }

    @Test
    public void testBytesToLong() {
        System.out.println("testBytesToLong");
        long l = ByteUtils.bytesToLong(new byte[]{0, 0, 0, 3, 0, 0, 48, 57});
        assertEquals(12884914233l, l);
    }
}
