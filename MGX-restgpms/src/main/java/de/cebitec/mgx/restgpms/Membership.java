package de.cebitec.mgx.restgpms;

import de.cebitec.gpms.core.MembershipI;
import de.cebitec.gpms.core.ProjectI;
import de.cebitec.gpms.core.RoleI;

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

}
