/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.seqstorage;

import de.cebitec.mgx.seqcompression.SequenceException;
import de.cebitec.mgx.sequence.DNASequenceI;
import de.cebitec.mgx.sequence.SeqReaderI;
import de.cebitec.mgx.sequence.SeqStoreException;
import java.util.Set;
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

public class AsyncReader<T extends DNASequenceI> implements SeqReaderI<T>, Runnable {

    private final SeqReaderI<T> source;
    private final LinkedBlockingQueue<T> queue = new LinkedBlockingQueue<>(5000);
    private final CountDownLatch isUp = new CountDownLatch(1);
    private T elem = null;
    private volatile boolean mayExit = false;

    public AsyncReader(ExecutorService exec, SeqReaderI<T> source) throws InterruptedException, SequenceException {
        this.source = source;
        if (source.hasMoreElements()) {
            elem = source.nextElement();
            exec.submit(this);
            isUp.await();
        }
    }

    @Override
    public boolean hasQuality() {
        return source.hasQuality();
    }

    @Override
    public T nextElement() {
        if (elem == null) {
            try {
                elem = queue.poll(5, TimeUnit.MINUTES);
            } catch (InterruptedException ex) {
                Logger.getLogger(AsyncReader.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        T ret = elem;
        elem = null;
        return ret;
    }

    @Override
    public boolean hasMoreElements() throws SequenceException {
        return elem != null || !queue.isEmpty() || source.hasMoreElements();
    }

    @Override
    public void run() {
        //System.out.println("AsyncReader alive on " + Thread.currentThread().getName());

        // pre-seed into queue and notify waiting ctor
        try {
            if (source.hasMoreElements()) {
                queue.put(source.nextElement());
            } else {
                return; // input is empty
            }
        } catch (SequenceException | InterruptedException ex) {
            Logger.getLogger(AsyncReader.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            isUp.countDown();
        }

        try {
            while (!mayExit && source.hasMoreElements()) {
                T cur = source.nextElement();
                queue.put(cur);
                mayExit = !source.hasMoreElements();
            }
        } catch (SequenceException | InterruptedException ex) {
            Logger.getLogger(AsyncReader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void close() throws SeqStoreException {
        mayExit = true;
        source.close();
    }

    @Override
    public void delete() {
        source.delete();
    }

    @Override
    public Set<T> fetch(long[] ids) throws SeqStoreException {
        throw new UnsupportedOperationException("Async reader does not support random sequence access.");
    }

}
