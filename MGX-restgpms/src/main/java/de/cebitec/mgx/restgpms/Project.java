package de.cebitec.mgx.restgpms;

import de.cebitec.gpms.core.ProjectClassI;
import de.cebitec.gpms.rest.RESTProjectI;
import java.util.Objects;

/**
 *
 * @author sjaenick
 */
public class Project implements RESTProjectI {

    private final String name;
    private final ProjectClassI projClass;
    private final boolean isPublic;
    private final String baseURI;

    public Project(String name, String baseURI, ProjectClassI pclass, boolean isPublic) {
        this.name = name;
        this.baseURI = baseURI;
        this.projClass = pclass;
        this.isPublic = isPublic;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getRESTURI() {
        return baseURI;
    }

    @Override
    public ProjectClassI getProjectClass() {
        return projClass;
    }

    @Override
    public boolean isPublic() {
        return isPublic;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + Objects.hashCode(this.name);
        hash = 97 * hash + Objects.hashCode(this.projClass);
        hash = 97 * hash + (this.isPublic ? 1 : 0);
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
        final Project other = (Project) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.projClass, other.projClass)) {
            return false;
        }
        if (this.isPublic != other.isPublic) {
            return false;
        }
        return true;
    }

}
