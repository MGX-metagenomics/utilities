package de.cebitec.mgx.admin;

/**
 *
 * @author belmann
 */
public class Reference {

    private String name;
    private int length;
    private String filePath;
    private long id = -1;

    public final long getId() {
        return id;
    }

    public final void setId(long id) {
        this.id = id;
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
}
