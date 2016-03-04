package de.cebitec.mgx.admin;

import com.jolbox.bonecp.BoneCPConfig;
import com.jolbox.bonecp.BoneCPDataSource;
import de.cebitec.mgx.admin.misc.Pair;
import de.cebitec.mgx.admin.misc.Reference;
import de.cebitec.mgx.admin.misc.Region;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Console;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.sql.DataSource;
import org.biojava.bio.Annotation;
import org.biojava.bio.BioException;
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
//        Console con = System.console();
//        char[] password = con.readPassword("Admin password: ");
//        DataSource ds = createDataSource(new String(password));
//        Connection conn = ds.getConnection();
//        List<String> globalRefs = listReferences(conn);

        List<String> fnames = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader("/home/sj/genomes.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                fnames.add("/home/sj/" + line);
            }
        }

        ExecutorService pool = Executors.newFixedThreadPool(8);
        final CountDownLatch allDone = new CountDownLatch(fnames.size());

        for (final String fn : fnames) {
            pool.submit(new Runnable() {
                @Override
                public void run() {
                    readReference(fn);
                    allDone.countDown();
                }
            });
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
        allDone.await();
    }

    private static void readReference(String fname) {
        Map<Pair<String, Reference>, String> ret = null; //new HashMap<>();
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
                //System.err.println("Name: " + seqname + "      (" + fname + ")");

                Reference ref = new Reference();
                ref.setName(seqname);
                ref.setRegions(new LinkedList<Region>());

                Iterator<Feature> iter = rs.features();
                while (iter.hasNext()) {
                    RichFeature elem = (RichFeature) iter.next();

                    if (elem.getType().equals("CDS") || elem.getType().equals("rRNA") || elem.getType().equals("tRNA")) {
                        if (genomeSeq == null) {
                            genomeSeq = elem.getSequence().seqString();
                        }
//
//                        Region region = new Region();
//                        region.setReference(ref);
//                        Annotation annot = elem.getAnnotation();
//                        region.setName((String) annot.getProperty("locus_tag"));
//                        if (annot.containsProperty("product")) {
//                            region.setDescription((String) annot.getProperty("product"));
//                        } else if (annot.containsProperty("function")) {
//                            region.setDescription((String) annot.getProperty("function"));
//                        } else {
//                            region.setDescription("");
//                        }
//
//                        int abs_start, abs_stop;
//                        if (elem.getStrand().getValue() == 1) {
//                            abs_start = elem.getLocation().getMin() - 1;
//                            abs_stop = elem.getLocation().getMax() - 1;
//                        } else {
//                            abs_stop = elem.getLocation().getMin() - 1;
//                            abs_start = elem.getLocation().getMax() - 1;
//                        }
//                        region.setStart(abs_start);
//                        region.setStop(abs_stop);
//
//                        ref.getRegions().add(region);
                    }
                }

                if (genomeSeq == null || genomeSeq.isEmpty()) {
                    System.err.println(fname + " NO SEQ!!!");
                } else {
                    ref.setLength(genomeSeq.length());
                }

                //System.out.println(ref.getName() + " has " + String.valueOf(ref.getLength()) + "bp");
                //ret.put(new Pair<>(rs.getName(), ref), genomeSeq);
                //saveReference(rs.getName(), ref, genomeSeq, conn);
            }

            br.close();
        } catch (IOException | NoSuchElementException | BioException ex) {
            System.err.println(fname + ": " + ex.getMessage() + "   (" + seqname + ")");
        }
        //return ret;
    }

    private static void saveReference(String accession, Reference ref, String dnaSeq, Connection conn) throws Exception {
        BufferedWriter bw = new BufferedWriter(new FileWriter(GLOBAL_DIR + accession + ".fas"));
        bw.append(">");
        bw.append(accession);
        bw.newLine();
        bw.append(dnaSeq);
        bw.newLine();
        bw.close();
        ref.setFile(GLOBAL_DIR + accession + ".fas");
        try (PreparedStatement stmt = conn.prepareStatement(ADD_REF)) {
            stmt.setString(1, ref.getName());
            stmt.setInt(2, ref.getLength());
            stmt.setString(3, ref.getFile());
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    throw new Exception("error");
                }
                Long id = rs.getLong(1);
                ref.setId(id);
            }
        }
        try (PreparedStatement stmt = conn.prepareStatement(ADD_REGION)) {
            for (Region r : ref.getRegions()) {
                stmt.setString(1, r.getName());
                stmt.setString(2, r.getDescription());
                stmt.setInt(3, r.getStart());
                stmt.setInt(4, r.getStop());
                stmt.setLong(5, ref.getId());
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
    private static final String ADD_REGION = "INSERT INTO region (name, description, reg_start, reg_stop, ref_id) VALUES (?,?,?, ?, ?)";

    public static DataSource createDataSource(String pw) throws ClassNotFoundException {
        Class.forName("org.postgresql.Driver");
        String jdbc = "jdbc:postgresql://mgx.postgresql:5432/MGX_global";

        BoneCPConfig cfg = new BoneCPConfig();
        cfg.setLazyInit(true);
        cfg.setMaxConnectionsPerPartition(5);
        cfg.setMinConnectionsPerPartition(2);
        cfg.setPartitionCount(1);
        cfg.setJdbcUrl(jdbc);
        cfg.setUsername("mgxadm");
        cfg.setPassword(pw);
        cfg.setCloseConnectionWatch(false);
        cfg.setMaxConnectionAgeInSeconds(600);
        cfg.setAcquireRetryAttempts(0);

        return new BoneCPDataSource(cfg);
    }
}
