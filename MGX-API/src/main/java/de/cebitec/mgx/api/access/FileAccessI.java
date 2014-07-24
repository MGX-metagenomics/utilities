/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.api.access;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.access.datatransfer.DownloadBaseI;
import de.cebitec.mgx.api.access.datatransfer.UploadBaseI;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.misc.TaskI;
import de.cebitec.mgx.api.model.MGXFileI;
import java.io.File;
import java.io.OutputStream;
import java.util.Iterator;

/**
 *
 * @author sj
 */
public interface FileAccessI {

    public MGXMasterI getMaster();

    public Iterator<MGXFileI> fetchall();

    public Iterator<MGXFileI> fetchall(MGXFileI parent);

    public TaskI delete(MGXFileI obj);

    public DownloadBaseI createPluginDumpDownloader(OutputStream writer);

    public DownloadBaseI createDownloader(String fullPath, OutputStream writer);

    public UploadBaseI createUploader(File localFile, MGXFileI targetDir, String name) throws MGXException;

    public boolean createDirectory(MGXFileI parentDir, String name) throws MGXException;

}
