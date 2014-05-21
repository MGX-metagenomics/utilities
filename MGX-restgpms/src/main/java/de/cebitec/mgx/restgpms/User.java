package de.cebitec.mgx.restgpms;

import de.cebitec.gpms.core.MembershipI;
import de.cebitec.gpms.core.ProjectClassI;
import de.cebitec.gpms.rest.RESTUserI;
import java.util.List;

/**
 *
 * @author sjaenick
 */
public class User implements RESTUserI {

    private final String login;
    private final String password;

    public User(String login, String password) {
        this.login = login;
        this.password = password;
    }

    @Override
    public String getLogin() {
        return login;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public List<? extends MembershipI> getMemberships(ProjectClassI projClass) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
