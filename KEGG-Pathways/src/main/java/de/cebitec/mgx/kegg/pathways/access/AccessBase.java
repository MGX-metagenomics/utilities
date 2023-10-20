package de.cebitec.mgx.kegg.pathways.access;

import de.cebitec.mgx.kegg.pathways.KEGGException;
import de.cebitec.mgx.kegg.pathways.KEGGMaster;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.channels.FileChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;

/**
 *
 * @author sj
 */
public class AccessBase {

    private final KEGGMaster master;

    public AccessBase(KEGGMaster master) {
        this.master = master;
    }

    protected final InputStream get(final WebTarget resource, final String... path) throws KEGGException {
        WebTarget wr = resource;
        for (String elem : path) {
            wr = wr.path(elem);
        }
        Response res = wr.request().get(Response.class);
        catchException(res);
        return res.readEntity(InputStream.class);
    }

    protected void catchException(final Response res) throws KEGGException {
        if (Response.Status.fromStatusCode(res.getStatus()) != Response.Status.OK) {
            StringBuilder msg = new StringBuilder(res.getStatus() + " ");
            try ( BufferedReader r = new BufferedReader(new InputStreamReader(res.readEntity(InputStream.class)))) {
                String buf;
                while ((buf = r.readLine()) != null) {
                    msg.append(buf);
                    msg.append(System.lineSeparator());
                }
            } catch (IOException ex) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
            }
            throw new KEGGException(msg.toString().trim());
        }
    }

    protected boolean isValid(File f) {
        boolean ret = f.exists() && f.canRead() && f.lastModified() > (System.currentTimeMillis() - getMaster().getTimeout());
        return ret;
    }

    protected WebTarget getRESTResource() {
        return master.getRESTResource();
    }

    protected KEGGMaster getMaster() {
        return master;
    }

    protected static void copyFile(File in, File out) throws IOException {
        try (FileInputStream fis = new FileInputStream(in)) {
            try (FileOutputStream fos = new FileOutputStream(out)) {
                try (FileChannel inChannel = fis.getChannel()) {
                    try (FileChannel outChannel = fos.getChannel()) {
                        inChannel.transferTo(0, inChannel.size(), outChannel);
                    }
                }
            }
        }
    }
}
