package edu.utexas.tacc.wcs.filemanager.common.model;



/**
 * @author Rion Dooley < dooley [at] tacc [dot] utexas [dot] edu >
 *
 * Class to handle the system account information 
 *
 */
public class System implements Comparable<System>{

    private Long id;
    private String resourceName ;
    private String userName;
    private String resourceId;
    private String status;
    private String ftpHostname;
    private String sshHostname;
    private String institution;
    private String userState;
    private String type;
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
    public String getSSHHostname() {
        return sshHostname;
    }
    
    /**
     * @param ftpHostname the ftpHostname to set
     */
    public void setSSHHostname(String sshHostname) {
        this.sshHostname = sshHostname;
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
	public String getFTPHostname() {
        return ftpHostname;
    }
    
    /**
     * @param hostname the hostname to set
     */
    public void setFTPHostname(String ftpHostname) {
        this.ftpHostname = ftpHostname;
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
    public String getType() {
        return type;
    }
    
    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }
    
    public int compareTo(System o) {
        return resourceName.compareTo(o.resourceName);
    }
    
    public boolean equals(Object o) {
        if (o instanceof System) {
            return resourceName.equals(((System)o).resourceName) &&
                userName.equals(((System)o).userName) &&
                userId.equals(((System)o).userId);
        }
        
        return false;
    }
    
    public String toString() {
        return "[" + this.resourceName + ", " + userName + ", " + ftpHostname + ", " + sshHostname + ", " + type + ", " + institution + ", " + userState + "]";
    }
//    
//    public FTPSettings toFtpSetting() {
//        FTPSettings ftpSetting = new FTPSettings(this.resourceName);
//        ftpSetting.name = this.resourceName;
//        ftpSetting.host = this.ftpHostname;
//        ftpSetting.sshHost = this.sshHostname;
//        ftpSetting.resourceId = this.resourceId;
//        ftpSetting.port = 2811;
//        ftpSetting.type = FTPType.GRIDFTP;
//        ftpSetting.passiveMode = true;
//        ftpSetting.connRetry = 2;
//        ftpSetting.connDelay = 0;
//        ftpSetting.connParallel = 1;
//        ftpSetting.connMaxNum = 2;
//        ftpSetting.loginMode = FTPLogin.LOGIN_USEPROXYINIT;
//        ftpSetting.hostType = this.type;
//        ftpSetting.listed = true;
//        ftpSetting.userName = "";
//        ftpSetting.password = "";
//        
//        return ftpSetting;
//    }
        
}