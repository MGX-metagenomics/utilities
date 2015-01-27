/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.parallel;

import de.cebitec.mgx.parallel.api.ParallelWorkerI;
import java.util.Iterator;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sj
 */
public abstract class OutputProcessor<U> implements Runnable {

    private Iterator<ParallelWorkerI<?, U>> flushIter;
    private CountDownLatch started, finished;
    private AtomicInteger count;
    private volatile boolean done = false;

    final void initOutput(Iterator<ParallelWorkerI<?, U>> flushIter, AtomicInteger count) {
        this.flushIter = flushIter;
        this.started = new CountDownLatch(1);
        this.count = count;
        finished = new CountDownLatch(1);
        new Thread(this).start();

        try {
            started.await();
        } catch (InterruptedException ex) {
            Logger.getLogger(OutputProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public  void done() {

        done = true;

        try {
            finished.await();
        } catch (InterruptedException ex) {
            Logger.getLogger(OutputProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public abstract void process(U u);

    @Override
    public void run() {
        started.countDown();

        flushIter.hasNext();  // always true...
        ParallelWorkerI<?, U> curWorker = flushIter.next();
        U elem = null;

        while (count.get() > 0 || !done) {
            elem = null;

            while (elem == null && count.get() > 0) {
                elem = curWorker.get();
            }
            if (elem != null) {
                count.decrementAndGet();
                process(elem);
                // advance to next worker thread
                flushIter.hasNext();
                curWorker = flushIter.next();
            }
        }

        finished.countDown();
    }

}
