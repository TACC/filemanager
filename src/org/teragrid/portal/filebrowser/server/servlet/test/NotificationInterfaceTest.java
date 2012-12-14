/* 
 * Created on Jun 22, 2008
 * 
 * Developed by: Rion Dooley - dooley [at] tacc [dot] utexas [dot] edu
 * 				 TACC, Texas Advanced Computing Center
 * 
 * https://www.tacc.utexas.edu/
 */

package org.teragrid.portal.filebrowser.server.servlet.test;

import java.util.ArrayList;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.teragrid.portal.filebrowser.server.servlet.exception.NotificationException;
import org.teragrid.portal.filebrowser.server.servlet.exception.PermissionException;
import org.teragrid.portal.filebrowser.server.servlet.exception.PersistenceException;
import org.teragrid.portal.filebrowser.server.servlet.impl.TGFileTransferHistoryImpl;
import org.teragrid.portal.filebrowser.server.servlet.impl.TGNotificationImpl;
import org.teragrid.portal.filebrowser.server.servlet.model.notification.NotificationType;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.basic.DateConverter;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * Insert Template description here.
 * 
 * @author Rion Dooley < dooley [at] tacc [dot] utexas [dot] edu >
 *
 */
public class NotificationInterfaceTest extends TestCaseWithData {
    
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
    
    public static Object result = null;
    

    /************************************************************************
     *                   Add Single Notification
     ************************************************************************/
    
    public void testSetupNotificationTestData() {
        
        TGFileTransferHistoryImpl ftInterface = new TGFileTransferHistoryImpl();
        
        try {
            result = ftInterface.add(dn,xstream.toXML(transfer),"",NotificationType.NONE.name());
        } catch (Exception e) {
            result = e;
        }
            
        assertTrue(result instanceof Integer);
        
        assertTrue(((Integer)result).intValue() != -1);
        
        props.put(TRANSFER_ID_KEY,result.toString());
        
    }
    
    public void testAddNotification() {
        TGNotificationImpl ftInterface = new TGNotificationImpl();
        
        try {
            result = ftInterface.add(dn,transferId,NotificationType.EMAIL.name());
        } catch (Exception e) {
            result = e;
        }
            
        assertTrue(result instanceof Integer);
        
        assertTrue(((Integer)result).intValue() == 0);
        
    }
    
    public void testAddNotificationDuplicate() {
        TGNotificationImpl ftInterface = new TGNotificationImpl();
        
        try {
            result = ftInterface.add(dn,transferId,NotificationType.EMAIL.name());
        } catch (Exception e) {
            result = e;
        }
            
        assertTrue(result instanceof PersistenceException);
        
    }
    
    public void testAddNotificationNullTransferId() {
        TGNotificationImpl ftInterface = new TGNotificationImpl();
        
        try {
            result = ftInterface.add(dn,null,NotificationType.EMAIL.name());
        } catch (Exception e) {
            result = e;
        }
            
        assertTrue(result instanceof NotificationException);
        
    }
    
    public void testAddNotificationInvalidTransferId() {
        TGNotificationImpl ftInterface = new TGNotificationImpl();
        
        try {
            result = ftInterface.add(dn,"-1",NotificationType.EMAIL.name());
        } catch (Exception e) {
            result = e;
        }
            
        assertTrue(result instanceof PersistenceException);
        
    }
    
    public void testAddNotificationUnauthorizedDN() {
        TGNotificationImpl ftInterface = new TGNotificationImpl();
        
        try {
            result = ftInterface.add(baddn,transferId,NotificationType.EMAIL.name());
        } catch (Exception e) {
            result = e;
        }
            
        assertTrue(result instanceof PermissionException);
        
    }

    
    
    public void testAddNotificationTypeNone() {
        TGNotificationImpl ftInterface = new TGNotificationImpl();
        
        try {
            result = ftInterface.add(dn,transferId,NotificationType.NONE.name());
        } catch (Exception e) {
            result = e;
        }
            
        assertTrue(result instanceof NotificationException);
        
    }
    
    /************************************************************************
     *                   Add Multiple Notifications
     ************************************************************************/
    
    public void testAddMultipleNotifications() {
        TGNotificationImpl ftInterface = new TGNotificationImpl();
        
        try {
            ArrayList<Integer> transferIds = new ArrayList<Integer>();
            transferIds.add(transfer.getId());
            result = ftInterface.addAll(dn,xstream.toXML(transferIds),NotificationType.TEXT.name());
        } catch (Exception e) {
            result = e;
        }
            
        assertTrue(result instanceof Integer);
        
        assertTrue(((Integer)result).intValue() == 0);
        
    }
    
    public void testAddMultipleNotificationsDuplicateType() {
        TGNotificationImpl ftInterface = new TGNotificationImpl();
        
        try {
            ArrayList<Integer> transferIds = new ArrayList<Integer>();
            transferIds.add(transfer.getId());
            result = ftInterface.addAll(dn,xstream.toXML(transferIds),NotificationType.TEXT.name());
        } catch (Exception e) {
            result = e;
        }
            
        assertTrue(result instanceof PersistenceException);
        
    }
    
    public void testAddMultipleNotificationsNullTransferId() {
        TGNotificationImpl ftInterface = new TGNotificationImpl();
        
        try {
            ArrayList<Integer> transferIds = new ArrayList<Integer>();
            transferIds.add(transfer.getId());
            transferIds.add(null);
            result = ftInterface.addAll(dn,xstream.toXML(transferIds),NotificationType.TEXT.name());
        } catch (Exception e) {
            result = e;
        }
            
        assertTrue(result instanceof PersistenceException);
        
    }
    
    public void testAddMultipleNotificationsInvalidTransferId() {
        TGNotificationImpl ftInterface = new TGNotificationImpl();
        
        try {
            ArrayList<Integer> transferIds = new ArrayList<Integer>();
            transferIds.add(transfer.getId());
            transferIds.add(transfer.getId());
            result = ftInterface.addAll(dn,xstream.toXML(transferIds),NotificationType.TEXT.name());
        } catch (Exception e) {
            result = e;
        }
            
        assertTrue(result instanceof PersistenceException);
        
    }
    
    public void testAddMultipleNotificationNullTransferId() {
        TGNotificationImpl ftInterface = new TGNotificationImpl();
        
        try {
            ArrayList<Integer> transferIds = new ArrayList<Integer>();
            transferIds.add(transfer.getId());
            transferIds.add(null);
            result = ftInterface.addAll(dn,xstream.toXML(transferIds),NotificationType.TEXT.name());
        } catch (Exception e) {
            result = e;
        }
            
        assertTrue(result instanceof PersistenceException);
        
    }
    
    public void testAddMultipleNotificationUnauthorizedDN() {
        TGNotificationImpl ftInterface = new TGNotificationImpl();
        
        try {
            ArrayList<Integer> transferIds = new ArrayList<Integer>();
            transferIds.add(transfer.getId());
            result = ftInterface.addAll(baddn,xstream.toXML(transferIds),NotificationType.TEXT.name());
        } catch (Exception e) {
            result = e;
        }
            
        assertTrue(result instanceof PermissionException);
        
    }

    public void testAddMultipleNotificationTypeNone() {
        TGNotificationImpl ftInterface = new TGNotificationImpl();
        
        try {
            ArrayList<Integer> transferIds = new ArrayList<Integer>();
            transferIds.add(transfer.getId());
            result = ftInterface.addAll(dn,xstream.toXML(transferIds),NotificationType.NONE.name());
        } catch (Exception e) {
            result = e;
        }
            
        assertTrue(result instanceof NotificationException);
        
    }
    

    /************************************************************************
     *                   Remove Notifications
     ************************************************************************/
    
    public void testRemoveNotification() {
        TGNotificationImpl ftInterface = new TGNotificationImpl();
        
        try {
            result = ftInterface.remove(dn,transferId,NotificationType.EMAIL.name());
        } catch (Exception e) {
            result = e;
        }
            
        assertTrue(result instanceof Integer);
        
        assertTrue(((Integer)result).intValue() == 0);
        
    }
    
    public void testRemoveNotificationNullTransferId() {
        TGNotificationImpl ftInterface = new TGNotificationImpl();
        
        try {
            result = ftInterface.remove(dn,null,NotificationType.EMAIL.name());
        } catch (Exception e) {
            result = e;
        }
            
        assertTrue(result instanceof NotificationException);
        
    }
    
    public void testRemoveNotificationInvalidTransferId() {
        TGNotificationImpl ftInterface = new TGNotificationImpl();
        
        try {
            result = ftInterface.remove(dn,"-1",NotificationType.EMAIL.name());
        } catch (Exception e) {
            result = e;
        }
            
        assertTrue(result instanceof PersistenceException);
        
    }
    
    public void testRemoveNotificationUnauthorizedDN() {
        TGNotificationImpl ftInterface = new TGNotificationImpl();
        
        try {
            result = ftInterface.remove(baddn,transferId,NotificationType.EMAIL.name());
        } catch (Exception e) {
            result = e;
        }
            
        assertTrue(result instanceof PermissionException);
        
    }
    

    /************************************************************************
     *                   Remove Multiple Notifications
     ************************************************************************/
    
    public void testRemoveMultipleNotifications() {
        TGNotificationImpl ftInterface = new TGNotificationImpl();
        
        try {
            ArrayList<Integer> transferIds = new ArrayList<Integer>();
            transferIds.add(transfer.getId());
            result = ftInterface.removeAll(dn,xstream.toXML(transferIds),NotificationType.TEXT.name());
        } catch (Exception e) {
            result = e;
        }
            
        assertTrue(result instanceof Integer);
        
        assertTrue(((Integer)result).intValue() == 0);
        
    }
    
    public void testRemoveMultipleNotificationsNullTransferId() {
        TGNotificationImpl ftInterface = new TGNotificationImpl();
        
        try {
            ArrayList<Integer> transferIds = new ArrayList<Integer>();
            transferIds.add(null);
            result = ftInterface.removeAll(dn,xstream.toXML(transferIds),NotificationType.TEXT.name());
        } catch (Exception e) {
            result = e;
        }
            
        assertTrue(result instanceof NotificationException);
        
    }
    
    public void testRemoveMultipleNotificationsInvalidTransferId() {
        TGNotificationImpl ftInterface = new TGNotificationImpl();
        
        try {
            ArrayList<Integer> transferIds = new ArrayList<Integer>();
            transferIds.add(transfer.getId());
            result = ftInterface.removeAll(dn,xstream.toXML(transferIds),NotificationType.TEXT.name());
        } catch (Exception e) {
            result = e;
        }
            
        assertTrue(result instanceof PersistenceException);
        
    }
    
    public void testRemoveMultipleNotificationUnauthorizedDN() {
        TGNotificationImpl ftInterface = new TGNotificationImpl();
        
        try {
            ArrayList<Integer> transferIds = new ArrayList<Integer>();
            transferIds.add(transfer.getId());
            result = ftInterface.removeAll(baddn,xstream.toXML(transferIds),NotificationType.TEXT.name());
        } catch (Exception e) {
            result = e;
        }
            
        assertTrue(result instanceof PermissionException);
        
    }

    /************************************************************************
     *                   Clear Notifications
     ************************************************************************/
    
    public void testSetupToClearNotificationHistory() {
        TGNotificationImpl ftInterface = new TGNotificationImpl();
        
        ftInterface.add(dn,transferId,NotificationType.EMAIL.name());
        ftInterface.add(dn,transferId,NotificationType.TEXT.name());
        
    }

    public void testClearNotifications() {
        TGNotificationImpl ftInterface = new TGNotificationImpl();
        
        try {
            ArrayList<Integer> transferIds = new ArrayList<Integer>();
            transferIds.add(transfer.getId());
            result = ftInterface.clear(dn,xstream.toXML(transferIds));
        } catch (Exception e) {
            result = e;
        }
            
        assertTrue(result instanceof Integer);
        
        assertTrue(((Integer)result).intValue() == 0);
        
    }
    
    public void testClearNotificationsNullTransferId() {
        TGNotificationImpl ftInterface = new TGNotificationImpl();
        
        try {
            ArrayList<Integer> transferIds = new ArrayList<Integer>();
            transferIds.add(null);
            result = ftInterface.clear(dn,xstream.toXML(transferIds));
        } catch (Exception e) {
            result = e;
        }
            
        assertTrue(result instanceof NotificationException);
        
    }
    
    public void testClearNotificationsInvalidTransferId() {
        TGNotificationImpl ftInterface = new TGNotificationImpl();
        
        try {
            ArrayList<Integer> transferIds = new ArrayList<Integer>();
            transferIds.add(new Integer(-1));
            result = ftInterface.clear(dn,xstream.toXML(transferIds));
        } catch (Exception e) {
            result = e;
        }
            
        assertTrue(result instanceof PersistenceException);
        
    }
    
    public void testClearNotificationUnauthorizedDN() {
        TGNotificationImpl ftInterface = new TGNotificationImpl();
        
        try {
            ArrayList<Integer> transferIds = new ArrayList<Integer>();
            transferIds.add(transfer.getId());
            result = ftInterface.clear(baddn,xstream.toXML(transferIds));
        } catch (Exception e) {
            result = e;
        }
            
        assertTrue(result instanceof PermissionException);
        
    }
    
    public void testCleanUpNotifcationTestData() {
        TGFileTransferHistoryImpl ftInterface = new TGFileTransferHistoryImpl();
        
        try {
            result = ftInterface.remove(dn,transferId);
        } catch (Exception e) {
            result = e;
        }
            
        assertTrue(result instanceof Integer);
        
        assertTrue(((Integer)result).intValue() == 0);
        
        props.remove(TRANSFER_ID_KEY);    
    }
    

    /************************************************************************
     *                   Junit Methods
     ************************************************************************/
    
    /* (non-Javadoc)
     * @see org.teragrid.portal.filebrowser.server.servlet.test.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        
        transfer = createTransfer();
        
        if (transferId != null && !transferId.equals("")) {
            transfer.setId(new Integer(transferId));
        }
    }
    
    /**
     * @param x
     */
    public NotificationInterfaceTest(String x) {
        super(x);
    }
    

    public static Test suite() {
        return new TestSuite(NotificationInterfaceTest.class);
    } 

}
