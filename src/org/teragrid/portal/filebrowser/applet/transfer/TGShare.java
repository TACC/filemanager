/* 
 * Created on Jul 23, 2008
 * 
 * Developed by: Rion Dooley - dooley [at] tacc [dot] utexas [dot] edu
 * 				 TACC, Texas Advanced Computing Center
 * 
 * https://www.tacc.utexas.edu/
 */

package org.teragrid.portal.filebrowser.applet.transfer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.GeneralSecurityException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;

import org.apache.axis.utils.StringUtils;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.MultipartPostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.FilePartSource;
import org.globus.ftp.FTPClient;
import org.globus.ftp.FileInfo;
import org.globus.ftp.HostPort;
import org.globus.ftp.exception.ClientException;
import org.globus.ftp.exception.ServerException;
import org.globus.ftp.vanilla.Reply;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.Client;
import org.restlet.data.ChallengeResponse;
import org.restlet.data.ChallengeScheme;
import org.restlet.data.Form;
import org.restlet.data.Method;
import org.restlet.data.Protocol;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.teragrid.portal.filebrowser.applet.ConfigOperation;
import org.teragrid.portal.filebrowser.applet.ConfigSettings;
import org.teragrid.portal.filebrowser.applet.file.TGShareFileInfo;
import org.teragrid.portal.filebrowser.applet.util.LogManager;
import org.teragrid.portal.filebrowser.server.servlet.exception.PermissionException;

/**
 * Adaptor class to interface with XSEDE $SHARE
 * 
 * @author Rion Dooley < dooley [at] tacc [dot] utexas [dot] edu >
 *
 */
@SuppressWarnings({"unused", "deprecation"})
public class TGShare extends FTPClient {
    
    private String password;
    private String username;
    
    private String pwd = "";
    
 // Create an anonymous class to trust all certificates.
    // This is bad style, you should create a separate class.
    private X509TrustManager xtm = new X509TrustManager() {
    	
    	public void checkClientTrusted(X509Certificate[] chain, String authType) {}
    	
    	public void checkServerTrusted(X509Certificate[] chain, String authType) {
    	
//    		System.out.println("cert: " + chain[0].toString() + ", authType: " + authType);
    	}
	    
    	public X509Certificate[] getAcceptedIssuers() {
    		return null;
    	}
    };
	
    // Create an class to trust all hosts
	private HostnameVerifier hnv = new HostnameVerifier() {
	
		public boolean verify(String hostname,SSLSession session) {
//	    	System.out.println("hostname: " + hostname);
	    	return true;
		}
	};
	
    /**
     * @param username
     * @param username
     * @throws ServerException 
     */
    public TGShare(String username, String password) {
    	
    	this.username = username;
    	this.password = password;
    	
    	// Initialize the TLS SSLContext with
	    // our TrustManager
		SSLContext sslContext = null;
		try {
			sslContext = SSLContext.getInstance("TLS");
			X509TrustManager[] xtmArray = new X509TrustManager[] { xtm };
			sslContext.init( null, xtmArray, new java.security.SecureRandom() );
		} catch( GeneralSecurityException gse ) {
			// Print out some error message and deal with this exception
		}
	
		// Set the default SocketFactory and HostnameVerifier
		// for javax.net.ssl.HttpsURLConnection
		if( sslContext != null ) {
			HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory() );
		}
		
		HttpsURLConnection.setDefaultHostnameVerifier( hnv );
    }
    
    /**
     * Cheater method to use the share service to do a remote internal copy rather than 
     * doing a gridftp 3rd party copy or streaming through the client.
     * 
     * @param srcPath
     * @param destPath
     * @throws IOException
     * @throws ServerException
     */
    public void copy(String srcPath, String destPath) throws IOException, ServerException {
            LogManager.debug("from: " + srcPath + " to: " + destPath);
            
            if (destPath.equals(srcPath)) {
                return;
            }
            
            try {
            	Request request = new Request(Method.PUT, ConfigSettings.SERVICE_TGSHARE_SERVICE + "/home/" + encodePath(srcPath));
    			
    			ChallengeScheme scheme = ChallengeScheme.HTTP_BASIC;
    	    	ChallengeResponse authentication = new ChallengeResponse(scheme,  
    	    			username, password);  
    	    	
    	    	request.setChallengeResponse(authentication); 

    	    	Form form = new Form();
    	    	form.add("dest_path", destPath);
    	    	request.setEntity(form.getWebRepresentation());
    	    	
    	    	Client rc = new Client(Protocol.HTTP);
    	    	Response response = rc.handle(request);
    	    	
    	    	if (!response.getStatus().isSuccess()) {
    	    		handleError(response.getStatus());
    	    	}
    	    	
			} catch (Exception e) {
				throw new ServerException(0,"Failed to copy file to " + destPath);
			}
            
    }
    
    /** 
     * Override the gridftp rename method to use the share service rename ability so we
     * can keep track of shared permissions as the name changes.
	 */
	public void rename(String oldName, String newName) throws IOException, ServerException {
		 LogManager.debug("rename from: " + oldName + " to: " + newName);
         
         if (oldName.equals(newName)) {
             return;
         }
         
         try {
         	Request request = new Request(Method.PUT, ConfigSettings.SERVICE_TGSHARE_SERVICE + "/home" +
         		encodePath(pwd + "/" + oldName));
         	
 			ChallengeScheme scheme = ChallengeScheme.HTTP_BASIC;
 	    	ChallengeResponse authentication = new ChallengeResponse(scheme,  
 	    			username, password);  
 	    	
 	    	request.setChallengeResponse(authentication); 

 	    	Form form = new Form();
 	    	form.add("new_name", newName);
 	    	request.setEntity(form.getWebRepresentation());
 	    	
 	    	Client rc = new Client(Protocol.HTTP);
 	    	Response response = rc.handle(request);
	    	
	    	if (!response.getStatus().isSuccess()) {
	    		handleError(response.getStatus());
	    	}
 	    	
		} catch (Exception e) {
			throw new ServerException(0,"Failed to rename file to " + newName + "\n" + e.getMessage());
		}
	}

	/** 
	 * Override the gridftp list(String) method to use the share service so we get share permissions
	 * and ownership info in the TGShareFileInfo object.
	 */
	public Vector<FileInfo> list(String sFilter) throws IOException, ServerException, ClientException {
		//System.out.println(ConfigSettings.SERVICE_TGSHARE_SERVICE + "/home" +  encodePath(getPath(pwd)));
    	Request request = new Request(Method.GET, ConfigSettings.SERVICE_TGSHARE_SERVICE + "/home" +  encodePath(getPath(pwd)));
    	
    	ChallengeScheme scheme = ChallengeScheme.HTTP_BASIC;
    	ChallengeResponse authentication = new ChallengeResponse(scheme,  
    			username, password);  
    	
    	request.setChallengeResponse(authentication); 
    	
    	Client rc = new Client(Protocol.HTTP);
    	
    	Response response = rc.handle(request);
    	
    	if (!response.getStatus().equals(Status.SUCCESS_OK)) {
    		handleError(response.getStatus()); 
    		System.out.println("ok");
    	}
    	
    	String jText = response.getEntity().getText();
    	
    	Vector<FileInfo> v = new Vector<FileInfo>();
    	
    	try {

	    	JSONArray jArray = new JSONArray(jText);
	    	
	    	for (int i = 0; i<jArray.length(); i++) {
	    		JSONObject jFile = jArray.getJSONObject(i);
	    		
				FileInfo f = new TGShareFileInfo(jFile);
				v.add(f);
	    	}
    	} catch (JSONException e) {
    		e.printStackTrace();
    		throw new IOException("Failed to parse JSON file representation");
    	}
		return v;
    }
    
    /** 
     * Changes the current working directory.
	 */
	@Override
	public void changeDir(String sDir) throws IOException, ServerException {
		String oldPath = pwd;
		pwd = getPath(sDir);
		try {
			list("*");
		} catch (Exception e) {
			pwd = oldPath;
			throw new ServerException(ServerException.SERVER_REFUSED,e.getMessage());
		}
	}

	/** 
     * Deletes the specified directory. Same as deleteFile(String)
	 */
	@Override
	public void deleteDir(String sDir) throws IOException, ServerException {
		deleteFile(sDir);
	}
	
	

	/** 
	 * Pulls file from share service using http GET and writes to the file specified.
	 * 
	 * @param sRemoteFile name of remote file
	 * @param sLocalFile local File object to write the downloaded file to
	 */
	public void get(String sRemoteFile, OutputStream out) throws IOException,
			ServerException, ClientException {
		Request request = new Request(Method.GET, ConfigSettings.SERVICE_TGSHARE_SERVICE + "/home/" + 
				encodePath(getPath(pwd + "/" + sRemoteFile)));
    	
    	ChallengeScheme scheme = ChallengeScheme.HTTP_BASIC;
    	ChallengeResponse authentication = new ChallengeResponse(scheme,  
    			username, password);  
    	
    	request.setChallengeResponse(authentication); 
    	
    	Client rc = new Client(Protocol.HTTP);
    	Response response = rc.handle(request);
    	
    	if (response.getStatus().isSuccess()) {
    		response.getEntity().write(out);
    		out.close();
    	} else {
    		throw new ServerException(ServerException.SERVER_REFUSED, "Download failed: " + response.getStatus().getDescription());
    	}  	
	}

	/**
	 * Opens an input stream to a remote file on the share service.
	 * 
	 * @param file
	 * @return
	 * @throws ServerException
	 * @throws IOException
	 */
	public InputStream getInputStream(String file) throws ServerException, IOException {
		Request request = new Request(Method.GET, ConfigSettings.SERVICE_TGSHARE_SERVICE + "/home/" + 
				encodePath(getPath(file)));
    	
    	ChallengeScheme scheme = ChallengeScheme.HTTP_BASIC;
    	ChallengeResponse authentication = new ChallengeResponse(scheme,  
    			username, password);  
    	
    	request.setChallengeResponse(authentication); 
    	
    	Client rc = new Client(Protocol.HTTP);
    	Response response = rc.handle(request);
    	
    	if (response.getStatus().isSuccess()) {
    		return response.getEntity().getStream();
    	} else {
    		throw new ServerException(ServerException.SERVER_REFUSED, "Failed to get input stream: " + response.getStatus().getDescription());
    	}
    	
	}
	
	/** 
	 * Posts a file to the share service.
	 */
	public void put(File srcFile, final String remoteFileName, boolean append)
			throws IOException, ServerException, ClientException {

		MultipartPostMethod post = new MultipartPostMethod(ConfigSettings.SERVICE_TGSHARE_SERVICE + "/home" + 
				encodePath(remoteFileName));
		
//		MultipartPostMethod post = new MultipartPostMethod("http://dooley-mac.tacc.utexas.edu:8080/ShareService/home/Desktop/deleteme.txt");
		
		LogManager.debug("POST sent to " + ConfigSettings.SERVICE_TGSHARE_SERVICE + "/home" + 
				encodePath(remoteFileName));
//		LogManager.debug("POST sent to " + "http://dooley-mac.tacc.utexas.edu:8080/ShareService/home/Desktop/deleteme.txt");
        try {
        	LogManager.debug("POST file is " + srcFile.getAbsolutePath());

        	post.addPart(new FilePart(srcFile.getName(), new FilePartSource(srcFile)));
        	post.setDoAuthentication(true);
	     
        	URL url = new URL(ConfigSettings.SERVICE_TGSHARE_SERVICE);
	        HttpClient client = new HttpClient();
	        client.getState().setCredentials(
	        		new AuthScope(url.getHost(), url.getPort()),
	        		new UsernamePasswordCredentials(username, password));
	        int status = client.executeMethod(post);
	        
	        if (HttpStatus.SC_OK == status) {
	        	String response = post.getResponseBodyAsString();
	            //System.out.println(response);
	        } else {
	        	String response = post.getResponseBodyAsString();
	            //System.out.println(response);
	        	throw new ServerException(ServerException.SERVER_REFUSED, "Upload failed");
	        }
        } catch (Exception e) {
        	throw new ServerException(ServerException.SERVER_REFUSED, "Upload failed: " + e.getMessage());
        } finally {
        	post.releaseConnection();
        }
	}

	/**
	 * Opens an ouput stream to write to a file on the share service.
	 * 
	 * @param file
	 * @param length
	 * @return
	 */
	public HttpURLConnection getOutputStream(String file, long length) {
//		URL url = new URL("http://www.domain.com/webems/upload.do");
//		// create a boundary string
//		String boundary = MultiPartFormOutputStream.createBoundary();
//		URLConnection urlConn = MultiPartFormOutputStream.createConnection(url);
//		urlConn.setRequestProperty("Accept", "*/*");
//		urlConn.setRequestProperty("Content-Type", 
//			MultiPartFormOutputStream.getContentType(boundary));
//		// set some other request headers...
//		urlConn.setRequestProperty("Connection", "Keep-Alive");
//		urlConn.setRequestProperty("Cache-Control", "no-cache");
//		// no need to connect cuz getOutputStream() does it
//		MultiPartFormOutputStream out = 
//			new MultiPartFormOutputStream(urlConn.getOutputStream(), boundary);
//		// write a text field element
//		out.writeField("myText", "text field text");
//		// upload a file
//		out.writeFile("myFile", "text/plain", new File("C:\\test.txt"));
//		// can also write bytes directly
//		//out.writeFile("myFile", "text/plain", "C:\\test.txt", 
////			"This is some file text.".getBytes("ASCII"));
//		out.close();
//		// read response from server
//		BufferedReader in = new BufferedReader(
//			new InputStreamReader(urlConn.getInputStream()));
//		String line = "";
//		while((line = in.readLine()) != null) {
//			 System.out.println(line);
//		}
//		in.close();
		return null;
	}
	
	/** 
     * Deletes the file or folder at the endpoint.
	 */
	@Override
	public void deleteFile(String filename) throws IOException, ServerException {
		String path = getPath(pwd + "/" + filename);
		Request request = new Request(Method.DELETE, ConfigSettings.SERVICE_TGSHARE_SERVICE + "/home" + encodePath(path));
		ChallengeScheme scheme = ChallengeScheme.HTTP_BASIC;
    	ChallengeResponse authentication = new ChallengeResponse(scheme,  
    			username, password);  
    	
    	request.setChallengeResponse(authentication); 
    	
    	Client rc = new Client(Protocol.HTTP);
		Response response = rc.handle(request);
		
		if (!response.getStatus().isSuccess()) {
    		throw new ServerException(ServerException.SERVER_REFUSED, "Failed to delete file: " + response.getStatus().getDescription());
    	}
	}

	/** 
     * Changes the current directory to the parent folder. If the parent
     * folder is the user's root home, it changes to "".
	 */
	@Override
	public void goUpDir() throws IOException, ServerException {
		changeDir("..");
	}
	
	public boolean exists(String remoteFile) throws IOException, ServerException {
		try {
			return getSize(remoteFile) > -1;
		} catch (IOException e) {
			throw e;
		} catch (ServerException e) {
			throw e;
		}
	}

	/** 
     * Creates a new folder with the given name in the current directory.
	 */
	@Override
	public void makeDir(String dir) throws IOException, ServerException {
		
		LogManager.debug("create new folder: " + pwd + "/" + dir);
         
         try {
         	Request request = new Request(Method.PUT, ConfigSettings.SERVICE_TGSHARE_SERVICE + "/home" + encodePath(pwd));
         	
 			ChallengeScheme scheme = ChallengeScheme.HTTP_BASIC;
 	    	ChallengeResponse authentication = new ChallengeResponse(scheme,  
 	    			username, password);  
 	    	
 	    	request.setChallengeResponse(authentication); 

 	    	Form form = new Form();
 	    	form.add("folder_name", dir);
 	    	
 	    	request.setEntity(form.getWebRepresentation());
 	    	
 	    	Client rc = new Client(Protocol.HTTP);
 	    	Response response = rc.handle(request);
 	    	
 	    	if (!response.getStatus().isSuccess()) {
 	    		//response.getEntity().write(System.out);
 	    		throw new ServerException(ServerException.SERVER_REFUSED, "Failed to create directory " + response.getStatus().getDescription());
 	    	} else {
 	    		//response.getEntity().write(System.out);
 	    	}
 	    	rc.stop();
		} catch (Exception e) {
			throw new ServerException(0,"Failed to create new folder " + pwd + "/" + dir);
		}
	}

	/**
     * The last directory listed.
     * 
     * @return current bucket
     */
    @Override
    public String getCurrentDir() throws IOException, ServerException {
    	return pwd;
    }

    /** 
     * Returns the username of the authenticated user.
	 */
    public String getUserName() {
        return this.username;
    }
    
    /**
     * Get the appropriate url for this file depending on whether it's public or private.
     * 
     * @param fileInfo
     * @return
     * @throws Exception
     */
    public String getUrl(TGShareFileInfo fileInfo) throws Exception {
    	
    	if (fileInfo.isOwner()) {
    		File parent = new File(fileInfo.getPath()).getParentFile();
    		if (pwd.equalsIgnoreCase("/public")) {
				return "http://share.teragrid.org/public/" + username + encodePath(fileInfo.getName());
			} else {
				return "https://share.teragrid.org:28443/ShareService/home" + 
				encodePath(pwd + "/" + fileInfo.getName());
			}
		} else {
			// will this get correct links to the children of shared folders?
			return "https://share.teragrid.org:28443/ShareService/home/shared/" + fileInfo.getNonce();
		}
    }

    /** 
     * Get the size of the named file.  Defaults to getSize(filename)
     * @param name of the file to get the size of
     */
    @Override
    public long getSize(String filename) throws IOException, ServerException {
        List<FileInfo> files = new ArrayList<FileInfo>();
		try {
			files = list(filename);
			
			for (FileInfo fileInfo: files) {
	        	if (fileInfo.getName().equals(filename)) {
	        		return fileInfo.getSize();
	        	}
	        }
			
		} catch (ClientException e) {
			throw new ServerException(ServerException.SERVER_REFUSED, "Failed to get remote file size: " + e.getMessage());
		}
        
        
        return -1;
    }    
    
    /** 
     * Sets the permissions on the remote file
     */
    @Override
    public Reply site(String command) throws IOException, ServerException {
    	String[] args = command.split(" ");
    	String uname = args[0];
    	int pemValue = Integer.parseInt(args[1]);
    	// comment out if not debugging
    	
    	// path may have spaces in the title which would have broken the tokenizer
    	String path = "";
    	for (int i=2;i<args.length - 1;i++) {
    		path += args[i] + " ";
    	}
    	path = path.trim();

    	try {
    		path = encodePath(path);
    		
    		int absValue = Math.abs(pemValue);
    		
    		String permission = "";
    		
    		Form form = new Form();
			
    		if (absValue == 7) {
    			form.add("remove_read_permission", uname);
    			form.add("remove_write_permission", uname);
    			form.add("add_all_permission", uname);
    		} else if (absValue == 6) {
    			form.add("add_read_permission", uname);
    			form.add("add_write_permission", uname);
    			form.add("remove_all_permission", uname);
    		} else if (absValue == 4) {
    			form.add("add_read_permission", uname);
    			form.add("remove_write_permission", uname);
    			form.add("remove_all_permission", uname);
    		} else if (absValue == 2) {
    			form.add("remove_read_permission", uname);
    			form.add("add_write_permission", uname);
    			form.add("remove_all_permission", uname);
    		} else {
    			form.add("remove_read_permission", uname);
    			form.add("remove_write_permission", uname);
    			form.add("remove_all_permission", uname);
    		}
    		
    		//form.add("notify_permission", ConfigOperation.getInstance().getConfigValue("notification").equalsIgnoreCase("NONE")?"false":"true");
    		form.add("notify_permission", "true");
    		
    		if (pemValue == 0) {
    			Request request = new Request(Method.PUT, ConfigSettings.SERVICE_TGSHARE_SERVICE + "/permissions" + path);
    			ChallengeResponse authentication = new ChallengeResponse(ChallengeScheme.HTTP_BASIC,  
    	    			username, password);  
    	    	
    	    	request.setChallengeResponse(authentication); 
    	    	request.setEntity(form.getWebRepresentation());
    	    	
    	    	Client rc = new Client(Protocol.HTTP);
    	    	Response response = rc.handle(request);
    	    	
    	    	if (!response.getStatus().isSuccess()) {
    	    		handleError(response.getStatus());
    	    	}
    	    	
    			rc.stop();
    			
    		} else if (pemValue > 0) {
    			
    			// delete the old permissions
//    			Request request = new Request(Method.DELETE, ConfigSettings.SERVICE_TGSHARE_SERVICE + "/permissions" + path);
//    			
    			ChallengeResponse authentication = new ChallengeResponse(ChallengeScheme.HTTP_BASIC,  
    	    			username, password);  
//    	    	
//    	    	request.setChallengeResponse(authentication); 
//    	    	
    	    	
//    	    	
    	    	// add the new ones
    	    	Request request = new Request(Method.PUT, ConfigSettings.SERVICE_TGSHARE_SERVICE + "/permissions" + path);
    	    	request.setChallengeResponse(authentication); 
    	    	request.setEntity(form.getWebRepresentation());
    	    	
    	    	Client rc = new Client(Protocol.HTTP);
    	    	Response response = rc.handle(request);
    	    	
    	    	if (!response.getStatus().isSuccess()) {
    	    		handleError(response.getStatus());
    	    	}	
    			
    			rc.stop();
    		} 
    		
    		return null;
    		
    	} catch (Exception e) {
    		e.printStackTrace(); 
    		throw new ServerException(0, "Failed to set permission " + args[1] + 
    				" for " + path);
    	}
    }
    
    private String encodePath(String path) throws IOException {
    	if (path.equals("") || path.equals("/")) return "/";
    	
    	String encodedPath = "";
    	String[] pathElements = StringUtils.split(path, '/');
		for (String element: pathElements) {
			if (!element.equals(""))
			encodedPath += "/" + URLEncoder.encode(element, "utf-8");
		}
		return encodedPath.replaceAll("//","/");
    }
    public void setType(int type) throws IOException, ServerException {}
    public void setDTP(boolean bPassive) throws IOException, ServerException, ClientException {}
    public void setMode(int mode) throws IOException, ServerException {}
    public void setParellel(int nParallel) throws IOException, ServerException {}
    public void setActive(HostPort hostPort) throws IOException, ServerException {}
    
    public HostPort setPassive() throws IOException, ServerException { return null; }
    public void setLocalActive() throws ClientException, IOException {}
    public HostPort setLocalPassive() throws IOException { return null; }
    public void close() throws IOException, ServerException {}
    
//    /**
//     * Set whether the node represented by the given path inherits the permissions of its parent 
//     * folder.
//     * 
//     * @param path String path to node
//     * @param inherits boolean flag does it inherit the parent permissions
//     * @throws ServerException
//     */
//    public void setInheritsParentPermissions(String path, boolean inherits) throws ServerException {
//    	try {
//    		client.setInheritsParentPermissions(getPath(path), inherits);
//    	} catch (Exception e) {
//    		e.printStackTrace(); 
//    		throw new ServerException(0, "Failed to set inherited permissions for " + path);
//    	}
//    }
//    
//    /**
//     * Does the node represented by the path inherit the permissions of its parent folder.
//     * 
//     * @param path String path to node
//     * @return boolean
//     * @throws ServerException
//     */
//    public boolean getInheritsParentPermissions(String path) throws ServerException {
//    	try {
//    		return client.getInheritsParentPermissions(getPath(path));
//    	} catch (Exception e) {
//    		e.printStackTrace(); 
//    		throw new ServerException(0, "Failed to set inherited permissions for " + path);
//    	}
//    }
//    
//    /**
//     * Returns current version information for node at path.
//     * 
//     * @param path
//     * @return Version object of node at path.
//     * @throws ServerException
//     */
//    public Version getCurrentVersion(String path) throws ServerException {
//    	try {
//    		return client.getVersion(getPath(path));
//    	} catch (Exception e) {
//    		e.printStackTrace();
//    		throw new ServerException(0, "Failed to retrieve version information for " + path);
//    	}
//    }
//    
//    public VersionHistory getVersionHistory(String path) throws ServerException {
//    	try {
//    		return client.getVersionHistory(getPath(path));
//    	} catch (Exception e) {
//    		e.printStackTrace();
//    		throw new ServerException(0, "Failed to retrieve version information for " + path);
//    	}
//    }
//    
//    public void revertVersion(String path,Version version) throws ServerException {
//    	try {
//    		client.revertVersion(path, version);
//    	} catch (Exception e) {
//    		e.printStackTrace();
//    		throw new ServerException(0, "Failed to rollback version to " + version.getVersionLabel());
//    	}
//    }
//    public void enableVersioning(String path, boolean versionable) throws ServerException {
//		try {
//			client.enableVersioning(path, versionable);
//		} catch (Exception e) {
//			e.printStackTrace();
//			throw new ServerException(0, "Failed to " + (versionable?"enable":"disable") + " versioning for " + path);
//		}
//	}
//    public void setVersionComment(String path, String comment) throws ServerException {
//    	try {
//			client.setVersionComment(path, comment);
//		} catch (Exception e) {
//			e.printStackTrace();
//			throw new ServerException(0, "Failed to add version comment to " + path);
//		}
//    }
    
    private void handleError(Status status) throws ServerException {
    	if (status.equals(Status.CLIENT_ERROR_FORBIDDEN)) {
			throw new ServerException(ServerException.SERVER_REFUSED, "Permission denied");
		} else if (status.equals(Status.CLIENT_ERROR_UNAUTHORIZED)) {
			throw new ServerException(ServerException.SERVER_REFUSED, "Autentication failed. Please check your username and password and try again.");
		} else {
			throw new ServerException(ServerException.SERVER_REFUSED, "Request failed");
		}
    }
    
    public String getPath(String relativePath) throws ServerException, IOException {
    	
    	if (relativePath == null)
    		return getCurrentDir();
        
    	if (relativePath.equals("")) {
    		return "/";
    	}
    	
    	relativePath = relativePath.replaceAll("//", "/");
        
//    	if (relativePath.equals("/public")) {
//    		return "/public/" + username;
//    	}
    	
        File currentDir = new File(getCurrentDir());
        
        if (relativePath.endsWith("..")) {
        	try {
        		return currentDir.getParentFile().getPath();
        	} catch (NullPointerException e) {
        		return getCurrentDir();
        	}
        }
        if (relativePath.startsWith("/")) {
        	return relativePath;
        } else {
        	return currentDir.getPath() + File.separator + relativePath;
        }
    }
	  
}
