package de.cebitec.mgx.restgpms;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import org.junit.Test;

/**
 *
 * @author sj
 */
public class TestMaster {

    public static GPMS get() {
        
        String serverURI = "https://mgx.cebitec.uni-bielefeld.de/MGX-maven-web/webresources/";
        
        String config = System.getProperty("user.home") + "/.m2/mgx.junit";
        File f = new File(config);
        if (f.exists() && f.canRead()) {
            Properties p = new Properties();
            try {
                p.load(new FileInputStream(f));
                serverURI = p.getProperty("testserver");
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        }
        
        GPMS gpms = new GPMS("MyServer", serverURI);
        return gpms;
    }
}
