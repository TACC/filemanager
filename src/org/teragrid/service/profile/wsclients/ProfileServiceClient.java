package org.teragrid.service.profile.wsclients;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.teragrid.portal.filebrowser.applet.util.HTTPSClient;
import org.teragrid.portal.filebrowser.applet.util.LogManager;
import org.teragrid.service.profile.wsclients.model.ComputeDTO;
import org.teragrid.service.profile.wsclients.model.EnvironmentVariable;

public class ProfileServiceClient {
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
		
		HTTPSClient client = new HTTPSClient(endpoint + "/resource", username, password);  
		  
		// Parse the response into a SystemDTO object
		try {
			String results = client.getText();
			JSONArray jsonEntries = new JSONArray(results);
			for (int i=0; i<jsonEntries.length(); i++) {
				JSONObject jsonEntry = jsonEntries.getJSONObject(i);
				ComputeDTO system = new ComputeDTO();
				system.setResourceId(clean(jsonEntry.getString("resource.id")));
				system.setTgcdbName(clean(jsonEntry.getString("tgcdb.name")));
				system.setName(clean(jsonEntry.getString("resource.name")));
				system.setSite(clean(jsonEntry.getString("site")));
				system.setLoginHostname(clean(jsonEntry.getString("gsissh.host")));
				system.setGridftpHostname(clean(jsonEntry.getString("gridftp.host")));
				system.setStatus(clean(jsonEntry.getString("status")));
				system.setLocalUsername(clean(jsonEntry.getString("localUsername")));
				system.setType(clean(jsonEntry.getString("type")));
				resources.add(system);
			}
		} catch (Exception e) {
			LogManager.error("Failed to retrieve output from " + endpoint + ": " + e.getMessage());
		} 
		
		return resources;
	}

	@SuppressWarnings("unchecked")
	public List<EnvironmentVariable> getEnvironment(String resourceId) {
		
		List<EnvironmentVariable> environment = new ArrayList<EnvironmentVariable>();
		
		HTTPSClient client = new HTTPSClient(endpoint + "/resource/resourceid/" + resourceId + "/env", username, password);  
		  
		// Parse the response into a list of EnvironmentVariable objects
		try {
			String results = client.getText();
			JSONObject jsonEnv = new JSONObject(results).getJSONObject("environment");
			for (String name: JSONObject.getNames(jsonEnv)) {
				EnvironmentVariable variable = new EnvironmentVariable();
				variable.setName(clean(name));
				variable.setValue(jsonEnv.getString(name));
				environment.add(variable);
			}
			Collections.sort(environment);		
		} catch (Exception e) {
			LogManager.error("Failed to retrieve output from " + endpoint + ": " + e.getMessage());
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
