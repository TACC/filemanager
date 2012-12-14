/* 
 * Created on Jun 22, 2008
 * 
 * Developed by: Rion Dooley - dooley [at] tacc [dot] utexas [dot] edu
 * 				 TACC, Texas Advanced Computing Center
 * 
 * https://www.tacc.utexas.edu/
 */

package org.teragrid.portal.filebrowser.server.servlet.test;

import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.teragrid.portal.filebrowser.applet.transfer.Task;
import org.teragrid.portal.filebrowser.server.servlet.exception.PermissionException;
import org.teragrid.portal.filebrowser.server.servlet.exception.PersistenceException;
import org.teragrid.portal.filebrowser.server.servlet.impl.TGFileTransferHistoryImpl;
import org.teragrid.portal.filebrowser.server.servlet.model.filetransfer.Transfer;
import org.teragrid.portal.filebrowser.server.servlet.model.notification.NotificationType;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.basic.DateConverter;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * Tests of the RPC file transfer history inferface.
 * 
 * @author Rion Dooley < dooley [at] tacc [dot] utexas [dot] edu >
 *
 */
public class FileTransferHistoryInterfaceTest extends TestCaseWithData {
    
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
     *                    History Management No Notification
     ************************************************************************/
    
    @SuppressWarnings("unchecked")
	public void testGetTransferHistory() {
        
        TGFileTransferHistoryImpl ftInterface = new TGFileTransferHistoryImpl();
        
        try {
            result = ftInterface.get(dn);
        } catch (Exception e) {
            result = e;
        }
            
        assertTrue(result instanceof String);
        
        assertTrue(xstream.fromXML((String)result) instanceof List);
        
    }

    public void testAddTransfer() {
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
    
    public void testAddTransferUnauthorizedDN() {
        TGFileTransferHistoryImpl ftInterface = new TGFileTransferHistoryImpl();
        
        try {
            transfer.setDn(baddn);
            result = ftInterface.add(dn,xstream.toXML(transfer),"",NotificationType.NONE.name());
        } catch (Exception e) {
            result = e;
        }
            
        assertTrue(result instanceof PermissionException);
        
    }
    
    public void testUpdateTransfer() {
        
        TGFileTransferHistoryImpl ftInterface = new TGFileTransferHistoryImpl();
        
        try {
            transfer.setStatus(Task.DONE);
            result = ftInterface.update(dn,xstream.toXML(transfer));
        } catch (Exception e) {
            result = e;
        }
            
        assertTrue(result instanceof Integer);
        
        assertTrue(((Integer)result).intValue() == 0);
    }
    
    public void testUpdateTransferNullTransfer() {
        
        TGFileTransferHistoryImpl ftInterface = new TGFileTransferHistoryImpl();
        
        try {
            result = ftInterface.update(dn,null);
        } catch (Exception e) {
            result = e;
        }
            
        assertTrue(result instanceof PersistenceException);
        
    }
    
    public void testUpdateTransferNullTransferId() {
        
        TGFileTransferHistoryImpl ftInterface = new TGFileTransferHistoryImpl();
        
        try {
            transfer.setId(null);
            result = ftInterface.update(dn,xstream.toXML(transfer));
        } catch (Exception e) {
            result = e;
        }
            
        assertTrue(result instanceof PersistenceException);
        
    }
    
    public void testUpdateTransferEmptyTransfer() {
        
        TGFileTransferHistoryImpl ftInterface = new TGFileTransferHistoryImpl();
        
        try {
            result = ftInterface.update(dn,"");
        } catch (Exception e) {
            result = e;
        }
            
        assertTrue(result instanceof PersistenceException);
        
    }
    
    public void testUpdateTransferInvalidStatus() {
        
        TGFileTransferHistoryImpl ftInterface = new TGFileTransferHistoryImpl();
        
        try {
            transfer.setStatus(18);
            result = ftInterface.update(dn,xstream.toXML(transfer));
        } catch (Exception e) {
            result = e;
        }
            
        assertTrue(result instanceof PersistenceException);
        
    }
    
    public void testUpdateTransferUnauthorizedDN() {
        TGFileTransferHistoryImpl ftInterface = new TGFileTransferHistoryImpl();
        
        try {
            result = ftInterface.update(baddn,xstream.toXML(transfer));
        } catch (Exception e) {
            result = e;
        }
            
        assertTrue(result instanceof PermissionException);
        
    }

    public void testRemoveTransferNullTransferId() {
        TGFileTransferHistoryImpl ftInterface = new TGFileTransferHistoryImpl();
        
        try {
            result = ftInterface.remove(dn,null);
        } catch (Exception e) {
            result = e;
        }
            
        assertTrue(result instanceof Exception);
        
    }
    
    public void testRemoveTransferBadTransferId() {
        TGFileTransferHistoryImpl ftInterface = new TGFileTransferHistoryImpl();
        
        try {
            result = ftInterface.remove(dn,"");
        } catch (Exception e) {
            result = e;
        }
            
        assertTrue(result instanceof Exception);
        
    }
    
    public void testRemoveTransferUnauthorizedDN() {
        TGFileTransferHistoryImpl ftInterface = new TGFileTransferHistoryImpl();
        
        try {
            result = ftInterface.remove(baddn,transferId);
        } catch (Exception e) {
            result = e;
        }
            
        assertTrue(result instanceof PermissionException);
        
    }
    
    public void testRemoveTransfer() {
        TGFileTransferHistoryImpl ftInterface = new TGFileTransferHistoryImpl();
        
        try {
            result = ftInterface.remove(dn,transferId);
        } catch (Exception e) {
            result = e;
        }
            
        assertTrue(result instanceof Integer);
        
        assertTrue(((Integer)result).intValue() == 0);
    }
    
    
    public void testRemoveTransferInvalidTransferId() {
        TGFileTransferHistoryImpl ftInterface = new TGFileTransferHistoryImpl();
        
        try {
            result = ftInterface.remove(dn,transferId);
        } catch (Exception e) {
            result = e;
        }
            
        assertTrue(result instanceof Exception);
        
    }
    
    public void testUpdateTransferAlreadyDeleted() {
        TGFileTransferHistoryImpl ftInterface = new TGFileTransferHistoryImpl();
        
        try {
            result = ftInterface.remove(dn,transferId);
        } catch (Exception e) {
            result = e;
        }
            
        assertTrue(result instanceof Exception);
        
        props.remove(TRANSFER_ID_KEY);
    }

    /************************************************************************
     *                    History Management Email Notification
     ************************************************************************/
    
    public void testAddTransferContainingEmailNotification() {
        TGFileTransferHistoryImpl ftInterface = new TGFileTransferHistoryImpl();
        
        try {
            result = ftInterface.add(dn,xstream.toXML(transfer),"",NotificationType.EMAIL.name());
        } catch (Exception e) {
            result = e;
        }
            
        assertTrue(result instanceof Integer);
        
        assertTrue(((Integer)result).intValue() != -1);
        
        props.put(TRANSFER_ID_KEY,result.toString());
    }
    
    public void testUpdateTransferContainingEmailNotification() {
        
        assertTrue(transferId != null && !transferId.equals(""));
        
        TGFileTransferHistoryImpl ftInterface = new TGFileTransferHistoryImpl();
        
        try {
            transfer.setStatus(Task.DONE);
            result = ftInterface.update(dn,xstream.toXML(transfer));
        } catch (Exception e) {
            result = e;
        }
            
        assertTrue(result instanceof Integer);
        
        assertTrue(((Integer)result).intValue() == 0);
    }
    
    public void testRemoveTransferContainingEmailNotification() {
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
     *                    History Management Text Notification
     ************************************************************************/
    
    public void testAddTransferContainingTextNotification() {
        TGFileTransferHistoryImpl ftInterface = new TGFileTransferHistoryImpl();
        
        try {
            Transfer transfer = createTransfer();
            result = ftInterface.add(dn,xstream.toXML(transfer),"",NotificationType.TEXT.name());
        } catch (Exception e) {
            result = e;
        }
            
        assertTrue(result instanceof Integer);
        
        assertTrue(((Integer)result).intValue() != -1);
        
        props.put(TRANSFER_ID_KEY,result.toString());
    }
    
    public void testUpdateTransferContainingTextNotification() {
        
        assertTrue(transferId != null && !transferId.equals(""));
        
        TGFileTransferHistoryImpl ftInterface = new TGFileTransferHistoryImpl();
        
        try {
            transfer.setStatus(Task.DONE);
            result = ftInterface.update(dn,xstream.toXML(transfer));
        } catch (Exception e) {
            result = e;
        }
            
        assertTrue(result instanceof Integer);
        
        assertTrue(((Integer)result).intValue() == 0);
    }
    
    public void testRemoveTransferContainingTextNotification() {
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
    public FileTransferHistoryInterfaceTest(String x) {
        super(x);
    }
    

    public static Test suite() {
        return new TestSuite(FileTransferHistoryInterfaceTest.class);
    } 

}
