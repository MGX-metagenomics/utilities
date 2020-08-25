/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.seqstorage.internal;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author sj
 */
public final class FileUtil {

    private FileUtil() {
    }

    public static boolean isGzip(File file) throws IOException {
        byte[] buf = new byte[2];
        try ( InputStream in = new FileInputStream(file)) {
            if (in.read(buf) < 2) {
                return false;
            }
        }

        // check for gzip magic
        return ((buf[0] == 31) && (buf[1] == -117));
    }

    public static boolean deleteDirectory(File directoryToDelete) {
        File[] content = directoryToDelete.listFiles();
        if (content != null) {
            for (File f : content) {
                deleteDirectory(f);
            }
        }
        return directoryToDelete.delete();
    }
}
