package de.cebitec.mgx.restgpms;

import de.cebitec.gpms.core.MasterI;
import de.cebitec.gpms.core.MembershipI;
import de.cebitec.gpms.core.ProjectI;
import de.cebitec.gpms.core.RoleI;
import de.cebitec.gpms.core.UserI;
import de.cebitec.gpms.rest.GPMSClientI;
import de.cebitec.gpms.rest.RESTMasterI;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Objects;
import java.util.logging.Logger;

/**
 *
 * @author sjaenick
 */
public class RESTMaster implements RESTMasterI, PropertyChangeListener {

    private final ProjectI project;
    private RoleI role;
    private UserI user;
    private final GPMSClientI gpmsclient;
    private static final Logger LOG = Logger.getLogger(RESTMaster.class.getName());
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    public RESTMaster(GPMSClientI gpmsclient, MembershipI m, UserI user) {
        this.project = m.getProject();
        this.role = m.getRole();
        this.user = user;
        this.gpmsclient = gpmsclient;
        gpmsclient.addPropertyChangeListener(this);
    }

    @Override
    public String getServerName() {
        return gpmsclient.getServerName();
    }

    @Override
    public final ProjectI getProject() {
        return project;
    }

    @Override
    public final RoleI getRole() {
        return role;
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
        //gpmsclient.logout();
        //project = null;
        role = null;
        user = null;
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getSource().equals(gpmsclient) && evt.getPropertyName().equals(GPMSClientI.PROP_LOGGEDIN)) {
            if (evt.getNewValue() instanceof Boolean) {
                Boolean newVal = (Boolean) evt.getNewValue();
                if (!newVal) {
                    gpmsclient.removePropertyChangeListener(this);
                    pcs.firePropertyChange(new PropertyChangeEvent(this, MasterI.PROP_LOGGEDIN, evt.getOldValue(), evt.getNewValue()));
                    close();
                }
            }
        }
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.project);
        hash = 79 * hash + Objects.hashCode(this.role);
        hash = 79 * hash + Objects.hashCode(this.user);
        hash = 79 * hash + Objects.hashCode(this.gpmsclient);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final RESTMaster other = (RESTMaster) obj;
        if (!Objects.equals(this.project, other.project)) {
            return false;
        }
        if (!Objects.equals(this.role, other.role)) {
            return false;
        }
        if (!Objects.equals(this.user, other.user)) {
            return false;
        }
        if (!Objects.equals(this.gpmsclient, other.gpmsclient)) {
            return false;
        }
        return true;
    }

}
