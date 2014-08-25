/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.api.access;

import de.cebitec.mgx.api.model.MappedSequenceI;
import de.cebitec.mgx.api.model.MappingI;
import java.util.Iterator;
import java.util.UUID;

/**
 *
 * @author sj
 */
public abstract class MappingAccessI implements AccessBaseI<MappingI> {

    public abstract Iterator<MappedSequenceI> byReferenceInterval(UUID uuid, int from, int to);

    public abstract UUID openMapping(long id);
    
    public abstract void closeMapping(UUID uuid);

    public abstract Iterator<MappingI> ByReference(long id);

    public abstract Iterator<MappingI> BySeqRun(long id);

    public abstract long getMaxCoverage(UUID sessionUUID);
}
