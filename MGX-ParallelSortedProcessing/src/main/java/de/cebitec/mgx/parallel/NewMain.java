/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.parallel;

import de.cebitec.mgx.parallel.misc.NOP;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author sj
 */
public class NewMain {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        System.err.println();
        for (int i = 1; i < 25; i++) {
            long start = System.currentTimeMillis();
            run(i);
            start = System.currentTimeMillis() - start;
            double speed = 10000d / start;
            System.err.println(i+" threads in " + start + "ms (" + speed + " elements/ms)");
        }

    }

    private static void run(int num) {
        final List<String> res = new ArrayList<>();
        ParallelProcessor<String, String> pp = new ParallelProcessor<>(num, new NOP(), new OutputProcessor<String>() {

            @Override
            public void process(String u) {
                //res.add();
            }
        });
        for (int i = 0; i < 500000; i++) {
            pp.add("xxx");
        }
        pp.done();
    }
}
