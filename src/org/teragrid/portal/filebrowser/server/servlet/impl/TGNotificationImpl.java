/* 
 * Created on Dec 16, 2007
 * 
 * Developed by: Rion Dooley - dooley [at] tacc [dot] utexas [dot] edu
 * 				 TACC, Texas Advanced Computing Center
 * 
 * https://www.tacc.utexas.edu/
 */

package org.teragrid.portal.filebrowser.server.servlet.impl;

import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.teragrid.portal.filebrowser.server.servlet.TGNotification;
import org.teragrid.portal.filebrowser.server.servlet.exception.AuthenticationException;
import org.teragrid.portal.filebrowser.server.servlet.exception.NotificationException;
import org.teragrid.portal.filebrowser.server.servlet.model.filetransfer.History;
import org.teragrid.portal.filebrowser.server.servlet.model.notification.NotificationType;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.basic.DateConverter;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * TG Notification class to send and manage notifications for file transfer
 * completions in the TG.
 * 
 * @author Rion Dooley < dooley [at] tacc [dot] utexas [dot] edu >
 *
 */
@SuppressWarnings("unchecked")
public class TGNotificationImpl implements TGNotification {
	
	private static final Logger logger = Logger.getLogger(TGNotificationImpl.class);
    
	private static XStream xstream = new XStream(new DomDriver());
    
    private static String defaultFormat = "MMM d, yyyy h:mm:ss a";
    
    private static String[] acceptableFormats = new String[] {
            "MM/dd/yyyy HH:mm:ss",
            "yyyy-MM-dd HH:mm:ss.S z",
            "yyyy-MM-dd HH:mm:ss.S a",
            "yyyy-MM-dd HH:mm:ssz",
            "yyyy-MM-dd HH:mm:ss z", // JDK 1.3 needs both prev versions
            "yyyy-MM-dd HH:mm:ssa" }; // backwards compatability
    
    {
        xstream.registerConverter(new DateConverter(defaultFormat, acceptableFormats));
    }
    public TGNotificationImpl(){}
    
    /* (non-Javadoc)
     * @see org.teragrid.portal.filebrowser.server.servlet.TGNotification#setNotification(java.lang.String, long, java.lang.String, boolean)
     */
    public int add(String dn, String transferId, String notificationType) {
        if (!isValid(dn))
            throw new AuthenticationException("Please supply a valid DN " + dn);
        
        if (!isValid(transferId)) {
            throw new NotificationException("Please supply a valid transfer ID");
        }
        
        if (!isValid(notificationType)) {
            throw new NotificationException("Please supply a valid notification type");
        }
        
        Integer tId = null;
        
        try {
            tId = new Integer(transferId);
        } catch (NumberFormatException e) {
            throw new NotificationException("Please supply a valid transfer ID");
        }
        
        History history = new History(dn);
    
        logger.debug("Setting a " + notificationType + " notification for transfer " + transferId + "...");
        
        history.notify(tId, NotificationType.getType(notificationType));
        
        
        return 0;
    }
    
    public int remove(String dn, String transferId, String notificationType) {
        if (!isValid(dn))
            throw new AuthenticationException("Please supply a valid DN " + dn);
        
        if (!isValid(transferId)) {
            throw new NotificationException("Please supply a valid transfer ID");
        }
        
        if (!isValid(notificationType)) {
            throw new NotificationException("Please supply a valid notification type");
        }
        
        Integer tId = null;
        
        try {
            tId = new Integer(transferId);
        } catch (NumberFormatException e) {
            throw new NotificationException("Please supply a valid transfer ID");
        }
        
        History history = new History(dn);
         
        logger.debug("Removing a " + notificationType + " notification for transfer " + transferId + "...");
        
        history.denotify(tId, NotificationType.getType(notificationType));
        
        return 0;
    }
    
    
    /* (non-Javadoc)
     * @see org.teragrid.portal.filebrowser.server.servlet.TGNotification#setNotification(java.lang.String, long[], java.lang.String, boolean)
     */
    public int addAll(String dn, String serializedTransferIds, String notificationType) {
        if (!isValid(dn))
            throw new AuthenticationException("Please supply a valid DN " + dn);
        
        if (!isValid(serializedTransferIds)) {
            throw new NotificationException("Please supply a valid transfer ID");
        }
        
        if (!isValid(notificationType)) {
            throw new NotificationException("Please supply a valid notification type");
        }
        
        ArrayList<Integer> transferIds = (ArrayList<Integer>)xstream.fromXML(serializedTransferIds);
        
        if (transferIds == null) {
            throw new NotificationException("Please supply a valid transfer ID");
        }
        
        History history = new History(dn);
        
        for (Integer transferId: transferIds){
            
            if (transferId == null) {
                throw new NotificationException("Please supply a valid transfer ID");
            }
            
            logger.debug("Setting a " + notificationType + " notification for transfer " + transferId + "...");
            history.notify(transferId, NotificationType.getType(notificationType));
            
        }
        
        return 0;
    }
    
    public int removeAll(String dn, String serializedTransferIds, String notificationType) {
        if (!isValid(dn))
            throw new AuthenticationException("Please supply a valid DN " + dn);
        
        if (!isValid(serializedTransferIds)) {
            throw new NotificationException("Please supply a valid transfer ID");
        }
        
        if (!isValid(notificationType)) {
            throw new NotificationException("Please supply a valid notification type");
        }
        
        ArrayList<Integer> transferIds = (ArrayList<Integer>)xstream.fromXML(serializedTransferIds);
        
        if (transferIds == null) {
            throw new NotificationException("Please supply a valid transfer ID");
        }
        
        History history = new History(dn);
        
        for (Integer transferId: transferIds){
            
            if (transferId == null) {
                throw new NotificationException("Please supply a valid transfer ID");
            }
            
            logger.debug("Removing a " + notificationType + " notification for transfer " + transferId + "...");
            history.denotify(transferId, NotificationType.getType(notificationType));
        }
        
        return 0;
    }
    
    /* (non-Javadoc)
     * @see org.teragrid.portal.filebrowser.server.servlet.TGNotification#clear(java.lang.String, long[])
     */
    public int clear(String dn, String serializedTransferIds) {
        if (!isValid(dn))
            throw new AuthenticationException("Please supply a valid DN " + dn);
        
        if (!isValid(serializedTransferIds)) {
            throw new NotificationException("Please supply a valid transfer ID");
        }
        
        ArrayList<Integer> transferIds = (ArrayList<Integer>)xstream.fromXML(serializedTransferIds);
        
        if (transferIds == null) {
            throw new NotificationException("Please supply a valid transfer ID");
        }
        
        logger.error("Received " + transferIds.size() + " ids for denotification:");
        
        History history = new History(dn);
        
        for (Integer transferId: transferIds){
            
            if (transferId == null) {
                throw new NotificationException("Please supply a valid transfer ID");
            }
            
            logger.debug("Removing all notifications for transfers " + transferId + "...");
            
            history.denotify(transferId);
            
        }
        
        return 0;
    }
    
    private boolean isValid(String s) {
        return !(s==null || s.equals(""));
    }
}
