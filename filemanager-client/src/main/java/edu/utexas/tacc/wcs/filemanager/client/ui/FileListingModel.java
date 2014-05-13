/* 
 * Created on Oct 19, 2007
 * 
 * Developed by: Rion Dooley - dooley [at] tacc [dot] utexas [dot] edu
 * 				 TACC, Texas Advanced Computing Center
 * 
 * https://www.tacc.utexas.edu/
 */

package edu.utexas.tacc.wcs.filemanager.client.ui;

import java.util.List;

import org.globus.ftp.FileInfo;

public interface FileListingModel {

    public List<FileInfo> getFileList();
    public void setFileList(List<FileInfo> fileList);
}
