/*
 * Portions of this file Copyright 2004-2007 Shanghai Jiaotong University
 * 
 * This file or a portion of this file is licensed under the
 * terms of the Globus Toolkit Public License, found at
 * http://www.globus.org/toolkit/legal/4.0/
 * If you redistribute this file, with or without
 * modifications, you must include this notice in the file.
 */

package edu.utexas.tacc.wcs.filemanager.common.model.enumeration;

public class FTPPort {
	public static final int[] FTP_PORT = new int[] {0, 21, 2811, 443, 7321/*, 5021, 22, 80*/ };// BBFTP, SFTP, HTTP have not been implemented yet

	public static final int PORT_NONE = 0;

	public static final int PORT_FTP = 21;

	public static final int PORT_GRIDFTP = 2811;

	public static final int PORT_BBFTP = 5021;

	public static final int PORT_SFTP = 22;

	public static final int PORT_HTTP = 80;
	
	public static final int PORT_S3 = 443;
	
	public static final int PORT_IRODS = 7321;
	
	public static final int PORT_TGSHARE = 8080;
	
}
