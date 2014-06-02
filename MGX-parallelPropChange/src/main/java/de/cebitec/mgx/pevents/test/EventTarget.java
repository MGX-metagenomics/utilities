/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.pevents.test;

import de.cebitec.mgx.pevents.internal.EventReceiver;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sj
 */
public class EventTarget implements PropertyChangeListener {

    private final int i;
    private int count = 0;

    public EventTarget(int sleepTime) {
        this.i = sleepTime;
    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {

        try {
            Thread.sleep(0, i);
        } catch (InterruptedException ex) {
            Logger.getLogger(EventReceiver.class.getName()).log(Level.SEVERE, null, ex);
            assert false;
        }
        synchronized (this) {
            count++;
        }
    }

    public int getCount() {
        return count;
    }
}
