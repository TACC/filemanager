package edu.utexas.tacc.wcs.filemanager.client.transfer.streams;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.text.MessageFormat;

import org.globus.common.ChainedIOException;
import org.globus.ftp.FTPClient;
import org.globus.io.gass.client.GassException;
import org.globus.io.gass.client.internal.GASSProtocol;
import org.globus.io.streams.GlobusOutputStream;
import org.globus.util.http.HttpResponse;

import edu.utexas.tacc.wcs.filemanager.client.transfer.HTTP;
import edu.utexas.tacc.wcs.filemanager.client.util.LogManager;
import edu.utexas.tacc.wcs.filemanager.client.util.ResourceName;
import edu.utexas.tacc.wcs.filemanager.client.util.SGGCResourceBundle;

public class HTTPOutputStream extends GlobusOutputStream {

	private static final byte[] CRLF = "\r\n".getBytes();

	private static final int DEFAULT_TIME = 3000;

	protected OutputStream output;

	protected InputStream in;

	protected Socket socket;

	protected FTPClient ftp;

	protected String targetFile;

	protected long size = -1;

	protected boolean append = false;

	/**
	 * Private constructor used by subclasses.
	 */
	protected HTTPOutputStream() {
	}

	/**
	 * Opens HTTP output stream (unsecure)
	 *
	 * @param host host name of the HTTP server.
	 * @param port port number of the HTTP server.
	 * @param file name of the file on the remote side.
	 * @param length total size of the data to be transfered.
	 *               Use -1 if unknown. The data then will be
	 *               transfered in chunks.
	 * @param append if true, append data to existing file.
	 *               Otherwise, the file will be overwritten.
	 */
	public HTTPOutputStream(String host, int port, String file, long length,
			boolean append) throws GassException, IOException {
		init(host, port, file, length, append);
	}

	public HTTPOutputStream(FTPClient ftpClient, String file, long length,
			boolean append) throws GassException, IOException {
		ftp = ftpClient;
		output = ((HTTP) ftp).getOutputStream();
		in = ((HTTP) ftp).getInputStream();

		int time = DEFAULT_TIME;

		long st = System.currentTimeMillis();
		long et = System.currentTimeMillis();

		time = 2 * (int) (et - st);

		put(((HTTP) ftp).getHost(), file, length, time);
	}

	private void init(String host, int port, String file, long length,
			boolean append) throws GassException, IOException {
		size = length;
		this.append = append;

		// default waiting time for response from the server
		int time = DEFAULT_TIME;

		long st = System.currentTimeMillis();
		socket = new Socket(host, port);
		long et = System.currentTimeMillis();

		time = 2 * (int) (et - st);
		output = socket.getOutputStream();
		in = socket.getInputStream();
		put(host, file, length, time);
	}

	private void sleep(int time) {
		try {
			Thread.sleep(time);
		} catch (Exception e) {
		}
	}

	protected void put(String host, String file, long length, int waittime)
			throws IOException {

		//        output = socket.getOutputStream();
		//        in  = socket.getInputStream();

		String msg = GASSProtocol.PUT(file, host, length, append);

		LogManager.debug("SENT: " + msg);
		
		output.write(msg.getBytes());
		output.flush();

		if (waittime < 0) {
			int maxsleep = DEFAULT_TIME;
			while (maxsleep != 0) {
				sleep(1000);
				maxsleep -= 1000;
				checkForReply();
			}
		} else {
			sleep(waittime);
		}

		checkForReply();
	}

	private void checkForReply() throws IOException {

		if (in.available() <= 0) {
			return;
		}

		HttpResponse reply = new HttpResponse(in);

		LogManager.debug("REPLY: " + reply);
		
		if (reply.httpCode != 100) {
			abort();
			String errorMsg = MessageFormat.format(SGGCResourceBundle.getResourceString(ResourceName.KEY_EXCEPTION_IOSTREAM_GASSPUTFAILED), 
					new Object[] {reply.httpMsg});
			throw new IOException(errorMsg);
		} else {
		    LogManager.debug("Received continuation reply");
		}
	}

	private void finish() throws IOException {
		if (size == -1) {
			String lHex = Integer.toHexString(0);
			output.write(lHex.getBytes());
			output.write(CRLF);
			output.write(CRLF);
		}
		output.flush();
	}

	private void closeSocket() {
		try {
			if (socket != null) {
				socket.close();
			}
			if (in != null) {
				in.close();
			}
			if (output != null) {
				output.close();
			}
		} catch (Exception e) {
		}
	}

	public void abort() {
		try {
			finish();
		} catch (Exception e) {
		}
		closeSocket();
	}

	public void close() throws IOException {

		// is there a way to get rid of that wait for final reply?

		finish();

		HttpResponse hd = new HttpResponse(in);

		closeSocket();

		LogManager.debug("REPLY: " + hd);
		
		if (hd.httpCode != 200) {
			String errorMsg = MessageFormat.format(SGGCResourceBundle.getResourceString(ResourceName.KEY_EXCEPTION_IOSTREAM_GASSPUTFAILED), 
					new Object[] {hd.httpMsg});
			throw new ChainedIOException(SGGCResourceBundle.getResourceString(ResourceName.KEY_EXCEPTION_IOSTREAM_GASSCLOSEFAILED),
					new GassException(errorMsg));
		}
	}

	public void write(byte[] msg) throws IOException {
		write(msg, 0, msg.length);
	}

	public void write(byte[] msg, int from, int length) throws IOException {
		checkForReply();
		if (size == -1) {
			String lHex = Integer.toHexString(length);
			output.write(lHex.getBytes());
			output.write(CRLF);
			output.write(msg, from, length);
			output.write(CRLF);
		} else {
			output.write(msg, from, length);
		}
	}

	public void write(int b) throws IOException {
		checkForReply();
		if (size == -1) {
			output.write("01".getBytes());
			output.write(CRLF);
			output.write(b);
			output.write(CRLF);
		} else {
			output.write(b);
		}
	}

	public void flush() throws IOException {
		output.flush();
	}
}