/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.api.access;

import de.cebitec.mgx.api.misc.DistributionI;
import de.cebitec.mgx.api.model.AttributeI;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.api.model.SequenceI;
import de.cebitec.mgx.api.model.tree.TreeI;
import java.util.Iterator;

/**
 *
 * @author sj
 */
public interface AttributeAccessI extends AccessBaseI<AttributeI> {

    public Iterator<AttributeI> BySeqRun(final long seqrun_id);

    public DistributionI getDistribution(long attrType_id, long job_id);

    public TreeI<Long> getHierarchy(long attrType_id, long job_id);

    public SequenceI[] search(String term, boolean exact, SeqRunI[] targets);
}
