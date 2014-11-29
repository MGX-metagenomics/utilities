/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.qc;

/**
 *
 * @author sj
 */
public class DataRow {

    private final String name;
    private final float[] data;

    public DataRow(String name, float[] data) {
        this.name = name;
        this.data = data;
    }

    public String getName() {
        return name;
    }

    public float[] getData() {
        return data;
    }
}
