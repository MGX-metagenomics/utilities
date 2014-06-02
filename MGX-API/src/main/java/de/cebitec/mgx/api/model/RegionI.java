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
public abstract class RegionI extends LocationBase<RegionI> {

    public RegionI(MGXMasterI m, int start, int stop, DataFlavor df) {
        super(m, start, stop, df);
    }

    public abstract long getReferenceId();

    public abstract void setReference(Long reference_id);

    public abstract String getName();

    public abstract void setName(String name);

    public abstract String getDescription();

    public abstract void setDescription(String description);

    public abstract int getLength();

    public abstract boolean isFwdStrand();

    /**
     * @return 1, 2, 3, -1, -2, -3 depending on the reading frame of the feature
     */
    public abstract int getFrame();

    @Override
    public abstract int compareTo(RegionI o);
    
}
