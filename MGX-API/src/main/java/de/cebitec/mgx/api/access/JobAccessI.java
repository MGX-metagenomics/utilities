/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.api.access;

import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.misc.TaskI;
import de.cebitec.mgx.api.model.JobI;
import de.cebitec.mgx.api.model.SeqRunI;
import java.util.List;

/**
 *
 * @author sj
 */
public interface JobAccessI extends AccessBaseI<JobI> {

    public List<JobI> BySeqRun(SeqRunI run);

    public List<JobI> ByAttributeTypeAndSeqRun(long atype_id, SeqRunI run);

    public String getErrorMessage(JobI job);

    public TaskI restart(JobI job) throws MGXException;

    public boolean cancel(JobI job) throws MGXException;

    public boolean verify(JobI job) throws MGXException;

    public boolean execute(JobI job) throws MGXException;

}
