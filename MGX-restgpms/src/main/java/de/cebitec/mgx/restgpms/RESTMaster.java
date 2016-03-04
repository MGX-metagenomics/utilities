package de.cebitec.mgx.restgpms;

import de.cebitec.gpms.core.MasterI;
import de.cebitec.gpms.core.MembershipI;
import de.cebitec.gpms.core.ProjectI;
import de.cebitec.gpms.core.RoleI;
import de.cebitec.gpms.core.UserI;
import de.cebitec.gpms.rest.GPMSClientI;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.logging.Logger;

/**
 *
 * @author sjaenick
 */
public class RESTMaster implements MasterI {

    private final MembershipI membership;
    private final UserI user;
    private static final Logger LOG = Logger.getLogger(RESTMaster.class.getName());
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    public RESTMaster(GPMSClientI gpmsclient, MembershipI m, UserI user) {
        this.membership = m;
        this.user = user;
        gpmsclient.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals(GPMSClientI.PROP_LOGGEDIN)) {
                    pcs.firePropertyChange(new PropertyChangeEvent(this, MasterI.PROP_LOGGEDIN, evt.getOldValue(), evt.getNewValue()));
                }
            }
        });
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

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }
}
