/* 
 * Created on Aug 23, 2008
 * 
 * Developed by: Rion Dooley - dooley [at] tacc [dot] utexas [dot] edu
 * 				 TACC, Texas Advanced Computing Center
 * 
 * https://www.tacc.utexas.edu/
 */

package org.teragrid.portal.filebrowser.applet.file;

import org.globus.ftp.FileInfo;

/**
 * Wrapper for Globus FileInfo to extend GenericFileInfo 
 * 
 * @author Rion Dooley < dooley [at] tacc [dot] utexas [dot] edu >
 *
 */
public class GlobusFileInfo extends FileInfo implements GenericFileInfo {
    
    private int mode = 0;
    
    public GlobusFileInfo() {
        super();
    }
    
    public GlobusFileInfo(FileInfo file) {
        super();
        setName(file.getName().trim().replaceAll("\r", "").replaceAll("\n", ""));
        setSize(file.getSize());
        setDate(file.getDate());
        setTime(file.getTime());
        setFileType(file.isDirectory()?DIRECTORY_TYPE:file.isDevice()?DEVICE_TYPE:file.isSoftLink()?SOFTLINK_TYPE:FILE_TYPE);
        setMode(file.getMode());
    }
    
    /* (non-Javadoc)
     * @see org.teragrid.portal.filebrowser.applet.file.GenericFileInfo#getPermissions()
     */
    public Object getPermissions() {
        return new Integer(mode);
    }

    /* (non-Javadoc)
     * @see org.teragrid.portal.filebrowser.applet.file.GenericFileInfo#setMode(int)
     */
    public void setMode(int mode) {
    	this.mode = mode;
    }
    
    public int getMode() {
    	return mode;
    }

    public String getModeAsString() {
        StringBuffer modeStr = new StringBuffer();
        for(int j=2;j>=0;j--) {
            int oct = 0;
            for(int i=2;i>=0;i--) {
                if ((mode & (1 << j*3+i)) != 0) {
                    oct += (int)Math.pow(2,i);
                }
            }
            modeStr.append(String.valueOf(oct));
        }
        return modeStr.toString();
    }

    /* (non-Javadoc)
     * @see org.teragrid.portal.filebrowser.applet.file.GenericFileInfo#setPermissions(java.lang.Object)
     */
    public void setPermissions(Object permissionObject) {}

}
