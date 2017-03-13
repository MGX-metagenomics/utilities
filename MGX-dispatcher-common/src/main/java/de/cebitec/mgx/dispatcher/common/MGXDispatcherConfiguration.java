package de.cebitec.mgx.dispatcher.common;

import de.cebitec.mgx.dispatcher.common.DispatcherConfigBase;
import de.cebitec.mgx.dispatcher.common.MGXDispatcherException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Singleton;
import javax.ejb.Startup;

/**
 *
 * @author sjaenick
 */
@Singleton
@Startup
public class MGXDispatcherConfiguration extends DispatcherConfigBase {

    public MGXDispatcherConfiguration() {
    }

    public final String getDispatcherHost() throws MGXDispatcherException {

        /*
         * dispatcher host might be changing, therefore we have to read
         * this file every time
         */
        File f = new File(dispatcherHostFile);
        if (!f.exists()) {
            throw new MGXDispatcherException("Dispatcher host file missing, dispatcher not running?");
        }

        Properties p = new Properties();
        FileInputStream in = null;
        try {
            in = new FileInputStream(f);
            p.load(in);
        } catch (IOException ex) {
            throw new MGXDispatcherException(ex);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(MGXDispatcherConfiguration.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return p.getProperty("mgx_dispatcherhost");
    }

    public final String getDispatcherToken() throws MGXDispatcherException {

        /*
         * dispatcher host might be changing, therefore we have to read
         * this file every time
         */
        File f = new File(dispatcherHostFile);
        if (!f.exists()) {
            throw new MGXDispatcherException("Dispatcher host file missing, dispatcher not running?");
        }

        Properties p = new Properties();
        FileInputStream in = null;
        try {
            in = new FileInputStream(f);
            p.load(in);
        } catch (IOException ex) {
            throw new MGXDispatcherException(ex);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(MGXDispatcherConfiguration.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return p.getProperty("mgx_dispatchertoken");
    }
}
