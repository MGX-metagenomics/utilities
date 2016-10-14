package de.cebitec.mgx.admin;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import de.cebitec.mgx.model.db.Reference;
import de.cebitec.mgx.model.db.Region;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Console;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;
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

    private final static String FTP_PREFIX = "ftp://ftp.ncbi.nlm.nih.gov/genomes/all/";
    private final static String LOCAL_DIR = "/vol/biodb/ncbi_genomes/all/";

    public static void main(String[] args) throws Exception {
        Console con = System.console();
        char[] password = con.readPassword("Admin password: ");
        DataSource ds = createDataSource("gpmsroot", new String(password));
        List<String> globalRefs = listReferences(ds);
        for (String s : globalRefs) {
            System.out.println(s);
        }

        List<File> files = new ArrayList<>();

        File summary = new File("/vol/biodb/ncbi_genomes/refseq/bacteria/assembly_summary.txt");
        try (BufferedReader br = new BufferedReader(new FileReader(summary))) {
            String line;
            while (null != (line = br.readLine())) {
                if (!line.startsWith("#")) {
                    String[] parts = line.split("\t");
                    if ("latest".equals(parts[10]) && "Complete Genome".equals(parts[11])) {
                        if ("representative genome".equals(parts[4])) {
                            String localDir = parts[19].substring(FTP_PREFIX.length());
                            String gbff = LOCAL_DIR + localDir + File.separatorChar + localDir + "_genomic.gbff.gz";
                            System.out.println(gbff);
                            File f = new File(gbff);
                            if (f.exists()) {
                                files.add(f);
                                readReference(globalRefs, gbff, ds);
                            }
                        }
                    }
                }
            }
        }
        System.out.println(files.size() + " files");
    }

    private static void readReference(List<String> globalRefs, String fname, DataSource ds) {
        String seqname = "";

        BufferedReader br = null;
        try {
            InputStream gzipStream = new GZIPInputStream(new FileInputStream(fname));
            br = new BufferedReader(new InputStreamReader(gzipStream));
        } catch (IOException ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }

        try {
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

                seqname = cleanupSeqName(rs.getDescription());

                if (seqname.contains("PROGRESS") || seqname.contains("draft") || seqname.contains("fragment") || seqname.contains("incision element")) {  //avoid incomplete sequences
                    continue;
                }
                if (seqname.contains("whole genome shotgun") || seqname.contains("plasmid")) {  //avoid incomplete sequences/plasmids
                    continue;
                }
                seqname = seqname.trim();

                if (globalRefs.contains(seqname)) {
                    System.out.println(seqname + " already present, skipping..");
                    return;
                }

                Reference ref = new Reference();
                ref.setName(seqname);
                List<Region> regions = new ArrayList<>();

                Iterator<Feature> iter = rs.features();
                while (iter.hasNext()) {
                    RichFeature elem = (RichFeature) iter.next();

                    if (elem.getType().equals("CDS") || elem.getType().equals("rRNA") || elem.getType().equals("tRNA")) {
                        if (genomeSeq == null) {
                            genomeSeq = elem.getSequence().seqString().toUpperCase();
                        }

                        Region region = new Region();
                        region.setType(elem.getType());
                        Annotation annot = elem.getAnnotation();
                        if (annot.containsProperty("locus_tag")) {
                            region.setName((String) annot.getProperty("locus_tag"));
                        } else {
                            System.out.println("ERROR no locus tag");
                            System.out.println(elem);
                            continue;
                        }
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
                    System.out.println("No DNA sequence for " + seqname + " found in " + fname);
                    return;
                } else {
                    ref.setLength(genomeSeq.length());
                    saveReference(rs.getName(), ref, regions, genomeSeq, ds);
                    System.out.println("OK: " + seqname + "      (" + fname + ")");
                    globalRefs.add(ref.getName());
                }
            }

        } catch (Exception ex) {
            System.out.println(fname + ": " + ex.getMessage() + "   (" + seqname + ")");
        } finally {
            try {
                br.close();
            } catch (IOException ex) {
            }
        }

        //return ret;
    }

    private static void saveReference(String accession, Reference ref, List<Region> regions, String dnaSeq, DataSource ds) throws Exception {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(GLOBAL_DIR + accession + ".fas"))) {
            bw.append(">");
            bw.append(accession);
            bw.newLine();
            bw.append(dnaSeq.toUpperCase());
            bw.newLine();
        }
        ref.setFile(GLOBAL_DIR + accession + ".fas");
        try (Connection conn = ds.getConnection()) {
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
        }
        try (Connection conn = ds.getConnection()) {
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
    }

    private static List<String> listReferences(DataSource ds) {
        List<String> ret = new ArrayList<>();
        try (Connection conn = ds.getConnection()) {
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

    private static String cleanupSeqName(String seqname) {
        seqname = seqname.replaceAll("\n", " ").trim();
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
        if (seqname.startsWith("[") && seqname.contains("]")) {
            seqname = seqname.substring(1); // [
            seqname = seqname.replaceFirst("]", "");
        }
        seqname = seqname.replaceAll(" complete sequence", "");
        seqname = seqname.replaceAll(", complete genome", "");
        seqname = seqname.replaceAll(" complete genome", "");
        seqname = seqname.replaceAll(" genome assembly", "");
        seqname = seqname.replaceAll(" :", ":");
        while (seqname.contains("  ")) {
            seqname = seqname.replaceAll("  ", " ");
        }

        return seqname;
    }
}
