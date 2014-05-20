/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.pevents.test;

import de.cebitec.mgx.pevents.ParallelPropertyChangeSupport;
import de.cebitec.mgx.pevents.test.EventTarget;
import java.beans.PropertyChangeSupport;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author sj
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        int cnt = Integer.parseInt(args[0]);

        FileWriter fw = new FileWriter("/tmp/foo", true);

        int numRepetitions = 3;
        long pcsTime = 0;
        long apcsTime = 0;

        for (int iteration = 0; iteration < numRepetitions; iteration++) {
            
            PropertyChangeSupport pcs = new PropertyChangeSupport(new Object());
            PropertyChangeSupport apcs = new ParallelPropertyChangeSupport("LALALA");

            List<EventTarget> recvs1 = new ArrayList<>();
            List<EventTarget> recvs2 = new ArrayList<>();
            for (int i = 0; i < cnt; i++) {
                int randomNum = 1 + (int)(Math.random()*499); 
                EventTarget r1 = new EventTarget(randomNum);
                EventTarget r2 = new EventTarget(randomNum);
                pcs.addPropertyChangeListener(r1);
                apcs.addPropertyChangeListener(r2);
                recvs1.add(r1);
                recvs2.add(r2);
            }
            long start = System.nanoTime();
            pcs.firePropertyChange("fpp", 1, 2);
            start = System.nanoTime() - start;
            pcsTime += start;

            long start2 = System.nanoTime();
            apcs.firePropertyChange("fpp", 1, 2);
            start2 = System.nanoTime() - start2;
            apcsTime += start2;

            for (EventTarget r : recvs1) {
                pcs.removePropertyChangeListener(r);
            }
            for (EventTarget r : recvs2) {
                apcs.removePropertyChangeListener(r);
            }
        }

        pcsTime = pcsTime / numRepetitions;
        apcsTime = apcsTime / numRepetitions;
        fw.write(cnt + "\t" + pcsTime + "\t" + apcsTime + "\n");
        fw.flush();
        //System.err.println("  sync notification completed after " + start + " ms");
        fw.close();
    }

}
