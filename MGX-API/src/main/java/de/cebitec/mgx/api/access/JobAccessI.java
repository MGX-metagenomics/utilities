/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.api.access;

import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.misc.TaskI;
import de.cebitec.mgx.api.model.JobI;
import de.cebitec.mgx.api.model.JobParameterI;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.api.model.ToolI;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author sj
 */
public interface JobAccessI  {

    public JobI create(ToolI tool, SeqRunI seqrun, Collection<JobParameterI> params);

    public JobI fetch(long id);

    public Iterator<JobI> fetchall();

    public void update(JobI obj);

    public TaskI delete(JobI obj);

    public List<JobI> BySeqRun(SeqRunI run);

    public List<JobI> ByAttributeTypeAndSeqRun(long atype_id, SeqRunI run);

    public String getErrorMessage(JobI job);

    public TaskI restart(JobI job) throws MGXException;

    public boolean cancel(JobI job) throws MGXException;

    public boolean verify(JobI job) throws MGXException;

    public boolean execute(JobI job) throws MGXException;

}
