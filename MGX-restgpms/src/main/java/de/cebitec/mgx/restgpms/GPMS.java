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
import de.cebitec.gpms.core.ProjectClassI;
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
import de.cebitec.gpms.rest.RESTMembershipI;
import de.cebitec.gpms.rest.RESTProjectI;
import de.cebitec.gpms.rest.RESTUserI;
import java.net.UnknownHostException;
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
    private final String baseuri;
    private final String servername;
    private RESTUserI user;
    private boolean loggedin = false;
    private String error;

    public GPMS(String servername, String baseuri) {
        if (baseuri == null) {
            throw new IllegalArgumentException("No base URI supplied.");
        }
        if (!baseuri.endsWith("/")) {
            baseuri += "/";
        }
        this.servername = servername;
        this.baseuri = baseuri;
        cc = new DefaultClientConfig();
        cc.getClasses().add(de.cebitec.mgx.protobuf.serializer.PBReader.class);
        cc.getClasses().add(de.cebitec.mgx.protobuf.serializer.PBWriter.class);
        cc.getProperties().put(ClientConfig.PROPERTY_THREADPOOL_SIZE, 10);
        cc.getProperties().put(ClientConfig.PROPERTY_CONNECT_TIMEOUT, 10000); // in ms
        cc.getProperties().put(ClientConfig.PROPERTY_READ_TIMEOUT, 10000);

        client = Client.create(cc);
    }

    @Override
    public Iterator<ProjectClassI> getProjectClasses() {
        List<ProjectClassI> ret = new LinkedList<>();
        ClientResponse response = getResource().path("GPMS").path("GPMSBean").path("listProjectClasses").get(ClientResponse.class);
        if (Status.fromStatusCode(response.getStatus()) == Status.OK) {
            ProjectClassDTOList list = response.<ProjectClassDTOList>getEntity(ProjectClassDTOList.class);
            for (ProjectClassDTO dto : list.getProjectclassList()) {
                List<RoleI> roles = new ArrayList<>(3);
                for (RoleDTO rdto : dto.getRoles().getRoleList()) {
                    roles.add(new Role(rdto.getName()));
                }
                ret.add(new ProjectClass(dto.getName(), roles));
            }
        } else {
            error = Status.fromStatusCode(response.getStatus()).getReasonPhrase();
            return null;
        }
        return ret.iterator();
    }

    @Override
    public RESTMasterI createMaster(final RESTMembershipI m) {
        if (m == null) {
            throw new IllegalArgumentException("RESTMembershipI is null");
        }
        return new RESTMaster(m, user, m.getProject().getRESTURI() != null ? m.getProject().getRESTURI() : baseuri, true);
    }

    @Override
    public Iterator<RESTMembershipI> getMemberships() {
        List<RESTMembershipI> ret = new ArrayList<>();
        ClientResponse response = getResource().path("GPMS").path("GPMSBean").path("listMemberships").get(ClientResponse.class);
        if (Status.fromStatusCode(response.getStatus()) == Status.OK) {
            MembershipDTOList list = response.<MembershipDTOList>getEntity(MembershipDTOList.class);
            for (MembershipDTO mdto : list.getMembershipList()) {

                List<RoleI> roles = new ArrayList<>(5);
                for (RoleDTO rdto : mdto.getProject().getProjectClass().getRoles().getRoleList()) {
                    roles.add(new Role(rdto.getName()));
                }

                ProjectClassI pclass = new ProjectClass(mdto.getProject().getProjectClass().getName(), roles);
                RESTProjectI proj = new Project(mdto.getProject().getName(), baseuri, pclass, false);
                RoleI role = new Role(mdto.getRole().getName());

                ret.add(new Membership(proj, role));
            }
        } else {
            error = Status.fromStatusCode(response.getStatus()).getReasonPhrase();
            return null;
        }
        return ret.iterator();
    }

    private WebResource getResource() {
        if (client == null) {
            return null;
        }
        return client.resource(baseuri);
    }

    @Override
    public synchronized boolean login(String login, String password) {
        if (login == null || password == null) {
            return false;
        }
        client = Client.create(cc);
        client.removeAllFilters();
        client.addFilter(new HTTPBasicAuthFilter(login, password));
        loggedin = false;
        user = null;
        error = null;

        ClientResponse response;
        try {
            response = getResource().path("GPMS").path("GPMSBean").path("login").get(ClientResponse.class);
        } catch (ClientHandlerException che) {
            if (che.getCause() != null && che.getCause() instanceof SSLHandshakeException) {
                return login(login, password);
            } else if (che.getCause() != null && che.getCause() instanceof UnknownHostException) {
                error = "Could not resolve server address. Check your internet connection.";
                return false;
            }
            // most common cause here: server down
            error = che.getCause().getMessage();
            return false;
        }

        switch (Status.fromStatusCode(response.getStatus())) {
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
                error = Status.fromStatusCode(response.getStatus()).getReasonPhrase();
                break;
        }

        return loggedin;
    }

    public String getError() {
        return error;
    }

    public long ping() {
        try {
            WebResource wr = getResource();
            if (wr == null) { // e.g. after logging out
                return -1;
            }
            return wr.path("GPMS").path("GPMSBean").path("ping").get(GPMSLong.class).getValue();
        } catch (UniformInterfaceException ufie) {
            System.err.println("MSG: " + ufie.getMessage());
        } catch (ClientHandlerException ex) {
            if (ex.getCause() != null && ex.getCause() instanceof SSLHandshakeException) {
                return ping(); //retry
            } else if (ex.getCause() != null && ex.getCause() instanceof UnknownHostException) {
                error = "Could not resolve server address. Check your internet connection.";
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

    @Override
    public String getServerName() {
        return servername;
    }

    @Override
    public RESTUserI getUser() {
        return user;
    }
}
