/* 
 * Created on Dec 12, 2007
 * 
 * Developed by: Rion Dooley - dooley [at] tacc [dot] utexas [dot] edu
 * 				 TACC, Texas Advanced Computing Center
 * 
 * https://www.tacc.utexas.edu/
 */

package edu.utexas.tacc.wcs.filemanager.service.exception;

/**
 * Insert Template description here.
 * 
 * @author Rion Dooley < dooley [at] tacc [dot] utexas [dot] edu >
 *
 */
@SuppressWarnings("serial")
public class PermissionException extends RuntimeException {

    /**
     * 
     */
    public PermissionException() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @param message
     */
    public PermissionException(String message) {
        super(message);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param message
     * @param cause
     */
    public PermissionException(String message, Throwable cause) {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param cause
     */
    public PermissionException(Throwable cause) {
        super(cause);
        // TODO Auto-generated constructor stub
    }

}
