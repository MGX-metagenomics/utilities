package de.cebitec.mgx.kegg.pathways.model;

import de.cebitec.mgx.kegg.pathways.KEGGException;
import de.cebitec.mgx.kegg.pathways.api.PathwayI;
import java.util.Objects;

/**
 *
 * @author sj
 */
public class Pathway implements PathwayI {

    private final String mapNum;
    private final String name;

    public Pathway(String mapNumber, String name) throws KEGGException {
        if (!mapNumber.startsWith("map")) {
            throw new KEGGException("Invalid map number: " + mapNumber);
        }
        this.mapNum = mapNumber;
        this.name = name;
    }

    @Override
    public String getMapNum() {
        return mapNum;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int compareTo(PathwayI o) {
        return name.compareTo(o.getName());
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 89 * hash + Objects.hashCode(this.mapNum);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Pathway other = (Pathway) obj;
        if (!Objects.equals(this.mapNum, other.mapNum)) {
            return false;
        }
        return true;
    }
}
