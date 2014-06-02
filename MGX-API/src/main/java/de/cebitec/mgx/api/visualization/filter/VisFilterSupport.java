package de.cebitec.mgx.api.visualization.filter;

import de.cebitec.mgx.api.groups.VisualizationGroupI;
import de.cebitec.mgx.api.misc.Pair;
import java.util.List;

/**
 *
 * @author sjaenick
 */
public final class VisFilterSupport {

    private VisFilterSupport() {
    }

    public static <V, T, U> VisFilterI append(VisFilterI<T> first, VisFilterI<T> second) {
        return new VFCombinedImpl(first, second);
    }
    
    private final static class VFCombinedImpl<T> implements VisFilterI<T> {

    VisFilterI<T> first;
    VisFilterI<T> second;

    public VFCombinedImpl(VisFilterI<T> first, VisFilterI<T> second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public List<Pair<VisualizationGroupI, T>> filter(List<Pair<VisualizationGroupI, T>> dists) {
        return second.filter(first.filter(dists));
    }
}
}
