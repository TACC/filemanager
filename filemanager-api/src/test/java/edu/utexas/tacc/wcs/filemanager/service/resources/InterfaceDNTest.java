/* 
 * Created on Jun 22, 2008
 * 
 * Developed by: Rion Dooley - dooley [at] tacc [dot] utexas [dot] edu
 * 				 TACC, Texas Advanced Computing Center
 * 
 * https://www.tacc.utexas.edu/
 */

package edu.utexas.tacc.wcs.filemanager.service.resources;

import org.testng.Assert;
import org.testng.annotations.Test;

import edu.utexas.tacc.wcs.filemanager.service.exception.AuthenticationException;
import edu.utexas.tacc.wcs.filemanager.service.resources.impl.SystemsResourceImpl;

/**
 * Tests for the servlet interfaces
 * 
 * @author Rion Dooley < dooley [at] tacc [dot] utexas [dot] edu >
 *
 */
@Test
public class InterfaceDNTest extends TestCaseWithData {
    public static Object result = null;
    
    public static final String FAKE_DN = "pete";
    
    /************************************************************************
     *                    Resource Discovery Tests
     ************************************************************************/
    
    public void testTGResourceDiscoveryRetrieveResourcesDeniesNullDN() {
        SystemsResource rdInterface = new SystemsResourceImpl();
        
        try {
            result = rdInterface.retrieveResources();
        } catch (Exception e) {
            result = e;
        }
            
        Assert.assertTrue(result instanceof AuthenticationException);
        
    }
    
    public void testTGResourceDiscoveryRetrieveResourcesDeniesInvalidDN() {
        SystemsResourceImpl rdInterface = new SystemsResourceImpl();
        rdInterface.setDn(FAKE_DN);
        try {
            result = rdInterface.retrieveResources();
        } catch (Exception e) {
            result = e;
        }
            
        Assert.assertTrue(result instanceof AuthenticationException);
        
    }
    
    public void testTGResourceDiscoveryRetrieveResourcesAcceptsValidDN() {
        SystemsResourceImpl rdInterface = new SystemsResourceImpl();
        rdInterface.setDn(dn);
        try {
            result = rdInterface.retrieveResources();
        } catch (Exception e) {
            result = e;
        }
            
        Assert.assertTrue(result instanceof String[]);
        
    }
    
    
    /************************************************************************
     *                    Transfer History Tests
     ************************************************************************/
    
//    public void testTGFileTransferHistoryAddDeniesNullDN() {
//        TransfersResourceImpl rdInterface = new TransfersResourceImpl();
//     
//        try {
//            result = rdInterface.add(null,"","","");
//        } catch (Exception e) {
//            result = e;
//        }
//            
//        Assert.assertTrue(result instanceof AuthenticationException);
//        
//    }
//    
//    public void testTGFileTransferHistoryAddDeniesInvalidDN() {
//        TransfersResourceImpl rdInterface = new TransfersResourceImpl();
//     
//        try {
//            result = rdInterface.add(FAKE_DN,"","","");
//        } catch (Exception e) {
//            result = e;
//        }
//            
//        Assert.assertTrue(result instanceof AuthenticationException);
//        
//    }
//    
//    public void testTGFileTransferHistoryGetDeniesNullDN() {
//        TransfersResourceImpl rdInterface = new TransfersResourceImpl();
//     
//        try {
//            result = rdInterface.get(null);
//        } catch (Exception e) {
//            result = e;
//        }
//            
//        Assert.assertTrue(result instanceof AuthenticationException);
//        
//    }
//    
//    public void testTGFileTransferHistoryGetDeniesInvalidDN() {
//        TransfersResourceImpl rdInterface = new TransfersResourceImpl();
//     
//        try {
//            result = rdInterface.get(FAKE_DN);
//        } catch (Exception e) {
//            result = e;
//        }
//            
//        Assert.assertTrue(result instanceof AuthenticationException);
//        
//    }
//    
//    public void testTGFileTransferHistoryRemoveDeniesNullDN() {
//        TransfersResourceImpl rdInterface = new TransfersResourceImpl();
//     
//        try {
//            result = rdInterface.remove(null,"");
//        } catch (Exception e) {
//            result = e;
//        }
//            
//        Assert.assertTrue(result instanceof AuthenticationException);
//        
//    }
//    
//    public void testTGFileTransferHistoryRemoveDeniesInvalidDN() {
//        TransfersResourceImpl rdInterface = new TransfersResourceImpl();
//     
//        try {
//            result = rdInterface.remove(FAKE_DN,"");
//        } catch (Exception e) {
//            result = e;
//        }
//            
//        Assert.assertTrue(result instanceof AuthenticationException);
//        
//    }
//    
//    public void testTGFileTransferHistoryUpdateDeniesNullDN() {
//        TransfersResourceImpl rdInterface = new TransfersResourceImpl();
//     
//        try {
//            result = rdInterface.update(null,"");
//        } catch (Exception e) {
//            result = e;
//        }
//            
//        Assert.assertTrue(result instanceof AuthenticationException);
//        
//    }
//    
//    public void testTGFileTransferHistoryUpdateDeniesInvalidDN() {
//        TransfersResourceImpl rdInterface = new TransfersResourceImpl();
//     
//        try {
//            result = rdInterface.update(FAKE_DN,"");
//        } catch (Exception e) {
//            result = e;
//        }
//            
//        Assert.assertTrue(result instanceof AuthenticationException);
//        
//    }
//    
//    /************************************************************************
//     *                    Notification Tests
//     ************************************************************************/
//    
//    public void testTGNotificationAddDeniesNullDN() {
//        NotificationsResourceImpl nInterface = new NotificationsResourceImpl();
//     
//        try {
//            result = nInterface.add(null,"","");
//        } catch (Exception e) {
//            result = e;
//        }
//            
//        Assert.assertTrue(result instanceof AuthenticationException);
//        
//    }
//    
//    public void testTGNotificationAddDeniesInvalidDN() {
//        NotificationsResourceImpl nInterface = new NotificationsResourceImpl();
//        
//        try {
//            result = nInterface.add(FAKE_DN,"","");
//        } catch (Exception e) {
//            result = e;
//        }
//            
//        Assert.assertTrue(result instanceof AuthenticationException);
//        
//    }
//    
//    public void testTGNotificationAddAllDeniesNullDN() {
//        NotificationsResourceImpl nInterface = new NotificationsResourceImpl();
//     
//        try {
//            result = nInterface.addAll(null,"","");
//        } catch (Exception e) {
//            result = e;
//        }
//            
//        Assert.assertTrue(result instanceof AuthenticationException);
//        
//    }
//    
//    public void testTGNotificationAddAllDeniesInvalidDN() {
//        NotificationsResourceImpl nInterface = new NotificationsResourceImpl();
//        
//        try {
//            result = nInterface.addAll(FAKE_DN,"","");
//        } catch (Exception e) {
//            result = e;
//        }
//            
//        Assert.assertTrue(result instanceof AuthenticationException);
//        
//    }
//    
//    public void testTGNotificationClearDeniesNullDN() {
//        NotificationsResourceImpl nInterface = new NotificationsResourceImpl();
//     
//        try {
//            result = nInterface.clear(null,"");
//        } catch (Exception e) {
//            result = e;
//        }
//            
//        Assert.assertTrue(result instanceof AuthenticationException);
//        
//    }
//    
//    public void testTGNotificationClearDeniesInvalidDN() {
//        NotificationsResourceImpl nInterface = new NotificationsResourceImpl();
//        
//        try {
//            result = nInterface.clear(FAKE_DN,"");
//        } catch (Exception e) {
//            result = e;
//        }
//            
//        Assert.assertTrue(result instanceof AuthenticationException);
//        
//    }
//    
//    public void testTGNotificationRemoveDeniesNullDN() {
//        NotificationsResourceImpl nInterface = new NotificationsResourceImpl();
//     
//        try {
//            result = nInterface.remove(null,"","");
//        } catch (Exception e) {
//            result = e;
//        }
//            
//        Assert.assertTrue(result instanceof AuthenticationException);
//        
//    }
//    
//    public void testTGNotificationRemoveDeniesInvalidDN() {
//        NotificationsResourceImpl nInterface = new NotificationsResourceImpl();
//        
//        try {
//            result = nInterface.remove(FAKE_DN,"","");
//        } catch (Exception e) {
//            result = e;
//        }
//            
//        Assert.assertTrue(result instanceof AuthenticationException);
//        
//    }
//    
//    public void testTGNotificationRemoveAllDeniesNullDN() {
//        NotificationsResourceImpl nInterface = new NotificationsResourceImpl();
//     
//        try {
//            result = nInterface.removeMultipleNotifications(null,"","");
//        } catch (Exception e) {
//            result = e;
//        }
//            
//        Assert.assertTrue(result instanceof AuthenticationException);
//        
//    }
//    
//    public void testTGNotificationRemoveAllDeniesInvalidDN() {
//        NotificationsResourceImpl nInterface = new NotificationsResourceImpl();
//        
//        try {
//            result = nInterface.removeMultipleNotifications(FAKE_DN,"","");
//        } catch (Exception e) {
//            result = e;
//        }
//            
//        Assert.assertTrue(result instanceof AuthenticationException);
//        
//    }
}
