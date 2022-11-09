
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import org.biojava.bio.Annotation;
import org.biojava.bio.BioException;
import org.biojava.bio.seq.Feature;
import org.biojava.bio.symbol.Location;
import org.biojavax.Namespace;
import org.biojavax.RichObjectFactory;
import org.biojavax.bio.seq.RichFeature;
import org.biojavax.bio.seq.RichSequence;
import org.biojavax.bio.seq.RichSequenceIterator;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import org.junit.Test;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author sj
 */
public class OSGITest {

//    @Test
//    public void testPackageExport() throws Exception {
//        System.out.println("testPackageExport");
//
//        //
//        // regression check; access classes to validate they are available within OSGi containers
//        //
//        Namespace ns = RichObjectFactory.getDefaultNamespace();
//        assertNotNull(ns);
//        Location loc = Location.empty;
//        assertNotNull(loc);
//    }

    @Test
    public void testParseGBK() {
        System.out.println("testParseGBK");
        File f = new File("src/test/resources/NC_017106.gbk");
        if (!f.exists()) {
            fail();
        }

        RichSequenceIterator seqs = null;
        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader(f));
            Namespace ns = RichObjectFactory.getDefaultNamespace();
            br.mark(10);
            Character first = (char) br.read();
            br.reset();
            if (first.toString().equals("L")) {
                seqs = RichSequence.IOTools.readGenbankDNA(br, ns);
            } else if (first.toString().equals(">")) {
                seqs = RichSequence.IOTools.readFastaDNA(br, ns);
            } else {
                seqs = RichSequence.IOTools.readEMBLDNA(br, ns);
            }
        } catch (IOException ex) {
            fail(ex.getMessage());
        }
        assertNotNull(seqs);
        while (seqs.hasNext()) {
            RichSequence rs = null;
            try {
                rs = seqs.nextRichSequence();
            } catch (NoSuchElementException | BioException ex) {
                fail(ex.getMessage());
            }
            assertNotNull(rs);
            String seqname = rs.getDescription().replaceAll("\n", " ");
            assertNotNull(seqname);

            Iterator<Feature> iter = rs.features();
            while (iter.hasNext()) {
                RichFeature elem = (RichFeature) iter.next();
                if (elem.getType().equals("CDS") || elem.getType().equals("rRNA") || elem.getType().equals("tRNA")) {
                    Annotation annot = elem.getAnnotation();
                }
            }
        }
    }
}
