/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.seqcompression;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;


/**
 *
 * @author sj
 */
public class FourBitEncoderTest {
    
    @Test
    public void testDecodeEmpty() {
        System.out.println("decodeEmpty");
        byte[] result = FourBitEncoder.decode(new byte[]{});
        assertEquals(0, result.length);
    }

    
}