package de.cebitec.mgx.admin;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import de.cebitec.mgx.model.db.Reference;
import de.cebitec.mgx.model.db.Region;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Console;
import java.io.FileReader;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.sql.DataSource;
import org.biojava.bio.Annotation;
import org.biojava.bio.seq.Feature;
import org.biojavax.Namespace;
import org.biojavax.RichObjectFactory;
import org.biojavax.bio.seq.RichFeature;
import org.biojavax.bio.seq.RichSequence;
import org.biojavax.bio.seq.RichSequenceIterator;

/**
 *
 *
 */
public class App {

    public static final String GLOBAL_DIR = "/vol/mgx-data/GLOBAL/references/";

    public static void main(String[] args) throws Exception {
        Console con = System.console();
        char[] password = con.readPassword("Admin password: ");
        DataSource ds = createDataSource("gpmsroot", new String(password));
        Connection conn = ds.getConnection();
//        List<String> globalRefs = listReferences(conn);

        List<String> fnames = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader("/home/sj/genomes.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                fnames.add("/home/sj/" + line);
            }
        }


        for (final String fn : fnames) {
            readReference(fn, conn);
//            for (Map.Entry<Pair<String, Reference>, String> me : refs.entrySet()) {
//                Pair<String, Reference> p = me.getKey();
//                if (!globalRefs.contains(p.getSecond().getName())) {
//                    globalRefs.add(p.getSecond().getName());
//                    System.err.println("importing " + p.getSecond().getName());
//                    //saveReference(p.getFirst(), p.getSecond(), me.getValue(), conn);
//                } else {
//                    System.err.println("skipping " + p.getSecond().getName() + ", already present");
//                }
//            }
        }
    }

    private static void readReference(String fname, Connection conn) {
//        Map<Pair<String, Reference>, String> ret = new HashMap<>();
        String seqname = "";

        try {
            BufferedReader br = new BufferedReader(new FileReader(fname));
            Namespace ns = RichObjectFactory.getDefaultNamespace();
            br.mark(10);
            Character first = (char) br.read();
            br.reset();
            RichSequenceIterator seqs;
            if (first.toString().equals("L")) {
                seqs = RichSequence.IOTools.readGenbankDNA(br, ns);
            } else {
                seqs = RichSequence.IOTools.readEMBLDNA(br, ns);
            }

            seqname = "";
            String genomeSeq = null;

            while (seqs.hasNext()) {
                RichSequence rs = seqs.nextRichSequence();

                seqname = rs.getDescription().replaceAll("\n", " ").trim();
                if (seqname.endsWith(", complete sequence.")) {
                    int trimPos = seqname.lastIndexOf(", complete sequence.");
                    seqname = seqname.substring(0, trimPos);
                }
                if (seqname.endsWith(", complete genome.")) {
                    int trimPos = seqname.lastIndexOf(", complete genome.");
                    seqname = seqname.substring(0, trimPos);
                }
                if (seqname.endsWith("complete genome")) {
                    int trimPos = seqname.lastIndexOf("complete genome");
                    seqname = seqname.substring(0, trimPos);
                }
                if (seqname.endsWith(".")) {
                    int trimPos = seqname.lastIndexOf(".");
                    seqname = seqname.substring(0, trimPos);
                }

                if (seqname.contains("PROGRESS") || seqname.contains("draft") || seqname.contains("fragment") || seqname.contains("incision element")) {  //avoid incomplete sequences
                    continue;
                }
                if (seqname.contains("whole genome shotgun")) {  //avoid incomplete sequences
                    continue;
                }

                seqname = seqname.trim();
                System.err.println("Name: " + seqname + "      (" + fname + ")");

                Reference ref = new Reference();
                ref.setName(seqname);
                List<Region> regions = new ArrayList<>();

                Iterator<Feature> iter = rs.features();
                while (iter.hasNext()) {
                    RichFeature elem = (RichFeature) iter.next();

                    if (elem.getType().equals("CDS") || elem.getType().equals("rRNA") || elem.getType().equals("tRNA")) {
                        if (genomeSeq == null) {
                            genomeSeq = elem.getSequence().seqString();
                        }

                        Region region = new Region();
                        region.setType(elem.getType());
                        Annotation annot = elem.getAnnotation();
                        region.setName((String) annot.getProperty("locus_tag"));
                        if (annot.containsProperty("product")) {
                            region.setDescription((String) annot.getProperty("product"));
                        } else if (annot.containsProperty("function")) {
                            region.setDescription((String) annot.getProperty("function"));
                        } else {
                            region.setDescription("");
                        }

                        int abs_start, abs_stop;
                        if (elem.getStrand().getValue() == 1) {
                            abs_start = elem.getLocation().getMin() - 1;
                            abs_stop = elem.getLocation().getMax() - 1;
                        } else {
                            abs_stop = elem.getLocation().getMin() - 1;
                            abs_start = elem.getLocation().getMax() - 1;
                        }
                        region.setStart(abs_start);
                        region.setStop(abs_stop);

                        regions.add(region);
                    }
                }

                if (genomeSeq == null || genomeSeq.isEmpty()) {
                    System.err.println(fname + " NO SEQ!!!");
                } else {
                    ref.setLength(genomeSeq.length());
                }
//
                saveReference(rs.getName(), ref, regions, genomeSeq, conn);
            }

            br.close();
        } catch (Exception ex) {
            System.err.println(fname + ": " + ex.getMessage() + "   (" + seqname + ")");
        }
        //return ret;
    }

    private static void saveReference(String accession, Reference ref, List<Region> regions, String dnaSeq, Connection conn) throws Exception {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(GLOBAL_DIR + accession + ".fas"))) {
            bw.append(">");
            bw.append(accession);
            bw.newLine();
            bw.append(dnaSeq.toUpperCase());
            bw.newLine();
        }
        ref.setFile(GLOBAL_DIR + accession + ".fas");
        try (PreparedStatement stmt = conn.prepareStatement(ADD_REF)) {
            stmt.setString(1, ref.getName());
            stmt.setInt(2, ref.getLength());
            stmt.setString(3, ref.getFile());
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    throw new Exception("error");
                }
                ref.setId(rs.getLong(1));
            }
        }
        try (PreparedStatement stmt = conn.prepareStatement(ADD_REGION)) {
            for (Region r : regions) {
                stmt.setString(1, r.getName());
                stmt.setString(2, r.getType());
                stmt.setString(3, r.getDescription());
                stmt.setInt(4, r.getStart());
                stmt.setInt(5, r.getStop());
                stmt.setLong(6, ref.getId());
                stmt.addBatch();
            }
            stmt.executeBatch();
        }
    }

    private static List<String> listReferences(Connection conn) {
        List<String> ret = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(GET_REFS)) {
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Long id = rs.getLong(1);
                    String name = rs.getString(2);
                    int length = rs.getInt(3);
                    String path = rs.getString(4);
                    //System.out.println("id: " + String.valueOf(id) + " " + name);
                    ret.add(name);
                }
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
            System.exit(1);
            return null;
        }
        return ret;
    }
    private static final String GET_REFS = "SELECT id, name, ref_length, ref_filepath FROM reference";
    private static final String ADD_REF = "INSERT INTO reference (name, ref_length, ref_filepath) VALUES (?,?,?) RETURNING id";
    private static final String ADD_REGION = "INSERT INTO region (name, type, description, reg_start, reg_stop, ref_id) VALUES (?,?,?,?,?,?)";

    public static DataSource createDataSource(String userName, String pw) throws ClassNotFoundException {
        Class.forName("org.postgresql.Driver");
        String jdbc = "jdbc:postgresql://postgresql.internal.computational.bio.uni-giessen.de:5432/MGX_global";

        HikariConfig cfg = new HikariConfig();
        cfg.setPoolName("globalPool");
        //cfg.setMinimumPoolSize(5);
        cfg.setMaximumPoolSize(20);
        cfg.setMinimumIdle(2);
        cfg.setDataSourceClassName("org.postgresql.ds.PGSimpleDataSource");
        cfg.addDataSourceProperty("user", userName);
        cfg.addDataSourceProperty("password", pw);
        cfg.addDataSourceProperty("serverName", "postgresql.internal.computational.bio.uni-giessen.de");
        cfg.addDataSourceProperty("portNumber", 5432);
        cfg.addDataSourceProperty("databaseName", "MGX_global");
        cfg.setConnectionTimeout(1500); // ms
        cfg.setMaxLifetime(1000 * 60 * 2);  // 2 mins
        cfg.setIdleTimeout(1000 * 60);
        cfg.setLeakDetectionThreshold(20000); // 20 sec before in-use connection is considered leaked

        return new HikariDataSource(cfg);
    }
}
