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
public interface QCResultI extends Comparable<QCResultI> {

    int compareTo(QCResultI o);

    DataRowI[] getData();

    String getName();
    
}