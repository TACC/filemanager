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

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;

import org.globus.ftp.FileInfo;
import org.teragrid.portal.filebrowser.applet.AppMain;
import org.teragrid.portal.filebrowser.applet.util.ResourceName;
import org.teragrid.portal.filebrowser.applet.util.SGGCResourceBundle;


@SuppressWarnings("serial")
public class ListModel extends AbstractTableModel implements FileListingModel {
	private static double[] arrSize = new double[]{1<<10, 1<<20, 1<<30, 1<<40};
    //column names of the table
    private final String [] TABLE_COLUMN_NAMES = new String[]{
    		SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_LISTMODEL_COLUMN_NAME),
    		};
    //
    private List<FileInfo> fileList = new ArrayList<FileInfo>();

    public ListModel(List<FileInfo> fileList){
        this.fileList = fileList;
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

        default:
            value = "";
            break;
        }
        return value;
    }

    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false; //return (rowIndex>0)&&(columnIndex==0);
    }

    public List<FileInfo> getFileList(){
        return fileList;
    }

    public void setFileList(List<FileInfo> fileList){
        this.fileList = fileList;
    }

    public FileInfo getFile(int index){
        return (FileInfo)fileList.get(index);
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
    
    public static String getDate(long time) {
        if (time == 0) 
            return "---";
        
        String sDate = null;
        Calendar date = Calendar.getInstance();
        date.setTimeInMillis(time);
        Calendar today = Calendar.getInstance();
        
        int daysago = today.get(Calendar.DAY_OF_YEAR) - date.get(Calendar.DAY_OF_YEAR);
        String formattedDate = ""; 
        if (daysago == 0) {
            sDate = "hh:mm a";
        } else if (daysago < 7) {
            sDate = "EEE, hh:mm a"; 
        } else {
            sDate = "MMM d, hh:mm a";
        }
        
        formattedDate = new SimpleDateFormat(sDate).format(date.getTime());

        return formattedDate;
    }
    
    @SuppressWarnings("unused")
	private static String resolveDay(int day) {
        switch (day) {
        case Calendar.SUNDAY:
            return "Sun";
        case Calendar.MONDAY:
            return "Mon";
        case Calendar.TUESDAY:
            return "Tue";
        case Calendar.WEDNESDAY:
            return "Wed";
        case Calendar.THURSDAY:
            return "Thu";
        case Calendar.FRIDAY:
            return "Fri";
        case Calendar.SATURDAY:
            return "Sat";
        }
        
        return "";
            
    }
}
