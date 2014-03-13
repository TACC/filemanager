/* 
 * Created on Dec 11, 2007
 * 
 * Developed by: Rion Dooley - dooley [at] tacc [dot] utexas [dot] edu
 * 				 TACC, Texas Advanced Computing Center
 * 
 * https://www.tacc.utexas.edu/
 */

package edu.utexas.tacc.wcs.filemanager.common.exception;

/**
 * Insert Template description here.
 * 
 * @author Rion Dooley < dooley [at] tacc [dot] utexas [dot] edu >
 *
 */
@SuppressWarnings("serial")
public class NotificationException extends RuntimeException {

    /**
     * 
     */
    public NotificationException() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @param message
     */
    public NotificationException(String message) {
        super(message);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param message
     * @param cause
     */
    public NotificationException(String message, Throwable cause) {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param cause
     */
    public NotificationException(Throwable cause) {
        super(cause);
        // TODO Auto-generated constructor stub
    }

}
