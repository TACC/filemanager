/* 
 * Created on Dec 16, 2007
 * 
 * Developed by: Rion Dooley - dooley [at] tacc [dot] utexas [dot] edu
 * 				 TACC, Texas Advanced Computing Center
 * 
 * https://www.tacc.utexas.edu/
 */

package edu.utexas.tacc.wcs.filemanager.service.resources.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.restlet.data.Status;
import org.restlet.resource.Post;
import org.restlet.resource.ResourceException;

import edu.utexas.tacc.wcs.filemanager.common.model.BulkNotificationRequest;
import edu.utexas.tacc.wcs.filemanager.common.restlet.resource.AbstractApiResource;
import edu.utexas.tacc.wcs.filemanager.service.manager.HistoryManager;
import edu.utexas.tacc.wcs.filemanager.service.resources.BulkDeleteNotificationsResource;

/**
 * Notification class to bulk add and remove notifications for file transfer
 * completions in the file manager.
 * 
 * @author Rion Dooley < dooley [at] tacc [dot] utexas [dot] edu >
 *
 */
public class BulkDeleteNotificationsResourceImpl extends AbstractApiResource implements BulkDeleteNotificationsResource
{	
	private static final Logger logger = Logger.getLogger(BulkDeleteNotificationsResourceImpl.class);
    
	public BulkDeleteNotificationsResourceImpl(){}
    
	/* (non-Javadoc)
	 * @see edu.utexas.tacc.wcs.filemanager.service.resources.impl.BulkDeleteNotificationsResource#removeAll(edu.utexas.tacc.wcs.filemanager.common.model.BulkNotificationRequest)
	 */
	@Override
	@Post
	public void removeAll(BulkNotificationRequest bulkNotifications) 
    {
        if (StringUtils.isEmpty(dn))
            throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,
					"Please supply a valid DN ");
        
        if (bulkNotifications.getTransferIds().isEmpty()) {
            throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,
					"Please supply a valid transfer ID");
        }
        
        if (bulkNotifications.getType() == null) {
            throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,
					"Please supply a valid notification type");
        }
        
        try
        {
	        logger.error("Received " + bulkNotifications.getTransferIds().size() + " ids for denotification:");
	        
	        HistoryManager history = new HistoryManager(dn);
	        
	        for (Long transferId: bulkNotifications.getTransferIds())
	        {   
	            logger.debug("Removing a " + bulkNotifications.getType() + " notification for transfer " + transferId + "...");
	            history.denotify(transferId, bulkNotifications.getType());
	        }
        } 
        catch(ResourceException e)
        {
     	   throw e;
        }
        catch(Throwable e)
        {
     	   throw new ResourceException(Status.SERVER_ERROR_INTERNAL,
 					"Failed to delete notifications", e);
        }
    }

}
