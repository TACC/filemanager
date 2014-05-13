/* 
 * Created on Jul 16, 2008
 * 
 * Developed by: Rion Dooley - dooley [at] tacc [dot] utexas [dot] edu
 * 				 TACC, Texas Advanced Computing Center
 * 
 * https://www.tacc.utexas.edu/
 */

package edu.utexas.tacc.wcs.filemanager.client.exception;

/**
 * Insert Template description here.
 * 
 * @author Rion Dooley < dooley [at] tacc [dot] utexas [dot] edu >
 *
 */
@SuppressWarnings("serial")
public class BandwidthCalculationException extends RuntimeException {

    /**
     * 
     */
    public BandwidthCalculationException() {}
    
    /**
     * @param arg0
     */
    public BandwidthCalculationException(String arg0) {
        super(arg0);
    }

    /**
     * @param arg0
     */
    public BandwidthCalculationException(Throwable arg0) {
        super(arg0);
    }

    /**
     * @param arg0
     * @param arg1
     */
    public BandwidthCalculationException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

}
