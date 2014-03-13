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
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.globus.ftp.FTPClient;

//not implemented yet

@SuppressWarnings({"unused"})
public class HTTP extends FTPClient {

	public static String Prefix = "http://";

	private String sHost, sUser, sPwd;

	private int nPort;

	private Socket socket;

	public HTTP(String host, int port) {
		this.sHost = host;
		this.nPort = port;
	}

	public void close() throws IOException {
		this.socket.close();
	}

	protected boolean openSocket() {
		try {
			this.socket = new Socket(this.sHost, this.nPort);
		} catch (IOException e) {
			return false;
		}
		return true;
	}

	public InputStream getInputStream() throws IOException {
		return this.socket.getInputStream();
	}

	public OutputStream getOutputStream() throws IOException {
		return this.socket.getOutputStream();
	}

	public String getHost() {
		return this.sHost;
	}

	public int getPort() {
		return this.nPort;
	}
}
