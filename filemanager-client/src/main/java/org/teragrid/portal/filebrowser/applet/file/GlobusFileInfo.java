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
    
    private int globusMode = 0;
    
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
        return new Integer(globusMode);
    }

    /* (non-Javadoc)
     * @see org.teragrid.portal.filebrowser.applet.file.GenericFileInfo#setMode(int)
     */
    public void setMode(int mode) {
    	this.globusMode = mode;
    }
    
    public int getMode() {
    	return globusMode;
    }

    public String getModeAsString() {
        StringBuffer modeStr = new StringBuffer();
        for(int j=2;j>=0;j--) {
            int oct = 0;
            for(int i=2;i>=0;i--) {
                if ((globusMode & (1 << j*3+i)) != 0) {
                    oct += (int)Math.pow(2,i);
                }
            }
            modeStr.append(String.valueOf(oct));
        }
        return modeStr.toString();
    }
    public boolean userCanRead() {
        return ((globusMode & (1 << 8)) != 0);
      }

      public boolean userCanWrite() {
        return ((globusMode & (1 << 7)) != 0);
      }

      public boolean userCanExecute() {
        return ((globusMode & (1 << 6)) != 0);
      }

      public boolean groupCanRead() {
        return ((globusMode & (1 << 5)) != 0);
      }

      public boolean groupCanWrite() {
        return ((globusMode & (1 << 4)) != 0);
      }

      public boolean groupCanExecute() {
        return ((globusMode & (1 << 3)) != 0);
      }

      public boolean allCanRead() {
        return ((globusMode & (1 << 2)) != 0);
      }

      public boolean allCanWrite() {
        return ((globusMode & (1 << 1)) != 0);
      }

      public boolean allCanExecute() {
        return ((globusMode & (1 << 0)) != 0);
      }
    
      // --------------------------------
      
      public String toString() {
          StringBuffer buf = new StringBuffer();
          buf.append("FileInfo: ");
          buf.append(getName() + " ");
          buf.append(getSize() + " ");
          buf.append(getDate() + " ");
          buf.append(getTime() + " ");
          
          if (isDirectory()) {
              buf.append("directory");
          } else if (isFile()) {
              buf.append("file");
          } else if (isSoftLink()) {
              buf.append("softlink");
          } else {
              buf.append("unknown type");
          }
          buf.append(" "+getModeAsString());
          
          return buf.toString();
      }

    /* (non-Javadoc)
     * @see org.teragrid.portal.filebrowser.applet.file.GenericFileInfo#setPermissions(java.lang.Object)
     */
    public void setPermissions(Object permissionObject) {}

}
