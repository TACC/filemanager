/*
 * Portions of this file Copyright 2004-2007 Shanghai Jiaotong University
 * 
 * This file or a portion of this file is licensed under the
 * terms of the Globus Toolkit Public License, found at
 * http://www.globus.org/toolkit/legal/4.0/
 * If you redistribute this file, with or without
 * modifications, you must include this notice in the file.
 */

package org.teragrid.portal.filebrowser.applet.transfer;

import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Vector;

import org.globus.ftp.exception.ServerException;
import org.globus.gsi.GlobusCredential;
import org.teragrid.portal.filebrowser.applet.ConfigSettings;


@SuppressWarnings("unchecked")
public class FTPSettings implements Comparable/* implements Cloneable*/{
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
    
    public static FTPSettings Local = new FTPSettings(ConfigSettings.RESOURCE_NAME_LOCAL,FTPPort.PORT_NONE,FTPType.FILE);

    /*Server's ID, Name, Hostname/IP, Port, Type, Position, inPassiveMode, AdjecentTable*/

    public String name;//Server's ID
    public String host;//Server's host
    public String sshHost; //Server's ftp server
    public int port;//Server's port
    public int type;//protocol type(FILE, FTP, GridFTP, BBFTP, SFTP)
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
    private GlobusCredential globusCred = null;//globus credit

    private Vector connPool = new Vector(connMaxNum);//connection pool
    private Vector tunnelPool = new Vector(connMaxNum);//gsissh tunnel pool
    @SuppressWarnings("unused")
	private ArrayList trans=new ArrayList();
    public String hostType = "hpc";
    public boolean listed = true;
    public Component parent = null;
	public String resourceId;
    
    
    public FTPSettings(String sHost){
        this(sHost, FTPPort.PORT_NONE, FTPType.GRIDFTP);
    }

    public FTPSettings(String sHost, int nType){
        this(sHost, FTPPort.PORT_NONE, nType);
    }

    public FTPSettings(String sHost, int nPort, int nType){
        this.name=sHost;
        this.sshHost=sHost;
        this.host=sHost;
        this.port=nPort;
        this.type=nType;
        this.loginMode = FTPLogin.LOGIN_USEPROXYINIT;
        
        if(FTPPort.PORT_NONE == this.port){
            switch(this.type){
            case FTPType.GRIDFTP:
                this.port=FTPPort.PORT_GRIDFTP;
                break;
            case FTPType.BBFTP:
                this.port=FTPPort.PORT_BBFTP;
                break;
            case FTPType.SFTP:
                this.port=FTPPort.PORT_SFTP;
                break;
            case FTPType.FTP:
                this.port=FTPPort.PORT_FTP;
                break;
            case FTPType.HTTP:
                this.port=FTPPort.PORT_HTTP;
            case FTPType.S3:
                this.port=FTPPort.PORT_S3;
            default:
                break;
            }
        }
        
        this.passiveMode = FTPSettings.DefaultPassiveMode;
        this.connRetry = FTPSettings.DefaultConnRetry;
        this.connDelay = FTPSettings.DefaultConnDelay;
        this.connParallel = nType == FTPType.FILE ? 1 : FTPSettings.DefaultConnPara;
        this.connMaxNum = FTPSettings.DefaultConnMaxNum;
        this.connKeepAlive = FTPSettings.DefaultConnKeep;
        this.showHidden = FTPSettings.DefaultShowHidden;
        this.stripeTransfers = FTPSettings.DefaultStripeTransfers;
        this.bufferSize = nType == FTPType.FILE ? FTPSettings.DefaultBufferSize : 33000;
        this.maxSearchDepth = FTPSettings.DefaultSearchDepth;
        this.maxSearchResults = FTPSettings.DefaultMaxSearchResults;
    }

    public FTPSettings(String sHost, int nPort, int nType, Component parent){
    	this(sHost, nPort, nType);
    	this.parent = parent;
    }
    
    public String getPrefix(){
        String sPrefix = "";
        switch(type){
        case FTPType.GRIDFTP:
            sPrefix = GridFTP.Prefix;
            break;
        case FTPType.BBFTP:
            sPrefix = BBFTP.Prefix;
            break;
        case FTPType.SFTP:
            sPrefix = SFTP.Prefix;
            break;
        case FTPType.FTP:
            sPrefix = FTP.Prefix;
            break;
        case FTPType.HTTP:
            sPrefix = HTTP.Prefix;
        case FTPType.S3:
            sPrefix = S3.Prefix;
        case FTPType.IRODS:
            sPrefix = Irods.Prefix;
        default:
            break;
        }
        return sPrefix;
    }

//    @SuppressWarnings("unused")
//	private InetAddress getIP(){
//        try {
//            return InetAddress.getByName(host);
//        } catch (Exception ex) {
//            AppMain.Error(null,SGGCResourceBundle.getResourceString(ResourceName.KEY_ERROR_FTPSETTINGS_INVALIDIP));
//            return null;
//        }
//    }

    
//    public SHGridFTP getConnection() throws IOException,ServerException{
//        SHGridFTP ftpConn=null;
//        if(connPool.size()<=connMaxNum){
//            ftpConn=new SHGridFTP(this);
//            connPool.add(ftpConn);
//        }
//        else{
//            int j=0;
//            for (ListIterator i = connPool.listIterator(); i.hasNext(); ) {
//                SHGridFTP conn = (SHGridFTP)i.next();
//                if(!(0==j && this==Local) && conn.isIdle()) {ftpConn=conn;break;}
//                j++;
//            }
//        }
//        return ftpConn;
//    }


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
    	
    	if (this.type == FTPType.FILE) {
    		SHGridFTP conn;
    		try {
    			conn = new SHGridFTP(this);
            } catch (Exception e) {
                return null;
            }
            return conn;
    	}
    	
        for (ListIterator i = connPool.listIterator(); i.hasNext(); ) {
            SHGridFTP conn = (SHGridFTP)i.next();
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
        
        if (this.type == FTPType.FILE) {
            GsiSSH tunnel;
            try {
                tunnel = new GsiSSH(this);
            } catch (Exception e) {
                return null;
            }
            return tunnel;
        }
        
        for (ListIterator i = tunnelPool.listIterator(); i.hasNext(); ) {
            GsiSSH tunnel = (GsiSSH)i.next();
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
        
        if (this.type == FTPType.FILE) {
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
        
        if (this.type == FTPType.FILE) {
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
    	
    	if (this.type == FTPType.FILE) {
    		return true;
    	}
    	
    	if (existNotGot()) {
    		return true;
    	}
    	
        for (ListIterator i = connPool.listIterator(); i.hasNext(); ) {
            SHGridFTP conn = (SHGridFTP)i.next();
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
        if (this.name.toLowerCase().indexOf("local") > -1) {
            return "file://localhost";
        } else {
            return FTPType.FTP_PROTOCOL[this.type].toLowerCase() + "://" + this.name + ":" + this.port;
        }
    }

	public GlobusCredential getGlobusCred() {
		return this.globusCred;
	}

	public void setGlobusCred(GlobusCredential globusCred) {
		this.globusCred = globusCred;
	}
	
	public FTPSettings clone(Component parent) {
	    FTPSettings clone = new FTPSettings(this.name,this.port,this.type);
	    clone.host=this.host;
	    clone.sshHost = this.sshHost;
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
        clone.type = this.type;
        clone.hostType = this.hostType;
        clone.maxSearchDepth = this.maxSearchDepth;
        clone.maxSearchResults = this.maxSearchResults;
        
        return clone;
	}
	
	public int compareTo(Object o) {
	    if (o instanceof FTPSettings) {
	        if (this.name.compareTo(((FTPSettings)o).name) == 0) {
	        	return this.host.compareTo(((FTPSettings)o).host);
	        } else {
	        	return this.name.compareTo(((FTPSettings)o).name);
	        }
	    } else {
	        return 1;
	    }
	}
	
	public String getSeparator() {
	    if (type == FTPType.FILE) {
	        return File.separator;
	    } else {
	        return "/";
	    }
	}
	
	public boolean isAvailable() {
		return available;
	}
	
	public boolean isLocal() {
	    return (this.type == FTPType.FILE);
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
		return (FTPType.GRIDFTP == this.type) && !this.userDefined;
	}
}
