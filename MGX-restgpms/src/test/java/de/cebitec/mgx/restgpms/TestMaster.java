package de.cebitec.mgx.restgpms;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Properties;
import static org.junit.Assert.fail;
import org.junit.Assume;

/**
 *
 * @author sj
 */
public class TestMaster {

    public static GPMSClient get() {

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

        Assume.assumeNotNull(serverURI);
        try {
            URL myURL = new URL(serverURI);
            URLConnection myURLConnection = myURL.openConnection();
            myURLConnection.connect();
        } catch (MalformedURLException e) {
            fail("Invalid URL");
        } catch (IOException e) {
            Assume.assumeFalse("Could not connect to "+serverURI, true);
        }

        GPMSClient gpms = new GPMSClient("MyServer", serverURI);
        return gpms;
    }
}
