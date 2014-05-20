package de.cebitec.mgx.kegg.pathways.access;

import de.cebitec.mgx.kegg.pathways.KEGGException;
import de.cebitec.mgx.kegg.pathways.KEGGMaster;
import de.cebitec.mgx.kegg.pathways.api.ECNumberI;
import de.cebitec.mgx.kegg.pathways.api.PathwayI;
import java.util.Set;

/**
 *
 * @author sj
 */
public class ECNumberAccess extends AccessBase {

    public ECNumberAccess(KEGGMaster master) {
        super(master);
    }

    public Set<ECNumberI> byPathway(PathwayI pw) throws KEGGException {
        return getMaster().Pathways().getCoords(pw).keySet();
    }
}
