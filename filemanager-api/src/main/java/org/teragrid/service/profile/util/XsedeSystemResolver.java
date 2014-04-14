/* 
 * Created on Jun 20, 2008
 * 
 * Developed by: Rion Dooley - dooley [at] tacc [dot] utexas [dot] edu
 * 				 TACC, Texas Advanced Computing Center
 * 
 * https://www.tacc.utexas.edu/
 */

package org.teragrid.service.profile.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.teragrid.service.profile.wsclients.model.ComputeDTO;
import org.teragrid.service.profile.wsclients.model.Service;

import edu.utexas.tacc.wcs.filemanager.common.model.System;
import edu.utexas.tacc.wcs.filemanager.common.model.enumeration.FileProtocolType;
import edu.utexas.tacc.wcs.filemanager.common.model.enumeration.SystemType;

/**
 * Utility class to resolve the TGCDB and inca short names with the
 * actual resource hostnames and gridftp endpoints.
 * 
 * @author Rion Dooley < dooley [at] tacc [dot] utexas [dot] edu >
 *
 */
public class XsedeSystemResolver {

	private static final Logger logger = Logger.getLogger(XsedeSystemResolver.class);
	
    /**
     * Adjusts the bad information coming from the db with accurate
     * hostname and user info.  
     * 
     * @param systems  List from TeraGridSystem accounts hydration
     * @return a list of valid TeraGridSystem objects
     */
	public static List<System> resolveResources(List<System> tgcdbResources) 
    {
    	List<System> resolvedSystems = new ArrayList<System>();
       
    	Map <String, ComputeDTO> cachedSystems = ResourceCache.getResources();
       
    	for (System tgcdbSystem: tgcdbResources)
    	{
	    	for (ComputeDTO cachedSystem: cachedSystems.values()) 
	    	{
	    		if (StringUtils.equalsIgnoreCase(tgcdbSystem.getResourceId(), cachedSystem.getTgcdbName())) {
	    			String message = "Found " + tgcdbSystem.getResourceId() + " in resource cache..."; 
	    			if (cachedSystem.getStatus().equals(Service.UP)) 
					{
		    			System system = new System();
						system.setResourceName(cachedSystem.getName());
						system.setSshHostname(cachedSystem.getLoginHostname());
						system.setSshPort(cachedSystem.getLoginPort());
						system.setFtpHostname(cachedSystem.getGridftpHostname());
						system.setFtpPort(cachedSystem.getGridftpPort());
						system.setStatus(cachedSystem.getStatus());
						system.setResourceId(cachedSystem.getResourceId());
						system.setSystemType(SystemType.valueOf(cachedSystem.getType()));
						system.setProtocol(FileProtocolType.GRIDFTP);
						system.setInstitution(cachedSystem.getSite());
						resolvedSystems.add(system);
						message += "added to user list";
					}
	    			else 
	    			{
	    				message += "skipping due to downtime";
	    			}
	    			
	    			logger.debug(message);
	    		}
	    	}
    	}
    	
    	Collections.sort(resolvedSystems);
   
    	return resolvedSystems;
    }
}
