/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.cebitec.mgx.api.groups;

import de.cebitec.mgx.api.groups.ConflictingJobsException;
import de.cebitec.mgx.api.groups.VisualizationGroupI;
import de.cebitec.mgx.api.misc.AttributeRank;
import de.cebitec.mgx.api.misc.DistributionI;
import de.cebitec.mgx.api.misc.Pair;
import de.cebitec.mgx.api.model.tree.TreeI;
import de.cebitec.mgx.api.visualization.ConflictResolver;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author sj
 */
public interface VGroupManagerI extends PropertyChangeListener {
    String VISGROUP_NUM_CHANGED = "vgNumChanged";
    String VISGROUP_SELECTION_CHANGED = "vgSelectionChanged";

    void addPropertyChangeListener(PropertyChangeListener p);

    VisualizationGroupI createGroup();

    List<VisualizationGroupI> getActiveGroups();

    Collection<VisualizationGroupI> getAllGroups();

    List<Pair<VisualizationGroupI, DistributionI>> getDistributions() throws ConflictingJobsException;

    List<Pair<VisualizationGroupI, TreeI<Long>>> getHierarchies();

    VisualizationGroupI getSelectedGroup();

    boolean hasGroup(String name);

    void registerResolver(ConflictResolver cr);

    void removeGroup(VisualizationGroupI vg);

    void removePropertyChangeListener(PropertyChangeListener p);

    boolean selectAttributeType(AttributeRank rank, String aType);

    void setSelectedGroup(VisualizationGroupI group);
    
}
