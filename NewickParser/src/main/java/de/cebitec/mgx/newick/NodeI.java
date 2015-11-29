package de.cebitec.mgx.newick;

import java.util.List;

/**
 *
 * @author sjaenick
 */
public interface NodeI {

    public List<NodeI> getChildren();

    public List<NodeI> getLeaves();

    public double getWeight();

    public String getName();
    
    public void setName(String name);

    public boolean isLeaf();
}
