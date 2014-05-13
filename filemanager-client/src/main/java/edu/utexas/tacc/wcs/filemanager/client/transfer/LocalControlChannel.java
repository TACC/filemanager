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

import java.io.IOException;
import java.util.LinkedList;

import org.globus.ftp.exception.FTPException;
import org.globus.ftp.exception.FTPReplyParseException;
import org.globus.ftp.exception.ServerException;
import org.globus.ftp.vanilla.BasicClientControlChannel;
import org.globus.ftp.vanilla.BasicServerControlChannel;
import org.globus.ftp.vanilla.Flag;
import org.globus.ftp.vanilla.Reply;

import edu.utexas.tacc.wcs.filemanager.client.util.LogManager;
import edu.utexas.tacc.wcs.filemanager.client.util.ResourceName;
import edu.utexas.tacc.wcs.filemanager.client.util.SGGCResourceBundle;



public class LocalControlChannel extends BasicClientControlChannel implements
		BasicServerControlChannel {

	// FIFO queue of Replies
	private LinkedList<Reply> replies = null;

	// how many replies have been pushed so far
	private int replyCount = 0;

	public LocalControlChannel() {
		this.replies = new LinkedList<Reply>();
	}

	protected synchronized void push(Reply newReply) {
	    LogManager.debug("push reply:" + newReply.toString());
		this.replies.add(newReply);
		this.replyCount++;
		notify();
	}

	// blocking pop from queue
	protected synchronized Reply pop() throws InterruptedException {
	    LogManager.debug("pop reply");
		try {
			while (this.replies.isEmpty()) {
				wait();
			}
		} catch (InterruptedException e) {
			throw e;
		}
		return (Reply) this.replies.removeFirst();
	}

	//non blocking; check if queue is ready for pop
	public synchronized boolean ready() {
		return (!this.replies.isEmpty());
	}

	public synchronized int getReplyCount() {
		return this.replyCount;
	}

	public void resetReplyCount() {
		this.replyCount = 0;
	}

	public Reply read() throws IOException, FTPReplyParseException,
			ServerException {
		try {
			return pop();
		} catch (InterruptedException e) {
			ServerException se = new ServerException(FTPException.UNSPECIFIED,
					SGGCResourceBundle.getResourceString(ResourceName.KEY_EXCEPTION_LOCALCONTROLCHANNEL_WAITINTERRUPT));
			se.setRootCause(e);
			throw se;
		}
	}

	public void write(Reply reply) {
	    LogManager.debug("writing reply");
		push(reply);
		LogManager.debug("wrote reply");
	}

	public void waitFor(Flag aborted, int ioDelay, int maxWait)
			throws ServerException, IOException, InterruptedException {
		int i = 0;
		LogManager.debug("waiting for reply in local control channel");
		while (!ready()) {
			if (aborted.flag) {
				throw new InterruptedException();
			}
			LogManager.debug("slept " + i);
			Thread.sleep(ioDelay);
			i += ioDelay;
			if (maxWait != WAIT_FOREVER && i >= maxWait) {
			    LogManager.debug("timeout");
				throw new ServerException(ServerException.REPLY_TIMEOUT);
			}
		}
		LogManager.debug("local control channel ready");
	}

    public void abortTransfer() {
        
    }

}// class localControlChannel

