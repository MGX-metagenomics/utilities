package de.cebitec.mgx.restgpms;

import de.cebitec.gpms.core.MembershipI;
import de.cebitec.gpms.core.ProjectI;
import de.cebitec.gpms.core.RoleI;
import java.util.Objects;

/**
 *
 * @author sjaenick
 */
public class Membership implements MembershipI {

    private final ProjectI project;
    private final RoleI role;

    public Membership(ProjectI project, RoleI role) {
        this.project = project;
        this.role = role;
    }

    @Override
    public ProjectI getProject() {
        return project;
    }

    @Override
    public RoleI getRole() {
        return role;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 71 * hash + Objects.hashCode(this.project);
        hash = 71 * hash + Objects.hashCode(this.role);
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
        final Membership other = (Membership) obj;
        if (!Objects.equals(this.project, other.project)) {
            return false;
        }
        if (!Objects.equals(this.role, other.role)) {
            return false;
        }
        return true;
    }
}
