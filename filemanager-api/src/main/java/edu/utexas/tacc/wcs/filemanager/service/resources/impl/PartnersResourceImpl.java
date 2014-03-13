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
import edu.utexas.tacc.wcs.filemanager.service.resources.PartnersResource;

/**
 * User project partner lookup service
 * 
 * @author dooley
 *
 */
public class PartnersResourceImpl extends AbstractApiResource implements PartnersResource
{
	private static final Logger logger = Logger.getLogger(PartnersResourceImpl.class);
	
	public PartnersResourceImpl() {}

	/* (non-Javadoc)
	 * @see edu.utexas.tacc.wcs.filemanager.service.resources.impl.PartnersResource#findProjectPartners()
	 */
	@Override
	@Get
	public List<User> findProjectPartners()
	{
		if (!StringUtils.isEmpty(dn))
            throw new AuthenticationException("Please supply a valid DN " + dn);
        
		try
		{
			List<User> colleagues = UserDAO.findProjectPartners(dn);
			
	        logger.debug("Found " + colleagues.size() + " partners");
	        
	        return colleagues;
	   } 
	   catch(PermissionException e)
	   {
		   throw new ResourceException(Status.CLIENT_ERROR_FORBIDDEN,
					"User does not have permission to access the partner listing", e);
	   }
	   catch(Throwable e)
	   {
		   throw new ResourceException(Status.SERVER_ERROR_INTERNAL,
					"Failed to retrieve partners", e);
	   } 
	}
}
