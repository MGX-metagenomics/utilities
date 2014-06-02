package de.cebitec.mgx.api.visualization.filter;

import de.cebitec.mgx.api.groups.VisualizationGroupI;
import de.cebitec.mgx.api.misc.Pair;
import java.util.List;

/**
 *
 * @author sjaenick
 */
public interface VisFilterI<T> {
    
    public List<Pair<VisualizationGroupI, T>> filter(List<Pair<VisualizationGroupI, T>> in);
    
}
