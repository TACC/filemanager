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

import java.awt.Component;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.globus.ftp.ByteRange;
import org.globus.ftp.FileInfo;
import org.globus.ftp.Session;
import org.teragrid.portal.filebrowser.applet.AppMain;
import org.teragrid.portal.filebrowser.applet.ConfigOperation;
import org.teragrid.portal.filebrowser.applet.ui.ListModel;
import org.teragrid.portal.filebrowser.applet.ui.UIRefresher;
import org.teragrid.portal.filebrowser.applet.util.LogManager;
import org.teragrid.portal.filebrowser.applet.util.ResourceName;
import org.teragrid.portal.filebrowser.applet.util.SGGCResourceBundle;

import edu.utexas.tacc.wcs.filemanager.common.model.Task;
import edu.utexas.tacc.wcs.filemanager.common.model.enumeration.FileProtocolType;

public class BatchTransfer extends Thread
{
    private List<FileTransferTask> fileTaskList = null;
    private FTPSettings sourceFTPSite = null;
    private FTPSettings destFTPSite = null;

    private Scheduler scheduler = null;

    private int nRetry;//The number of opportunity remained for retry
    private int delay;//The time delay between 2 connection attempt
    private Component parent;
    
    public BatchTransfer(Component parent,List<FileTransferTask> fileTaskList, FTPSettings sourceFTPSite, FTPSettings destFTPSite, Scheduler scheduler) {
        this.fileTaskList = fileTaskList;
        this.sourceFTPSite = sourceFTPSite;
        this.destFTPSite = destFTPSite;
        this.parent = parent;
        this.scheduler = scheduler;//new LessFirstScheduler(fileTaskList);

        this.nRetry = (((sourceFTPSite.connRetry) < (destFTPSite.connRetry)) ? (sourceFTPSite.connRetry) : (destFTPSite.connRetry));
        this.delay = (((sourceFTPSite.connDelay) > (destFTPSite.connDelay)) ? (sourceFTPSite.connDelay) : (destFTPSite.connDelay));
        
        //if (ConfigOperation.isLoggingEnabled()) {
        	HistoryManager.addTasks(this.fileTaskList);
        //}
    }

    public void updateGUI(){
        UIRefresher.refreshQueue();
        UIRefresher.scrollQueuePanelToTop();
    }


    public synchronized void run(){
        updateGUI();

        try {
            FileTransferTask fileTask = null;//When the fileTask is null, a new task will be taken in the next loop
            while (true) {
//                if(fileTask == null){
                if(this.sourceFTPSite.hasFreeConnection() && this.destFTPSite.hasFreeConnection()){
                    //take a new task
                    fileTask = this.scheduler.getNext();
                    //if the task is null, all work donebreak
                    if (fileTask == null) {
                        //break;
//                    	System.out.println("end");
                        wait();
                        continue;
                    }
                } else {
//                	if (!this.sourceFTPSite.hasFreeConnection()) {
//                        synchronized (this.sourceFTPSite) {
//                			this.sourceFTPSite.wait();
//                		}
//                	}
//                	if (!this.destFTPSite.hasFreeConnection()) {
//                        synchronized (this.destFTPSite) {
//							this.destFTPSite.wait();                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              
//						}                		
//                	}
                	wait(1000);
                	continue;
                }
                
                if (fileTask.isCancelled()) {
            		LogManager.debug("Transfer cancelled: '" + fileTask.getFile().getName()
            				+ "' from '" + this.sourceFTPSite.name + " " + this.sourceFTPSite.host 
            				+ "' to '"+ this.destFTPSite.name + " " + this.destFTPSite.host + "'");
                	fileTask = null;
                	continue;
                }

                SHGridFTP srcConn = null;
                SHGridFTP destConn = null;
                
                long startTime = System.currentTimeMillis();
                
                srcConn = this.sourceFTPSite.getConnection();
            	destConn = this.destFTPSite.getConnection();
                while ((srcConn == null || destConn == null) && !fileTask.isCancelled()) {
                	wait(1000 * (long) this.delay);
                	if (srcConn == null) {
                		LogManager.debug("Transfer '" + fileTask.getFile().getName() + "'.  Fail to connect to " + this.sourceFTPSite.name + " " + this.sourceFTPSite.host);
                		srcConn = this.sourceFTPSite.getConnection();
                	}
                	if (destConn == null) {
                		LogManager.debug("Transfer '" + fileTask.getFile().getName() + "'.  Fail to connect to " + this.destFTPSite.name + " " + this.destFTPSite.host);
                		destConn = this.destFTPSite.getConnection();
                	}
                	this.nRetry--;
                	if (this.nRetry < 0) {
                		if (srcConn != null) {
                			srcConn.close();
                			srcConn.setIdle(true);
                		}
                		if (destConn != null) {
                			destConn.close();
                			destConn.setIdle(true);
                		}
                		this.nRetry = (((sourceFTPSite.connRetry) < (destFTPSite.connRetry)) ? (sourceFTPSite.connRetry) : (destFTPSite.connRetry));
                	}
                }
                LogManager.debug("Connections established.  Start to transfer '" + fileTask.getFile().getName()
        				+ "' from '" + this.sourceFTPSite.name + " " + this.sourceFTPSite.host 
        				+ "' to '"+ this.destFTPSite.name + " " + this.destFTPSite.host + "'");
                
                if (!fileTask.isCancelled()) {
                	useConnection(srcConn, destConn, fileTask, startTime);
                }
                fileTask = null; //Set fileTask to nullallow a new task to be taken in the next loop
                	
//                //Get free connections from the source site and the destination site
//                srcConn = this.sourceFTPSite.getFreeConnection();
//                destConn = this.destFTPSite.getFreeConnection();
//                

//                //If we got the connections successfully, transfer starts.
//                if (srcConn != null && destConn != null) {
//            		LogManager.debug("Connections already exist.  Start to transfer '" + fileTask.getFile().getName()
//            				+ "' from '" + this.sourceFTPSite.name + " " + this.sourceFTPSite.host 
//            				+ "' to '"+ this.destFTPSite.name + " " + this.destFTPSite.host + "'");
//                    useConnection(srcConn, destConn, fileTask);
//                    fileTask = null; //Set fileTask to nullallow a new task to be taken in the next loop
//                }
//                //If we cannot get free connections
//                else {
//
//                    //No free connections in the connection pool , but the site has free connections: acquire a new connection
//                    if (srcConn == null && this.sourceFTPSite.existNotGot()
//                        ||
//                        destConn == null && this.destFTPSite.existNotGot()) {
//
//                        //We still have retry opportunity
//                        if (this.nRetry > 0) {
//                            if(srcConn == null) {
//                            	srcConn = this.sourceFTPSite.getNewConnection();
//                            }
//                            if(destConn == null) {
//                            	destConn = this.destFTPSite.getNewConnection();
//                            }
//                            //Got and use the new connection
//                            if (srcConn != null && destConn !=null) {
//                        		LogManager.debug("Got new connections.  Start to transfer '" + fileTask.getFile().getName()
//                        				+ "' from '" + this.sourceFTPSite.name + " " + this.sourceFTPSite.host 
//                        				+ "' to '"+ this.destFTPSite.name + " " + this.destFTPSite.host + "'");
//                                useConnection(srcConn, destConn, fileTask);
//                                fileTask = null; //Set fileTask to nullallow a new task to be taken in the next loop
//                            }
//                            //Acquire failed. Wait and retry
//                            else {
//                            	if (srcConn == null) {
//                            		LogManager.debug("Transfer '" + fileTask.getFile().getName() + "'.  Fail to connect to " + this.sourceFTPSite.name + " " + this.sourceFTPSite.host + ". Remain " + this.nRetry + " times");
//                            	} 
//                            	if (destConn == null) {
//                            		LogManager.debug("Transfer '" + fileTask.getFile().getName() + "'.  Fail to connect to " + this.destFTPSite.name + " " + this.destFTPSite.host + ". Remain " + this.nRetry + " times");
//                                }
//                                this.nRetry--;
//                                wait(1000 * (long) this.delay);
//                            }
//                        }
//                        //No retry opportunity
//                        else {
//                            //Either the source or the destination site has no connction, escape the loop
//                            if (this.sourceFTPSite.noConnection() ||
//                                this.destFTPSite.noConnection()) {
//                            	if (srcConn == null) {
//                            		LogManager.debug("Transfer '" + fileTask.getFile().getName() + "'.  " + this.sourceFTPSite.name + " " + this.sourceFTPSite.host + " has no connection.");
//                            	} 
//                            	if (destConn == null) {
//                            		LogManager.debug("Transfer '" + fileTask.getFile().getName() + "'.  " + this.destFTPSite.name + " " + this.destFTPSite.host + " has no connection.");
//                                }
//                                break;
//                            }
//                            //Both the source and the destination site have connctions, wait and retry.
//                            else {
//                            	if (srcConn != null) {
//                            		LogManager.debug("Transfer '" + fileTask.getFile().getName() + "'.  " + this.sourceFTPSite.name + " " + this.sourceFTPSite.host + " has no retry oppotunity.");
//                            	} 
//                            	if (destConn != null) {
//                            		LogManager.debug("Transfer '" + fileTask.getFile().getName() + "'.  " + this.destFTPSite.name + " " + this.destFTPSite.host + " has no retry oppotunity.");
//                                }
//                                wait(1000 * (long) this.delay);
//                            }
//                        }
//                    }
//                    //Both the source and the destination site have no free connections, wait and retry.
//                    else {
//                		LogManager.debug("Transfer '" + fileTask.getFile().getName() + "'.  Both '"
//                				+ this.sourceFTPSite.name + " " + this.sourceFTPSite.host + "' and '"
//                				+ this.destFTPSite.name + " " + this.destFTPSite.host + "' have no free connections.");
//                    	
//                        wait(5000);
//                    }
//                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            org.teragrid.portal.filebrowser.applet.util.LogManager.debug(ex.getLocalizedMessage() + " at " + (ex.getStackTrace())[0]);
        }
    }


    //Use the connections to transfer
    private void useConnection(SHGridFTP srcConn, SHGridFTP destConn, FileTransferTask fileTask, long startTime){
        srcConn.setIdle(false);
        destConn.setIdle(false);

        //FileTransfer fileTransfer = new FileTransfer(this, this.batch, fileTask, srcConn, destConn);
        //fileTransfer.start();
        
        fileTask.setStatus(Task.ONGOING);
        fileTask.setStartTime(startTime);
        if(!fileTask.isFileTask()){
            processDir(fileTask, srcConn, destConn);
            this.updateGUI();
        }else{
            TransferListener listener = new TransferListener(this, fileTask, srcConn, destConn, startTime);
            SHGridFTP.transfer(srcConn, destConn, fileTask, listener);
        }
    }

    private void processDir(FileTransferTask fileTask, SHGridFTP srcConnection, SHGridFTP destConnection){
        String fileName = ListModel.getFileName(fileTask.getFile());
        String destDir = fileTask.getDestDir() + separator(destFTPSite) + fileName;
        String srcDir = fileTask.getSrcDir() + separator(sourceFTPSite) + fileName;

        if(fileTask.getNewName() != null){
            destDir = fileTask.getDestDir() + separator(destFTPSite) + fileTask.getNewName();
        }

        try{
            //Create the directory on the destination ftp site.
        	try {
        		if (!destConnection.exists(destDir)) {
        			destConnection.makeDir(destDir);
        		}
        	} catch (Exception e) {
        	    AppMain.Error(parent,SGGCResourceBundle.getResourceString(ResourceName.KEY_ERROR_BATCHTRANSFER_CREATEDIR) + destDir);
        	    e.printStackTrace();
        	}

            //Read in the source file list.
            srcConnection.setDir(srcDir);
            srcConnection.setType(Session.TYPE_ASCII);
            srcConnection.setDTP(srcConnection.getFtpServer().passiveMode);
            Vector<FileInfo> files = srcConnection.list("*");

            for (int i = 0; i < files.size(); i++) {
                FileInfo file = (FileInfo) files.get(i);
                if(file.getName().equals(".") || file.getName().equals("..")) {
                	continue;
                }

            	int para = sourceFTPSite.connParallel < destFTPSite.connParallel ? sourceFTPSite.connParallel : destFTPSite.connParallel;
                List<FileTransferTask> fileTaskList = new ArrayList<FileTransferTask>();
                BatchTransfer.addTask(fileTaskList, file, fileTask.getSrcSite(), 
                		fileTask.getDestSite(), srcDir, destDir, para, null);
                this.scheduler.addTasks(fileTaskList);
            	HistoryManager.addTasks(fileTaskList);
            }

            this.scheduler.removeTask(fileTask);
            
            //if (ConfigOperation.isLoggingEnabled()) {
            HistoryManager.deleteTask(fileTask);
            //}
            UIRefresher.refreshQueue();

			try {
				srcConnection.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
            srcConnection.setIdle(true);
			
			try {
				destConnection.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
            destConnection.setIdle(true);
            
        }catch(Exception e){
            //File transfer failed
            e.printStackTrace();
            org.teragrid.portal.filebrowser.applet.util.LogManager.debug(e.getLocalizedMessage() + " at " + (e.getStackTrace())[0]);
            AppMain.Error(parent,e.getMessage());
            if (ConfigOperation.isLoggingEnabled()) {
            	HistoryManager.deleteTasks(fileTaskList);
            }
        }
    }
//	
//	public void notifyFTP() {
//		System.out.println("notify: " + this.sourceFTPSite.name + " " + this.destFTPSite.name);
//		this.sourceFTPSite.notifyAll();
//		this.destFTPSite.notifyAll();
//	}
////
//	public FTPSettings getDestFTPSite() {
//		return destFTPSite;
//	}
//
//	public FTPSettings getSourceFTPSite() {
//		return sourceFTPSite;
//	}
    
    public static void addTask(List<FileTransferTask> taskList, FileInfo file, FTPSettings srcSite, 
    		FTPSettings destSite, String srcDir, String destDir, int parallism, String newName) {
    	if (taskList == null || file == null || srcSite == null
    			|| destSite == null || srcDir == null || destDir == null || parallism <= 0) {
    		return;
    	}
    	
    	if (destSite.protocol.equals(FileProtocolType.FILE) || file.isDirectory() || parallism == 1) {
    		FileTransferTask newtask = new FileTransferTask(file, srcSite, destSite, srcDir, destDir);
    		if (newName != null) {
    			newtask.setNewName(newName);
    			newtask.setDisplayName(newName);
    		}
    		taskList.add(newtask);
    	} else {
			long begin = 0, end = 0, nSize = file.getSize() / parallism;
			for (int j = 0; j < parallism; j++) {
				begin = end;
				if (j == parallism - 1) {
					end = file.getSize();
				} else {
					end = begin + nSize;
				}
				FileTransferTask newtask = new FileTransferTask(file, srcSite,
						destSite, srcDir, destDir);
				newtask.setSrcRange(new ByteRange(begin, end));
				newtask.setDstStartOffset(begin);
				newtask.setDisplayName(newtask.getDisplayName() + "[" + (j + 1)
						+ "]");
				if (newName != null) {
					newtask.setNewName(newName);
					newtask.setDisplayName(newtask.getNewName() + "[" + (j + 1)
						+ "]");
				}
				newtask.setParaId(j + 1);
				newtask.setPara(parallism);
				taskList.add(newtask);
				if (j%1000 == 0) {
				    System.out.println("Size of parallel array is now: " + taskList.size());
				}
			}
			System.out.println("Size of parallel array is: " + taskList.size());
		}
    }
    
    private String separator(FTPSettings site) {
        return (site.protocol.equals(FileProtocolType.FILE)) ? File.separator : "/";
    }
}
