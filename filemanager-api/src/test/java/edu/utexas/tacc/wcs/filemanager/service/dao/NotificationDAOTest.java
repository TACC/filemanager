/* 
 * Created on Jun 22, 2008
 * 
 * Developed by: Rion Dooley - dooley [at] tacc [dot] utexas [dot] edu
 * 				 TACC, Texas Advanced Computing Center
 * 
 * https://www.tacc.utexas.edu/
 */

package edu.utexas.tacc.wcs.filemanager.service.dao;

import java.util.List;

import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import edu.utexas.tacc.wcs.filemanager.common.model.Notification;
import edu.utexas.tacc.wcs.filemanager.common.model.User;
import edu.utexas.tacc.wcs.filemanager.common.model.enumeration.NotificationType;
import edu.utexas.tacc.wcs.filemanager.service.manager.NotificationManager;
import edu.utexas.tacc.wcs.filemanager.service.resources.TestCaseWithData;

/**
 * Test notification persistence
 * 
 * @author Rion Dooley < dooley [at] tacc [dot] utexas [dot] edu >
 *
 */
@Test
public class NotificationDAOTest extends TestCaseWithData {
	
	private static final Logger logger = Logger.getLogger(NotificationDAOTest.class);
	
    protected static Notification notification ;
    
    @Test public void testNotificationSetup() {
        
        transfer = createTransfer();
        
        new TransferDAO().makePersistent(transfer);
        
        Assert.assertNotNull(transfer.getId());
        
        props.put(TRANSFER_ID_KEY, transfer.getId().toString());
    }
    
    @Test public void testNotificationFind() {
       
        List<Notification> notifications = NotificationDAO.getAllByTransferID(transfer.getId());
        
        Assert.assertNotNull(notifications);
    }
    
    @Test public void testNotificationInsert() {
        
        notification = createNotification(transfer.getId(),user,NotificationType.EMAIL);
        
        NotificationDAO.makePersistent(notification);
        
        Assert.assertNotNull(notification.getId());
        
        props.put(NOTIFICATION_ID_KEY, notification.getId().toString());
        
    }
    
    @Test public void testNotificationDelete() {
        
        NotificationDAO.makeTransient(notification);
        
        Assert.assertFalse(NotificationDAO.exists(new Long(notificationId)));
        
        props.remove(NOTIFICATION_ID_KEY);
        
    }
    
    @Test public void testNotificationSendEmail() {
        
        NotificationManager.send(createNotification(transfer.getId(),user,NotificationType.EMAIL));
        
    }
    
    /* (non-Javadoc)
     * @see org.teragrid.portal.filebrowser.server.servlet.test.TestCase#setUp()
     */
    @BeforeMethod protected void setUp() throws Exception {
        super.setUp();
        
        user = UserDAO.findUserByDN(dn);
        
        if (transferId != null && !transferId.equals("")) {
            transfer = new TransferDAO().getTransferById(new Long(transferId), false);
        }
        
        if (notificationId != null && !notificationId.equals("")) {
            notification = NotificationDAO.getNotificationById(new Long(notificationId), false);
        }
        
        if (transfer != null) 
            logger.debug(transfer.toString());
        
    }

    protected Notification createNotification(Long transferId, User user, NotificationType notificationType) 
    {
        Notification notification = new Notification(transferId,
                "junit notification test",
                "This is a test of the TGFM notification api. Please disregard.",
                user,
                notificationType);
        
        return notification;
    }
    
}
