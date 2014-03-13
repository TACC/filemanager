/* 
 * Created on Jun 22, 2008
 * 
 * Developed by: Rion Dooley - dooley [at] tacc [dot] utexas [dot] edu
 * 				 TACC, Texas Advanced Computing Center
 * 
 * https://www.tacc.utexas.edu/
 */

package edu.utexas.tacc.wcs.filemanager.service.manager;

import java.util.List;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.teragrid.service.profile.util.TeraGridSystemResolver;
import org.testng.annotations.Test;

import edu.utexas.tacc.wcs.filemanager.service.dao.SystemDAO;
import edu.utexas.tacc.wcs.filemanager.service.dao.UserDAO;
import edu.utexas.tacc.wcs.filemanager.common.model.System;
import edu.utexas.tacc.wcs.filemanager.service.resources.TestCaseWithData;

@Test
public class TeraGridSystemResolverTest extends TestCaseWithData
{
	private static final Logger logger = Logger.getLogger(TeraGridSystemResolverTest.class);
	
    public void testResourceResolution() {
        
    	user = UserDAO.findUserByDN(dn);
        
        List<System> systems = SystemDAO.findSystemAccounts(user);
        
        systems = TeraGridSystemResolver.resolveResources(systems);
        
        Assert.assertNotNull(systems);
        
        for (System system: systems) {
            if (system.getFTPHostname()== null || system.getFTPHostname().equals("")) {
                logger.debug("Failed to resolve system " + system.toString());
                Assert.assertNotNull(system.getFTPHostname());
                
            }
        }
    }
}
