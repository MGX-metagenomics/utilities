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
                    PropertyChangeEvent event = dEvent.getEvent();
                    
//                     // debug code
//                    assert event != null;
//                    assert event.getPropertyName() != null;
//                    assert event.getOldValue() != null;
//                    assert event.getNewValue() != null;
                    
                    PropertyChangeListener pcl = dEvent.getTarget();
                    if (pcl instanceof PropertyChangeListenerProxy) {
                        PropertyChangeListenerProxy pclp = (PropertyChangeListenerProxy) pcl;
                        if (pclp.getPropertyName().equals(event.getPropertyName())) {
                            pclp.propertyChange(event);
                        }
                    } else {
                        long start = System.currentTimeMillis();
                        pcl.propertyChange(event);
                        start = System.currentTimeMillis() - start;
                        if (start >= 100) {
                            Logger.getLogger(EventReceiver.class.getName()).log(Level.INFO, "Slow processing of propertyChange ({0} ms) for target {1}", new Object[]{start, pcl.toString()});
                        }
                    }
                    dEvent.processed();
                }
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(EventReceiver.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
