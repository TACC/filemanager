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

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.utexas.tacc.wcs.filemanager.service.dao.TransferDAO;
import edu.utexas.tacc.wcs.filemanager.service.persistence.HibernateUtil;

/**
 * Tests of the RPC file transfer history inferface.
 * 
 * @author Rion Dooley < dooley [at] tacc [dot] utexas [dot] edu >
 *
 */
public class TransferResourceImplTest extends TestCaseWithData 
{
	private ObjectMapper mapper = new ObjectMapper();
	private TransferDAO dao = new TransferDAO();
    
    @BeforeMethod 
    protected void setUp() throws Exception {
        transfer = createTransfer();
        dao.makePersistent(transfer);
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
		session.flush();
		session.close();
	}

    /************************************************************************
     *                    History Management No Notification
     ************************************************************************/
    
//    @SuppressWarnings("unchecked")
//    @Test
//	public void testGetTransferHistory() {
//        
//        TransfersResourceImpl ftInterface = new TransfersResourceImpl();
//        
//        try {
//            response = ftInterface.getAllTransfers(dn);
//        } catch (Exception e) {
//            Assert.fail("Failed to retrieve transfers.", e);
//        }
//        
//        Assert.assertEquals(response.getStatus(), 200, "Service request failed.");
//        
//        JsonNode result = parseApiResponse(response);
//        
//        Assert.assertTrue(result instanceof ArrayNode);
//        
//        Assert.assertEquals(result.size(), 1, "Incorrect number of transfers returned.");
//        
//    }
//
//    @Test public void testAddTransfer() {
//        TransfersResourceImpl ftInterface = new TransfersResourceImpl();
//        
//        try {
//            response = ftInterface.addMultipleTransfers(dn, mapper.writeValueAsString(transfer),"",NotificationType.NONE.name());
//        } catch (Exception e) {
//            response = e;
//        }
//            
//        Assert.assertTrue(response instanceof Integer);
//        
//        Assert.assertTrue(((Integer)response).intValue() != -1);
//        
//        props.put(TRANSFER_ID_KEY,response.toString());
//    }
//    
//    @Test public void testAddTransferUnauthorizedDN() {
//        TransfersResourceImpl ftInterface = new TransfersResourceImpl();
//        
//        try {
//            transfer.setDn(baddn);
//            response = ftInterface.add(dn,xstream.toXML(transfer),"",NotificationType.NONE.name());
//        } catch (Exception e) {
//            response = e;
//        }
//            
//        Assert.assertTrue(response instanceof PermissionException);
//        
//    }
//    
//    @Test public void testUpdateTransfer() {
//        
//        TransfersResourceImpl ftInterface = new TransfersResourceImpl();
//        
//        try {
//            transfer.setStatus(Task.DONE);
//            response = ftInterface.update(dn,xstream.toXML(transfer));
//        } catch (Exception e) {
//            response = e;
//        }
//            
//        Assert.assertTrue(response instanceof Integer);
//        
//        Assert.assertTrue(((Integer)response).intValue() == 0);
//    }
//    
//    @Test public void testUpdateTransferNullTransfer() {
//        
//        TransfersResourceImpl ftInterface = new TransfersResourceImpl();
//        
//        try {
//            response = ftInterface.update(dn,null);
//        } catch (Exception e) {
//            response = e;
//        }
//            
//        Assert.assertTrue(response instanceof PersistenceException);
//        
//    }
//    
//    @Test public void testUpdateTransferNullTransferId() {
//        
//        TransfersResourceImpl ftInterface = new TransfersResourceImpl();
//        
//        try {
//            transfer.setId(null);
//            response = ftInterface.update(dn,xstream.toXML(transfer));
//        } catch (Exception e) {
//            response = e;
//        }
//            
//        Assert.assertTrue(response instanceof PersistenceException);
//        
//    }
//    
//    @Test public void testUpdateTransferEmptyTransfer() {
//        
//        TransfersResourceImpl ftInterface = new TransfersResourceImpl();
//        
//        try {
//            response = ftInterface.update(dn,"");
//        } catch (Exception e) {
//            response = e;
//        }
//            
//        Assert.assertTrue(response instanceof PersistenceException);
//        
//    }
//    
//    @Test public void testUpdateTransferInvalidStatus() {
//        
//        TransfersResourceImpl ftInterface = new TransfersResourceImpl();
//        
//        try {
//            transfer.setStatus(18);
//            response = ftInterface.update(dn,xstream.toXML(transfer));
//        } catch (Exception e) {
//            response = e;
//        }
//            
//        Assert.assertTrue(response instanceof PersistenceException);
//        
//    }
//    
//    @Test public void testUpdateTransferUnauthorizedDN() {
//        TransfersResourceImpl ftInterface = new TransfersResourceImpl();
//        
//        try {
//            response = ftInterface.update(baddn,xstream.toXML(transfer));
//        } catch (Exception e) {
//            response = e;
//        }
//            
//        Assert.assertTrue(response instanceof PermissionException);
//        
//    }
//
//    @Test public void testRemoveTransferNullTransferId() {
//        TransfersResourceImpl ftInterface = new TransfersResourceImpl();
//        
//        try {
//            response = ftInterface.remove(dn,null);
//        } catch (Exception e) {
//            response = e;
//        }
//            
//        Assert.assertTrue(response instanceof Exception);
//        
//    }
//    
//    @Test public void testRemoveTransferBadTransferId() {
//        TransfersResourceImpl ftInterface = new TransfersResourceImpl();
//        
//        try {
//            response = ftInterface.remove(dn,"");
//        } catch (Exception e) {
//            response = e;
//        }
//            
//        Assert.assertTrue(response instanceof Exception);
//        
//    }
//    
//    @Test public void testRemoveTransferUnauthorizedDN() {
//        TransfersResourceImpl ftInterface = new TransfersResourceImpl();
//        
//        try {
//            response = ftInterface.remove(baddn,transferId);
//        } catch (Exception e) {
//            response = e;
//        }
//            
//        Assert.assertTrue(response instanceof PermissionException);
//        
//    }
//    
//    @Test public void testRemoveTransfer() {
//        TransfersResourceImpl ftInterface = new TransfersResourceImpl();
//        
//        try {
//            response = ftInterface.remove(dn,transferId);
//        } catch (Exception e) {
//            response = e;
//        }
//            
//        Assert.assertTrue(response instanceof Integer);
//        
//        Assert.assertTrue(((Integer)response).intValue() == 0);
//    }
//    
//    
//    @Test public void testRemoveTransferInvalidTransferId() {
//        TransfersResourceImpl ftInterface = new TransfersResourceImpl();
//        
//        try {
//            response = ftInterface.remove(dn,transferId);
//        } catch (Exception e) {
//            response = e;
//        }
//            
//        Assert.assertTrue(response instanceof Exception);
//        
//    }
//    
//    @Test public void testUpdateTransferAlreadyDeleted() {
//        TransfersResourceImpl ftInterface = new TransfersResourceImpl();
//        
//        try {
//            response = ftInterface.remove(dn,transferId);
//        } catch (Exception e) {
//            response = e;
//        }
//            
//        Assert.assertTrue(response instanceof Exception);
//        
//        props.remove(TRANSFER_ID_KEY);
//    }
//
//    /************************************************************************
//     *                    History Management Email Notification
//     ************************************************************************/
//    
//    @Test public void testAddTransferContainingEmailNotification() {
//        TransfersResourceImpl ftInterface = new TransfersResourceImpl();
//        
//        try {
//            response = ftInterface.add(dn,xstream.toXML(transfer),"",NotificationType.EMAIL.name());
//        } catch (Exception e) {
//            response = e;
//        }
//            
//        Assert.assertTrue(response instanceof Integer);
//        
//        Assert.assertTrue(((Integer)response).intValue() != -1);
//        
//        props.put(TRANSFER_ID_KEY,response.toString());
//    }
//    
//    @Test public void testUpdateTransferContainingEmailNotification() {
//        
//        Assert.assertTrue(transferId != null && !transferId.equals(""));
//        
//        TransfersResourceImpl ftInterface = new TransfersResourceImpl();
//        
//        try {
//            transfer.setStatus(Task.DONE);
//            response = ftInterface.update(dn,xstream.toXML(transfer));
//        } catch (Exception e) {
//            response = e;
//        }
//            
//        Assert.assertTrue(response instanceof Integer);
//        
//        Assert.assertTrue(((Integer)response).intValue() == 0);
//    }
//    
//    @Test public void testRemoveTransferContainingEmailNotification() {
//        TransfersResourceImpl ftInterface = new TransfersResourceImpl();
//        
//        try {
//            response = ftInterface.remove(dn,transferId);
//        } catch (Exception e) {
//            response = e;
//        }
//            
//        Assert.assertTrue(response instanceof Integer);
//        
//        Assert.assertTrue(((Integer)response).intValue() == 0);
//        
//        props.remove(TRANSFER_ID_KEY);
//    }
//    
//
//    /************************************************************************
//     *                    History Management Text Notification
//     ************************************************************************/
//    
//    @Test public void testAddTransferContainingTextNotification() {
//        TransfersResourceImpl ftInterface = new TransfersResourceImpl();
//        
//        try {
//            Transfer transfer = createTransfer();
//            response = ftInterface.add(dn,xstream.toXML(transfer),"",NotificationType.TEXT.name());
//        } catch (Exception e) {
//            response = e;
//        }
//            
//        Assert.assertTrue(response instanceof Integer);
//        
//        Assert.assertTrue(((Integer)response).intValue() != -1);
//        
//        props.put(TRANSFER_ID_KEY,response.toString());
//    }
//    
//    @Test public void testUpdateTransferContainingTextNotification() {
//        
//        Assert.assertTrue(transferId != null && !transferId.equals(""));
//        
//        TransfersResourceImpl ftInterface = new TransfersResourceImpl();
//        
//        try {
//            transfer.setStatus(Task.DONE);
//            response = ftInterface.update(dn,xstream.toXML(transfer));
//        } catch (Exception e) {
//            response = e;
//        }
//            
//        Assert.assertTrue(response instanceof Integer);
//        
//        Assert.assertTrue(((Integer)response).intValue() == 0);
//    }
//    
//    @Test public void testRemoveTransferContainingTextNotification() {
//        TransfersResourceImpl ftInterface = new TransfersResourceImpl();
//        
//        try {
//            response = ftInterface.remove(dn,transferId);
//        } catch (Exception e) {
//            response = e;
//        }
//            
//        Assert.assertTrue(response instanceof Integer);
//        
//        Assert.assertTrue(((Integer)response).intValue() == 0);
//        
//        props.remove(TRANSFER_ID_KEY);
//    }
    
}
