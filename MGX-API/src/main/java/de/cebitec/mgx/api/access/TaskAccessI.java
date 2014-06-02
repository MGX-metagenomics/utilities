/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.api.access;

import de.cebitec.mgx.api.misc.TaskI;
import de.cebitec.mgx.api.model.ModelBase;
import java.util.UUID;

/**
 *
 * @author sj
 */
public interface TaskAccessI<T extends ModelBase> {

    public abstract TaskI get(T obj, UUID taskId, TaskI.TaskType tt);

    public abstract TaskI refresh(TaskI origTask);
}
