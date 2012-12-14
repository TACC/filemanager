package org.teragrid.service.profile.wsclients;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.restlet.Client;
import org.restlet.data.Protocol;
import org.restlet.data.Response;
import org.restlet.resource.Representation;
import org.teragrid.portal.filebrowser.server.servlet.Settings;
import org.teragrid.service.profile.wsclients.model.ComputeDTO;

public class KitServicesClient {
	private static Logger log = Logger.getLogger(KitServicesClient.class);
	
	private Set<ComputeDTO> ctssSystems = new HashSet<ComputeDTO>();
	
	public KitServicesClient(String endpoint) {
		String gridftpUrl = Settings.KIT_SERVICES_SERVER + "type/gridftp/name/gridftp-default-server/";
		String loginUrl = Settings.KIT_SERVICES_SERVER + "type/gsi-openssh/name/gsi-openssh/";
		
		retrieveGridFTPEndpoints(gridftpUrl);
		
		retrieveGsiSshEndpoints(loginUrl);
	}
	
	private void retrieveGridFTPEndpoints(String endpoint) {
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
				JSONArray array = new JSONArray(new JSONObject(resources).getString("Kit.Services"));
				for (int i=0;i<array.length();i++) {
					
					JSONObject system = array.getJSONObject(i);
					
					log.debug("Processing system " + system.getString("ResourceID"));
					
					ComputeDTO sysDto = new ComputeDTO();
					sysDto.setSite(system.getString("SiteID"));
					String id = system.getString("ResourceID");
					if (id.equals("teradre.purdue.teragrid.org")) {
						id = "condor.purdue.teragrid.org";
//					} else if (id.equals("lonestar-westmere.tacc.teragrid.org")) {
//						id = "lonestar.tacc.teragrid.org";
					}
					sysDto.setResourceId(id);
					String host = system.getString("Endpoint");
					if (host != null) {
						host = host.substring(9);
						host = host.substring(0,host.lastIndexOf(":"));
					}
					sysDto.setGridftpHostname(host);
					
					ctssSystems.add(sysDto);
				}
//				for (String line: resources.split("\n")) { 
//					if (line.startsWith("Type")) continue;
//					log.debug("Processing line " + line);
//					String[] values = line.split(",");
//					if (!(values.length >= 7)) continue;
//					ComputeDTO sysDto = new ComputeDTO();
//					sysDto.setSite(values[5].replaceAll("\"", ""));
//					sysDto.setGridftpHostname(values[4].replaceAll("\"", ""));
//					sysDto.setResourceId(values[6].replaceAll("\"", ""));
					
					// patch for purdue misnaming their resourceID
//					if (sysDto.getResourceId().equals("tg-condor.purdue.teragrid.org")) {
//						sysDto.setResourceId("condor.purdue.teragrid.org");
//					} else if (sysDto.getResourceId().equals("fpga.purdue.teragrid.org")) {
//						sysDto.setResourceId("brutus.purdue.teragrid.org");
//					}
//					
//					ctssSystems.add(sysDto);
//				}
			}
		} catch (Exception e) {
			log.debug("Failed to retrieve output from " + endpoint,e);
		} finally {
			try { in.close(); } catch (Exception e) { log.error(e);}
		}
		
	}
	
	private void retrieveGsiSshEndpoints(String endpoint) {
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
//				for (String line: resources.split("\n")) { 
//					if (line.startsWith("Type")) continue;
////					log.debug("Processing line " + line);
//					String[] values = line.split(",");
//					if (values.length != 13) continue;
				JSONArray array = new JSONArray(new JSONObject(resources).getString("Kit.Services"));
				for (int i=0;i<array.length();i++) {
					
					JSONObject resource = array.getJSONObject(i);
					
					log.debug("Processing system " + resource.getString("ResourceID"));
					for (ComputeDTO system: ctssSystems) {
						String id = resource.getString("ResourceID");
						if (id.equals("teradre.purdue.teragrid.org")) {
							id = "condor.purdue.teragrid.org";
//						} else if (id.equals("lonestar-westmere.tacc.teragrid.org")) {
//							id = "lonestar.tacc.teragrid.org";
						}
						if (system.getResourceId().equals(id)) {
							String login = resource.getString("Endpoint");
							if (login == null) continue;
							system.setLoginHostname(login.contains(":")?login.substring(0,login.indexOf(":")):login);
						}
					}	
				}
				
			}
		} catch (Exception e) {
			log.debug("Failed to retrieve output from " + endpoint,e);
		} finally {
			try { in.close(); } catch (Exception e) { log.error(e);}
		}
		
	}

	public Set<ComputeDTO> getResources() {
		return ctssSystems;
	}

}
