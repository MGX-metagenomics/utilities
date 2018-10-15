package de.cebitec.mgx.kegg.pathways;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import de.cebitec.mgx.kegg.pathways.access.AccessBase;
import de.cebitec.mgx.kegg.pathways.access.ECNumberAccess;
import de.cebitec.mgx.kegg.pathways.access.PathwayAccess;
import java.io.File;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.h2.jdbcx.JdbcConnectionPool;

/**
 *
 * @author sj
 */
public class KEGGMaster implements AutoCloseable {

    private final Client restclient;
    private final Client keggclient;
    private final String cacheDir;
    private final static String REST_BASE = "http://rest.kegg.jp/";
    private final static String KEGG_BASE = "https://www.genome.jp/";
    private final static String CACHEDIR = "/tmp/kegg/";
    private final long timeout = 1000L * 60L * 60L * 24L * 7L * 24L; // 24 weeks
    private PathwayAccess pwacc = null;
    private ECNumberAccess ecacc = null;
    private JdbcConnectionPool pool = null;

    //private Connection conn;
    //
    private final static Map<String, KEGGMaster> instances = new HashMap<>();

    public static KEGGMaster getInstance(String cacheDir) throws KEGGException {
        if (!instances.containsKey(cacheDir)) {
            instances.put(cacheDir, new KEGGMaster(cacheDir));
        }
        return instances.get(cacheDir);
    }

    public static KEGGMaster getInstance() throws KEGGException {
        return getInstance(CACHEDIR);
    }

    private KEGGMaster() throws KEGGException {
        this(CACHEDIR);
    }

    private KEGGMaster(String cacheDirectory) throws KEGGException {
        cacheDir = cacheDirectory + File.separator;
        ClientConfig cc = new DefaultClientConfig();
        cc.getProperties().put(ClientConfig.PROPERTY_THREADPOOL_SIZE, 30);
        restclient = Client.create(cc);
        keggclient = Client.create(cc);
        File f = new File(cacheDir);
        if (!f.exists()) {
            if (!f.mkdirs()) {
                throw new KEGGException("Could not create " + cacheDir);
            }
        }
        try {
            Class.forName("org.h2.Driver");
            //conn = DriverManager.getConnection("jdbc:h2:" + cacheDir + File.separator + "kegg" + ";DEFAULT_TABLE_ENGINE=org.h2.mvstore.db.MVTableEngine");
            pool = JdbcConnectionPool.create("jdbc:h2:" + cacheDir + File.separator + "kegg" + ";DEFAULT_TABLE_ENGINE=org.h2.mvstore.db.MVTableEngine", "sa", "sa");
            checkDBH2();
        } catch (ClassNotFoundException | SQLException ex) {
            throw new KEGGException(ex);
        }
//        try {
//            Class.forName("org.hsqldb.jdbc.JDBCDriver");
//            conn = DriverManager.getConnection("jdbc:hsqldb:file:" + cacheDir + File.separator + "kegg.hsqldb", "SA", "");
//            checkDBHSQL();
//        } catch (ClassNotFoundException | SQLException ex) {
//            throw new KEGGException(ex.getMessage());
//        }
//        try {
//            Class.forName("org.sqlite.JDBC");
//            conn = DriverManager.getConnection("jdbc:sqlite:" + cacheDir + File.separator + "kegg.db");
//            conn.prepareStatement("PRAGMA foreign_keys = ON").execute();
//            checkDB();
//        } catch (ClassNotFoundException | SQLException ex) {
//            throw new KEGGException(ex.getMessage());
//        }
    }

    public WebResource getRESTResource() {
        return restclient.resource(REST_BASE);
    }

    public WebResource getKEGGResource() {
        WebResource ret = keggclient.resource(KEGG_BASE);
        return ret;
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
        assert timeout > 0;
        return timeout;
    }

    public final Connection getConnection() throws SQLException {
        return pool.getConnection();
        //return DriverManager.getConnection("jdbc:h2:" + cacheDir + File.separator + "kegg" + ";DEFAULT_TABLE_ENGINE=org.h2.mvstore.db.MVTableEngine");
    }

    private void checkDBH2() throws SQLException {
        try (Connection conn = getConnection()) {
            DatabaseMetaData dbm = conn.getMetaData();
            try (ResultSet rs = dbm.getTables(null, null, "pathway", null)) {
                if (rs.next()) {
                    // Table exists
                } else {
                    // Table does not exist
                    String sql = "CREATE TABLE IF NOT EXISTS pathway ("
                            + "   mapnum VARCHAR(10), "
                            + "   name VARCHAR(512), "
                            + "   UNIQUE(mapnum))";
                    try (Statement stmt = conn.createStatement()) {
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
                    try (Statement stmt = conn.createStatement()) {
                        stmt.execute(sql);
                    }

                    sql = "CREATE TABLE IF NOT EXISTS timestamps ("
                            + "type VARCHAR(30), "
                            + "time DATETIME)";
                    try (Statement stmt = conn.createStatement()) {
                        stmt.execute(sql);
                    }
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(KEGGMaster.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public boolean isValid(final String type) {
        boolean ret = false;
        try (Connection conn = getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement("SELECT time FROM timestamps WHERE type=?")) {
                stmt.setString(1, type);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        String s = rs.getString(1);
                        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                        Date lastFetch = df.parse(s);

                        Date earliestValidDate = new Date(System.currentTimeMillis() - getTimeout());
                        ret = (earliestValidDate.before(lastFetch) || earliestValidDate.equals(lastFetch));
                    }
                }
            }
        } catch (SQLException | ParseException ex) {
            Logger.getLogger(KEGGMaster.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ret;
    }

    public void setValid(final String type) {
        System.err.println("now valid: "+ type);
        try (Connection conn = getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement("UPDATE timestamps SET time=CURRENT_DATE() WHERE type=?")) {
                stmt.setString(1, type);
                stmt.execute();
                int updateCnt = stmt.getUpdateCount();
                if (updateCnt != 1) {
                    try (PreparedStatement pstmt = conn.prepareStatement("INSERT INTO timestamps (type, time) VALUES (?, CURRENT_DATE)")) {
                        pstmt.setString(1, type);
                        pstmt.executeUpdate();
                    }
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(AccessBase.class.getName()).log(Level.SEVERE, null, ex.getMessage());
        }
    }

    @Override
    public void close() throws Exception {
    }
}
