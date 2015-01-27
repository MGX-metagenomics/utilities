/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.parallel;

import de.cebitec.mgx.parallel.api.ParallelWorkerI;
import de.cebitec.mgx.parallel.internal.RingBuffer;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author sj
 */
public class ParallelProcessor<T, U> {

    private final RingBuffer<ParallelWorkerI<T, U>> workers;
    private final Iterator<ParallelWorkerI<T, U>> addIter;
    private final Iterator<ParallelWorkerI<?, U>> flushIter;
    private final OutputProcessor<U> outProc;
    private volatile boolean done = false;
    private final AtomicInteger count = new AtomicInteger(0);
    private final int numWorkers;

    public ParallelProcessor(ParallelWorkerI<T, U> worker, OutputProcessor<U> outProc) {
        this(5, worker, outProc);
    }

    public ParallelProcessor(int numWorkers, ParallelWorkerI<T, U> worker, OutputProcessor<U> outProc) {
        this.numWorkers = numWorkers;
        workers = new RingBuffer<>(numWorkers);

        startWorkers(worker);

        // we need two iterators (both starting at offset 0) to maintain
        // order between input and output elements
        addIter = workers.iterator();
        flushIter = new Iter<>(workers.iterator());

        // start output processor
        this.outProc = outProc;
        outProc.initOutput(flushIter, count);
    }

    public synchronized void add(T t) {
        if (done) {
            throw new IllegalArgumentException("Cannot add data, already in finished state.");
        }
        count.incrementAndGet();
        // distribute to workers, round-robin
        assert addIter.hasNext();
        addIter.next().add(t);
    }

    public synchronized void done() {
        if (done) {
            return;
        }

        done = true;
        for (ParallelWorkerI<T, U> pw : workers.fetchall()) {
            pw.done();
        }

        outProc.done();
        assert count.get() == 0;
    }

    private void startWorkers(ParallelWorkerI<T, U> worker) {
        // create and start workers
        workers.add(worker);
        new Thread(worker).start();
        for (int i = 1; i < numWorkers; i++) {
            ParallelWorkerI<T, U> clone = worker.clone();
            workers.add(clone);
            new Thread(clone).start();
        }
    }

    private class Iter<T> implements Iterator<ParallelWorkerI<?, U>> {

        private final Iterator<ParallelWorkerI<T, U>> it;

        public Iter(Iterator<ParallelWorkerI<T, U>> it) {
            this.it = it;
        }

        @Override
        public boolean hasNext() {
            return it.hasNext();
        }

        @Override
        public ParallelWorkerI<?, U> next() {
            return it.next();
        }
    }

}
