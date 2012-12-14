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

public interface Task {
    public static final int WAITING = 0;
    public static final int ONGOING = 1;
    public static final int DONE = 2;
    public static final int FAILED = 3;
    public static final int RESTARTABLE = 4;
    public static final int STOPPED = 5;

    /**
     * Get the task status
     * @return int
     */
    public int getStatus();

    /**
     * Set the task status
     * @param status int
     * @throws IllegalArgumentException
     */
    public void setStatus(int status) throws IllegalArgumentException;

}
