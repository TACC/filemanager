/* 
 * Created on Aug 23, 2008
 * 
 * Developed by: Rion Dooley - dooley [at] tacc [dot] utexas [dot] edu
 * 				 TACC, Texas Advanced Computing Center
 * 
 * https://www.tacc.utexas.edu/
 */

package org.teragrid.portal.filebrowser.applet.file;

/**
 * Interface to generalize the abstraction of the globus FileInfo class.
 * 
 * @author Rion Dooley < dooley [at] tacc [dot] utexas [dot] edu >
 *
 */
public interface GenericFileInfo {

    public void setMode(int mode);
    
    public Object getPermissions();
    public void setPermissions(Object permissionObject);
    
}
