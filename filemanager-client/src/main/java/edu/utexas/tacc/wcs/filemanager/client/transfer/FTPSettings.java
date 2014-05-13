/*
 * Portions of this file Copyright 2004-2007 Shanghai Jiaotong University
 * 
 * This file or a portion of this file is licensed under the
 * terms of the Globus Toolkit Public License, found at
 * http://www.globus.org/toolkit/legal/4.0/
 * If you redistribute this file, with or without
 * modifications, you must include this notice in the file.
 */

package edu.utexas.tacc.wcs.filemanager.client.transfer;

import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.util.ListIterator;
import java.util.Vector;

import org.globus.ftp.exception.ServerException;
import org.ietf.jgss.GSSCredential;

import edu.utexas.tacc.wcs.filemanager.client.ConfigSettings;
import edu.utexas.tacc.wcs.filemanager.common.model.enumeration.FileProtocolType;
import edu.utexas.tacc.wcs.filemanager.common.model.enumeration.SystemType;


public class FTPSettings implements Comparable<FTPSettings>/* implements Cloneable*/
{
    /*Used for global settings*/
    public static String DefaultLogfile="";
    public static boolean DefaultStripeTransfers = true;
    public static boolean DefaultShowHidden = false;
    public static int DefaultBufferSize=32;
    public static int DefaultConnMaxNum=10;
    public static int DefaultConnRetry=1;
    public static int DefaultConnDelay=5;
    public static int DefaultConnKeep=30;
    public static int DefaultConnPara=1;
    public static int DefaultSearchDepth=5;
    public static int DefaultMaxSearchResults=200;
    public static boolean DefaultPassiveMode=false;
    public static String DefaultCertificate="";
    
    public static FTPSettings Local = new FTPSettings(ConfigSettings.RESOURCE_NAME_LOCAL, FileProtocolType.FILE.getDefaultPort(), FileProtocolType.FILE);

    /*Server's ID, Name, Hostname/IP, Port, Type, Position, inPassiveMode, AdjecentTable*/

    public String name;//Server's ID
    public String host;//Server's host
    public String sshHost; //Server's ftp server
    public Integer sshPort;//Server's port
    public Integer filePort;//Server's port
    public FileProtocolType protocol;//protocol type(FILE, FTP, GridFTP, BBFTP, SFTP)
    public String zone; // irods specific
    public String resource; // irods specific
    public boolean passiveMode;//passive mode
    public int connRetry; //The number of opportunity of connection retry
    public int connDelay; //The time delay between 2 connection attempt
    public int connParallel; //Parallel number
    public int loginMode=FTPLogin.LOGIN_USEPROXYINIT;//Login mode
    public String userName = "";//user name
    public String password = "";//password
    public int connMaxNum;//The maximum size of the connection pool
    public int connKeepAlive;//The time delay between 2 keep-alive action
    public int bufferSize;
    public boolean showHidden;
    public boolean stripeTransfers = true;
    public int maxSearchDepth;
    public int maxSearchResults;
    public boolean userDefined = false;//is this a tg resource or user defined
    public boolean available = true; // is this resource available or in a maintenance period
    private GSSCredential globusCred = null;//globus credit

    private Vector<SHGridFTP> connPool = new Vector<SHGridFTP>(connMaxNum);//connection pool
    private Vector<GsiSSH> tunnelPool = new Vector<GsiSSH>(connMaxNum);//gsissh tunnel pool
    public SystemType hostType = SystemType.HPC;
    public boolean listed = true;
    public Component parent = null;
	public String resourceId;
    
    public FTPSettings(edu.utexas.tacc.wcs.filemanager.common.model.System system) 
    {
    	this(system.getFtpHostname(), system.getFtpPort(), system.getProtocol());
    	
    	this.name = system.getResourceName();
    	this.hostType = system.getSystemType();
    	this.resourceId = system.getResourceId();
    	
    	this.sshHost = system.getSshHostname();
    	this.sshPort = system.getSshPort() == null ? 22 : system.getSshPort();
    	
    	this.userName = system.getUserName();
    	
    	this.protocol = system.getProtocol();
    	this.host = system.getFtpHostname();
    	this.filePort = system.getFtpPort();
    	if (this.protocol.equals(FileProtocolType.GRIDFTP)) {
    		this.passiveMode = true;
    	}
    }
    
    public FTPSettings(String sHost){
        this(sHost, FileProtocolType.GRIDFTP.getDefaultPort(), FileProtocolType.GRIDFTP);
    }

    public FTPSettings(String sHost, FileProtocolType nType){
        this(sHost, FileProtocolType.FILE.getDefaultPort(), nType);
    }

    public FTPSettings(String sHost, Integer nPort, FileProtocolType nType){
        this.name=sHost;
        this.sshHost=sHost;
        this.sshPort = 22;
        this.host=sHost;
        this.filePort= nPort == null ? nType.getDefaultPort() : nPort;
        this.protocol=nType;
        this.loginMode = FTPLogin.LOGIN_USEPROXYINIT;
        
        this.passiveMode = FTPSettings.DefaultPassiveMode;
        this.connRetry = FTPSettings.DefaultConnRetry;
        this.connDelay = FTPSettings.DefaultConnDelay;
        this.connParallel = nType == FileProtocolType.FILE ? 1 : FTPSettings.DefaultConnPara;
        this.connMaxNum = FTPSettings.DefaultConnMaxNum;
        this.connKeepAlive = FTPSettings.DefaultConnKeep;
        this.showHidden = FTPSettings.DefaultShowHidden;
        this.stripeTransfers = FTPSettings.DefaultStripeTransfers;
        this.bufferSize = nType == FileProtocolType.FILE ? FTPSettings.DefaultBufferSize : 33000;
        this.maxSearchDepth = FTPSettings.DefaultSearchDepth;
        this.maxSearchResults = FTPSettings.DefaultMaxSearchResults;
    }

    public FTPSettings(String sHost, int nPort, FileProtocolType nType, Component parent){
    	this(sHost, nPort, nType);
    	this.parent = parent;
    }
    
    public String getPrefix()
    {
        return protocol.getSchema();
    }

    public SHGridFTP getConnection() throws IOException, ServerException{
        SHGridFTP conn = null;
        conn = getFreeConnection();
        if(conn == null){
            conn = getNewConnection();
        }
        return conn;
    }
    
    public GsiSSH getTunnel() {
        GsiSSH conn = null;
        conn = getFreeTunnel();
        if(conn == null){
            conn = getNewTunnel();
        }
        return conn;
    }

    /**
     * Get free connections from the connection pool
     * @return SHGridFTP
     */
    public SHGridFTP getFreeConnection(){
    	
    	if (this.protocol.equals(FileProtocolType.FILE)) {
    		SHGridFTP conn;
    		try {
    			conn = new SHGridFTP(this);
            } catch (Exception e) {
                return null;
            }
            return conn;
    	}
    	
        for (ListIterator<SHGridFTP> i = connPool.listIterator(); i.hasNext(); ) 
        {
            SHGridFTP conn = i.next();
            if(conn.isIdle()) {
            	return conn;
            }
        }
        return null;
    }
    
    /**
     * Get free connections from the connection pool
     * @return GsiSSH
     */
    public GsiSSH getFreeTunnel(){
        
        if (this.protocol.equals(FileProtocolType.FILE)) {
            GsiSSH tunnel;
            try {
                tunnel = new GsiSSH(this);
            } catch (Exception e) {
                return null;
            }
            return tunnel;
        }
        
        for (ListIterator<GsiSSH> i = tunnelPool.listIterator(); i.hasNext(); ) {
            GsiSSH tunnel = i.next();
            if(tunnel.isIdle()) {
                return tunnel;
            }
        }
        return null;
    }

    /**
     * Acquire a new connection and add it to the connection pool
     * @return SHGridFTP
     */
    public SHGridFTP getNewConnection() throws IOException, ServerException{
        SHGridFTP conn = null;
        
        if (this.protocol.equals(FileProtocolType.FILE)) {
    		try {
    			conn = new SHGridFTP(this);
            } catch (Exception e) {
                return null;
            }
            if (conn != null) {
                connPool.add(conn);
            }
            return conn;
    	}
        
        if(connPool.size() < connMaxNum){
            try {
                conn = new SHGridFTP(this);
            } catch (IOException e) {
//                e.printStackTrace();
                throw e;
            } catch (ServerException e) {
                throw e;
            }
            if (conn != null) {
                connPool.add(conn);
            }
        }
        return conn;
    }
    
    /**
     * Acquire a new connection and add it to the connection pool
     * @return SHGridFTP
     */
    public GsiSSH getNewTunnel(){
        GsiSSH tunnel = null;
        
        if (this.protocol.equals(FileProtocolType.FILE)) {
            try {
                tunnel = new GsiSSH(this);
            } catch (Exception e) {
                return null;
            }
            return tunnel;
        }
        
        if(tunnelPool.size() < connMaxNum){
            try {
                tunnel = new GsiSSH(this);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            if (tunnel != null) {
                tunnelPool.add(tunnel);
            }
        }
        return tunnel;
    }

    /**
     * test no connection
     * @return boolean
     */
    public boolean noConnection(){
        return connPool.size()<=0;
    }

    /**
     * test the size of the connection pool is smaller than the maximum connection number.
     * @return boolean
     */
    public boolean existNotGot(){
        return connPool.size() < connMaxNum;
    }

    public boolean hasFreeConnection(){
    	
    	if (this.protocol.equals(FileProtocolType.FILE)) {
    		return true;
    	}
    	
    	if (existNotGot()) {
    		return true;
    	}
    	
        for (ListIterator<SHGridFTP> i = connPool.listIterator(); i.hasNext(); ) {
            SHGridFTP conn = i.next();
            if(conn.isIdle()) {
            	return true;
            }
        }
        return false;
    }


    public void removeConn(SHGridFTP ftpConn){
        if(connPool.contains(ftpConn)) {
        	connPool.remove(ftpConn);
        }
    }
    
    public void removeTunnel(GsiSSH sshTunnel){
        if(connPool.contains(sshTunnel)) {
            connPool.remove(sshTunnel);
        }
    }

    public boolean equals(FTPSettings ftpServer) {
        return (ftpServer.name.equals(name) && ftpServer.host.equals(host));
        //return ftpServer.getIP().equals(getIP()) && ftpServer.port==port && ftpServer.type==type && ftpServer.userName==userName;
    }

    public String toString(){
//        return "name: " + this.name + "\n" + 
        return this.name + " (" + this.host + ")";
//        "port: " + this.port + "\n" +
//        "type: " + this.type+ "\n" +
//        "username: " + this.userName + "\n" +
//        "password: " + this.password + "\n" +
//        "passive mode: " + this.passiveMode + "\n" +
//        "login mode: " + this.loginMode;
//    	return this.name;
    }
    
    public String getURLString() {
    	return String.format("%s%s:%d", protocol.getSchema(), this.host, this.filePort);
    }

	public GSSCredential getGSSCred() {
		return this.globusCred;
	}

	public void setGSSCred(GSSCredential globusCred) {
		this.globusCred = globusCred;
	}
	
	public FTPSettings clone(Component parent) {
	    FTPSettings clone = new FTPSettings(this.name,this.filePort,this.protocol);
	    clone.host = this.host;
	    clone.filePort = this.filePort;
	    clone.sshHost = this.sshHost;
	    clone.sshPort = this.sshPort;
        clone.loginMode = this.loginMode;
        clone.showHidden = this.showHidden;
        clone.passiveMode = this.passiveMode;
        clone.available = this.available;
        clone.connRetry = this.connRetry;
        clone.connDelay = this.connDelay;
        clone.connParallel = this.connParallel;
        clone.connMaxNum = this.connMaxNum;
        clone.connKeepAlive = this.connKeepAlive;
        clone.parent = this.parent;
        clone.bufferSize = this.bufferSize;
        clone.stripeTransfers = this.stripeTransfers;
        clone.userName = this.userName;
        clone.password = this.password;
        clone.protocol = this.protocol;
        clone.hostType = this.hostType;
        clone.maxSearchDepth = this.maxSearchDepth;
        clone.maxSearchResults = this.maxSearchResults;
        
        return clone;
	}
	
	public int compareTo(FTPSettings o) {
		if (this.name.compareTo(o.name) == 0) {
        	if (this.host.compareTo(o.host) == 0) {
        		return this.filePort.compareTo(o.filePort);
        	} else {
        		return this.host.compareTo(o.host); 
        	}
        } else {
        	return this.name.compareTo(o.name);
        }
	}
	
	public String getSeparator() {
	    if (this.protocol.equals(FileProtocolType.FILE)) {
	        return File.separator;
	    } else {
	        return "/";
	    }
	}
	
	public boolean isAvailable() {
		return available;
	}
	
	public boolean isLocal() {
	    return (this.protocol.equals(FileProtocolType.FILE));
	}
	
	public boolean equals(Object o) {
		// this equivalence operation is failing to match resources
		// this is due to a naming mismatch between iis and gpir.
		// ex. GPIR: Ranger || IIS: TACC Ranger
		// ex. GPIR: NSTG	|| IIS: ORNL Neutron Science TeraGrid Gateway
		if (o instanceof FTPSettings) {
			if (((FTPSettings)o).name.contains(name) || 
					name.contains(((FTPSettings)o).name)) {
				if (((FTPSettings)o).host == null && host == null) {
					return true;
				} else if (((FTPSettings)o).host == null || host == null) {
					return false;
				} else if (((FTPSettings)o).host.equals(host)) {
					return true;
				} else {
					return false;
				}
			} else {
				return false;
			}
		} else { 
			return false;
		}
	}

	public boolean isTeraGridResource() {
		return (this.protocol.equals(FileProtocolType.GRIDFTP)) && !this.userDefined;
	}
}
