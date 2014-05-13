package edu.utexas.tacc.wcs.filemanager.client.transfer.streams;

import java.io.IOException;

import org.globus.ftp.FTPClient;
import org.globus.ftp.exception.FTPException;

public class GridFTPOutputStream extends FTPOutputStream {
	public GridFTPOutputStream(FTPClient gridFtp, String file, boolean passive,
			boolean append) throws IOException, FTPException {
		super(gridFtp, file, passive, append);
	}

	public GridFTPOutputStream(FTPClient gridFtp, String file, boolean passive,
			int type, boolean append) throws IOException, FTPException {
		super(gridFtp, file, passive, type, append);
	}
}