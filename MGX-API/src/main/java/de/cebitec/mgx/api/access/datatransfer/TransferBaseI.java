package de.cebitec.mgx.api.access.datatransfer;

import de.cebitec.mgx.pevents.ParallelPropertyChangeSupport;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 *
 * @author sj
 */
public abstract class TransferBaseI {

    private final PropertyChangeSupport pcs;
    public static final String NUM_ELEMENTS_SENT = "numElementsSent";
    public static final String NUM_ELEMENTS_RECEIVED = "numElementsReceived";
    public static final String TRANSFER_FAILED = "transferFailed";
    public static final String TRANSFER_COMPLETED = "transferCompleted";

    public TransferBaseI() {
        this.pcs = new ParallelPropertyChangeSupport(this);
    }

    protected void fireTaskChange(String propName, long total_elements) {
        pcs.firePropertyChange(propName, 0, total_elements);
    }

    public void addPropertyChangeListener(PropertyChangeListener p) {
        pcs.addPropertyChangeListener(p);
    }

    public void removePropertyChangeListener(PropertyChangeListener p) {
        pcs.removePropertyChangeListener(p);
    }
}
