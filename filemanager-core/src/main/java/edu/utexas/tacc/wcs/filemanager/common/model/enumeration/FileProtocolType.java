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

public enum FileProtocolType 
{
	FILE (0, "file://"),
	FTP (21, "ftp://"),
	GRIDFTP (2811, "gridftp://"),
	S3 (443, "http://"),
	IRODS (1247, "irods://"),
	BBFTP (5021, "bbftp://"),
	SFTP (22, "sftp://"),
	HTTP (80, "http://");
	
	
	private Integer defaultPort;
	private String schema;
	
	private FileProtocolType(Integer defaultPort, String schema) {
		this.setDefaultPort(defaultPort);
		this.schema = schema;
	}
	
	public int getDefaultPort() {
		return defaultPort;
	}

	public void setDefaultPort(Integer defaultPort) {
		this.defaultPort = defaultPort;
	}
	
	public String getSchema()
	{
		return this.schema + "://";
	}

	public static FileProtocolType getType(String sType) {
		for (FileProtocolType ftpType: FileProtocolType.values()) {
			if (ftpType.name().equalsIgnoreCase(sType)) {
				return ftpType;
			}
		}
		
		return FILE;
	}	

}
