package de.cebitec.mgx.restgpms;

import de.cebitec.gpms.core.RoleI;
import java.util.Objects;

/**
 *
 * @author sjaenick
 */
public class Role implements RoleI {

    public static final String USER = "User";
    public static final String GUEST = "Guest";
    public static final String ADMIN = "Admin";
    private final String name;

    public Role(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.name);
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
        final Role other = (Role) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        return true;
    }
}
