package de.cebitec.mgx.kegg.pathways.access;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import de.cebitec.mgx.kegg.pathways.KEGGException;
import de.cebitec.mgx.kegg.pathways.KEGGMaster;
import static de.cebitec.mgx.kegg.pathways.access.AccessBase.copyFile;
import de.cebitec.mgx.kegg.pathways.api.ECNumberI;
import de.cebitec.mgx.kegg.pathways.api.PathwayI;
import de.cebitec.mgx.kegg.pathways.model.ECNumberFactory;
import de.cebitec.mgx.kegg.pathways.model.Pathway;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;

/**
 *
 * @author sj
 */
public class PathwayAccess extends AccessBase {

    public PathwayAccess(KEGGMaster master) {
        super(master);
    }
    private final static String INSERT_PW = "INSERT INTO pathway (mapnum, name) VALUES (?, ?)";
    //private final static String INSERT_EC = "INSERT INTO ecnumber (number) VALUES (?)";
    private final static String INSERT_COORD = "INSERT INTO coords (pw_num, ec_num, x, y, width, height) VALUES (?,?,?,?,?,?)";
    private final static String FETCHALL = "SELECT mapnum, name FROM pathway";
    //
    private final static String PATHWAYS = "pathways";
    public final static String COORDS = "coords";

    public Set<PathwayI> fetchall() throws KEGGException {

        if (!isValid(PATHWAYS)) {
            fetchAllFromServer();
        }
        assert isValid(PATHWAYS);

        final Set<PathwayI> all = new HashSet<>();
        try (Connection conn = getMaster().getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement(FETCHALL)) {
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        Pathway pathway = new Pathway(rs.getString(1), rs.getString(2));
                        all.add(pathway);
                    }
                }
            }
        } catch (SQLException ex) {
            throw new KEGGException(ex);
        }

        return all;
    }

    private void fetchAllFromServer() throws KEGGException {
        try (InputStream in = get(getRESTResource(), "list/pathway")) {
            try (BufferedReader bin = new BufferedReader(new InputStreamReader(in))) {
                Connection conn = getMaster().getConnection();
                try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM coords")) {
                    stmt.execute();
                }
                try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM pathway")) {
                    stmt.execute();
                }
                //conn.prepareStatement("DELETE FROM ecnumber").execute();

                // save incoming data to a map first to avoid duplicate entries
                // which sometimes seem to occur when accessing the REST service
                Map<String, String> data = new HashMap<>();
                String line;
                while ((line = bin.readLine()) != null) {
                    line = line.substring(5); // remove leading "path:"
                    String[] split = line.split("\t");
                    if (data.containsKey(split[0])) {
                        Logger.getLogger(PathwayAccess.class.getName()).log(Level.INFO, "KEGG map {0} received more than once.", split[0]);
                    }
                    data.put(split[0], split[1]);
                }

                try (PreparedStatement stmt = conn.prepareStatement(INSERT_PW)) {
                    for (Entry<String, String> e : data.entrySet()) {
                        stmt.setString(1, e.getKey());
                        stmt.setString(2, e.getValue());
                        stmt.addBatch();
                    }
                    stmt.executeBatch();
                } catch (SQLException ex) {
                    throw new KEGGException(ex);
                }

                setValid(PATHWAYS);
            }
        } catch (IOException | SQLException ex) {
            throw new KEGGException(ex);
        }
    }

    public void fetchImageFromServer(PathwayI p) throws KEGGException {
        File cacheFile = new File(getMaster().getCacheDir() + p.getMapNumber() + ".png");

        if (isValid(cacheFile)) {
            return; // no need to refetch
        }
        // http://www.genome.jp/kegg/pathway/map/map00620.png
        try (InputStream in = get(getKEGGResource(), "kegg/pathway/map/" + p.getMapNumber() + ".png")) {
            File tmpFile = File.createTempFile(p.getMapNumber(), "tmp");
            try (OutputStream bw = new FileOutputStream(tmpFile)) {
                int read;
                byte[] bytes = new byte[1024];
                while ((read = in.read(bytes)) != -1) {
                    bw.write(bytes, 0, read);
                }
            }
            if (!tmpFile.renameTo(cacheFile)) {
                copyFile(tmpFile, cacheFile);
                tmpFile.delete();
            }
        } catch (IOException ex) {
            if (cacheFile.exists()) {
                cacheFile.delete();
            }
            throw new KEGGException(ex);
        }
    }

    public BufferedImage getImage(PathwayI p) throws KEGGException {
        BufferedImage img = null;
        File cacheFile = new File(getMaster().getCacheDir() + p.getMapNumber() + ".png");
        if (!isValid(cacheFile)) {
            fetchImageFromServer(p);
        }
        assert isValid(cacheFile);

        try {
            img = ImageIO.read(cacheFile);
        } catch (IOException ex) {
            throw new KEGGException(ex);
        }
        return img;
    }

//    private int getPathwayID(final PathwayI pw) {
//        final Result<Integer> ret = new Result<>(-1);
//        getMaster().doSQL(new SQLWrapper(getMaster()) {
//            @Override
//            public void doSQL(Connection conn) throws KEGGException {
//                 validate the pathway itself
//                try (PreparedStatement stmt = conn.prepareStatement("SELECT id FROM pathway WHERE mapnum=?")) {
//                    stmt.setString(1, pw.getMapNum());
//                    try (ResultSet rs = stmt.executeQuery()) {
//                        if (rs.next()) {
//                            ret.set(rs.getInt(1));
//                        }
//                    }
//                } catch (SQLException ex) {
//                    throw new KEGGException(ex.getMessage());
//                }
//                if (ret.get() == -1) {
//                    throw new KEGGException("No such pathway: " + pw.getMapNum());
//                }
//            }
//        });
//        return ret.get();
//    }
//
//    private int getECNumberID(Connection conn, ECNumberI ecNum) throws SQLException {
//        int ec_id = -1;
//        try (PreparedStatement stmt = conn.prepareStatement(SELECT_EC)) {
//            stmt.setString(1, ecNum.getNumber());
//            try (ResultSet rs = stmt.executeQuery()) {
//                if (rs.next()) {
//                    ec_id = rs.getInt(1);
//                }
//            }
//        }
//        if (ec_id == -1) {
//            try (PreparedStatement stmt = conn.prepareStatement(INSERT_EC)) {
//                stmt.setString(1, ecNum.getNumber());
//                stmt.executeUpdate();
//                try (PreparedStatement stmt2 = conn.prepareStatement("SELECT id FROM ecnumber WHERE number=?")) {
//                    stmt2.setString(1, ecNum.getNumber());
//                    try (ResultSet rs = stmt2.executeQuery()) {
//                        if (rs.next()) {
//                            ec_id = rs.getInt(1);
//                        }
//                    }
//                }
//            }
//        }
//        return ec_id;
//    }
    public Map<ECNumberI, Set<Rectangle>> getCoords(final PathwayI pw) throws KEGGException {

        if (!isValid(PATHWAYS)) {
            fetchall();
        }

        if (!isValid(pw)) {
            fetchCoordsFromServer(pw);
        }
        assert isValid(pw);

        // fetch from db
        final Map<ECNumberI, Set<Rectangle>> ret = new HashMap<>();
        try (Connection conn = getMaster().getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement("SELECT ec_num, x, y, width, height FROM coords WHERE pw_num=?")) {
                stmt.setString(1, pw.getMapNumber());
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        ECNumberI ec = ECNumberFactory.fromString(rs.getString(1));
                        Rectangle rect = new Rectangle(rs.getInt(2), rs.getInt(3), rs.getInt(4), rs.getInt(5));
                        if (ret.containsKey(ec)) {
                            ret.get(ec).add(rect);
                        } else {
                            Set<Rectangle> tmp = new HashSet<>();
                            tmp.add(rect);
                            ret.put(ec, tmp);
                        }
                    }
                }
            }
        } catch (SQLException ex) {
            throw new KEGGException(ex);
        }
        return ret;
    }

    private void fetchCoordsFromServer(final PathwayI pw) throws KEGGException {

        assert !isValid(pw);

        final WebResource wr = getKEGGResource()
                .path("/kegg-bin/show_pathway")
                .queryParam("org_name", "map")
                .queryParam("mapno", pw.getMapNumber().substring(3));
        //System.err.println("GET: "+wr.getURI().toASCIIString());

        ClientResponse cr = wr.header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:40.0) Gecko/20100101 Firefox/40.1").get(ClientResponse.class);
        catchException(cr);
        final Map<ECNumberI, Set<Rectangle>> data = new HashMap<>();
        // fetch and parse
        try (BufferedReader br = new BufferedReader(new InputStreamReader(cr.getEntityInputStream()))) {
            Pattern splitSpaces = Pattern.compile("\\s+");
            Pattern ecNumber = Pattern.compile("\\d+[.](-|\\d+)[.](-|\\d+)[.](-|\\d+)");
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("<area shape=rect")) {

                    String[] split = splitSpaces.split(line);

                    String coords = split[2].substring(7);
                    String[] corners = coords.split(",");
                    if (corners.length != 4) {
                        throw new KEGGException("invalid line: " + line);
                    }

                    Rectangle rect = null;
                    try {
                        int x = Integer.parseInt(corners[0]);
                        int y = Integer.parseInt(corners[1]);
                        int width = Integer.parseInt(corners[2]) - x;
                        int height = Integer.parseInt(corners[3]) - y;
                        rect = new Rectangle(x, y, width, height);
                    } catch (NumberFormatException nfe) {
                        throw new KEGGException("invalid line: " + line);
                    }

                    if (line.contains("title=\"")) {
                        String titleString = line.substring(line.indexOf("title=\"") + 7);
                        titleString = titleString.substring(0, titleString.indexOf('"'));

                        Matcher matcher = ecNumber.matcher(titleString);
                        if (matcher.find()) {
//                            ECNumberI ecNum = new ECNumber(titleString.substring(matcher.start(), matcher.end()));
                            ECNumberI ecNum = ECNumberFactory.fromString(titleString.substring(matcher.start(), matcher.end()));
                            if (data.containsKey(ecNum)) {
                                data.get(ecNum).add(rect);
                            } else {
                                Set<Rectangle> tmp = new HashSet<>();
                                tmp.add(rect);
                                data.put(ecNum, tmp);
                            }
                        }
                    }
                }
            }
        } catch (IOException ex) {
            throw new KEGGException(ex);
        }
        // save to db
        try (Connection conn = getMaster().getConnection()) {
            try (PreparedStatement stmt1 = conn.prepareStatement("DELETE FROM coords WHERE pw_num=?")) {
                stmt1.setString(1, pw.getMapNumber());
                stmt1.executeUpdate();
            }
            try (PreparedStatement stmt1 = conn.prepareStatement("DELETE FROM timestamps WHERE type=?")) {
                stmt1.setString(1, COORDS + "_" + pw.getMapNumber());
                stmt1.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new KEGGException(ex);
        }

        try (Connection conn = getMaster().getConnection()) {
            for (Entry<ECNumberI, Set<Rectangle>> e : data.entrySet()) {
                try (PreparedStatement stmt = conn.prepareStatement(INSERT_COORD)) {
                    for (Rectangle rect : e.getValue()) {
                        stmt.setString(1, pw.getMapNumber());
                        stmt.setString(2, e.getKey().getNumber());
                        stmt.setInt(3, rect.x);
                        stmt.setInt(4, rect.y);
                        stmt.setInt(5, rect.width);
                        stmt.setInt(6, rect.height);
                        stmt.addBatch();
                    }
                    stmt.executeBatch();
                }
            }

            setValid(COORDS + "_" + pw.getMapNumber());
        } catch (SQLException ex) {
            throw new KEGGException(ex);
        }
    }

    public Set<PathwayI> getMatchingPathways(final ECNumberI ec) throws KEGGException {

        Set<PathwayI> allPW = fetchall();
        final CountDownLatch done = new CountDownLatch(allPW.size());
        for (final PathwayI pw : allPW) {

            if (isValid(pw)) {
                done.countDown();
            } else {
                getPool().execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            getCoords(pw);
                        } catch (KEGGException ex) {
                            Logger.getLogger(PathwayAccess.class.getName()).log(Level.SEVERE, null, ex);
                        } finally {
                            done.countDown();
                        }
                    }
                });
            }
        }
        try {
            done.await();
        } catch (InterruptedException ex) {
            throw new KEGGException(ex);
        }

        final Set<PathwayI> ret = new HashSet<>();

        try (Connection conn = getMaster().getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement("SELECT DISTINCT mapnum, name FROM pathway LEFT JOIN coords ON (pathway.mapnum=coords.pw_num) WHERE coords.ec_num=?")) {
                stmt.setString(1, ec.getNumber());
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        ret.add(new Pathway(rs.getString(1), rs.getString(2)));
                    }
                }
            }
        } catch (SQLException ex) {
            throw new KEGGException(ex);
        }
        return ret;
    }

    public Set<PathwayI> getMatchingPathways(final Set<ECNumberI> ecs) throws KEGGException {

        Set<PathwayI> allPW = fetchall();
        final CountDownLatch done = new CountDownLatch(allPW.size());
        for (final PathwayI pw : allPW) {
            if (isValid(pw)) {
                done.countDown();
            } else {
                getPool().execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            getCoords(pw);
                        } catch (KEGGException ex) {
                            Logger.getLogger(PathwayAccess.class.getName()).log(Level.SEVERE, null, ex);
                        } finally {
                            done.countDown();
                        }
                    }
                });
            }
        }
        try {
            done.await();
        } catch (InterruptedException ex) {
            throw new KEGGException(ex);
        }

        StringBuilder sql = new StringBuilder()
                .append("SELECT DISTINCT mapnum, name FROM pathway LEFT JOIN coords ON (pathway.mapnum=coords.pw_num) WHERE coords.ec_num IN (")
                .append(join(ecs, ",", "'"))
                .append(")");

        final Set<PathwayI> ret = new HashSet<>();

        try (Connection conn = getMaster().getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        ret.add(new Pathway(rs.getString(1), rs.getString(2)));
                    }
                }
            }
        } catch (SQLException ex) {
            throw new KEGGException(ex);
        }
        return ret;
    }

    private static String join(final Iterable< ? extends Object> pColl, String separator, String quoteChar) {
        Iterator< ? extends Object> oIter;
        if (pColl == null || (!(oIter = pColl.iterator()).hasNext())) {
            return "";
        }
        StringBuilder oBuilder = new StringBuilder(quoteChar + String.valueOf(oIter.next() + quoteChar));
        while (oIter.hasNext()) {
            oBuilder.append(separator).append(quoteChar).append(oIter.next()).append(quoteChar);
        }
        return oBuilder.toString();
    }
}
