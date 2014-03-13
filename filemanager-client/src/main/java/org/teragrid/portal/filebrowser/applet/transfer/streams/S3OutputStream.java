package org.teragrid.portal.filebrowser.applet.transfer.streams;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;

import org.globus.ftp.FTPClient;
import org.globus.ftp.exception.ServerException;
import org.globus.io.streams.GlobusOutputStream;
import org.teragrid.portal.filebrowser.applet.transfer.S3;
import org.teragrid.portal.filebrowser.applet.util.LogManager;

public class S3OutputStream extends GlobusOutputStream {

    protected OutputStream out;

    protected FTPClient ftpClient;
    
    protected HttpURLConnection connection;
    /**
     * Opens ouput stream to S3
     * @param file name of the file on the remote side.
     * @param length total size of the data to be transfered.
     *               Use -1 if unknown. The data then will be
     *               transfered in chunks.
     * @throws IOException 
     * @throws MalformedURLException 
     */
    public S3OutputStream(FTPClient ftpClient, String file, long length) throws MalformedURLException, IOException {
        this.ftpClient = ftpClient;
        this.connection = ((S3)ftpClient).getOutputStream(file, length);
        this.out = connection.getOutputStream();
    }

    /* (non-Javadoc)
     * @see org.globus.io.streams.GlobusOutputStream#abort()
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
     * @see org.globus.io.streams.GlobusOutputStream#write(int)
     */
    @Override
    public void write(int b) throws IOException {
        
        this.out.write(b);
    }

    /* (non-Javadoc)
     * @see java.io.OutputStream#close()
     */
    @Override
    public void close() throws IOException {
        try {
            this.connection.getResponseMessage();
            this.ftpClient.close();
        } catch (ServerException e) {
            LogManager.error("Failed to close S3InputStream",e);
        }
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
    
}