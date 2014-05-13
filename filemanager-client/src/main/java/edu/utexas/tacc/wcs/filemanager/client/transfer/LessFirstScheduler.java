/*
 * Portions of this file Copyright 2004-2007 Shanghai Jiaotong University
 * 
 * This file or a portion of this file is licensed under the
 * terms of the Globus Toolkit Public License, found at
 * http://www.globus.org/toolkit/legal/4.0/
 * If you redistribute this file, with or without
 * modifications, you must include this notice in the file.
 */

package edu.utexas.tacc.wcs.filemanager.client.transfer;

import java.util.ArrayList;
import java.util.List;

import org.globus.ftp.FileInfo;

import edu.utexas.tacc.wcs.filemanager.common.model.Task;


public class LessFirstScheduler implements Scheduler {
    private List<FileTransferTask> fileTaskList = null;

    public LessFirstScheduler(List<FileTransferTask> fileTaskList) {
        this.fileTaskList = fileTaskList;
    }

    /**
     *
     * @param fileTransferTask FileTransferTask
     * @todo Implement this cgftp.Scheduler method
     */
    public void add(FileTransferTask fileTransferTask) {
        this.fileTaskList.add(fileTransferTask);
    }
    
    public void addTasks(List<FileTransferTask> taskList) {
		if (taskList == null || this.fileTaskList == null) {
			return;
		}
		
		for (int i = 0; i < taskList.size(); i++) {
			this.add((FileTransferTask)taskList.get(i));
		}
	}

    public void removeTask(FileTransferTask fileTransferTask){
    	if (this.fileTaskList == null) {
			return ;
		}
    	
    	this.fileTaskList.remove(fileTransferTask);
    }

    /**
     *
     * @return FileTransferTask
     * @todo Implement this cgftp.Scheduler method
     */
    public FileTransferTask getNext() {
        if(this.fileTaskList == null || this.fileTaskList.size() == 0) {
        	return null;
        }

        //Return the largest file in fileTransferTask
        FileTransferTask fileTransferTask = null;
        FileTransferTask dirTask = null;
        for(int i = 0; i < this.fileTaskList.size(); i++){
            FileTransferTask currentTask = (FileTransferTask)this.fileTaskList.get(i);
            FileInfo file = currentTask.getFile();

            //The current task is not waiting, skip it.
            if(currentTask.getStatus() != Task.WAITING){
                continue;
            }

            if(!file.isFile()){
                if(dirTask == null) {
                	dirTask = currentTask;
                }
            }else{
                if (fileTransferTask == null ||
                    fileTransferTask.getSize() > currentTask.getSize()) {
                    fileTransferTask = currentTask;
                } 
            }
        }

        if(fileTransferTask != null){
            //this.fileTaskList.remove(fileTransferTask);
            //fileTransferTask.setStatus(Task.ONGOING);
            return fileTransferTask;
        }

        if(dirTask != null){
            //dirTask.setStatus(Task.ONGOING);
            return dirTask;
        }

        return null;
    }

    /**
     *
     * @return boolean
     * @todo Implement this cgftp.Scheduler method
     */
    public boolean isEmpty() {
        if(this.fileTaskList == null || this.fileTaskList.size() == 0){
            return true;
        }else{
            return false;
        }
    }

    /**
     *
     * @todo Implement this cgftp.Scheduler method
     */
    public void removeAll() {
        this.fileTaskList = new ArrayList<FileTransferTask>();
    }

    /**
     *
     * @return int
     * @todo Implement this cgftp.Scheduler method
     */
    public int size() {
        return this.fileTaskList.size();
    }

}
