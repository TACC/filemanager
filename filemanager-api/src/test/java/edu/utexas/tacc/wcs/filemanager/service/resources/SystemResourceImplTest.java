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

import edu.utexas.tacc.wcs.filemanager.service.resources.impl.SystemsResourceImpl;

/**
 * Test classes to verify the correctness of the resource discovery interfaces.
 * 
 * @author Rion Dooley < dooley [at] tacc [dot] utexas [dot] edu >
 *
 */
public class SystemResourceImplTest extends TestCaseWithData 
{
    public static Object result = null;
    
    @Test
    public void testRetrievesResources() 
    {
        SystemsResourceImpl rdInterface = new SystemsResourceImpl();
        rdInterface.setDn(dn);
        try {
            result = rdInterface.retrieveResources();
        } catch (Exception e) {
            e.printStackTrace();
            result = e;
        }
            
        Assert.assertNotNull(result, "No systems returned");
        
    }
}
