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
import java.beans.PropertyChangeListenerProxy;
import java.beans.PropertyChangeSupport;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sj
 */
public class ParallelPropertyChangeSupport extends PropertyChangeSupport implements AutoCloseable {

    private static EventDistributor distributor = null;
    private static int numInstances = 0;
    private volatile boolean closed = false;
    private final boolean traceErrors;
    private final boolean returnImmediate;
    private static final Logger LOG = Logger.getLogger(ParallelPropertyChangeSupport.class.getSimpleName());
    private final Object source;
    private final Object ADD_REMOVE_LOCK = new Object();

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
    public final void addPropertyChangeListener(PropertyChangeListener listener) {
        synchronized (ADD_REMOVE_LOCK) {
            if (listener == null && traceErrors) {
                throw new IllegalArgumentException("null PropertyChangeListener added");
            }
            if (traceErrors) {
                for (PropertyChangeListener pcl : getPropertyChangeListeners()) {
                    if (pcl == listener) {
                        LOG.log(Level.INFO, "Duplicate PropertyChangeListener added: {0}", listener.getClass().getSimpleName());
                    }
                }
            }

            if (listener instanceof PropertyChangeListenerProxy) {
                PropertyChangeListenerProxy proxy = (PropertyChangeListenerProxy) listener;
                addPropertyChangeListener(proxy.getPropertyName(), proxy.getListener());
            } else {
                super.addPropertyChangeListener(listener);
            }
        }
        if (distributor == null) {
            startDistributor();
        }
    }

    @Override
    public final void removePropertyChangeListener(PropertyChangeListener listener) {
        synchronized (ADD_REMOVE_LOCK) {
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
                    LOG.log(Level.INFO, "PropertyChangeListener {0} cannot be removed from source {1} because it is not a registered listener.", new Object[]{listener, source});
                    for (PropertyChangeListener propertyChangeListener : propertyChangeListeners) {
                        LOG.log(Level.INFO, "  listener: {0}", propertyChangeListener);
                    }
                    //throw new IllegalArgumentException("PropertyChangeListener " + listener + " cannot be removed because it is not a registered listener.");
                }
            }
            super.removePropertyChangeListener(listener);
        }
    }

    @Override
    public PropertyChangeListener[] getPropertyChangeListeners() {
        synchronized (ADD_REMOVE_LOCK) {
            return super.getPropertyChangeListeners();
        }
    }

    @Override
    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        synchronized (ADD_REMOVE_LOCK) {
            super.removePropertyChangeListener(propertyName, listener);
        }
    }

    @Override
    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        synchronized (ADD_REMOVE_LOCK) {
            super.addPropertyChangeListener(propertyName, listener);
        }
    }

    @Override
    public final void firePropertyChange(PropertyChangeEvent event) {

        if (distributor.allDeliveryThreadsBlocked()) {
            // we're looping, e.g. because an instance forwards an event
            // generated by another instance; avoid the deadlock and
            // deliver in same thread
            Logger.getLogger(getClass().getName()).log(Level.INFO, "Blocked, delivering event {0} to all targets on thread {1}", new Object[]{event, Thread.currentThread().getName()});
            Activator.direct(getPropertyChangeListeners().length);
            super.firePropertyChange(event);
        } else {
            PropertyChangeListener[] listeners = getPropertyChangeListeners();
            PropertyChangeListener[] propNameListeners = getPropertyChangeListeners(event.getPropertyName());

            if (listeners.length == 0 && propNameListeners.length == 0) {
                return;
            }

            if (propNameListeners.length > 0) {
                listeners = Arrays.copyOf(listeners, listeners.length + propNameListeners.length);
                System.arraycopy(propNameListeners, 0, listeners, listeners.length, propNameListeners.length);
            }

            Activator.async(listeners.length);

            AccumulatedEvent aEvent = new AccumulatedEvent(this, listeners, event);
            distributor.distributeEvent(aEvent);
            if (!returnImmediate) {
                aEvent.await();
            }
        }

    }

    private synchronized static void startDistributor() {
        if (distributor == null) {
            distributor = new EventDistributor();
            Thread thread = new Thread(distributor, "AsyncPCS-Distributor");
            thread.setDaemon(true);
            thread.start();
        }
    }

    @Override
    public final synchronized void close() {
        if (!closed) {
            closed = true;
            //System.err.println("close()");
            numInstances--;
            if (numInstances == 0 && distributor != null) {
                distributor.close();
                distributor = null;
            }

            if (traceErrors) {
                PropertyChangeListener[] propertyChangeListeners = getPropertyChangeListeners();
                if (propertyChangeListeners != null && propertyChangeListeners.length > 0) {
                    LOG.log(Level.INFO, "Removing PropertyChangeSupport for source {0} with remaining listeners:", source);
                    for (PropertyChangeListener pcl : propertyChangeListeners) {
                        LOG.log(Level.INFO, "  {0}", pcl.getClass().getSimpleName());
                    }
                }
            }
        }
    }

//    @Override
//    protected synchronized void finalize() throws Throwable {
//        //System.err.println("finalize()");
//        close();
//        if (traceErrors) {
//            PropertyChangeListener[] propertyChangeListeners = getPropertyChangeListeners();
//            if (propertyChangeListeners != null && propertyChangeListeners.length > 0) {
//                LOG.log(Level.INFO, "Removing PropertyChangeSupport for source {0} with remaining listeners:", source);
//                for (PropertyChangeListener pcl : propertyChangeListeners) {
//                    LOG.log(Level.INFO, "  {0}", pcl.getClass().getSimpleName());
//                }
//            }
//        }
//        super.finalize();
//    }
}
