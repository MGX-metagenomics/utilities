/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.api.model;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.pevents.ParallelPropertyChangeSupport;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;

/**
 *
 * @author sjaenick
 */
public abstract class MGXDataModelBase<T extends MGXDataModelBaseI<T>> implements MGXDataModelBaseI<T> {

    private final MGXMasterI master;
    private final DataFlavor dataflavor;
    private final PropertyChangeSupport pcs = new ParallelPropertyChangeSupport(this, true);
    //
    private String managedState = OBJECT_MANAGED;

    public MGXDataModelBase(MGXMasterI master, DataFlavor dataFlavor) {
        this.master = master;
        this.dataflavor = dataFlavor;
    }

    @Override
    public final MGXMasterI getMaster() {
        return master;
    }

    @Override
    public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[]{dataflavor};
    }

    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return flavor != null && flavor.equals(dataflavor);
    }

    @Override
    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        if (isDataFlavorSupported(flavor)) {
            return this;
        } else {
            throw new UnsupportedFlavorException(flavor);
        }
    }

    @Override
    public void modified() {
        if (managedState.equals(OBJECT_DELETED)) {
            throw new RuntimeException("Invalid object state, cannot modify deleted object.");
        }
        firePropertyChange(ModelBaseI.OBJECT_MODIFIED, 1, 2);
    }

    @Override
    public void deleted() {
        if (managedState.equals(OBJECT_DELETED)) {
            throw new RuntimeException("Invalid object state, cannot delete deleted object.");
        }
        firePropertyChange(ModelBaseI.OBJECT_DELETED, 0, 1);
        managedState = OBJECT_DELETED;
    }

    @Override
    public final boolean isDeleted() {
        return managedState.equals(OBJECT_DELETED);
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }

    @Override
    public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        PropertyChangeEvent evt = new PropertyChangeEvent(this, propertyName, oldValue, newValue);
        firePropertyChange(evt);
    }

    @Override
    public void firePropertyChange(String propertyName, int oldValue, int newValue) {
        PropertyChangeEvent evt = new PropertyChangeEvent(this, propertyName, oldValue, newValue);
        firePropertyChange(evt);
        //pcs.firePropertyChange(propertyName, oldValue, newValue);
    }

    @Override
    public void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {
        PropertyChangeEvent evt = new PropertyChangeEvent(this, propertyName, oldValue, newValue);
        firePropertyChange(evt);
    }

    @Override
    public void firePropertyChange(PropertyChangeEvent event) {
        pcs.firePropertyChange(event);
    }

}
