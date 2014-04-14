/* 
 * Created on Dec 13, 2007
 * 
 * Developed by: Rion Dooley - dooley [at] tacc [dot] utexas [dot] edu
 * 				 TACC, Texas Advanced Computing Center
 * 
 * https://www.tacc.utexas.edu/
 */

package edu.utexas.tacc.wcs.filemanager.service.resources.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.log4j.Logger;
import org.restlet.Request;
import org.restlet.data.Status;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.Put;
import org.restlet.resource.ResourceException;

import edu.utexas.tacc.wcs.filemanager.common.model.BulkTransferRequest;
import edu.utexas.tacc.wcs.filemanager.common.model.Transfer;
import edu.utexas.tacc.wcs.filemanager.common.restlet.resource.AbstractApiResource;
import edu.utexas.tacc.wcs.filemanager.service.Settings;
import edu.utexas.tacc.wcs.filemanager.service.exception.AuthenticationException;
import edu.utexas.tacc.wcs.filemanager.service.manager.HistoryManager;
import edu.utexas.tacc.wcs.filemanager.service.resources.BulkTransfersResource;

/**
 * Class to query and manage the user's file transfer history. Records
 * are persisted in a back end database.
 * 
 * @author Rion Dooley < dooley [at] tacc [dot] utexas [dot] edu >
 *
 */
public class BulkTransfersResourceImpl extends AbstractApiResource implements BulkTransfersResource
{
	private static final Logger logger = Logger.getLogger(BulkTransfersResourceImpl.class);
	
    public BulkTransfersResourceImpl() {}
    
    /* (non-Javadoc)
	 * @see edu.utexas.tacc.wcs.filemanager.service.resources.impl.BulkTransfersResource#getAllTransfers()
	 */
    @Override
	@Get
	public List<Transfer> getAllTransfers() 
    {
    	int page = NumberUtils.toInt((String)Request.getCurrent().getAttributes().get("page"), 1);
    	int pageSize = NumberUtils.toInt((String)Request.getCurrent().getAttributes().get("pageSize"), Settings.DEFAULT_PAGE_SIZE);
    	
    	
        // retrieve the user's history in the paging manner requested.
    	if (StringUtils.isEmpty(dn))
        	throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, 
        			"Please supply a valid DN");
        
        logger.debug("Retrieving user history for " + dn);
        
        try 
        {
	        HistoryManager history = new HistoryManager(dn);
	        
	        List<Transfer> transfers = history.get(page, pageSize);
	        
	        logger.debug("Retrieved " + transfers.size() + 
	                    " transfer records for " + history.getUser().getUsername());
	        
	        return transfers;
        }
        catch (Throwable e)
		{
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL,
					"Failed to retrieve transfer history.", e);
		}
			  
    }
    
    /* (non-Javadoc)
	 * @see edu.utexas.tacc.wcs.filemanager.service.resources.impl.BulkTransfersResource#addMultipleTransfers(edu.utexas.tacc.wcs.filemanager.common.model.BulkTransferRequest)
	 */
    @Override
	@Post
	public List<Long> addMultipleTransfers(BulkTransferRequest bulkTransferRequest)
    {   
        if (StringUtils.isEmpty(dn))
        	throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, 
        			"Please supply a valid DN");
        
        if (bulkTransferRequest.getTransfers().isEmpty())
        	throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, 
        			"Please supply a valid file transfer.");
        try 
        {
	        HistoryManager history = new HistoryManager(dn);
	        
	        logger.debug("Adding " + bulkTransferRequest.getTransfers().size() + " transfers to user history...");
	        
	        List<Long> transferIds = history.add(bulkTransferRequest.getTransfers(), bulkTransferRequest.getEpr(), bulkTransferRequest.getNotificationType());
	        
	        return transferIds;
	    }
	    catch (Throwable e)
		{
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL,
					"Failed to retrieve transfer history.", e);
		}
    }
    
    /* (non-Javadoc)
	 * @see edu.utexas.tacc.wcs.filemanager.service.resources.impl.BulkTransfersResource#update(edu.utexas.tacc.wcs.filemanager.common.model.BulkTransferRequest)
	 */
    @Override
	@Put
    public void update(BulkTransferRequest bulkTransferRequest) 
    {   
        if (StringUtils.isEmpty(dn))
        	throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, 
        			"Please supply a valid DN");
        
        if (bulkTransferRequest.getTransfers().isEmpty())
        	throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, 
        			"Please supply a valid file transfer.");
        
        try
        {
	        HistoryManager history = new HistoryManager(dn);
	        
	        for (Transfer transfer: bulkTransferRequest.getTransfers()) {
	            if (transfer.getId() == null)
	            	throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, 
	            			"Please supply a valid file transfer ID.");
	        
	            logger.debug("Updating status of file transfer " + transfer.getId() + 
	                    " to " + Transfer.getStatusString(transfer.getStatus()) + 
	                    " with " + transfer.getProgress() + "% completed.");
	            
	            history.update(transfer);
	        }
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
	 * @see edu.utexas.tacc.wcs.filemanager.service.resources.impl.BulkTransfersResource#clearAllTransfers()
	 */
    @Override
	@Delete
	public void clearAllTransfers() 
    {
        if (StringUtils.isEmpty(dn))
            throw new AuthenticationException("Please supply a valid DN " + dn);
        try
        {
	        HistoryManager history = new HistoryManager(dn);
	        
	        logger.debug("Clearing all transfers for dn =  " + dn);
	        
	        history.removeAll();
	    }
	    catch (Throwable e)
		{
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL,
					"Failed to clear transfer history.", e);
		}
    }
}
