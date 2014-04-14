/**
 * 
 */
package edu.utexas.tacc.wcs.filemanager.service.resources.impl;

import static edu.utexas.tacc.wcs.filemanager.common.model.enumerations.UserQueryType.EMAIL;
import static edu.utexas.tacc.wcs.filemanager.common.model.enumerations.UserQueryType.NAME;
import static edu.utexas.tacc.wcs.filemanager.common.model.enumerations.UserQueryType.USERNAME;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.restlet.data.Status;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ResourceException;

import edu.utexas.tacc.wcs.filemanager.common.model.User;
import edu.utexas.tacc.wcs.filemanager.common.model.UserQuery;
import edu.utexas.tacc.wcs.filemanager.common.model.enumerations.UserQueryType;
import edu.utexas.tacc.wcs.filemanager.common.restlet.resource.AbstractApiResource;
import edu.utexas.tacc.wcs.filemanager.service.dao.UserDAO;
import edu.utexas.tacc.wcs.filemanager.service.exception.AuthenticationException;
import edu.utexas.tacc.wcs.filemanager.service.exception.PermissionException;
import edu.utexas.tacc.wcs.filemanager.service.resources.UsersResource;

/**
 * User lookup service
 * 
 * @author dooley
 *
 */
public class UsersResourceImpl extends AbstractApiResource implements UsersResource
{
	private static final Logger logger = Logger.getLogger(UsersResourceImpl.class);
	
	public UsersResourceImpl() {}

	/* (non-Javadoc)
	 * @see edu.utexas.tacc.wcs.filemanager.service.resources.impl.UsersResource#findUsers(edu.utexas.tacc.wcs.filemanager.common.model.UserQuery)
	 */
	@Override
	@Post
	public List<User> findUsers(UserQuery userQuery)
	{
		if (StringUtils.isEmpty(dn))
            throw new AuthenticationException("Please supply a valid DN");
        
		if (userQuery.getUserQueryType() == null) {
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,
					"Invalid query type: " + userQuery.getUserQueryType());
		}
		try
		{
			List<User> users = new ArrayList<User>();
			
			if (userQuery.getUserQueryType().equals(USERNAME)) {
				users = UserDAO.findByUsername(userQuery.getSearchTerm());
			} else if (userQuery.getUserQueryType().equals(EMAIL)) {
				users = UserDAO.findByEmail(userQuery.getSearchTerm());
			} else if (userQuery.getUserQueryType().equals(NAME)) {
				users = UserDAO.findByName(userQuery.getSearchTerm());
			}
			
	        logger.debug("Found " + users.size() + " users.");
        	
	        return users;
	   } 
	   catch(PermissionException e)
	   {
		   throw new ResourceException(Status.CLIENT_ERROR_FORBIDDEN,
					"User does not have permission to access the user listing", e);
	   }
	   catch(Throwable e)
	   {
		   throw new ResourceException(Status.SERVER_ERROR_INTERNAL,
					"Failed to retrieve user resources", e);
	   } 
	}
	
	@Get
	public UserQuery getSampleQuery() {
		
		return new UserQuery(UserQueryType.EMAIL, "dooley@tacc.utexas.edu");
	}
}
