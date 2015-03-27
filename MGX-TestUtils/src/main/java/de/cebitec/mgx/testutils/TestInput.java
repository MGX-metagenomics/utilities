/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.testutils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;

/**
 *
 * @author sjaenick
 */
public class TestInput {

    public static File copyTestData(Class clazz, String uri) throws Exception {
        File f = null;
        try (BufferedInputStream is = new BufferedInputStream(clazz.getClassLoader().getResourceAsStream(uri))) {
            f = File.createTempFile("seq", ".fq");
            f.deleteOnExit();
            try (FileOutputStream fos = new FileOutputStream(f)) {
                byte[] buffer = new byte[1024];
                int bytesRead = is.read(buffer);
                while (bytesRead >= 0) {
                    fos.write(buffer, 0, bytesRead);
                    bytesRead = is.read(buffer);
                }
            }
        }
        return f;
    }
}
