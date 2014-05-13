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

import java.text.Collator;
import java.util.Comparator;

import org.globus.ftp.FileInfo;


@SuppressWarnings("unchecked")
public class ListSorter implements Comparator {
	public static final int BYNAME=1;
    public static final int BYSIZE=2;
    public static final int BYDATE=3;
    int nOrder;

    public ListSorter(){
        this(BYNAME);
    }
    public ListSorter(int nOrder){
        this.nOrder=nOrder;
    }

    public int compare(Object o1,Object o2){
        switch(nOrder){
        case BYSIZE:
            break;
        case BYDATE:
            break;
        case BYNAME:
        default:
            if(o1 instanceof FileInfo && o2 instanceof FileInfo){
                FileInfo file1 = (FileInfo)o1;
                FileInfo file2 = (FileInfo)o2;
                int t1=ListModel.getType(file1), t2=ListModel.getType(file2);
                String s1=ListModel.getFileName(file1),s2=ListModel.getFileName(file2);
                if(t1!=t2){
                    if(FileInfo.DIRECTORY_TYPE==t1) {
                    	return -1;
                    }
                    else if(FileInfo.DIRECTORY_TYPE==t2) {
                    	return 1;
                    }
                    else if(FileInfo.SOFTLINK_TYPE==t1) {
                    	return -1;
                    }
                    else if(FileInfo.SOFTLINK_TYPE==t2) {
                    	return 1;
                    }
                    else if(FileInfo.FILE_TYPE==t1) {
                    	return -1;
                    }
                    else if(FileInfo.FILE_TYPE==t2) {
                    	return 1;
                    }
                    else if(FileInfo.UNKNOWN_TYPE==t1) {
                    	return -1;
                    }
                    else {
                    	return 1;
                    }
                }
                else{
                    if(s1.equals("..")) {
                    	return -1;
                    }
                    else if(s2.equals("..")) {
                    	return 1;
                    }
                    return Collator.getInstance().compare(s1,s2);
                }
            }
            break;
        }
        return 0;
    }
}
