/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.parallel;

import de.cebitec.mgx.parallel.misc.NOP;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

/**
 *
 * @author sj
 */
public class ParallelProcessorTest {

    public ParallelProcessorTest() {
    }


    @Test
    public void testSomeMethod() {
        final List<String> res = new ArrayList<>();
        ParallelProcessor<String, String> pp = new ParallelProcessor<>(2, new NOP(), new OutputProcessor<String>() {

            @Override
            public void process(String u) {
                res.add(u);
            }
        });

        pp.add("foo");
        pp.add("bar");
        pp.add("baz");
        pp.done();
//        for (String s : res) {
//            System.err.println(s);
//        }
        assertEquals(3, res.size());
    }

    @Test
    public void testSomeMethod2() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException ex) {
            Logger.getLogger(ParallelProcessorTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        final List<String> res = new ArrayList<>();
        ParallelProcessor<String, String> pp = new ParallelProcessor<>(20, new NOP(), new OutputProcessor<String>() {

            @Override
            public void process(String u) {
                res.add(u);
            }
        });

        for (int i = 0; i < 100000; i++) {
            pp.add("xxx");
        }
        pp.done();

        assertEquals(100000, res.size());
    }

    @Test
    public void testTiming() {
        for (int i = 1; i < 25; i++) {
            long start = System.currentTimeMillis();
            run(i);
            start = System.currentTimeMillis() - start;
            double speed = 10000d / start;
            System.err.println("threads "+i+ " in "+start+"ms ("+speed+" elements/ms)");
        }

    }

    private void run(int num) {
        final List<String> res = new ArrayList<>();
        ParallelProcessor<String, String> pp = new ParallelProcessor<>(num, new NOP(), new OutputProcessor<String>() {

            @Override
            public void process(String u) {
                res.add(u);
            }
        });

        for (int i = 0; i < 10000; i++) {
            pp.add("xxx");
        }
        pp.done();
        assertEquals(10000, res.size());
    }
}
