/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.pevents;

import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 *
 * @author sj
 */
public class Activator implements BundleActivator {

    private static final AtomicLong numDirect = new AtomicLong(0);
    private static final AtomicLong numThreaded = new AtomicLong(0);
    private final static boolean DUMP_STATS = true;

    @Override
    public void start(BundleContext context) throws Exception {
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        if (DUMP_STATS) {
            Logger.getLogger(getClass().getPackage().getName())
                    .log(Level.INFO, "Terminating with {0} direct and {1} async event deliveries.",
                            new Object[]{numDirect.get(), numThreaded.get()});
        }
    }

    public static synchronized void direct(int num) {
        numDirect.addAndGet(num);
    }

    public static synchronized void async(int num) {
        numThreaded.addAndGet(num);
    }
}
