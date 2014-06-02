/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.cebitec.mgx.api.access;

import de.cebitec.mgx.api.model.AttributeTypeI;
import de.cebitec.mgx.api.model.JobI;
import de.cebitec.mgx.api.model.SeqRunI;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author sj
 */
public interface SeqRunAccessI extends AccessBaseI<SeqRunI> {

    public Map<JobI, Set<AttributeTypeI>> getJobsAndAttributeTypes(SeqRunI run);

    public Iterator<SeqRunI> ByExtract(long extract_id);
    
}
