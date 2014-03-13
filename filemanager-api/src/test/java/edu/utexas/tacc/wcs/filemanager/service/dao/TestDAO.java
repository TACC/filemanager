/* 
 * Created on Jun 21, 2008
 * 
 * Developed by: Rion Dooley - dooley [at] tacc [dot] utexas [dot] edu
 * 				 TACC, Texas Advanced Computing Center
 * 
 * https://www.tacc.utexas.edu/
 */

package edu.utexas.tacc.wcs.filemanager.service.dao;

import java.util.List;

import org.apache.log4j.Logger;
import org.teragrid.service.profile.util.TeraGridSystemResolver;

import edu.utexas.tacc.wcs.filemanager.common.model.Notification;
import edu.utexas.tacc.wcs.filemanager.common.model.Transfer;
import edu.utexas.tacc.wcs.filemanager.common.model.User;
import edu.utexas.tacc.wcs.filemanager.common.model.System;
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
        
        List<System> systems = SystemDAO.findSystemAccounts(user);
        
        systems = TeraGridSystemResolver.resolveResources(systems);
        
        if (systems != null) {
            logger.debug("Found " + systems.size() + " resources for user " + 
                    user.getUsername());
            
            for (System system: systems) {
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
