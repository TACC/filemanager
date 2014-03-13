/* 
 * Created on Aug 23, 2008
 * 
 * Developed by: Rion Dooley - dooley [at] tacc [dot] utexas [dot] edu
 * 				 TACC, Texas Advanced Computing Center
 * 
 * https://www.tacc.utexas.edu/
 */

package org.teragrid.portal.filebrowser.applet.file;

import java.io.File;

/**
 * Wrapper for local file objects.
 * 
 * @author Rion Dooley < dooley [at] tacc [dot] utexas [dot] edu >
 *
 */
@SuppressWarnings("serial")
public class LocalFileInfo extends File implements GenericFileInfo {
    int permissions = 0;
    
    public LocalFileInfo() {
        super("");
    }
    
    public LocalFileInfo(File file) {
        super(file.getAbsolutePath());
    }
    
    /* (non-Javadoc)
     * @see org.teragrid.portal.filebrowser.applet.file.GenericFileInfo#getPermissions()
     */
    public Object getPermissions() {
        return permissions;
    }

    /* (non-Javadoc)
     * @see org.teragrid.portal.filebrowser.applet.file.GenericFileInfo#setMode(int)
     */
    public void setMode(int mode) {
    	this.permissions = mode;
    }

    /* (non-Javadoc)
     * @see org.teragrid.portal.filebrowser.applet.file.GenericFileInfo#setPermissions(java.lang.Object)
     */
    public void setPermissions(Object permissionObject) {
    	this.permissions = ((Integer)permissionObject).intValue();
    }
    
}
