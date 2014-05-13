/* 
 * Created on Aug 20, 2008
 * 
 * Developed by: Rion Dooley - dooley [at] tacc [dot] utexas [dot] edu
 * 				 TACC, Texas Advanced Computing Center
 * 
 * https://www.tacc.utexas.edu/
 */

package edu.utexas.tacc.wcs.filemanager.client.transfer;

import java.awt.Component;
import java.io.IOException;
import java.util.Vector;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOCase;
import org.globus.ftp.FileInfo;
import org.globus.ftp.exception.ClientException;
import org.globus.ftp.exception.ServerException;

import edu.utexas.tacc.wcs.filemanager.client.exception.SearchException;
import edu.utexas.tacc.wcs.filemanager.client.util.LogManager;

/**
 * Utility Thread to perform recursive directory searching. It matches file names
 * using wildcard matching via apache.io.FilenameUtils.
 * 
 * @author Rion Dooley < dooley [at] tacc [dot] utexas [dot] edu >
 *
 */
public class UrlSearch implements Runnable {;

    protected boolean canceled       = false;
    protected boolean thirdParty     = true;
    
    protected SearchListener listener  = null;
    protected Component parent       = null;
    protected FTPSettings ftpServer  = null;
    protected String regex            = null;
    
    private SHGridFTP ftpServerConn = null;
    //private GlobFilenameFilter fileNameFilter = null;
    
    private String searchRoot = null;
    
    private int searchResultCount;
    
    public UrlSearch(SHGridFTP svrConn, String regex) {
        this.ftpServerConn = svrConn;
        this.regex = regex;
        this.searchResultCount = 0;
        this.ftpServer = svrConn.getFtpServer();
    }
    
    /* (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    public void run() {
        try {
        	searchRoot = ftpServerConn.getDir();
        	search(0);
        	ftpServerConn.setDir(searchRoot);
        } catch(Exception e) {
            if (ftpServerConn != null) {
                try {
                    ftpServerConn.close();
                } catch (Exception e1) {
                    LogManager.error("Failed to close the connection.",e1);
                }
            }
            this.fireSearchErrorEvent(e);
        } finally {
            this.fireSearchCompletedEvent();
            this.listener = null;
        }
    }
    
    public void addUrlSearchListener(SearchListener listener) {
        this.listener = listener;
    }
    
    /**
     * Checks if the search was canceled.
     *
     * @return true if search was canceled
     */
    public boolean isCanceled() {
    	return this.canceled;
    }
    
    /**
     * Sets the cancelled flag to true.
     */
    public void cancel() {
        this.canceled = true;
        try {
			this.ftpServerConn.setDir(searchRoot);
		} catch (Exception e) {
			LogManager.error("Failed to reset the connection to the search root.");
		}
    }

    public void search(int currentDepth) 
    throws SearchException, IOException, ServerException, ClientException {
        
        if (searchResultCount >= ftpServer.maxSearchResults || currentDepth >= ftpServer.maxSearchDepth) return;
        
        String searchDir = ftpServerConn.getDir();
        
        if (searchDir != null && !searchDir.equals("")) {
            searchDir += this.ftpServer.getSeparator();
        } else {
            searchDir = "";
        }
        
        fireSearchPathChangeEvent(searchDir);
        
        if (this.canceled) throw new SearchException("Search was cancelled by the user.");
        
        Vector<FileInfo> v = ftpServerConn.list("*",ftpServer.showHidden);
        
        for (FileInfo file: v) {
            
            if (file.isDirectory()) {
                
                if (this.canceled) throw new SearchException("Search was cancelled by the user.");
                
                if (searchResultCount < ftpServer.maxSearchResults || currentDepth < ftpServer.maxSearchDepth)  {
                    if (FilenameUtils.wildcardMatch(file.getName(), regex, IOCase.INSENSITIVE)) {
                        LogManager.debug(searchResultCount + ", " + currentDepth +  " Adding folder " + 
                                searchDir + ftpServer.getSeparator() + file.getName());
                        fireSearchProgressEvent(new SearchResult(file,searchDir,ftpServer.isLocal()));   
                        searchResultCount++;
                    }
                    
                    // recursively search this directory
                    try {
                        ftpServerConn.setDir(searchDir + file.getName());
                    } catch (IOException e) {
                        LogManager.error("Skipping directory " + ftpServerConn.getDir() + 
                                ftpServer.getSeparator() + file.getName(),e);
                        continue;
                    }
                    
                    search(currentDepth+1);
                }
                
            } else {

                if (searchResultCount >= 0 && FilenameUtils.wildcardMatch(file.getName(), regex, IOCase.INSENSITIVE)) {
                    LogManager.debug(searchResultCount + ", " + currentDepth +  " Adding file " + 
                            searchDir + ftpServer.getSeparator() + file.getName());
                    fireSearchProgressEvent(new SearchResult(file,searchDir,ftpServer.isLocal()));
                    searchResultCount++;
                } else {
                    continue;
                }
            }
        }
        
    }
    
    /**
     * Fire an event to show the progress of the transfer
     * @param transferedBytes long
     */
    private void fireSearchProgressEvent(SearchResult searchResult) {
        if(this.listener == null) {
            return;
        }

        listener.searchResult(searchResult);
        
    }
    
    /**
     * Fire an event to show the progress of the transfer
     * @param transferedBytes long
     */
    private void fireSearchPathChangeEvent(String path) {
        if(this.listener == null) {
            return;
        }

        listener.searchPathChanged(path);
        
    }

    /**
     * Fire an event when an error occurs durling transfer
     * @param e Exception
     */
    private void fireSearchErrorEvent(Exception e){
        if(this.listener == null) {
            return;
        }
        
        listener.searchError(e);
    }

    /**
     * Fire an event to be catch when the transfer completes
     */
    private void fireSearchCompletedEvent(){
        if(this.listener == null) {
            return;
        }

        listener.searchCompleted();
    }
}
