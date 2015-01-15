package de.cebitec.mgx.admin.misc;


import java.io.Serializable;
import java.util.Collection;


/**
 *
 * @author belmann
 */
public class Reference implements Serializable {

    private Long id;
    
    private Collection<Region> regions;

    private String name;
    
    private int length;
    
    private String filePath;
    
    
    public Collection<Region> getRegions() {
        return regions;
    }

    public void setRegions(Collection<Region> regions) {
        this.regions = regions;
    }
    
    public String getFile() {
        return filePath;
    }

    public void setFile(String filePath) {
        this.filePath = filePath;
    }
    
    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    @Override
    public String toString() {
        return "de.cebitec.mgx.model.db.Reference";
    }

    public Long getId() {
     return id;   
    }
    
    public void setId(Long l) {
        id = l;
    }
}
