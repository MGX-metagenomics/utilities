/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.pevents.internal;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashSet;
import java.util.Set;
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
public class EventDistributor implements Runnable, AutoCloseable {

    private final BlockingQueue<AccumulatedEvent> in = new LinkedTransferQueue<>();
    private final BlockingQueue<DistributionEvent> out = new LinkedTransferQueue<>();
    private final Set<EventReceiver> receivers = new HashSet<>(MAX_RECV_THREADS);
    private volatile boolean exit = false;
    //
    private static final int MAX_RECV_THREADS = 5;

    @Override
    public void run() {
        try {
            while (!exit) {
                AccumulatedEvent aEvent = in.poll(5, TimeUnit.MILLISECONDS);
                if (aEvent != null) {
                    startReceivers();
                    PropertyChangeListener[] listeners = aEvent.getListeners();
                    PropertyChangeEvent event = aEvent.getEvent();
                    CountDownLatch allDelivered = new CountDownLatch(listeners.length);
                    //Logger.getLogger(EventDistributor.class.getName()).log(Level.INFO, "ED got event for {0} targets", listeners.length);
                    for (PropertyChangeListener pcl : listeners) {
                        DistributionEvent dEvent = new DistributionEvent(allDelivered, event, pcl);
                        out.add(dEvent);
                    }
                    allDelivered.await();
                    aEvent.processed();
                }
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(EventDistributor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    void startReceivers() {
        if (receivers.size() == MAX_RECV_THREADS) {
            return;
        }
        for (int i = receivers.size() + 1; i <= MAX_RECV_THREADS; i++) {
            EventReceiver er = new EventReceiver(out, i);
            Thread thread = new Thread(er, "pPCS-Receiver-" + i);
            thread.setDaemon(true);
            thread.start();
            receivers.add(er);
        }
    }

    public void distributeEvent(AccumulatedEvent event) {
        in.add(event);
    }

    @Override
    public synchronized void close() {
        for (EventReceiver er : receivers) {
            er.shutDown();
        }
        receivers.clear();
        exit = true;
    }
}
