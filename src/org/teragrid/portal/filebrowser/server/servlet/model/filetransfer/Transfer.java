/* 
 * Created on Dec 11, 2007
 * 
 * Developed by: Rion Dooley - dooley [at] tacc [dot] utexas [dot] edu
 * 				 TACC, Texas Advanced Computing Center
 * 
 * https://www.tacc.utexas.edu/
 */

package org.teragrid.portal.filebrowser.server.servlet.model.filetransfer;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Calendar;

import org.apache.log4j.Logger;
import org.globus.ftp.FileInfo;
import org.teragrid.portal.filebrowser.applet.transfer.FTPSettings;
import org.teragrid.portal.filebrowser.applet.transfer.FTPType;
import org.teragrid.portal.filebrowser.applet.transfer.FileTransferTask;
import org.teragrid.portal.filebrowser.applet.transfer.Task;


/**
 * Representation of a remote file.  This class is a persisted record
 * of a file transfer.
 * 
 * @author Rion Dooley < dooley [at] tacc [dot] utexas [dot] edu >
 *
 */
public class Transfer {
	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(Transfer.class);
	
    public static final byte UNKNOWN_TYPE   = 0;
    public static final byte FILE_TYPE      = 1;
    public static final byte DIRECTORY_TYPE = 2;
    public static final byte SOFTLINK_TYPE  = 3;
    public static final byte DEVICE_TYPE  = 4;
    
    private Integer id = null;
    private String epr = "";
    private String dn = "";
    private int notified = 0;
    private Calendar created;
    private Calendar start;
    private Calendar stop;
    private int para;
    private int paraId;
    private long speed;
    private long transferBytes;
    private long totalBytes;
    private int progress;
    // FileInfo fields
    private String fileName;
    private String newFileName;
    private String fileDate;
    private byte fileType;
    private long fileSize;
    private String fileTime;
    private String task = "";
    private String file = "";
    private int status = Task.WAITING;
    private boolean visible = true;
    private String source = "file:///";
    private String dest = "file:///";
    
    public Transfer() {}
   
    public Transfer(FileTransferTask task, String epr, String dn) 
    throws MalformedURLException, URISyntaxException {
        
        this.id = task.getId() == -1? null: new Integer(task.getId());
        
        this.epr = epr;
        this.dn = dn;
        this.status = task.getStatus();
        this.para = task.getPara();
        this.paraId = task.getParaID();
        this.speed = task.getSpeed();
        this.progress = task.getProgress();
        
        setSourceURL(new URL("ftp://" + task.getSrcSite().host + ":" + 
                ((task.getSrcSite().port == 0)?"":task.getSrcSite().port) + "/" + 
                task.getSrcDir()));
        setDestURL(new URL("ftp://" + task.getDestSite().host + ":" + 
                ((task.getDestSite().port == 0)?-1:task.getDestSite().port) + "/" + 
                task.getDestDir()));
        
        this.newFileName = task.getNewName();
        this.fileName = task.getFile().getName();
        this.fileDate = task.getFile().getDate();
        this.fileType = task.getFile().isFile()?FILE_TYPE:
            task.getFile().isDirectory()?DIRECTORY_TYPE:
                task.getFile().isSoftLink()?SOFTLINK_TYPE:
                    task.getFile().isDevice()?DEVICE_TYPE:UNKNOWN_TYPE;
        this.fileSize = task.getFile().getSize();
        this.fileTime = task.getFile().getTime();
        this.created = Calendar.getInstance();
        this.created.setTimeInMillis(task.getCreated());
        
    }    

    /**
     * @return the created
     */
    public Calendar getCreated() {
        return created;
    }

    /**
     * @return the transferBytes
     */
    public long getTransferBytes() {
        return transferBytes;
    }

    /**
     * @param transferBytes the transferBytes to set
     */
    public void setTransferBytes(long transferBytes) {
        this.transferBytes = transferBytes;
    }

    /**
     * @return the totalBytes
     */
    public long getTotalBytes() {
        return totalBytes;
    }

    /**
     * @param totalBytes the totalBytes to set
     */
    public void setTotalBytes(long totalBytes) {
        this.totalBytes = totalBytes;
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
     * @param created the created to set
     */
    public void setCreated(Calendar created) {
        this.created = created;
    }

    /**
     * @return the id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return the parallelism
     */
    public int getPara() {
        return para;
    }

    /**
     * @param parallelism the parallelism to set
     */
    public void setPara(int para) {
        this.para = para;
    }

    /**
     * @return the parallelism
     */
    public int getParaId() {
        return paraId;
    }

    /**
     * @param parallelism the parallelism to set
     */
    public void setParaId(int paraId) {
        this.para = paraId;
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
     * @return the totalTime
     */
    public long getTotalTime() {
        if (start == null) {
            return 0;
        } else if(stop == null) {
            return Calendar.getInstance().getTimeInMillis() - start.getTimeInMillis();
        }
        
        return stop.getTimeInMillis() - start.getTimeInMillis();
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
    
//    public FileTransferTask getFileTransferTask() {
//        return (FileTransferTask)xstream.fromXML(task);
//    }
//    
//    public void setFileTransferTask(FileTransferTask ftt) {
//        this.ftt = ftt;
//        this.task = xstream.toXML(ftt);
//    }
    
    /**
     * @return the newFileName
     */
    public String getNewFileName() {
        return newFileName;
    }

    /**
     * @param newFileName the newFileName to set
     */
    public void setNewFileName(String newFileName) {
        this.newFileName = newFileName;
    }

    /**
     * @return the task in serialized xml
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
     * @param file the file to set
     */
    public String getFile() {
        return this.file;
    }
    /**
     * @param file the file to set
     */
    public void setFile(String file) {
        this.file = file;
    }

    /**
     * @return the fileName
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * @param name the fileName to set
     */
    public void setFileName(String name) {
        fileName = name;
    }

    /**
     * @return the fileDate
     */
    public String getFileDate() {
        return fileDate;
    }

    /**
     * @param date the fileDate to set
     */
    public void setFileDate(String date) {
        fileDate = date;
    }

    /**
     * @return the fileType
     */
    public byte getFileType() {
        return fileType;
    }

    /**
     * @param type the fileType to set
     */
    public void setFileType(byte type) {
        fileType = type;
    }

    /**
     * @return the fileSize
     */
    public long getFileSize() {
        return fileSize;
    }

    /**
     * @param size the fileSize to set
     */
    public void setFileSize(long size) {
        fileSize = size;
    }

    /**
     * @return the fileTime
     */
    public String getFileTime() {
        return fileTime;
    }

    /**
     * @param time the fileTime to set
     */
    public void setFileTime(String time) {
        fileTime = time;
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
     * @return the destURL
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
     * @return the sourceURL
     */
    public String getSource() {
        return source;
    }

    /**
     * @param sourceURL the sourceURL to set
     */
    public void setSource(String source) {
        this.source = source;
    }
    
    public URL getSourceURL() throws MalformedURLException {
        return new URL(source);
    }
    
    public URL getDestURL() throws MalformedURLException {
        return new URL(dest);
    }
    
    public void setSourceURL(URL source) {
        this.source = source.toString();
    }
    
    public void setDestURL(URL dest) {
        this.dest = dest.toString();
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
     * @return the file
     */
    public FileInfo getFileInfo() {
        FileInfo fInfo = new FileInfo();
        
        fInfo.setDate(fileDate);
        fInfo.setFileType(fileType);
        fInfo.setName(fileName);
        fInfo.setSize(fileSize);
        fInfo.setTime(fileTime);
        
        return fInfo;
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

    

    //  ********************** Common Methods ********************** //

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Transfer)) return false;
        final Transfer t = (Transfer) o;
        if (!this.dn.equals(t.getDn())) return false;
        if (!this.epr.equals(t.getEpr())) return false;
        if (!this.source.equals(t.getSource())) return false;
        if (!this.dest.equals(t.getDest())) return false;
        if (!this.getFile().toString().equals(t.getFile().toString())) return false;
        return true;
    }
    
    public String toString() {
        return  "File transfer " + id + " from "  +
            source + " to " + dest + 
            " " + FileTransferTask.getStatusString(status);
    }
    
    public int compareTo(Object o) {
        if (o instanceof Transfer) {
            if (this.source.compareTo(((Transfer)o).getSource()) > 0 ||
                    this.dest.compareTo(((Transfer)o).getDest()) > 0 ||
                    this.dn.compareTo(((Transfer)o).getDn()) > 0 ||
                    this.status > ((Transfer)o).getStatus() ||
                    this.epr.compareTo(((Transfer)o).getEpr()) > 0) {
                return 1;
            }
        }
        return 0;
    }
    
    public FileTransferTask toFileTransferTask() throws MalformedURLException {
        URL source = getSourceURL();
        URL dest = getDestURL();
        
        FTPSettings srcSite = new FTPSettings(source.getHost(),
                source.getPort(),
                ((source.getHost().toLowerCase().indexOf("local") > -1?
                        FTPType.FILE:FTPType.GRIDFTP)));
        
        FTPSettings destSite = new FTPSettings(dest.getHost(),
                dest.getPort(),
                ((dest.getHost().toLowerCase().indexOf("local") > -1?
                        FTPType.FILE:FTPType.GRIDFTP)));
        
        
        FileTransferTask ftt = new FileTransferTask(
                getFileInfo(), srcSite, destSite, 
                source.getPath(), dest.getPath());
        
        ftt.setNewName(getNewFileName());
        ftt.setId(getId().intValue());
        ftt.setStartTime((getStart()==null)?0:getStart().getTimeInMillis());
        ftt.setCreated(getCreated().getTimeInMillis());
        ftt.setStatus(getStatus());
        ftt.setPara(getPara());
        ftt.setParaID(getParaId());
        ftt.setSpeed(getSpeed());
        ftt.setTotalTime(getTotalTime());
        ftt.setLeftTime(0);
        ftt.setProgress(getProgress());
        
        return ftt;
    }
    
}
