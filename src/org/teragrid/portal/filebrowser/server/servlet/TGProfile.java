/**
 * 
 */
package org.teragrid.portal.filebrowser.server.servlet;

import java.util.Arrays;
import java.util.List;

/**
 * Interface to the TGProfile service.  This service has one method, getProfile that returns
 * a <List> of <User> objects representing the matching users.
 * @author dooley
 *
 */
public interface TGProfile {
	public static final String EMAIL = "email";
	public static final String NAME = "name";
	public static final String ORGANIZATION = "organization";
	public static final String USERNAME = "username";
	public static final String DEPARTMENT = "department";
	
	public static final List<String> QUERY_TYPES = Arrays.asList(USERNAME,EMAIL,NAME);//,ORGANIZATION,DEPARTMENT);
	
	public String findUsers(String dn, String queryType, String searchString) throws Exception;
	public String findColleagues(String dn) throws Exception;
	public String findProjectPartners(String dn) throws Exception;
}
