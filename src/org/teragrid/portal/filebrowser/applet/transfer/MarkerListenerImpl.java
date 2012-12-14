/* 
 * Created on Jul 21, 2008
 * 
 * Developed by: Rion Dooley - dooley [at] tacc [dot] utexas [dot] edu
 * 				 TACC, Texas Advanced Computing Center
 * 
 * https://www.tacc.utexas.edu/
 */

package org.teragrid.portal.filebrowser.applet.transfer;

import java.util.LinkedList;
import java.util.List;

import org.globus.ftp.ByteRange;
import org.globus.ftp.ByteRangeList;
import org.globus.ftp.GridFTPRestartMarker;
import org.globus.ftp.Marker;
import org.globus.ftp.MarkerListener;
import org.globus.ftp.PerfMarker;
import org.globus.ftp.exception.PerfMarkerException;
import org.globus.io.urlcopy.UrlCopyListener;
import org.teragrid.portal.filebrowser.applet.util.LogManager;

/**
 * Insert Template description here.
 * 
 * @author Rion Dooley < dooley [at] tacc [dot] utexas [dot] edu >
 *
 */
@SuppressWarnings({"unused"})
public class MarkerListenerImpl implements MarkerListener {

    public ByteRangeList list = new ByteRangeList();
    private ByteRange range;
    private UrlCopyListener listener;
    private List<UrlCopyListener> listeners = new LinkedList<UrlCopyListener>();
    private UrlCopy urlCopy;
    private long aggregateStripedDateTransferred = 0;
    
    public MarkerListenerImpl(ByteRange range, List<UrlCopyListener> listeners, UrlCopy urlCopy) {
        this.listeners = listeners;
        this.range = range;
        this.urlCopy = urlCopy;
    }
    
    /* (non-Javadoc)
     * @see org.globus.ftp.MarkerListener#markerArrived(org.globus.ftp.Marker)
     */
    public void markerArrived(Marker m) {
        if (urlCopy.isCanceled()) {
            try {
                urlCopy.srcFTP.abort();
            } catch (Exception e) {
                LogManager.error("Failed to stop transfer.",e);
            }
        }
        
        if (m instanceof GridFTPRestartMarker) {
            restartMarkerArrived((GridFTPRestartMarker) m);
        } else if (m instanceof PerfMarker) {
            perfMarkerArrived((PerfMarker) m);
        } else {
            LogManager.error("Received unsupported marker type");
        }
    };

    private void restartMarkerArrived(GridFTPRestartMarker marker) {
        LogManager.info("--> restart marker arrived:");
        list.merge(marker.toVector());
        LogManager.info("Current transfer state: " + list.toFtpCmdArgument());
    }

    private void perfMarkerArrived(PerfMarker marker) {
        
        long transferedBytes = 0;
        
        LogManager.info("--> perf marker arrived");
        // time stamp
        LogManager.info("Timestamp = " + marker.getTimeStamp());
        
        // stripe index
        long index = -1;
        if (marker.hasStripeIndex()) {
            try {
                LogManager.info("Stripe index =" + marker.getStripeIndex());
                index = marker.getStripeIndex();
            } catch (PerfMarkerException e) {
                LogManager.error(e.toString());
            } 
        }else {
            LogManager.info("Stripe index: not present");
        }
        
        try {
            transferedBytes = marker.getStripeBytesTransferred();
            LogManager.debug("Striped bytes transferred = " + transferedBytes);
        } catch (PerfMarkerException e) {
            LogManager.error("Failed to handle perf marker.",e);
        }
        
        this.aggregateStripedDateTransferred += transferedBytes;
        
        if (index == 0) {
            for(UrlCopyListener listener: listeners) {
                listener.transfer(aggregateStripedDateTransferred - this.range.from, this.range.to - this.range.from);
                this.aggregateStripedDateTransferred = 0;
            }
        }
        
        // total stripe count
        if (marker.hasTotalStripeCount()) {
            try {
                LogManager.info("Total stripe count = " 
                             + marker.getTotalStripeCount());
            } catch (PerfMarkerException e) {
                LogManager.error(e.toString());
            } 
        }else {
            LogManager.info("Total stripe count: not present");
        }
    }//PerfMarkerArrived   
}//class MarkerListenerImpl
