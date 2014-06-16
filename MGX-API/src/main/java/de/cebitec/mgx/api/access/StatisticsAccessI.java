/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.api.access;

import de.cebitec.mgx.api.groups.VisualizationGroupI;
import de.cebitec.mgx.api.misc.DistributionI;
import de.cebitec.mgx.api.misc.PCAResultI;
import de.cebitec.mgx.api.misc.Pair;
import de.cebitec.mgx.api.misc.Point;
import de.cebitec.mgx.newick.NodeI;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author sj
 */
public interface StatisticsAccessI {

    public Iterator<Point> Rarefaction(DistributionI dist);

    public PCAResultI PCA(Collection<Pair<VisualizationGroupI, DistributionI>> groups, int pc1, int pc2);

    public List<Point> PCoA(Collection<Pair<VisualizationGroupI, DistributionI>> groups);

    public NodeI Clustering(List<Pair<VisualizationGroupI, DistributionI>> dists, String distanceMethod, String agglomeration);

}