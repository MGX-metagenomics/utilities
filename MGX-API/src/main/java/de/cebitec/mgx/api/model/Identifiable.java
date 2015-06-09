package de.cebitec.mgx.api.model;

import de.cebitec.mgx.api.MGXMasterI;
import java.awt.datatransfer.DataFlavor;

/**
 *
 * @author sj
 */
public abstract class Identifiable<T> extends ModelBase<T> {

    public final static long INVALID_IDENTIFIER = -1;
    protected long id = INVALID_IDENTIFIER;

    public Identifiable(MGXMasterI master, DataFlavor df) {
        super(master, df);
    }

    public final void setId(long id) {
        this.id = id;
    }

    public final long getId() {
        return id;
    }
}
