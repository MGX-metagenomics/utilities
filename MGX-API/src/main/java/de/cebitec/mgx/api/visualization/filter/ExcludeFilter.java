package de.cebitec.mgx.api.visualization.filter;

import de.cebitec.mgx.api.groups.VisualizationGroupI;
import de.cebitec.mgx.api.misc.DistributionI;
import de.cebitec.mgx.api.misc.Pair;
import de.cebitec.mgx.api.model.AttributeI;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 *
 * @author sj
 */
public class ExcludeFilter implements VisFilterI<DistributionI> {

    private final Set<AttributeI> blacklist;

    public ExcludeFilter(Set<AttributeI> blacklist) {
        this.blacklist = Collections.unmodifiableSet(blacklist);
    }

    @Override
    public List<Pair<VisualizationGroupI, DistributionI>> filter(List<Pair<VisualizationGroupI, DistributionI>> in) {
        for (Pair<VisualizationGroupI, DistributionI> p : in) {
            filterDist(p.getSecond());
        }
        return in;
    }

    public void filterDist(DistributionI d) {
        for (AttributeI x : blacklist) {
            if (d.containsKey(x)) {
                d.remove(x);
            }
        }
    }
}
