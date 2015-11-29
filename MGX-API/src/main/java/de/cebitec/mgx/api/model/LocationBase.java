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
public abstract class LocationBase<T extends LocationBase<T>> extends Identifiable<T> {

    private final int start;
    private final int stop;
    private int min = -1;
    private int max = -1;

    public LocationBase(MGXMasterI m, int start, int stop, DataFlavor df) {
        super(m, df);
        this.start = start;
        this.stop = stop;

    }

    public final int getStart() {
        return start;
    }

    public final int getStop() {
        return stop;
    }

    public final int getMax() {
        if (max == -1) {
            max = FastMath.max(start, stop);
        }
        return max;
    }

    public final int getMin() {
        if (min == -1) {
            min = FastMath.min(start, stop);
        }
        return min;
    }
}
