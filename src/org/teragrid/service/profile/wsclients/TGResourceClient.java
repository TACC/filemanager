package org.teragrid.service.profile.wsclients;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.Client;
import org.restlet.data.Protocol;
import org.restlet.data.Response;
import org.restlet.resource.Representation;
import org.teragrid.service.profile.wsclients.model.ComputeDTO;
import org.teragrid.service.profile.wsclients.model.Service;

public class TGResourceClient {
	private static Logger log = Logger.getLogger(TGResourceClient.class);
	
	private Set<ComputeDTO> ctssSystems = new HashSet<ComputeDTO>();
	
	public TGResourceClient(String endpoint) {
		// parse csv output from ctss-resource-v1 service
		Client client = new Client(Protocol.HTTP);  
		Response response = client.get(endpoint);  
		  
		// Parse the response into a SystemDTO object
		Representation output = response.getEntity(); 
		
		InputStream in = null;
		try {
			in = output.getStream();
			byte[] b = new byte[1024];
			String resources = "";
			while((in.read(b)) > -1) {
				resources += new String(b);
			}
			if (resources != null && !resources.equals("")) {
//				resources = resources.replaceAll("\\{\n", ": {\n");
				System.out.println("");
				JSONArray array = new JSONArray(new JSONObject(resources).getString("Resources"));
				for (int i=0;i<array.length();i++) {
					
					JSONObject object = array.getJSONObject(i);
					JSONObject system = object.getJSONObject(JSONObject.getNames(object)[0]);
					try {
						system.getString("ResourceName"); 
						system.getString("resource_name"); 
						log.debug("Processing system " + system.getString("ResourceName"));
						
						ComputeDTO sysDto = new ComputeDTO();
						sysDto.setSite(system.getString("SiteID"));
						String id = system.getString("ResourceID");
						if (id.equals("teradre.purdue.teragrid.org")) {
							id = "condor.purdue.teragrid.org";
						}
						sysDto.setResourceId(id);
						
						sysDto.setName(system.getString("ResourceName"));
						sysDto.setTgcdbName(system.getString("resource_name"));
						sysDto.setType("compute");
						sysDto.setStatus(Service.UP);
						
						ctssSystems.add(sysDto);
						
					} catch (JSONException e) {
						continue;
					}
					
					
				}
//				for (String line: resources.split("\n")) { 
//					if (line.startsWith("Site")) continue;
////					
//					String[] values = line.split(",");
//					if (values.length == 1) continue;
//					if (values[8].startsWith("\"") && !values[8].contains(".teragrid")) continue;
//					if (values[2].replaceAll("\"", "").equals("")) continue;
//					log.debug("Processing line " + line);
//					ComputeDTO sysDto = new ComputeDTO();
//					sysDto.setSite(values[0].replaceAll("\"", ""));
//					sysDto.setResourceId(values[1].replaceAll("\"", ""));
//					
//					sysDto.setName(values[2].replaceAll("\"",""));
//					sysDto.setTgcdbName(values[8].replaceAll("\"", ""));
//					sysDto.setType("compute");
//					sysDto.setStatus(Service.UP);
////					sysDto.setType("compute");
//	//				sysDto.setGridftpHostname(values[4].replaceAll("\"", ""));
//					
////					if (!ServletUtil.isValid(sysDto.getName())) continue;
//					
//					ctssSystems.add(sysDto);
//				}
			}
		} catch (Exception e) {
			log.debug("Failed to retrieve output from " + endpoint,e);
			e.printStackTrace();
		} finally {
			try { in.close(); } catch (Exception e) { log.error(e);}
		}
		
	}

	public Set<ComputeDTO> getResources() {
		return ctssSystems;
	}
}
