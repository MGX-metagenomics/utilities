/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.qc.io;

import de.cebitec.mgx.qc.DataRow;
import de.cebitec.mgx.qc.DataRowI;
import de.cebitec.mgx.qc.QCResult;
import de.cebitec.mgx.qc.QCResultI;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author sj
 */
public class Loader {

    public static QCResultI load(String fName) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(fName))) {
            String title = br.readLine();
            String line;
            List<DataRowI> payload = new ArrayList<>();
            while ((line = br.readLine()) != null) {
                String[] split = line.split("\t");
                String[] data = split[1].split(",");
                float[] d = new float[data.length];
                for (int i=0;i<d.length;i++) {
                    d[i] = Float.parseFloat(data[i]);
                }
                DataRowI dr = new DataRow(split[0], d);
                payload.add(dr);
            }
            return new QCResult(title, payload.toArray(new DataRow[]{}));
        }
    }

}