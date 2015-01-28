/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.parallel;

import de.cebitec.mgx.parallel.api.ParallelWorkerI;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sj
 */
public abstract class ParallelWorker<T, U> extends ParallelWorkerI<T, U> {

    private final Queue<T> in = new LinkedTransferQueue<>();
    private final BlockingQueue<U> out = new LinkedTransferQueue<>();
    private volatile boolean done = false;
    private final CountDownLatch finishedProcessing = new CountDownLatch(1);
    private long TID = -1;

    public abstract U process(T t);

    @Override
    public void add(T t) {
        if (done) {
            throw new IllegalArgumentException("Cannot add data, worker already in finished state.");
        }
        in.add(t);
    }

    @Override
    public U get() {
        try {
            U u = out.poll(5, TimeUnit.MILLISECONDS);
            if (u != null) {
                return u;
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(ParallelWorker.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public void done() {
        done = true;
        try {
            finishedProcessing.await();
        } catch (InterruptedException ex) {
            Logger.getLogger(ParallelWorker.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public abstract ParallelWorkerI<T, U> clone();

    @Override
    public void run() {
        TID = Thread.currentThread().getId();

        //int numProcessed = 0;
        while (!done || !in.isEmpty()) {
            T t = null;
            t = in.poll();
//            try { 
//                t = in.poll(5, TimeUnit.MILLISECONDS);
//            } catch (InterruptedException ex) {
//                Logger.getLogger(ParallelWorker.class.getName()).log(Level.SEVERE, null, ex);
//            }
            if (t != null) {
                //numProcessed++;
                out.add(process(t));
            }
        }

        // there might be data left in "out" waiting to be retrieved
        //System.err.println("     worker " + Thread.currentThread().getId() + " completed processing " + numProcessed);
        finishedProcessing.countDown();

    }

}
