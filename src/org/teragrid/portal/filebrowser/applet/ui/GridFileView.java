/* 
 * Created on Nov 16, 2007
 * 
 * Developed by: Rion Dooley - dooley [at] tacc [dot] utexas [dot] edu
 * 				 TACC, Texas Advanced Computing Center
 * 
 * https://www.tacc.utexas.edu/
 */

package org.teragrid.portal.filebrowser.applet.ui;

import java.awt.Cursor;
import java.util.ArrayList;
import java.util.Hashtable;

import org.globus.ftp.FileInfo;

/**
 * Insert Template description here.
 * 
 * @author Rion Dooley < dooley [at] tacc [dot] utexas [dot] edu >
 *
 */
public interface GridFileView {

//    public int getSelectedRow();
//    
//    public int[] getSelectedRows();
    
    public FileInfo getSelectedFileInfo();
    
    public Hashtable<String,ArrayList<FileInfo>> getSelectedFileTable();
    
    public Object getModel();
    
    public void setCursor(Cursor cursor);
    
}
