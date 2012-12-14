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

public class FTPType {
	public static final String[] FTP_PROTOCOL = new String[] {"FILE", "FTP",
			"GridFTP","Amazon S3","XSHARE", "IRODS"/*, "BBFTP", "SFTP", "HTTP"*/ };	// BBFTP, SFTP, HTTP have not been implemented yet

	public static final int FILE = 0;

	public static final int FTP = 1;

	public static final int GRIDFTP = 2;
	
	public static final int S3 = 3;
	
	public static final int XSHARE = 4;
	
	public static final int IRODS = 5;
	
	public static final int BBFTP = 6;

	public static final int SFTP = 7;

	public static final int HTTP = 8;
	
	
	public static int getType(String sType) {
		if (sType.equals("FTP")) {
			return 1;
		} else if (sType.equals("GridFTP")) {
			return 2;
		} else if (sType.equals("Amazon S3")) {
			return 3;
		} else if (sType.equals("IRODS")) {
			return 5;
		} else if (sType.equals("BBFTP")) {
			return 6;
		} else if (sType.equals("SFTP")) {
            return 7;
        } else if (sType.equals("HTTP")) {
			return 8;
		} else if (sType.equals("FILE")){
			return 0;
		} else if (sType.equals("TGSHARE")) {
			return 4;
		} else if (sType.equals("XSHARE")) {
			return 4;
		} else {
		    return 0;
		}
	}
}
