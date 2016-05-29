/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.kegg.pathways.model;

import de.cebitec.mgx.kegg.pathways.KEGGException;
import de.cebitec.mgx.kegg.pathways.api.ECNumberI;
import org.junit.Assert;
import static org.junit.Assert.fail;
import org.junit.Test;

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
        Assert.assertEquals(ec1, ec2);
        Assert.assertNotEquals(ec1, ec3);
    }

    @Test
    public void testFactory() {
        System.out.println("testFactory");
        try {
            ECNumberI ec1 = ECNumberFactory.fromString("1.1.1.1");
            ECNumberI ec2 = ECNumberFactory.fromString("1.1.1.1");
            ECNumberI ec3 = ECNumberFactory.fromString("1.1.1.2");
            Assert.assertNotNull(ec1);
            Assert.assertNotNull(ec2);
            Assert.assertNotNull(ec3);
            Assert.assertEquals(ec1, ec2);
            Assert.assertSame(ec1, ec2);
            Assert.assertNotEquals(ec1, ec3);
        } catch (KEGGException ex) {
            fail(ex.getMessage());
        }
    }

}
