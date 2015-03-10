/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.cebitec.mgx.api.groups;

import de.cebitec.mgx.api.misc.AttributeRank;
import de.cebitec.mgx.api.misc.DistributionI;
import de.cebitec.mgx.api.misc.Triple;
import de.cebitec.mgx.api.model.AttributeI;
import de.cebitec.mgx.api.model.AttributeTypeI;
import de.cebitec.mgx.api.model.JobI;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.api.model.tree.TreeI;
import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author sj
 */
public interface VisualizationGroupI extends PropertyChangeListener {
    String VISGROUP_ACTIVATED = "visgroup_activated";
    String VISGROUP_ATTRTYPE_CHANGED = "vgAttrTypeChange";
    String VISGROUP_CHANGED = "visgroup_changed";
    String VISGROUP_DEACTIVATED = "visgroup_deactivated";
    String VISGROUP_HAS_DIST = "vgHasDist";
    String VISGROUP_RENAMED = "visgroup_renamed";

    void addPropertyChangeListener(PropertyChangeListener p);

    void addSeqRun(final SeqRunI sr);

    void addSeqRuns(final Set<SeqRunI> runs);

    Iterator<AttributeTypeI> getAttributeTypes();

    Color getColor();

    List<Triple<AttributeRank, SeqRunI, Set<JobI>>> getConflicts();
    
    Map<SeqRunI, Set<JobI>> getConflicts(AttributeRank rank);

    DistributionI<Long> getDistribution() throws ConflictingJobsException;

    TreeI<Long> getHierarchy();

    int getId();
    
    void close();

    String getName();

    long getNumSequences();

    Map<SeqRunI, Set<AttributeI>> getSaveSet(List<String> requestedAttrs);

    String getSelectedAttributeType();

    Set<SeqRunI> getSeqRuns();

    boolean isActive();

    @Override
    void propertyChange(PropertyChangeEvent evt);

    void removePropertyChangeListener(PropertyChangeListener p);

    void removeSeqRun(final SeqRunI sr);

    void resolveConflict(AttributeRank rank, SeqRunI sr, JobI j);

    /**
     *
     * @param rank
     * @param attrType
     * @throws ConflictingJobsException
     *
     * promote selection of an attribute type to the group; checks all contained
     * sequencing runs, i.) if they provide the attribute type and ii.) if the
     * attribute type is provided by a single job only. if several jobs are able
     * to provide the corresponding attribute type, a ConflictingJobsException
     * will be raised for resolval of the conflict.
     */
    void selectAttributeType(AttributeRank rank, String attrType) throws ConflictingJobsException;

    void setActive(boolean is_active);

    void setColor(Color color);

    void setName(String name);
    
}
