/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.streamlogger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sj
 */
public class StringLogger extends Thread {

    private final InputStream is;
    private StringBuilder output;

    public StringLogger(String name, InputStream in) {
        super(name);
        this.is = in;
    }
    
    public final String getOutput() {
        return output == null ? null : output.toString();
    }

    @Override
    public void run() {
        try {
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line;
            while ((line = br.readLine()) != null) {
                if (output == null) {
                    output = new StringBuilder();
                }
                output.append(line);
            }
        } catch (IOException ioe) {
        }
    }
}
