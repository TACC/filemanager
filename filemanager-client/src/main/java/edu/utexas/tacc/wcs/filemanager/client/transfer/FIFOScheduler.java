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

public class FIFOScheduler implements Scheduler {
    private List<FileTransferTask> fileTaskList = null;
    
    private int curPos = 0;

    public FIFOScheduler(List<FileTransferTask> fileTaskList) {
        this.fileTaskList = fileTaskList;
//        if (this.fileTaskList != null) {
//        	this.listIterator = this.fileTaskList.iterator();
//        }
    }
    
	public void add(FileTransferTask fileTransferTask) {
		if (fileTransferTask == null) {
			return;
		}
//		System.out.println("add: " + fileTransferTask.getFile().getName());
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

	public FileTransferTask getNext() {
//		if (this.listIterator != null && this.listIterator.hasNext()) {
//			return (FileTransferTask)this.listIterator.next();
//		}
		if (this.fileTaskList != null && this.curPos < this.fileTaskList.size()) {
			return (FileTransferTask)this.fileTaskList.get(this.curPos++);
		}
		return null;
	}

	public boolean isEmpty() {
		if(this.fileTaskList == null || this.fileTaskList.size() == 0){
            return true;
        }else{
            return false;
        }
	}

	public void removeAll() {
		this.fileTaskList = new ArrayList<FileTransferTask>();
		this.curPos = 0;

	}

	public void removeTask(FileTransferTask fileTransferTask) {
		if (this.fileTaskList == null) {
			return ;
		}
		int removePos = this.fileTaskList.indexOf(fileTransferTask);
		if (removePos != -1 && removePos < this.curPos) {
			this.curPos--;
		}
//		System.out.println("remove: " + fileTransferTask.getFile().getName());
		this.fileTaskList.remove(fileTransferTask);

	}

	public int size() {
		return this.fileTaskList.size();
	}

}
