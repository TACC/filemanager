package edu.utexas.tacc.wcs.filemanager.common.model;

import org.apache.commons.lang3.StringUtils;

import edu.utexas.tacc.wcs.filemanager.common.model.enumeration.FileProtocolType;
import edu.utexas.tacc.wcs.filemanager.common.model.enumeration.SystemType;

/**
 * @author Rion Dooley < dooley [at] tacc [dot] utexas [dot] edu >
 *
 * Class to handle the system account information 
 *
 */
public class System implements Comparable<System>
{
    private Long id;
    private String resourceName ;
    private String userName;
    private String resourceId;
    private String status;
    private String ftpHostname;
    private Integer ftpPort;
    private String sshHostname;
    private Integer sshPort;
    private String institution;
    private String userState;
    private SystemType systemType = SystemType.HPC;
    private FileProtocolType protocol = FileProtocolType.GRIDFTP;
    private Long userId;
    
    public System(){}
    
    
    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }
    
    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }
    
    /**
     * @return the userId
     */
    public Long getUserId() {
        return userId;
    }
    
    /**
     * @param userId the userId to set
     */
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    /**
     * @return Returns the userName.
     */
    public String getUserName() {
            return userName;
    }
    
    /**
     * @param userName The user name to set.
     */
    public void setUserName(String userName) {
            this.userName = userName;
    }
    
    /**
     * @return Returns the resourceName.
     */
    public String getResourceName() {
        return resourceName;
    }
    
    /**
     * @param resourceName The resource name to set.
     */
    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }
    
    /**
     * @return the userState
     */
    public String getUserState() {
        return userState;
    }
    
    /**
     * @return the ftpHostname
     */
    public String getSshHostname() {
        return sshHostname;
    }
    
    /**
     * @param ftpHostname the ftpHostname to set
     */
    public void setSshHostname(String sshHostname) {
        this.sshHostname = sshHostname;
    }
    
    public Integer getSshPort() {
		return sshPort;
	}


	public void setSshPort(Integer sshPort) {
		this.sshPort = sshPort;
	}


	/**
	 * @param resourceId the resourceId to set
	 */
	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @return the resourceId
	 */
	public String getResourceId() {
		return resourceId;
	}

	/**
     * @return the hostname
     */
	public String getFtpHostname() {
        return ftpHostname;
    }
    
    /**
     * @param hostname the hostname to set
     */
    public void setFtpHostname(String ftpHostname) {
        this.ftpHostname = ftpHostname;
    }
    
    public Integer getFtpPort() {
		return ftpPort;
	}


	public void setFtpPort(Integer ftpPort) {
		this.ftpPort = ftpPort;
	}


	/**
     * @return the institution
     */
    public String getInstitution() {
        return institution;
    }
    
    /**
     * @param institution the institution to set
     */
    public void setInstitution(String institution) {
        this.institution = institution;
    }
    
    /**
     * @param userState the userState to set
     */
    public void setUserState(String userState) {
        this.userState = userState;
    }
    
    /**
     * @return the type
     */
    public SystemType getSystemType() {
        return systemType;
    }
    
    /**
     * @param type the type to set
     */
    public void setSystemType(SystemType systemType) {
        this.systemType = systemType;
    }
    
    public FileProtocolType getProtocol() {
		return protocol;
	}


	public void setProtocol(FileProtocolType protocol) {
		this.protocol = protocol;
	}


	public int compareTo(System o) {
        return resourceName.compareTo(o.resourceName);
    }
    
    public boolean equals(System o) {
        return resourceId.equals(o.resourceId) &&
                userName.equals(o.userName) &&
                userId.equals(o.userId);
    }
    
    public String toString() {
        return String.format("%s (%s)", StringUtils.isEmpty(this.resourceName) ? this.resourceId : this.resourceName,  this.systemType.name());
//        "[" + this.resourceName + ", " + userName + ", " + ftpHostname + ", " + sshHostname + ", " + type + ", " + institution + ", " + userState + "]";
    }       
}