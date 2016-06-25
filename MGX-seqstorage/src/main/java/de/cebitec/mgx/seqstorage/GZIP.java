/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.seqstorage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author sj
 */
public class GZIP {

//    private final static byte[] GZIP_MAGIC = new byte[]{31, -117}; // 0x8b 0x1f

    private GZIP() {
    }

    public static boolean isGzip(File file) throws IOException {
        byte[] buf = new byte[2];
        try (InputStream in = new FileInputStream(file)) {
            if (in.read(buf) < 2) {
                return false;
            }
        }

        // check for gzip magic
        return ((buf[0] == 31) && (buf[1] == -117));
    }
}
