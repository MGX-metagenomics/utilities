/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.api.model;

import de.cebitec.mgx.api.MGXMasterI;
import java.awt.datatransfer.DataFlavor;
import org.apache.commons.math3.util.FastMath;

/**
 *
 * @author sjaenick
 */
public abstract class LocationBase<T> extends Identifiable<T> {

    private final int start;
    private final int stop;
    private final int min;
    private final int max;

    public LocationBase(MGXMasterI m, int start, int stop, DataFlavor df) {
        super(m, df);
        this.start = start;
        this.stop = stop;
        min = FastMath.min(start, stop);
        max = FastMath.max(start, stop);
    }

    public int getStart() {
        return start;
    }

    public int getStop() {
        return stop;
    }

    public int getMax() {
        return max;
    }

    public int getMin() {
        return min;
    }
}
