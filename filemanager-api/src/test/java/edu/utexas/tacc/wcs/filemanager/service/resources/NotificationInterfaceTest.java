/* 
 * Created on Jun 22, 2008
 * 
 * Developed by: Rion Dooley - dooley [at] tacc [dot] utexas [dot] edu
 * 				 TACC, Texas Advanced Computing Center
 * 
 * https://www.tacc.utexas.edu/
 */

package edu.utexas.tacc.wcs.filemanager.service.resources;

import org.hibernate.Session;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import edu.utexas.tacc.wcs.filemanager.service.dao.TransferDAO;
import edu.utexas.tacc.wcs.filemanager.service.persistence.HibernateUtil;

/**
 * Insert Template description here.
 * 
 * @author Rion Dooley < dooley [at] tacc [dot] utexas [dot] edu >
 *
 */
public class NotificationInterfaceTest extends TestCaseWithData {
    
	@BeforeMethod
    protected void setUp() throws Exception {
    	transfer = createTransfer();
        new TransferDAO().makePersistent(transfer);
        Assert.assertNotNull(transfer.getId(), "Failed to save transfer.");
    }
    
    /**
	 * Clears all transfers from the db prior before each test
	 */
	@AfterMethod
	public void afterMethod()
	{
		Session session = HibernateUtil.getSession();
		session.clear();
		session.createSQLQuery("delete Transfer").executeUpdate();
		session.createSQLQuery("delete Notification").executeUpdate();
		session.flush();
		session.close();
	}
	
    public static Object result = null;
    

    /************************************************************************
     *                   Add Single Notification
     ************************************************************************/
    
//    public void testSetupNotificationTestData() {
//        
//        TransfersResourceImpl ftInterface = new TransfersResourceImpl();
//        
//        try {
//            result = ftInterface.add(dn,xstream.toXML(transfer),"",NotificationType.NONE.name());
//        } catch (Exception e) {
//            result = e;
//        }
//            
//        Assert.assertTrue(result instanceof Integer);
//        
//        Assert.assertTrue(((Integer)result).intValue() != -1);
//        
//        props.put(TRANSFER_ID_KEY,result.toString());
//        
//    }
//    
//    public void testAddNotification() {
//        NotificationsResourceImpl ftInterface = new NotificationsResourceImpl();
//        
//        try {
//            result = ftInterface.add(dn,transferId,NotificationType.EMAIL.name());
//        } catch (Exception e) {
//            result = e;
//        }
//            
//        Assert.assertTrue(result instanceof Integer);
//        
//        Assert.assertTrue(((Integer)result).intValue() == 0);
//        
//    }
//    
//    public void testAddNotificationDuplicate() {
//        NotificationsResourceImpl ftInterface = new NotificationsResourceImpl();
//        
//        try {
//            result = ftInterface.add(dn,transferId,NotificationType.EMAIL.name());
//        } catch (Exception e) {
//            result = e;
//        }
//            
//        Assert.assertTrue(result instanceof PersistenceException);
//        
//    }
//    
//    public void testAddNotificationNullTransferId() {
//        NotificationsResourceImpl ftInterface = new NotificationsResourceImpl();
//        
//        try {
//            result = ftInterface.add(dn,null,NotificationType.EMAIL.name());
//        } catch (Exception e) {
//            result = e;
//        }
//            
//        Assert.assertTrue(result instanceof NotificationException);
//        
//    }
//    
//    public void testAddNotificationInvalidTransferId() {
//        NotificationsResourceImpl ftInterface = new NotificationsResourceImpl();
//        
//        try {
//            result = ftInterface.add(dn,"-1",NotificationType.EMAIL.name());
//        } catch (Exception e) {
//            result = e;
//        }
//            
//        Assert.assertTrue(result instanceof PersistenceException);
//        
//    }
//    
//    public void testAddNotificationUnauthorizedDN() {
//        NotificationsResourceImpl ftInterface = new NotificationsResourceImpl();
//        
//        try {
//            result = ftInterface.add(baddn,transferId,NotificationType.EMAIL.name());
//        } catch (Exception e) {
//            result = e;
//        }
//            
//        Assert.assertTrue(result instanceof PermissionException);
//        
//    }
//
//    
//    
//    public void testAddNotificationTypeNone() {
//        NotificationsResourceImpl ftInterface = new NotificationsResourceImpl();
//        
//        try {
//            result = ftInterface.add(dn,transferId,NotificationType.NONE.name());
//        } catch (Exception e) {
//            result = e;
//        }
//            
//        Assert.assertTrue(result instanceof NotificationException);
//        
//    }
//    
//    /************************************************************************
//     *                   Add Multiple Notifications
//     ************************************************************************/
//    
//    public void testAddMultipleNotifications() {
//        NotificationsResourceImpl ftInterface = new NotificationsResourceImpl();
//        
//        try {
//            ArrayList<Integer> transferIds = new ArrayList<Integer>();
//            transferIds.add(transfer.getId());
//            result = ftInterface.addAll(dn,xstream.toXML(transferIds),NotificationType.TEXT.name());
//        } catch (Exception e) {
//            result = e;
//        }
//            
//        Assert.assertTrue(result instanceof Integer);
//        
//        Assert.assertTrue(((Integer)result).intValue() == 0);
//        
//    }
//    
//    public void testAddMultipleNotificationsDuplicateType() {
//        NotificationsResourceImpl ftInterface = new NotificationsResourceImpl();
//        
//        try {
//            ArrayList<Integer> transferIds = new ArrayList<Integer>();
//            transferIds.add(transfer.getId());
//            result = ftInterface.addAll(dn,xstream.toXML(transferIds),NotificationType.TEXT.name());
//        } catch (Exception e) {
//            result = e;
//        }
//            
//        Assert.assertTrue(result instanceof PersistenceException);
//        
//    }
//    
//    public void testAddMultipleNotificationsNullTransferId() {
//        NotificationsResourceImpl ftInterface = new NotificationsResourceImpl();
//        
//        try {
//            ArrayList<Integer> transferIds = new ArrayList<Integer>();
//            transferIds.add(transfer.getId());
//            transferIds.add(null);
//            result = ftInterface.addAll(dn,xstream.toXML(transferIds),NotificationType.TEXT.name());
//        } catch (Exception e) {
//            result = e;
//        }
//            
//        Assert.assertTrue(result instanceof PersistenceException);
//        
//    }
//    
//    public void testAddMultipleNotificationsInvalidTransferId() {
//        NotificationsResourceImpl ftInterface = new NotificationsResourceImpl();
//        
//        try {
//            ArrayList<Integer> transferIds = new ArrayList<Integer>();
//            transferIds.add(transfer.getId());
//            transferIds.add(transfer.getId());
//            result = ftInterface.addAll(dn,xstream.toXML(transferIds),NotificationType.TEXT.name());
//        } catch (Exception e) {
//            result = e;
//        }
//            
//        Assert.assertTrue(result instanceof PersistenceException);
//        
//    }
//    
//    public void testAddMultipleNotificationNullTransferId() {
//        NotificationsResourceImpl ftInterface = new NotificationsResourceImpl();
//        
//        try {
//            ArrayList<Integer> transferIds = new ArrayList<Integer>();
//            transferIds.add(transfer.getId());
//            transferIds.add(null);
//            result = ftInterface.addAll(dn,xstream.toXML(transferIds),NotificationType.TEXT.name());
//        } catch (Exception e) {
//            result = e;
//        }
//            
//        Assert.assertTrue(result instanceof PersistenceException);
//        
//    }
//    
//    public void testAddMultipleNotificationUnauthorizedDN() {
//        NotificationsResourceImpl ftInterface = new NotificationsResourceImpl();
//        
//        try {
//            ArrayList<Integer> transferIds = new ArrayList<Integer>();
//            transferIds.add(transfer.getId());
//            result = ftInterface.addAll(baddn,xstream.toXML(transferIds),NotificationType.TEXT.name());
//        } catch (Exception e) {
//            result = e;
//        }
//            
//        Assert.assertTrue(result instanceof PermissionException);
//        
//    }
//
//    public void testAddMultipleNotificationTypeNone() {
//        NotificationsResourceImpl ftInterface = new NotificationsResourceImpl();
//        
//        try {
//            ArrayList<Integer> transferIds = new ArrayList<Integer>();
//            transferIds.add(transfer.getId());
//            result = ftInterface.addAll(dn,xstream.toXML(transferIds),NotificationType.NONE.name());
//        } catch (Exception e) {
//            result = e;
//        }
//            
//        Assert.assertTrue(result instanceof NotificationException);
//        
//    }
//    
//
//    /************************************************************************
//     *                   Remove Notifications
//     ************************************************************************/
//    
//    public void testRemoveNotification() {
//        NotificationsResourceImpl ftInterface = new NotificationsResourceImpl();
//        
//        try {
//            result = ftInterface.remove(dn,transferId,NotificationType.EMAIL.name());
//        } catch (Exception e) {
//            result = e;
//        }
//            
//        Assert.assertTrue(result instanceof Integer);
//        
//        Assert.assertTrue(((Integer)result).intValue() == 0);
//        
//    }
//    
//    public void testRemoveNotificationNullTransferId() {
//        NotificationsResourceImpl ftInterface = new NotificationsResourceImpl();
//        
//        try {
//            result = ftInterface.remove(dn,null,NotificationType.EMAIL.name());
//        } catch (Exception e) {
//            result = e;
//        }
//            
//        Assert.assertTrue(result instanceof NotificationException);
//        
//    }
//    
//    public void testRemoveNotificationInvalidTransferId() {
//        NotificationsResourceImpl ftInterface = new NotificationsResourceImpl();
//        
//        try {
//            result = ftInterface.remove(dn,"-1",NotificationType.EMAIL.name());
//        } catch (Exception e) {
//            result = e;
//        }
//            
//        Assert.assertTrue(result instanceof PersistenceException);
//        
//    }
//    
//    public void testRemoveNotificationUnauthorizedDN() {
//        NotificationsResourceImpl ftInterface = new NotificationsResourceImpl();
//        
//        try {
//            result = ftInterface.remove(baddn,transferId,NotificationType.EMAIL.name());
//        } catch (Exception e) {
//            result = e;
//        }
//            
//        Assert.assertTrue(result instanceof PermissionException);
//        
//    }
//    
//
//    /************************************************************************
//     *                   Remove Multiple Notifications
//     ************************************************************************/
//    
//    public void testRemoveMultipleNotifications() {
//        NotificationsResourceImpl ftInterface = new NotificationsResourceImpl();
//        
//        try {
//            ArrayList<Integer> transferIds = new ArrayList<Integer>();
//            transferIds.add(transfer.getId());
//            result = ftInterface.removeAll(dn,xstream.toXML(transferIds),NotificationType.TEXT.name());
//        } catch (Exception e) {
//            result = e;
//        }
//            
//        Assert.assertTrue(result instanceof Integer);
//        
//        Assert.assertTrue(((Integer)result).intValue() == 0);
//        
//    }
//    
//    public void testRemoveMultipleNotificationsNullTransferId() {
//        NotificationsResourceImpl ftInterface = new NotificationsResourceImpl();
//        
//        try {
//            ArrayList<Integer> transferIds = new ArrayList<Integer>();
//            transferIds.add(null);
//            result = ftInterface.removeAll(dn,xstream.toXML(transferIds),NotificationType.TEXT.name());
//        } catch (Exception e) {
//            result = e;
//        }
//            
//        Assert.assertTrue(result instanceof NotificationException);
//        
//    }
//    
//    public void testRemoveMultipleNotificationsInvalidTransferId() {
//        NotificationsResourceImpl ftInterface = new NotificationsResourceImpl();
//        
//        try {
//            ArrayList<Integer> transferIds = new ArrayList<Integer>();
//            transferIds.add(transfer.getId());
//            result = ftInterface.removeAll(dn,xstream.toXML(transferIds),NotificationType.TEXT.name());
//        } catch (Exception e) {
//            result = e;
//        }
//            
//        Assert.assertTrue(result instanceof PersistenceException);
//        
//    }
//    
//    public void testRemoveMultipleNotificationUnauthorizedDN() {
//        NotificationsResourceImpl ftInterface = new NotificationsResourceImpl();
//        
//        try {
//            ArrayList<Integer> transferIds = new ArrayList<Integer>();
//            transferIds.add(transfer.getId());
//            result = ftInterface.removeAll(baddn,xstream.toXML(transferIds),NotificationType.TEXT.name());
//        } catch (Exception e) {
//            result = e;
//        }
//            
//        Assert.assertTrue(result instanceof PermissionException);
//        
//    }
//
//    /************************************************************************
//     *                   Clear Notifications
//     ************************************************************************/
//    
//    public void testSetupToClearNotificationHistory() {
//        NotificationsResourceImpl ftInterface = new NotificationsResourceImpl();
//        
//        ftInterface.addMultipleNotifications(dn,transferId,NotificationType.EMAIL.name());
//        
//    }
//
//    public void testClearNotifications() {
//        NotificationsResourceImpl ftInterface = new NotificationsResourceImpl();
//        
//        try {
//            result = ftInterface.removeMultipleNotifications(dn,transferIds.toString(), null);
//        } catch (Exception e) {
//            result = e;
//        }
//            
//        Assert.assertTrue(result instanceof Integer);
//        
//        Assert.assertTrue(((Integer)result).intValue() == 0);
//        
//    }
//    
//    public void testClearNotificationsNullTransferId() {
//        NotificationsResourceImpl ftInterface = new NotificationsResourceImpl();
//        
//        try {
//            ArrayList<Integer> transferIds = new ArrayList<Integer>();
//            transferIds.add(null);
//            result = ftInterface.removeMultipleNotifications(dn,null, null);
//        } catch (Exception e) {
//            result = e;
//        }
//            
//        Assert.assertTrue(result instanceof NotificationException);
//        
//    }
//    
//    public void testClearNotificationsInvalidTransferId() {
//        NotificationsResourceImpl ftInterface = new NotificationsResourceImpl();
//        
//        try {
//            result = ftInterface.removeMultipleNotifications(dn,"-1", null);
//        } catch (Exception e) {
//            result = e;
//        }
//            
//        Assert.assertTrue(result instanceof PersistenceException);
//        
//    }
//    
//    public void testClearNotificationUnauthorizedDN() {
//        NotificationsResourceImpl ftInterface = new NotificationsResourceImpl();
//        
//        try {
//            result = ftInterface.removeMultipleNotifications(baddn, transfer.getId().toString(), null);
//        } catch (Exception e) {
//            result = e;
//        }
//            
//        Assert.assertTrue(result instanceof PermissionException);
//        
//    }
//    
//    public void testCleanUpNotifcationTestData() {
//        TransfersResourceImpl ftInterface = new TransfersResourceImpl();
//        
//        try {
//            result = ftInterface.removeTransfer(dn,transferId);
//        } catch (Exception e) {
//            result = e;
//        }
//            
//        Assert.assertTrue(result instanceof Integer);
//        
//        Assert.assertTrue(((Integer)result).intValue() == 0);
//        
//        props.remove(TRANSFER_ID_KEY);    
//    }
}