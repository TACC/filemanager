package org.teragrid.service.profile.wsclients;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.teragrid.service.profile.wsclients.model.ComputeDTO;
import org.teragrid.service.profile.wsclients.model.EnvironmentVariable;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ProfileServiceClient extends AbstractHttpClient
{
	private static Logger log = Logger.getLogger(ProfileServiceClient.class);
	
	private String endpoint;
	private String username;
	private String password;
	
	public ProfileServiceClient(String endpoint, String username, String password) {
		this.endpoint = endpoint;
		this.username = username;
		this.password = password;
	}
	
	public List<ComputeDTO> getResources() {
		
		List<ComputeDTO> resources = new ArrayList<ComputeDTO>();
		
		// Parse the response into a SystemDTO object
		try 
		{
			String results = doGet(endpoint + "/resource", username, password);
			ObjectMapper mapper = new ObjectMapper();
			if (!StringUtils.isEmpty(results)) 
			{
				JsonNode jsonEntries = mapper.readTree(results);
				
				for (int i=0; i<jsonEntries.size(); i++) {
					JsonNode jsonEntry = jsonEntries.get(i);
					ComputeDTO system = new ComputeDTO();
					system.setResourceId(clean(jsonEntry.get("resource.id").asText()));
					system.setTgcdbName(clean(jsonEntry.get("tgcdb.name").asText()));
					system.setName(clean(jsonEntry.get("resource.name").asText()));
					system.setSite(clean(jsonEntry.get("site").asText()));
					system.setLoginHostname(clean(jsonEntry.get("gsissh.host").asText()));
					system.setGridftpHostname(clean(jsonEntry.get("gridftp.host").asText()));
					system.setStatus(clean(jsonEntry.get("status").asText()));
					system.setLocalUsername(clean(jsonEntry.get("localUsername").asText()));
					system.setType(clean(jsonEntry.get("type").asText()));
					resources.add(system);
				}
			}
		} catch (Exception e) {
			log.error("Failed to retrieve output from " + endpoint + ": " + e.getMessage());
		} 
		
		return resources;
	}

	public List<EnvironmentVariable> getEnvironment(String resourceId) 
	{
		
		List<EnvironmentVariable> environment = new ArrayList<EnvironmentVariable>();
		
		// Parse the response into a list of EnvironmentVariable objects
		try {
			String results = doGet(endpoint + "/resource/resourceid/" + resourceId + "/env", username, password);  
			ObjectMapper mapper = new ObjectMapper();
			if (!StringUtils.isEmpty(results)) 
			{
				JsonNode jsonEntries = mapper.readTree(results);
				JsonNode jsonEnvironment = jsonEntries.get("environment");
				for (Iterator<String> iter = jsonEnvironment.fieldNames(); iter.hasNext();) {
					String name = iter.next();
					EnvironmentVariable variable = new EnvironmentVariable();
					variable.setName(clean(name));
					variable.setValue(jsonEnvironment.get(name).asText());
					environment.add(variable);
				}
				Collections.sort(environment);
			}
		} catch (Exception e) {
			log.error("Failed to retrieve output from " + endpoint + ": " + e.getMessage());
		} 
		
		return environment;
	}
	
	public static void main(String[] args) {
		ProfileServiceClient client = new ProfileServiceClient(
				"http://localhost:8080/web-apps/json/profile-v1", 
				"",
				"");
		
		System.out.println(client.getResources());
	}
	
	private String clean(String value) {
		if (value == null) return value;
		
		value = value.trim();
		if (value.equals("")) return null;
		if (value.equalsIgnoreCase("null")) return null;
		
		return value;
 	}
}
