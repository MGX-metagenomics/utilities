package de.cebitec.mgx.resteasy;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.ext.RuntimeDelegate;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 *
 * @author sj
 */
public class Activator implements BundleActivator {

    @Override
    public void start(BundleContext context) throws Exception {
        System.setProperty(ClientBuilder.JAXRS_DEFAULT_CLIENT_BUILDER_PROPERTY, "org.jboss.resteasy.client.jaxrs.internal.ResteasyClientBuilderImpl");
        System.setProperty(RuntimeDelegate.JAXRS_RUNTIME_DELEGATE_PROPERTY, "org.jboss.resteasy.client.jaxrs.internal.ResteasyClientBuilderImpl");
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        System.setProperty(ClientBuilder.JAXRS_DEFAULT_CLIENT_BUILDER_PROPERTY, ClientBuilder.JAXRS_DEFAULT_CLIENT_BUILDER_PROPERTY);
    }

}
