/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.pevents.internal;

import de.cebitec.mgx.pevents.ParallelPropertyChangeSupport;
import java.beans.PropertyChangeListener;

/**
 *
 * @author sj
 */
public class DistributionEvent extends EventBase {

    private final PropertyChangeListener target;
    private final ParallelPropertyChangeSupport source;

    public DistributionEvent(AccumulatedEvent accEvent, PropertyChangeListener target) {
        super(accEvent.getLatch(), accEvent.getEvent());
        this.target = target;
        this.source = accEvent.getSource();
    }

    public ParallelPropertyChangeSupport getSource() {
        return source;
    }

    public PropertyChangeListener getTarget() {
        return target;
    }

}
