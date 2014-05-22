/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.pevents.test;

import de.cebitec.mgx.pevents.ParallelPropertyChangeSupport;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 *
 * @author sjaenick
 */
public class Forwarder implements PropertyChangeListener {

    private final ParallelPropertyChangeSupport pcs = new ParallelPropertyChangeSupport(this);
    private int cnt = 0;

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        cnt++;
        pcs.firePropertyChange(evt);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    public int getCount() {
        return cnt;
    }
}
