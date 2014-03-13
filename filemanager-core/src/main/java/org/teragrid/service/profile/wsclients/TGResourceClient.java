package org.teragrid.service.profile.wsclients;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.teragrid.service.profile.wsclients.model.ComputeDTO;
import org.teragrid.service.profile.wsclients.model.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TGResourceClient extends AbstractHttpClient 
{
	private static Logger log = Logger.getLogger(TGResourceClient.class);
	
	private Set<ComputeDTO> ctssSystems = new HashSet<ComputeDTO>();
	
	public TGResourceClient(String endpoint) {
		try {
			String results = doGet(endpoint);
			ObjectMapper mapper = new ObjectMapper();
			if (!StringUtils.isEmpty(results)) 
			{
				JsonNode jsonResults = mapper.readTree(results);
				JsonNode jsonResources = jsonResults.get("Resources");
				for (Iterator<JsonNode> iter = jsonResources.elements(); iter.hasNext();) {
			
					JsonNode jsonSystem = iter.next().get(0);
					try {
						log.debug("Processing system " + jsonSystem.get("ResourceName").asText());
						
						ComputeDTO sysDto = new ComputeDTO();
						sysDto.setSite(jsonSystem.get("SiteID").asText());
						String id = jsonSystem.get("ResourceID").asText();
						if (id.equals("teradre.purdue.teragrid.org")) {
							id = "condor.purdue.teragrid.org";
						}
						sysDto.setResourceId(id);
						
						sysDto.setName(jsonSystem.get("ResourceName").asText());
						sysDto.setTgcdbName(jsonSystem.get("resource_name").asText());
						sysDto.setType("compute");
						sysDto.setStatus(Service.UP);
						
						ctssSystems.add(sysDto);
						
					} catch (Exception e) {
						continue;
					}
				}
			}
		} catch (Exception e) {
			log.error("Failed to retrieve output from " + endpoint, e);
		} 
	}

	public Set<ComputeDTO> getResources() {
		return ctssSystems;
	}
}
