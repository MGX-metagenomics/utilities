package de.cebitec.mgx.api.visualization.filter;

import de.cebitec.mgx.api.groups.VisualizationGroupI;
import de.cebitec.mgx.api.misc.DistributionI;
import de.cebitec.mgx.api.misc.Pair;
import de.cebitec.mgx.api.model.AttributeI;
import java.util.List;
import java.util.Map.Entry;

/**
 *
 * @author sjaenick
 */
public class ToFractionFilter implements VisFilterI<DistributionI> {

    @Override
    public List<Pair<VisualizationGroupI, DistributionI>> filter(List<Pair<VisualizationGroupI, DistributionI>> dists) {
        for (Pair<VisualizationGroupI, DistributionI> pair : dists) {
            convertSingleDistribution(pair.getSecond());
        }
        return dists;
    }

    private DistributionI convertSingleDistribution(DistributionI dist) {

        // sum up
        long total = dist.getTotalClassifiedElements();

        for (Entry<AttributeI, Number> e : dist.entrySet()) {
            //e.setValue((double)e.getValue().longValue() / total);
            dist.put(e.getKey(), (double)e.getValue().longValue() / total);
        }
        
        return dist;
    }
}
