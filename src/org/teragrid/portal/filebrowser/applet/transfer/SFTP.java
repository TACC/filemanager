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

import java.io.IOException;
import java.util.Vector;

import org.globus.ftp.FTPClient;
import org.globus.ftp.FileInfo;
import org.globus.ftp.exception.ServerException;

//not implemented yet

@SuppressWarnings("unused")
public class SFTP extends FTPClient {

	public static String Prefix = "sftp://";

	private String sHost, sUser, sPwd, sCurrentDir;

	private int nPort;

	private StringBuffer sErr = new StringBuffer(), sIn = new StringBuffer(),
			sOut = new StringBuffer();

	public SFTP(String sHost, int nPort) {
		this.sHost = sHost;
		this.nPort = nPort;
		this.sCurrentDir = "/";
	}

	public void authorize(String sUser, String sPwd) throws IOException,
			ServerException {
		this.sUser = sUser;
		this.sPwd = sPwd;
		exec("sftp " + this.sHost + " -e \"dir " + this.sCurrentDir + "\" -u " + sUser
				+ " -m", sPwd, sOut, sErr);
	}

	public void exec(String sCmd, String sInput, StringBuffer sOutput,
			StringBuffer sError) {
	}

	public Vector<FileInfo> list(String sFilter) {
		return null;
	}

	public void get(String sRemoteFile, String sLocalFile) {
	}

	public void put(String sLocalFile, String sRemoteFile, boolean bAppend) {
	}
}
