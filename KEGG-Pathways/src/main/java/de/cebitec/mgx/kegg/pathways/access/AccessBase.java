package de.cebitec.mgx.kegg.pathways.access;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import de.cebitec.mgx.kegg.pathways.KEGGException;
import de.cebitec.mgx.kegg.pathways.KEGGMaster;
import static de.cebitec.mgx.kegg.pathways.access.PathwayAccess.COORDS;
import de.cebitec.mgx.kegg.pathways.api.PathwayI;
import java.awt.EventQueue;
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

/**
 *
 * @author sj
 */
public class AccessBase {

    private final KEGGMaster master;
    private final ExecutorService pool;

    public AccessBase(KEGGMaster master) {
        this.master = master;
        pool = Executors.newFixedThreadPool(30);
    }

    ExecutorService getPool() {
        return pool;
    }

    protected final InputStream get(final WebResource resource, final String path) throws KEGGException {
        assert !EventQueue.isDispatchThread();
        ClientResponse res = resource.path(path).get(ClientResponse.class);
        catchException(res);
        return res.getEntityInputStream();
    }

    protected final void catchException(final ClientResponse res) throws KEGGException {
        if (res.getClientResponseStatus() != ClientResponse.Status.OK) {
            StringBuilder msg = new StringBuilder();
            try (InputStreamReader isr = new InputStreamReader(res.getEntityInputStream())) {
                try (BufferedReader r = new BufferedReader(isr)) {
                    String buf;
                    while ((buf = r.readLine()) != null) {
                        msg.append(buf);
                        msg.append(System.lineSeparator());
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(AccessBase.class.getName()).log(Level.SEVERE, null, ex);
            }
            throw new KEGGException(msg.toString());
        }
    }

    protected boolean isValid(File f) {
        boolean ret = f.exists() && f.canRead() && f.lastModified() > (System.currentTimeMillis() - getMaster().getTimeout());
        return ret;
    }

    protected boolean isValid(String type) {
        boolean ret = getMaster().isValid(type);
        return ret;
    }

    protected boolean isValid(PathwayI pw) {
        boolean ret = getMaster().isValid(COORDS + "_" + pw.getMapNum());
        return ret;
    }

    protected void setValid(String type) {
         getMaster().setValid(type);
    }

    protected WebResource getRESTResource() {
        return master.getRESTResource();
    }

    protected WebResource getKEGGResource() {
        return master.getKEGGResource();
    }

    protected KEGGMaster getMaster() {
        return master;
    }

    protected static void copyFile(File in, File out) throws IOException {
        FileInputStream fis = new FileInputStream(in);
        FileOutputStream fos = new FileOutputStream(out);
        FileChannel inChannel = fis.getChannel();
        FileChannel outChannel = fos.getChannel();
        try {
            inChannel.transferTo(0, inChannel.size(), outChannel);
        } catch (IOException e) {
            throw e;
        } finally {
            if (inChannel != null) {
                inChannel.close();
            }
            if (outChannel != null) {
                outChannel.close();
            }
            fis.close();
            fos.close();
        }
    }
}