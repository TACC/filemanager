package org.teragrid.service.profile.wsclients;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.teragrid.service.profile.wsclients.model.ComputeDTO;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CtssClient extends AbstractHttpClient 
{
	private static Logger log = Logger.getLogger(CtssClient.class);
	private String endpoint;
	
	private Map<String, ComputeDTO> ctssSystems = new HashMap<String, ComputeDTO>();
	
	public CtssClient(String endpoint) 
	{
		this.endpoint = endpoint;
	}
	
	public void init()
	{
		try 
		{
			String results = doGet(endpoint);
			ObjectMapper mapper = new ObjectMapper();
			if (!StringUtils.isEmpty(results)) 
			{
				JsonNode jsonResults = mapper.readTree(results);
				JsonNode jsonResources = jsonResults.get("Resources");
				for (Iterator<JsonNode> iter = jsonResources.elements(); iter.hasNext();) 
				{
					JsonNode jsonSystem = iter.next();
					JsonNode jsonSystemNode = jsonSystem.get(jsonSystem.fieldNames().next());
					try 
					{
						log.debug("Processing system " + jsonSystemNode.get("ResourceName").asText());
						
						ComputeDTO sysDto = new ComputeDTO();
						sysDto.setSite(jsonSystemNode.get("SiteID").textValue());
						String id = jsonSystemNode.get("ResourceID").textValue();
//						if (id.equals("teradre.purdue.teragrid.org")) {
//							id = "condor.purdue.teragrid.org";
//						}
						
						sysDto.setResourceId(id);
						sysDto.setName(jsonSystemNode.get("ResourceName").asText());
						sysDto.setTgcdbName(jsonSystemNode.get("TgcdbResourceName").textValue());
						
						ctssSystems.put(sysDto.getResourceId(), sysDto);
					} 
					catch (Exception e) 
					{
						e.printStackTrace();
						continue;
					}
				}
			}
		} catch (Exception e) {
			log.error("Failed to retrieve output from " + endpoint, e);
		} 
	}

	public Map<String, ComputeDTO> getResources() {
		if (ctssSystems.isEmpty()) {
			init();
		}
		return ctssSystems;
	}
}
