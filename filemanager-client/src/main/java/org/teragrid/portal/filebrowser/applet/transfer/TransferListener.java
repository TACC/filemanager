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

import org.globus.io.urlcopy.UrlCopyListener;
import org.teragrid.portal.filebrowser.applet.ui.UIRefresher;
import org.teragrid.portal.filebrowser.applet.util.LogManager;

import edu.utexas.tacc.wcs.filemanager.common.model.Task;


public class TransferListener implements UrlCopyListener {
    private Exception _exception;

    private BatchTransfer handler;
    private FileTransferTask fileTask;
    private SHGridFTP srcConn;
    private SHGridFTP destConn;
    private long startTime, lastTime, lastTransferedBytes = 0;

    private int count = 0;
    private final int REFRESH_COUNT = 1;
    private final int REFRESH_INTERVAL = 1000;

    public TransferListener(BatchTransfer handler, FileTransferTask fileTask, SHGridFTP srcConn, SHGridFTP destConn, long startTime) {
        this.handler = handler;
        this.fileTask = fileTask;
        this.srcConn = srcConn;
        this.destConn = destConn;
//        this.startTime = System.currentTimeMillis();
        this.startTime = startTime;
        
        HistoryManager.update(fileTask);
           
    }

    /**
     * This function is contniuosly called during url transfers.
     * @param transferedBytes long - number of bytes currently trasfered if -1, then performing thrid party transfer
     * @param totalBytes long - number of total bytes to transfer if -1, the total size in unknown
     */
    public void transfer(long transferedBytes, long totalBytes) {
        int progress = 0;
        long currentTime, totalTime, leftTime, speed;

/*
        if (totalBytes == -1) {
            if (transferedBytes == -1) {
                System.out.println("<thrid party transfer: progress unavailable>");
            } else {
                System.out.println(transferedBytes);
            }
        } else {
            System.out.println(current + " out of " + total);
        }
 */
        //Calculate progress(REFRESH_COUNT),TotalTime(in ms), LeftTime(in ms), speed(in bps)
        if (++this.count < REFRESH_COUNT) {
        	return; 
        }else {
        	this.count = 0;
        }
        currentTime = System.currentTimeMillis();
        long interval = currentTime - this.lastTime;
        if(interval < REFRESH_INTERVAL) {
        	return;
        }
        
        this.lastTime = currentTime;
        if(totalBytes>0) {
        	progress = (int) (transferedBytes * 100 / totalBytes);
        }
        totalTime =  this.lastTime - this.startTime;
        leftTime = (totalBytes - transferedBytes) * totalTime / ((transferedBytes ==0)?1:transferedBytes);
        speed = ((transferedBytes - this.lastTransferedBytes)<<10) / interval;
        
        this.lastTransferedBytes = transferedBytes;

        this.fileTask.setProgress(progress);
        this.fileTask.setTotalTime(totalTime);
        this.fileTask.setLeftTime(leftTime);
        this.fileTask.setSpeed(speed);

        this.handler.updateGUI();
   }

   /**
    * This function is called only when an error occurs
    * @param exception Exception - the actual error exception
    */
   public void transferError(Exception exception) {
	   LogManager.error("Transfer error",exception);
       this.fileTask.setStatus(Task.FAILED);
       
       HistoryManager.update(fileTask);
       
       this._exception = exception;
       synchronized(this.handler){
//         this.handler.notify();
    	   this.handler.notify();
       }
//       synchronized(this.srcConn.getFtpServer()) {
//    	   this.srcConn.getFtpServer().notify();
//       }
//       
//       synchronized(this.destConn.getFtpServer()) {
//    	   this.destConn.getFtpServer().notify();
//       }
       LogManager.debug("Transfer aborted: '" + this.fileTask.getFile().getName()
				+ "' from '" + this.srcConn.getFtpServer().name + " " + this.srcConn.getFtpServer().host 
				+ "' to '"+ this.destConn.getFtpServer().name + " " + this.destConn.getFtpServer().host + "'");
   }

   /**
    * This function is called once the transfer is completed either successfully or because of a failure.
    * If an error occurred during the transfer the transferError() function is called first.
    */
   public void transferCompleted() {
       //this.srcConn.bIdle = true;
       //this.destConn.bIdle = true;

       if (this._exception == null) {
           long totalTime = System.currentTimeMillis()-startTime;

           this.fileTask.setProgress(100);
           this.fileTask.setLeftTime(0);
           this.fileTask.setTotalTime(totalTime);
           if (totalTime != 0) {
        	   this.fileTask.setSpeed(this.fileTask.getSize()*1024/totalTime);
           }
           this.fileTask.setStatus(Task.DONE);
       } else {
           this.fileTask.setStatus(Task.FAILED);
       }
       
       HistoryManager.update(fileTask);
       
       this.handler.updateGUI();
       UIRefresher.refreshSite(fileTask.getDestSite());

       synchronized(this.handler){
//           this.handler.notify();
    	   this.handler.notify();
       }
//       synchronized(this.srcConn.getFtpServer()) {
//    	   this.srcConn.getFtpServer().notify();
//       }
//       
//       synchronized(this.destConn.getFtpServer()) {
//    	   this.destConn.getFtpServer().notify();
//       }
       
       LogManager.debug("Transfer finished: '" + this.fileTask.getFile().getName()
				+ "' from '" + this.srcConn.getFtpServer().name + " " + this.srcConn.getFtpServer().host 
				+ "' to '"+ this.destConn.getFtpServer().name + " " + this.destConn.getFtpServer().host + "'");
  		
   }

}
