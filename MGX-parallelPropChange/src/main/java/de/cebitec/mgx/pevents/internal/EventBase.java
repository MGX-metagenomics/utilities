/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.pevents.internal;

import java.beans.PropertyChangeEvent;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sjaenick
 */
public abstract class EventBase {

    private final CountDownLatch latch;
    private final PropertyChangeEvent event;

    public EventBase(CountDownLatch latch, PropertyChangeEvent event) {
        this.latch = latch;
        this.event = event;
    }

    public PropertyChangeEvent getEvent() {
        return event;
    }

    public void processed() {
        latch.countDown();
    }

    public void await() {
        try {
            latch.await();
        } catch (InterruptedException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        }
    }
}
