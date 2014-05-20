/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.pevents.internal;

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
    private final int id;
    private volatile boolean exit = false;

    public EventReceiver(BlockingQueue<DistributionEvent> in, int id) {
        this.in = in;
        this.id = id;
    }

    public void shutDown() {
        exit = true;
    }

    @Override
    public void run() {
        try {
            while (!exit) {
                DistributionEvent dEvent = in.poll(5, TimeUnit.MILLISECONDS);
                if (dEvent != null) {
                    //Logger.getLogger(EventReceiver.class.getName()).log(Level.INFO, "receiver " + id + " got event");
                    PropertyChangeListener pcl = dEvent.getTarget();
                    if (pcl instanceof PropertyChangeListenerProxy) {
                        PropertyChangeListenerProxy pclp = (PropertyChangeListenerProxy) pcl;
                        if (pclp.getPropertyName().equals(dEvent.getEvent().getPropertyName())) {
                            pclp.propertyChange(dEvent.getEvent());
                        }
                    } else {
                        pcl.propertyChange(dEvent.getEvent());
                    }
                    //Logger.getLogger(EventReceiver.class.getName()).log(Level.INFO, "receiver " + id + " delivered event");
                    dEvent.processed();
                }
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(EventReceiver.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
