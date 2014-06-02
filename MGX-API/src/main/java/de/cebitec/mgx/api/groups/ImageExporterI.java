package de.cebitec.mgx.api.groups;


/**
 *
 * @author sjaenick
 */
public interface ImageExporterI {
    
    public FileType[] getSupportedTypes();

    public boolean export(FileType type, String fName) throws Exception;
}
