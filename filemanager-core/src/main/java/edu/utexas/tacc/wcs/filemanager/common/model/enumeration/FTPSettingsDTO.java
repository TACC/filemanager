/*
 * Portions of this file Copyright 2004-2007 Shanghai Jiaotong University
 * 
 * This file or a portion of this file is licensed under the
 * terms of the Globus Toolkit Public License, found at
 * http://www.globus.org/toolkit/legal/4.0/
 * If you redistribute this file, with or without
 * modifications, you must include this notice in the file.
 */

package edu.utexas.tacc.wcs.filemanager.common.model.enumeration;

import java.io.File;

public class FTPSettingsDTO implements Comparable<FTPSettingsDTO> 
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
    public int loginMode = FTPLogin.LOGIN_USEPROXYINIT;//Login mode
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
    
    public String hostType = "hpc";
    public boolean listed = true;
	public String resourceId;
    
    
    public FTPSettingsDTO(String sHost){
        this(sHost, FTPPort.PORT_NONE, FTPType.GRIDFTP);
    }

    public FTPSettingsDTO(String sHost, int nType){
        this(sHost, FTPPort.PORT_NONE, nType);
    }

    public FTPSettingsDTO(String sHost, int nPort, int nType){
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
        
        this.passiveMode = FTPSettingsDTO.DefaultPassiveMode;
        this.connRetry = FTPSettingsDTO.DefaultConnRetry;
        this.connDelay = FTPSettingsDTO.DefaultConnDelay;
        this.connParallel = nType == FTPType.FILE ? 1 : FTPSettingsDTO.DefaultConnPara;
        this.connMaxNum = FTPSettingsDTO.DefaultConnMaxNum;
        this.connKeepAlive = FTPSettingsDTO.DefaultConnKeep;
        this.showHidden = FTPSettingsDTO.DefaultShowHidden;
        this.stripeTransfers = FTPSettingsDTO.DefaultStripeTransfers;
        this.bufferSize = nType == FTPType.FILE ? FTPSettingsDTO.DefaultBufferSize : 33000;
        this.maxSearchDepth = FTPSettingsDTO.DefaultSearchDepth;
        this.maxSearchResults = FTPSettingsDTO.DefaultMaxSearchResults;
    }

    
    public String toString(){
        return this.name + " (" + this.host + ")";
    }
    
    public String getURLString() {
        if (this.name.toLowerCase().indexOf("local") > -1) {
            return "file://localhost";
        } else {
            return FTPType.FTP_PROTOCOL[this.type].toLowerCase() + "://" + this.name + ":" + this.port;
        }
    }

	public FTPSettingsDTO clone() {
		FTPSettingsDTO clone = new FTPSettingsDTO(this.name,this.port,this.type);
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
	
	public int compareTo(FTPSettingsDTO o) {
		if (this.name.compareTo(o.name) == 0) {
        	return this.host.compareTo(o.host);
        } else {
        	return this.name.compareTo(o.name);
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
		if (o instanceof FTPSettingsDTO) {
			if (((FTPSettingsDTO)o).name.contains(name) || 
					name.contains(((FTPSettingsDTO)o).name)) {
				if (((FTPSettingsDTO)o).host == null && host == null) {
					return true;
				} else if (((FTPSettingsDTO)o).host == null || host == null) {
					return false;
				} else if (((FTPSettingsDTO)o).host.equals(host)) {
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
