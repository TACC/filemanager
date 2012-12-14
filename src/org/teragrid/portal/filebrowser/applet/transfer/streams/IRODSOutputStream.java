package org.teragrid.portal.filebrowser.applet.transfer.streams;

import java.io.IOException;
import java.io.OutputStream;

import org.globus.ftp.FTPClient;
import org.globus.ftp.exception.ServerException;
import org.globus.io.streams.GlobusOutputStream;
import org.teragrid.portal.filebrowser.applet.transfer.Irods;
import org.teragrid.portal.filebrowser.applet.util.LogManager;

public class IRODSOutputStream extends GlobusOutputStream {
    protected OutputStream out;

    protected FTPClient ftpClient;
    
    /**
     * Opens ouput stream to SRB
     * 
     * @param file name of the file on the remote side.
     * @throws IOException 
     *  
     */
    public IRODSOutputStream(FTPClient ftpClient, String file) throws IOException {
        this.ftpClient = ftpClient;
        this.out = ((Irods)ftpClient).getOutputStream(file);
    }

    /* (non-Javadoc)
     * @see org.globus.io.streams.GlobusOutputStream#abort()
     */
    @Override
    public void abort() {
        try {
            this.out.close();
        } catch (Exception e) {
            LogManager.error("Failed to abort IRODSOutputStream",e);
        }
    }

    /* (non-Javadoc)
     * @see java.io.OutputStream#close()
     */
    @Override
    public void close() throws IOException {
        this.out.close();
    }

    /* (non-Javadoc)
     * @see java.io.OutputStream#flush()
     */
    @Override
    public void flush() throws IOException {
        this.out.flush();
    }

    /* (non-Javadoc)
     * @see java.io.OutputStream#write(byte[], int, int)
     */
    @Override
    public void write(byte[] b, int off, int len) throws IOException {
       
        this.out.write(b,off,len);
    }

    /* (non-Javadoc)
     * @see java.io.OutputStream#write(byte[])
     */
    @Override
    public void write(byte[] b) throws IOException {
        this.out.write(b);
    }
    
    /* (non-Javadoc)
     * @see java.io.OutputStream#write(int)
     */
    @Override
    public void write(final int b) throws IOException {
		this.out.write(b);
	}
}