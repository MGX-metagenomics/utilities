package de.cebitec.mgx.api;

import de.cebitec.gpms.rest.RESTMembershipI;
import de.cebitec.mgx.api.access.AccessBaseI;
import de.cebitec.mgx.api.access.AttributeAccessI;
import de.cebitec.mgx.api.access.DNAExtractAccessI;
import de.cebitec.mgx.api.access.FileAccessI;
import de.cebitec.mgx.api.access.HabitatAccessI;
import de.cebitec.mgx.api.access.JobAccessI;
import de.cebitec.mgx.api.access.MappingAccessI;
import de.cebitec.mgx.api.access.ObservationAccessI;
import de.cebitec.mgx.api.access.ReferenceAccessI;
import de.cebitec.mgx.api.access.SampleAccessI;
import de.cebitec.mgx.api.access.SeqRunAccessI;
import de.cebitec.mgx.api.access.SequenceAccessI;
import de.cebitec.mgx.api.access.StatisticsAccessI;
import de.cebitec.mgx.api.access.TaskAccessI;
import de.cebitec.mgx.api.access.TermAccessI;
import de.cebitec.mgx.api.access.ToolAccessI;
import de.cebitec.mgx.api.model.AttributeTypeI;
import de.cebitec.mgx.api.model.MGXDataModelBaseI;
import de.cebitec.mgx.api.model.ModelBaseI;
import de.cebitec.mgx.pevents.ParallelPropertyChangeSupport;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.util.logging.Level;

/**
 *
 * @author sjaenick
 */
public abstract class MGXMasterI implements ModelBaseI<MGXMasterI> {

    public static final DataFlavor DATA_FLAVOR = new DataFlavor(MGXMasterI.class, "MGXMasterI");
    //
    private final PropertyChangeSupport pcs = new ParallelPropertyChangeSupport(this);
    private String managedState = OBJECT_MANAGED;
    
    public MGXMasterI() {
        //super(null, dataflavor);
    }

    public abstract RESTMembershipI getMembership();

    public abstract String getProject();

    public abstract String getLogin();

    public abstract void log(Level lvl, String msg);

    public abstract HabitatAccessI Habitat();

    public abstract SampleAccessI Sample();

    public abstract DNAExtractAccessI DNAExtract();

    public abstract SeqRunAccessI SeqRun();

    public abstract ToolAccessI Tool();

    public abstract JobAccessI Job();

    public abstract AttributeAccessI Attribute();

    public abstract AccessBaseI<AttributeTypeI> AttributeType();

    public abstract ObservationAccessI Observation();

    public abstract SequenceAccessI Sequence();

    public abstract TermAccessI Term();

    public abstract FileAccessI File();

    public abstract ReferenceAccessI Reference();

    public abstract MappingAccessI Mapping();

    public abstract StatisticsAccessI Statistics();

    public abstract <T extends MGXDataModelBaseI<T>> TaskAccessI<T> Task();

    @Override
    public final void modified() {
        if (managedState.equals(OBJECT_DELETED)) {
            throw new RuntimeException("Invalid object state, cannot modify deleted object.");
        }
        firePropertyChange(ModelBaseI.OBJECT_MODIFIED, 1, 2);
    }

    @Override
    public final void deleted() {
        if (managedState.equals(OBJECT_DELETED)) {
            throw new RuntimeException("Invalid object state, cannot delete deleted object.");
        }
        firePropertyChange(ModelBaseI.OBJECT_DELETED, 0, 1);
        managedState = OBJECT_DELETED;
    }

    @Override
    public final void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    @Override
    public final void removePropertyChangeListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }

    @Override
    public final void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        PropertyChangeEvent evt = new PropertyChangeEvent(this, propertyName, oldValue, newValue);
        firePropertyChange(evt);
    }

    @Override
    public final void firePropertyChange(String propertyName, int oldValue, int newValue) {
        PropertyChangeEvent evt = new PropertyChangeEvent(this, propertyName, oldValue, newValue);
        firePropertyChange(evt);
        //pcs.firePropertyChange(propertyName, oldValue, newValue);
    }

    @Override
    public final void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {
        PropertyChangeEvent evt = new PropertyChangeEvent(this, propertyName, oldValue, newValue);
        firePropertyChange(evt);
    }

    @Override
    public final void firePropertyChange(PropertyChangeEvent event) {
        pcs.firePropertyChange(event);
    }

    @Override
    public final DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[]{DATA_FLAVOR};
    }

    @Override
    public final boolean isDataFlavorSupported(DataFlavor flavor) {
        return flavor != null && flavor.equals(DATA_FLAVOR);
    }

    @Override
    public final Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        if (isDataFlavorSupported(flavor)) {
            return this;
        } else {
            throw new UnsupportedFlavorException(flavor);
        }
    }

}
