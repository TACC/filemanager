package org.teragrid.portal.filebrowser.applet.transfer.streams;

import java.io.IOException;

import org.globus.ftp.FTPClient;
import org.globus.ftp.RestartData;
import org.globus.ftp.exception.FTPException;

public class GridFTPInputStream extends FTPInputStream {
	public GridFTPInputStream(FTPClient gridFtp, String file, boolean passive)
			throws IOException, FTPException {
		super(gridFtp, file, passive, null);
	}

	public GridFTPInputStream(FTPClient gridFtp, String file, boolean passive,
			RestartData restart) throws IOException, FTPException {
		super(gridFtp, file, passive, restart);
	}
}