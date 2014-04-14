package edu.utexas.tacc.wcs.filemanager.common.model;

import java.util.Calendar;

import org.globus.ftp.FileInfo;

public class Transfer implements Comparable<Transfer>
{
	protected Long id = null;
	protected String epr = "";
	protected String dn = "";
	protected int notified = 0;
	protected Calendar created;
	protected Calendar start;
	protected Calendar stop;
	protected int para;
	protected int paraId;
	protected long speed;
	protected int progress;
	
	// FileInfo fields
	protected FileInfo file; //the file to transfer
    protected String fileName;
	protected String fileDate;
	protected byte fileType;
	protected long fileSize;
	protected String fileTime;
	protected String task = "";
	protected int status = Task.WAITING;
	protected boolean visible = true;
	protected String source = "file:///";
	protected String dest = "file:///";
	
	public Transfer(){}

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
	 * @return the epr
	 */
	public String getEpr() {
		return epr;
	}

	/**
	 * @param epr the epr to set
	 */
	public void setEpr(String epr) {
		this.epr = epr;
	}

	/**
	 * @return the dn
	 */
	public String getDn() {
		return dn;
	}

	/**
	 * @param dn the dn to set
	 */
	public void setDn(String dn) {
		this.dn = dn;
	}

	/**
	 * @return the notified
	 */
	public int getNotified() {
		return notified;
	}

	/**
	 * @param notified the notified to set
	 */
	public void setNotified(int notified) {
		this.notified = notified;
	}

	/**
	 * @return the created
	 */
	public Calendar getCreated() {
		return created;
	}

	/**
	 * @param created the created to set
	 */
	public void setCreated(Calendar created) {
		this.created = created;
	}

	/**
	 * @return the start
	 */
	public Calendar getStart() {
		return start;
	}

	/**
	 * @param start the start to set
	 */
	public void setStart(Calendar start) {
		this.start = start;
	}

	/**
	 * @return the stop
	 */
	public Calendar getStop() {
		return stop;
	}

	/**
	 * @param stop the stop to set
	 */
	public void setStop(Calendar stop) {
		this.stop = stop;
	}

	/**
	 * @return the para
	 */
	public int getPara() {
		return para;
	}

	/**
	 * @param para the para to set
	 */
	public void setPara(int para) {
		this.para = para;
	}

	/**
	 * @return the paraId
	 */
	public int getParaId() {
		return paraId;
	}

	/**
	 * @param paraId the paraId to set
	 */
	public void setParaId(int paraId) {
		this.paraId = paraId;
	}

	/**
	 * @return the speed
	 */
	public long getSpeed() {
		return speed;
	}

	/**
	 * @param speed the speed to set
	 */
	public void setSpeed(long speed) {
		this.speed = speed;
	}

	/**
	 * @return the progress
	 */
	public int getProgress() {
		return progress;
	}

	/**
	 * @param progress the progress to set
	 */
	public void setProgress(int progress) {
		this.progress = progress;
	}
	
	/**
     * Return the Fileinfo
     * @return FileInfo
     */
    public FileInfo getFile(){
        return this.file;
    }

    /**
     * Set the Fileinfo
     * @param file FileInfo
     */
    public void setFile(FileInfo file){
        this.file = file;
    }

  	/**
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * @param fileName the fileName to set
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * @return the fileDate
	 */
	public String getFileDate() {
		return fileDate;
	}

	/**
	 * @param fileDate the fileDate to set
	 */
	public void setFileDate(String fileDate) {
		this.fileDate = fileDate;
	}

	/**
	 * @return the fileType
	 */
	public byte getFileType() {
		return fileType;
	}

	/**
	 * @param fileType the fileType to set
	 */
	public void setFileType(byte fileType) {
		this.fileType = fileType;
	}

	/**
	 * @return the fileSize
	 */
	public long getFileSize() {
		return fileSize;
	}

	/**
	 * @param fileSize the fileSize to set
	 */
	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}

	/**
	 * @return the fileTime
	 */
	public String getFileTime() {
		return fileTime;
	}

	/**
	 * @param fileTime the fileTime to set
	 */
	public void setFileTime(String fileTime) {
		this.fileTime = fileTime;
	}

	/**
	 * @return the task
	 */
	public String getTask() {
		return task;
	}

	/**
	 * @param task the task to set
	 */
	public void setTask(String task) {
		this.task = task;
	}

	/**
	 * @return the status
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(int status) {
		this.status = status;
	}

	/**
	 * @return the visible
	 */
	public boolean isVisible() {
		return visible;
	}

	/**
	 * @param visible the visible to set
	 */
	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	/**
	 * @return the source
	 */
	public String getSource() {
		return source;
	}

	/**
	 * @param source the source to set
	 */
	public void setSource(String source) {
		this.source = source;
	}

	/**
	 * @return the dest
	 */
	public String getDest() {
		return dest;
	}

	/**
	 * @param dest the dest to set
	 */
	public void setDest(String dest) {
		this.dest = dest;
	}

	/**
	 * @param status
	 * @return
	 */
	public static String getStatusString(int status){
        String statusString = "";

        switch(status){
        case Task.DONE:
            statusString = "Done";
            break;
            case Task.FAILED:
                statusString = "Failed";
            break;
            case Task.ONGOING:
                statusString = "Ongoing";
                break;
            case Task.RESTARTABLE:
                statusString = "Restartable";
                break;
            case Task.WAITING:
                statusString = "Waiting";
                break;
            case Task.STOPPED:
                statusString = "Stopped";
                break;
        }

        return statusString;
    }
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Transfer)) return false;
        final Transfer t = (Transfer) o;
        if (!this.dn.equals(t.getDn())) return false;
        if (!this.epr.equals(t.getEpr())) return false;
        if (!this.source.equals(t.getSource())) return false;
        if (!this.dest.equals(t.getDest())) return false;
        return true;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return  "File transfer " + id + " from "  +
            source + " to " + dest + 
            " " + getStatusString(status);
    }
    
    /**
     * @param o
     * @return
     */
    public int compareTo(Transfer o) {
    	if (this.source.compareTo(((Transfer)o).getSource()) > 0 ||
                this.dest.compareTo(((Transfer)o).getDest()) > 0 ||
                this.dn.compareTo(((Transfer)o).getDn()) > 0 ||
                this.status > ((Transfer)o).getStatus() ||
                this.epr.compareTo(((Transfer)o).getEpr()) > 0) {
            return 1;
        }
    	
    	return 0;
    }
    
    /**
     * @return the subject
     */
    public String getSubject() {
        String subject = "File Notification";
        if (getStatus() == Task.ONGOING) {
            subject = "Transfer of " + getFileName() + " has begun.";
        } else if (getStatus() == Task.DONE) {
            subject = "Transfer of " + getFileName() + " has finished.";
        } else if (getStatus() == Task.FAILED) {
            subject = "Transfer of " + getFileName() + " has failed.";
        }
        
        return subject;
    }
	
}
