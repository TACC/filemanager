/* 
 * Created on Dec 13, 2007
 * 
 * Developed by: Rion Dooley - dooley [at] tacc [dot] utexas [dot] edu
 * 				 TACC, Texas Advanced Computing Center
 * 
 * https://www.tacc.utexas.edu/
 */

package edu.utexas.tacc.wcs.filemanager.service.resources.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;
import org.restlet.Request;
import org.restlet.data.Status;
import org.restlet.resource.Delete;
import org.restlet.resource.Put;
import org.restlet.resource.ResourceException;

import edu.utexas.tacc.wcs.filemanager.common.model.Transfer;
import edu.utexas.tacc.wcs.filemanager.common.restlet.resource.AbstractApiResource;
import edu.utexas.tacc.wcs.filemanager.service.exception.AuthenticationException;
import edu.utexas.tacc.wcs.filemanager.service.manager.HistoryManager;
import edu.utexas.tacc.wcs.filemanager.service.resources.TransfersResource;

/**
 * Class to query and manage the user's file transfer history. Records
 * are persisted in a back end database.
 * 
 * @author Rion Dooley < dooley [at] tacc [dot] utexas [dot] edu >
 *
 */
public class TransfersResourceImpl extends AbstractApiResource implements TransfersResource
{
	private static final Logger logger = Logger.getLogger(TransfersResourceImpl.class);
	
	private Long transferId;
	
    public TransfersResourceImpl() {}
    
    /* (non-Javadoc)
	 * @see edu.utexas.tacc.wcs.filemanager.service.resources.impl.TransfersResource#update(edu.utexas.tacc.wcs.filemanager.service.model.Transfer)
	 */
    @Override
	@Put
    public void update(Transfer transfer) 
    {   
        if (StringUtils.isEmpty(dn))
        	throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, 
        			"Please supply a valid DN");
        
        String tid = (String)Request.getCurrent().getAttributes().get("transferId");
        if (!NumberUtils.isNumber(tid)) 
        	throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,
        			"Please supply a valid transfer ID.");
        
        if (transfer == null) {
        	throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, 
        			"Please supply a valid file transfer.");
        }
        
        try
        {
	        if (!transfer.getId().equals(Long.valueOf(tid)))
	            throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, 
	            			"POST transform id does not match the id in the url.");
	        
	        HistoryManager history = new HistoryManager(dn);
	        
	        logger.debug("Updating status of file transfer " + transfer.getId() + 
	                    " to " + transfer.getStatusString(transfer.getStatus()) + 
	                    " with " + transfer.getProgress() + "% completed.");
	            
	        history.update(transfer);
        } 
        catch (ResourceException e) 
        {
        	throw e;
        }
        catch (Throwable e)
		{
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL,
					"Failed to update transfers", e);
		}
    }
    
    /* (non-Javadoc)
	 * @see edu.utexas.tacc.wcs.filemanager.service.resources.impl.TransfersResource#removeTransfer()
	 */
    @Override
	@Delete
    public void removeTransfer() 
    {
        if (StringUtils.isEmpty(dn))
            throw new AuthenticationException("Please supply a valid DN " + dn);
        
        String tid = (String)Request.getCurrent().getAttributes().get("transferId");
        if (!NumberUtils.isNumber(tid)) 
        	throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,
        			"Please supply a valid transfer ID.");
        
        try
        {
	        HistoryManager history = new HistoryManager(dn);
	        
	        try 
	        {
	        	Long transferId = Long.valueOf(tid);
	            
	            logger.debug("Removing transfer " + transferId + " from user history...");
	            
	            history.remove(transferId);
	            
	        } 
	        catch (NumberFormatException e) 
	        {
	        	throw new ResourceException(Status.SERVER_ERROR_INTERNAL,
						"Failed to delete transfer " + transferId, e);
	        }
	    } 
	    catch (Throwable e)
		{
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL,
					"Failed to update transfer " + tid, e);
		}
    }
}
