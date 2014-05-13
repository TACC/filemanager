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

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.Vector;

import org.globus.ftp.FTPClient;
import org.globus.ftp.FileInfo;
import org.globus.ftp.exception.ServerException;

import edu.utexas.tacc.wcs.filemanager.client.AppMain;


public class BBFTP extends FTPClient {
	public static String Prefix = "bbftp://";

	String sHost, sUser, sPwd, sCurrentDir;

	int nPort;

	StringBuffer sErr = new StringBuffer(), sIn = new StringBuffer(),
			sOut = new StringBuffer();

	public BBFTP(String sHost, int nPort) {
		this.sHost = sHost;
		this.nPort = nPort;
		sCurrentDir = "/";
	}

	public void authorize(String sUser, String sPwd) throws IOException,
			ServerException {
		this.sUser = sUser;
		this.sPwd = sPwd;
		exec("bbftp " + sHost + " -e \"dir " + sCurrentDir + "\" -u " + sUser
				+ " -m", sPwd, sOut, sErr);
	}

	public void exec(String sCmd, String sInput, StringBuffer sOutput,
			StringBuffer sError) {
		String sLine;
		try {
			Process p = Runtime.getRuntime().exec(sCmd);
			// OutputStreamWriter w = new
			// OutputStreamWriter(p.getOutputStream());
			// w.write(sInput);
			InputStreamReader e = new InputStreamReader(p.getErrorStream());
			InputStreamReader r = new InputStreamReader(p.getInputStream());
			sError.setLength(0);
			LineNumberReader eLine = new LineNumberReader(e);
			while ((sLine = eLine.readLine()) != null)
				sError.append(sLine + '\n');
			// e.read(sErr);
			LineNumberReader rLine = new LineNumberReader(r);
			sOutput.setLength(0);
			while ((sLine = rLine.readLine()) != null)
				sOutput.append(sLine + '\n');
		} catch (Exception ex) {
			AppMain.Error(null,ex.toString());
		}
	}

	public Vector<FileInfo> list(String sFilter) {
		exec("bbftp " + sHost + " -e \"dir " + sCurrentDir + "\" -u " + sUser
				+ " -m", sPwd, sOut, sErr);
		AppMain.Error(null,sOut.toString(), "list");
		return null;
	}

	public void get(String sRemoteFile, String sLocalFile) {
	}

	public void put(String sLocalFile, String sRemoteFile, boolean bAppend) {
	}
}
