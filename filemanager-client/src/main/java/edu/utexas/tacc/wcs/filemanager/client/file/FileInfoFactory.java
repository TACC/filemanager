/* 
 * Created on Aug 23, 2008
 * 
 * Developed by: Rion Dooley - dooley [at] tacc [dot] utexas [dot] edu
 * 				 TACC, Texas Advanced Computing Center
 * 
 * https://www.tacc.utexas.edu/
 */

package edu.utexas.tacc.wcs.filemanager.client.file;

import java.io.File;

import org.globus.ftp.FileInfo;
import org.irods.jargon.core.pub.io.IRODSFile;
import org.irods.jargon.core.query.CollectionAndDataObjectListingEntry;
import org.jets3t.service.model.S3Bucket;
import org.jets3t.service.model.S3Object;

/**
 * Insert Template description here.
 * 
 * @author Rion Dooley < dooley [at] tacc [dot] utexas [dot] edu >
 *
 */
public class FileInfoFactory {

    public static GenericFileInfo newFile(Object obj) {
        if (obj instanceof IRODSFile) {
            return new IRODSFileInfo((CollectionAndDataObjectListingEntry)obj);
        } else if (obj instanceof S3Object) {
            return new S3FileInfo((S3Object)obj);
        } else if (obj instanceof S3Bucket) {
            return new S3FileInfo((S3Bucket)obj);
        } else if (obj instanceof FileInfo) {
            return new GlobusFileInfo((FileInfo) obj);
        } else if (obj instanceof File) {
            return new LocalFileInfo((File)obj);
        } else
            return null;
    }
    
    public static IRODSFileInfo getIRODSFileInfo(CollectionAndDataObjectListingEntry obj, String username) {
        return new IRODSFileInfo(obj, username);
    }
    
    public static IRODSFileInfo getIRODSFileInfo(CollectionAndDataObjectListingEntry obj) {
        return new IRODSFileInfo(obj);
    }
    public static S3FileInfo getS3FileInfo(S3Object obj) {
        return new S3FileInfo(obj);
    }
    public static S3FileInfo getS3FileInfo(S3Bucket obj) {
        return new S3FileInfo(obj);
    }
    public static GlobusFileInfo getGlobusFileInfo(FileInfo obj) {
        return new GlobusFileInfo(obj);
    }
    public static LocalFileInfo getGlobusFileInfo(File obj) {
        return new LocalFileInfo(obj);
    }
}
