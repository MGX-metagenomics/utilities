/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.pevents.internal;

import de.cebitec.mgx.pevents.ParallelPropertyChangeSupport;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.concurrent.CountDownLatch;

/**
 *
 * @author sjaenick
 */
public class AccumulatedEvent extends EventBase {

    private final PropertyChangeListener[] listeners;
    private final ParallelPropertyChangeSupport source;

    public AccumulatedEvent(ParallelPropertyChangeSupport source, PropertyChangeListener[] listeners, PropertyChangeEvent event) {
        super(new CountDownLatch(1), event);
        this.listeners = listeners;
        this.source = source;
    }
    
    public ParallelPropertyChangeSupport getSource() {
        return source;
    }

    public PropertyChangeListener[] getListeners() {
        return listeners;
    }

}
