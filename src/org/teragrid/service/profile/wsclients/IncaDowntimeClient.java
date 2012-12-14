package org.teragrid.service.profile.wsclients;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;
import org.restlet.Client;
import org.restlet.data.Protocol;
import org.restlet.data.Response;
import org.teragrid.service.profile.wsclients.model.ComputeDTO;

public class IncaDowntimeClient {
	
	private static Logger log = Logger.getLogger(IncaDowntimeClient.class);
	
	private Set<ComputeDTO> downSystems = new HashSet<ComputeDTO>();
	
	public IncaDowntimeClient(String endpoint) {
		Client client = new Client(Protocol.HTTP);
		Response response = client.get(endpoint);
		InputStream in = null;
		try {
			in = response.getEntity().getStream();
			
			Properties props = new Properties();
			
			if (in != null) {
				props.load(in);
			}
			
			// downtime list comes in the form <ResourceID>=<rss.id>
			if (!props.isEmpty()) {
				for (Object key: props.keySet()) {
					ComputeDTO systemDto = new ComputeDTO();
					systemDto.setResourceId((String)key);
					downSystems.add(systemDto);
				}
			}
		} catch (IOException e) {
			log.error("Failed to retrieve down resources.",e);
		} finally {
			try { in.close(); } catch (Exception e) { log.error(e);}
		}
		
	}
	
	public Set<ComputeDTO> getResources() {
		return this.downSystems;
	}
}
