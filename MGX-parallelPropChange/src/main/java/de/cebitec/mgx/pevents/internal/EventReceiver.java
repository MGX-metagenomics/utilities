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

    private static boolean DEBUG = true;

    public EventReceiver(BlockingQueue<DistributionEvent> in, int id) {
        this.in = in;
        this.id = id;
    }

    public void shutDown() {
        exit = true;
    }

    @Override
    public void run() {
        while (!exit) {
            DistributionEvent dEvent = null;
            try {
                dEvent = in.poll(2, TimeUnit.MILLISECONDS);
            } catch (InterruptedException ex) {
                Logger.getLogger(EventReceiver.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            if (dEvent != null) {
                PropertyChangeEvent event = dEvent.getEvent();
                PropertyChangeListener target = dEvent.getTarget();

                if (DEBUG) {
                    //
                    // additional test if the target listener has been removed before the event was delivered
                    //
                    PropertyChangeListener[] propertyChangeListeners = dEvent.getSource().getPropertyChangeListeners();
                    boolean stillThere = false;
                    for (PropertyChangeListener pcl : propertyChangeListeners) {
                        if (pcl.equals(target)) {
                            stillThere = true;
                            break;
                        }
                    }
                    if (!stillThere) {
                        Logger.getLogger(EventReceiver.class.getName()).log(Level.INFO, "target listener {0} was removed before event could be delivered", target);
                    }
                }

                if (target instanceof PropertyChangeListenerProxy) {
                    PropertyChangeListenerProxy pclp = (PropertyChangeListenerProxy) target;
                    if (pclp.getPropertyName().equals(event.getPropertyName())) {
                        pclp.propertyChange(event);
                    }
                } else {
                    long start = System.currentTimeMillis();
                    target.propertyChange(event);
                    start = System.currentTimeMillis() - start;
                    if (start >= 100) {
                        Logger.getLogger(EventReceiver.class.getName()).log(Level.INFO, "Slow processing of propertyChange {0} ({1} ms) for target {2}", new Object[]{event.getPropertyName(), start, target.toString()});
                    }
                }
                dEvent.delivered();
            }
        }
    }

}
