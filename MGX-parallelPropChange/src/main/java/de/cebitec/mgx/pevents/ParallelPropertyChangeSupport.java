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
    private final boolean traceErrors;
    private final boolean returnImmediate;
    private static final Logger LOG = Logger.getLogger(ParallelPropertyChangeSupport.class.getSimpleName());
    private final Object source;

    public ParallelPropertyChangeSupport(Object sourceBean) {
        this(sourceBean, true, false);
    }

    public ParallelPropertyChangeSupport(Object sourceBean, boolean traceErrors) {
        this(sourceBean, traceErrors, false);
    }

    public ParallelPropertyChangeSupport(Object sourceBean, boolean traceErrors, boolean returnImmediate) {
        super(sourceBean);
        this.source = sourceBean;
        numInstances++;
        this.traceErrors = traceErrors;
        this.returnImmediate = returnImmediate;
    }

    @Override
    public final synchronized void addPropertyChangeListener(PropertyChangeListener listener) {
        if (listener == null) {
            if (traceErrors) {
                throw new IllegalArgumentException("null PropertyChangeListener added");
            }
            return;
        }
        if (traceErrors) {
            for (PropertyChangeListener pcl : getPropertyChangeListeners()) {
                if (pcl == listener) {
                    LOG.log(Level.INFO, "Duplicate PropertyChangeListener added: {0}", listener.getClass().getSimpleName());
//                    throw new IllegalArgumentException("Duplicate PropertyChangeListener added");
                }
            }
        }
        if (distributor == null) {
            startDistributor();
        }
        super.addPropertyChangeListener(listener);
    }

    @Override
    public final synchronized void removePropertyChangeListener(PropertyChangeListener listener) {
        if (listener == null) {
            if (traceErrors) {
                throw new IllegalArgumentException("null PropertyChangeListener removed");
            }
            return;
        }
        if (traceErrors) {
            boolean found = false;
            PropertyChangeListener[] propertyChangeListeners = getPropertyChangeListeners();
            for (PropertyChangeListener propertyChangeListener : propertyChangeListeners) {
                if (propertyChangeListener.equals(listener)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                LOG.log(Level.INFO, "PropertyChangeListener {0} cannot be removed because it is not a registered listener.", listener.getClass().getSimpleName());
                for (PropertyChangeListener propertyChangeListener : propertyChangeListeners) {
                    LOG.log(Level.INFO, "  listener: {0}", propertyChangeListener.getClass().getSimpleName());
                }
                //throw new IllegalArgumentException("PropertyChangeListener " + listener + " cannot be removed because it is not a registered listener.");
            }
        }
        super.removePropertyChangeListener(listener);
    }

    @Override
    public final void firePropertyChange(PropertyChangeEvent event) {
        PropertyChangeListener[] listeners = getPropertyChangeListeners();
        if (listeners.length == 0) {
            return;
        }

        AccumulatedEvent aEvent = new AccumulatedEvent(listeners, event);
        distributor.distributeEvent(aEvent);
        if (!returnImmediate) {
            aEvent.await();
        }
    }

    private synchronized void startDistributor() {
        assert distributor == null;
        distributor = new EventDistributor();
        Thread thread = new Thread(distributor, "AsyncPCS-Distributor");
        thread.setDaemon(true);
        thread.start();
    }

    @Override
    public final synchronized void close() {
        numInstances--;
        if (numInstances == 0 && distributor != null) {
            distributor.close();
            distributor = null;
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        close();
        if (traceErrors) {
            PropertyChangeListener[] propertyChangeListeners = getPropertyChangeListeners();
            if (propertyChangeListeners != null && propertyChangeListeners.length > 0) {
                LOG.log(Level.INFO, "Removing PropertyChangeSupport for source {0} with remaining listeners:", source.getClass().getSimpleName());
                for (PropertyChangeListener pcl : propertyChangeListeners) {
                    LOG.log(Level.INFO, "  {0}", pcl.getClass().getSimpleName());
                }
            }
        }
    }
}
