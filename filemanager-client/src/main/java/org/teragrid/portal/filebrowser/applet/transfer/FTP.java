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
import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;

import org.globus.ftp.FTPClient;
import org.globus.ftp.FileInfo;
import org.globus.ftp.exception.ClientException;
import org.globus.ftp.exception.ServerException;
import org.globus.ftp.vanilla.Reply;
import org.teragrid.portal.filebrowser.applet.AppMain;


public class FTP extends FTPClient {

	public static String Prefix = "ftp://";
	private Component parent = null;
	
	public FTP(Component parent, String sHost, int nPort) throws IOException, ServerException {
		super(sHost, nPort);
		this.parent = parent;
	}

	public Reply getControl() {
		try {
			return this.controlChannel.read();
		} catch (Exception ex) {
			AppMain.Error(parent,ex.toString());
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public void deleteDir(String sDir) throws IOException, ServerException {
		changeDir(sDir);
		try {
			setPassive();
			setLocalActive();
			Vector<FileInfo> sSub = list("");
			for (Iterator<FileInfo> r = sSub.iterator(); r.hasNext();) {
				FileInfo f = r.next();
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
}
