/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.pevents;

import de.cebitec.mgx.pevents.internal.AccumulatedEvent;
import de.cebitec.mgx.pevents.internal.EventDistributor;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sj
 */
public class ParallelPropertyChangeSupport extends PropertyChangeSupport implements AutoCloseable {

    private static EventDistributor distributor = null;
    private static int numInstances = 0;
    private final boolean traceDuplicates;
    private static final Logger LOG = Logger.getLogger(ParallelPropertyChangeSupport.class.getName());

    public ParallelPropertyChangeSupport(Object sourceBean) {
        this(sourceBean, false);
    }

    public ParallelPropertyChangeSupport(Object sourceBean, boolean traceDuplicates) {
        super(sourceBean);
        numInstances++;
        this.traceDuplicates = traceDuplicates;
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        if (listener == null) {
            return;
        }
        if (traceDuplicates) {
            for (PropertyChangeListener pcl : getPropertyChangeListeners()) {
                if (pcl.equals(listener)) {
                    LOG.log(Level.INFO, "Duplicate PropertyChangeListener added: {0}", listener.toString());
                }
            }
        }
        if (distributor == null) {
            startDistributor();
        }
        super.addPropertyChangeListener(listener);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        if (listener == null) {
            return;
        }
        super.removePropertyChangeListener(listener);
    }

    @Override
    public void firePropertyChange(PropertyChangeEvent event) {
        PropertyChangeListener[] listeners = getPropertyChangeListeners();
        if (listeners.length == 0) {
            return;
        }

        AccumulatedEvent aEvent = new AccumulatedEvent(listeners, event);
        distributor.distributeEvent(aEvent);
        aEvent.await();
    }

    private synchronized void startDistributor() {
        assert distributor == null;
        distributor = new EventDistributor();
        Thread thread = new Thread(distributor, "AsyncPCS-Distributor");
        thread.setDaemon(true);
        thread.start();
    }

    @Override
    public synchronized void close() throws Exception {
        numInstances--;
        if (numInstances == 0 && distributor != null) {
            distributor.close();
            distributor = null;
        }
    }

}
