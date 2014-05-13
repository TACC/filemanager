/*
 * Portions of this file Copyright 2004-2007 Shanghai Jiaotong University
 * 
 * This file or a portion of this file is licensed under the
 * terms of the Globus Toolkit Public License, found at
 * http://www.globus.org/toolkit/legal/4.0/
 * If you redistribute this file, with or without
 * modifications, you must include this notice in the file.
 */

package edu.utexas.tacc.wcs.filemanager.client.ui.table;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.AbstractTableModel;

import org.globus.ftp.FileInfo;

import edu.utexas.tacc.wcs.filemanager.client.AppMain;
import edu.utexas.tacc.wcs.filemanager.client.ui.FileListingModel;
import edu.utexas.tacc.wcs.filemanager.client.ui.IconData;
import edu.utexas.tacc.wcs.filemanager.client.util.ResourceName;
import edu.utexas.tacc.wcs.filemanager.client.util.SGGCResourceBundle;


@SuppressWarnings("serial")
public class DetailListModel extends AbstractTableModel 
implements TableColumnModelListener, FileListingModel {
    public int sortedColumnIndex = 2;
    public boolean sortAscending = true;

    private static double[] arrSize = new double[]{1<<10, 1<<20, 1<<30, 1<<40};
    //column names of the table
    private final String [] TABLE_COLUMN_NAMES = new String[]{
    		SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_LISTMODEL_COLUMN_NAME),
    		SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_LISTMODEL_COLUMN_SIZE),
    		SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_LISTMODEL_COLUMN_TYPE),
    		SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_LISTMODEL_COLUMN_MODIFIED),
    		SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_LISTMODEL_COLUMN_ATTRIBUTES)
//    		SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_LISTMODEL_COLUMN_DESCRIPTION),
//    		SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_LISTMODEL_COLUMN_OWNER),
    		};
    
    public static ImageIcon COLUMN_UP = AppMain.icoSortUp;
    public static ImageIcon COLUMN_DOWN = AppMain.icoSortDown;

    //
    private List<FileInfo> fileList = new ArrayList<FileInfo>();

    public DetailListModel() {
        super();
    }
    
    public DetailListModel(List<FileInfo> fileList){
        super();
        this.fileList = fileList;
        sortData();
    }

    public String getColumnName(int col) {
        return TABLE_COLUMN_NAMES[col].toString();
    }

    public int getColumnCount() {
        return TABLE_COLUMN_NAMES.length;
    }

    @SuppressWarnings("unchecked")
	public Class getColumnClass(int c) {
        return getValueAt(0, c).getClass();
    }

    public int getRowCount() {
        return fileList.size();
    }

    public Object getValueAt(int row, int column) {
        Object value = null;
        if(row>=fileList.size()) {
        	return null;
        }
        FileInfo file = (FileInfo)fileList.get(row);
        switch(column){
        case 0:
            //Name
            value = new IconData((file.isDirectory()||file.isSoftLink()?AppMain.icoFolder:AppMain.icoFile), getOldFileName(file));
            
            break;
        case 1:
            //Size
            value = getSize(file);
            break;
        case 2:
            //Type
            value = getTypeName(file);
            break;
        case 3:
            //Modified
            value = (0==row) ? "" : file.getDate() + ", " + file.getTime();
            break;
        case 4:
            //Attributes
            value = (0==row) ? "" : getTypeShort(file) + getMode(file.getMode());
            break;
        case 5:
            //Description
            value = "";
            break;
        case 6:
            //Owner
            value = "";
            break;
        default:
            value = "";
            break;
        }
        return value;
    }

    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false; //return (rowIndex>0)&&(columnIndex==0);
    }
    
    @SuppressWarnings("unchecked")
	public void sortData() {
        Collections.sort(fileList, new
                FileComparator(sortedColumnIndex, sortAscending));
        this.fireTableDataChanged();
    }

    public List<FileInfo> getFileList(){
        return fileList;
    }
    
    public int getRow(FileInfo file) {
        return fileList.indexOf(file);
    }

    public void setFileList(List<FileInfo> fileList){
        this.fileList = fileList;
        sortData();
    }

    public FileInfo getFile(int index){
        return (FileInfo)fileList.get(index);
    }
    
    public FileInfo getFile(String name){
        for (FileInfo file: fileList) {
//        	if (file instanceof TGShareFileInfo) {
//        		if (file.getName().equals(name) || ((TGShareFileInfo)file).getNonce().equals(name)) return file;
//        	} else {
        		if (file.getName().equals(name)) return file;
//        	}
        }
        return null;
    }

    public static String getOldFileName(FileInfo file){
        return file.getName();
    }

    public static String getFileName(FileInfo file){
        String s=file.getName();
        if(!file.isSoftLink()) {
        	return s;
        } else if(s.indexOf(" ->")<0) {
        	return s;
        } else {
        	return s.substring(0,s.indexOf(" ->"));
        }
    }

    public static FileInfo getFileInfo(Vector<FileInfo> v){
        return (FileInfo)v.lastElement();
    }

    public static String getMode(int nMode){
        int i,j;
        StringBuffer sMode=new StringBuffer("rwxrwxrwx");
        for(i=0,j=1;i<9;i++,j=j<<1){
            if((nMode&j)!=j) {
            	sMode.setCharAt(8-i,'-');
            }
        }
        return sMode.toString();
    }

    public static byte getType(FileInfo file){
        if(file.isDevice()) {
        	return FileInfo.DEVICE_TYPE;
        } else if(file.isDirectory()) {
        	return FileInfo.DIRECTORY_TYPE;
        } else if(file.isFile()) {
        	return FileInfo.FILE_TYPE;
        } else if(file.isSoftLink()) {
        	return FileInfo.SOFTLINK_TYPE;
        } else {
        	return FileInfo.SOFTLINK_TYPE;
        }
    }

    public static String getTypeShort(FileInfo file){
        if(file.isDevice()) {
        	return "b";
        } else if(file.isDirectory()) {
        	return "d";
        } else if(file.isFile()) {
        	return "-";
        } else if(file.isSoftLink()) {
        	return "l";
        } else {
        	return "u";
        }
    }

    public static String getTypeName(FileInfo file){
        if(file.isDevice()) {
        	return SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_LISTMODEL_TYPE_DEVICE);
        } else if(file.isDirectory()) {
        	return SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_LISTMODEL_TYPE_FOLDER);
        } else if(file.isFile()) {
        	return SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_LISTMODEL_TYPE_FILE);
        } else if(file.isSoftLink()) {
        	return SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_LISTMODEL_TYPE_LINK);
        } else {
        	return SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_LISTMODEL_TYPE_UNKNOWN);
        }
    }

    public static String getSize(FileInfo file){
        String sSize;
        long nSize=file.getSize();
        NumberFormat f = NumberFormat.getInstance();
        f.setMaximumFractionDigits(1);

        if(nSize < arrSize[0]){
            sSize = String.valueOf(nSize) + " B";
        }else if(nSize < arrSize[1]){
            sSize = String.valueOf(f.format(nSize / arrSize[0])) + " KB";
        }else if(nSize < arrSize[2]){
            sSize = String.valueOf(f.format(nSize / arrSize[1])) + "MB";
        }
        else{
            sSize = String.valueOf(f.format(nSize / arrSize[2])) + "GB";
        }
        return sSize;
    }

    public static String getSpeed(long nSpeed){
        String sSize;
        NumberFormat f = NumberFormat.getInstance();
        f.setMaximumFractionDigits(1);
        if(nSpeed < arrSize[0]){
            sSize = String.valueOf(nSpeed) + " B/s";
        }else if(nSpeed < arrSize[1]){
            sSize = String.valueOf(f.format(nSpeed / arrSize[0])) + " KB/s";
        }else{
            sSize = String.valueOf(f.format(nSpeed / arrSize[1])) + "MB/s";
        }
        return sSize;
    }

    public static String getTime(long time){
        String timeString = "";
        int hour = (int)time/1000/60/60;
        int min = (int)time/1000/60%60;
        float sec = (float)time/1000%60;

        String hourString = String.valueOf(hour);
        String minString = (min>=10)?String.valueOf(min):"0"+String.valueOf(min);
        String secString = (sec>=10)?String.valueOf(sec):"0"+String.valueOf(sec);

        timeString = hourString + ":" + minString + ":" + secString;

        return timeString;
    }
    
    public Icon getColumnIcon(int column) {
        if (column==sortedColumnIndex) {
            return sortAscending ? COLUMN_UP : COLUMN_DOWN;
        }
        return null;
    }

    public void columnAdded(TableColumnModelEvent arg0) {
        // TODO Auto-generated method stub
        
    }

    public void columnMarginChanged(ChangeEvent arg0) {
        // TODO Auto-generated method stub
        
    }

    public void columnMoved(TableColumnModelEvent arg0) {
        // TODO Auto-generated method stub
        
    }

    public void columnRemoved(TableColumnModelEvent arg0) {
        // TODO Auto-generated method stub
        
    }

    public void columnSelectionChanged(ListSelectionEvent arg0) {
        // TODO Auto-generated method stub
        
    }
}
