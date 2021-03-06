/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.pevents.test;

import de.cebitec.mgx.pevents.ParallelPropertyChangeSupport;
import java.beans.PropertyChangeListener;

/**
 *
 * @author sj
 */
public class Sender {

    private final ParallelPropertyChangeSupport pcs;

    public Sender(String source) {
        pcs = new ParallelPropertyChangeSupport(source);
//        assert pcs.getPropertyChangeListeners().length == 0;
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }

    public PropertyChangeListener[] getPropertyChangeListeners() {
        return pcs.getPropertyChangeListeners();
    }

    public void firePropertyChange() {
        pcs.firePropertyChange("TEST", 0, 1);
    }
}
