/* 
 * Created on Jul 23, 2008
 * 
 * Developed by: Rion Dooley - dooley [at] tacc [dot] utexas [dot] edu
 * 				 TACC, Texas Advanced Computing Center
 * 
 * https://www.tacc.utexas.edu/
 */

package edu.utexas.tacc.wcs.filemanager.client.transfer;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import org.apache.commons.lang.StringUtils;
import org.globus.ftp.FTPClient;
import org.globus.ftp.FileInfo;
import org.globus.ftp.HostPort;
import org.globus.ftp.exception.ClientException;
import org.globus.ftp.exception.ServerException;
import org.globus.ftp.vanilla.Reply;
import org.globus.gsi.GlobusCredential;
import org.globus.gsi.gssapi.GlobusGSSCredentialImpl;
import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSException;
import org.irods.jargon.core.connection.GSIIRODSAccount;
import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.core.connection.IRODSProtocolManager;
import org.irods.jargon.core.connection.IRODSSession;
import org.irods.jargon.core.connection.IRODSSimpleProtocolManager;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.protovalues.FilePermissionEnum;
import org.irods.jargon.core.pub.CollectionAO;
import org.irods.jargon.core.pub.CollectionAndDataObjectListAndSearchAO;
import org.irods.jargon.core.pub.DataObjectAO;
import org.irods.jargon.core.pub.DataTransferOperations;
import org.irods.jargon.core.pub.IRODSAccessObjectFactory;
import org.irods.jargon.core.pub.IRODSAccessObjectFactoryImpl;
import org.irods.jargon.core.pub.UserAO;
import org.irods.jargon.core.pub.UserGroupAO;
import org.irods.jargon.core.pub.domain.AvuData;
import org.irods.jargon.core.pub.domain.UserFilePermission;
import org.irods.jargon.core.pub.domain.UserGroup;
import org.irods.jargon.core.pub.io.IRODSFile;
import org.irods.jargon.core.pub.io.IRODSFileFactory;
import org.irods.jargon.core.query.CollectionAndDataObjectListingEntry;
import org.irods.jargon.core.query.IRODSGenQuery;
import org.irods.jargon.core.query.IRODSQueryResultRow;
import org.irods.jargon.core.query.IRODSQueryResultSetInterface;
import org.irods.jargon.core.query.MetaDataAndDomainData;
import org.irods.jargon.core.query.RodsGenQueryEnum;

import edu.utexas.tacc.wcs.filemanager.client.AppMain;
import edu.utexas.tacc.wcs.filemanager.client.file.FileInfoFactory;
import edu.utexas.tacc.wcs.filemanager.client.util.LogManager;
import edu.utexas.tacc.wcs.filemanager.client.util.ServletUtil;
import edu.utexas.tacc.wcs.filemanager.common.model.User;

/**
 * Adaptor class to interface with IRODS
 * 
 * @author Rion Dooley < dooley [at] tacc [dot] utexas [dot] edu >
 *
 */
@SuppressWarnings("unused")
public class Irods extends FTPClient {
    
	public static String Prefix = "irods://";

	/** integer from some queries signifying user can read a file */
	static final int READ_PERMISSIONS = 1050;
	/** integer from some queries signifying user can write to a file */
	static final int WRITE_PERMISSIONS = 1120;
	
	private long lastUpdated;
	
	private IRODSProtocolManager irodsConnectionManager;
	private IRODSSession irodsSession;
	private IRODSAccount irodsAccount;
	private IRODSAccessObjectFactory accessObjectFactory;
    
    private String homeDirectory;
    private String host;
    private String zone;
    private String resource;
    private int port;
    private String password;
    
    private String currentDirectory;
    
    public Irods(String sHost, int nPort, String username, 
    		String password, String zone, 
    		String resource) throws IOException, ServerException {
    	this.host = sHost;
        this.port = nPort;
        this.zone = zone;
        this.resource = resource;
        this.username = username;
        this.password = password;
        
        LogManager.debug("Starting session with " + host +":" + port +":" + username +":" + password +":" + zone);
    }
    
    /* (non-Javadoc)
     * @see org.globus.ftp.FTPClient#abort()
     */
    @Override
    public void abort() throws IOException, ServerException {
    	close();
    }

    public void setType(int type) throws IOException, ServerException {}
    public HostPort setPassive() throws IOException, ServerException {return null;}
    public void setActive(HostPort hostPort){}
    public void setActive(){}
    public void setLocalActive() throws ClientException, IOException {}
    public HostPort setLocalPassive() throws IOException {return null;}
    public HostPort setLocalPassive(int port, int queue) throws IOException {return null;}
    
    public void authorize(String uname, String pass) throws IOException, ServerException {
    	authenticate(uname, pass);
    }
    
    public void authenticate(GlobusCredential cred) throws IOException, ServerException {
    	authenticate(this.username, this.password);
    }
    
    private void authenticate(GSSCredential gssCred) throws IOException, ServerException {
    	authenticate(this.username, this.password);
    }

    public void authenticate(String token) throws IOException, ServerException {
    	authenticate(this.username, token);
    }
    
    public void authenticate() throws IOException, ServerException {
    	authenticate(this.username, this.password);
    }
    
    public void authenticate(String username, String password) throws IOException, ServerException {
    	try
		{
    		irodsConnectionManager = IRODSSimpleProtocolManager.instance();
    		irodsSession = new IRODSSession(irodsConnectionManager);
    		irodsAccount = getAccount();
    		accessObjectFactory = IRODSAccessObjectFactoryImpl.instance(irodsSession);
    		
    		currentDirectory = irodsAccount.getHomeDirectory();
    		if (StringUtils.isEmpty(currentDirectory)) {
    			currentDirectory = "/" + zone + "/home/" + username;
    		}
		}
    	catch (Exception e) {
			throw new IOException("Failed to connect to IRODS at " + this.host, e);
		}
    }
    
    /* 
     * Close the connection to s3. Since HTTPUrlConnection are used, this is 
     * empty.
     */
    @Override
    public void close() throws IOException, ServerException {
    	try {
			accessObjectFactory.closeSessionAndEatExceptions();
		} catch (Exception e) {}
    }
    
    /* 
     * Delete the given directory. This is just the directory name.
     * @param name directory to delete
     */
   public void deleteDir(String name) throws IOException, ServerException 
    {
    	if (StringUtils.isEmpty(name)) {
            throw new IllegalArgumentException("Required argument missing");
        }
    	
        IRODSFile file = null;
		try {
			file = getFile(currentDirectory + "/" + name);
			
			if (file.exists()) {
				file.delete();
			} else {
				throw new ServerException(0,"No directory to delete found at " + name);
			}
		} catch (Exception e) {
			throw new ServerException(0,"Failed to delete directory: " + e.getMessage());
		} finally {
			try { file.close(); } catch (Exception e) {}
		}
    }

    /* 
     * Delete the given file. This is just the file name.
     * @param name file to delete
     */
    @Override
    public void deleteFile(String name) throws IOException, ServerException {
        if (StringUtils.isEmpty(name)) {
            throw new IllegalArgumentException("Required argument missing");
        }
        IRODSFile file = null;
		try {
			file = getFile(currentDirectory + "/" + name);
			
			if (file.exists()) {
				file.delete();
			} else {
				throw new ServerException(0,"No file to delete found at " + name);
			}
		} catch (Exception e) {
			throw new ServerException(0,"Failed to delete file: " + e.getMessage());
		} finally {
			try { file.close(); } catch (Exception e) {}
		}
    }

    @SuppressWarnings("deprecation")
	public void copy(String srcFilename, String destFilename, boolean recursive) throws JargonException {
    	LogManager.debug("from: " + srcFilename + " to: " + destFilename);
        
        if (srcFilename == null) {
            throw new IllegalArgumentException("Required argument 'source' missing");
        }
        
        if (srcFilename == null) {
            throw new IllegalArgumentException("Required argument 'dest' missing");
        }
        
        if (destFilename.equals(srcFilename)) {
            return;
        }
        
        DataTransferOperations dataTransferOperations = accessObjectFactory.getDataTransferOperations(irodsAccount);
        
		dataTransferOperations.copy(srcFilename, resource, destFilename, null, true, null);
	}

    /* (non-Javadoc)
     * @see org.globus.ftp.FTPClient#get(java.lang.String, java.io.File)
     */
    @Override
    public void get(String irodsSourcePath, File targetLocalFile) throws IOException,
            ServerException {
        
        if (irodsSourcePath == null) {
            throw new IllegalArgumentException("No remote path specified");
        }
        
        if (targetLocalFile == null) {
            throw new IllegalArgumentException("No local file specified");
        }
        
        if (!targetLocalFile.exists()) {
            if (!targetLocalFile.createNewFile()) {
                throw new IOException("Failed to create local file " + targetLocalFile.getAbsolutePath());
            }
        }
        try 
        {
	        IRODSFile irodsSourceFile = getFile(irodsSourcePath);
	        
	        accessObjectFactory.getDataTransferOperations(irodsAccount)
	        	.getOperation(irodsSourceFile, targetLocalFile, null, null);
        } catch (Exception e) {
        	throw new ServerException(0, "Failed to retrieve remote file: " + e.getMessage());
        } 
    }

    /*
     * The currently viewed directory.
     * 
     * @return current bucket
     */
    @Override
    public String getCurrentDir() throws IOException, ServerException {
        return currentDirectory;
    }

    /* 
     * Static host of SRB.
     */
    @Override
    public String getHost() {
        return this.host;
    }

    /* 
     * Gets the user's username
     */
    @Override
    public String getUserName() {
        
        return this.username;
        
    }

    /* (non-Javadoc)
     * @see org.globus.ftp.FTPClient#goUpDir()
     */
    @Override
    public void goUpDir() throws IOException, ServerException {
    	IRODSFile file = getFile(currentDirectory);
    	if (file.exists() && file.isDirectory()) {
    		IRODSFile parent = getFile(file.getParent());
    		if (file.exists()) {
    			currentDirectory = file.getParent();
    		} else {
    			throw new ServerException(ServerException.SERVER_REFUSED, "Failed to change directories");
    		}
    	}
    }
    
    /* 
     * s3 does not support directories, so the changedir command essentially
     * just switches buckets
     * @param dir directory to which to change
     */
    @Override
    public void changeDir(String dir) throws IOException, ServerException {
        
    	if (StringUtils.isEmpty(dir)) {
            currentDirectory = homeDirectory;
        } else {
        	if (dir.contains("..")) {
        		String[] tokens = dir.split("/");
        		List<String> pathElements = new ArrayList<String>();
        		for(int i=tokens.length-1; i >= 0; i--) {
        			if (tokens[i].equals("..")) {
        				i--;
        			} else {
        				pathElements.add(tokens[i]);
        			}
        		}
        		Collections.reverse(pathElements);
        		dir = ServletUtil.explode("/", pathElements);
        	}
        	IRODSFile file = getFile(dir);
        	if (file.exists() && file.isDirectory()) {
        		currentDirectory = dir;
        	} else {
        		throw new ServerException(ServerException.SERVER_REFUSED, "Invalid path");
        	}
        }
    }

    /* (non-Javadoc)
     * @see org.globus.ftp.FTPClient#list()
     */
    @Override
    public Vector<FileInfo> list() throws ServerException, ClientException, IOException {
        
        return list(currentDirectory);
    }

    /* (non-Javadoc)
     * @see org.globus.ftp.FTPClient#list(java.lang.String)
     */
    @Override
   public Vector<FileInfo> list(String path) throws IOException, ServerException {
		
		// list irods folder
    	try 
    	{
			CollectionAndDataObjectListAndSearchAO util = accessObjectFactory.getCollectionAndDataObjectListAndSearchAO(irodsAccount);
			if (path.endsWith("/")) {
				path = path.substring(0,path.length()-1);
			}
			
			Vector<FileInfo> v = new Vector<FileInfo>();
			
			for (CollectionAndDataObjectListingEntry entry: util.listDataObjectsAndCollectionsUnderPath(currentDirectory) ) {
				v.add(FileInfoFactory.getIRODSFileInfo(entry, username));
			}
			
			return v;
    	} catch (Exception e) {
    		throw new ServerException(0, e.getMessage());
    	}
	}

    /* (non-Javadoc)
     * @see org.globus.ftp.FTPClient#makeDir(java.lang.String)
     */
    @Override
    public void makeDir(String dir) throws IOException, ServerException {
        try {
            
        	IRODSFile dest = getFile(currentDirectory + "/" + dir);
			
			if (dest.exists()) {
				if (dest.isDirectory()) {
					throw new ServerException(0, "Directory already exists");
				} else {
					throw new ServerException(ServerException.SERVER_REFUSED, "File already exists");
				}
			} else {
				if (!dest.mkdir())
					throw new ServerException(ServerException.SERVER_REFUSED);
			}
        } catch (Exception e) {
            throw new ServerException(0,"Failed to create the directory: " + e.getMessage());
        }
    }

    /* (non-Javadoc)
     * @see org.globus.ftp.FTPClient#put(java.io.File, java.lang.String, boolean)
     */
    @Override
    public void put(File src, String remotePath, boolean append) throws IOException, ServerException {
        try 
        {
	    	IRODSFile dest = getFile(remotePath);        
	        accessObjectFactory.getDataTransferOperations(irodsAccount).putOperation(src, dest, null, null);
        } catch (JargonException e) {
        	throw new ServerException(0, e.getMessage());
        }
    }
    
    /** 
     * Sets the permissions on the remote file
     */
    @Override
    public Reply site(String command) throws IOException, ServerException {
    	String[] args = command.split(" ");
    	String uname = args[0];
    	FilePermissionEnum pemValue = FilePermissionEnum.NONE;
    	int i=0;
    	for(FilePermissionEnum pem: FilePermissionEnum.values()) {
    		if (args[1].equals(String.valueOf(i))) {
    			pemValue = pem;
    			break;
    		}
    		i++;
    	}
    	
    	String path = "";
    	for (i=2;i<args.length - 1;i++) {
    		path += args[i] + " ";
    	}
    	path = path.trim();
    	
    	try 
    	{	
    		setPermission(uname, path, pemValue, false);
	    	
    		return null;
	    } 
    	catch (JargonException e) {
    		throw new ServerException(0, e.getMessage());
    	}
    }


    /* (non-Javadoc)
     * @see org.globus.ftp.FTPClient#rename(java.lang.String, java.lang.String)
     */
    @Override
    public void rename(String oldPath, String newPath) throws IOException,ServerException {
        
        IRODSFile src = getFile(oldPath);
        IRODSFile dest = getFile(newPath);
		
		if (dest.exists()) {
			throw new IOException("Rename failed: File or folder of that name already exists");
		}
		
		if (!src.renameTo(dest)) {
			throw new IOException("Rename failed");
		}
    }

    /* 
     * Get the size of the named file.  Defaults to getSize(filename)
     * @param name of the file to get the size of
     */
    @Override
    public long size(String filename) throws IOException, ServerException {
        try {
        	IRODSFile file = getFile(filename);
        	if (file.exists()) {
        		return file.length();
        	} else {
        		throw new FileNotFoundException("File does not exist");
        	}
        } catch (FileNotFoundException e) {
        	throw e;
        } catch (Exception e) {
        	throw new ServerException(0, "Failed to retrieve file size: " + e.getMessage()); 
        }
    }    
    
    public IRODSFile getFile(String path) throws ServerException {
		try {
			IRODSFileFactory irodsFileFactory = accessObjectFactory.getIRODSFileFactory(irodsAccount);
			return irodsFileFactory.instanceIRODSFile(path);
		} catch (JargonException e) {
			throw new ServerException(0, e.getMessage());
		}
	}
	
	public List<UserFilePermission> getAllPermissions(String path) throws IOException {
		List<UserFilePermission> pems = null;
		
		try {
			IRODSFile file = accessObjectFactory.getIRODSFileFactory(irodsAccount).instanceIRODSFile(path);
			
			// UPDATE jargon-core so you have the list method!!
			if (file.isFile()) {
				pems = accessObjectFactory.getDataObjectAO(irodsAccount).listPermissionsForDataObject(file.getAbsolutePath());
			} else {
				pems = accessObjectFactory.getCollectionAO(irodsAccount).listPermissionsForCollection(file.getAbsolutePath());
			}
		} catch (JargonException e) {
			throw new IOException("Unable to retrieve permissions.", e);
		}
		
		return pems;
	}
	
	public FilePermissionEnum getPermissionForUser(String username, String path) throws JargonException {
		
		IRODSFile file = accessObjectFactory.getIRODSFileFactory(irodsAccount).instanceIRODSFile(path);
		
		// need to check parent for permissions due to strict permissions.
		UserFilePermission pem = null;
		if (!file.exists()) {
			// check the parent folder to get the parent pem
			pem = accessObjectFactory.getCollectionAO(irodsAccount).getPermissionForUserName(file.getParent(), username);
		} else if (file.isFile()) {
			pem = accessObjectFactory.getDataObjectAO(irodsAccount).getPermissionForDataObjectForUserName(file.getAbsolutePath(), username);
			if (pem == null || pem.getFilePermissionEnum().equals(FilePermissionEnum.NONE)) {
				pem = accessObjectFactory.getDataObjectAO(irodsAccount).getPermissionForDataObjectForUserName(file.getParent(), username);
			}
		} else if (file.isDirectory()) {
			pem = accessObjectFactory.getCollectionAO(irodsAccount).getPermissionForUserName(file.getAbsolutePath(), username);
		}
		
		return pem == null ? FilePermissionEnum.NONE : pem.getFilePermissionEnum();
	}
	
	public void setPermission(String username, String path, FilePermissionEnum pem, boolean recursive) throws JargonException 
	{
		IRODSFile file = accessObjectFactory.getIRODSFileFactory(irodsAccount).instanceIRODSFile(path);
		
		if (pem.equals(FilePermissionEnum.NULL)) {
			clearPermissions(username, path, recursive);
		} else if (pem.equals(FilePermissionEnum.WRITE)) {
			setWritePermission(username, path, recursive);
		} else if (pem.equals(FilePermissionEnum.READ)) {
			setReadPermission(username, path, recursive);
		} else if (pem.equals(FilePermissionEnum.OWN)) {
			setOwnerPermission(username, path, recursive);
		} else {
			throw new JargonException("Invalid permission value " + pem.name());
		}
	}
	
	public boolean hasReadPermission(String path, String username) throws JargonException {
		
		FilePermissionEnum userPem = getPermissionForUser(username, path);
		
		return (userPem.equals(FilePermissionEnum.READ) || userPem.equals(FilePermissionEnum.OWN));
	}
	
	public boolean hasWritePermission(String path, String username) throws JargonException {
		
		FilePermissionEnum userPem = getPermissionForUser(username, path);
		
		return (userPem.equals(FilePermissionEnum.WRITE) || userPem.equals(FilePermissionEnum.OWN));
	}
	
	public void setOwnerPermission(String username, String path, boolean recursive) throws JargonException {
		
		IRODSFile file = accessObjectFactory.getIRODSFileFactory(irodsAccount).instanceIRODSFile(path);
		
		if (file.isFile()) {
			accessObjectFactory.getDataObjectAO(irodsAccount).setAccessPermissionOwn(zone, file.getAbsolutePath(), username);
		} else {
			accessObjectFactory.getCollectionAO(irodsAccount).setAccessPermissionOwn(zone, file.getAbsolutePath(), username, recursive);
		}
	}

	public void setReadPermission(String username, String path, boolean recursive) throws JargonException {
		
		IRODSFile file = accessObjectFactory.getIRODSFileFactory(irodsAccount).instanceIRODSFile(path);
		
		if (file.isFile()) {
			accessObjectFactory.getDataObjectAO(irodsAccount).setAccessPermissionRead(zone, file.getAbsolutePath(), username);
		} else {
			accessObjectFactory.getCollectionAO(irodsAccount).setAccessPermissionRead(zone, file.getAbsolutePath(), username, recursive);
		}
	}
	
	public void removeReadPermission(String username, String path, boolean recursive) throws JargonException {
		
		IRODSFile file = accessObjectFactory.getIRODSFileFactory(irodsAccount).instanceIRODSFile(path);
		
		if (file.isFile()) {
			accessObjectFactory.getDataObjectAO(irodsAccount).removeAccessPermissionsForUser(zone, file.getAbsolutePath(), username);
		} else {
			accessObjectFactory.getCollectionAO(irodsAccount).removeAccessPermissionForUser(zone, file.getAbsolutePath(), username, recursive);
		}
	}
	
	public void setWritePermission(String username, String path, boolean recursive) throws JargonException {
		
		IRODSFile file = accessObjectFactory.getIRODSFileFactory(irodsAccount).instanceIRODSFile(path);
		
		if (file.isFile()) {
			accessObjectFactory.getDataObjectAO(irodsAccount).setAccessPermissionWrite(zone, file.getAbsolutePath(), username);
		} else {
			accessObjectFactory.getCollectionAO(irodsAccount).setAccessPermissionWrite(zone, file.getAbsolutePath(), username, recursive);
		}
	}
	
	public void removeWritePermission(String username, String path, boolean recursive) throws JargonException {
		removeReadPermission(username, path, recursive);
	}
	
	public void clearPermissions(String username, String path, boolean recursive) throws JargonException {
		
		IRODSFile file = accessObjectFactory.getIRODSFileFactory(irodsAccount).instanceIRODSFile(path);
		
		List<UserFilePermission> pems = null;
		if (file.isFile()) {
			DataObjectAO dao = accessObjectFactory.getDataObjectAO(irodsAccount);
			pems = dao.listPermissionsForDataObject(file.getAbsolutePath());
			for (UserFilePermission pem: pems) {
				dao.removeAccessPermissionsForUser(zone, file.getAbsolutePath(), pem.getUserName());
			}
		} else {
			CollectionAO cao = accessObjectFactory.getCollectionAO(irodsAccount); 
			pems = cao.listPermissionsForCollection(file.getAbsolutePath());
			for (UserFilePermission pem: pems) {
				cao.removeAccessPermissionForUser(zone, file.getAbsolutePath(), pem.getUserName(), recursive);
			}
		}
	}
	
	@Override
	public boolean exists(String path) throws ServerException {
		
		IRODSFile file = null;
		try {
			file = getFile(path);
			if (file != null) {
				return file.exists();
			} else {
				return false;
			}
		} catch (Exception e) {
			throw new ServerException(0,e.getMessage());
		} finally {
			try { file.close(); } catch (Exception e) {}
		}
	}
	
	public InputStream getInputStream(String path) throws IOException {
		try 
		{
			IRODSFileFactory irodsFileFactory = accessObjectFactory.getIRODSFileFactory(irodsAccount);
			IRODSFile irodsFile = irodsFileFactory.instanceIRODSFile(path);
			InputStream in = irodsFileFactory.instanceIRODSFileInputStream(irodsFile);
			return in;
		} catch (Exception e) {
			throw new IOException(e);
		}
	}
	
	public OutputStream getOutputStream(String path) throws IOException {
		try
		{
			IRODSFileFactory irodsFileFactory = accessObjectFactory.getIRODSFileFactory(irodsAccount);
			IRODSFile irodsFile = irodsFileFactory.instanceIRODSFile(path);
			//OutputStream out = new BufferedOutputStream(irodsFileFactory.instanceIRODSFileOutputStream(irodsFile));
			OutputStream out = irodsFileFactory.instanceIRODSFileOutputStream(irodsFile);
			
			return out;
		} catch (Exception e) {
			throw new IOException(e);
		}
	}
	
	public void deleteMetadata(String path, AvuData data) throws IOException {
		try {
			IRODSFileFactory irodsFileFactory = accessObjectFactory.getIRODSFileFactory(irodsAccount);
			IRODSFile irodsFile = irodsFileFactory.instanceIRODSFile(path);
			if (irodsFile.isDirectory()) {
				accessObjectFactory.getCollectionAO(irodsAccount).deleteAVUMetadata(irodsFile.getAbsolutePath(), data);
			} else {
				accessObjectFactory.getDataObjectAO(irodsAccount).deleteAVUMetadata(irodsFile.getAbsolutePath(), data);
			}
		} catch (Exception e) {
			throw new IOException("Failed to add the metadata term to " + path);
		}
	}
	
	public void addMetadata(String path, AvuData data) throws IOException {
		try {
			IRODSFileFactory irodsFileFactory = accessObjectFactory.getIRODSFileFactory(irodsAccount);
			IRODSFile irodsFile = irodsFileFactory.instanceIRODSFile(path);
			if (irodsFile.isDirectory()) {
				accessObjectFactory.getCollectionAO(irodsAccount).addAVUMetadata(irodsFile.getAbsolutePath(), data);				
			} else {
				accessObjectFactory.getDataObjectAO(irodsAccount).addAVUMetadata(irodsFile.getAbsolutePath(), data);
			}
		} catch (Exception e) {
			throw new IOException("Failed to add the metadata term to " + path);
		}
	}
	
	public void updateMetadata(String path, AvuData oldValue, AvuData newValue) throws IOException {
		try {
			IRODSFileFactory irodsFileFactory = accessObjectFactory.getIRODSFileFactory(irodsAccount);
			IRODSFile irodsFile = irodsFileFactory.instanceIRODSFile(path);
			if (irodsFile.isDirectory()) {
				accessObjectFactory.getCollectionAO(irodsAccount).modifyAVUMetadata(irodsFile.getAbsolutePath(), oldValue, newValue);
			} else {
				accessObjectFactory.getDataObjectAO(irodsAccount).modifyAVUMetadata(irodsFile.getAbsolutePath(), oldValue, newValue);
			}
		} catch (Exception e) {
			throw new IOException("Failed to add the metadata term to " + path);
		}
	}
	
	public List<MetaDataAndDomainData> getAllPathMetadata(String path) throws IOException {
		try {
			IRODSFileFactory irodsFileFactory = accessObjectFactory.getIRODSFileFactory(irodsAccount);
			IRODSFile irodsFile = irodsFileFactory.instanceIRODSFile(path);
			List<MetaDataAndDomainData> metadata = null;
			if (irodsFile.isDirectory()) {
				metadata = accessObjectFactory.getCollectionAO(irodsAccount).findMetadataValuesForCollection(irodsFile.getAbsolutePath());
			} else {
				metadata = accessObjectFactory.getDataObjectAO(irodsAccount).findMetadataValuesForDataObject(irodsFile);
			}
			return metadata;
			
		} catch (Exception e) {
			throw new IOException("Failed to add the metadata term to " + path);
		}
	}
	
	public List<IRODSQueryResultRow> query(IRODSGenQuery irodsQuery, int offset) throws IOException {
		try {
			IRODSQueryResultSetInterface resultSet = null;
			
			resultSet = accessObjectFactory.getIRODSGenQueryExecutor(irodsAccount).executeIRODSQueryWithPaging(irodsQuery, offset);
			
			return resultSet.getResults();
			
		} catch (Exception e) {
			throw new IOException("Failed to add the metadata term to " + irodsQuery.toString());
		}
	}
	
	public void write(InputStream in, String path) throws JargonException, IOException {
		
		IRODSFileFactory irodsFileFactory = accessObjectFactory.getIRODSFileFactory(irodsAccount);
		IRODSFile file = irodsFileFactory.instanceIRODSFile(path);
		
		if (!file.getParentFile().exists())
			file.getParentFile().mkdirs();
		
		// this is stupid. why can't I create an outputstream by passing in just the IRODSFile?
		if (!file.exists())
			file.createNewFile();
		
		OutputStream out = new BufferedOutputStream(irodsFileFactory.instanceIRODSFileOutputStream(file));
		
		byte[] tmp = new byte[65536];
		int len;
		try {
			while ((len = in.read(tmp)) != -1) {
				out.write(tmp, 0, len);
			}
			out.flush();
			file.close();
		} finally {
			try { out.close(); } catch (Exception e) {}
			try { in.close(); } catch (Exception e) {}
		}
	}
	
	private IRODSAccount getAccount() 
	{
		try
		{
			if (StringUtils.isEmpty(this.password) && StringUtils.isEmpty(this.username)) 
			{
				return GSIIRODSAccount.instance(this.host, this.port, AppMain.defaultCredential, this.resource);
			} 
			else 
			{
				return new IRODSAccount(this.host,
						this.port, this.username,
						this.password, "",this.zone, this.resource);
			}
		}
		catch (JargonException e)
		{
			LogManager.error("Failed to initialize the IRODS account", e);
			return null;
		}
	}

	public List<User> findUsers(String searchString) throws IOException
	{
		List<User> users = new ArrayList<User>();
		
		try 
		{
			UserAO userAO = accessObjectFactory.getUserAO(irodsAccount);
			List<org.irods.jargon.core.pub.domain.User> irodsUsers = null;
			UserGroupAO usergroupAO = accessObjectFactory.getUserGroupAO(irodsAccount);
			
			if (StringUtils.isEmpty(searchString))
			{
				irodsUsers = userAO.findAll();
			}
			else
			{
				final StringBuilder sb = new StringBuilder();
				sb.append(RodsGenQueryEnum.COL_USER_NAME.getName());
				sb.append(" LIKE ");
				sb.append(" '%" + searchString + "%'");
				
				irodsUsers = userAO.findWhere(sb.toString());
			}
			
			for(org.irods.jargon.core.pub.domain.User irodsUser : irodsUsers)
			{
				User user = new User();
				List<AvuData> userMeta = userAO.listUserMetadataForUserId(irodsUser.getId());
				String[] tokens = irodsUser.getName().split(" ");
				
				user.setFirstName(tokens[0]);
				if (tokens.length > 1) {
					user.setLastName(irodsUser.getName().replaceFirst(tokens[0], "").trim());
				}
				
				user.setUsername(irodsUser.getNameWithZone().split("#")[0]);
				
				users.add(user);
			}
			
			return users;
		}
		catch (Exception e) {
			throw new IOException("Failed to query for users matching \"" + searchString + "\"", e);
		}
	}
	
	
//	public String resolveFileName(String name) throws JargonException {
//		
//		String newName = name.trim();
//		
//		for (int i=0; exists(newName); i++) {
//			String basename = FilenameUtils.getBaseName(name);
//			String extension = FilenameUtils.getExtension(name);
//			newName = basename + "-" + i;
//			if (StringUtils.isEmpty(extension)) {
//				newName = newName + "." + extension;
//			}
//		}
//		
//		return newName;
//	}
 
}
