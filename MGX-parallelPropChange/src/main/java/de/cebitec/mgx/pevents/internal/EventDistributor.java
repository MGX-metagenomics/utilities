/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.pevents.internal;

import java.beans.PropertyChangeListener;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sj
 */
public class EventDistributor implements Runnable, AutoCloseable {

    private final BlockingQueue<AccumulatedEvent> in = new LinkedTransferQueue<>();
    private final BlockingQueue<DistributionEvent> out = new LinkedTransferQueue<>();
    private final EventReceiver[] receivers = new EventReceiver[MAX_RECV_THREADS];
    private volatile boolean exit = false;
    private final Semaphore allDeliveryThreadsBusy;
    //
    private static final int MAX_RECV_THREADS = 11;
    public final static String RECVTHREAD_NAME_PREFIX = "pPCS-Receiver-";

    public EventDistributor() {
        for (int i = 1; i <= MAX_RECV_THREADS; i++) {
            EventReceiver er = new EventReceiver(this, out);
            Thread thread = new Thread(er, RECVTHREAD_NAME_PREFIX + i);
            thread.setDaemon(true);
            thread.start();
            receivers[i - 1] = er;
        }
        allDeliveryThreadsBusy = new Semaphore(MAX_RECV_THREADS);
    }

    @Override
    public void run() {
        while (!(exit && in.isEmpty())) {
            AccumulatedEvent aEvent = null;
            try {
                aEvent = in.poll(500, TimeUnit.MILLISECONDS);
            } catch (InterruptedException ex) {
                Logger.getLogger(EventDistributor.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (aEvent != null) {
                PropertyChangeListener[] listeners = aEvent.getListeners();
                for (PropertyChangeListener pcl : listeners) {
                    DistributionEvent dEvent = new DistributionEvent(aEvent, pcl);
                    out.add(dEvent);
                }
            }
        }
    }

    public final boolean allDeliveryThreadsBlocked() {
        return allDeliveryThreadsBusy.availablePermits() == 0;
    }

    final void acquireBusyLock() {
        allDeliveryThreadsBusy.acquireUninterruptibly();
    }

    final void releaseBusyLock() {
        allDeliveryThreadsBusy.release();
    }

    public void distributeEvent(AccumulatedEvent event) {
        in.add(event);
    }

    @Override
    public void close() {
        for (EventReceiver er : receivers) {
            er.shutDown();
        }
        exit = true;
    }
}
