/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.pevents.internal;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeListenerProxy;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sj
 */
public class EventReceiver implements Runnable {

    private final BlockingQueue<DistributionEvent> in;
    private final EventDistributor distributor;
    private volatile boolean exit = false;

    public EventReceiver(EventDistributor dist, BlockingQueue<DistributionEvent> in) {
        this.distributor = dist;
        this.in = in;
    }

    @Override
    public void run() {
        while (!exit) {
            DistributionEvent dEvent = null;
            try {
                dEvent = in.poll(500, TimeUnit.MILLISECONDS);
            } catch (InterruptedException ex) {
                Logger.getLogger(EventReceiver.class.getName()).log(Level.SEVERE, null, ex);
            }

            if (dEvent != null) {
                PropertyChangeEvent event = dEvent.getEvent();
                PropertyChangeListener target = dEvent.getTarget();

                if (target instanceof PropertyChangeListenerProxy) {
                    PropertyChangeListenerProxy pclp = (PropertyChangeListenerProxy) target;
                    if (pclp.getPropertyName().equals(event.getPropertyName())) {
                        distributor.acquireBusyLock();
                        pclp.propertyChange(event);
                        distributor.releaseBusyLock();
                    }
                } else {
                    long start = System.currentTimeMillis();
                    //Logger.getLogger(getClass().getName()).log(Level.INFO, "Delivering event {0} to target {1} on thread {2}", new Object[]{event, target.getClass().getSimpleName(), Thread.currentThread().getName()});
                    distributor.acquireBusyLock();
                    target.propertyChange(event);
                    distributor.releaseBusyLock();
                    start = System.currentTimeMillis() - start;
                    if (start >= 100) {
                        Logger.getLogger(getClass().getName()).log(Level.INFO, "Slow processing of propertyChange {0} ({1} ms) for target {2} on thread {3}", new Object[]{event.getPropertyName(), start, target.getClass().getSimpleName(), Thread.currentThread().getName()});
                    }
                }
                dEvent.delivered();
            }
        }
    }

    void shutDown() {
        exit = true;
    }
}
