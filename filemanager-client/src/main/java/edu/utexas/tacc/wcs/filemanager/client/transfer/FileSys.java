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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Vector;

import org.globus.ftp.FTPClient;
import org.globus.ftp.FileInfo;
import org.globus.ftp.HostPort;
import org.globus.ftp.exception.FTPException;

import edu.utexas.tacc.wcs.filemanager.client.AppMain;
import edu.utexas.tacc.wcs.filemanager.client.util.ResourceName;
import edu.utexas.tacc.wcs.filemanager.client.util.SGGCResourceBundle;


public class FileSys extends FTPClient {

	public static String Prefix = "file://";

	@SuppressWarnings("unused")
	private String sHost, sUser, sPwd, sCurrentDir;

	private File fDir = new File("/");

	public FileSys(String sHost, int nPort) {
	}

	public void changeDir(String sDir) throws IOException {
		File f = null;

		try {
            if (sDir.equals("~")) {
            	f = new File(System.getProperty("user.home"));
    		} else {
    			f = getFile(sDir);
    		}
        } catch (Exception e) {
            e.printStackTrace();
        }
        
		if (f.exists() && f.canRead()) {
			this.fDir = f;
		} else {
            System.out.println("Error trying to cd to " + sCurrentDir + File.separator + sDir);
			throw new IOException(SGGCResourceBundle.getResourceString(ResourceName.KEY_EXCEPTION_FILESYS_PERMISSIONDENIED) + ": " + f.getName());
		}
	}

	public void close() {
	}

	public boolean exists(String sFile) throws IOException {
		File f = getFile(sFile);
		return f.exists();
	}
	public void deleteDir(String sDir) throws IOException {
		File f = getFile(sDir);
		if (!f.exists() || !deleteDirAll(f)) {
			throw new IOException(SGGCResourceBundle.getResourceString(ResourceName.KEY_EXCEPTION_FILESYS_DIRACCESSERROR) + ": " + f.getName());
		}
	}

	private boolean deleteDirAll(File sFile) throws IOException {
		boolean bSuccess = true;
		if (sFile.isFile()) {
			return sFile.delete();
		}
		
		if (!(bSuccess = sFile.delete())) {
			File[] sSub = sFile.listFiles();
			for (int i = 0, l = sSub.length; i < l; i++)
				bSuccess = deleteDirAll(sSub[i]) && bSuccess;
		}
		return bSuccess || sFile.delete();
	}

	public void deleteFile(String sFile) throws IOException {
		File f = getFile(sFile);
		if (!f.exists() || !f.delete()) {
			throw new IOException(SGGCResourceBundle.getResourceString(ResourceName.KEY_EXCEPTION_FILESYS_FILENOTEXIST) + ": " + f.getName());
		}
	}

	public void get(String sRemoteFile, File sLocalFile) throws IOException {
		if (sLocalFile.exists() || !sLocalFile.createNewFile()) {
			throw new IOException(SGGCResourceBundle.getResourceString(ResourceName.KEY_EXCEPTION_FILESYS_FILEALREADYEXIST) + ": " + sLocalFile.getName());
		}
		FileInputStream fIn = new FileInputStream(sRemoteFile);
		FileOutputStream fOut = new FileOutputStream(sLocalFile);
		byte[] b = new byte[200000];

		int length = 0;
		while ((length = fIn.read(b)) > 0)
			fOut.write(b, 0, length);
		fIn.close();
		fOut.close();
	}

	public String getCurrentDir() throws IOException {
		return this.fDir.getCanonicalPath();
	}

	private File getFile(String sName) throws IOException {
		if ('/' == sName.charAt(0) || AppMain.appOS.startsWith("Windows")
				&& sName.length() > 1 && ':' == sName.charAt(1)) {
			return new File(sName);
		} else {
			return new File(this.fDir, sName).getCanonicalFile();
		}
	}

	private String getMode(File f) {
		StringBuffer s = new StringBuffer();
		s.append(f.isDirectory() ? 'd' : '-');
		s.append(f.canRead() ? 'r' : '-');
		s.append(f.canWrite() ? 'w' : '-');
		s.append("-------");
		return s.toString();
	}

	public long getSize(String sFile) throws IOException {
		File f = getFile(sFile);
		return f.length();
	}

	public boolean isActiveMode() {
		return true;
	}

	public boolean isPassiveMode() {
		return true;
	}

	public Vector<FileInfo> list(String sFilter) {
		Vector<FileInfo> v = new Vector<FileInfo>();
		File[] fs = this.fDir.listFiles();
		SimpleDateFormat df = new SimpleDateFormat("MMM dd HH:mm", Locale.US);
		for (int i = 0; null != fs && i < fs.length; i++) {
			if (!FTPSettings.DefaultShowHidden && fs[i].isHidden()) {
				continue;
			}
            
			Date d = new Date(fs[i].lastModified());
			String sUnixListReply = getMode(fs[i]) + "\t1\tftp\tftp\t"
					+ fs[i].length() + "\t" + df.format(d) + "\t"
					+ fs[i].getName();
			try {
				FileInfo fi = new FileInfo(sUnixListReply);
				v.add(fi);
			} catch (FTPException ex) {
			}
		}
		return v;
	}

	public void makeDir(String sDir) throws IOException {
		File f = getFile(sDir);
		if (!f.mkdirs()) {
			throw new IOException(SGGCResourceBundle.getResourceString(ResourceName.KEY_EXCEPTION_FILESYS_DIRALREADYEXIST) + ": " + f.getName());
		}
	}

	public void put(File sLocalFile, String sRemoteFile, boolean bAppend)
			throws IOException {
		File fRemoteFile = getFile(sRemoteFile);
		if (fRemoteFile.exists() || !fRemoteFile.createNewFile()) {
			throw new IOException(SGGCResourceBundle.getResourceString(ResourceName.KEY_EXCEPTION_FILESYS_FILEALREADYEXIST) + ": " + fRemoteFile.getName());
		}
		FileInputStream fIn = new FileInputStream(sLocalFile);
		FileOutputStream fOut = new FileOutputStream(fRemoteFile);
		byte[] b = new byte[200000];

		int length = 0;
		while ((length = fIn.read(b)) > 0)
			fOut.write(b, 0, length);
		fIn.close();
		fOut.close();
	}

	public void rename(String sOldName, String sNewName) throws IOException {
		File f = getFile(sOldName);
		if (!f.renameTo(new File(this.fDir, sNewName))) {
			throw new IOException(SGGCResourceBundle.getResourceString(ResourceName.KEY_EXCEPTION_FILESYS_FILEALREADYEXIST) + ": " + f.getName());
		}
	}

	public void setActive() {
	}

	public HostPort setPassive() {
		return null;
	}

	public void setLocalActive() {
	}

	public HostPort setLocalPassive() {
		return null;
	}

	public void setMode(int nMode) {
	}

	public void setType(int nType) {
	}
}
