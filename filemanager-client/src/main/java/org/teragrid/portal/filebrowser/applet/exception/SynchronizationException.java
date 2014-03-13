/* 
 * Created on Nov 8, 2007
 * 
 * Developed by: Rion Dooley - dooley [at] tacc [dot] utexas [dot] edu
 * 				 TACC, Texas Advanced Computing Center
 * 
 * https://www.tacc.utexas.edu/
 */

package org.teragrid.portal.filebrowser.applet.exception;

@SuppressWarnings("serial")
public class SynchronizationException extends RuntimeException {

    public SynchronizationException(String string) {
        // TODO Auto-generated constructor stub
    }

    /**
     * 
     */
    public SynchronizationException() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @param message
     * @param cause
     */
    public SynchronizationException(String message, Throwable cause) {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param cause
     */
    public SynchronizationException(Throwable cause) {
        super(cause);
        // TODO Auto-generated constructor stub
    }

}
