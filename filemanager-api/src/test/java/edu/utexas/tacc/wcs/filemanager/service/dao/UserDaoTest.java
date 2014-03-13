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

import edu.utexas.tacc.wcs.filemanager.common.model.User;
import edu.utexas.tacc.wcs.filemanager.service.resources.TestCaseWithData;

/**
 * Tests to the user management dao.
 * 
 * @author Rion Dooley < dooley [at] tacc [dot] utexas [dot] edu >
 *
 */
@Test
public class UserDaoTest extends TestCaseWithData {

    public void testUserFind() throws Exception {
        user = UserDAO.findUserByDN(dn);
        Assert.assertNotNull(user);
    }
//    public void testUserSearchFindColleagues() throws Exception {
//    	List<User> users = UserDAO.findColleagues(dn);
//    	Assert.assertNotNull(users);
//    	Assert.assertTrue(users.size() > 0);
//    }
//    public void testUserSearchFindProjectPartners() throws Exception {
//    	List<User> users = UserDAO.findProjectPartners(dn);
//    	Assert.assertNotNull(users);
//    	Assert.assertTrue(users.size() > 0);
//    }
//    public void testUserSearchByPartialEmail() throws Exception {
//    	List<User> users = UserDAO.findByEmail(email.substring(0,email.indexOf('@')));
//    	Assert.assertNotNull(users);
//    	Assert.assertTrue(users.size() > 0);
//    }
//    
//    public void testUserSearchByFullEmail() throws Exception {
//    	List<User> users = UserDAO.findByEmail(email);
//    	Assert.assertNotNull(users);
//    	Assert.assertTrue(users.size() == 1);
//    }
    
    public void testUserSearchFirstNameProperCase() throws Exception {
    	List<User> users = UserDAO.findByName(firstName);
    	Assert.assertNotNull(users);
    	Assert.assertTrue(users.size() > 0);
    	System.out.println("Search for " + firstName + " returned " + users.size() + " results.");
    }
    
    public void testUserSearchFirstNameUpperCase() throws Exception {
    	List<User> users = UserDAO.findByName(firstName.toUpperCase());
    	Assert.assertNotNull(users);
    	Assert.assertTrue(users.size() > 0);
    	System.out.println("Search for " + firstName.toUpperCase()
    			+ " returned " + users.size() + " results.");
    }
    
    public void testUserSearchFirstNameLowerCase() throws Exception {
    	List<User> users = UserDAO.findByName(firstName.toLowerCase());
    	Assert.assertNotNull(users);
    	Assert.assertTrue(users.size() > 0);
    	System.out.println("Search for " + firstName.toLowerCase()
    			+ " returned " + users.size() + " results.");
    }
    
    public void testUserSearchLastNameProperCase() throws Exception {
    	List<User> users = UserDAO.findByName(lastName);
    	Assert.assertNotNull(users);
    	Assert.assertTrue(users.size() > 0);
    	System.out.println("Search for " + lastName + " returned " + users.size() + " results.");
    }
    
    public void testUserSearchLastNameUpperCase() throws Exception {
    	List<User> users = UserDAO.findByName(lastName.toUpperCase());
    	Assert.assertNotNull(users);
    	Assert.assertTrue(users.size() > 0);
    	System.out.println("Search for " + lastName.toUpperCase()
    			+ " returned " + users.size() + " results.");
    }
    
    public void testUserSearchLastNameLowerCase() throws Exception {
    	List<User> users = UserDAO.findByName(lastName.toLowerCase());
    	Assert.assertNotNull(users);
    	Assert.assertTrue(users.size() > 0);
    	System.out.println("Search for " + lastName.toLowerCase()
    			+ " returned " + users.size() + " results.");
    }
    
    public void testUserSearchFullNameProperCase() throws Exception {
    	List<User> users = UserDAO.findByName(firstName + " " + lastName);
    	Assert.assertNotNull(users);
    	Assert.assertTrue(users.size() > 0);
    	System.out.println("Search for " + firstName + " " + lastName
    			+ " returned " + users.size() + " results.");
    }
    
    public void testUserSearchFullNameUpperCase() throws Exception {
    	List<User> users = UserDAO.findByName(firstName.toUpperCase() + " " + lastName.toUpperCase());
    	Assert.assertNotNull(users);
    	Assert.assertTrue(users.size() > 0);
    	System.out.println("Search for " + firstName.toUpperCase() + " " + lastName.toUpperCase()
    			+ " returned " + users.size() + " results.");
    }
    
    public void testUserSearchFullNameLowerCase() throws Exception {
    	List<User> users = UserDAO.findByName(firstName.toLowerCase() + " " + lastName.toLowerCase());
    	Assert.assertNotNull(users);
    	Assert.assertTrue(users.size() > 0);
    	System.out.println("Search for " + firstName.toLowerCase() + " " + lastName.toLowerCase()
    			+ " returned " + users.size() + " results.");
    }
    
    public void testUserSearchFullNameMixedCase() throws Exception {
    	List<User> users = UserDAO.findByName(firstName.toUpperCase() + " " + lastName.toLowerCase());
    	Assert.assertNotNull(users);
    	Assert.assertTrue(users.size() > 0);
    	System.out.println("Search for " + firstName.toUpperCase() + " " + lastName.toLowerCase()
    			+ " returned " + users.size() + " results.");
    }
    
	public void testUserSearchFirstNameResuls() throws Exception {
    	List<User> users = UserDAO.findByName(firstName);
    	Assert.assertNotNull(users);
    	Assert.assertTrue(users.size() > 0);
    	System.out.println("Search for " + firstName + " returned " + users.size() + " results.");	
    	for(User user: users) {
    		Assert.assertTrue(user.getFirstName().contains(firstName) || 
    				user.getLastName().contains(firstName));
    	}
    }
    
	public void testUserSearchLastNameResults() throws Exception {
    	List<User> users = UserDAO.findByName(lastName);
    	Assert.assertNotNull(users);
    	Assert.assertTrue(users.size() > 0);
    	System.out.println("Search for " + lastName + " returned " + users.size() + " results.");
    	for(User user: users) {
    		Assert.assertTrue(user.getFirstName().startsWith(lastName) || 
    				user.getLastName().startsWith(lastName));
    	}
    }
    
//    public void testUserSearchOrganization() throws Exception {
//    	List<User> users = UserDAO.findByOrganization(organization);
//    	Assert.assertNotNull(users);
//    	Assert.assertTrue(users.size() > 0);
//    }
    
//    public void testUserSearchDepartment() throws Exception {
//    	List<User> users = UserDAO.findByDepartment(department);
//    	Assert.assertNotNull(users);
//    	Assert.assertTrue(users.size() > 0);
//    }
//    
//    public void testUserSearchUsername() throws Exception {
//    	List<User> users = UserDAO.findByUsername(username);
//    	Assert.assertNotNull(users);
//    	Assert.assertTrue(users.size() > 0);
//    }
    
 }
