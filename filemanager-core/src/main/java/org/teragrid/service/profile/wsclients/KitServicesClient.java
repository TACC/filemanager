package org.teragrid.service.profile.wsclients;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.teragrid.service.profile.wsclients.model.ComputeDTO;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class KitServicesClient extends AbstractHttpClient 
{
	private static Logger log = Logger.getLogger(KitServicesClient.class);
	
	private Set<ComputeDTO> ctssSystems = new HashSet<ComputeDTO>();
	
	public KitServicesClient(String endpoint) {
		String gridftpUrl = "http://info.xsede.org/web-apps/json/kit-services-v1/type/gridftp/name/gridftp-default-server/";
		String loginUrl = "http://info.xsede.org/web-apps/json/kit-services-v1/type/gsi-openssh/name/gsi-openssh/";
		
		retrieveGridFTPEndpoints(gridftpUrl);
		
		retrieveGsiSshEndpoints(loginUrl);
	}
	
	private void retrieveGridFTPEndpoints(String endpoint) {
		try 
		{
			String response = doGet(endpoint);
			ObjectMapper mapper = new ObjectMapper();
			if (!StringUtils.isEmpty(response)) 
			{
				JsonNode json = mapper.readTree(response);
				
				if (json.has("Kit.Services"))
				{
					JsonNode array = json.get("Kit.Services");
					
					for (int i=0;i<array.size(); i++) 
					{
						JsonNode system = array.get(i);
						
						log.debug("Processing system " + system.get("ResourceID").asText());
						
						ComputeDTO sysDto = new ComputeDTO();
						sysDto.setSite(system.get("SiteID").asText());
						String id = system.get("ResourceID").asText();
						if (id.equals("teradre.purdue.teragrid.org")) {
							id = "condor.purdue.teragrid.org";
						}
						sysDto.setResourceId(id);
						String host = system.get("Endpoint").asText();
						if (host != null) {
							host = host.substring(9);
							host = host.substring(0,host.lastIndexOf(":"));
						}
						sysDto.setGridftpHostname(host);
						
						ctssSystems.add(sysDto);
					}
				}
			}
		} catch (Exception e) {
			log.debug("Failed to retrieve output from " + endpoint,e);
		} 
		
	}
	
	private void retrieveGsiSshEndpoints(String endpoint) 
	{
		try 
		{
			String response = doGet(endpoint);
			
			ObjectMapper mapper = new ObjectMapper();
			if (!StringUtils.isEmpty(response)) 
			{
				JsonNode json = mapper.readTree(response);
				
				if (json.has("Kit.Services"))
				{
					JsonNode array = json.get("Kit.Services");
					
					for (int i=0;i<array.size(); i++) 
					{
						JsonNode resource = array.get(i);
					
						log.debug("Processing system " + resource.get("ResourceID").asText());
						for (ComputeDTO system: ctssSystems) {
							String id = resource.get("ResourceID").asText();
							if (id.equals("teradre.purdue.teragrid.org")) {
								id = "condor.purdue.teragrid.org";
							}
							if (system.getResourceId().equals(id)) {
								String login = resource.get("Endpoint").asText();
								if (login == null) continue;
								system.setLoginHostname(login.contains(":")?login.substring(0,login.indexOf(":")):login);
							}
						}	
					}
				}
			}
		} catch (Exception e) {
			log.debug("Failed to retrieve output from " + endpoint,e);
		}
		
	}

	public Set<ComputeDTO> getResources() {
		return ctssSystems;
	}

}
