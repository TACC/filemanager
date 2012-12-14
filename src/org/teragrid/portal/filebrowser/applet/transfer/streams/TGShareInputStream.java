package org.teragrid.portal.filebrowser.applet.transfer.streams;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

import org.globus.ftp.FTPClient;
import org.globus.ftp.exception.ServerException;
import org.globus.io.streams.GlobusInputStream;
import org.teragrid.portal.filebrowser.applet.transfer.TGShare;
import org.teragrid.portal.filebrowser.applet.util.LogManager;


public class TGShareInputStream extends GlobusInputStream {
	
	protected InputStream in;
    
    protected FTPClient ftpClient;
    
	public TGShareInputStream(FTPClient ftpClient, String file) throws MalformedURLException, IOException, ServerException {
        this.ftpClient = ftpClient;
        this.in = ((TGShare)ftpClient).getInputStream(file);
    }
	
    /* (non-Javadoc)
     * @see org.globus.io.streams.GlobusInputStream#abort()
     */
    @Override
    public void abort() {
        
        try {
            this.ftpClient.abort();
        } catch (Exception e) {
            LogManager.error("Failed to abort S3InputStream",e);
        }
    }

    /* (non-Javadoc)
     * @see java.io.InputStream#close()
     */
    @Override
    public void close() throws IOException {
        this.in.close();
    }

    /* (non-Javadoc)
     * @see java.io.InputStream#markSupported()
     */
    @Override
    public boolean markSupported() {
        return false;
    }

    public int read() throws IOException {
        return this.in.read();
    }

    public int read(byte[] buf) throws IOException {
        return this.in.read(buf);
    }

    public int read(byte[] buf, int off, int len) throws IOException {
        return this.in.read(buf, off, len);
    }

    
    public int available() throws IOException {
        return this.in.available();
    }    
}