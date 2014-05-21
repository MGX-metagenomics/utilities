package de.cebitec.mgx.restgpms;

import de.cebitec.gpms.core.ProjectClassI;
import de.cebitec.gpms.core.ProjectI;

/**
 *
 * @author sjaenick
 */
public class Project implements ProjectI {

    private final String name;
    private final ProjectClassI projClass;
    private final boolean isPublic;

    public Project(String name, ProjectClassI pclass, boolean isPublic) {
        this.name = name;
        this.projClass = pclass;
        this.isPublic = isPublic;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ProjectClassI getProjectClass() {
        return projClass;
    }

    @Override
    public boolean isPublic() {
        return isPublic;
    }

}
