package org.teragrid.portal.filebrowser.applet.transfer.streams;

import java.io.IOException;
import java.io.InputStream;

import org.globus.ftp.FTPClient;
import org.globus.ftp.InputStreamDataSink;
import org.globus.ftp.RestartData;
import org.globus.ftp.Session;
import org.globus.ftp.exception.FTPException;
import org.globus.ftp.vanilla.TransferState;
import org.globus.io.streams.GlobusInputStream;
import org.teragrid.portal.filebrowser.applet.util.LogManager;

public class FTPInputStream extends GlobusInputStream {

	protected InputStream input;

	protected FTPClient ftp;

	protected TransferState state;

	protected String targetFile;

	protected FTPInputStream() {
	}

	public FTPInputStream(FTPClient client, String file, boolean passive)
			throws IOException, FTPException {
		this(client, file, passive, null);
	}

	public FTPInputStream(FTPClient client, String file, boolean passive,
			RestartData restart) throws IOException, FTPException {
		this.ftp = client;
		this.targetFile = file;
		get(passive, restart, file);
	}

	protected void get(boolean passive, RestartData restart, String remoteFile)
			throws IOException, FTPException {
		InputStreamDataSink sink = null;

		try {
			this.ftp.setType(Session.TYPE_IMAGE);
			if (passive) {
				this.ftp.setPassive();
				this.ftp.setLocalActive();
			} else {
				this.ftp.setLocalPassive();
				this.ftp.setActive();
			}
			if (null != restart) {
				this.ftp.setRestartMarker(restart);
			}

			sink = new InputStreamDataSink();
			this.input = sink.getInputStream();
			this.state = this.ftp.asynchGet(remoteFile, sink, null);
			this.state.waitForStart();
		} catch (FTPException e) {
			if (sink != null) {
				sink.close();
			}
			close();
			LogManager.debug("Current thread with closed socket is: " + Thread.currentThread().getName());
			throw e;
		}
	}

	public long getSize() {
		long rep = -1;

		try {
			rep = this.ftp.getSize(this.targetFile);
		} catch (Exception e) {
			e.printStackTrace();
            org.teragrid.portal.filebrowser.applet.util.LogManager.debug(e.getLocalizedMessage() + " at " + (e.getStackTrace())[0]);  
		}
		//System.out.println(rep+" is the size");
		return rep;
	}

	public void abort() {
		if (this.input != null) {
			try {
				this.input.close();
			} catch (Exception e) {
			}
		}
		/*try {
		 this.ftp.abort();
		 } catch (IOException e) {
		 } catch (FTPException e) {
		 }*/
	}

	// standard InputStream methods

	public void close() throws IOException {

		if (this.input != null) {
			try {
				this.input.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		try {
			if (this.state != null) {
				long s = System.currentTimeMillis();
//				this.state.waitForEnd();
				wait(1000);
				System.out.println(this.targetFile + " wait for: " + (System.currentTimeMillis() - s));
			}
		} catch (Exception e) {
//			throw new ChainedIOException(
//					SGGCResourceBundle
//							.getResourceString(ResourceName.KEY_EXCEPTION_IOSTREAM_CLOSE),
//					e);
		}
		
	}

	public int read(byte[] msg) throws IOException {
		return this.input.read(msg);
	}

	public int read(byte[] buf, int off, int len) throws IOException {
		return this.input.read(buf, off, len);
	}

	public int read() throws IOException {
		return this.input.read();
	}

	public int available() throws IOException {
		return this.input.available();
	}

}