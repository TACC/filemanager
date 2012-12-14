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

import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.Point;
import java.awt.Toolkit;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.security.cert.CertificateEncodingException;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JFrame;

import org.globus.ftp.DataSink;
import org.globus.ftp.DataSinkStream;
import org.globus.ftp.DataSource;
import org.globus.ftp.FileInfo;
import org.globus.ftp.FileRandomIO;
import org.globus.ftp.GridFTPClient;
import org.globus.ftp.GridFTPSession;
import org.globus.ftp.MarkerListener;
import org.globus.ftp.RetrieveOptions;
import org.globus.ftp.exception.ClientException;
import org.globus.ftp.exception.FTPException;
import org.globus.ftp.exception.ServerException;
import org.globus.ftp.vanilla.Reply;
import org.globus.gsi.GlobusCredential;
import org.globus.gsi.GlobusCredentialException;
import org.globus.gsi.gssapi.GlobusGSSCredentialImpl;
import org.globus.tools.proxy.GridProxyInit;
import org.gridforum.jgss.ExtendedGSSCredential;
import org.gridforum.jgss.ExtendedGSSManager;
import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSException;
import org.teragrid.portal.filebrowser.applet.AppMain;
import org.teragrid.portal.filebrowser.applet.ConfigOperation;
import org.teragrid.portal.filebrowser.applet.file.GlobusFileInfo;
import org.teragrid.portal.filebrowser.applet.util.LogManager;
import org.teragrid.portal.filebrowser.applet.util.proxy.MyProxyDialog;

public class GridFTP extends GridFTPClient {
	public static String Prefix = "gsiftp://";

	protected GSSCredential gssCred = null;

	protected GlobusCredential globusCred = null;

	protected int nParallel = 1;

	public GridFTP(String sHost, int nPort, GlobusCredential cred)
			throws IOException, ServerException {
		super(sHost, nPort);
		LogManager.debug("Starting session with " + sHost + ":" + nPort);
		this.globusCred = cred;
	}

	public static GlobusCredential authorize() throws IOException,
			GlobusCredentialException {

		String proxy = null;
		GlobusCredential cred = null;
		
		if (AppMain.isApplet()) {
			try {
				proxy = AppMain.getApplet().getParameter("filebrowser.gsscredential");
			} catch (NullPointerException e) {
				// running as a separate frame
			}
			
			if (proxy != null) {
				proxy = proxy.replaceAll("KEY-----", "KEY-----\n");
				proxy = proxy.replaceAll("-----END", "\n-----END");
				proxy = proxy.replaceAll("CERTIFICATE-----", "CERTIFICATE-----\n");
	
				ByteArrayInputStream bisProxyString = new ByteArrayInputStream(
						proxy.getBytes());
				cred = new GlobusCredential(bisProxyString);
				// Set the user's credential from the proxy stored in the session
				// variables
	
			}
		}
		
		if (proxy == null || cred == null || cred.getTimeLeft() == 0) {
			LogManager.debug("No valid proxy retrieved from portlet." +
					"\nTrying MyProxy.");
			try {
				cred = retrieveCredential();
			} catch (Exception e) {
				LogManager.error("Failed to retrieve the remote proxy.",e);
			}
		}

		// Set the user's credential from the proxy stored in the session
		// variables
		LogManager.debug("DN for user is " + cred.getSubject());

		return cred;
	}

	public void authorize(String s1, String s2) throws IOException,
			ServerException {
		if (this.globusCred == null) {
			this.globusCred = AppMain.defaultCredential;
		}
		
		if (this.globusCred != null && this.globusCred.getTimeLeft() > 0) {
			authenticate(this.globusCred);
		} else {
			AppMain.defaultCredential = authorize(ConfigOperation.getInstance().getConfigValue("proxy"));
			this.globusCred = AppMain.defaultCredential;
		}

	}

	public static GlobusCredential authorize(java.awt.Frame frmParent) {
		return authorize(ConfigOperation.getInstance().getConfigValue("proxy"));
	}

	public static GlobusCredential authorize(String certPath) {

		GlobusCredential cred = null;

//		try {
//			// always pull the credential from myproxy so we have a sso token
//			cred = readCredential(certPath);
//
//			if (cred != null && cred.getTimeLeft() > 0)
//				return cred;
//
//		} catch (Exception e) {
//			LogManager.error("No credential found on disk at " + certPath, e);
//		}

		try {
			cred = retrieveCredential();
		} catch (Exception e) {
			LogManager.error("Failed to retrieve the remote proxy.", e);
		}

		return cred;
	}
	
	private static GlobusCredential readCredential(String path)
			throws IOException, GSSException {

		GlobusCredential cred = null;

		java.io.File certFile = new java.io.File(path);
		byte[] data = new byte[(int) certFile.length()];
		FileInputStream in = new FileInputStream(path);
		// read in the credential data
		in.read(data);
		in.close();
		ExtendedGSSManager manager = (ExtendedGSSManager) ExtendedGSSManager
				.getInstance();
		GSSCredential proxy = manager.createCredential(data,
				ExtendedGSSCredential.IMPEXP_OPAQUE,
				GSSCredential.DEFAULT_LIFETIME, null, // use default mechanism -
				// GSI
				GSSCredential.ACCEPT_ONLY);

		if (proxy instanceof GlobusGSSCredentialImpl) {
			cred = ((GlobusGSSCredentialImpl) proxy).getGlobusCredential();
		}

		return cred;

	}

	private static GlobusCredential retrieveCredential() throws IOException,
			GSSException, CertificateEncodingException {

//		GlobusCredential cred = null;
//
//		Object[] authMethod = { "MyProxy", "Grid-Proxy-Init" };
// 		String s = (String) AppMain.Prompt(AppMain.getFrame(),
//				"Select your authentication method.", "Authentication Dialog",
//				JOptionPane.PLAIN_MESSAGE, authMethod, authMethod[0]);
//
//		// If a string was returned, open that dialog.
//		if ((s != null) && (s.length() > 0)) {
//			if (s.equals(authMethod[0])) {
//				cred = showMyProxyDialog();
//			} else if (s.equals(authMethod[1])) {
//				cred = showGridProxyInitDialog();
//			}
//		}
//
//		return cred;
		return showMyProxyDialog();
	}

	private static GlobusCredential showMyProxyDialog() throws IOException,
			GSSException, CertificateEncodingException {
		MyProxyDialog dlgGrid = new MyProxyDialog(AppMain.getFrame(),"MyProxy Authentication");
		dlgGrid.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		Dimension dlgSize = dlgGrid.getPreferredSize();
		Dimension frmSize = Toolkit.getDefaultToolkit().getScreenSize();
		Point loc = new Point(0, 0);
		dlgGrid.setLocation((frmSize.width - dlgSize.width) / 2 + loc.x,
				(frmSize.height - dlgSize.height) / 2 + loc.y);
		dlgGrid.pack();
		dlgGrid.setVisible(true);
		
//		GlobusCredential proxy = dlgGrid.get.getProxy();
//		proxy.save(new FileOutputStream(ConfigOperation.getInstance()
//				.getConfigValue("proxy")));

		// // Set the user's credential from the proxy stored in the session
		// variables
//		ConfigOperation.getInstance().setConfigValue("dn", proxy.getSubject());
//		MyProxyDialog dlgMyproxy = new MyProxyDialog(null,
//				"MyProxy Logon");
		
		return dlgGrid.getGlobusCredential(); 
	}

	/**
	 * Launches the Globus grid-proxy-init dialog.
	 * 
	 * @return
	 * @throws HeadlessException
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	private static GlobusCredential showGridProxyInitDialog()
			throws HeadlessException, FileNotFoundException, IOException {

		GridProxyInit dlgGrid = new GridProxyInit(null, true);
		dlgGrid.setCloseOnSuccess(true);
		Dimension dlgSize = dlgGrid.getPreferredSize();
		Dimension frmSize = Toolkit.getDefaultToolkit().getScreenSize();
		Point loc = new Point(0, 0);
		dlgGrid.setLocation((frmSize.width - dlgSize.width) / 2 + loc.x,
				(frmSize.height - dlgSize.height) / 2 + loc.y);
		dlgGrid.pack();
		dlgGrid.setVisible(true);

		GlobusCredential proxy = dlgGrid.getProxy();
		proxy.save(new FileOutputStream(ConfigOperation.getInstance()
				.getConfigValue("proxy")));

		// // Set the user's credential from the proxy stored in the session
		// variables
		ConfigOperation.getInstance().setConfigValue("dn", proxy.getSubject());

		return proxy;
	}

	public void authenticate(GlobusCredential cred) throws IOException,
			ServerException {
		try {
			LogManager.debug("Authenticating with remote resource.");
			// convert a GlobusCredentail to a GSSCredntial required byFTP
			this.gssCred = new GlobusGSSCredentialImpl(cred,
					GSSCredential.DEFAULT_LIFETIME);

			authenticate(this.gssCred);
		} catch (GSSException ex) {
			throw new ServerException(ServerException.SERVER_REFUSED, ex.getMessage());
		} catch (NullPointerException e) {
			AppMain.Error(AppMain.getFrame(),
				"Failed to load user credentials.\n"
						+ "Please reload the page and log back\ninto the TGUP.");
		}
	}

	public void authenticate(String sCert) throws IOException, ServerException {
		try {
			this.globusCred = new GlobusCredential(sCert);
			authenticate(this.globusCred);
		} catch (GlobusCredentialException ex) {
			throw new ServerException(ServerException.SERVER_REFUSED, ex.getMessage());
		}
	}

	public void changeDir(String sDir) throws IOException, ServerException {
		if (sDir.equals("..")) {
			sDir = getCurrentDir();
			int nIndex = sDir.lastIndexOf("/");
			if (nIndex <= 0) {
				nIndex++;
			}
			sDir = sDir.substring(0, nIndex);
		}
		super.changeDir(sDir);
	}

	@SuppressWarnings("unchecked")
	public void deleteDir(String sDir) throws IOException, ServerException {
		super.changeDir(sDir);
		try {
			setPassive();
			setLocalActive();
			Vector<FileInfo> sSub = list();
			for (Iterator<FileInfo> r = sSub.iterator(); r.hasNext();) {
				FileInfo f = (FileInfo) r.next();
				if (f.getName().equals(".") || f.getName().equals("..")) {
					continue;
				}
				if (f.isDirectory()) {
					deleteDir(f.getName());
				} else {
					deleteFile(f.getName());
				}
			}
		} catch (ClientException ex) {
		}
		goUpDir();
		super.deleteDir(sDir);
	}

	public Vector<FileInfo> list(String sFilter) throws IOException, ServerException,
			ClientException {
		// setLocalNoDataChannelAuthentication();
		Vector<FileInfo> v = new Vector<FileInfo>();
		// InputStreamDataSink iSink = new InputStreamDataSink(); //This will
		// cause some dirs cannot be listed
		OutputStream o = new ByteArrayOutputStream();
		DataSinkStream iSink = new DataSinkStream(o);
		// try adding mlsd support here. Will give info on files not staged from tape yet
		super.list("", "", iSink); // Cannot use any filter
		//super.mlsd(null, iSink);
		String response = o.toString().replaceAll("\r", "\n").replaceAll("\n\n", "\n");
		String[] sLines = response.split("\n");
		for (int i = 0, l = sLines.length; i < l; i++) {
			String listing = "";
			if (sLines[i].toLowerCase().startsWith("total")) continue;
			if (super.getHost().contains("mss.ncsa")) {
				int j=0;
				String[] tokens = sLines[i].split("[\\s]+");
				for (String token: tokens) {
					if (j != 3 && j != 5) {
						listing += token + " ";
					}
					j++;
				}
				listing = listing.trim();
//				listing = listing.replaceAll("root DK", "")
//				  .replaceAll("root AR", "")
//				  .replaceAll("ac DK", "")
//				  .replaceAll("ac AR", "");
			} else {
				listing = sLines[i];
			}
			try {
				FileInfo f = new GlobusFileInfo(new FileInfo(listing));
				v.add(f);
			} catch (FTPException ex) {
				LogManager.error("failed to create file info",ex);
			}
		}
		return v;
		// return super.list(sFilter); //This cannot work in GridFTP of GT
		// higher than 3.9
	}

	public void get(String sRemoteFile, String sLocalFile) throws IOException,
			ServerException, ClientException {
		long size = getSize(sRemoteFile);
		DataSink sink = null;
		sink = new FileRandomIO(new RandomAccessFile(sLocalFile, "rw"));
		setLocalPassive();
		setActive();
		extendedGet(sRemoteFile, size, sink, null);
	}

	public void put(String sLocalFile, String sRemoteFile,
			MarkerListener listener, boolean bAppend) throws IOException,
			ServerException, ClientException {
		DataSource source = null;
		source = new FileRandomIO(new RandomAccessFile(sLocalFile, "rw"));
		setPassive();
		setLocalActive();
		extendedPut(sRemoteFile, source, listener);
	}
	
	public void setParellel(int nParallel) throws IOException, ServerException {
		gSession.parallel = nParallel;
		if (nParallel > 1) {
			setMode(GridFTPSession.MODE_EBLOCK);
			setOptions(new RetrieveOptions(nParallel));
		} else {
			setMode(GridFTPSession.MODE_STREAM);
		}
	}
	
	@Override
	public Reply site(String arg) throws IOException, ServerException {
		String[] args = arg.split(" ");
		return super.site("chmod " + args[1] + " " + args[2]);
	}

	public GlobusCredential getGlobusCred() {
		return this.globusCred;
	}
}
