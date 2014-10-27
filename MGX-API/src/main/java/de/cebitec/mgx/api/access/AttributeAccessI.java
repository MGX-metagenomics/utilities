/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.api.access;

import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.misc.DistributionI;
import de.cebitec.mgx.api.model.AttributeI;
import de.cebitec.mgx.api.model.AttributeTypeI;
import de.cebitec.mgx.api.model.JobI;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.api.model.SequenceI;
import de.cebitec.mgx.api.model.tree.TreeI;
import java.util.Iterator;

/**
 *
 * @author sj
 */
public interface AttributeAccessI extends AccessBaseI<AttributeI> {

    public Iterator<AttributeI> BySeqRun(final SeqRunI seqrun) throws MGXException;

    public DistributionI getDistribution(AttributeTypeI attrType, JobI job) throws MGXException;

    public TreeI<Long> getHierarchy(AttributeTypeI attrType, JobI job) throws MGXException;

    public Iterator<SequenceI> search(String term, boolean exact, SeqRunI[] targets) throws MGXException;
    
    public Iterator<String> find(String term, SeqRunI[] targets) throws MGXException;
}
