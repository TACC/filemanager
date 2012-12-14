/* 
 * Created on Jun 21, 2008
 * 
 * Developed by: Rion Dooley - dooley [at] tacc [dot] utexas [dot] edu
 * 				 TACC, Texas Advanced Computing Center
 * 
 * https://www.tacc.utexas.edu/
 */

package org.teragrid.portal.filebrowser.server.servlet.dao;

import java.util.List;

import org.apache.log4j.Logger;
import org.teragrid.portal.filebrowser.server.servlet.model.filetransfer.Transfer;
import org.teragrid.portal.filebrowser.server.servlet.model.notification.Notification;
import org.teragrid.portal.filebrowser.server.servlet.model.resource.TeraGridSystem;
import org.teragrid.portal.filebrowser.server.servlet.model.resource.TeraGridSystemResolver;
import org.teragrid.portal.filebrowser.server.servlet.model.user.User;

/**
 * Insert Template description here.
 * 
 * @author Rion Dooley < dooley [at] tacc [dot] utexas [dot] edu >
 *
 */
public class TestDAO {
	private static final Logger logger = Logger.getLogger(TestDAO.class);
    public static void main(String[] chars) {
        
        String dn = chars[0];
        
        logger.debug("Performing user tests...\n\n");
        
        User user = testUser(dn);
        
        logger.debug("Performing resource tests...\n\n");
        
        testResources(user);
        
        logger.debug("Performing transfer tests...\n\n");
        
        Transfer transfer = testTransfer(user);
        
        logger.debug("Performing notification tests...\n\n");
        
        testNotification(transfer,user);
        
    }
    
    public static User testUser(String dn) {
        
    	
        logger.debug("Trying find user...");
        
        User user = UserDAO.findUserByDN(dn);
        
        assert(user != null);
        
        logger.debug("Success: " + user.toString());
        
        logger.debug("Trying load user...");
        
        user = UserDAO.loadUserByDN(dn);
        
        assert(user != null);
        
        logger.debug("Success: " + user.toString());
        
        logger.debug(user.toString());
        
        return user;
        
    }
    
    public static void testResources(User user) {
        
        logger.debug("Finding resources...");
        
        List<TeraGridSystem> systems = TeraGridSystemDAO.findSystemAccounts(user);
        
        systems = TeraGridSystemResolver.resolveResources(systems);
        
        if (systems != null) {
            logger.debug("Found " + systems.size() + " resources for user " + 
                    user.getUsername());
            
            for (TeraGridSystem system: systems) {
                logger.debug(system.toString());
            }
           
        }
        
    }
    
    public static Transfer testTransfer(User user) {
        
        logger.debug("Retrieving transfers for dn " + user.getDn() + "...");
        
        List<Transfer> transfers = new TransferDAO().getAllByDN(user.getDn());
        
        if (transfers != null && transfers.size() > 0) {
        
            logger.debug("Found " + transfers.size() + " transfers for user " + 
                    user.getUsername());
            
            for (Transfer transfer: transfers) {
                logger.debug(transfer.toString());
            }
            
            return transfers.get(0);
            
        } else {
            logger.debug("No transfers found for user " + user.getUsername());
        }
        
        return null;
        
    }

    public static void testNotification(Transfer transfer, User user) {
        
        if (transfer != null) {
        
            logger.debug("Retrieving notifications for transfer " + 
                    transfer.getId() + "...");
            
            List<Notification> notifs = NotificationDAO.get(
                    transfer.getId(), user.getUsername());
            
            assert(notifs != null);
    
            logger.debug("Found " + notifs.size() + 
                    " notifications for user transfer" + transfer.getId());
            
            for (Notification notif: notifs) {
                logger.debug(notif.toString());
            }
        } else {
            logger.debug("No transfers available to retrieve notifications.");
        }
    }
}
