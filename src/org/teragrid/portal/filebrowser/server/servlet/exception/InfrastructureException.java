/* 
 * Created on Dec 12, 2007
 * 
 * Developed by: Rion Dooley - dooley [at] tacc [dot] utexas [dot] edu
 * 				 TACC, Texas Advanced Computing Center
 * 
 * https://www.tacc.utexas.edu/
 */

package org.teragrid.portal.filebrowser.server.servlet.exception;

/**
 * Insert Template description here.
 * 
 * @author Rion Dooley < dooley [at] tacc [dot] utexas [dot] edu >
 *
 */
@SuppressWarnings("serial")
public class InfrastructureException extends RuntimeException {

    /**
     * 
     */
    public InfrastructureException() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @param message
     */
    public InfrastructureException(String message) {
        super(message);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param message
     * @param cause
     */
    public InfrastructureException(String message, Throwable cause) {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param cause
     */
    public InfrastructureException(Throwable cause) {
        super(cause);
        // TODO Auto-generated constructor stub
    }

}
