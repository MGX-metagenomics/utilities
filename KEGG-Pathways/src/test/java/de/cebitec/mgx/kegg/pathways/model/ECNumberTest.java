/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.kegg.pathways.model;

import de.cebitec.mgx.kegg.pathways.KEGGException;
import de.cebitec.mgx.kegg.pathways.api.ECNumberI;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.Test;

/**
 *
 * @author sj
 */
public class ECNumberTest {

    public ECNumberTest() {
    }

    @Test
    public void testEquals() {
        System.out.println("testEquals");
        ECNumberI ec1 = new ECNumber("1.1.1.1");
        ECNumberI ec2 = new ECNumber("1.1.1.1");
        ECNumberI ec3 = new ECNumber("1.1.1.2");
        assertEquals(ec1, ec2);
        assertNotEquals(ec1, ec3);
    }

    @Test
    public void testFactory() {
        System.out.println("testFactory");
        try {
            ECNumberI ec1 = ECNumberFactory.fromString("1.1.1.1");
            ECNumberI ec2 = ECNumberFactory.fromString("1.1.1.1");
            ECNumberI ec3 = ECNumberFactory.fromString("1.1.1.2");
            assertNotNull(ec1);
            assertNotNull(ec2);
            assertNotNull(ec3);
            assertEquals(ec1, ec2);
            assertSame(ec1, ec2);
            assertNotEquals(ec1, ec3);
        } catch (KEGGException ex) {
            fail(ex.getMessage());
        }
    }

}
