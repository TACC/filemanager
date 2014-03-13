/**
 * 
 */
package edu.utexas.tacc.wcs.filemanager.service.resources.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.restlet.data.Status;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;

import edu.utexas.tacc.wcs.filemanager.common.model.User;
import edu.utexas.tacc.wcs.filemanager.common.restlet.resource.AbstractApiResource;
import edu.utexas.tacc.wcs.filemanager.service.dao.UserDAO;
import edu.utexas.tacc.wcs.filemanager.service.exception.AuthenticationException;
import edu.utexas.tacc.wcs.filemanager.service.exception.PermissionException;
import edu.utexas.tacc.wcs.filemanager.service.resources.ColleaguesResource;

/**
 * User colleague lookup service
 * 
 * @author dooley
 *
 */
public class ColleaguesResourceImpl extends AbstractApiResource implements ColleaguesResource
{
	private static final Logger logger = Logger.getLogger(ColleaguesResourceImpl.class);
	
	public ColleaguesResourceImpl() {}

	/* (non-Javadoc)
	 * @see edu.utexas.tacc.wcs.filemanager.service.resources.impl.ColleaguesResource#findColleagues()
	 */
	@Override
	@Get
	public List<User> findColleagues()
	{
		if (!StringUtils.isEmpty(dn))
            throw new AuthenticationException("Please supply a valid DN " + dn);
        
		try
		{
			List<User> colleagues = UserDAO.findColleagues(dn);
			
	        logger.debug("Found " + colleagues.size() + " colleagues");
	        
	        return colleagues;
	   } 
	   catch(PermissionException e)
	   {
		   throw new ResourceException(Status.CLIENT_ERROR_FORBIDDEN,
					"User does not have permission to access the colleague listing", e);
	   }
	   catch(Throwable e)
	   {
		   throw new ResourceException(Status.SERVER_ERROR_INTERNAL,
					"Failed to retrieve colleagues", e);
	   } 
	}
}
