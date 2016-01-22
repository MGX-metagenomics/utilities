package de.cebitec.mgx.restgpms;

import de.cebitec.gpms.core.MasterI;
import de.cebitec.gpms.core.MembershipI;
import de.cebitec.gpms.core.ProjectI;
import de.cebitec.gpms.core.RoleI;
import de.cebitec.gpms.core.UserI;
import java.util.logging.Logger;

/**
 *
 * @author sjaenick
 */
public class RESTMaster implements MasterI {
    
    private final MembershipI membership;
    private final UserI user;
    private static final Logger LOG = Logger.getLogger(RESTMaster.class.getName());
    
    public RESTMaster(MembershipI m, UserI user) {
        this.membership = m;
        this.user = user;
    }
    
    @Override
    public final ProjectI getProject() {
        return membership.getProject();
    }
    
    @Override
    public final RoleI getRole() {
        return membership.getRole();
    }
    
    @Override
    public final UserI getUser() {
        return user;
    }

    @Override
    public final void log(String message) {
        LOG.info(message);
    }

    @Override
    public final void setUser(UserI user) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public final void close() {
    }
}
