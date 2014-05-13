/*
 * Portions of this file Copyright 2004-2007 Shanghai Jiaotong University
 * 
 * This file or a portion of this file is licensed under the
 * terms of the Globus Toolkit Public License, found at
 * http://www.globus.org/toolkit/legal/4.0/
 * If you redistribute this file, with or without
 * modifications, you must include this notice in the file.
 */

package edu.utexas.tacc.wcs.filemanager.client.ui;

import java.awt.Component;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.globus.ftp.ByteRange;
import org.globus.ftp.FileInfo;

import edu.utexas.tacc.wcs.filemanager.client.AppMain;
import edu.utexas.tacc.wcs.filemanager.client.transfer.BatchTransfer;
import edu.utexas.tacc.wcs.filemanager.client.transfer.FIFOScheduler;
import edu.utexas.tacc.wcs.filemanager.client.transfer.FTPSettings;
import edu.utexas.tacc.wcs.filemanager.client.transfer.FileTransferTask;
import edu.utexas.tacc.wcs.filemanager.client.transfer.LargeFirstScheduler;
import edu.utexas.tacc.wcs.filemanager.client.transfer.LessFirstScheduler;
import edu.utexas.tacc.wcs.filemanager.client.transfer.Scheduler;
import edu.utexas.tacc.wcs.filemanager.client.util.LogManager;
import edu.utexas.tacc.wcs.filemanager.client.util.ResourceName;
import edu.utexas.tacc.wcs.filemanager.client.util.SGGCResourceBundle;
import edu.utexas.tacc.wcs.filemanager.common.model.Task;
import edu.utexas.tacc.wcs.filemanager.common.model.enumeration.FileProtocolType;


public class TransferProxy {
    /**
     * transfer files
     * @param fileList List - transfer file list
     * @param sourceFTPSite FTPSettings - source ftp site
     * @param destFTPSite FTPSettings - destination ftp site
     * @param sourceDir String - source ftp directory
     * @param destDir String - destination ftp directory
     * @param destFileList List - destination ftp directory file list
     * @return boolean - transfer result, true if succeeded
     */
    public static void transfer(Component parent, List<FileInfo> fileList, FTPSettings sourceFTPSite, FTPSettings destFTPSite, String sourceDir, String destDir, List<FileInfo> destFileList){
        
        //the source ftp and the destination ftp must be different
        
        //TODO: rethimk not allowing copying withing resources. we need to support moving of files around withing an 
        //existin file hierarchy even if it's just local
//        System.out.println("Source directories are" + (sourceDir.equals(destDir)?"":" not ") + 
//                "equal.\nMachines are " + (sourceFTPSite.host.equals(destFTPSite.host)?"":" not ") + " equal.");
        if(sourceDir.equals(destDir)  && sourceFTPSite.host.equals(destFTPSite.host)){
            // ignore copies to the same directory
//            System.out.println("Source and destination are the same. Rejecting transfer.");
        
        } else {
        	LogManager.debug("This is a transfer from " + 
                    sourceFTPSite.host + ":" + sourceDir + " to " + 
                    destFTPSite.host + ":" + destDir);
//            System.out.println("Source and destination are different.  Performing transfer.");
            if(true) {
                //start transfering file!
    
                boolean rememberAction = false;
                int defaultAction = 0;
            	int para = sourceFTPSite.connParallel < destFTPSite.connParallel ? sourceFTPSite.connParallel : destFTPSite.connParallel;
    
//                if(isThirdParty(sourceFTPSite, destFTPSite) && areBothGridFTP(sourceFTPSite, destFTPSite)){
//                    
//                    DlgThirdPartyTrans dlgThirdParty = new DlgThirdPartyTrans(AppMain.getFrame(), 
//                    		SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_TRANSFERPROXY_3PARTYDLGTITLE), 
//                    		true);
//                    dlgThirdParty.show();
//    
//                    if(!dlgThirdParty.getResult()) {
//                    	return;
//                    }
//                }
    
                //tranfer task list
                List<FileTransferTask> fileTaskList = new ArrayList<FileTransferTask>();
                for(int i=0; i<fileList.size(); i++){
                    FileInfo file = (FileInfo)fileList.get(i);
                    if(file.getName().equals(".") || file.getName().equals("..")) {
                    	continue;
                    }
    
                    //check if the destination file exists
                    FileInfo existedFile = checkExist(file,destFileList);
    
                    //if the destination file already exists
                    if(existedFile != null){
                        //initialize the action
                        int action = defaultAction;
    
                        //get the action: skip, overwrite, resume or rename
                        if(!rememberAction){
                            DlgResumeTrans dlg = new DlgResumeTrans(AppMain.getFrame(), 
                            		SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_TRANSFERPROXY_RESUMEDLGTITLE), 
                            		true, file, existedFile);
                            dlg.setVisible(true);
    
                            /*frmTrans dlg = new frmTrans(AppMain.frame, "confirm", true, true, false,
                                    file, existedFile);
                            dlg.show();
                             */
    
                            if(!dlg.isSucceed()) {
                            	return;
                            }
    
                            action = dlg.getChoice();
                            boolean remember = dlg.isRemember();
    
                            if (remember &&
                                action != DlgResumeTrans.CHOICE_RENAME) {
                                rememberAction = true;
                                defaultAction = action;
                            }
                        }
    
                        switch(action){
                        case DlgResumeTrans.CHOICE_SKIP:
                            continue;
                        case DlgResumeTrans.CHOICE_RESUME:
                            long existedFileSize = existedFile.getSize();
                            long toTransferFileSize = file.getSize();
                            
                            if(existedFileSize < toTransferFileSize){
                                FileTransferTask fileTask = new FileTransferTask(file, sourceFTPSite, destFTPSite, sourceDir, destDir, new ByteRange(existedFileSize, toTransferFileSize));
                                fileTask.setResume();
                                
                                fileTaskList.add(fileTask);
                            }
                            break;
                        case DlgResumeTrans.CHOICE_OVERWRITE:
    //                        fileTaskList.add(new FileTransferTask(file, sourceFTPSite, destFTPSite, sourceDir, destDir));
                        	BatchTransfer.addTask(fileTaskList, file, sourceFTPSite, destFTPSite, sourceDir, destDir, para, null);
                            break;
                        case DlgResumeTrans.CHOICE_RENAME:
    
    						String s;
    						do {
    							s = (String) AppMain
    									.Prompt(parent,
    											SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_TRANSFERPROXY_NEWNAMEDLGPROMT),
    											SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_TRANSFERPROXY_NEWNAMEDLGTITLE),
    											ListModel.getFileName(file));
    						} while (s != null
    								&& checkExist(s, destFileList) != null);
    						if (null == s || s.equals("")) {
    							AppMain.Error(parent,SGGCResourceBundle.getResourceString(ResourceName.KEY_ERROR_TRANSFERPROXY_INVALIDNAME),
    									SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_ERROR));
    						} else {
    //							FileTransferTask fileTask = new FileTransferTask(file, sourceFTPSite, destFTPSite, sourceDir, destDir);
    //							fileTask.setNewName(s);
    //							fileTaskList.add(fileTask);
    							BatchTransfer.addTask(fileTaskList, file, sourceFTPSite, destFTPSite, sourceDir, destDir, para, s);
    						}
    
    					}
    
                    }else{
    //                	if (destFTPSite.type != FTPType.FILE) {
    //                		fileTaskList.add(new FileTransferTask(file, sourceFTPSite, destFTPSite, sourceDir, destDir));
    //                	} else {
    //                	
    //                	
    //                	int para = sourceFTPSite.connParallel < destFTPSite.connParallel ? sourceFTPSite.connParallel : destFTPSite.connParallel;
    //                    if (para == 1) {
    //                    	fileTaskList.add(new FileTransferTask(file, sourceFTPSite, destFTPSite, sourceDir, destDir));
    //                    } else if (para > 1) {
    //							long begin = 0, end = 0, nSize = file.getSize() / para;
    //							for (int j = 0; j < para; j++) {
    //								begin = end;
    //								if (j == para - 1) {
    //									end = file.getSize();
    //								} else {
    //									end = begin + nSize;
    //								}
    //								FileTransferTask newtask = new FileTransferTask(
    //										file, sourceFTPSite, destFTPSite,
    //										sourceDir, destDir);
    //								newtask.setSrcRange(new ByteRange(begin, end));
    //								newtask.setDstStartOffset(begin);
    //								newtask.setDisplayName(newtask.getDisplayName()
    //										+ "[" + (j + 1) + "]");
    //								newtask.setParaID(j + 1);
    //								newtask.setPara(para);
    //								fileTaskList.add(newtask);
    //							}
    //						}
    //					}
                    	BatchTransfer.addTask(fileTaskList, file, sourceFTPSite, destFTPSite, sourceDir, destDir, para, null);
                    }
                
                } 
    
                Scheduler scheduler = null;
                switch (AppMain.getScheduleType()) {
                case AppMain.scheduleTypeLargeFile:
                	scheduler = new LargeFirstScheduler(fileTaskList);
                	break;
                case AppMain.scheduleTypeSmallFile:
                	scheduler = new LessFirstScheduler(fileTaskList);
                	break;
                case AppMain.scheduleTypeFIFO:
                	scheduler = new FIFOScheduler(fileTaskList);
                	break;
                default:
                	
                }
                BatchTransfer batchTransfer = new BatchTransfer(parent,fileTaskList,sourceFTPSite, destFTPSite, scheduler);
                batchTransfer.start();
            
            } 
        } 
    }
    
    public static void transfer(Component parent, List<FileTransferTask> fileTaskList) {
    	        
        for (int i = 0; i < fileTaskList.size(); i++) {
        	FileTransferTask currentTask = (FileTransferTask)fileTaskList.get(i);
        	if (currentTask.getStatus() != Task.ONGOING) {
        		LinkedList<FileTransferTask> newTransferList = new LinkedList<FileTransferTask>();
				FileTransferTask restartTask = new FileTransferTask(currentTask.getFile(), 
						currentTask.getSrcSite(), currentTask.getDestSite(), 
						currentTask.getSrcDir(), currentTask.getDestDir());
				restartTask.setDisplayName(currentTask.getDisplayName());
				restartTask.setDstStartOffset(currentTask.getDstStartOffset());
				restartTask.setPara(currentTask.getPara());
				restartTask.setParaId(currentTask.getParaId());
				restartTask.setSrcRange(currentTask.getSrcRange());
				newTransferList.add(restartTask);
				edu.utexas.tacc.wcs.filemanager.client.transfer.HistoryManager.deleteTask(currentTask);
				
				Scheduler scheduler = null;
				switch (AppMain.getScheduleType()) {
	            case AppMain.scheduleTypeLargeFile:
	            	scheduler = new LargeFirstScheduler(newTransferList);
	            	break;
	            case AppMain.scheduleTypeSmallFile:
	            	scheduler = new LessFirstScheduler(newTransferList);
	            	break;
	            case AppMain.scheduleTypeFIFO:
	            	scheduler = new FIFOScheduler(fileTaskList);
	            	break;
	            default:
	            	
	            }
				BatchTransfer batchTransfer = new BatchTransfer(parent,newTransferList,currentTask.getSrcSite(), currentTask.getDestSite(), scheduler);
	            batchTransfer.start();
    		}
        }
    }
    
    /*
     * check if the destination file exists
     * file:the file to transfer
     * destFileListthe destinatino file list
     * return: If the destination file is existed, return it, else return null.
     */
    private static FileInfo checkExist(FileInfo file, List<FileInfo> destFileList) {
        for (int i = 0; i < destFileList.size(); i++) {
            FileInfo destFile = (FileInfo) destFileList.get(i);

            if (ListModel.getFileName(file).equalsIgnoreCase(ListModel.getFileName(destFile))) {
                return destFile;
            }
        }

        return null;
    }
    
    private static FileInfo checkExist(String filename, List<FileInfo> destFileList) {
        for (int i = 0; i < destFileList.size(); i++) {
            FileInfo destFile = (FileInfo) destFileList.get(i);

            if (filename.equalsIgnoreCase(ListModel.getFileName(destFile))) {
                return destFile;
            }
        }

        return null;
    }

    @SuppressWarnings("unused")
	private static boolean isThirdParty(FTPSettings sourceFTPSite, FTPSettings destFTPSite){
        return !FTPSettings.Local.equals(sourceFTPSite) && !FTPSettings.Local.equals(destFTPSite);
    }

    @SuppressWarnings("unused")
	private static boolean areBothGridFTP(FTPSettings sourceFTPSite, FTPSettings destFTPSite){
        return sourceFTPSite.protocol.equals(FileProtocolType.GRIDFTP) && destFTPSite.protocol.equals(FileProtocolType.GRIDFTP);
    }

}
