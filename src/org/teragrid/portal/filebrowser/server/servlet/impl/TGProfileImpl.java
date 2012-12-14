/**
 * 
 */
package org.teragrid.portal.filebrowser.server.servlet.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.teragrid.portal.filebrowser.server.servlet.TGProfile;
import org.teragrid.portal.filebrowser.server.servlet.dao.UserDAO;
import org.teragrid.portal.filebrowser.server.servlet.exception.AuthenticationException;
import org.teragrid.portal.filebrowser.server.servlet.exception.UserSearchException;
import org.teragrid.portal.filebrowser.server.servlet.model.user.User;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.basic.DateConverter;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * Implementation of the TGProfile service to find users based on fixed search criteria.
 * @author dooley
 *
 */
public class TGProfileImpl implements TGProfile {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(TGProfileImpl.class);
	
	private static XStream xstream = new XStream(new DomDriver());
    
    private static String defaultFormat = "MMM d, yyyy h:mm:ss a";
    
    private static String[] acceptableFormats = new String[] {
            "MM/dd/yyyy HH:mm:ss",
            "yyyy-MM-dd HH:mm:ss.S z",
            "yyyy-MM-dd HH:mm:ss.S a",
            "yyyy-MM-dd HH:mm:ssz",
            "yyyy-MM-dd HH:mm:ss z", // JDK 1.3 needs both prev versions
            "yyyy-MM-dd HH:mm:ssa" }; // backwards compatability
    
    {
        xstream.registerConverter(new DateConverter(defaultFormat, acceptableFormats));
    }
    
	public TGProfileImpl() {}

	/* (non-Javadoc)
	 * @see org.teragrid.portal.filebrowser.server.servlet.TGProfile#findUsers(java.lang.String, java.lang.String)
	 */
	public String findUsers(String dn, String queryType, String searchString)
			throws Exception {
		
		if (!isValid(dn))
            throw new AuthenticationException("Please supply a valid DN " + dn);
        
		if (!QUERY_TYPES.contains(queryType)) {
			throw new UserSearchException("Invalid query type: " + queryType);
		}
		
		searchString = searchString.trim();
		
		List<User> users = new ArrayList<User>();
		
		if (queryType.equals(USERNAME)) {
			users = UserDAO.findByUsername(searchString);
		} else if (queryType.equals(EMAIL)) {
			users = UserDAO.findByUsername(searchString);
//		} else if (QUERY_TYPES.equals(ORGANIZATION)) {
//			users = UserDAO.findByUsername(searchString);
//		} else if (QUERY_TYPES.equals(DEPARTMENT)) {
//			users = UserDAO.findByUsername(searchString);
		} else if (queryType.equals(NAME)) {
			users = UserDAO.findByName(searchString);
		}
		
		List<User> usersDAO = new ArrayList<User>();
		for (User user: users) {
			usersDAO.add(user.shallowCopy());
		}
		return xstream.toXML(usersDAO);
	}

	public String findColleagues(String dn) throws Exception {
		
		if (!isValid(dn))
            throw new AuthenticationException("Please supply a valid DN " + dn);
		
		List<User> usersDAO = new ArrayList<User>();
		
		for (User user: UserDAO.findColleagues(dn)) {
			usersDAO.add(user.shallowCopy());
		}
		
		return xstream.toXML(usersDAO);
	}

	public String findProjectPartners(String dn) throws Exception {
		if (!isValid(dn))
            throw new AuthenticationException("Please supply a valid DN " + dn);
		
		List<User> userDTOs = new ArrayList<User>();
		
		for (User user: UserDAO.findProjectPartners(dn)) {
			userDTOs.add(user.shallowCopy());
		}
		
		return xstream.toXML(userDTOs);
	}

    private boolean isValid(String s) {
        return !(s==null || s.equals(""));
    }
	
}
