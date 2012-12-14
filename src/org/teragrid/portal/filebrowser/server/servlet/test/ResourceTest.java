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

import org.apache.log4j.Logger;
import org.teragrid.portal.filebrowser.server.servlet.dao.TeraGridSystemDAO;
import org.teragrid.portal.filebrowser.server.servlet.dao.UserDAO;
import org.teragrid.portal.filebrowser.server.servlet.model.resource.TeraGridSystem;
import org.teragrid.portal.filebrowser.server.servlet.model.resource.TeraGridSystemResolver;

import junit.framework.Test;
import junit.framework.TestSuite;

public class ResourceTest extends TestCaseWithData{

	private static final Logger logger = Logger.getLogger(ResourceTest.class);
	
    public void testResourceFind() {
        List<TeraGridSystem> systems = TeraGridSystemDAO.findSystemAccounts(user);
        
        assertNotNull(systems);
        
        logger.debug("Found " + systems.size() + " system resources for user " + 
                user.getUsername());
        System.out.println("Found " + systems.size() + " system resources for user " + 
                user.getUsername());
        for (TeraGridSystem system: systems) {
            logger.debug(system.toString());
            System.out.println(system.getResourceName());
        }
    }
    
    public void testResourceResolution() {
        
        List<TeraGridSystem> systems = TeraGridSystemDAO.findSystemAccounts(user);
        
        systems = TeraGridSystemResolver.resolveResources(systems);
        
        assertNotNull(systems);
        
        for (TeraGridSystem system: systems) {
            if (system.getFTPHostname()== null || system.getFTPHostname().equals("")) {
                logger.debug("Failed to resolve system " + system.toString());
                System.out.println("Failed to resolve system " + system.getFTPHostname());
                assertNotNull(system.getFTPHostname());
                
            }
            System.out.println(system.toString());
        }
    }
    
    @Override
    protected void setUp() throws Exception{
    	super.setUp();
        user = UserDAO.findUserByDN(dn);
        assertNotNull(user);
    }
    
    public ResourceTest(String x) {
        super(x);
    }

    public static Test suite() {
        return new TestSuite(ResourceTest.class);
    }    
}
