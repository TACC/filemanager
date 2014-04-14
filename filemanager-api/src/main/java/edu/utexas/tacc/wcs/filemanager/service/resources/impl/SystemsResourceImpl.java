/* 
 * Created on Dec 16, 2007
 * 
 * Developed by: Rion Dooley - dooley [at] tacc [dot] utexas [dot] edu
 * 				 TACC, Texas Advanced Computing Center
 * 
 * https://www.tacc.utexas.edu/
 */

package edu.utexas.tacc.wcs.filemanager.service.resources.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.restlet.data.Status;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;
import org.teragrid.service.profile.util.XsedeSystemResolver;

import edu.utexas.tacc.wcs.filemanager.common.model.System;
import edu.utexas.tacc.wcs.filemanager.common.model.User;
import edu.utexas.tacc.wcs.filemanager.common.restlet.resource.AbstractApiResource;
import edu.utexas.tacc.wcs.filemanager.service.dao.SystemDAO;
import edu.utexas.tacc.wcs.filemanager.service.dao.UserDAO;
import edu.utexas.tacc.wcs.filemanager.service.exception.PermissionException;
import edu.utexas.tacc.wcs.filemanager.service.resources.SystemsResource;

/**
 * Resource discovery class. 
 * 
 * @author Rion Dooley < dooley [at] tacc [dot] utexas [dot] edu >
 *
 */
public class SystemsResourceImpl extends AbstractApiResource implements SystemsResource
{
	private static final Logger logger = Logger.getLogger(SystemsResourceImpl.class);
    
    public SystemsResourceImpl() {}
    
    /* (non-Javadoc)
	 * @see edu.utexas.tacc.wcs.filemanager.service.resources.impl.SystemsResource#retrieveResources()
	 */
    @Override
	@Get
    public List<System> retrieveResources() {
        
       if (StringUtils.isEmpty(dn))
        	throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,
        			"Please supply a valid DN");
       try
       {
	        // dn = "/C=US/O=UTAustin/OU=TACC/CN=Rion Dooley/UID=dooley";
	        
	        // Verify that the user's DN is valid
	        User user = UserDAO.loadUserByDN(dn);
	        
	        logger.info("Retrieving resources for " + user.getUsername() + "...");
	        
	        // Get the resources on which the user has a valid account and resolve
	        // the hostname issues caused by inaccuracies in the TGCDB
	        List<System> systems = XsedeSystemResolver.resolveResources(
	                    SystemDAO.findSystemAccounts(user));
	        
	        logger.info("Found " + systems.size() + " resources for user " + user.getUsername());
	        
	        return systems;
       }
       catch(PermissionException e)
       {
    	   throw new ResourceException(Status.CLIENT_ERROR_FORBIDDEN,
					"User does not have permission to access the resource listing", e);
       }
       catch(Throwable e)
       {
    	   throw new ResourceException(Status.SERVER_ERROR_INTERNAL,
					"Failed to retrieve user resources", e);
       }  
    }
}
