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
public class QCResult {
    
    private final String name;
    private final DataRow[] data;

    public QCResult(String name, DataRow[] data) {
        this.name = name;
        this.data = data;
    }

    public String getName() {
        return name;
    }

    public DataRow[] getData() {
        return data;
    }
    
}
