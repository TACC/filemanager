/*
 * Portions of this file Copyright 2004-2007 Shanghai Jiaotong University
 * 
 * This file or a portion of this file is licensed under the
 * terms of the Globus Toolkit Public License, found at
 * http://www.globus.org/toolkit/legal/4.0/
 * If you redistribute this file, with or without
 * modifications, you must include this notice in the file.
 */

package org.teragrid.portal.filebrowser.applet.ui;

import java.io.EOFException;
import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Properties;
import java.util.Vector;

import org.globus.ftp.FileInfo;
import org.globus.ftp.Session;
import org.globus.ftp.exception.ClientException;
import org.globus.ftp.exception.ServerException;
import org.teragrid.portal.filebrowser.applet.AppMain;
import org.teragrid.portal.filebrowser.applet.ConfigOperation;
import org.teragrid.portal.filebrowser.applet.exception.ResourceException;
import org.teragrid.portal.filebrowser.applet.transfer.FTPSettings;
import org.teragrid.portal.filebrowser.applet.transfer.SHGridFTP;
import org.teragrid.portal.filebrowser.applet.transfer.SearchListener;
import org.teragrid.portal.filebrowser.applet.transfer.TextAppender;
import org.teragrid.portal.filebrowser.applet.util.LogManager;
import org.teragrid.portal.filebrowser.applet.util.ResourceName;
import org.teragrid.portal.filebrowser.applet.util.SGGCResourceBundle;
import org.teragrid.service.profile.wsclients.model.EnvironmentVariable;

import edu.utexas.tacc.wcs.filemanager.common.exception.PermissionException;
import edu.utexas.tacc.wcs.filemanager.common.model.User;
import edu.utexas.tacc.wcs.filemanager.common.model.enumeration.FileProtocolType;

@SuppressWarnings("unchecked")
public class FTPThread extends Thread{
    protected Properties envProps = new Properties();
    protected boolean bConnected = false;
    protected boolean bRemoteMode = true;
    protected boolean bPaused = false;
    protected FTPSettings ftpServer = null;
    protected SHGridFTP ftpSrvConn = null;
    protected Vector vParent = new Vector();
    protected PnlBrowse pnlBrowse = null;
    protected Vector vCmdQueue = new Vector();
    protected int nRetry = 0;
    private String threadName;
    private TextAppender app;
    private String sDir = null;
//    private RemoteEnvironmentReader envThread = null;
    
    public FTPThread(FTPSettings ftpServer,boolean bRemoteMode,PnlBrowse pnlBrowse){
        this.ftpServer=ftpServer;
        this.bRemoteMode=bRemoteMode;
        this.pnlBrowse=pnlBrowse;
        this.vParent.add(new IconData(AppMain.icoFolder,".."));
        this.vParent.add("0 KB");
        this.vParent.add("Folder");
        this.vParent.add("");
        this.vParent.add("drw-rw-rw-");
        this.vParent.add("");
        this.vParent.add("");
        this.nRetry = ftpServer.connRetry;
//        this.envThread = new RemoteEnvironmentReader(this);
    }

    public void run(){
    	this.app = TextAppender.getInstance();
        this.threadName = Thread.currentThread().getName();

        this.app.addTextBox(this.pnlBrowse.getTxtLog(), this.threadName);

        while(true){
            try{
	            if(!this.bConnected){
	                if(this.nRetry>0){
	                	this.nRetry--;
	                	String logConnect = MessageFormat.format(SGGCResourceBundle.getResourceString(ResourceName.KEY_MSG_FTPTHREAD_CONNECT), 
	                			new Object[] {new Integer(this.nRetry)});
	                	LogManager.debug(logConnect);
	                    connect();
	                }
	                else{
	                    LogManager.debug(SGGCResourceBundle.getResourceString(ResourceName.KEY_MSG_FTPTHREAD_CONNECTCLOSE));
	                    break;
	                }
	            }
	            if(this.bConnected){
	                if(cmdEmpty() && !this.bPaused) {
	                	keepAlive();
	                }
	                while(!cmdEmpty()){
	                    Vector vCmd=(Vector)this.vCmdQueue.get(0);
	                    if(cmdProcess(vCmd)) {
	                    	this.vCmdQueue.remove(vCmd);
	                    } else {
	                    	break;
	                    }
	                }
	            }
	            synchronized(this){
	            	if (this.bRemoteMode) {
	            		wait(this.bConnected ? this.ftpServer.connKeepAlive*1000 : this.ftpServer.connDelay*1000);
	            	} else {
	            		wait(10000);
	            	}
	            }
            }catch(InterruptedException ex){
            	ex.printStackTrace();
            	close();
            }
        }
        this.app.removeTextBox(this.threadName);
        close();
    }

    public boolean cmdAdd(String sCmd){
        return cmdAdd(sCmd,null,null,true);
    }
    public boolean cmdAdd(String sCmd,Object sPara1,Object sPara2){
        return cmdAdd(sCmd,sPara1,sPara2,true);
    }
    public boolean cmdAdd(String sCmd,Object sPara1,Object sPara2,boolean bImmediate){
		if (true/*this.ftpSrvConn.isIdle()*/) {
			Vector vCmd = new Vector();
			vCmd.add(sCmd);
			vCmd.add(sPara1);
			vCmd.add(sPara2);
			this.vCmdQueue.add(vCmd);
			synchronized (this) {
				if (bImmediate) {
					notify();
				}
			}
			return true;
		}
		AppMain.Message(this.pnlBrowse,SGGCResourceBundle.getResourceString(ResourceName.KEY_MSG_FTPTHREAD_FTPNOTIDLE));
		return false;
    }
    
    public boolean cmdAdd(String sCmd,Object sPara1,Object sPara2,Object sPara3,Object sPara4,boolean bImmediate){
    	if (true/*this.ftpSrvConn.isIdle()*/) {
			Vector vCmd = new Vector();
			vCmd.add(sCmd);
			vCmd.add(sPara1);
			vCmd.add(sPara2);
			vCmd.add(sPara3);
			vCmd.add(sPara4);
			this.vCmdQueue.add(vCmd);
			synchronized (this) {
				if (bImmediate) {
					notify();
				}
			}
			return true;
		}
		AppMain.Message(this.pnlBrowse,SGGCResourceBundle.getResourceString(ResourceName.KEY_MSG_FTPTHREAD_FTPNOTIDLE));
		return false;
    }

    public void cmdRemove(Vector vCmd){
    	this.vCmdQueue.remove(vCmd);
    }

    public boolean cmdEmpty(){
        return this.vCmdQueue.size()<=0;
    }

    protected boolean cmdProcess(Vector vCmd){
        boolean bRemove=true;
        if(null != this.pnlBrowse) {
        	this.pnlBrowse.suspendLayout();
        }
        try{
            String sCmd=(String)vCmd.get(0);
            if(sCmd.equals("List")){
                list();
            }
            else if(sCmd.equals("Type")){
            	this.ftpSrvConn.setType(((Integer)vCmd.get(1)).intValue());
            }
            else if(sCmd.equals("Dtp")){
            	this.ftpSrvConn.setDTP(((Boolean)vCmd.get(1)).booleanValue());
            }
            else if(sCmd.equals("Mode")){
            	this.ftpSrvConn.setMode(((Integer)vCmd.get(1)).intValue());
            }
//            else if(sCmd.equals("SetInherits")){
//            	this.ftpSrvConn.setInherits((String)vCmd.get(1),((Boolean)vCmd.get(2)).booleanValue());
//            }
//            else if (sCmd.equals("GetInherits")) {
//            	((TGSharePermissionsPanel)vCmd.get(2)).setChkInheritParentPermissions(this.ftpSrvConn.getInherits((String)vCmd.get(1)));
//            }
//            else if(sCmd.equals("Rollback")) {
//            	String path = (String)vCmd.get(1);
//            	Version version = (Version)vCmd.get(2);
//            	this.ftpSrvConn.rollbackVersion(path,version);
//            }
//          else if(sCmd.equals("Version")){
//        	String path = (String)vCmd.get(1);
//        	boolean versionable = ((Boolean)vCmd.get(2)).booleanValue();
//        	this.ftpSrvConn.enableVersioning(path,versionable);
//        }
            else if(sCmd.equals("Chmod")){
            	String path = (String)vCmd.get(1);
            	int permissionCode = ((Integer)vCmd.get(2)).intValue();
            	boolean recursive = ((Boolean)vCmd.get(3)).booleanValue();
            	String username = (String)vCmd.get(4);
            	
            	this.ftpSrvConn.setPermissions(path, permissionCode, recursive, username);
            }
            else if(sCmd.equals("Pwd")){
            	this.sDir = this.ftpSrvConn.getDir();
            }
            else if(sCmd.equals("Cwd")){
                String sNewDir=(String)vCmd.get(1);
                
                if (sNewDir.startsWith("~")) {
                
                    sNewDir = ftpSrvConn.getDirHome() + ftpServer.getSeparator() + sNewDir.substring(1);
                
                } else if (sNewDir.startsWith("$")) {
                    String variableName = sNewDir.substring(1);
                    
                    if (variableName.startsWith("TG_")) {
                        // if the tg variable is there, try it without TG_ prepended
                        // ie. $WORK instead of $TG_WORK
                        if (envProps.getProperty(variableName) == null) {
                            variableName = variableName.substring("TG_".length());
                            sNewDir = envProps.getProperty(variableName);
                        } else {
                            sNewDir = envProps.getProperty(variableName);
                        }
                    } else {
                        sNewDir = envProps.getProperty(variableName);
                    }
                    
                    if (sNewDir == null) {
                        AppMain.Message(AppMain.getApplet(), "The environmental variable " + 
                                (String)vCmd.get(1) + "\nis not defined in your default environment."); 
                        sNewDir = this.sDir;
                    }
                }
                
                LogManager.debug("Changing directory to " + sNewDir);
                
                if(!sNewDir.equals(this.sDir)){
                	this.sDir = this.ftpSrvConn.setDir(sNewDir);
                    cmdAdd("List");
                }
            }
//            else if(sCmd.equals("Expand")) {
//                String sNewDir=((FileNode)vCmd.get(1)).getAbsolutePath();
//                System.out.println("expand: Setting adaptor to directory " + sNewDir + "\ncurrent directory is " + this.sDir);
//                if(!sNewDir.equals(this.sDir)){
//                    this.sDir = this.ftpSrvConn.setDir(sNewDir);
//                    list((FileNode)vCmd.get(1),(DefaultMutableTreeNode)vCmd.get(2));
//                }
//            }
            else if(sCmd.equals("Filter")){
                String sNewDir=(String)vCmd.get(1);
                if(!sNewDir.equals(this.sDir)){
                    this.sDir = this.ftpSrvConn.setDir(sNewDir);
                    list(sNewDir);
                }
            }
            else if(sCmd.equals("CwdHome")){
            	this.sDir = this.ftpSrvConn.setDirHome();
                cmdAdd("List");
            }
            else if(sCmd.equals("FileAdd")){
                fileAdd((String)vCmd.get(1),((Integer)vCmd.get(2)).intValue());
                cmdAdd("List");
            }
            else if(sCmd.equals("FileDel")){
                fileDel((String)vCmd.get(1),((Integer)vCmd.get(2)).intValue());
                cmdAdd("List");
            }
            else if(sCmd.equals("FileRen")){
                fileRen((String)vCmd.get(1),(String)vCmd.get(2));
                cmdAdd("List");
            }
            else if(sCmd.equals("Find")){
                // if there is an extra argument, is the root directory
                // from which to search
                String oldCwd = getDir();
                
                if ((String)vCmd.get(1) != null) {
                    cmdAdd("Cwd");
                }
                
                search((String)vCmd.get(2));
                
                // now return to the previous directory
                this.sDir = this.ftpSrvConn.setDir(oldCwd);
//                cmdAdd("List");
//                cmdAdd("Cwd",oldCwd,null,true);
            }
        }
        catch(IOException ex){
            if(ex instanceof EOFException || ex instanceof SocketException){
                bRemove = false;
                this.bConnected=false;
                interrupt();
            }
            else {
                AppMain.Error(this.pnlBrowse,ex.getMessage(),SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_ERROR));
                ex.printStackTrace();
            }
        }
        catch(ClientException ex){
            AppMain.Error(this.pnlBrowse,ex.getMessage(),SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_ERROR));
            ex.printStackTrace();
            org.teragrid.portal.filebrowser.applet.util.LogManager.debug(ex.getLocalizedMessage() + " at " + (ex.getStackTrace())[0]);  
        }
        catch(PermissionException ex) {
        	AppMain.Error(this.pnlBrowse,ex.getMessage(), SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_ERROR));
        	pnlBrowse.disConn();
        }
        catch(ServerException ex){

        	AppMain.Error(this.pnlBrowse,ex.getCustomMessage(), SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_ERROR));
			switch (ex.getCode()) {
			case ServerException.REPLY_TIMEOUT:

				bRemove = false;
				this.bConnected = false;
				this.pnlBrowse.disConn();
				break;
			default:
				//AppMain.Error(this.pnlBrowse,ex.getMessage(), SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_ERROR));
				break;
			}
			LogManager.error(ex.getCustomMessage(), ex);
        }
        finally{
            if(null != this.pnlBrowse) {
            	this.pnlBrowse.resumeLayout();
            }
        }
        return bRemove;
    }

    public boolean close(){
//    	if (!this.ftpSrvConn.isIdle()) {
//    		AppMain.Message(SGGCResourceBundle.getResourceString(ResourceName.KEY_MSG_FTPTHREAD_FTPNOTIDLE));
//    		return false;
//    	}

        try{
            if(this.bConnected) {
            	this.ftpSrvConn.close();
            	this.ftpSrvConn.setIdle(true);
            }
            this.bConnected=false;
            this.nRetry=0;
            interrupt();
        }
        catch(IOException ex){
        	ex.printStackTrace();
        }
        catch(ServerException ex){
        	ex.printStackTrace();
        }
        
        return true;
    }

    protected void connect(){
        if(null != this.pnlBrowse) {
        	this.pnlBrowse.suspendLayout();
        }
        try {
        	if (this.ftpSrvConn != null) {
        		this.ftpSrvConn.close();
        	}
        	
        	
    	    this.ftpSrvConn = this.ftpServer.getConnection();
        	
    	    if(null != this.ftpSrvConn && (this.bConnected = this.ftpSrvConn.isConnected())){
            	this.ftpSrvConn.setIdle(false); //This connection is used for browsing only!
                if(null==this.sDir || this.sDir.equals("")){
                	this.sDir = this.ftpSrvConn.getDir();
                    if(cmdEmpty()) {
                    	cmdAdd("List");
                    }
                }
            }
    	    
    	    // disable the shortcuts until we sort out how to get the 
    	    // environmental variables from the systems.
//    	    if (!SiteUtil.isLocal(ftpServer)) this.envThread.run();
        }
        catch(IOException ex) {
            this.nRetry = 0;
            if (ex.getMessage().indexOf("Connection refused") > -1) {
                AppMain.Error(this.pnlBrowse,
                        "A connection refused exception\n" + 
                        "was thrown by the server. This\n" + 
                        "generally means that the server\n" +
                        "is temporarily unavailable. If\n" +
                        "this problem persists, please contact\n" +
                        "help@xsede.org.",
                        "Connection Error");
            } else if(ex.getMessage().indexOf("Unknown CA") > -1) {
                AppMain.Error(this.pnlBrowse,
                        "An authentication exception was thrown\n" +
                        "by the server because the trusted CA\n" + 
                        "certificate for " + ftpServer.name + "\n" + 
                        "is not present. Please close your browser\n" +
                        "and reoload this applet to correct the problem.",
                        "Authentication failed");
            } else if (ex instanceof UnknownHostException) {
            	AppMain.Error(this.pnlBrowse,
            			"Could not find resolve the hostname. " + 
            			(this.ftpServer.userDefined ? "\nPlease check the hostname you\nentered \"" + 
            					ftpServer.host + "\"\nand try again." : "Please\nreset your resource list."),
            			"Connection error");
            } else {
                AppMain.Error(this.pnlBrowse,"User credentials have expired.\n" + 
                    "Please refresh your proxy by\n" + 
                    "reauthenticating to the portal.",
                    "Authentication Error");
            }
            LogManager.error("Failed to connect to resource.",ex);
            
        }
        catch(ServerException ex){
            this.nRetry = 0;
            if (ex.getMessage().indexOf("Bad password") > -1) {
                AppMain.Error(this.pnlBrowse,
                        "An authentication exception was thrown " +
                		"by the server because your DN\n\"" + 
                		ConfigOperation.getInstance().getConfigValue("dn") + "\"\n" +
                		"has not been mapped to a user account on " + ftpServer.name + 
                		". If this problem persists,\n" +
                		"please contact help@xsede.org.",
                        "Authentication failed");
            } else if(ex.getMessage().indexOf("Unknown CA") > -1) {
                AppMain.Error(this.pnlBrowse,
                        "An authentication exception was thrown\n" +
                        "by the server because the trusted CA\n" + 
                        "certificate for " + ftpServer.name + "\n" + 
                        "is not present. Please close your browser\n" +
                        "and reoload this applet to correct the problem.",
                        "Authentication failed");
            } else if(ex.getMessage().indexOf("timed out") > -1) {
                AppMain.Error(this.pnlBrowse,
                        "The connection to " + ftpServer.name + "\n" +
                        "timed out while waiting to establish a\n" + 
                        "connection. If this problem persists,\n" +
                        "Please contact your local system admin.",
                        "Authentication failed");
            } else {
                AppMain.Error(this.pnlBrowse,
                        "A connection refused exception\n" + 
                        "was thrown by the server. This\n" + 
                        "generally means that the server\n" +
                        "is temporarily unavailable. If\n" +
                        "this problem persists, please\n" +
                        "contact help@xsede.org.",
                        "Connection Error");
            }
            LogManager.error("Failed to connect to resource. Remote server threw an exception.",ex);
        }
        catch (Exception e) {
        	this.nRetry = 0;
        	AppMain.Error(this.pnlBrowse, e.getMessage());
        }
        finally{
            if(null != this.pnlBrowse) {
                this.pnlBrowse.resumeLayout();
            }
        }
    }

    protected void fileAdd(String sFile,int nFileType) throws IOException,ServerException,ClientException{
        switch (nFileType) {
        case FileInfo.DIRECTORY_TYPE:
        	this.ftpSrvConn.makeDir(sFile);
            break;
        case FileInfo.FILE_TYPE:
        	this.ftpSrvConn.makeFile(sFile);
            break;
        default:
            break;
        }
    }

    protected void fileDel(String sFile,int nFileType) throws IOException,ServerException{
        switch (nFileType) {
        case FileInfo.DIRECTORY_TYPE:
        	this.ftpSrvConn.deleteDir(sFile);
            break;
        case FileInfo.FILE_TYPE:
        	this.ftpSrvConn.deleteFile(sFile);
            break;
        default:
            break;
        }
    }

    protected void fileRen(String sFile,String sNewFile) throws IOException,ServerException{
    	this.ftpSrvConn.rename(sFile, sNewFile);
    }

    public synchronized String getDir(){
        return this.sDir;
    }

    public void setEnvProperties(List<EnvironmentVariable> environment) {
    	this.envProps.clear();
    	for(EnvironmentVariable environmentVariable: environment) {
    		this.envProps.put(environmentVariable.getName(), environmentVariable.getValue());
    	}
    }
    
    private void search(String regex) throws ServerException, ClientException, IOException {
    	String currentDir = ftpSrvConn.getDir();
    	pnlBrowse.pnlSearch.setUrlSearch(SHGridFTP.search(ftpSrvConn, regex, new SearchListener(pnlBrowse.pnlSearch)));
    	ftpSrvConn.setDir(currentDir);
    }
    
    public boolean isConnected(){
        return this.bConnected;
    }

    protected void keepAlive(){
//    	sggc.utils.LogManager.debug("keep alive: " + this.ftpServer.host);
    	
        //Keep connection alive or refresh
        if (this.bConnected/* && this.ftpSrvConn.isIdle()*/) {
            if(!this.bRemoteMode) {
            	cmdAdd("List",null,null,false);
            } else{
                double nRnd = Math.random();
                if (nRnd < .2) {
                    cmdAdd("Pwd", null, null, false);
                } else if (nRnd < .4) {
                    cmdAdd("Pwd", null, null, false);
                } else if (nRnd < .6) {
                    cmdAdd("Type", new Integer(Session.TYPE_ASCII), null, false);
                } else if (nRnd < .8) {
                    cmdAdd("Type", new Integer(Session.TYPE_IMAGE), null, false);
                } else {
                    cmdAdd("List", null, null, false);
                }
            }
        }
    }

    protected void list() throws IOException,ServerException,ClientException{
        List<FileInfo> fileList = new ArrayList<FileInfo>();
        
        if (ftpServer.protocol.equals(FileProtocolType.GRIDFTP)) {
	        this.ftpSrvConn.setType(Session.TYPE_ASCII);
	        this.ftpSrvConn.setDTP(this.ftpServer.passiveMode);
        }
        Vector<FileInfo> vl = this.ftpSrvConn.list("*", this.ftpServer.showHidden);

        //load the parent node
        FileInfo paraentFolder = new FileInfo();
        paraentFolder.setName("..");
        paraentFolder.setSize(0);
        paraentFolder.setFileType(FileInfo.DIRECTORY_TYPE);
        fileList.add(paraentFolder);

        for(ListIterator<FileInfo> i=vl.listIterator();i.hasNext();){
            FileInfo f=(FileInfo)i.next();
            if(ListModel.getFileName(f).equals(".") || ListModel.getFileName(f).equals("..")) {
            	continue;
            }
            fileList.add(f);
        }
        if(null != this.pnlBrowse) {
            this.pnlBrowse.setCurrentDir(this.ftpSrvConn.getDir());
        	this.pnlBrowse.list(fileList);   
        	
        }
    }
    
    public List list(String newDir) throws IOException,ServerException,ClientException{
        List<FileInfo> fileList = new ArrayList<FileInfo>();

        this.ftpSrvConn.setType(Session.TYPE_ASCII);
        this.ftpSrvConn.setDTP(this.ftpServer.passiveMode);
        this.sDir = this.ftpSrvConn.setDir(newDir);
        Vector<FileInfo> vl = this.ftpSrvConn.list("*", this.ftpServer.showHidden);

        //load the parent node
        FileInfo paraentFolder = new FileInfo();
        paraentFolder.setName("..");
        paraentFolder.setSize(0);
        paraentFolder.setFileType(FileInfo.DIRECTORY_TYPE);
        fileList.add(paraentFolder);

        for(ListIterator<FileInfo> i=vl.listIterator();i.hasNext();){
            FileInfo f=(FileInfo)i.next();
            if(ListModel.getFileName(f).equals(".") || ListModel.getFileName(f).equals("..")) {
                continue;
            }
            fileList.add(f);
        }
        this.pnlBrowse.setCurrentDir(this.ftpSrvConn.getDir());
        
        return fileList;
//        if(null != this.pnlBrowse) {
//            this.pnlBrowse.list(fileList);   
//            this.pnlBrowse.setCurrentDir(this.ftpSrvConn.getDir());
//        }
    }

    public void holdRefresh(boolean isPaused) {
        this.bPaused = isPaused;
    }
    
    
    /**
     * Retrieve the permissions of a file/folder synchronously. 
     * This is kept generic for future use, but right now is just 
     * in there for irods resources because cost of getting this
     * info wiht the listing is very expensive.
     *  
     * @param path absolute path of the file/folder
     * @return
     * @throws IOException
     * @throws ServerException
     * @throws ResourceException
     */
    public Object getPermissions(String path) throws IOException, ServerException, ResourceException {
    	return this.ftpSrvConn.getPermissions(path);
    }

	public List<User> findUsers(String searchString) throws IOException
	{
		return this.ftpSrvConn.findUsers(searchString);
	}
    
}

