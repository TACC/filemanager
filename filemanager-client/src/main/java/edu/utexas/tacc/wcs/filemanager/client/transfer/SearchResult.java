/* 
 * Created on Aug 19, 2008
 * 
 * Developed by: Rion Dooley - dooley [at] tacc [dot] utexas [dot] edu
 * 				 TACC, Texas Advanced Computing Center
 * 
 * https://www.tacc.utexas.edu/
 */

package edu.utexas.tacc.wcs.filemanager.client.transfer;

import java.io.File;

import org.globus.ftp.FileInfo;

/**
 * Insert Template description here.
 * 
 * @author Rion Dooley < dooley [at] tacc [dot] utexas [dot] edu >
 *
 */
public class SearchResult {

    private FileInfo file;
    private String path;
    private String host;
    private String separator;
    
    public SearchResult(boolean local) {
        this.separator = local?File.separator:"/";
    }
    
    public SearchResult(FileInfo fileInfo, String filePath, boolean local) {
        this(local);
        this.file = fileInfo;
        this.path = filePath;
    }

    /**
     * @return the file
     */
    public FileInfo getFile() {
        return file;
    }

    /**
     * @param file the file to set
     */
    public void setFile(FileInfo file) {
        this.file = file;
    }

    /**
     * @return the path
     */
    public String getPath() {
        return path;
    }

    /**
     * @param path the path to set
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * @return the host
     */
    public String getHost() {
        return host;
    }

    /**
     * @param host the host to set
     */
    public void setHost(String host) {
        this.host = host;
    }
    
    public String toString() {
        return path + separator + file.getName();
    }
}
