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

import java.util.List;


public interface Scheduler {

    /**
     * Add a task to FileTransferTask
     * @param fileTransferTask FileTransferTask
     */
    public void add(FileTransferTask fileTransferTask);
    
    public void addTasks(List<FileTransferTask> taskList);

    public void removeTask(FileTransferTask fileTransferTask);

    /**
     * delete all tasks
     */
    public void removeAll();

    /**
     * get the next task
     * @return FileTransferTask
     */
    public FileTransferTask getNext();

    /**
     * get the number of tasks
     * @return int
     */
    public int size();

    /**
     * test if the task list is empty
     * @return boolean
     */
    public boolean isEmpty();
}
