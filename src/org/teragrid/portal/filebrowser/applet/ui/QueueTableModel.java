/* 
 * Created on Jan 24, 2008
 * 
 * Developed by: Rion Dooley - dooley [at] tacc [dot] utexas [dot] edu
 * 				 TACC, Texas Advanced Computing Center
 * 
 * https://www.tacc.utexas.edu/
 */

package org.teragrid.portal.filebrowser.applet.ui;

import java.awt.Component;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JProgressBar;
import javax.swing.table.AbstractTableModel;

import org.globus.ftp.FileInfo;
import org.teragrid.portal.filebrowser.applet.AppMain;
import org.teragrid.portal.filebrowser.applet.transfer.FileTransferTask;
import org.teragrid.portal.filebrowser.applet.transfer.HistoryManager;
import org.teragrid.portal.filebrowser.applet.transfer.Task;
import org.teragrid.portal.filebrowser.applet.util.ResourceName;
import org.teragrid.portal.filebrowser.applet.util.SGGCResourceBundle;
import org.teragrid.portal.filebrowser.server.servlet.model.notification.NotificationType;

@SuppressWarnings("serial")
public class QueueTableModel extends AbstractTableModel{
    //column names of the table
    private final String [] TABLE_COLUMN_NAMES = new String[]{
            SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_QUEUETABLEMODEL_ID),
            SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_QUEUETABLEMODEL_ITEMNAME),
            SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_QUEUETABLEMODEL_SOURCE),
            "-->",
            SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_QUEUETABLEMODEL_DEST),
            SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_QUEUETABLEMODEL_PROGRESS),
//            SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_QUEUETABLEMODEL_PARA),
//            SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_QUEUETABLEMODEL_STRIPE),
            SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_QUEUETABLEMODEL_SPEED),
            "Start Time",
            SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_QUEUETABLEMODEL_TOTALNAME),
            SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_QUEUETABLEMODEL_LEFTTIME),
            SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_QUEUETABLEMODEL_STATUS), 
            /*"ID","Item Name","Source","-->","Destination","Progress","Parallelism","Stripe","Speed","Total Time","Left Time","Status"*/
            };

    List<FileTransferTask> fileTaskList = null;
    private Component parent = null;
    
    public QueueTableModel(Component parent){
        this.fileTaskList = new ArrayList<FileTransferTask>();
        this.parent = parent;
    }

    public QueueTableModel(Component parent, List<FileTransferTask> fileTaskList){
        if(fileTaskList == null){
            this.fileTaskList = new ArrayList<FileTransferTask>();
        }else{
            this.fileTaskList = fileTaskList;
        }
        this.parent = parent;
    }

    public String getColumnName(int col) {
        return TABLE_COLUMN_NAMES[col].toString();
    }

    public int getColumnCount() {
        return TABLE_COLUMN_NAMES.length;
    }


	@SuppressWarnings("unchecked")
	public Class getColumnClass(int c) {
        switch(c){
        //"ID","Item Name","Source","-->","Destination","Progress","Parellelism","Stripe","Speed","Total Time","Left Time","Status"
        case 1:
        case 6:
            return Integer.class;
        case 0:
        //case 1:
            return IconData.class;
        case 5:
            return JProgressBar.class;
        default:
            return String.class;
        }
    }

    public int getRowCount() {
        return fileTaskList.size();
    }

    public Object getValueAt(int row, int column) {
        Object value = null;
        if(row>=fileTaskList.size()) {
            return null;
        }

        FileTransferTask fileTask = (FileTransferTask)this.fileTaskList.get(row);

        switch(column){
        //"ID","Item Name","Source","-->","Destination","Progress","Parellelism","Stripe","Speed","Total Time","Left Time","Status"
        case 0:
            //ID
            ImageIcon icon = null;
            switch(fileTask.getStatus()){
            case Task.WAITING:
                icon = AppMain.icoStatusYellow;
                break;
            case Task.DONE:
                icon = new ImageIcon();
                break;
            case Task.FAILED:
                icon = AppMain.icoStatusRed;
                break;
            case Task.ONGOING:
                icon = AppMain.icoStatusGreen;
                break;
            default:
                icon = AppMain.icoHelp;
            }
            value = new IconData(icon,fileTask.getId());
            break;

        case 1:
            //Item Name
            FileInfo file = fileTask.getFile();
//            value = new IconData((file.isDirectory()||file.isSoftLink()?AppMain.icoFolder:AppMain.icoFile),ListModel.getFileName(file));
            value = new IconData((file.isDirectory()||file.isSoftLink()?AppMain.icoFolder:AppMain.icoFile),fileTask.getDisplayName());
            break;
        case 2:
            //Source
            value = fileTask.getSrcSite().name;
            break;
        case 3:
            //-->
            value = "-->";
            break;
        case 4:
            //Destination
            value = fileTask.getDestSite().name;
            break;
        case 5:
            //Progress
            JProgressBar progressBar = new JProgressBar();
            progressBar.setStringPainted(true);
            progressBar.setValue(fileTask.getProgress());
            value = progressBar;
            break;
//        case 6:
//            //Parellelism
////            value = new Integer(1);
//            value = fileTask.getParaID() + "/" + fileTask.getPara();
//            break;
//        case 7:
//            //Stripe
//            value = "";
//            break;
        case 6:
            //speed
            value = fileTask.getSpeedString();
            break;
        case 7:
            // Start Time
            value = fileTask.getStartTimeString();
            break;
        case 8:
            //Total Time
            value = fileTask.getTotalTimeString();
            break;
        case 9:
            //Left Time
            value = fileTask.getLeftTimeString();
            break;
        case 10:
            //Status
            value = fileTask.getStatusString();
            break;
        }

        return value;
    }

    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false; //return (rowIndex>0)&&(columnIndex==0);
    }

    public List<FileTransferTask> getTaskList(){
        return this.fileTaskList;
    }
    
    public void refreshTaskList() {
        HistoryManager.refreshTaskList();
        UIRefresher.refreshQueue();
    }

    @SuppressWarnings("unchecked")
	public void setTaskList(List<FileTransferTask> taskList){
//        if (taskList.size() != this.fileTaskList.size() || 
//                !this.fileTaskList.containsAll(taskList)) {
//            UIRefresher.scrollQueuePanelToBottom();
//        }
        if (taskList != null && taskList.size() > 0) {
            this.fileTaskList = taskList;
        }
        
        Collections.sort(this.fileTaskList);
        
        
    }

    public void addTask(FileTransferTask task){
        this.fileTaskList.add(task);
        UIRefresher.refreshQueue();
        
    }

    public void addTask(FileTransferTask task, int index){
        this.fileTaskList.add(index, task);
        UIRefresher.refreshQueue();
        
    }

    public void addTask(List<FileTransferTask> tasks){
        for(int i=0; i<tasks.size(); i++){
            this.fileTaskList.add(tasks.get(i));
        }
        UIRefresher.refreshQueue();
        
    }

    public void removeAll(){
        HistoryManager.deleteAllTasks();
        UIRefresher.refreshQueue();
    }

    public void removeSelected(int [] rows){
//        for(int i = rows.length-1; i>=0; i--){
//            HistoryManager.deleteTask(rows[i]);
//        }
        HistoryManager.deleteTasks(rows);
        UIRefresher.refreshQueue();
    }

    public void removeFinished(){
        HistoryManager.deleteFinishedTasks();
        UIRefresher.refreshQueue();
    }

    public void stopSelected(int[] rows){
        HistoryManager.stopTasks(rows);
        UIRefresher.refreshQueue();
    }
    
    public void restartSelected(int[] rows){
        LinkedList<FileTransferTask> newTransferList = new LinkedList<FileTransferTask>();
        for (int row:rows) {
            FileTransferTask currentTask = (FileTransferTask)this.fileTaskList.get(row);       
            if (currentTask.getStatus() != Task.ONGOING ) {
                newTransferList.add(currentTask);
            }
        }
        TransferProxy.transfer(parent,newTransferList);
        UIRefresher.refreshQueue();
    }
    
    public void restartAll() {
        
    }
    
    public void setNotification(int[] rows, NotificationType type) {
        
        HistoryManager.setNotification(getId(rows),type, true);
        
        UIRefresher.refreshQueue();
    }
    
    public void clearAllNotifications(int[] rows) {
        
        HistoryManager.clearNotifications(getId(rows));
        
        UIRefresher.refreshQueue();
    }
    
    private ArrayList<Integer> getId(int[] rows) {
        ArrayList<Integer> ids = new ArrayList<Integer>();
        
        for (int i=0;i<rows.length;i++) {
            ids.add(Integer.valueOf(((FileTransferTask)fileTaskList.get(rows[i])).getId()));
        }
        
        return ids;
    }
}