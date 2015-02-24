/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.parallel.misc;

import de.cebitec.mgx.parallel.ParallelWorker;
import de.cebitec.mgx.parallel.api.ParallelWorkerI;

/**
 *
 * @author sj
 */
public class NOP extends ParallelWorker<String, String> {

    @Override
    public String process(String t) {
//        try {
//            //System.err.println(Thread.currentThread().getId() + " processes "+ t);
//            //Thread.sleep(1);
//        } catch (InterruptedException ex) {
//            Logger.getLogger(NOP.class.getName()).log(Level.SEVERE, null, ex);
//        }
      
        return t;
    }

    @Override
    public ParallelWorkerI<String, String> clone() {
        return new NOP();
    }

}
