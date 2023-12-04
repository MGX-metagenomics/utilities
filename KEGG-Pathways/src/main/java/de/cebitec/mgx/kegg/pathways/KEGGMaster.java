package de.cebitec.mgx.kegg.pathways;

import de.cebitec.mgx.kegg.pathways.access.ECNumberAccess;
import de.cebitec.mgx.kegg.pathways.access.PathwayAccess;
import java.io.File;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sj
 */
public class KEGGMaster implements AutoCloseable {

    private final Client restclient;
    private final String cacheDir;
    private final static String REST_BASE = "https://rest.kegg.jp/";
    private final long timeout = 1000L * 60L * 60L * 24L * 7L * 24L; // 24 weeks
    private PathwayAccess pwacc = null;
    private ECNumberAccess ecacc = null;

    //private Connection conn;
    //
    private final static Map<String, KEGGMaster> instances = new HashMap<>();

    public static KEGGMaster getInstance(String cacheDir) throws KEGGException {
        return getInstance(cacheDir, false);
    }

    public static KEGGMaster getInstance(String cacheDir, boolean createNewDB) throws KEGGException {
        if (!instances.containsKey(cacheDir)) {
            instances.put(cacheDir, new KEGGMaster(cacheDir, createNewDB));
        }
        return instances.get(cacheDir);
    }

    private KEGGMaster(String cacheDirectory, boolean createNewDB) throws KEGGException {
        cacheDir = cacheDirectory + File.separator;

        File f = new File(cacheDir);
        if (!f.exists()) {
            if (!f.mkdirs()) {
                throw new KEGGException("Could not create " + cacheDir);
            }
        }

        // check sql driver is available
        try {
            Class.forName("org.duckdb.DuckDBDriver");
        } catch (ClassNotFoundException ex) {
            throw new KEGGException(ex);
        }

        if (createNewDB) {
            createDB();
        } else {
            installDB(f);
        }

        restclient = ClientBuilder.newBuilder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build();
    }

    public WebTarget getRESTResource() {
        return restclient.target(REST_BASE);
    }

    public PathwayAccess Pathways() {
        if (pwacc == null) {
            pwacc = new PathwayAccess(this);
        }
        return pwacc;
    }

    public ECNumberAccess ECNumber() {
        if (ecacc == null) {
            ecacc = new ECNumberAccess(this);
        }
        return ecacc;
    }

    public String getCacheDir() {
        return cacheDir;
    }

    public long getTimeout() {
        return timeout;
    }

    public final Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:duckdb:" + cacheDir + File.separator + "kegg.db");
    }

    private void createDB() throws KEGGException {
        try ( Connection c = getConnection()) {
            DatabaseMetaData dbm = c.getMetaData();
            try ( ResultSet rs = dbm.getTables(null, null, "pathway", null)) {
                if (rs.next()) {
                    // Table exists
                } else {
                    // Table does not exist
                    String sql = "CREATE TABLE IF NOT EXISTS pathway ("
                            + "   mapnum VARCHAR(10), "
                            + "   name VARCHAR(512), "
                            + "   UNIQUE(mapnum))";
                    try ( Statement stmt = c.createStatement()) {
                        stmt.execute(sql);
                    }

//                sql = "CREATE TABLE IF NOT EXISTS ecnumber ("
//                        + "   number TEXT, "
//                        + "   UNIQUE(number))";
//                conn.createStatement().execute(sql);
                    sql = "CREATE TABLE IF NOT EXISTS coords ("
                            + "   pw_num VARCHAR(10), "
                            + "   ec_num VARCHAR(20), "
                            + "   x INTEGER, "
                            + "   y INTEGER, "
                            + "   width INTEGER, "
                            + "   height INTEGER, "
                            + "   FOREIGN KEY(pw_num) REFERENCES pathway(mapnum), "
                            //                       + "   FOREIGN KEY(ec_num) REFERENCES ecnumber(number),"
                            + "   UNIQUE(pw_num, ec_num, x, y) "
                            + ")";
                    try ( Statement stmt = c.createStatement()) {
                        stmt.execute(sql);
                    }
                }
            }
        } catch (SQLException ex) {
            throw new KEGGException(ex.getMessage());
        }
    }

    private void installDB(File targetDir) throws KEGGException {
        if (!targetDir.exists()) {
            if (!targetDir.mkdirs()) {
                throw new KEGGException("Could not create " + targetDir.getAbsolutePath());
            }
        }
        if (!targetDir.canWrite()) {
            throw new KEGGException("Cannot write to " + targetDir.getAbsolutePath());
        }

        // remove previous detabase
        String target = targetDir + File.separator + "kegg.db";
        if (new File(target).exists()) {
            new File(target).delete();
        }

        try ( InputStream is = getClass().getClassLoader().getResourceAsStream("de/cebitec/mgx/kegg/kegg.db")) {
            try ( FileOutputStream rOut = new FileOutputStream(target)) {

                byte[] buffer = new byte[4096];

                int bytesRead = is.read(buffer);
                while (bytesRead >= 0) {
                    rOut.write(buffer, 0, bytesRead);
                    bytesRead = is.read(buffer);
                }

                rOut.flush();
            }
        } catch (IOException ex) {
            throw new KEGGException(ex.getMessage());
        }

        Logger.getLogger(getClass().getPackage().getName()).log(Level.INFO, "KEGG database successfully installed to {0}.", targetDir.getAbsolutePath());
    }

    @Override
    public void close() {
        restclient.close();
        instances.remove(cacheDir);
    }
}
