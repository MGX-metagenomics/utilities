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
import java.io.Serial;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sj
 */
public class ParallelPropertyChangeSupport extends PropertyChangeSupport implements AutoCloseable {
    
    @Serial
    private static final long serialVersionUID = 6401253773779951803L;

    private volatile static EventDistributor distributor = null;
    private volatile static AtomicInteger numInstances = new AtomicInteger(0);
    private volatile boolean closed = false;
    private final boolean traceErrors;
    private final boolean returnImmediate;
    private static final Logger LOG = Logger.getLogger(ParallelPropertyChangeSupport.class.getSimpleName());
    private final transient Object source;
    private final transient Object ADD_REMOVE_LOCK = new Object();

    public ParallelPropertyChangeSupport(Object sourceBean) {
        this(sourceBean, true, false);
    }

    public ParallelPropertyChangeSupport(Object sourceBean, boolean traceErrors) {
        this(sourceBean, traceErrors, false);
    }

    public ParallelPropertyChangeSupport(Object sourceBean, boolean traceErrors, boolean returnImmediate) {
        super(sourceBean);
        this.source = sourceBean;
        numInstances.addAndGet(1);
        this.traceErrors = traceErrors;
        this.returnImmediate = returnImmediate;
    }

    @Override
    public final void addPropertyChangeListener(PropertyChangeListener listener) {
        synchronized (ADD_REMOVE_LOCK) {
            if (traceErrors) {
                for (PropertyChangeListener pcl : getPropertyChangeListeners()) {
                    if (pcl == listener) {
                        LOG.log(Level.INFO, "Duplicate PropertyChangeListener {0} added to source {1}", new Object[]{listener.getClass().getSimpleName(), source.getClass().getSimpleName()});
                    }
                }
            }

            if (listener instanceof PropertyChangeListenerProxy) {
                PropertyChangeListenerProxy proxy = (PropertyChangeListenerProxy) listener;
                super.addPropertyChangeListener(proxy.getPropertyName(), new WeakPropertyChangeListener(proxy.getListener()));
            } else {
                super.addPropertyChangeListener(new WeakPropertyChangeListener(listener));
            }
        }
        if (distributor == null) {
            startDistributor();
        }
    }

    @Override
    public final void removePropertyChangeListener(PropertyChangeListener listener) {
        synchronized (ADD_REMOVE_LOCK) {
            if (traceErrors) {
                boolean found = false;
                PropertyChangeListener[] propertyChangeListeners = super.getPropertyChangeListeners();
                for (PropertyChangeListener propertyChangeListener : propertyChangeListeners) {
                    if (propertyChangeListener instanceof WeakPropertyChangeListener) {
                        WeakPropertyChangeListener wPCL = (WeakPropertyChangeListener) propertyChangeListener;
                        if (wPCL.target.get() == listener) {
                            found = true;
                            break;
                        }
                    } else {
                        assert false;
                    }
                }
                if (!found) {
                    LOG.log(Level.INFO, "PropertyChangeListener {0} cannot be removed from source {1} because it is not a registered listener.", new Object[]{listener, source});
                    for (PropertyChangeListener propertyChangeListener : propertyChangeListeners) {
                        LOG.log(Level.INFO, "  listener: {0}", propertyChangeListener);
                    }
                }
            }

            PropertyChangeListener[] propertyChangeListeners = super.getPropertyChangeListeners();
            for (PropertyChangeListener propertyChangeListener : propertyChangeListeners) {
                if (propertyChangeListener instanceof WeakPropertyChangeListener) {
                    WeakPropertyChangeListener wPCL = (WeakPropertyChangeListener) propertyChangeListener;
                    if (wPCL.target.get() == listener) {
                        super.removePropertyChangeListener(wPCL);
                        return;
                    }
                } else {
                    assert false;
                }
            }

        }
    }

    @Override
    public PropertyChangeListener[] getPropertyChangeListeners() {
        synchronized (ADD_REMOVE_LOCK) {
            PropertyChangeListener[] listeners = super.getPropertyChangeListeners();
            if (listeners.length == 0) {
                return listeners;
            }
            List<PropertyChangeListener> alive = new ArrayList<>(listeners.length);
            for (PropertyChangeListener listener : listeners) {
                if (listener instanceof WeakPropertyChangeListener) {
                    PropertyChangeListener pcl = ((WeakPropertyChangeListener) listener).target.get();
                    if (pcl != null) {
                        alive.add(pcl);
                    } else {
                        // remove garbage-collected orphan
                        super.removePropertyChangeListener(listener);
                    }
                } else {
                    assert false;
                }
            }
            return alive.toArray(new PropertyChangeListener[]{});
        }
    }

    @Override
    public PropertyChangeListener[] getPropertyChangeListeners(String propertyName) {
        synchronized (ADD_REMOVE_LOCK) {
            PropertyChangeListener[] listeners = super.getPropertyChangeListeners(propertyName);
            if (listeners.length == 0) {
                return listeners;
            }
            List<PropertyChangeListener> alive = new ArrayList<>(listeners.length);
            for (PropertyChangeListener listener : listeners) {
                if (listener instanceof WeakPropertyChangeListener) {
                    PropertyChangeListener pcl = ((WeakPropertyChangeListener) listener).target.get();
                    if (pcl != null) {
                        alive.add(pcl);
                    } else {
                        // remove garbage-collected orphan
                        super.removePropertyChangeListener(listener);
                    }
                } else {
                    assert false;
                }
            }
            return alive.toArray(new PropertyChangeListener[]{});
        }
    }

    @Override
    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        synchronized (ADD_REMOVE_LOCK) {
            PropertyChangeListener[] listeners = super.getPropertyChangeListeners();
            for (PropertyChangeListener pcl : listeners) {
                if (pcl instanceof WeakPropertyChangeListener) {
                    WeakPropertyChangeListener wpcl = ((WeakPropertyChangeListener) pcl);
                    if (wpcl.target.get() == listener) {
                        super.removePropertyChangeListener(propertyName, wpcl);
                        return;
                    }
                } else {
                    assert false;
                }
            }
        }
    }

    @Override
    public final void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        synchronized (ADD_REMOVE_LOCK) {
            super.addPropertyChangeListener(propertyName, new WeakPropertyChangeListener(listener));
        }
    }
    
    private volatile PropertyChangeEvent lastEvent = null;
    private final Semaphore busy = new Semaphore(10);

    @Override
    public final void firePropertyChange(PropertyChangeEvent event) {

        busy.acquireUninterruptibly();
        lastEvent = event;
        
        if (closed) {
            busy.release();
            throw new RuntimeException("PropertyChangeSupport has already been closed.");
        }

        PropertyChangeListener[] listeners = getPropertyChangeListeners();
        PropertyChangeListener[] propNameListeners = getPropertyChangeListeners(event.getPropertyName());

        if (propNameListeners.length > 0) {
            listeners = Arrays.copyOf(listeners, listeners.length + propNameListeners.length);
            System.arraycopy(propNameListeners, 0, listeners, listeners.length, propNameListeners.length);
        }

        if (listeners.length == 0) {
            busy.release();
            return;
        }

        if (distributor.allDeliveryThreadsBlocked()) {
            // we're looping, e.g. because an instance forwards an event
            // generated by another instance; avoid the deadlock and
            // deliver in same thread
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("Blocked, delivering event %s to %s targets on thread %s", event, listeners.length, Thread.currentThread().getName()));
            sb.append(System.lineSeparator());

            for (PropertyChangeListener pcl : listeners) {
                sb.append("    target is ");
                sb.append(pcl);
                sb.append(System.lineSeparator());
            }
            Logger.getLogger(getClass().getSimpleName()).log(Level.INFO, sb.toString());
            Activator.direct(listeners.length);
            super.firePropertyChange(event);
        } else {

            Activator.async(listeners.length);

            AccumulatedEvent aEvent = new AccumulatedEvent(this, listeners, event);
            distributor.distributeEvent(aEvent);
            if (!returnImmediate) {
                aEvent.await();
            }
        }
        busy.release();
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
        
        busy.acquireUninterruptibly(10);
        
        if (!closed) {
            closed = true;
            int remainingInstances = numInstances.addAndGet(-1);
            if (remainingInstances == 0 && distributor != null) {
                distributor.close();
                distributor = null;
            }

            if (traceErrors) {
                PropertyChangeListener[] propertyChangeListeners = getPropertyChangeListeners();
                if (propertyChangeListeners != null && propertyChangeListeners.length > 0) {
                    LOG.log(Level.INFO, "Removing PropertyChangeSupport for source {0} (last event: {1}) with remaining listeners:", new Object[]{source, lastEvent == null ? "null" : lastEvent.getPropertyName()});
                    for (PropertyChangeListener pcl : propertyChangeListeners) {
                        LOG.log(Level.INFO, "  {0}", pcl);
                    }
                }
            }
        }
        
        busy.release(10);
    }

    private final class WeakPropertyChangeListener implements PropertyChangeListener {

        private final WeakReference<PropertyChangeListener> target;

        public WeakPropertyChangeListener(PropertyChangeListener pcl) {
            this.target = new WeakReference<>(pcl);
        }

        @Override
        public final void propertyChange(PropertyChangeEvent evt) {
            PropertyChangeListener pcl = target.get();
            if (pcl != null) {
                pcl.propertyChange(evt);
            } else {
                // reference has been garbage-collected, remove self from subscribers
                //ParallelPropertyChangeSupport.super.removePropertyChangeListener(this);
                ParallelPropertyChangeSupport.this.removePropertyChangeListener(this);
            }
        }

        @Override
        public String toString() {
            return "WeakPropertyChangeListener{" + "target=" + target + '}';
        }
    }
}
