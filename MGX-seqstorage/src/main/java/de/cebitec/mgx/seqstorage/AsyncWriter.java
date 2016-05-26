/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.seqstorage;

import de.cebitec.mgx.sequence.DNASequenceI;
import de.cebitec.mgx.sequence.SeqStoreException;
import de.cebitec.mgx.sequence.SeqWriterI;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sj
 */
public class AsyncWriter<T extends DNASequenceI> implements SeqWriterI<T>, Runnable {

    private final SeqWriterI<T> target;
    private final LinkedBlockingQueue<T> queue = new LinkedBlockingQueue<>(5000);
    private final CountDownLatch isDone = new CountDownLatch(1);
    private volatile boolean mayExit = false;

    public AsyncWriter(ExecutorService exec, SeqWriterI<T> target) throws InterruptedException {
        this.target = target;
        exec.submit(this);
    }

    @Override
    public void addSequence(T seq) throws SeqStoreException {
        if (!queue.offer(seq)) {
            try {
                queue.put(seq);
            } catch (InterruptedException ex) {
                throw new SeqStoreException(ex.getMessage());
            }
        }
    }

    @Override
    public void run() {
//        System.out.println("AsyncWriter alive on " + Thread.currentThread().getName());

        try {
            while (!mayExit) {
                T cur = queue.poll(50, TimeUnit.MILLISECONDS);
                if (cur != null) {
                    target.addSequence(cur);
                    cur = null;
                }
            }

            // flush queue
            while (!queue.isEmpty()) {
                T cur = queue.poll();
                if (cur != null) {
                    target.addSequence(cur);
                }
            }
        } catch (SeqStoreException | InterruptedException ex) {
            Logger.getLogger(AsyncWriter.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            isDone.countDown();
        }
    }

    @Override
    public void close() throws Exception {
        mayExit = true;
        isDone.await();
        target.close();
    }

}
