/* 
 * Created on Nov 8, 2007
 * 
 * Developed by: Rion Dooley - dooley [at] tacc [dot] utexas [dot] edu
 * 				 TACC, Texas Advanced Computing Center
 * 
 * https://www.tacc.utexas.edu/
 */

package edu.utexas.tacc.wcs.filemanager.service.exception;

@SuppressWarnings("serial")
public class ResourceException extends RuntimeException {

    public ResourceException() {
        super();
        // TODO Auto-generated constructor stub
    }

    public ResourceException(String message) {
        super(message);
        // TODO Auto-generated constructor stub
    }

    public ResourceException(String message, Throwable cause) {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }

    public ResourceException(Throwable cause) {
        super(cause);
        // TODO Auto-generated constructor stub
    }

}
