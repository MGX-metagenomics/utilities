package de.cebitec.mgx.kegg.pathways.access;

import de.cebitec.mgx.kegg.pathways.KEGGException;
import de.cebitec.mgx.kegg.pathways.KEGGMaster;
import static de.cebitec.mgx.kegg.pathways.access.AccessBase.copyFile;
import de.cebitec.mgx.kegg.pathways.api.ECNumberI;
import de.cebitec.mgx.kegg.pathways.api.PathwayI;
import de.cebitec.mgx.kegg.pathways.model.ECNumberFactory;
import de.cebitec.mgx.kegg.pathways.model.Pathway;
import java.awt.Color;
import java.awt.Graphics2D;
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
import java.util.Collection;
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
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;
import java.util.concurrent.atomic.AtomicInteger;

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
        try ( Connection conn = getMaster().getConnection()) {
            try ( PreparedStatement stmt = conn.prepareStatement(FETCHALL)) {
                try ( ResultSet rs = stmt.executeQuery()) {
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

        try ( InputStream in = get(getRESTResource(), "list", "pathway")) {
            rwl.writeLock().lockInterruptibly();

            // re-check validity after entering critical section
            if (isValid(PATHWAYS)) {
                return;
            }

            try ( BufferedReader bin = new BufferedReader(new InputStreamReader(in))) {
                try ( Connection conn = getMaster().getConnection()) {
                    try ( PreparedStatement stmt = conn.prepareStatement("DELETE FROM coords")) {
                        stmt.execute();
                    }
                    try ( PreparedStatement stmt = conn.prepareStatement("DELETE FROM pathway")) {
                        stmt.executeUpdate();
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

                    try ( PreparedStatement stmt = conn.prepareStatement(INSERT_PW)) {
                        for (Entry<String, String> e : data.entrySet()) {
                            stmt.setString(1, e.getKey());
                            stmt.setString(2, e.getValue());
                            stmt.addBatch();
                        }
                        stmt.executeBatch();
                    }
                }

                setValid(PATHWAYS);
            }
        } catch (IOException | SQLException | InterruptedException ex) {
            Exception ex1 = ex;
            while (ex1.getCause() != null && ex1.getCause() instanceof Exception) {
                ex1 = (Exception) ex1.getCause();
            }
            throw new KEGGException(ex);
        } finally {
            rwl.writeLock().unlock();
        }
    }

    public void fetchImageFromServer(PathwayI p) throws KEGGException {
        File cacheFile = new File(getMaster().getCacheDir() + p.getMapNumber() + ".png");

        if (isValid(cacheFile)) {
            return; // no need to refetch
        }
        // http://www.genome.jp/kegg/pathway/map/map00620.png
        // REST: https://rest.kegg.jp/get/map07045/image
        // try ( InputStream in = get(getKEGGResource(), "kegg", "pathway", "map", p.getMapNumber() + ".png")) {
        try ( InputStream in = get(getRESTResource(), "get", p.getMapNumber(), "image")) {
            File tmpFile = File.createTempFile(p.getMapNumber(), "tmp");
            try ( OutputStream bw = new FileOutputStream(tmpFile)) {
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
        } catch (IOException | ProcessingException ex) {
            if (cacheFile.exists()) {
                cacheFile.delete();
            }
            Exception ex1 = ex;
            while (ex1.getCause() != null && ex1.getCause() instanceof Exception) {
                ex1 = (Exception) ex1.getCause();
            }
            throw new KEGGException(ex1.getMessage());
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

        // check for black 1px border
        if (img.getRGB(0, 0) == -16777216 && img.getRGB(1, 1) == -1) {
            Graphics2D g2 = img.createGraphics();
            g2.setColor(new Color(-1));
            g2.drawRect(0, 0, img.getWidth() - 1, img.getHeight() - 1);
        }

        return img;
    }

    public Map<ECNumberI, Collection<Rectangle>> getCoords(final PathwayI pw) throws KEGGException {

        if (!isValid(PATHWAYS)) {
            fetchall();
        }

        if (!isValid(pw)) {
            fetchCoordsFromServer(pw);
        }
        assert isValid(pw);

        // fetch from db
        final Map<ECNumberI, Collection<Rectangle>> ret = new HashMap<>();
        try ( Connection conn = getMaster().getConnection()) {

            try {
                rwl.readLock().lockInterruptibly();
            } catch (InterruptedException ex) {
                Logger.getLogger(PathwayAccess.class.getName()).log(Level.SEVERE, null, ex);
                return null;
            }

            try ( PreparedStatement stmt = conn.prepareStatement("SELECT ec_num, x, y, width, height FROM coords WHERE pw_num=?")) {
                stmt.setString(1, pw.getMapNumber());
                try ( ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        ECNumberI ec = ECNumberFactory.fromString(rs.getString(1));
                        Rectangle rect = new Rectangle(rs.getInt(2), rs.getInt(3), rs.getInt(4), rs.getInt(5));
                        if (ret.containsKey(ec)) {
                            ret.get(ec).add(rect);
                        } else {
                            Collection<Rectangle> tmp = new HashSet<>();
                            tmp.add(rect);
                            ret.put(ec, tmp);
                        }
                    }
                }
            }
        } catch (SQLException ex) {
            throw new KEGGException(ex);
        } finally {
            rwl.readLock().unlock();
        }
        return ret;
    }

    private void fetchCoordsFromServer(final PathwayI pw) throws KEGGException {

        assert !isValid(pw);

        // switch to kegg.jp?
        // https://www.kegg.jp/pathway/map00052
//        final WebTarget wr = getKEGGResource()
//                .path("kegg-bin").path("show_pathway")
//                .queryParam("org_name", "map")
//                .queryParam("mapno", pw.getMapNumber().substring(3));
        final WebTarget wr = getRESTResource()
                .path("get").path(pw.getMapNumber()).path("conf");

        Response res = wr.request().header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:40.0) Gecko/20100101 Firefox/40.1").get(Response.class);

        try {
            catchException(res);
        } catch (KEGGException ex) {
            throw new KEGGException(wr.getUri().toASCIIString() + " failed: " + ex.getMessage());
        }
        final Map<ECNumberI, Set<Rectangle>> data = new HashMap<>();
        // fetch and parse
        try ( BufferedReader br = new BufferedReader(new InputStreamReader(res.readEntity(InputStream.class)))) {
            Pattern splitTSV = Pattern.compile("\\t");
            Pattern splitSpaces = Pattern.compile("\\s+");
            Pattern ecNumber = Pattern.compile("\\d+[.](-|\\d+)[.](-|\\d+)[.](-|\\d+)");
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("rect ")) {

                    //
                    // rect (297,202) (343,219)        /dbget-bin/www_bget?3.2.2.25+R07918     3.2.2.25, R07918
                    //
                    String[] columns = splitTSV.split(line.substring(5));

                    String[] split = splitSpaces.split(columns[0]);

                    String coordString1 = split[0].replace("(", "").replace(")", "");
                    String coordString2 = split[1].replace("(", "").replace(")", "");

                    String[] corners1 = coordString1.split(",");
                    String[] corners2 = coordString2.split(",");
                    if (corners1.length != 2 || corners2.length != 2) {
                        throw new KEGGException("invalid line: " + line);
                    }

                    Rectangle rect = null;
                    try {
                        int x = Integer.parseInt(corners1[0]);
                        int y = Integer.parseInt(corners1[1]);
                        int width = Integer.parseInt(corners2[0]) - x;
                        int height = Integer.parseInt(corners2[1]) - y;
                        rect = new Rectangle(x, y, width, height);
                    } catch (NumberFormatException nfe) {
                        throw new KEGGException("invalid line: " + line);
                    }

                    Matcher matcher = ecNumber.matcher(columns[2]);
                    if (matcher.find()) {
                        ECNumberI ecNum = ECNumberFactory.fromString(columns[2].substring(matcher.start(), matcher.end()));
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
        } catch (IOException ex) {
            Exception ex1 = ex;
            while (ex1.getCause() != null && ex1.getCause() instanceof Exception) {
                ex1 = (Exception) ex1.getCause();
            }
            throw new KEGGException(ex);
        }
        // save to db
        try ( Connection conn = getMaster().getConnection()) {

            rwl.writeLock().lockInterruptibly();

            // re-check validity after entering critical section
            if (isValid(pw)) {
                return;
            }

            try ( PreparedStatement stmt1 = conn.prepareStatement("DELETE FROM coords WHERE pw_num=?")) {
                stmt1.setString(1, pw.getMapNumber());
                stmt1.executeUpdate();
            }
            try ( PreparedStatement stmt1 = conn.prepareStatement("DELETE FROM timestamps WHERE type=?")) {
                stmt1.setString(1, COORDS + "_" + pw.getMapNumber());
                stmt1.executeUpdate();
            }
        } catch (SQLException | InterruptedException ex) {
            throw new KEGGException(ex);
        } finally {
            rwl.writeLock().unlock();
        }

        try ( Connection conn = getMaster().getConnection()) {
            rwl.writeLock().lockInterruptibly();

            // re-check validity after entering critical section
            if (isValid(pw)) {
                return;
            }

            for (Entry<ECNumberI, Set<Rectangle>> e : data.entrySet()) {
                try ( PreparedStatement stmt = conn.prepareStatement(INSERT_COORD)) {
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
        } catch (SQLException | InterruptedException ex) {
            throw new KEGGException(ex);
        } finally {
            rwl.writeLock().unlock();
        }
    }

    public Collection<PathwayI> getMatchingPathways(final ECNumberI ec) throws KEGGException {

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

        try ( Connection conn = getMaster().getConnection()) {
            try ( PreparedStatement stmt = conn.prepareStatement("SELECT DISTINCT mapnum, name FROM pathway LEFT JOIN coords ON (pathway.mapnum=coords.pw_num) WHERE coords.ec_num=?")) {
                stmt.setString(1, ec.getNumber());
                try ( ResultSet rs = stmt.executeQuery()) {
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

    public Collection<PathwayI> getMatchingPathways(final Set<ECNumberI> ecs) throws KEGGException {

        Set<PathwayI> allPW = fetchall();
        final CountDownLatch done = new CountDownLatch(allPW.size());

        AtomicInteger success = new AtomicInteger(0);
        for (final PathwayI pw : allPW) {
            if (isValid(pw)) {
                done.countDown();
            } else {
                getPool().execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            getCoords(pw);
                            success.addAndGet(1);
                        } catch (KEGGException ex) {
                            System.err.println("ERROR after " + success.get());
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

        String sql = "SELECT DISTINCT mapnum, name FROM pathway LEFT JOIN coords ON (pathway.mapnum=coords.pw_num) WHERE coords.ec_num IN ("
                + join(ecs, ",", "'")
                + ")";

        final Set<PathwayI> ret = new HashSet<>();

        try ( Connection conn = getMaster().getConnection()) {
            try ( PreparedStatement stmt = conn.prepareStatement(sql)) {
                try ( ResultSet rs = stmt.executeQuery()) {
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
