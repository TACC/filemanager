/* 
 * Created on Jun 22, 2008
 * 
 * Developed by: Rion Dooley - dooley [at] tacc [dot] utexas [dot] edu
 * 				 TACC, Texas Advanced Computing Center
 * 
 * https://www.tacc.utexas.edu/
 */

package org.teragrid.portal.filebrowser.server.servlet.test;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.teragrid.portal.filebrowser.server.servlet.exception.AuthenticationException;
import org.teragrid.portal.filebrowser.server.servlet.impl.TGFileTransferHistoryImpl;
import org.teragrid.portal.filebrowser.server.servlet.impl.TGNotificationImpl;
import org.teragrid.portal.filebrowser.server.servlet.impl.TGResourceDiscoveryImpl;

/**
 * Tests for the servlet interfaces
 * 
 * @author Rion Dooley < dooley [at] tacc [dot] utexas [dot] edu >
 *
 */
public class InterfaceDNTest extends TestCaseWithData {
    public static Object result = null;
    
    public static final String FAKE_DN = "pete";
    
    /************************************************************************
     *                    Resource Discovery Tests
     ************************************************************************/
    
    
    public void testTGResourceDiscoveryRetrieveResourcesDeniesNullDN() {
        TGResourceDiscoveryImpl rdInterface = new TGResourceDiscoveryImpl();
     
        try {
            result = rdInterface.retrieveResources(null);
        } catch (Exception e) {
            result = e;
        }
            
        assertTrue(result instanceof AuthenticationException);
        
    }
    
    public void testTGResourceDiscoveryRetrieveResourcesDeniesInvalidDN() {
        TGResourceDiscoveryImpl rdInterface = new TGResourceDiscoveryImpl();
     
        try {
            result = rdInterface.retrieveResources(FAKE_DN);
        } catch (Exception e) {
            result = e;
        }
            
        assertTrue(result instanceof AuthenticationException);
        
    }
    
    public void testTGResourceDiscoveryRetrieveResourcesAcceptsValidDN() {
        TGResourceDiscoveryImpl rdInterface = new TGResourceDiscoveryImpl();
     
        try {
            result = rdInterface.retrieveResources(dn);
        } catch (Exception e) {
            result = e;
        }
            
        assertTrue(result instanceof String[]);
        
    }
    
    
    /************************************************************************
     *                    Transfer History Tests
     ************************************************************************/
    
    public void testTGFileTransferHistoryAddDeniesNullDN() {
        TGFileTransferHistoryImpl rdInterface = new TGFileTransferHistoryImpl();
     
        try {
            result = rdInterface.add(null,"","","");
        } catch (Exception e) {
            result = e;
        }
            
        assertTrue(result instanceof AuthenticationException);
        
    }
    
    public void testTGFileTransferHistoryAddDeniesInvalidDN() {
        TGFileTransferHistoryImpl rdInterface = new TGFileTransferHistoryImpl();
     
        try {
            result = rdInterface.add(FAKE_DN,"","","");
        } catch (Exception e) {
            result = e;
        }
            
        assertTrue(result instanceof AuthenticationException);
        
    }
    
    public void testTGFileTransferHistoryGetDeniesNullDN() {
        TGFileTransferHistoryImpl rdInterface = new TGFileTransferHistoryImpl();
     
        try {
            result = rdInterface.get(null);
        } catch (Exception e) {
            result = e;
        }
            
        assertTrue(result instanceof AuthenticationException);
        
    }
    
    public void testTGFileTransferHistoryGetDeniesInvalidDN() {
        TGFileTransferHistoryImpl rdInterface = new TGFileTransferHistoryImpl();
     
        try {
            result = rdInterface.get(FAKE_DN);
        } catch (Exception e) {
            result = e;
        }
            
        assertTrue(result instanceof AuthenticationException);
        
    }
    
    public void testTGFileTransferHistoryRemoveDeniesNullDN() {
        TGFileTransferHistoryImpl rdInterface = new TGFileTransferHistoryImpl();
     
        try {
            result = rdInterface.remove(null,"");
        } catch (Exception e) {
            result = e;
        }
            
        assertTrue(result instanceof AuthenticationException);
        
    }
    
    public void testTGFileTransferHistoryRemoveDeniesInvalidDN() {
        TGFileTransferHistoryImpl rdInterface = new TGFileTransferHistoryImpl();
     
        try {
            result = rdInterface.remove(FAKE_DN,"");
        } catch (Exception e) {
            result = e;
        }
            
        assertTrue(result instanceof AuthenticationException);
        
    }
    
    public void testTGFileTransferHistoryUpdateDeniesNullDN() {
        TGFileTransferHistoryImpl rdInterface = new TGFileTransferHistoryImpl();
     
        try {
            result = rdInterface.update(null,"");
        } catch (Exception e) {
            result = e;
        }
            
        assertTrue(result instanceof AuthenticationException);
        
    }
    
    public void testTGFileTransferHistoryUpdateDeniesInvalidDN() {
        TGFileTransferHistoryImpl rdInterface = new TGFileTransferHistoryImpl();
     
        try {
            result = rdInterface.update(FAKE_DN,"");
        } catch (Exception e) {
            result = e;
        }
            
        assertTrue(result instanceof AuthenticationException);
        
    }
    
    /************************************************************************
     *                    Notification Tests
     ************************************************************************/
    
    public void testTGNotificationAddDeniesNullDN() {
        TGNotificationImpl nInterface = new TGNotificationImpl();
     
        try {
            result = nInterface.add(null,"","");
        } catch (Exception e) {
            result = e;
        }
            
        assertTrue(result instanceof AuthenticationException);
        
    }
    
    public void testTGNotificationAddDeniesInvalidDN() {
        TGNotificationImpl nInterface = new TGNotificationImpl();
        
        try {
            result = nInterface.add(FAKE_DN,"","");
        } catch (Exception e) {
            result = e;
        }
            
        assertTrue(result instanceof AuthenticationException);
        
    }
    
    public void testTGNotificationAddAllDeniesNullDN() {
        TGNotificationImpl nInterface = new TGNotificationImpl();
     
        try {
            result = nInterface.addAll(null,"","");
        } catch (Exception e) {
            result = e;
        }
            
        assertTrue(result instanceof AuthenticationException);
        
    }
    
    public void testTGNotificationAddAllDeniesInvalidDN() {
        TGNotificationImpl nInterface = new TGNotificationImpl();
        
        try {
            result = nInterface.addAll(FAKE_DN,"","");
        } catch (Exception e) {
            result = e;
        }
            
        assertTrue(result instanceof AuthenticationException);
        
    }
    
    public void testTGNotificationClearDeniesNullDN() {
        TGNotificationImpl nInterface = new TGNotificationImpl();
     
        try {
            result = nInterface.clear(null,"");
        } catch (Exception e) {
            result = e;
        }
            
        assertTrue(result instanceof AuthenticationException);
        
    }
    
    public void testTGNotificationClearDeniesInvalidDN() {
        TGNotificationImpl nInterface = new TGNotificationImpl();
        
        try {
            result = nInterface.clear(FAKE_DN,"");
        } catch (Exception e) {
            result = e;
        }
            
        assertTrue(result instanceof AuthenticationException);
        
    }
    
    public void testTGNotificationRemoveDeniesNullDN() {
        TGNotificationImpl nInterface = new TGNotificationImpl();
     
        try {
            result = nInterface.remove(null,"","");
        } catch (Exception e) {
            result = e;
        }
            
        assertTrue(result instanceof AuthenticationException);
        
    }
    
    public void testTGNotificationRemoveDeniesInvalidDN() {
        TGNotificationImpl nInterface = new TGNotificationImpl();
        
        try {
            result = nInterface.remove(FAKE_DN,"","");
        } catch (Exception e) {
            result = e;
        }
            
        assertTrue(result instanceof AuthenticationException);
        
    }
    
    public void testTGNotificationRemoveAllDeniesNullDN() {
        TGNotificationImpl nInterface = new TGNotificationImpl();
     
        try {
            result = nInterface.removeAll(null,"","");
        } catch (Exception e) {
            result = e;
        }
            
        assertTrue(result instanceof AuthenticationException);
        
    }
    
    public void testTGNotificationRemoveAllDeniesInvalidDN() {
        TGNotificationImpl nInterface = new TGNotificationImpl();
        
        try {
            result = nInterface.removeAll(FAKE_DN,"","");
        } catch (Exception e) {
            result = e;
        }
            
        assertTrue(result instanceof AuthenticationException);
        
    }
    
    /**
     * @param x
     */
    public InterfaceDNTest(String x) {
        super(x);
    }
    
    public static Test suite() {
        return new TestSuite(InterfaceDNTest.class);
    } 

}
