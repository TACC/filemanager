/* 
 * Created on Jun 22, 2008
 * 
 * Developed by: Rion Dooley - dooley [at] tacc [dot] utexas [dot] edu
 * 				 TACC, Texas Advanced Computing Center
 * 
 * https://www.tacc.utexas.edu/
 */

package edu.utexas.tacc.wcs.filemanager.service.dao;

import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

import edu.utexas.tacc.wcs.filemanager.common.model.System;
import edu.utexas.tacc.wcs.filemanager.service.resources.TestCaseWithData;
 
public class SystemDAOTest extends TestCaseWithData
{
	@Test 
    public void testFindSystemAccounts() 
    {
    	user = UserDAO.findUserByDN(dn);
        
    	Assert.assertNotNull(user, "No user found matching dn " + dn);
    	
    	List<System> systems = SystemDAO.findSystemAccounts(user);
        
        Assert.assertNotNull(systems, "No systems found for user " + user.getWholeName());
    }
}
