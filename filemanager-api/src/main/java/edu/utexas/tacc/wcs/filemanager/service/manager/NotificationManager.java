/* 
 * Created on Dec 12, 2007
 * 
 * Developed by: Rion Dooley - dooley [at] tacc [dot] utexas [dot] edu
 * 				 TACC, Texas Advanced Computing Center
 * 
 * https://www.tacc.utexas.edu/
 */

package edu.utexas.tacc.wcs.filemanager.service.manager;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.PersistenceException;

import org.apache.log4j.Logger;

import edu.utexas.tacc.wcs.filemanager.common.model.Notification;
import edu.utexas.tacc.wcs.filemanager.common.model.Task;
import edu.utexas.tacc.wcs.filemanager.common.model.Transfer;
import edu.utexas.tacc.wcs.filemanager.common.model.User;
import edu.utexas.tacc.wcs.filemanager.common.model.enumeration.NotificationType;
import edu.utexas.tacc.wcs.filemanager.service.dao.NotificationDAO;
import edu.utexas.tacc.wcs.filemanager.service.dao.TransferDAO;
import edu.utexas.tacc.wcs.filemanager.service.exception.NotificationException;
import edu.utexas.tacc.wcs.filemanager.service.exception.PermissionException;
import edu.utexas.tacc.wcs.filemanager.service.notification.EmailMessage;



/**
 * Manager to monitor, register, and unregister notifications for file
 * transfers.
 * 
 * @author Rion Dooley < dooley [at] tacc [dot] utexas [dot] edu >
 *
 */
public class NotificationManager {

	private static final Logger logger = Logger.getLogger(NotificationManager.class);
	
    public static void register(List<Long> transferIds, NotificationType type, User user) 
    throws NotificationException, PermissionException{
        
        if (type.equals(NotificationType.NONE)) {
            throw new NotificationException("Cannot register notification of type " + type.name());
        }
        
        logger.debug("Registering " + transferIds.size() + " transfers with type " + type);
        
        List<Notification> notifications = new ArrayList<Notification>();
        
        for (Long transferId: transferIds) {
            Notification n = new Notification(transferId,
                    "TG File Transfer Notification",
                    "Your file transfer has completed.",user, type);
            
            notifications.add(n);
            
        }
        
        if (!NotificationDAO.exists(notifications)) {
                NotificationDAO.makePersistent(notifications);
        } else {
            throw new PersistenceException(type + 
                    " notification already scheduled for one or more of the transfers");
        }
        
    }
    
    public static void register(Long transferId, NotificationType type, User user) 
    throws NotificationException, PermissionException{
        
        if (type.equals(NotificationType.NONE)) {
            throw new NotificationException("Cannot register notification of type " + type.name());
        }
        
        logger.debug("Registering transfer " + transferId + " with type " + type);
        
        Notification n = new Notification(transferId,
                "TG File Transfer Notification",
                "Your file transfer has completed.",user,type);
        
        List<Notification> notifications = NotificationDAO.getByExample(n);
        
        if (notifications == null || notifications.size() == 0) {
            NotificationDAO.makePersistent(n);
        } else {
            throw new PersistenceException(type + 
                    " notification already scheduled for transfer " + transferId);
        }
        
    }
    
    public static void unregister(Long transferId, NotificationType type, User user)
    throws NotificationException{
        
        if (type.equals(NotificationType.NONE)) {
            throw new NotificationException("Cannot unegister notifications of type " + type.name());
        }
        
        Notification n = new Notification(transferId,
                "TG File Transfer Notification",
                "Your file transfer has completed.",user,type);
        
        List<Notification> notifications = NotificationDAO.getByExample(n);
        
        if (notifications == null || notifications.size() == 0) {
            throw new PersistenceException("No notification of type " + type +
                    " scheduled for transfer " + transferId);
        } else {
            NotificationDAO.makeTransient(notifications);
        }
        
    }
    
    public static void unregister(Long transferId, User user)
    throws NotificationException{
        
        List<Notification> notifications = NotificationDAO.get(transferId, user.getWholeName());
        
        if (notifications == null || notifications.size() == 0) {
//            logger.debug("No notification for user" + user.getUsername() +
//                    " scheduled for transfer " + transferId);
        } else {
            NotificationDAO.makeTransient(notifications);
        }   
        
    }

    public static void unregisterAll(User user)
    throws NotificationException{
        
        List<Notification> notifications = NotificationDAO.get(user.getWholeName());
        
        if (notifications == null || notifications.size() == 0) {
            logger.error("No notifications to delete for user" + user.getUsername());
        } else {
            NotificationDAO.makeTransient(notifications);
        }
        
    }
    
    public static void send(Notification notification) {
        logger.debug("Sending notification for transfer " + 
                notification.getTransferId() + " with type " + notification.getType());
        
//        logger.debug("Email successfully sent to " + n.getUser().getEmail() + 
//                " upon transfer " + FileTransferTask.getStatusString(n.getTransfer().getStatus()));
        if (notification.getType().equals(NotificationType.EMAIL)) {
            EmailMessage.send(notification);
        }
        
        cleanUp(notification);
    }
    
    public static void sendAll(Transfer t) {
        List<Notification> notifs = NotificationDAO.getAllByTransferID(t.getId());
        
        logger.debug("Found " + notifs.size() + " notifications to send out for transfer " + t.getId());
        try {
            for (Notification n: notifs) {
                try {
                    send(n);
                } catch (Exception e) {
                    logger.debug("Failed to send " + n.getType() + " notification to user " + n.getUsername());
//                    n.delete();
                }
            }
        } catch (Exception e) {
            logger.debug("Failed to complete notifications: " + e.getMessage());
            e.printStackTrace();
            
        }
    }

    public static void runBatch() {
    
   
        // Query for all completed transfers and send
        // their corresponding notices
        List<Notification> notifs = NotificationDAO.getAllCompleted();
        
        if (notifs == null) return;
        
        logger.debug("Found " + notifs.size() + " notifications to send out.");
        
        for (Notification n: notifs) {
            try {
                send(n);
            } catch (Exception e) {
                logger.debug("Failed to send " + n.getType() + " notification to user " + n.getUsername());
                NotificationDAO.makeTransient(n);
            }
            notifs.remove(n);
        }
    }
    
    /**
     * This method is called after every notification to see if the transfer
     * has completed.  If so, the notification is removed from the db.
     * 
     * @param n
     */
    private static void cleanUp(Notification n) {
        Transfer t = new TransferDAO().getTransferById(n.getTransferId(), false);
        
        if (t.getStatus() == Task.DONE || t.getStatus() == Task.FAILED) {
        	NotificationDAO.makeTransient(n);
        }
    }
}
