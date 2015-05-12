package de.cebitec.mgx.restgpms;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.client.urlconnection.HTTPSProperties;
import de.cebitec.gpms.core.ProjectI;
import de.cebitec.gpms.core.RoleI;
import de.cebitec.gpms.rest.RESTMasterI;
import de.cebitec.gpms.rest.RESTMembershipI;
import de.cebitec.gpms.rest.RESTUserI;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 *
 * @author sjaenick
 */
public class RESTMaster implements RESTMasterI {

    private final RESTMembershipI membership;
    private final RESTUserI user;
    private final ClientConfig cc;
    private Client client = null;

    public RESTMaster(RESTMembershipI m, RESTUserI u, String baseuri, boolean verifySSL) {
        this.membership = m;
        this.user = u;

        cc = new DefaultClientConfig();
        cc.getProperties().put(ClientConfig.PROPERTY_THREADPOOL_SIZE, 10);
        cc.getProperties().put(ClientConfig.PROPERTY_CONNECT_TIMEOUT, 10000); // in ms
        cc.getProperties().put(ClientConfig.PROPERTY_READ_TIMEOUT, 15000);

        if (!verifySSL) {

            /*
             * taken from
             * http://stackoverflow.com/questions/6047996/ignore-self-signed-ssl-cert-using-jersey-client
             * 
             * code below disables certificate validation; required for servers running
             * with self-signed or otherwise untrusted certificates
             */
            // Create a trust manager that does not validate certificate chains
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                @Override
                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }};

            SSLContext ctx = null;
            try {
                ctx = SSLContext.getInstance("SSL");
                ctx.init(null, trustAllCerts, new SecureRandom());
                HttpsURLConnection.setDefaultSSLSocketFactory(ctx.getSocketFactory());
            } catch (NoSuchAlgorithmException | KeyManagementException ex) {
                Logger.getLogger(RESTMaster.class.getName()).log(Level.SEVERE, null, ex);
            }

            cc.getProperties().put(HTTPSProperties.PROPERTY_HTTPS_PROPERTIES, new HTTPSProperties(null, ctx));
        }

        client = Client.create(cc);
        client.addFilter(new HTTPBasicAuthFilter(user.getLogin(), user.getPassword()));
    }

    @Override
    public void registerSerializer(Class c) {
        if (!cc.getClasses().contains(c)) {
            cc.getClasses().add(c);
            client = Client.create(cc);
            client.addFilter(new HTTPBasicAuthFilter(user.getLogin(), user.getPassword()));
        }
    }

    @Override
    public ProjectI getProject() {
        return membership.getProject();
    }

    @Override
    public RoleI getRole() {
        return membership.getRole();
    }

    @Override
    public RESTUserI getUser() {
        return user;
    }

    @Override
    public Client getClient() {
        return client;
    }
}
