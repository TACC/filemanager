package edu.utexas.tacc.wcs.filemanager.client.transfer.streams;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.text.MessageFormat;

import org.globus.ftp.FTPClient;
import org.globus.io.gass.client.internal.GASSProtocol;
import org.globus.io.streams.GlobusInputStream;
import org.globus.util.GlobusURL;
import org.globus.util.http.HTTPChunkedInputStream;
import org.globus.util.http.HttpResponse;

import edu.utexas.tacc.wcs.filemanager.client.transfer.HTTP;
import edu.utexas.tacc.wcs.filemanager.client.util.LogManager;
import edu.utexas.tacc.wcs.filemanager.client.util.ResourceName;
import edu.utexas.tacc.wcs.filemanager.client.util.SGGCResourceBundle;

public class HTTPInputStream extends GlobusInputStream {

	protected InputStream input;

	protected FTPClient ftp;

	protected String targetFile;

	protected Socket socket;

	protected long size = -1;

	/**
	 * Private constructor used by subclasses.
	 */
	protected HTTPInputStream() {
	}

	/**
	 * Opens HTTP input stream connection (unsecure)
	 *
	 * @param host host name of the HTTP server.
	 * @param port port number of the HTTP server.
	 * @param file file to retrieve from the server.
	 */
	public HTTPInputStream(String host, int port, String file)
			throws IOException {
		this.targetFile = file;

		get(host, port, null, file);
	}

	public HTTPInputStream(FTPClient ftpClient, String file) throws IOException {
		this.targetFile = file;
		this.ftp = ftpClient;

		get(((HTTP) this.ftp).getHost(), ((HTTP) this.ftp).getPort(), this.ftp,
				file);
	}

	// subclasses should overwrite this function
	protected Socket openSocket(String host, int port) throws IOException {
		return new Socket(host, port);
	}

	protected void get(String host, int port, FTPClient ftpClient, String file)
			throws IOException {

		HttpResponse hd = null;
		OutputStream out = null;

		while (true) {
			if (ftpClient == null) {
				this.socket = openSocket(host, port);
				this.input = this.socket.getInputStream();
				out = this.socket.getOutputStream();
			} else {
				this.input = ((HTTP) ftpClient).getInputStream();
				out = ((HTTP) ftpClient).getOutputStream();
			}
			String msg = GASSProtocol.GET(file, host);

			try {
				out.write(msg.getBytes());
				out.flush();

				LogManager.debug("SENT: " + msg);
				

				hd = new HttpResponse(this.input);
			} catch (IOException e) {
				abort();
				throw e;
			}

			if (hd.httpCode == 200) {
				break;
			} else {
				abort();
				switch (hd.httpCode) {
				case 404:
					throw new FileNotFoundException(
							MessageFormat
									.format(
											SGGCResourceBundle
													.getResourceString(ResourceName.KEY_EXCEPTION_IOSTREAM_FILENOTFOUND),
											new Object[] {file}));
				case 301:
				case 302:
					LogManager.info("Received redirection to: " + hd.location);
					GlobusURL newLocation = new GlobusURL(hd.location);
					host = newLocation.getHost();
					port = newLocation.getPort();
					file = newLocation.getPath();
					break;
				default:
					throw new IOException(
							MessageFormat
									.format(
											SGGCResourceBundle
													.getResourceString(ResourceName.KEY_EXCEPTION_IOSTREAM_GETERROR),
											new Object[] {hd.httpMsg, new Integer(hd.httpCode)})
					//                            "Failed to retrieve file from server. " +
					//                            " Server returned error: " + hd.httpMsg +
					//                            " (" + hd.httpCode + ")"
					);
				}
			}
		}

		if (hd.chunked) {
			this.input = new HTTPChunkedInputStream(this.input);
		} else if (hd.contentLength > 0) {
			this.size = hd.contentLength;
		} else {
			abort();
			throw new IOException(SGGCResourceBundle.getResourceString(ResourceName.KEY_EXCEPTION_IOSTREAM_INVALIDGETREPLY));
		}
	}

	public void abort() {
		try {
			close();
		} catch (Exception e) {
		}
	}

	public long getSize() {
		return this.size;
	}

	public void close() throws IOException {
		if (this.input != null) {
			this.input.close();
		}
		if (this.socket != null) {
			this.socket.close();
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