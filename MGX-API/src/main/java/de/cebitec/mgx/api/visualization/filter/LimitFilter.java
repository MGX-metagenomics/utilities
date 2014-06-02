package de.cebitec.mgx.api.visualization.filter;

import de.cebitec.mgx.api.groups.VisualizationGroupI;
import de.cebitec.mgx.api.misc.DistributionI;
import de.cebitec.mgx.api.misc.Pair;
import de.cebitec.mgx.api.model.AttributeI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author sjaenick
 */
public class LimitFilter implements VisFilterI<DistributionI> {

    private LIMITS limit = LIMITS.ALL;

    public enum LIMITS {

        ALL("All", -1),
        TOP10("Top 10", 10),
        TOP25("Top 25", 25),
        TOP50("Top 50", 50),
        TOP100("Top 100", 100);
        private final String s;
        private final int l;

        private LIMITS(String s, int c) {
            this.s = s;
            l = c;
        }

        public int getValue() {
            return l;
        }

        @Override
        public String toString() {
            return s;
        }
        
    };

    public void setLimit(LIMITS max) {
        limit = max;
    }

    @Override
    public List<Pair<VisualizationGroupI, DistributionI>> filter(List<Pair<VisualizationGroupI, DistributionI>> dists) {
        if (limit == LIMITS.ALL) {
            return dists;
        }

        // summary distribution over all groups
        Map<AttributeI, Double> summary = new HashMap<>();
        for (Pair<VisualizationGroupI, DistributionI> pair : dists) {
            for (Map.Entry<AttributeI, ? extends Number> e : pair.getSecond().entrySet()) {
                if (summary.containsKey(e.getKey())) {
                    Double old = summary.get(e.getKey());
                    summary.put(e.getKey(), old + e.getValue().doubleValue());
                } else {
                    summary.put(e.getKey(), e.getValue().doubleValue());
                }
            }
        }
        
        // find most abundant entries
        List<AttributeI> sortList = new ArrayList<>();
        sortList.addAll(summary.keySet());
        Collections.sort(sortList, new SortOrder.SortByValue(summary));

        List<AttributeI> toKeep = sortList.size() > limit.getValue() 
                ? sortList.subList(0, limit.getValue())
                : sortList;
        
        
        for (Pair<VisualizationGroupI, DistributionI> p : dists) {
            p.getSecond().setOrder(toKeep);
        }

        return dists;
    }
}
