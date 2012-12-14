/* 
 * Created on Jun 22, 2008
 * 
 * Developed by: Rion Dooley - dooley [at] tacc [dot] utexas [dot] edu
 * 				 TACC, Texas Advanced Computing Center
 * 
 * https://www.tacc.utexas.edu/
 */

package org.teragrid.portal.filebrowser.server.servlet.test;

import org.teragrid.portal.filebrowser.server.servlet.impl.TGResourceDiscoveryImpl;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Test classes to verify the correctness of the resource discovery interfaces.
 * 
 * @author Rion Dooley < dooley [at] tacc [dot] utexas [dot] edu >
 *
 */
public class ResourceInterfaceTest extends TestCaseWithData {
    public static Object result = null;
    
    public void testRetrievesResources() {
        TGResourceDiscoveryImpl rdInterface = new TGResourceDiscoveryImpl();
        
        try {
            result = rdInterface.retrieveResources(dn);
        } catch (Exception e) {
            e.printStackTrace();
            result = e;
        }
            
        assertTrue(result instanceof String[]);
        
    }
    
    /**
     * @param x
     */
    public ResourceInterfaceTest(String x) {
        super(x);
    }
    
    public static Test suite() {
        return new TestSuite(ResourceInterfaceTest.class);
    } 
}
