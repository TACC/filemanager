/* 
 * Created on Aug 6, 2008
 * 
 * Developed by: Rion Dooley - dooley [at] tacc [dot] utexas [dot] edu
 * 				 TACC, Texas Advanced Computing Center
 * 
 * https://www.tacc.utexas.edu/
 */

package org.teragrid.portal.filebrowser.applet.file;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

import org.globus.ftp.FileInfo;
import org.jets3t.service.acl.AccessControlList;
import org.jets3t.service.acl.GrantAndPermission;
import org.jets3t.service.acl.GroupGrantee;
import org.jets3t.service.acl.Permission;
import org.jets3t.service.model.S3Bucket;
import org.jets3t.service.model.S3Object;

/**
 * Extension class to map S3 ACL's to unix file permissions. In
 * this situation, the 'execute' bit represents the ability to 
 * view and edit the ACL of the S3Object/S3Bucket.  
 * 
 * @author Rion Dooley < dooley [at] tacc [dot] utexas [dot] edu >
 *
 */
public class S3FileInfo extends FileInfo implements GenericFileInfo {
    
    private int mode = 0;
    
    private AccessControlList acl = null;
    
    public S3FileInfo() {
        super();
    }
    
    public S3FileInfo(S3Bucket bucket) {
        this(bucket,true);
    }
    
    public S3FileInfo(S3Bucket bucket, boolean owner) {
        this(bucket.getName(), 0, bucket.getCreationDate(),DIRECTORY_TYPE,owner);
    }
    
    public S3FileInfo(S3Object s3Object) {
        this(s3Object,true);
    }
    
    public S3FileInfo(S3Object s3Object, boolean owner) {
        this(s3Object.getKey(), s3Object.getContentLength(),s3Object.getLastModifiedDate(),FILE_TYPE,owner);
    }
    
    public S3FileInfo(String name, long size, Date date, byte fileType) {
        this(name,size,date,fileType,true);
    }
    
    public S3FileInfo(String name, long size, Date date, byte fileType, boolean owner) {
        this();
        setName(name);
        setSize(size);
        setDate(new SimpleDateFormat("MMM dd").format(date));
        setTime(new SimpleDateFormat("HH:mm").format(date));
        setFileType(fileType);
        setMode(owner?700:744);
    }
    
    public void setMode(int mode) {
        this.mode = mode;
    }
    
    @SuppressWarnings("unchecked")
	public void setPermissions(Object permissionObject) {
        
        AccessControlList acl = (AccessControlList)permissionObject;
        
        this.acl = acl;
        
        this.mode = 0;
        
        for (GrantAndPermission gap : (Set<GrantAndPermission>)acl.getGrants()) {
            if (gap.getGrantee().equals(GroupGrantee.ALL_USERS)) {
                // everyone has total access
                if (gap.getPermission().equals(Permission.PERMISSION_FULL_CONTROL)) {
                    this.mode = 777;
                    return;
                } else if (gap.getPermission().equals(Permission.PERMISSION_READ)) {
                    if (((mode & (1 << 8)) == 0)) mode += (1<<8);
                    if (((mode & (1 << 5)) == 0)) mode += (1<<5);
                    if (((mode & (1 << 2)) == 0)) mode += (1<<2);
                } else if (gap.getPermission().equals(Permission.PERMISSION_WRITE)) {
                    if (((mode & (1 << 7)) == 0)) mode += (1<<7);
                    if (((mode & (1 << 4)) == 0)) mode += (1<<5);
                    if (((mode & (1 << 1)) == 0)) mode += (1<<1);
                } else if (gap.getPermission().equals(Permission.PERMISSION_READ_ACP) || 
                        gap.getPermission().equals(Permission.PERMISSION_WRITE_ACP)) {
                    if (((mode & (1 << 6)) == 0)) mode += (1<<6);
                    if (((mode & (1 << 3)) == 0)) mode += (1<<3);
                    if (((mode & (1 << 0)) == 0)) mode += (1<<0);
                }
                
            } else {
                // this is the owner and the current user
                if (gap.getPermission().equals(Permission.PERMISSION_FULL_CONTROL)) {
                    if (acl.getOwner().getId().equals(gap.getGrantee().getIdentifier())) {
                        if (((mode & (1 << 9)) == 0)) mode += (1<<9);
                        if (((mode & (1 << 8)) == 0)) mode += (1<<8);
                        if (((mode & (1 << 7)) == 0)) mode += (1<<7);
                    } else {
                        if (((mode & (1 << 6)) == 0)) mode += (1<<6);
                        if (((mode & (1 << 5)) == 0)) mode += (1<<5);
                        if (((mode & (1 << 4)) == 0)) mode += (1<<4);
                    }
                } else if (gap.getPermission().equals(Permission.PERMISSION_READ)) {
                    if (acl.getOwner().getDisplayName().equals(gap.getGrantee().getIdentifier())) {
                        if (((mode & (1 << 9)) == 0)) mode += (1<<9);
                    } else {
                        if (((mode & (1 << 6)) == 0)) mode += (1<<6);
                    } 
                } else if (gap.getPermission().equals(Permission.PERMISSION_WRITE)) {
                    if (acl.getOwner().getDisplayName().equals(gap.getGrantee().getIdentifier())) {
                        if (((mode & (1 << 8)) == 0)) mode += (1<<8);
                    } else {
                        if (((mode & (1 << 5)) == 0)) mode += (1<<5);
                    }
                } else if (gap.getPermission().equals(Permission.PERMISSION_READ_ACP) || 
                        gap.getPermission().equals(Permission.PERMISSION_WRITE_ACP)) {
                    if (acl.getOwner().getDisplayName().equals(gap.getGrantee().getIdentifier())) {
                        if (((mode & (1 << 7)) == 0)) mode += (1<<7);
                    } else {
                        if (((mode & (1 << 4)) == 0)) mode += (1<<4);
                    }
                }
            } 
        }
    }
    
    public Object getPermissions() {
        return this.acl;
    }
    
    public int getMode() {
        return mode;
      }

      public String getModeAsString() {
        StringBuffer modeStr = new StringBuffer();
        for(int j=2;j>=0;j--) {
            int oct = 0;
            for(int i=2;i>=0;i--) {
                if ((mode & (1 << j*3+i)) != 0) {
                    oct += (int)Math.pow(2,i);
                }
            }
            modeStr.append(String.valueOf(oct));
        }
        return modeStr.toString();
      }

      public boolean userCanRead() {
        return ((mode & (1 << 8)) != 0);
      }

      public boolean userCanWrite() {
        return ((mode & (1 << 7)) != 0);
      }

      public boolean userCanExecute() {
        return ((mode & (1 << 6)) != 0);
      }

      public boolean groupCanRead() {
        return ((mode & (1 << 5)) != 0);
      }

      public boolean groupCanWrite() {
        return ((mode & (1 << 4)) != 0);
      }

      public boolean groupCanExecute() {
        return ((mode & (1 << 3)) != 0);
      }

      public boolean allCanRead() {
        return ((mode & (1 << 2)) != 0);
      }

      public boolean allCanWrite() {
        return ((mode & (1 << 1)) != 0);
      }

      public boolean allCanExecute() {
        return ((mode & (1 << 0)) != 0);
      }
}