/* 
 * Created on July 24, 2012
 * 
 * Developed by: Rion Dooley - dooley [at] tacc [dot] utexas [dot] edu
 * 				 TACC, Texas Advanced Computing Center
 * 
 * https://www.tacc.utexas.edu/
 */

package org.teragrid.portal.filebrowser.applet.file;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.globus.ftp.FileInfo;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.protovalues.FilePermissionEnum;
import org.irods.jargon.core.protovalues.UserTypeEnum;
import org.irods.jargon.core.pub.domain.UserFilePermission;
import org.irods.jargon.core.query.CollectionAndDataObjectListingEntry;
import org.teragrid.portal.filebrowser.applet.transfer.Irods;

/**
 * Extension class to map S3 ACL's to unix file permissions. In
 * this situation, the 'execute' bit represents the ability to 
 * view and edit the ACL of the S3Object/S3Bucket.  
 * 
 * @author Rion Dooley < dooley [at] tacc [dot] utexas [dot] edu >
 *
 */
public class IRODSFileInfo extends FileInfo implements GenericFileInfo{
    private static final Logger log = Logger.getLogger(IRODSFileInfo.class);
    private int mode = 0;
    
    private String userName = "";
    private String resource = "";
    private Irods client = null;
    private List<UserFilePermission> permissions = new ArrayList<UserFilePermission>();
    private String absolutePath = "";
    private String queryUsername = "";
    
    public IRODSFileInfo() {
        super();
    }
    
    public IRODSFileInfo(CollectionAndDataObjectListingEntry file, String username) {
    	this();
        setAbsolutePath(file.getParentPath() + "/" + file.getPathOrName());
        setName(file.getNodeLabelDisplayValue());
        setSize(file.getDataSize());
        setFileType(file.isCollection() ? DIRECTORY_TYPE : FILE_TYPE);
        setUserName(file.getOwnerName());
        setQueryUsername(username);
        setPermissions(file.getUserFilePermission());
        
        Date date = file.getModifiedAt();
        setDate(new SimpleDateFormat("MMM dd, yyyy").format(date));
        setTime(new SimpleDateFormat("HH:mm").format(date));
    }
    
    public IRODSFileInfo(CollectionAndDataObjectListingEntry file) {
        this(file, file.getOwnerName());
    }
    
    public IRODSFileInfo(String name, long size, Date date, byte fileType, boolean owner) {
        this();
        
        setName(name);
        setSize(size);
        
        setDate(new SimpleDateFormat("MMM dd").format(date));
        setTime(new SimpleDateFormat("HH:mm").format(date));
        
        setFileType(fileType);
        
        this.permissions = new ArrayList<UserFilePermission>();
        
        setMode(owner?700:744);
    }
    
    public boolean isOwner() {
    	return userHasPermission(getQueryUsername(), FilePermissionEnum.OWN);
    }
    
    public void setMode(int mode) {
        this.mode = mode;
    }
    
    @SuppressWarnings("unchecked")
	public void setPermissions(Object permissionObject) {
        //System.out.println(getUserName() + " " + getQueryUsername());
    	String token = "---------";
    	
    	if (permissionObject == null || ((List<UserFilePermission>)permissionObject).isEmpty())
    	{
    		
    		if (getUserName().equals(queryUsername)) {
    			token = "rwx------";
    		} else {
    			token = "rwxr-----";
    		}
    	}
    	else
    	{ 
    		this.permissions = (List<UserFilePermission>)permissionObject;
    		
    		int groupMode = 0;
    		String ownerPems = "---";//getUserName().equals(getQueryUsername()) ? "rwx" : "---";
    		String groupPems = "---";
    		String otherPems = "---";
    		
	        for(UserFilePermission userPem: (List<UserFilePermission>)permissionObject) {
	        	if (userPem.getUserName().equals(getUserName())) {
	        		//if (getUserName().equals(getQueryUsername())) {
	        		if (userPem.getFilePermissionEnum().equals(FilePermissionEnum.READ)) {
	        			ownerPems = "r--";
	        		} else if (userPem.getFilePermissionEnum().equals(FilePermissionEnum.WRITE)) {
	 	               ownerPems = "-w-";
	        		} else if (userPem.getFilePermissionEnum().equals(FilePermissionEnum.EXECUTE)) {
	 	               ownerPems = "--x";
	        		} else if (userPem.getFilePermissionEnum().equals(FilePermissionEnum.OWN)) {
	 	               ownerPems = "rwx";
	        		}
//	        		} else {
//	        			groupPems = "rwx";
//	        			//break;
//	        		}
	        	} else {
	        		if (userPem.getFilePermissionEnum().equals(FilePermissionEnum.READ)) {
	                    if (groupMode < 4) {
	                    	groupMode = 4;
	                    	groupPems = "r--";
	                    }
	                } else if (userPem.getFilePermissionEnum().equals(FilePermissionEnum.WRITE)) {
	                	if (groupMode < 6) {
	                		groupMode = 6;
	                		groupPems = "rw-";
	                	}
	                } else if (userPem.getFilePermissionEnum().equals(FilePermissionEnum.EXECUTE)) {
	                	if (groupMode < 1) {
	                		groupMode = 1;
	                		groupPems = "--x";
	                	}
	                } else if (userPem.getFilePermissionEnum().equals(FilePermissionEnum.OWN)) {
	                	if (groupMode < 7) {
	                		groupMode = 7;
	                		groupPems = "rwx";
	                		//break;
	                	}
	                }
	        	}
	        }
	        token  = ownerPems + groupPems + otherPems;
    	} 
    	
    	token = (isDirectory() ? "d" : "-") + token;
    	
    	for(int i=1;i<=9;i++) {
            if (token.charAt(i) != '-') {
                this.mode += 1 << (9 - i);
            }
        }
    	//System.out.println(getName() + " " + mode);
    }
    
    public Object getPermissions() {
    	if (this.permissions == null || this.permissions.isEmpty()) {
    		
    		if (getUserName().equals(queryUsername)) {
    			this.permissions = Arrays.asList(new UserFilePermission(getUserName(), 
												getUserName(), 
												FilePermissionEnum.OWN, 
												UserTypeEnum.RODS_USER, ""));
			} else {
				this.permissions = Arrays.asList(
					new UserFilePermission(getUserName(), 
						getUserName(), 
						FilePermissionEnum.OWN, 
						UserTypeEnum.RODS_USER, ""),
					new UserFilePermission(getUserName(), 
						getUserName(), 
						FilePermissionEnum.READ, 
						UserTypeEnum.RODS_USER, ""));
			}
    	} 
    	
    	return this.permissions;
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
    	return userHasPermission(getQueryUsername(), FilePermissionEnum.READ);
    }
    
    public boolean userCanWrite() {
    	return userHasPermission(getQueryUsername(), FilePermissionEnum.WRITE);
    }

    public boolean userCanExecute() {
    	return userHasPermission(getQueryUsername(), FilePermissionEnum.EXECUTE);
    }
    
    private boolean userHasPermission(String username, FilePermissionEnum permissionEnum) 
    {
    	for(UserFilePermission pem: this.permissions) {
			if (pem.getUserName().equals(username)) {
				if (pem.getFilePermissionEnum().equals(permissionEnum)) {
					return true;
				}
			}
		}
    	
    	return false;
    }

    

    public boolean groupCanRead() {
    	int mode = (int) Math.floor((this.mode%100)/10);
    	return (mode >= 4);
    }

    public boolean groupCanWrite() {
    	int mode = (int) Math.floor((this.mode%100)/10);
    	return (mode == 2 || mode == 6 || mode == 7);
  	}

    public boolean groupCanExecute() {
    	int mode = (int) Math.floor((this.mode%100)/10);
    	return (mode == 1 || mode == 3 || mode == 5 || mode == 7);
    }

    public boolean allCanRead() {
    	return false;
    }

    public boolean allCanWrite() {
    	return false;
    }

    public boolean allCanExecute() {
    	return false;
    }

    /**
     * @return the userName
     */
    public String getUserName() {
        return userName;
    }

    /**
     * @param userName the userName to set
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

	/**
	 * @param absolutePath the absolutePath to set
	 */
	public void setAbsolutePath(String absolutePath)
	{
		this.absolutePath = absolutePath;
	}

	/**
	 * @return the absolutePath
	 */
	public String getAbsolutePath()
	{
		return absolutePath;
	}

	/**
	 * @param queryUsername the queryUsername to set
	 */
	public void setQueryUsername(String queryUsername)
	{
		this.queryUsername = queryUsername;
	}

	/**
	 * @return the queryUsername
	 */
	public String getQueryUsername()
	{
		return queryUsername;
	}      
}