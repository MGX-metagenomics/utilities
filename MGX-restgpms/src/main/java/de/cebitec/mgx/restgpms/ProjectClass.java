
package de.cebitec.mgx.restgpms;

import de.cebitec.gpms.core.ProjectClassI;
import de.cebitec.gpms.core.RoleI;
import java.util.List;

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

}
