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
public class StreamLogger extends Thread {

    private final InputStream is;
    private final Logger logger;

    public StreamLogger(String name, InputStream in) {
        this(name, in, Logger.getLogger(name));
    }

    public StreamLogger(String name, InputStream in, Logger logger) {
        super(name);
        this.is = in;
        this.logger = logger;
    }

    @Override
    public void run() {
        try {
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.isEmpty()) 
                    logger.log(Level.INFO, line);
            }
        } catch (IOException ioe) {
        }
    }
}
