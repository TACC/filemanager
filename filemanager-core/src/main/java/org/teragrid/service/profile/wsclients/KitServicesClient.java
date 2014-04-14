package org.teragrid.service.profile.wsclients;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.teragrid.service.profile.wsclients.model.ComputeDTO;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.utexas.tacc.wcs.filemanager.common.model.enumeration.FileProtocolType;

public class KitServicesClient extends AbstractHttpClient 
{
	private static Logger log = Logger.getLogger(KitServicesClient.class);
	private String endpoint;
	
	private Map<String, ComputeDTO> ctssSystems = new HashMap<String, ComputeDTO>();
	
	public KitServicesClient(String endpoint) 
	{
		this.endpoint = endpoint;
	}
	
	private void init() 
	{
		retrieveGridFTPEndpoints(endpoint + "type/gridftp/name/gridftp-default-server/");
		retrieveGsiSshEndpoints(endpoint + "type/gsi-openssh");
	}
	
	private void retrieveGridFTPEndpoints(String endpoint) 
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
						JsonNode system = array.get(i);
						
						String resourceID = system.get("ResourceID").asText();
						log.debug("Processing system " + resourceID);
						if (!StringUtils.isEmpty(resourceID))
						{	
							ComputeDTO sysDto = new ComputeDTO();
							sysDto.setSite(system.get("SiteID").asText());
						
							sysDto.setResourceId(resourceID);
							String host = system.get("Endpoint").asText();
							try
							{
								URI uri = URI.create(host);
								sysDto.setGridftpHostname(uri.getHost());
								sysDto.setGridftpPort(uri.getPort() == -1 ? FileProtocolType.GRIDFTP.getDefaultPort() : uri.getPort());
							}
							catch (Exception e) {
								log.error("Failed to parse gridftp url for " + resourceID + " => " + host, e);
								continue;
							}
							
							ctssSystems.put(sysDto.getResourceId(), sysDto);
						}	
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
						String resourceID = resource.get("ResourceID").asText();
						log.debug("Processing system " + resourceID);
						
						if (!StringUtils.isEmpty(resourceID))
						{
							ComputeDTO system = ctssSystems.get(resourceID);
							if (system != null)
							{
								String loginEndpoint = resource.get("Endpoint").textValue();
								if (!StringUtils.isEmpty(loginEndpoint))
								{
									if (loginEndpoint.contains(":")) {
										String[] tokens = loginEndpoint.split(":");
										system.setLoginHostname(tokens[0]);
										system.setLoginPort(Integer.valueOf(tokens[1]));
									} 
									else
									{
										system.setLoginHostname(loginEndpoint);
										system.setLoginPort(FileProtocolType.SFTP.getDefaultPort());
									}
									ctssSystems.put(resourceID, system);
								}	
							}
						}	
					}
				}
			}
		} catch (Exception e) {
			log.debug("Failed to retrieve output from " + endpoint,e);
		}
		
	}

	public Map<String, ComputeDTO> getResources() {
		if (ctssSystems.isEmpty()) {
			init();
		}
		return ctssSystems;
	}

}
