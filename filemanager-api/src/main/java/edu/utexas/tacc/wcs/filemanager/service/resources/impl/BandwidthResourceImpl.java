/* 
 * Created on Dec 16, 2007
 * 
 * Developed by: Rion Dooley - dooley [at] tacc [dot] utexas [dot] edu
 * 				 TACC, Texas Advanced Computing Center
 * 
 * https://www.tacc.utexas.edu/
 */

package edu.utexas.tacc.wcs.filemanager.service.resources.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.restlet.data.Status;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import edu.utexas.tacc.wcs.filemanager.common.model.BandwidthQuery;
import edu.utexas.tacc.wcs.filemanager.service.Settings;
import edu.utexas.tacc.wcs.filemanager.service.dao.BandwidthDAO;
import edu.utexas.tacc.wcs.filemanager.service.resources.BandwidthResource;

/**
 * Query for bandwidth between two points.
 * 
 * @author Rion Dooley < dooley [at] tacc [dot] utexas [dot] edu >
 *
 */
public class BandwidthResourceImpl extends ServerResource implements BandwidthResource
{
	private static final Logger logger = Logger.getLogger(BandwidthResourceImpl.class);
	
    public BandwidthResourceImpl() {}
    
    /* (non-Javadoc)
	 * @see edu.utexas.tacc.wcs.filemanager.service.resources.impl.BandwidthResource#getBandwidth(java.lang.String, java.lang.String)
	 */
    @Override
	@Get
    public Double getBandwidth(BandwidthQuery query) 
    {
    	if (StringUtils.isEmpty(query.getSource()))
        	throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,
        			"Please supply a valid source system");
    	
    	if (StringUtils.isEmpty(query.getDest()))
        	throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,
        			"Please supply a valid destination system");
		try 
		{	
			BandwidthDAO speedpage = new BandwidthDAO(Settings.SPEEDPAGE_SERVER);
			Double result = speedpage.getMeasuredBandwidth(query.getSource(), query.getDest()).getMeasurement();
			
			return result;
			
		} catch (Exception e) {
			logger.error("Failed to resolve bandwidth",e);
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL,
					"Failed to resolve bandwidth", e);
		}
	}
}
