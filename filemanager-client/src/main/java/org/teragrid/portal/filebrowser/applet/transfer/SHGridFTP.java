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
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.util.List;
import java.util.ListIterator;
import java.util.StringTokenizer;
import java.util.Vector;

import org.globus.ftp.Buffer;
import org.globus.ftp.ByteRangeList;
import org.globus.ftp.DataSink;
import org.globus.ftp.DataSinkStream;
import org.globus.ftp.FTPClient;
import org.globus.ftp.FileInfo;
import org.globus.ftp.Session;
import org.globus.ftp.exception.ClientException;
import org.globus.ftp.exception.FTPException;
import org.globus.ftp.exception.NotImplementedException;
import org.globus.ftp.exception.ServerException;
import org.teragrid.portal.filebrowser.applet.AppMain;
import org.teragrid.portal.filebrowser.applet.exception.ResourceException;
import org.teragrid.portal.filebrowser.applet.util.LogManager;

import edu.utexas.tacc.wcs.filemanager.common.model.User;
import edu.utexas.tacc.wcs.filemanager.common.model.enumeration.FileProtocolType;

class FTPCommand {
	public static final int NONE = 0;

	public static final int LIST = 1;

	public static final int TYPE = 2;

	public static final int DTP = 3;

	public static final int MODE = 4;

	public static final int PWD = 5;

	public static final int CWD = 6;

	public static final int CWDHOME = 7;

	public static final int FILEADD = 8;

	public static final int FILEDEL = 9;

	public static final int FILEREN = 10;

	public static final int GET = 11;

	public static final int PUT = 12;
}


class FTPReply {
	public static final int REPLY_TIMEOUT = 421;

	public static final int REPLY_TOOMANY = 530;

}



class FTPTransfer {
	public static final int TRANSFER_NONE = 0;

	public static final int TRANSFER_OVERWRITE = 1;

	public static final int TRANSFER_PARTIAL = 2;

	public static final int TRANSFER_SKIP = 3;
}

class TransferStatus {
	public static final int IN_WAIT = -1;

	public static final int IN_TRANSFER = 0;

	public static final int JOB_DONE = 1;
}

@SuppressWarnings("unused")
public class SHGridFTP {
	
    private boolean used = false;

	private boolean bIdle = true;

	private boolean bConnected = false;
	
	private FTPClient ftpClient = null;

	private FTPSettings ftpServer = null;

	private FileProtocolType nType = FileProtocolType.GRIDFTP;
	
	private int sessionType = Session.TYPE_ASCII;
	
	private String sRemote = null, sRemoteHome = null;

	private String sLocalFile = "", sRemoteFile = "";

	private int searchResultCount = 0;
	
	private int searchDepthMax = 0;
	
	private ByteRangeList rLocalRange = new ByteRangeList(),
			rRemoteRange = new ByteRangeList();

	// protected int nLastError = 0;

	/**
	 * * Initiate GRIDFTP session
	 * 
	 * @param host
	 *            remote GRIDFTP host
	 * @param filePort
	 *            remote GridFTP port (default is 2811)
	 */
	public SHGridFTP(Component parent,String sHost) throws IOException, ServerException {
		this(new FTPSettings(sHost));
		ftpServer.parent = parent;
	}

	public SHGridFTP(Component parent,String sHost, FileProtocolType nType) throws IOException,
			ServerException {
		this(new FTPSettings(sHost, nType));
		ftpServer.parent = parent;
	}

	public SHGridFTP(Component parent,String sHost, int nPort, FileProtocolType nType) throws IOException,
			ServerException {
		this(new FTPSettings(sHost, nPort, nType));
		ftpServer.parent = parent;
	}
	
	public SHGridFTP(FTPSettings ftpServer) throws IOException, ServerException {
		this.ftpServer = ftpServer;
		connect();
	}

	public void abort() throws IOException, ServerException {
		this.ftpClient.abort();
	}

	public FTPClient getRawClient() {
		return this.ftpClient;
	}

	public void setParallel(int nParallel) throws IOException, ServerException {
		if (this.ftpClient instanceof GridFTP) {
			((GridFTP) this.ftpClient).setParellel(nParallel);
		}
	}

	public String getDir()  {
		return this.sRemote;
	}

	public String setDir(String sDir) throws IOException, ServerException {
		if (sDir.equals(this.sRemote)) {
			return this.sRemote;
		}
		this.ftpClient.changeDir(sDir);

		return this.sRemote = this.ftpClient.getCurrentDir();
	}

	public String getDirHome() throws IOException, ServerException {
		return this.sRemoteHome;
	}

	public String setDirHome() throws IOException, ServerException {
		if (FileProtocolType.FILE == this.ftpServer.protocol) {
			return setDir("~");
		} else {
			return setDir(this.sRemoteHome);
		}
	}

	public void setDTP(boolean bPassive) throws IOException, ServerException,
			ClientException {
		if (bPassive) {
			// if(ftpClient.isPassiveMode()) return;
			this.ftpClient.setPassive();
			this.ftpClient.setLocalActive();
		} else {
			// if(ftpClient.isActiveMode()) return;
			this.ftpClient.setLocalPassive();
			this.ftpClient.setActive();
		}
	}

	public void setDTP(boolean bPassive, boolean bRemote) throws IOException,
			ServerException, ClientException {
		if (bRemote) {
			if (bPassive) {
				this.ftpClient.setPassive();
			} else {
				this.ftpClient.setActive();
			}
		} else {
			if (bPassive) {
				this.ftpClient.setLocalPassive();
			} else {
				this.ftpClient.setLocalActive();
			}
		}
	}
	
	public void setPermissions(String path, int permission, boolean recursive, String username) throws IOException, ServerException {
		this.ftpClient.site(username + " " + permission + " " + path + " " + recursive);
	}
	
	public Object getPermissions(String path) throws IOException, ServerException, ResourceException {
		if (this.ftpClient instanceof Irods) {
			return ((Irods)ftpClient).getAllPermissions(path);
		} else {
			throw new IOException("Direct file permission queries not supported for " + 
					ftpServer.protocol.name() + " resources.");
//			throw new NotSupportedException("Direct file permission queries not supported for " + 
//					FileProtocolType.FTP_PROTOCOL[ftpServer.type] + " resources.");
		}
	}
	
	
	// uncomment when adding share support
//	public void setInherits(String path, boolean inheritParentPermissions) throws ServerException{
//		((TGShare)this.ftpClient).setInheritsParentPermissions(path,inheritParentPermissions);
//	}
//	public boolean getInherits(String path) throws ServerException{
//		return ((TGShare)this.ftpClient).getInheritsParentPermissions(path);
//	}
//	
//	public Version getCurrentVersion(String path) throws ServerException {
//		return ((TGShare)this.ftpClient).getCurrentVersion(path);
//	}
//	public VersionHistory getVersionHistory(String path) throws ServerException {
//		return ((TGShare)this.ftpClient).getVersionHistory(path);
//	}
//	public void enableVersioning(String path, boolean versionable) throws ServerException {
//		((TGShare)this.ftpClient).enableVersioning(path, versionable);
//	}
//	public void rollbackVersion(String path, Version version) throws ServerException {
//		((TGShare)this.ftpClient).revertVersion(path, version);
//	}
//	public void setVersionComment(String path, String comment) throws ServerException {
//		((TGShare)this.ftpClient).setVersionComment(path, comment);
//	}
	
	public void setMode(int nMode) throws IOException, ServerException {
		this.ftpClient.setMode(nMode);
	}

	public long getSize(String sFile) throws IOException, ServerException {
		return this.ftpClient.getSize(sFile);
	}

	/*
	 * public int getType(){ return nType; }
	 */

	public void setType(int nType) throws IOException, ServerException {
		// if(this.nType!=nType) ftpClient.setType(this.nType=nType);
//		this.nType = nType;
		this.sessionType = nType;
		this.ftpClient.setType(nType);
	}

	public void connect() throws IOException, ServerException {
		
		switch (this.ftpServer.protocol) {
		case GRIDFTP:
			this.ftpClient = new GridFTP(this.ftpServer.host, this.ftpServer.filePort, AppMain.defaultCredential);
			
			switch (this.ftpServer.loginMode) {
				case FTPLogin.LOGIN_USEPROXYINIT:
					this.ftpClient.authorize(null, null);
					break;
				case FTPLogin.LOGIN_USECERT:
					this.ftpClient.authorize(FTPSettings.DefaultCertificate, null);
					// this.ftpClient.authorize("/home/guser/.globus/userkey.pem",
					// "/home/guser/.globus/usercert.pem");
					break;
				case FTPLogin.LOGIN_ASUSER:
				default:
					this.ftpClient.authorize(this.ftpServer.userName,
							this.ftpServer.password);
					break;
			}
			this.ftpServer.setGSSCred(((GridFTP)(this.ftpClient)).getGSSCred());

			((GridFTP) this.ftpClient)
					.setTCPBufferSize(FTPSettings.DefaultBufferSize * 1024);
			 //setParallel(ftpServer.connParallel);
			 setParallel(1);
			 ((GridFTP) this.ftpClient).setClientWaitParams(20000, 100);
			this.sRemote = this.sRemoteHome = this.ftpClient.getCurrentDir();
			break;
		case BBFTP:
			this.ftpClient = new BBFTP(this.ftpServer.host, this.ftpServer.filePort);
			this.ftpClient.authorize(this.ftpServer.userName, this.ftpServer.password);
			this.sRemote = this.sRemoteHome = this.ftpClient.getCurrentDir();
			break;
		case SFTP:
			this.ftpClient = new SFTP(this.ftpServer.host, this.ftpServer.filePort);
			this.ftpClient.authorize(this.ftpServer.userName, this.ftpServer.password);
			this.sRemote = this.sRemoteHome = this.ftpClient.getCurrentDir();
			break;
		case FTP:
			this.ftpClient = new FTP(this.ftpServer.parent,this.ftpServer.host, this.ftpServer.filePort);
			this.ftpClient.authorize(this.ftpServer.userName, this.ftpServer.password);
			this.sRemote = this.sRemoteHome = this.ftpClient.getCurrentDir();
			break;
		case HTTP:
			this.ftpClient = new HTTP(this.ftpServer.host, this.ftpServer.filePort);
			((HTTP) this.ftpClient).openSocket();
			break;
		case S3:
		    this.ftpClient = new S3(this.ftpServer.userName,this.ftpServer.password);
		    this.sRemote = this.sRemoteHome = this.ftpClient.getCurrentDir();
		    break;
		 // uncomment when adding share support
//		case XSHARE:
//		    this.ftpClient = new TGShare(this.ftpServer.userName,this.ftpServer.password);
//		    this.sRemote = this.sRemoteHome = this.ftpClient.getCurrentDir();
//		    break;
		case IRODS:
            this.ftpClient = new Irods(this.ftpServer.host,
            							this.ftpServer.filePort,
            							this.ftpServer.userName,
            							this.ftpServer.password,
            							this.ftpServer.zone,
            							this.ftpServer.resource);
            this.ftpClient.authorize(this.ftpServer.userName,
										this.ftpServer.password);
            this.sRemote = this.sRemoteHome = this.ftpClient.getCurrentDir();
            break;
		case FILE:
		default:
			this.ftpClient = new FileSys(this.ftpServer.host, this.ftpServer.filePort);
			this.sRemote = this.sRemoteHome = setDirHome();
			break;
		}
		// ftpServer.addConn(this);
		this.bConnected = true;
	}

	public void close() throws IOException, ServerException {
		this.ftpServer.removeConn(this);
		this.bConnected = false;
		this.ftpClient.close();
	}

	public void deleteDir(String sDir) throws IOException, ServerException {
		if (this.ftpClient instanceof FileSys)
		if (!(this.ftpClient instanceof FileSys) && !(this.ftpClient instanceof Irods)) {
			String curDir = this.ftpClient.getCurrentDir();
			try {
				this.ftpClient.setType(Session.TYPE_ASCII);
				this.setDTP(this.ftpServer.passiveMode);
				this.ftpClient.changeDir(sDir);
				Vector<FileInfo> vl = this.list("*");
				
				for (ListIterator<FileInfo> i = vl.listIterator(); i.hasNext();) {
					FileInfo f = i.next();

					if (f.isFile()) {
						this.deleteFile(f.getName());
					} else if (!f.getName().equalsIgnoreCase(".") && !f.getName().equalsIgnoreCase("..")){
						this.deleteDir(f.getName());
					}
				}
			} catch (ClientException e) {
				e.printStackTrace();
			} finally {
				this.ftpClient.changeDir(curDir);
			}
		}		
		this.ftpClient.deleteDir(sDir);
	}

	public void deleteFile(String sFile) throws IOException, ServerException {
		this.ftpClient.deleteFile(sFile);
	}

	public boolean isConnected() {
		return this.bConnected;
	}
	
	public Vector<FileInfo> list(String sFilter) throws IOException, ServerException,
			ClientException {
		return list(sFilter, true);
	}

	@SuppressWarnings("unchecked")
	public Vector<FileInfo> list(String sFilter, boolean bShowHidden) throws IOException,
			ServerException, ClientException {
	    Vector<FileInfo> v = null;
	    
	    // adjust for the 
//	    if (ftpServer.host.indexOf("mss.ncsa") > -1 ) { 
//	        ((GridFTP)this.ftpClient).setLocalNoDataChannelAuthentication();
//	        v = listNoMinusD(sFilter);
//	    } else {
	        v = this.ftpClient.list(sFilter);
//	    }
        
	    if (!bShowHidden) {
		    
			for (int i = v.size() - 1; i >= 0; i--) {
				FileInfo f = (FileInfo) v.get(i);
				if (f.getName().startsWith(".")) {
				    
					v.remove(i);
				}
			}
		} 
		return v;
		// return ftpClient.list(sFilter);
	}

	/** this method is used for old servers that do not support the "list -d"
     * command. The problem is that is does not work for directories, only for
     * files.
     */
    private Vector<FileInfo> listNoMinusD(String filter)
            throws ServerException, ClientException, IOException {

        final ByteArrayOutputStream received = new ByteArrayOutputStream(1000);

        // unnamed DataSink subclass will write data channel content
        // to "received" stream.

        DataSink sink = new DataSink() {
            public void write(Buffer buffer) throws IOException {
                received.write(buffer.getBuffer(), 0, buffer.getLength());
            }

            public void close() throws IOException {
            }

        };

        this.ftpClient.list(filter, "", sink);

        // transfer done. Data is in received stream.
        // convert it to a vector.

        LogManager.debug("result of list " + filter + " is: "
                + received.toString());

        BufferedReader reader = new BufferedReader(new StringReader(received
                .toString()));

        Vector<FileInfo> fileList = new Vector<FileInfo>();
        FileInfo fileInfo = null;
        String line = null;

        while ((line = reader.readLine()) != null) {
            if (line.startsWith("total"))
                continue;
            try {
                fileInfo = new FileInfo(fixListReply(line));
                fileList.addElement(fileInfo);
            } catch (org.globus.ftp.exception.FTPException e) {
                LogManager.debug("WARNING, could not create FileInfo for: " + line);
/*
                ClientException ce = new ClientException(
                        ClientException.UNSPECIFIED,
                        "Could not create FileInfo");
                ce.setRootCause(e);
                throw ce;
                */
            }
        }

        return fileList;
    }

	
	public Vector<FileInfo> listR(String sFilter) throws IOException, ServerException,
			ClientException {
		String sDirPre = "";
		Vector<FileInfo> v = new Vector<FileInfo>();
		OutputStream o = new ByteArrayOutputStream();
		DataSinkStream iSink = new DataSinkStream(o);
		this.ftpClient.list(sFilter, "-R", iSink);
		String[] sLines = o.toString().split("\r\n");
		for (int i = 0, l = sLines.length; i < l; i++) {
			if (sLines[i].startsWith("total")) {
				continue;
			} else if (sLines[i].startsWith("/")) {
				sDirPre = sLines[i].substring(0, sLines[i].length());
				continue;
			}
			try {
				FileInfo f = new FileInfo(sLines[i]);
				f.setName(sDirPre + f.getName());
				v.add(f);
			} catch (FTPException ex) {
			}
		}
		return v;
		// return ftpClient.list(sFilter,"-R");
	}
	
	public void makeDir(String sDir) throws IOException, ServerException {
		// ftpClient.exists(sDir);
		this.ftpClient.makeDir(sDir);

	}
	
	public boolean exists(String sDir) throws IOException, ServerException {
		return this.ftpClient.exists(sDir);

	}

	public void makeFile(String sFile) throws IOException, ServerException,
			ClientException {
		this.ftpClient.put(new File("/dev/null"), sFile, false);
	}

	public void rename(String sOldName, String sNewName) throws IOException,
			ServerException {
		this.ftpClient.rename(sOldName, sNewName);
	}

	public static UrlCopy transfer(SHGridFTP srcConn, SHGridFTP destConn,
			FileTransferTask fileTask, TransferListener listener) {
		// srcConn.bIdle=destConn.bIdle=false;
	    UrlCopy urlCopy = new UrlCopy(srcConn, destConn, fileTask);
		urlCopy.addUrlCopyListener(listener);
		fileTask.setProcess(urlCopy);
		(new Thread(urlCopy)).start();
		return urlCopy;
		// destConn.bIdle=srcConn.bIdle=true;
	}

	public static UrlSearch search(SHGridFTP srcConn, String regex, SearchListener listener) {
	    UrlSearch urlSearch = new UrlSearch(srcConn, regex);
	    urlSearch.addUrlSearchListener(listener);
	    (new Thread(urlSearch)).start();
	    return urlSearch;
	}
	public boolean isIdle() {
		return this.bIdle;
	}

	public void setIdle(boolean idle) {
		this.bIdle = idle;
	}

	public FTPClient getFtpClient() {
		return this.ftpClient;
	}

	public FTPSettings getFtpServer() {
		return this.ftpServer;
	}
	
	/**
     * Some servers seem to return a strange format directory listing that the
     * COG does not parse. For instance, the NCSA grid ftp server returns
     * something like:
     * 
     * -rw------- 1 ccguser ac DK common 861 Jul 26 16:56 qcrjm.hist
     * 
     * while the cog only parses:
     * 
     * drwxr-xr-x 2 guest other 1536 Jan 31 15:15 run.bat
     * 
     * This method removes the unused tokens from the reply
     */
    public String fixListReply(String reply) throws FTPException {
        StringTokenizer tokens = new StringTokenizer(reply);

        LogManager.debug("fixing old ftp server list reply: " + reply
                    + "#tokens = " + tokens.countTokens());
        

        if (tokens.countTokens() < 10) {
            return reply;
        }

        LogManager.debug("COG workaround parsing old ftp server list reply.");
        
        String res = "";

        res += tokens.nextToken(); // permissions
        res += " " + tokens.nextToken(); // ???
        res += " " + tokens.nextToken(); // owner
        tokens.nextToken(); // skip
        tokens.nextToken(); // skip

        /*
         * res += " " + tokens.nextToken(); // group res += " " +
         * tokens.nextToken(); // size res += " " + tokens.nextToken(); // month
         * res += " " + tokens.nextToken(); // day res += " " +
         * tokens.nextToken(); // time res += " " + tokens.nextToken(); //
         * filename
         */

        // if there are more tokens, just add them
        while (tokens.hasMoreTokens()) {
            res += " " + tokens.nextToken();
        }

        LogManager.debug("fixed version is: " + res);
        
        return res;
    }

	public List<User> findUsers(String searchString) throws IOException, NotImplementedException
	{
		if (this.ftpClient instanceof Irods) {
			return ((Irods)ftpClient).findUsers(searchString);
		} else {
			return null;
////			throw new IOException("Direct user queries not supported for " + 
////					FileProtocolType.FTP_PROTOCOL[ftpServer.type] + " resources.");
//		}
		}
	}
}
