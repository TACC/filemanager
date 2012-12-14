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

import org.teragrid.portal.filebrowser.server.servlet.dao.UserDAO;
import org.teragrid.portal.filebrowser.server.servlet.model.user.User;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Tests to the user management api.
 * 
 * @author Rion Dooley < dooley [at] tacc [dot] utexas [dot] edu >
 *
 */
public class UserTest extends TestCaseWithData {

    
    public void testUserFind() throws Exception {
        user = UserDAO.findUserByDN(dn);
        assertNotNull(user);
    }
//    public void testUserSearchFindColleagues() throws Exception {
//    	List<User> users = UserDAO.findColleagues(dn);
//    	assertNotNull(users);
//    	assertTrue(users.size() > 0);
//    }
//    public void testUserSearchFindProjectPartners() throws Exception {
//    	List<User> users = UserDAO.findProjectPartners(dn);
//    	assertNotNull(users);
//    	assertTrue(users.size() > 0);
//    }
//    public void testUserSearchByPartialEmail() throws Exception {
//    	List<User> users = UserDAO.findByEmail(email.substring(0,email.indexOf('@')));
//    	assertNotNull(users);
//    	assertTrue(users.size() > 0);
//    }
//    
//    public void testUserSearchByFullEmail() throws Exception {
//    	List<User> users = UserDAO.findByEmail(email);
//    	assertNotNull(users);
//    	assertTrue(users.size() == 1);
//    }
    
    public void testUserSearchFirstNameProperCase() throws Exception {
    	List<User> users = UserDAO.findByName(firstName);
    	assertNotNull(users);
    	assertTrue(users.size() > 0);
    	System.out.println("Search for " + firstName + " returned " + users.size() + " results.");
    }
    
    public void testUserSearchFirstNameUpperCase() throws Exception {
    	List<User> users = UserDAO.findByName(firstName.toUpperCase());
    	assertNotNull(users);
    	assertTrue(users.size() > 0);
    	System.out.println("Search for " + firstName.toUpperCase()
    			+ " returned " + users.size() + " results.");
    }
    
    public void testUserSearchFirstNameLowerCase() throws Exception {
    	List<User> users = UserDAO.findByName(firstName.toLowerCase());
    	assertNotNull(users);
    	assertTrue(users.size() > 0);
    	System.out.println("Search for " + firstName.toLowerCase()
    			+ " returned " + users.size() + " results.");
    }
    
    public void testUserSearchLastNameProperCase() throws Exception {
    	List<User> users = UserDAO.findByName(lastName);
    	assertNotNull(users);
    	assertTrue(users.size() > 0);
    	System.out.println("Search for " + lastName + " returned " + users.size() + " results.");
    }
    
    public void testUserSearchLastNameUpperCase() throws Exception {
    	List<User> users = UserDAO.findByName(lastName.toUpperCase());
    	assertNotNull(users);
    	assertTrue(users.size() > 0);
    	System.out.println("Search for " + lastName.toUpperCase()
    			+ " returned " + users.size() + " results.");
    }
    
    public void testUserSearchLastNameLowerCase() throws Exception {
    	List<User> users = UserDAO.findByName(lastName.toLowerCase());
    	assertNotNull(users);
    	assertTrue(users.size() > 0);
    	System.out.println("Search for " + lastName.toLowerCase()
    			+ " returned " + users.size() + " results.");
    }
    
    public void testUserSearchFullNameProperCase() throws Exception {
    	List<User> users = UserDAO.findByName(firstName + " " + lastName);
    	assertNotNull(users);
    	assertTrue(users.size() > 0);
    	System.out.println("Search for " + firstName + " " + lastName
    			+ " returned " + users.size() + " results.");
    }
    
    public void testUserSearchFullNameUpperCase() throws Exception {
    	List<User> users = UserDAO.findByName(firstName.toUpperCase() + " " + lastName.toUpperCase());
    	assertNotNull(users);
    	assertTrue(users.size() > 0);
    	System.out.println("Search for " + firstName.toUpperCase() + " " + lastName.toUpperCase()
    			+ " returned " + users.size() + " results.");
    }
    
    public void testUserSearchFullNameLowerCase() throws Exception {
    	List<User> users = UserDAO.findByName(firstName.toLowerCase() + " " + lastName.toLowerCase());
    	assertNotNull(users);
    	assertTrue(users.size() > 0);
    	System.out.println("Search for " + firstName.toLowerCase() + " " + lastName.toLowerCase()
    			+ " returned " + users.size() + " results.");
    }
    
    public void testUserSearchFullNameMixedCase() throws Exception {
    	List<User> users = UserDAO.findByName(firstName.toUpperCase() + " " + lastName.toLowerCase());
    	assertNotNull(users);
    	assertTrue(users.size() > 0);
    	System.out.println("Search for " + firstName.toUpperCase() + " " + lastName.toLowerCase()
    			+ " returned " + users.size() + " results.");
    }
    
	public void testUserSearchFirstNameResuls() throws Exception {
    	List<User> users = UserDAO.findByName(firstName);
    	assertNotNull(users);
    	assertTrue(users.size() > 0);
    	System.out.println("Search for " + firstName + " returned " + users.size() + " results.");	
    	for(User user: users) {
    		assertTrue(user.getFirstName().contains(firstName) || 
    				user.getLastName().contains(firstName));
    	}
    }
    
    public void testUserSearchLastNameResults() throws Exception {
    	List<User> users = UserDAO.findByName(lastName);
    	assertNotNull(users);
    	assertTrue(users.size() > 0);
    	System.out.println("Search for " + lastName + " returned " + users.size() + " results.");
    	for(User user: users) {
    		assertTrue(user.getFirstName().startsWith(lastName) || 
    				user.getLastName().startsWith(lastName));
    	}
    }
    
//    public void testUserSearchOrganization() throws Exception {
//    	List<User> users = UserDAO.findByOrganization(organization);
//    	assertNotNull(users);
//    	assertTrue(users.size() > 0);
//    }
    
//    public void testUserSearchDepartment() throws Exception {
//    	List<User> users = UserDAO.findByDepartment(department);
//    	assertNotNull(users);
//    	assertTrue(users.size() > 0);
//    }
//    
//    public void testUserSearchUsername() throws Exception {
//    	List<User> users = UserDAO.findByUsername(username);
//    	assertNotNull(users);
//    	assertTrue(users.size() > 0);
//    }
    
    /**
     * 
     */
    public UserTest(String x) {
        super(x);
    }

    public static Test suite() {
        return new TestSuite(UserTest.class);
    }    

}
