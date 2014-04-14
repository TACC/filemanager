package org.teragrid.service.profile.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.restlet.Context;
import org.teragrid.service.profile.wsclients.KitRdrClient;
import org.teragrid.service.profile.wsclients.KitServicesClient;
import org.teragrid.service.profile.wsclients.TGOutageClient;
import org.teragrid.service.profile.wsclients.CtssClient;
import org.teragrid.service.profile.wsclients.model.ComputeDTO;
import org.teragrid.service.profile.wsclients.model.Service;

import edu.utexas.tacc.wcs.filemanager.common.model.enumeration.SystemType;
import edu.utexas.tacc.wcs.filemanager.service.Settings;


public class ResourceCache {
	private static final Logger log = Logger.getLogger(ResourceCache.class);
	
	private static Map<String, ComputeDTO> resourceCache = new HashMap<String, ComputeDTO>();
	private static long lastUpdated = System.currentTimeMillis();

	public static void init(Context context) {}
		
	public static Map<String, ComputeDTO> getResources() {
		
		checkCache();
		
		return resourceCache;
	}
	
	public static ComputeDTO getResourceByTgcdbName(String tgcdbName) {
		
		checkCache();
		
		for(ComputeDTO system: resourceCache.values()) {
			if (system.getTgcdbName().equalsIgnoreCase(tgcdbName)) {
				return system;
			}
		}
		
		return null;
	}

	public static ComputeDTO getResourceByResourceId(String resourceId) {
		
		checkCache();
		
		return resourceCache.get(resourceId);
	}
	
	public static ComputeDTO getResourceByName(String name) {
		
		checkCache();
		
		for(ComputeDTO system: resourceCache.values()) {
			if (system.getName().equalsIgnoreCase(name)) {
				return system;
			}
		}
		
		return null;
	}
	
	private static void checkCache() {
		
		 // check to see if the cache has expired
		if (lastUpdated + Settings.REFRESH_INTERVAL <= System.currentTimeMillis() || resourceCache.isEmpty()) {
    		log.debug("Updating entire resource cache...");
    		
    		try {
				refreshResources();
			} catch (IOException e) {
				log.error("Failed to retrieve resource listing from ctss-resource-v1",e);
			}
			
			try {
				refreshEndpoints();
			} catch (IOException e) {
				log.error("Failed to retrieve resource listing from ctss-resource-v1",e);
			}
			
    		try {
				updateDowntimes();
			} catch (IOException e) {
				log.error("Failed to retrieve resource downtimes from Inca",e);
			}
    		
    		lastUpdated = System.currentTimeMillis();
        }
	}
	
	/**
	 * Calls the ctss service to get a listing of resources with their tgcdb id
	 * mappings and filters it by the list of currently available and active
	 * resources from the rdr service.
	 * 
	 * @throws IOException
	 */
	private static void refreshResources() throws IOException {
		KitRdrClient rdrClient = new KitRdrClient(Settings.RDR_SERVICES_SERVER);
		CtssClient ctssClient = new CtssClient(Settings.CTSS_SERVER);
		Map<String, ComputeDTO> rdrResources = rdrClient.getResources();
		Map<String, ComputeDTO> ctssResources = ctssClient.getResources();
		for(String resourceId: ctssResources.keySet())
		{
			if (rdrResources.containsKey(resourceId)) {
				ComputeDTO ctssResource = ctssResources.get(resourceId);
				ComputeDTO rdrResource = rdrResources.get(resourceId);
				ctssResource.setName(rdrResource.getName());
				ctssResource.setType(rdrResource.getType());
				resourceCache.put(resourceId, ctssResource);
			}
		}
	}
	
	/**
	 * Adds the gsissh and gsi open ssh endpoints to each resource.
	 * 
	 * @throws IOException
	 */
	private static void refreshEndpoints() throws IOException 
	{
		KitServicesClient client = new KitServicesClient(Settings.KIT_SERVICES_SERVER);
		Map<String, ComputeDTO> kitSystems = client.getResources();
		for(String resourceId: kitSystems.keySet())
		{
			if (resourceCache.containsKey(resourceId)) 
			{
				ComputeDTO kitSystem = kitSystems.get(resourceId);
				ComputeDTO cachedSystem = resourceCache.get(resourceId);
				cachedSystem.setGridftpHostname(kitSystem.getGridftpHostname());
				cachedSystem.setGridftpPort(kitSystem.getGridftpPort());
				if (!StringUtils.equalsIgnoreCase(cachedSystem.getType(), SystemType.ARCHIVE.name())) 
				{
					cachedSystem.setLoginHostname(kitSystem.getLoginHostname());
					cachedSystem.setLoginPort(kitSystem.getLoginPort());
				}
			}
		}
	}
	
	/**
	 * Updates the availability of each resource using the status from iis.
	 * 
	 * @throws IOException
	 */
	private static void updateDowntimes() throws IOException {
		TGOutageClient client = new TGOutageClient(Settings.IIS_OUTAGE_SERVER);
		
		Map<String, ComputeDTO> downtimeResources = client.getResources();
		for(String resourceId: downtimeResources.keySet()) {
			resourceCache.get(resourceId).setStatus(resourceCache.containsKey(resourceId) ? Service.DOWN : Service.UP);
		}
	}
	
	/**
	 * Returns a sample csv output as would be returned from the ctss-resource-v1 service
	 * @return
	 */
	@SuppressWarnings("unused")
	private static String createDummyData() {
		return "SiteID,ResourceID,TgcdbResourceName,ResourceName,OrganizationName,PopsName,AmieName" + 
				"iu.teragrid.org,bigred.iu.teragrid.org,\"bigred.iu.teragrid\",\"IU BigRed\",\"Indiana University\",\"Indiana University IBM e1350 (Big Red)\",\"IU\"" + 
				"iu.teragrid.org,hpss.iu.teragrid.org,\"hpss.iu.teragrid\",\"IU HPSS Archival Service\",\"Indiana University\",\"Indiana University HPSS Archival Storage\",\"IU\"" + 
				"loni-lsu.teragrid.org,queenbee.loni-lsu.teragrid.org,\"queenbee.loni-lsu.teragrid\",\"LONI Queenbee\",\"Louisiana Optical Network Initiative\",\"\",\"LONI-LSU\"" + 
				"ncar.teragrid.org,frost.ncar.teragrid.org,\"frost.ncar.teragrid\",\"frost.ncar.teragrid.org\",\"National Center for Atmospheric Research\",\"NCAR IBM Blue Gene (Frost)\",\"NCAR\"" + 
				"ncsa.teragrid.org,abe.ncsa.teragrid.org,\"abe.ncsa.teragrid\",\"NCSA Abe\",\"National Center for Supercomputing Applications\",\"NCSA/LONI Dell PowerEdge Linux Clusters (Abe/Queenbee)\",\"NCSA\"" + 
				"ncsa.teragrid.org,cobalt.ncsa.teragrid.org,\"cobalt.ncsa.teragrid\",\"NCSA Cobalt Altix\",\"National Center for Supercomputing Applications\",\"NCSA SGI Altix (Cobalt)\",\"NCSA\"" + 
				"ncsa.teragrid.org,dtf.ncsa.teragrid.org,\"dtf.ncsa.teragrid\",\"dtf.ncsa.teragrid.org\",\"National Center for Supercomputing Applications\",\"\",\"NCSA\"" + 
				"ncsa.teragrid.org,lincoln.ncsa.teragrid.org,\"lincoln.ncsa.teragrid\",\"NCSA Lincoln\",\"National Center for Supercomputing Applications\",\"NCSA Lincoln Supercluster\",\"NCSA\"" + 
				"nics.teragrid.org,kraken.nics.teragrid.org,\"kraken.nics.teragrid\",\"NICS Kraken Cray XT5\",\"University of Tennessee, Knoxville\",\"NICS Cray XT5 (Kraken)\",\"NICS\"" + 
				"ornl.teragrid.org,nstg.ornl.teragrid.org,\"nstg.ornl.teragrid\",\"ORNL Neutron Science TeraGrid Gateway\",\"Oak Ridge National Laboratory\",\"\",\"ORNL\"" + 
				"psc.teragrid.org,bigben.psc.teragrid.org,\"bigben.psc.teragrid\",\"PSC Bigben\",\"Pittsburgh Supercomputing Center\",\"PSC XT3 (Big Ben)\",\"PSC\"" + 
				"psc.teragrid.org,pople.psc.teragrid.org,\"pople.psc.teragrid\",\"PSC Pople\",\"Pittsburgh Supercomputing Center\",\"PSC SGI Altix (Pople)\",\"PSC\"" + 
				"purdue.teragrid.org,brutus.purdue.teragrid.org,\"brutus.purdue.teragrid\",\"Purdue Condor Pools\",\"Purdue University\",\"Purdue FPGA Prototyping Environment (Brutus)\",\"Purdue\"" + 
				"purdue.teragrid.org,condor.purdue.teragrid.org,\"condor.purdue.teragrid\",\"Purdue Condor Pools\",\"Purdue University\",\"Purdue Condor Pool\",\"Purdue\"" + 
				"purdue.teragrid.org,steele.purdue.teragrid.org,\"steele.purdue.teragrid\",\"Purdue Steele\",\"Purdue University\",\"Purdue Dell PowerEdge Linux Cluster (Steele)\",\"Purdue\"" + 
				"sdsc.teragrid.org,dtf.sdsc.teragrid.org,\"dtf.sdsc.teragrid\",\"SDSC DTF\",\"San Diego Supercomputer Center\",\"\",\"SDSC\"" + 
				"tacc.teragrid.org,lonestar.tacc.teragrid.org,\"lonestar.tacc.teragrid\",\"TACC Lonestar\",\"University of Texas at Austin\",\"TACC Dell PowerEdge Linux Cluster (Lonestar)\",\"TACC\"" + 
				"tacc.teragrid.org,ranger.tacc.teragrid.org,\"ranger.tacc.teragrid\",\"TACC Ranger\",\"University of Texas at Austin\",\"TACC Sun Constellation Cluster (Ranger)\",\"TACC\"" + 
				"tacc.teragrid.org,spur.tacc.teragrid.org,\"spur.tacc.teragrid\",\"TACC Spur\",\"University of Texas at Austin\",\"TACC Sun Visualization System (Spur)\",\"TACC\"" + 
				"uc.teragrid.org,dtf.uc.teragrid.org,\"dtf.anl.teragrid\",\"UC/ANL DTF\",\"Argonne National Laboratory\",\"\",\"ANL\"" + 
				"uc.teragrid.org,viz.uc.teragrid.org,\"viz.anl.teragrid\",\"UC/ANL Visualization\",\"Argonne National Laboratory\",\"UC/ANL Visualization Cluster\",\"ANL\"";
	}
}
