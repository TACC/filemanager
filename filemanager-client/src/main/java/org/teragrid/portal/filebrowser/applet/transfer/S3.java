/* 
 * Created on Jul 23, 2008
 * 
 * Developed by: Rion Dooley - dooley [at] tacc [dot] utexas [dot] edu
 * 				 TACC, Texas Advanced Computing Center
 * 
 * https://www.tacc.utexas.edu/
 */

package org.teragrid.portal.filebrowser.applet.transfer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import net.spy.s3.AWSAuthConnection;
import net.spy.s3.Utils;

import org.globus.ftp.FTPClient;
import org.globus.ftp.FileInfo;
import org.globus.ftp.HostPort;
import org.globus.ftp.exception.ClientException;
import org.globus.ftp.exception.ServerException;
import org.jets3t.service.S3Service;
import org.jets3t.service.S3ServiceException;
import org.jets3t.service.acl.AccessControlList;
import org.jets3t.service.impl.rest.httpclient.RestS3Service;
import org.jets3t.service.model.S3Bucket;
import org.jets3t.service.model.S3Object;
import org.jets3t.service.security.AWSCredentials;
import org.teragrid.portal.filebrowser.applet.file.S3FileInfo;
import org.teragrid.portal.filebrowser.applet.util.LogManager;

/**
 * Adaptor class to interface with Amazon S3
 * 
 * @author Rion Dooley < dooley [at] tacc [dot] utexas [dot] edu >
 *
 */
public class S3 extends FTPClient {
    
    public static String Prefix = "http://";

    private AWSAuthConnection connection;
    
    private S3Service service;
    
    private S3Bucket currentBucket = null;
    
    private List<S3Bucket> bucketList = new ArrayList<S3Bucket>();
    
    /**
     * @param awsAccessKeyId
     * @param awsSecretAccessKey
     * @throws IOException
     * @throws ServerException
     */
    public S3(String awsAccessKeyId, String awsSecretAccessKey) throws IOException, ServerException {
        authorize(awsAccessKeyId,awsSecretAccessKey);
    }

//    public S3() {}

    /* (non-Javadoc)
     * @see org.globus.ftp.FTPClient#abort()
     */
    @Override
    public void abort() throws IOException, ServerException {
        connection.kill();
    }

    public void setType(int type) throws IOException, ServerException {}
    public HostPort setPassive() throws IOException, ServerException {return null;}
    public void setActive(HostPort hostPort){}
    public void setActive(){}
    public void setLocalActive() throws ClientException, IOException {}
    public HostPort setLocalPassive() throws IOException {return null;}
    public HostPort setLocalPassive(int port, int queue) throws IOException {return null;}
    
    /* 
     * Establish a connection to amazon s3 under the user's account.
     * 
     * @param awsAccessKeyId the S3 account key
     * @param awsSecretAccessKey the s3 account secret key 
     */
    public void authorize(String awsAccessKeyId, String awsSecretAccessKey) throws IOException,
            ServerException {
        
        try {
            service = new RestS3Service(new AWSCredentials(awsAccessKeyId,awsSecretAccessKey));
        } catch (S3ServiceException e1) {
            e1.printStackTrace();
        }
        
        connection = new AWSAuthConnection(awsAccessKeyId,awsSecretAccessKey,false);
        
        try {
            
//            bucketList = connection.listAllMyBuckets(null).entries;
            
            bucketList = java.util.Arrays.asList(service.listAllBuckets());
        	
            // permissions area all screwed up and there's not way to get your "friendly name"
            // aside from the ACL of an object or bucket.  Since you may not be the owner
            // of the bucket or object, the only sure way is to create one and pull the 
            // ACL to which you will be the only entry.
            String tempBucketName = "deleteme.tmp" + System.currentTimeMillis();
            S3Bucket tmpBucket = service.createBucket(tempBucketName);
            AccessControlList acl = service.getBucketAcl(tmpBucket);
            this.username = acl.getOwner().getDisplayName();
            service.deleteBucket(tmpBucket);
            
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServerException(0,"Failed to authenticate to S3: " + e.getMessage());
        } 
    }

    /* 
     * Close the connection to s3. Since HTTPUrlConnection are used, this is 
     * empty.
     */
    @Override
    public void close() throws IOException, ServerException {
//        connection.kill();
    }

    /* 
     * Delete the bucket named 'dir'
     * @param bucket bucket to delete
     */
    @Override
    public void deleteDir(String bucket) throws IOException, ServerException {
        try {
//            connection.deleteBucket(bucket, null);
            service.deleteBucket(bucket);
        } catch (Exception e) {
            throw new ServerException(0,"Failed to delete file: " + e.getMessage());
        }
    }

    /* 
     * Delete the file with key equal to the filename in the current bucket
     * @param filename
     */
    @Override
    public void deleteFile(String filename) throws IOException, ServerException {
        try {
//            connection.delete(currentBucket.name, filename, null);
            service.deleteObject(currentBucket, filename);
        } catch (Exception e) {
            throw new ServerException(0,"Failed to delete file: " + e.getMessage());
        }
    }

    public void copyFile(String srcFilename, String destFilename) throws IOException, ServerException {
            LogManager.debug("from: " + srcFilename + " to: " + destFilename);
            
            try {
                changeDir(srcFilename.substring(0,srcFilename.lastIndexOf("/")));
            } catch (ServerException e) {
                throw new IOException(e.getCustomMessage());
            }
            if (currentBucket == null) {
                
                throw new IOException("Cannot write to root directory of S3 drive. All data must be writen to a specific directory.");
            }
            
            if (destFilename.equals(srcFilename)) {
                return;
            }
            
            S3Bucket destBucket = getBucketFromPath(destFilename);
            if (destBucket == null) {    
                throw new ServerException(1,"Cannot write to root directory of S3 drive. All data must be writen to a specific directory.");
            }
            
            String relativeSrcFileName = srcFilename.substring(srcFilename.indexOf(currentBucket.getName()) + currentBucket.getName().length() + 1);
            String relativeDestFileName = destFilename.substring(destFilename.indexOf(destBucket.getName()) + destBucket.getName().length() + 1);
            connection.copy(currentBucket.getName(), relativeSrcFileName, destBucket.getName(), relativeDestFileName, null);
            
    }

    /* (non-Javadoc)
     * @see org.globus.ftp.FTPClient#get(java.lang.String, java.io.File)
     */
    @Override
    public void get(String remoteFileName, File localFile) throws IOException,
            ClientException, ServerException {
        InputStream in = getInputStream(remoteFileName);
        FileOutputStream out = new FileOutputStream(localFile);
        byte data[] = new byte[32145];
        while(in.read(data) >= 0) {
            out.write(data);
        }
        
        in.close();
        out.close();
    }

    public InputStream getInputStream(String remoteFileName) throws MalformedURLException, IOException {
        try {
            changeDir(remoteFileName.substring(0,remoteFileName.lastIndexOf("/")));
        } catch (ServerException e) {
            throw new IOException(e.getCustomMessage());
        }
        
        if (currentBucket == null) {
            
            throw new IOException("No files can be read or written from root directory of S3 drive.");
        }
        
        try {
            String relativeFileName = remoteFileName.substring(getCurrentDir().length() + 1);
            try {
            	return service.getObject(currentBucket, relativeFileName).getDataInputStream();
            } catch (S3ServiceException e) {
                throw new IOException(e.getMessage());
            }
//            return connection.getInputStream(currentBucket.getName(), relativeFileName, null);
        } catch (ServerException e) {
            throw new IOException(e.getCustomMessage());
        }
    }
    
    public HttpURLConnection getOutputStream(String remoteFileName, long length) throws MalformedURLException, IOException {
        try {
            changeDir(remoteFileName.substring(0,remoteFileName.lastIndexOf("/")));
        } catch (ServerException e) {
            throw new IOException(e.getCustomMessage());
        }
        if (currentBucket == null) {
            
            throw new IOException("Cannot write to root directory of S3 drive. All data must be writen to a specific directory.");
        }
        try {
            String relativeFileName = remoteFileName.substring(getCurrentDir().length() + 1);
            
            return connection.getOutputStream(currentBucket.getName(), relativeFileName, null, null, length);
        } catch (ServerException e) {
            throw new IOException(e.getCustomMessage());
        }
    }
    /*
     * The concept of buckets is not present in s3, rather we use
     * buckets to contain S3Objects. 
     * 
     * @return current bucket
     */
    @Override
    public String getCurrentDir() throws IOException, ServerException {
        return "/"+getUserName() + (currentBucket == null?"":"/" + currentBucket.getName());
    }

    /* 
     * Static host of amazon s3 service.
     */
    @Override
    public String getHost() {
        return Utils.DEFAULT_HOST;
    }

    /* 
     * Query s3 for the size of the remote file.
     * 
     * @param filename name of file in current bucket
     */
    @Override
    public long getSize(String filename) throws IOException, ServerException {
        try {
            return service.getObjectDetails(currentBucket, filename).getContentLength();
//            List<ListEntry> entries = connection.listBucket(getCurrentDir(), filename, null, null, null).entries;
//            
//            for (ListEntry entry: entries) {
//                if (entry.key.equals(filename)) {
//                    return entry.size;
//                }
//            }
//            return -1;
        } catch (Exception e) {
            throw new ServerException(0,"Failed to get size of s3 file: " + e.getMessage());
        }
    }

    /* 
     * Gets the user's s3 username
     */
    @Override
    public String getUserName() {
        
        return this.username;
        
    }

    /* (non-Javadoc)
     * @see org.globus.ftp.FTPClient#goUpDir()
     */
    @Override
    public void goUpDir() throws IOException, ServerException {
        
        changeDir(null);
    }

    
    /* 
     * s3 does not support directories, so the changedir command essentially
     * just switches buckets
     * @param dir directory to which to change
     */
    @Override
    public void changeDir(String dir) throws IOException, ServerException {
        
        currentBucket = getBucketFromPath(dir);
        
    }

    /* (non-Javadoc)
     * @see org.globus.ftp.FTPClient#list()
     */
    @Override
    public Vector<FileInfo> list() throws ServerException, ClientException, IOException {
        
        Vector<FileInfo> v = new Vector<FileInfo>();
        
        if (currentBucket == null) {
            
//            bucketList = connection.listAllMyBuckets(null).entries;
            try {
                bucketList = java.util.Arrays.asList(service.listAllBuckets());
            } catch (S3ServiceException e) {
                e.printStackTrace();
            }
            
            for (S3Bucket bucket: bucketList) {
                S3FileInfo file = new S3FileInfo(bucket,bucket.getOwner().getDisplayName().equals(username));
                v.add(file);
            }
        } else {
            try {
//                List<ListEntry> entries = connection.listBucket(currentBucket.getName(), null, null, null, null).entries;
                List<S3Object> entries = java.util.Arrays.asList(service.listObjects(currentBucket));
                for (S3Object entry: entries) {
                    S3FileInfo file = new S3FileInfo(entry,entry.getOwner().getDisplayName().equals(username));
                    v.add(file);
                }
            } catch (Exception e) {
                throw new ServerException(0,"Failed to list directory " + 
                        currentBucket.getName() + ": " + e.getMessage());
            }
        }
        
        return v;
    }

    /* (non-Javadoc)
     * @see org.globus.ftp.FTPClient#list(java.lang.String)
     */
    @Override
    public Vector<FileInfo> list(String filter) throws ServerException, ClientException,
            IOException {
        Vector<FileInfo> v = new Vector<FileInfo>();
        
        if (currentBucket == null) {
            return list();
        }
        try {
//            List<ListEntry> entries = connection.listBucket(currentBucket.getName(), null, null, null, null).entries;
            List<S3Object> entries = java.util.Arrays.asList(service.listObjects(currentBucket));
            for (S3Object entry: entries) {
                S3FileInfo file = new S3FileInfo(entry,entry.getOwner().getDisplayName().equals(username));
                v.add(file);
            }
            return v;
        } catch (Exception e) {
            throw new ServerException(0,"Failed to list directory " + 
                    currentBucket.getName() + ": " + e.getMessage());
        }
    }

    /* (non-Javadoc)
     * @see org.globus.ftp.FTPClient#makeDir(java.lang.String)
     */
    @Override
    public void makeDir(String dir) throws IOException, ServerException {
        try {
            
//            connection.createBucket(dir,null);
            service.createBucket(dir);
        } catch (Exception e) {
            throw new ServerException(0,"Failed to create the directory: " + e.getMessage());
        }
    }

    /* (non-Javadoc)
     * @see org.globus.ftp.FTPClient#put(java.io.File, java.lang.String, boolean)
     */
    @Override
    public void put(File localFile, String remoteFileName, boolean append)
            throws IOException, ServerException, ClientException {
        try {
            if (currentBucket == null) {
                changeDir(remoteFileName);
            }
            service.putObject(currentBucket, new S3Object(currentBucket,localFile));
        } catch (S3ServiceException e) {
            throw new IOException("Failed to store the file: " + e.getMessage());
        }
    }
    
    public void putDirectoryACL(AccessControlList acl, String bucketName)
    throws IOException {
        try {
            S3Bucket bucket = getBucketFromPath(bucketName);
            bucket.setAcl(acl);
            service.putBucketAcl(bucket);
        } catch (S3ServiceException e) {
            throw new IOException("Failed to updated bucket ACL.\n" + e.getMessage());
        }
    }
    
    public void putFileACL(AccessControlList acl, String remoteFile)
    throws IOException {
        try {
            S3Bucket bucket = getBucketFromPath(remoteFile);
            
            String relativeFileName = remoteFile.substring(1 + username.length() + 1 + bucket.getName().length() + 1);
            
            service.putObjectAcl(bucket.getName(), relativeFileName, acl);
            
        } catch (S3ServiceException e) {
            throw new IOException("Failed to updated object ACL." + e.getMessage());
        }
    }
    
    public boolean exists(String remoteFile) throws IOException {
    	 try {
             getSize(remoteFile);
             return true;
         } catch (Exception e) {
             return false;
         }
    }

    /* (non-Javadoc)
     * @see org.globus.ftp.FTPClient#rename(java.lang.String, java.lang.String)
     */
    @Override
    public void rename(String oldName, String newName) throws IOException,
            ServerException {
        connection.copy(currentBucket.getName(),oldName,currentBucket.getName(),newName,null);
        try {
            service.deleteObject(currentBucket.getName(),oldName);
        } catch (S3ServiceException e) {
           throw new IOException(e.getMessage());
        }
    }

    /* 
     * Get the size of the named file.  Defaults to getSize(filename)
     * @param name of the file to get the size of
     */
    @Override
    public long size(String filename) throws IOException, ServerException {
        return getSize(filename);
    }    
    
    public S3FileInfo getDetailedListing(String remoteFileName) throws IOException, ServerException, S3ServiceException {
        S3FileInfo file = null;
        
        if (currentBucket == null) {            
            if (remoteFileName.equals("..")) {
                file = new S3FileInfo("..",0,new Date(),S3FileInfo.DIRECTORY_TYPE);
            } else {
                file = new S3FileInfo(remoteFileName,0,new Date(),S3FileInfo.DIRECTORY_TYPE);
                file.setPermissions(service.getBucketAcl(remoteFileName));
            }
            
        } else {
            
            S3Object s3Object = service.getObject(currentBucket, remoteFileName);
            file = new S3FileInfo(s3Object);
            file.setPermissions(service.getObjectAcl(currentBucket, remoteFileName));
            
        }
        
        return file;
        
    }
    
    public S3Bucket getBucketFromPath(String path) {
        // s3 has a flat structure of an account with multiple buckets
        // we can only go up or into a bucket.  anything else throws an exception
        if (path == null) {
            return null;
        }
        
        // double periods are not allowed in bucket names, so this must be
        // an updir command.
        if (path.indexOf("..") > -1) {
            return null;
        }
        
        if (path.endsWith("/")) path = path.substring(0,path.lastIndexOf("/"));
        path = path.replaceFirst(getUserName(),"").replaceAll("/", "");
        
        if (path.equals("")) {;
            return null;
        }
        
        for (S3Bucket bucket: bucketList) {
            if (path.endsWith(bucket.getName())) {
                return bucket;
            }
        }
        
        return null;
    }
    
}
