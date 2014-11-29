/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.qc.io;

import de.cebitec.mgx.qc.DataRow;
import de.cebitec.mgx.qc.QCResult;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 *
 * @author sj
 */
public class Persister {

    public static boolean persist(String prefix, QCResult qc) {
        File f = null;
        try {
            f = File.createTempFile(prefix, "tmp");
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(f))) {
                bw.write(qc.getName());
                bw.newLine();
                for (DataRow dr : qc.getData()) {
                    bw.write(dr.getName());
                    bw.write("\t");
                    bw.write(join(dr.getData(), ","));
                    bw.newLine();
                }
            }

        } catch (IOException ex) {
            if (f != null && f.exists()) {
                f.delete();
                return false;
            }
        }
        return f.renameTo(new File(prefix + qc.getName()));
    }

    private static String join(float[] d, String separator) {
        if (d == null || d.length == 0) {
            return "";
        }
        int i=0;
        StringBuilder sb = new StringBuilder(String.valueOf(d[i++]));
        while (i < d.length) {
            sb.append(separator).append(String.valueOf(d[i]));
        }
        return sb.toString();
    }

}
