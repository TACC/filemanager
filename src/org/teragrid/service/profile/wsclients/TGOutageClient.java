package org.teragrid.service.profile.wsclients;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.restlet.Client;
import org.restlet.data.Protocol;
import org.restlet.data.Response;
import org.restlet.resource.Representation;
import org.teragrid.service.profile.wsclients.model.ComputeDTO;
import org.teragrid.service.profile.wsclients.model.Service;

public class TGOutageClient {
	private static Logger log = Logger.getLogger(TGOutageClient.class);
	
	private Set<ComputeDTO> downSystems = new HashSet<ComputeDTO>();
	
	public TGOutageClient(String endpoint) {
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
				for (String line: resources.split("\n")) { 
					if (line.startsWith("Site")) continue;
					
					String[] values = line.split(",");
					if (values.length == 1) continue;
					if (!values[8].contains(".teragrid")) continue;
					if (values[2].replaceAll("\"", "").equals("")) continue;
					log.debug("Processing line " + line);
					ComputeDTO sysDto = new ComputeDTO();
					sysDto.setSite(values[0].replaceAll("\"", ""));
					sysDto.setResourceId(values[1].replaceAll("\"", ""));
					sysDto.setStatus(Service.DOWN);

					downSystems.add(sysDto);
				}
			}
		} catch (Exception e) {
			log.debug("Failed to retrieve output from " + endpoint,e);
		} finally {
			try { in.close(); } catch (Exception e) { log.error(e);}
		}
		
	}

	public Set<ComputeDTO> getResources() {
		return downSystems;
	}
}
