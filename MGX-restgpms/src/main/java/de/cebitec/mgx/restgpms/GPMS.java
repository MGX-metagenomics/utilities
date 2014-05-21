package de.cebitec.mgx.restgpms;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.ClientResponse.Status;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import de.cebitec.gpms.core.MembershipI;
import de.cebitec.gpms.core.ProjectClassI;
import de.cebitec.gpms.core.ProjectI;
import de.cebitec.gpms.core.RoleI;
import de.cebitec.gpms.dto.impl.GPMSLong;
import de.cebitec.gpms.dto.impl.GPMSString;
import de.cebitec.gpms.dto.impl.MembershipDTO;
import de.cebitec.gpms.dto.impl.MembershipDTOList;
import de.cebitec.gpms.dto.impl.ProjectClassDTO;
import de.cebitec.gpms.dto.impl.ProjectClassDTOList;
import de.cebitec.gpms.dto.impl.RoleDTO;
import de.cebitec.gpms.rest.GPMSClientI;
import de.cebitec.gpms.rest.RESTMasterI;
import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import javax.net.ssl.SSLHandshakeException;

/**
 *
 * @author sjaenick
 */
public class GPMS implements GPMSClientI {

    private ClientConfig cc = null;
    private Client client;
    //private WebResource res = null;
    private final String baseuri;
    private final String servername;
    private User user;
    private boolean loggedin = false;
    private String error;

    public GPMS(String servername, String baseuri) {

        if (!baseuri.endsWith("/")) {
            baseuri += "/";
        }
        this.servername = servername;
        this.baseuri = baseuri;
        cc = new DefaultClientConfig();
        cc.getClasses().add(de.cebitec.mgx.protobuf.serializer.PBReader.class);
        cc.getClasses().add(de.cebitec.mgx.protobuf.serializer.PBWriter.class);
        client = Client.create(cc);
    }

    @Override
    public Iterator<ProjectClassI> getProjectClasses() {
        assert !EventQueue.isDispatchThread();
        List<ProjectClassI> ret = new LinkedList<>();
        ClientResponse response = getResource().path("/GPMS/GPMSBean/listProjectClasses").get(ClientResponse.class);
        if (response.getClientResponseStatus() == Status.OK) {
            ProjectClassDTOList list = response.<ProjectClassDTOList>getEntity(ProjectClassDTOList.class);
            for (ProjectClassDTO dto : list.getProjectclassList()) {
                List<RoleI> roles = new ArrayList<>(3);
                for (RoleDTO rdto : dto.getRoles().getRoleList()) {
                    roles.add(new Role(rdto.getName()));
                }
                ret.add(new ProjectClass(dto.getName(), roles));
            }
        } else {
            error = response.getClientResponseStatus().toString();
            return null;
        }
        return ret.iterator();
    }

    @Override
    public RESTMasterI createMaster(final MembershipI m) {
        return new RESTMaster(m, user, baseuri, true);
    }

    @Override
    public Iterator<MembershipI> getMemberships() {
        List<MembershipI> ret = new ArrayList<>();
        assert !EventQueue.isDispatchThread();
        ClientResponse response = getResource().path("/GPMS/GPMSBean/listMemberships").get(ClientResponse.class);
        if (Status.OK == response.getClientResponseStatus()) {
            MembershipDTOList list = response.<MembershipDTOList>getEntity(MembershipDTOList.class);
            for (MembershipDTO mdto : list.getMembershipList()) {

                List<RoleI> roles = new ArrayList<>(5);
                for (RoleDTO rdto : mdto.getProject().getProjectClass().getRoles().getRoleList()) {
                    roles.add(new Role(rdto.getName()));
                }

                ProjectClassI pclass = new ProjectClass(mdto.getProject().getProjectClass().getName(), roles);
                ProjectI proj = new Project(mdto.getProject().getName(), pclass, false);
                RoleI role = new Role(mdto.getRole().getName());

                ret.add(new Membership(proj, role));
            }
        } else {
            error = response.getClientResponseStatus().toString();
            return null;
        }
        return ret.iterator();
    }
    
    private WebResource getResource() {
        return client.resource(baseuri);
    }

    @Override
    public boolean login(String login, String password) {
        if (login == null || password == null) {
            return false;
        }
        if (client == null) {
            client = Client.create(cc);
        }
        client.removeAllFilters();
        client.addFilter(new HTTPBasicAuthFilter(login, password));
        ClientResponse response;
        try {
            response = getResource().path("/GPMS/GPMSBean/login").get(ClientResponse.class);
        } catch (ClientHandlerException che) {
            if (che.getCause() != null && che.getCause() instanceof SSLHandshakeException) {
                return login(login, password);
            }
            // most common cause here: server down
            error = che.getCause().getMessage();
            return false;
        }

        switch (response.getClientResponseStatus()) {
            case OK:
                GPMSString reply = response.<GPMSString>getEntity(GPMSString.class);
                if ("MGX".equals(reply.getValue())) {
                    loggedin = true;
                    user = new User(login, password);
                }
                break;
            case UNAUTHORIZED:
                error = "Wrong username/password";
                break;
            case GATEWAY_TIMEOUT:
                error = "Connection refused, server down?";
                break;
            default:
                error = response.getClientResponseStatus().toString();
                break;
        }

        return loggedin;
    }

    public String getError() {
        return error;
    }

    public long ping() {
        try {
            return getResource().path("/GPMS/GPMSBean/ping").get(GPMSLong.class).getValue();
        } catch (UniformInterfaceException ufie) {
        } catch (ClientHandlerException ex ) {
            if (ex.getCause() != null && ex.getCause() instanceof SSLHandshakeException) {
                return ping(); //retry
            } else {
                System.err.println("MSG: "+ ex.getMessage());
                assert false;
            }
        }
        return -1;
    }

    @Override
    public void logout() {
        client = null;
        user = null;
        error = null;
        loggedin = false;
    }

    @Override
    public String getBaseURI() {
        return baseuri;
    }

    public String getServerName() {
        return servername;
    }
}
