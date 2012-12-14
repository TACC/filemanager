/* 
 * Created on Dec 12, 2007
 * 
 * Developed by: Rion Dooley - dooley [at] tacc [dot] utexas [dot] edu
 * 				 TACC, Texas Advanced Computing Center
 * 
 * https://www.tacc.utexas.edu/
 */

package org.teragrid.portal.filebrowser.server.servlet.model.notification;

import java.util.*;

import org.apache.log4j.Logger;
import org.teragrid.portal.filebrowser.applet.transfer.Task;
import org.teragrid.portal.filebrowser.server.servlet.dao.NotificationDAO;
import org.teragrid.portal.filebrowser.server.servlet.exception.NotificationException;
import org.teragrid.portal.filebrowser.server.servlet.exception.PermissionException;
import org.teragrid.portal.filebrowser.server.servlet.exception.PersistenceException;
import org.teragrid.portal.filebrowser.server.servlet.model.filetransfer.Transfer;
import org.teragrid.portal.filebrowser.server.servlet.model.user.User;


/**
 * Manager to monitor, register, and unregister notifications for file
 * transfers.
 * 
 * @author Rion Dooley < dooley [at] tacc [dot] utexas [dot] edu >
 *
 */
public class NotificationManager {

	private static final Logger logger = Logger.getLogger(NotificationManager.class);
	
    public static void register(List<Integer> transferIds, NotificationType type, User user) 
    throws NotificationException, PermissionException{
        
        if (type.equals(NotificationType.NONE)) {
            throw new NotificationException("Cannot register notification of type " + type.name());
        }
        
        logger.debug("Registering " + transferIds.size() + " transfers with type " + type);
        
        List<Notification> notifications = new ArrayList<Notification>();
        
        for (Integer transferId: transferIds) {
            Notification n = new Notification(transferId,
                    "TG File Transfer Notification",
                    "Your file transfer has completed.",user,type);
            
            notifications.add(n);
            
        }
        
        if (!NotificationDAO.exists(notifications)) {
                NotificationDAO.makePersistent(notifications);
        } else {
            throw new PersistenceException(type + 
                    " notification already scheduled for one or more of the transfers");
        }
        
    }
    
    public static void register(Integer transferId, NotificationType type, User user) 
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
            n.persist();
        } else {
            throw new PersistenceException(type + 
                    " notification already scheduled for transfer " + transferId);
        }
        
    }
    
    public static void unregister(Integer transferId, NotificationType type, User user)
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
    
    public static void unregister(Integer transferId, User user)
    throws NotificationException{
        
        List<Notification> notifications = NotificationDAO.get(transferId,user.getWholeName());
        
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
    
    public static void send(Notification n) {
        logger.debug("Sending notification for transfer " + 
                n.getTransfer().getId() + " with type " + n.getType());
        
//        logger.debug("Email successfully sent to " + n.getUser().getEmail() + 
//                " upon transfer " + FileTransferTask.getStatusString(n.getTransfer().getStatus()));
        if (n.getType().equals(NotificationType.EMAIL)) {
            EmailMessage.send(n);
        } else if (n.getType().equals(NotificationType.TEXT) || 
                n.getType().equals(NotificationType.SMS)) {
            TextMessage.send(n);
        } else if (n.getType().equals(NotificationType.IM)) {
            IMMessage.send(n);
        }
        
        cleanUp(n);
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
                n.delete();
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
        Transfer t = n.getTransfer();
        
        if (t.getStatus() == Task.DONE || t.getStatus() == Task.FAILED) {
            n.delete();
        }
    }
}
