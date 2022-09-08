package de.cebitec.mgx.kegg.pathways.access;

import de.cebitec.mgx.kegg.pathways.KEGGException;
import de.cebitec.mgx.kegg.pathways.KEGGMaster;
import static de.cebitec.mgx.kegg.pathways.access.PathwayAccess.COORDS;
import de.cebitec.mgx.kegg.pathways.api.PathwayI;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;
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
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sj
 */
public class AccessBase {

    private final KEGGMaster master;
    private final ExecutorService pool;

    protected final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();

    public AccessBase(KEGGMaster master) {
        this.master = master;
        pool = Executors.newFixedThreadPool(5);
    }

    ExecutorService getPool() {
        return pool;
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
            StringBuilder msg = new StringBuilder();
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

    protected boolean isValid(String type) {
        try {
            rwl.readLock().lockInterruptibly();
        } catch (InterruptedException ex) {
            Logger.getLogger(AccessBase.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }

        boolean ret = getMaster().isValid(type);
        rwl.readLock().unlock();
        return ret;
    }

    protected boolean isValid(PathwayI pw) {
        try {
            rwl.readLock().lockInterruptibly();
        } catch (InterruptedException ex) {
            Logger.getLogger(AccessBase.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        boolean ret = getMaster().isValid(COORDS + "_" + pw.getMapNumber());
        rwl.readLock().unlock();
        return ret;
    }

    protected void setValid(String type) throws InterruptedException {
        rwl.writeLock().lockInterruptibly();
        getMaster().setValid(type);
        rwl.writeLock().unlock();
    }

    protected WebTarget getRESTResource() {
        return master.getRESTResource();
    }

    protected WebTarget getKEGGResource() {
        return master.getKEGGResource();
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
