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
import org.restlet.resource.Post;
import org.restlet.resource.ResourceException;

import edu.utexas.tacc.wcs.filemanager.common.model.enumeration.NotificationType;
import edu.utexas.tacc.wcs.filemanager.common.restlet.resource.AbstractApiResource;
import edu.utexas.tacc.wcs.filemanager.service.manager.HistoryManager;
import edu.utexas.tacc.wcs.filemanager.service.resources.TransferNotificationsResource;

/**
 * Class to query and manage the user's file transfer history. Records
 * are persisted in a back end database.
 * 
 * @author Rion Dooley < dooley [at] tacc [dot] utexas [dot] edu >
 *
 */
public class TransferNotificationsResourceImpl extends AbstractApiResource implements TransferNotificationsResource
{
	private static final Logger logger = Logger.getLogger(TransferNotificationsResourceImpl.class);
	
	private Long transferId;
	
    public TransferNotificationsResourceImpl() {}
    
    /* (non-Javadoc)
	 * @see edu.utexas.tacc.wcs.filemanager.service.resources.impl.TransferNotificationsResource#addNotification()
	 */
    @Override
	@Post()
	public void addNotification() 
    {
        if (StringUtils.isEmpty(dn))
            throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,
					"Please supply a valid DN " + dn);
        
        String tid = (String)Request.getCurrent().getAttributes().get("transferId");
        if (!NumberUtils.isNumber(tid)) 
        	throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,
        			"Please supply a valid transfer ID.");
        
        String notificationType = (String)Request.getCurrent().getAttributes().get("type");
        if (StringUtils.isEmpty(notificationType)) 
        	throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,
					"Please supply a valid notification type");
        
        try
        {
	        Long tId = null;
	        
	        try {
	            tId = new Long(transferId);
	        } catch (NumberFormatException e) {
	            throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,
						"Please supply a valid transfer ID");
	        }
	        
	        HistoryManager history = new HistoryManager(dn);
	    
	        logger.debug("Setting a " + notificationType + " notification for transfer " + transferId + "...");
	        
	        history.notify(tId, NotificationType.getType(notificationType));
        } 
        catch(ResourceException e)
        {
     	   throw e;
        }
        catch(Throwable e)
        {
     	   throw new ResourceException(Status.SERVER_ERROR_INTERNAL,
 					"Failed to create notification", e);
        }  
    }
    
	/* (non-Javadoc)
	 * @see edu.utexas.tacc.wcs.filemanager.service.resources.impl.TransferNotificationsResource#removeNotification()
	 */
	@Override
	@Delete
    public void removeNotification() 
    {
		if (StringUtils.isEmpty(dn))
            throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,
					"Please supply a valid DN " + dn);
        
        String tid = (String)Request.getCurrent().getAttributes().get("transferId");
        if (!NumberUtils.isNumber(tid)) 
        	throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,
        			"Please supply a valid transfer ID.");
        
        String notificationType = (String)Request.getCurrent().getAttributes().get("type");
        if (StringUtils.isEmpty(notificationType)) 
        	throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,
					"Please supply a valid notification type");
        
        try
        {
	        Long tId = null;
	      
	        try {
	            tId = new Long(transferId);
	        } catch (NumberFormatException e) {
	            throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,
						"Please supply a valid transfer ID");
	        }
	        
	        HistoryManager history = new HistoryManager(dn);
	         
	        logger.debug("Removing a " + notificationType + " notification for transfer " + transferId + "...");
	        
	        history.denotify(tId, NotificationType.getType(notificationType));
        } 
        catch(ResourceException e)
        {
     	   throw e;
        }
        catch(Throwable e)
        {
     	   throw new ResourceException(Status.SERVER_ERROR_INTERNAL,
 					"Failed to remove notification", e);
        }  
    }
}
