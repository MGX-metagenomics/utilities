/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.dnautils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;


/**
 *
 * @author sj
 */
public class DNAUtilsTest {
    
    public DNAUtilsTest() {
    }

    /**
     * Test of translate method, of class DNAUtils.
     */
    @Test
    public void testTranslate() {
        System.out.println("translate");
        String result = DNAUtils.translate("ATG");
        assertEquals("M", result);
    }

    
}
