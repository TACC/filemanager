/*
 * Portions of this file Copyright 2004-2007 Shanghai Jiaotong University
 * 
 * This file or a portion of this file is licensed under the
 * terms of the Globus Toolkit Public License, found at
 * http://www.globus.org/toolkit/legal/4.0/
 * If you redistribute this file, with or without
 * modifications, you must include this notice in the file.
 */

package org.teragrid.portal.filebrowser.applet.ui;

import java.util.HashMap;

import javax.swing.DefaultBoundedRangeModel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.teragrid.portal.filebrowser.applet.transfer.FTPSettings;
import org.teragrid.portal.filebrowser.applet.transfer.HistoryManager;

@SuppressWarnings({"unchecked"})
public class UIRefresher {
    private static JTable tblQueue = null;
    private static JScrollPane pneQueue = null;
    private static HashMap hashMap = new HashMap();

    public static void setDisplayTable(JTable table) {
        tblQueue = table;
    }
    
    public static void setDisplayScrollPane(JScrollPane pane) {
        pneQueue = pane;
    }

    public static void registerPnlBrowse(FTPSettings site, PnlBrowse pnlBrowse){
        hashMap.put(site, pnlBrowse);
    }

    public static void clearSites() {
        hashMap.clear();
    }
    public static void unRegisterPnlBrowse(FTPSettings site){
        hashMap.remove(site);
    }

    public static void refreshQueue(){

        QueueTableModel model = (QueueTableModel)tblQueue.getModel();
        //model.setTaskList(fileTaskList);
        model.setTaskList(HistoryManager.getTaskList());

        tblQueue.repaint();
        tblQueue.revalidate();
        
    }

    public static void refreshSite(FTPSettings site){
        
    	synchronized(hashMap){ 
    		PnlBrowse pnlBrowse = (PnlBrowse)hashMap.get(site);
        	if(pnlBrowse!=null){
        		pnlBrowse.tBrowse.cmdAdd("List");
        	}
        }
        
    }

    public static void updateGUI(){
        tblQueue.repaint();
        tblQueue.revalidate();
    }
    
    public static void scrollQueuePanelToTop() {
        DefaultBoundedRangeModel model = (DefaultBoundedRangeModel)pneQueue.getVerticalScrollBar().getModel();
        model.setValue(model.getMinimum());
    }

}
