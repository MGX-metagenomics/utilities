/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.qc.io;

import de.cebitec.mgx.qc.Analyzer;
import de.cebitec.mgx.qc.DataRowI;
import de.cebitec.mgx.qc.QCResult;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sj
 */
public class Persister {

    public static boolean persist(String prefix, Analyzer<?> a) {
        File f = null;
        try {
            f = new File(prefix + a.getName() + ".tmp");
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(f))) {
                QCResult qc = a.get();
                bw.write(qc.getName());
                bw.write("\t");
                bw.write(qc.getDescription());
                bw.newLine();
                for (DataRowI dr : qc.getData()) {
                    bw.write(dr.getName());
                    bw.write("\t");
                    bw.write(join(dr.getData(), ","));
                    bw.newLine();
                }
            }

        } catch (IOException ex) {
            Logger.getLogger(Persister.class.getName()).log(Level.SEVERE, null, ex);
            if (f != null && f.exists()) {
                f.delete();
                return false;
            }
        }
        return f != null && f.renameTo(new File(prefix + a.getName()));
    }

    private static String join(float[] d, String separator) {
        if (d == null || d.length == 0) {
            return "";
        }
        int i = 0;
        StringBuilder sb = new StringBuilder(String.valueOf(d[i++]));
        while (i < d.length) {
            sb.append(separator).append(String.valueOf(d[i++]));
        }
        return sb.toString();
    }

}
