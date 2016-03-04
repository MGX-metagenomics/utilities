package de.cebitec.mgx.admin.misc;

/**
 *
 * @author belmann
 */
import java.io.Serializable;

/**
 *
 * @author sjaenick
 */
public class Region implements Serializable {

    private Long id;
    private String type;
    private String name;
    private String description;
    private Reference reference;
    private int start;
    private int stop;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Reference getReference() {
        return reference;
    }

    public void setReference(Reference reference) {
        this.reference = reference;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getStop() {
        return stop;
    }

    public void setStop(int stop) {
        this.stop = stop;
    }

    @Override
    public String toString() {
        return "de.cebitec.mgx.Region";
    }
}
