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
import edu.utexas.tacc.wcs.filemanager.service.resources.BulkAddNotificationsResource;

/**
 * Notification class to bulk add and remove notifications for file transfer
 * completions in the file manager.
 * 
 * @author Rion Dooley < dooley [at] tacc [dot] utexas [dot] edu >
 *
 */
public class BulkAddNotificationsResourceImpl extends AbstractApiResource implements BulkAddNotificationsResource
{	
	private static final Logger logger = Logger.getLogger(BulkAddNotificationsResourceImpl.class);
    
	public BulkAddNotificationsResourceImpl() {
		
	}
    
    
    /* (non-Javadoc)
     * @see org.teragrid.portal.filebrowser.server.servlet.TGNotification#setNotification(java.lang.String, long[], java.lang.String, boolean)
     */
	/* (non-Javadoc)
	 * @see edu.utexas.tacc.wcs.filemanager.service.resources.impl.BulkAddNotificationsResource#addAll(edu.utexas.tacc.wcs.filemanager.common.model.BulkNotificationRequest)
	 */
	@Override
	@Post
	public void addAll(BulkNotificationRequest bulkNotifications) 
    {
        if (StringUtils.isEmpty(dn))
            throw new ResourceException(Status.CLIENT_ERROR_FORBIDDEN,
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
	        logger.error("Received " + bulkNotifications.getTransferIds().size() + " ids for notification:");
		    
	        HistoryManager history = new HistoryManager(dn);
	        
	        for (Long transferId: bulkNotifications.getTransferIds())
	        {   
	            logger.debug("Setting a " + bulkNotifications.getType() + " notification for transfer " + transferId + "...");
	            
	            history.notify(transferId, bulkNotifications.getType());
	        }
        } 
        catch(ResourceException e)
        {
     	   throw e;
        }
        catch(Throwable e)
        {
     	   throw new ResourceException(Status.SERVER_ERROR_INTERNAL,
 					"Failed to create notifications", e);
        }  
    }
}
