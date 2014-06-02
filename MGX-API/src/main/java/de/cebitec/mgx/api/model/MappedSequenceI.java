/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.cebitec.mgx.api.model;

import de.cebitec.mgx.api.MGXMasterI;
import java.awt.datatransfer.DataFlavor;

/**
 *
 * @author sj
 */
public abstract class MappedSequenceI extends LocationBase<MappedSequenceI> {
    //
    public static final DataFlavor DATA_FLAVOR = new DataFlavor(MappedSequenceI.class, "MappedSequenceI");

    public MappedSequenceI(MGXMasterI m, int start, int stop) {
        super(m, start, stop, DATA_FLAVOR);
    }

    public abstract long getSeqId();

    public abstract int getIdentity();

    @Override
    public abstract int compareTo(MappedSequenceI o);

    @Override
    public abstract boolean equals(Object obj);

    @Override
    public abstract int hashCode();

    @Override
    public abstract String toString();
    
}
