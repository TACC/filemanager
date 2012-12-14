/* 
 * Created on Jun 22, 2008
 * 
 * Developed by: Rion Dooley - dooley [at] tacc [dot] utexas [dot] edu
 * 				 TACC, Texas Advanced Computing Center
 * 
 * https://www.tacc.utexas.edu/
 */

package org.teragrid.portal.filebrowser.server.servlet.test;

import java.io.FileOutputStream;
import java.util.List;

import org.apache.log4j.Logger;
import org.teragrid.portal.filebrowser.server.servlet.dao.NotificationDAO;
import org.teragrid.portal.filebrowser.server.servlet.dao.TransferDAO;
import org.teragrid.portal.filebrowser.server.servlet.dao.UserDAO;
import org.teragrid.portal.filebrowser.server.servlet.model.notification.Notification;
import org.teragrid.portal.filebrowser.server.servlet.model.notification.NotificationManager;
import org.teragrid.portal.filebrowser.server.servlet.model.notification.NotificationType;
import org.teragrid.portal.filebrowser.server.servlet.model.user.User;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Insert Template description here.
 * 
 * @author Rion Dooley < dooley [at] tacc [dot] utexas [dot] edu >
 *
 */
public class NotificationTest extends TestCaseWithData {
	
	private static final Logger logger = Logger.getLogger(NotificationTest.class);
	
    protected static Notification notification ;
    
    public void testNotificationSetup() {
        
        transfer = createTransfer();
        
        new TransferDAO().makePersistent(transfer);
        
        assertNotNull(transfer.getId());
        
        props.put(TRANSFER_ID_KEY, transfer.getId().toString());
    }
    
    public void testNotificationFind() {
       
        List<Notification> notifications = NotificationDAO.getAllByTransferID(transfer.getId());
        
        assertNotNull(notifications);
    }
    
    public void testNotificationInsert() {
        
        notification = createNotification(transfer.getId(),user,NotificationType.EMAIL);
        
        NotificationDAO.makePersistent(notification);
        
        assertNotNull(notification.getId());
        
        props.put(NOTIFICATION_ID_KEY, notification.getId().toString());
        
    }
    
    public void testNotificationDelete() {
        
        NotificationDAO.makeTransient(notification);
        
        assertFalse(NotificationDAO.exists(new Integer(notificationId)));
        
        props.remove(NOTIFICATION_ID_KEY);
        
    }
    
    public void testNotificationSendEmail() {
        
        NotificationManager.send(createNotification(transfer.getId(),user,NotificationType.EMAIL));
        
    }
    
    public void testNotificationSendText() {
        
        NotificationManager.send(createNotification(transfer.getId(),user,NotificationType.TEXT));
        
    }
    
    
    /* (non-Javadoc)
     * @see org.teragrid.portal.filebrowser.server.servlet.test.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        
        user = UserDAO.findUserByDN(dn);
        
        if (transferId != null && !transferId.equals("")) {
            transfer = new TransferDAO().getTransferById(new Integer(transferId), false);
        }
        
        if (notificationId != null && !notificationId.equals("")) {
            notification = NotificationDAO.getNotificationById(new Integer(notificationId), false);
        }
        
        if (transfer != null) 
            logger.debug(transfer.toString());
        
    }

    /* (non-Javadoc)
     * @see org.teragrid.portal.filebrowser.server.servlet.test.TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        props.store(new FileOutputStream(TEST_PROPERTIES_LOCATION),"");
        super.tearDown();
    }
    
    
    public NotificationTest(String x) {
        super(x);
    }

    public static Test suite() {
        return new TestSuite(NotificationTest.class);
    }  
    
    protected Notification createNotification(Integer transferId, User user, NotificationType notificationType) {
        
        Notification notification = new Notification(transferId,
                "junit notification test",
                "This is a test of the TGFM notification api. Please disregard.",
                user,
                notificationType);
        
        return notification;
        
        
    }
    
}
