/* 
 * Created on Apr 11, 2008
 * 
 * Developed by: Rion Dooley - dooley [at] tacc [dot] utexas [dot] edu
 * 				 TACC, Texas Advanced Computing Center
 * 
 * https://www.tacc.utexas.edu/
 */

package org.teragrid.portal.filebrowser.applet.transfer;

import java.awt.Component;
import java.io.IOException;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;

import org.globus.ftp.ByteRangeList;
import org.globus.ftp.FileInfo;
import org.globus.ftp.Session;
import org.globus.ftp.exception.ClientException;
import org.globus.ftp.exception.ServerException;
import org.teragrid.portal.filebrowser.applet.AppMain;
import org.teragrid.portal.filebrowser.applet.exception.RemoteExecutionException;

import edu.utexas.tacc.wcs.filemanager.common.model.enumeration.FileProtocolType;

class SSHCommand {
    public static final int NONE = 0;

    public static final int ENV = 1;

    public static final int FIND = 2;
    
}

@SuppressWarnings("unused")
public class GsiSSH 
{   
    private boolean used = false;

    private boolean bIdle = true;

    private boolean bConnected = false;
    
    private GsiSshClient gsisshClient = null;

    private FTPSettings ftpServer = null;

    private int nType = Session.TYPE_ASCII;

    private String sRemote = null, sRemoteHome = null;
    
    private boolean bShowHidden = false;

    private ByteRangeList rLocalRange = new ByteRangeList(),
            rRemoteRange = new ByteRangeList();
    
    /**
     * * Initiate GRIDFTP session
     * 
     * @param host
     *            remote GRIDFTP host
     * @param port
     *            remote GridFTP port (default is 2811)
     */
    public GsiSSH(Component parent,String sHost) throws IOException, ServerException {
        this(new FTPSettings(sHost));
        ftpServer.parent = parent;
    }

    public GsiSSH(Component parent, String sHost, FileProtocolType nType) throws IOException,
            ServerException {
        this(new FTPSettings(sHost, nType));
        ftpServer.parent = parent;
    }

    public GsiSSH(Component parent,String sHost, int nPort, FileProtocolType nType) throws IOException,
            ServerException {
        this(new FTPSettings(sHost, nPort, nType));
        ftpServer.parent = parent;
    }

    public GsiSSH(FTPSettings ftpServer) throws IOException, ServerException {
        this.ftpServer = ftpServer;
        connect();
    }

    public GsiSshClient getRawClient() {
        return this.gsisshClient;
    }
    
    public void connect() throws IOException, ServerException {
        
        this.gsisshClient = new GsiSshClient(this.ftpServer.sshHost, this.ftpServer.sshPort, AppMain.defaultCredential);
        
        this.bConnected = true;
    }

    public void close() throws IOException, ServerException {
        this.ftpServer.removeTunnel(this);
        this.bConnected = false;
        this.gsisshClient.close();
    }
    
    public boolean isConnected() {
        return this.bConnected;
    }
    
    public void abort() throws IOException, ServerException {
        this.gsisshClient.abort();
    }
    
    public boolean isIdle() {
        return this.bIdle;
    }

    public Vector<FileInfo> find(String regex) throws RemoteExecutionException {
        return find(".", regex, true);
    }
    
    public Vector<FileInfo> find(String rootDir, String regex) throws RemoteExecutionException {
        return find(rootDir, regex, true);
    }
    
    public Vector<FileInfo> find(String rootDir, String regex, boolean bShowHidden) throws RemoteExecutionException {
        
        Vector<FileInfo> v = new Vector<FileInfo>();
        
        String sResults = this.gsisshClient.find(rootDir, regex, this.ftpServer.maxSearchDepth);
        System.out.println(sResults);
        String[] sLines = sResults.split("\r\n");
        
        for (int i=0;i<sLines.length;i++) {
            StringTokenizer tokens = new StringTokenizer(sLines[i]);
            String sLineItem = "";
            
            tokens.nextToken();
            tokens.nextToken();
            
            while (tokens.hasMoreTokens()) {
                sLineItem += tokens.nextToken() + " ";
            }
            
            System.out.println("Parsing line: " + sLineItem);
            
            try {
//                if (!parseLine(sLineItem)) {
//                    break;
//                }
                FileInfo f = new FileInfo(sLineItem);
                f.setName(f.getName());
                v.add(f);
                System.out.println(f.toString());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        
        return v;
    }
    
    public Properties getEnv() throws ClientException, ServerException, IOException {
        return this.gsisshClient.getProperties();
    }
    
    public void setIdle(boolean idle) {
        this.bIdle = idle;
    }
}
