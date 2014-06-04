
package de.cebitec.mgx.restgpms;

import de.cebitec.gpms.core.ProjectClassI;
import de.cebitec.gpms.core.RoleI;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author sjaenick
 */
public class ProjectClass implements ProjectClassI {

    private final String name;
    private final List<? extends RoleI> roles;

    public ProjectClass(String name, List<? extends RoleI> rs) {
        this.name = name;
        this.roles = rs;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<? extends RoleI> getRoles() {
        return roles;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 71 * hash + Objects.hashCode(this.name);
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
        final ProjectClass other = (ProjectClass) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        return true;
    }
}
