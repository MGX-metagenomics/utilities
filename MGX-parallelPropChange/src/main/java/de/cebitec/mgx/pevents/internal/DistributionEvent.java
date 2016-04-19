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
 * @author sj
 */
public class DistributionEvent extends EventBase {

    private final PropertyChangeListener target;
    private final ParallelPropertyChangeSupport source;

    public DistributionEvent(CountDownLatch latch, ParallelPropertyChangeSupport source, PropertyChangeEvent event, PropertyChangeListener target) {
        super(latch, event);
        this.target = target;
        this.source = source;
    }

    public ParallelPropertyChangeSupport getSource() {
        return source;
    }

    public PropertyChangeListener getTarget() {
        return target;
    }

}
