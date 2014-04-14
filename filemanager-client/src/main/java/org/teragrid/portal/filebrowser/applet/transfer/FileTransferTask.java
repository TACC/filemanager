/*
 * Portions of this file Copyright 2004-2007 Shanghai Jiaotong University
 * 
 * This file or a portion of this file is licensed under the
 * terms of the Globus Toolkit Public License, found at
 * http://www.globus.org/toolkit/legal/4.0/
 * If you redistribute this file, with or without
 * modifications, you must include this notice in the file.
 */

package org.teragrid.portal.filebrowser.applet.transfer;

import java.text.MessageFormat;
import java.util.Calendar;

import org.globus.ftp.ByteRange;
import org.globus.ftp.FileInfo;
import org.teragrid.portal.filebrowser.applet.ui.DrawState;
import org.teragrid.portal.filebrowser.applet.ui.ListModel;
import org.teragrid.portal.filebrowser.applet.util.ResourceName;
import org.teragrid.portal.filebrowser.applet.util.SGGCResourceBundle;

import edu.utexas.tacc.wcs.filemanager.common.model.Task;
import edu.utexas.tacc.wcs.filemanager.common.model.Transfer;

public class FileTransferTask extends Transfer implements Task {
//    private int id = -1;
    private FileInfo file = null;//the file to transfer
    private FTPSettings srcSite = null;//source Site
    private FTPSettings destSite = null;//destination Site
//    private String source = null;//source directory
//    private String dest = null;//destination directory
//
    private ByteRange srcRange = null;//the source file range
    private long dstStartOffset = 0;//the destination file range
    private UrlCopy process = null;//UrlCopy process
//
//    private int status = Task.WAITING;//the status of the transfering
//    private int progress = 0;//progress:0~100
    private long totalTime = 0;//time elapsed(in millisecond)
    private long leftTime = 0;//time left(in millisecond)
    private long startTime = 0;// time transfer started
//    private long speed = 0;//speed(in bps)
//    
    private boolean resume = false;
    private String reName = null;
    private String displayName = "";
//    private int paraID = 1;
//    private int para = 1;
//    private long created = 0;
    
    //directory separator
    @SuppressWarnings("unused")
	private String SEPARTOR = "/";
    private boolean cancelled = false; 

    public FileTransferTask(Transfer transfer)
    {
    	this.id = transfer.getId();
		this.epr = transfer.getEpr();
		this.dn = transfer.getDn();
		this.dest = transfer.getDest();
		this.notified = transfer.getNotified();
		this.created = transfer.getCreated();
		this.start = transfer.getStart();
		this.startTime = start.getTimeInMillis();
		this.stop = transfer.getStop();
		this.para = transfer.getPara();
		this.paraId = transfer.getParaId();
		this.speed = transfer.getSpeed();
		this.progress = transfer.getProgress();
		this.fileName = transfer.getFileName();
		this.fileDate = transfer.getFileDate();
		this.fileType = transfer.getFileType();
		this.fileSize = transfer.getFileSize();
		this.fileTime = transfer.getFileTime();
		this.task = transfer.getTask();
		this.status = transfer.getStatus();
		this.visible = transfer.isVisible();
		this.source = transfer.getSource();
		this.dest = transfer.getDest();
    }
    
    public FileTransferTask(FileInfo file, FTPSettings srcSite, FTPSettings destSite, String srcDir, String destDir){
        this(file, srcSite, destSite,srcDir,destDir,new ByteRange(0, file.getSize()));
    }

    public FileTransferTask(FileInfo file, FTPSettings srcSite, FTPSettings destSite, String srcDir, String destDir, ByteRange srcRange){
    	this(file, srcSite, destSite,srcDir,destDir,srcRange, 0);
    }
    
    public FileTransferTask(FileInfo file, FTPSettings srcSite, FTPSettings destSite, String srcDir, String destDir, ByteRange srcRange, int dstStartOffset){
        this.file = file;
        this.srcSite = srcSite;
        this.destSite = destSite;
        this.source = srcDir;
        this.dest = destDir;
        this.displayName = file.getName();
        this.startTime = System.currentTimeMillis();
        this.srcRange = srcRange;
        this.dstStartOffset = dstStartOffset;
        this.created = Calendar.getInstance();
        
    }

    public void cancel(){
        if(null!=process) {
        	process.cancel();
        }
        this.cancelled = true;
        this.setStatus(Task.FAILED);
    }
    
    public void kill(){
        if(null!=process) {
            process.cancel();
        }
        this.cancelled = true;
        this.setStatus(Task.STOPPED);
    }
    
    public boolean isCancelled() {
    	return this.cancelled;
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
     * @return the startTime
     */
    public long getStartTime() {
        return startTime;
    }
    
    public String getStartTimeString() {
        return ListModel.getDate(startTime);
    }
    
    /**
     * @param startTime the startTime to set
     */
    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    /**
     * Return true if the tranfer object is a file, not a directory.
     * @return boolean
     */
    public boolean isFileTask(){
        return file.isFile();
    }

    /**
     * Return the transfer status
     * @return int
     * @todo Implement this cgftp.Task method
     */
    public int getStatus() {
        return this.status;
    }
    
    /**
     * Set the transfer status
     * @param status int
     * @throws IllegalArgumentException
     * @todo Implement this cgftp.Task method
     */
    public void setStatus(int status) throws IllegalArgumentException {
        if ((status != Task.WAITING) && (status != Task.ONGOING)
				&& (status != Task.DONE) && (status != Task.FAILED)
				&& (status != Task.RESTARTABLE)
				&& (status != Task.STOPPED)) {
        	String exceptionMsg = MessageFormat.format(SGGCResourceBundle.getResourceString(ResourceName.KEY_EXCEPTION_FILETRANSFERTASK_INVALIDSTATUS), 
        			new Object[] {new Integer(status)});
			throw new IllegalArgumentException(exceptionMsg);
		}

		this.status = status;
    }

    /**
	 * Return the source site
	 * 
	 * @return FTPSettings
	 */
    public FTPSettings getSrcSite(){
        return this.srcSite;
    }

    /**
     * Set the source site
     * @param srcSite FTPSettings
     */
    public void setSrcSite(FTPSettings srcSite){
        this.srcSite = srcSite;
    }

    /**
     * Return the destination site
     * @return FTPSettings
     */
    public FTPSettings getDestSite(){
        return this.destSite;
    }

    /**
     * Set the destination site
     * @param destSite FTPSettings
     */
    public void setDestSite(FTPSettings destSite){
        this.destSite = destSite;
    }

    /**
     * Return the source directory
     * @return String
     */
    public String getSrcDir(){
        return this.source;
    }

    /**
     * Set the source directory
     * @param srcDir String
     */
    public void setSrcDir(String srcDir){
        this.source = srcDir;
    }

    /**
     * Return the destination directory
     * @return String
     */
    public String getDestDir(){
        return this.dest;
    }

    /**
     * Set the destination directory
     * @param destDir String
     */
    public void setDestDir(String destDir){
        this.dest = destDir;
    }

    /**
     * Return the transfer range of the file
     * @return long
     */
    public ByteRange getSrcRange(){
        return this.srcRange;
    }

    /**
     * Set the transfer range of the file
     * @param startPoint long
     */
    public void setSrcRange(ByteRange range){
        this.srcRange = range;
    }

    /**
     * Return the transfer size of the file
     * @return long
     */
    public long getSize(){
        return srcRange.to - srcRange.from;
    }

    /**
     * Return the transfer progress of the file
     * @return int
     */
    public int getProgress(){
        return this.progress;
    }

    /**
     * Set the transfer progress of the file
     * @param progress int
     */
    public void setProgress(int progress){
        if(progress <=0 ){
            this.progress = 0;
        }else if(progress >= 100){
            this.progress = 100;
        }else{
            this.progress = progress;
        }
    }

    /**
     * Set the transfer process
     * @param process UrlCopy
     */
    public void setProcess(UrlCopy process){
        this.process=process;
    }

    /**
     * Get the time elapsed
     * @return String
     */
    public long getTotalTime(){
        return this.totalTime;
    }

    public String getTotalTimeString(){
        return ListModel.getTime(totalTime);
    }

    /**
     * Set the time elapsed
     * @param totalTime String
     */
    public void setTotalTime(long totalTime){
        this.totalTime = totalTime;
    }

    /**
     * Get the time remained
     * @return String
     */
    public long getLeftTime(){
        return this.leftTime;
    }

    public String getLeftTimeString(){
        return ListModel.getTime(leftTime);
    }

    /**
     * Set the time remained
     * @param leftTime String
     */
    public void setLeftTime(long leftTime){
        this.leftTime = leftTime;
    }

    /**
     * Get the transfer speed
     * @return long
     */
    public long getSpeed(){
        return this.speed;
    }

    public String getSpeedString(){
        return ListModel.getSpeed(speed);
    }

    /**
     * Set the transfer speed
     * @param speed double
     */
    public void setSpeed(long speed){
        this.speed = speed;
    }

    /**
     * Set the resume mode
     */
    public void setResume(){
        this.resume = true;
    }

    /**
     * Get the resume mode
     * @return boolean
     */
    public boolean isResume(){
        return resume;
    }

    /**
     * Rename the file
     * @param name String
     */
    public void setNewName(String name){
        this.reName = name;
    }

    /**
     * Get the rename
     * @return String
     */
    public String getNewName(){
        return this.reName;
    }

    /**
     * Return a DrawState object for map view
     * @param time long
     * @return String
     */
    public DrawState task2State() {
        DrawState state = new DrawState(this, DrawState.ARROW_LEN, DrawState.STEP_LEN);
        return state;
    }

    /**
     * Return a url string
     * @return String
     */
    public String toString() {
        return this.srcSite.getPrefix() + this.srcSite.host + this.source + "/" + this.file.getName() + " (" + this.srcSite.name + ")";
    }

	public long getDstStartOffset() {
		return this.dstStartOffset;
	}

	public void setDstStartOffset(long dstStartOffset) {
		this.dstStartOffset = dstStartOffset;
	}

	public String getDisplayName() {
		return this.displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

    public int compareTo(FileTransferTask o) 
    {
    	return created.compareTo(o.created);
    }
    
    public Transfer toTransfer()
    {
    	Transfer transfer = new Transfer();
    	
		transfer.setId(id);
		transfer.setEpr(epr);
		transfer.setDn(dn);
		transfer.setDest(dest);
		transfer.setNotified(notified);
		transfer.setCreated(created);
		transfer.setStart(Calendar.getInstance());
		transfer.getStart().setTimeInMillis(startTime);
		transfer.setStop(stop);
		transfer.setPara(para);
		transfer.setParaId(paraId);
		transfer.setSpeed(speed);
		transfer.setProgress(progress);
		transfer.setFileName(file.getName());
		transfer.setFileDate(file.getDate());
		transfer.setFileType(file.isDirectory() ? FileInfo.DIRECTORY_TYPE : FileInfo.FILE_TYPE);
		transfer.setFileSize(file.getSize());
		transfer.setFileTime(file.getTime());
		transfer.setTask(task);
		transfer.setStatus(status);
		transfer.setVisible(visible);
		transfer.setSource(source);
		transfer.setDest(dest);
        
		return transfer;
    }
}
