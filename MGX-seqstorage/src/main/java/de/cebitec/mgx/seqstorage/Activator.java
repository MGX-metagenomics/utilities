/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.seqstorage;

import de.cebitec.mgx.sequence.FactoryI;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 *
 * @author sj
 */
public class Activator implements BundleActivator {

    ServiceRegistration<?> registerService;

    @Override
    public void start(BundleContext context) throws Exception {
        registerService = context.registerService(FactoryI.class.getName(), new ReaderFactory(), null);
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        context.ungetService(registerService.getReference());
    }

}
