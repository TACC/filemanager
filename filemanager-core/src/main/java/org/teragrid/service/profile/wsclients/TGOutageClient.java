package org.teragrid.service.profile.wsclients;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.teragrid.service.profile.wsclients.model.ComputeDTO;
import org.teragrid.service.profile.wsclients.model.Service;

public class TGOutageClient extends AbstractHttpClient 
{
	private static Logger log = Logger.getLogger(TGOutageClient.class);
	private String endpoint;
	private Map<String, ComputeDTO> downSystems = new HashMap<String, ComputeDTO>();
	
	public TGOutageClient(String endpoint) 
	{
		this.endpoint = endpoint;
	}
	
	private void init()
	{
		try {
			String resources = doGet(endpoint);
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

					downSystems.put(sysDto.getResourceId(), sysDto);
				}
			}
		} catch (Exception e) {
			log.debug("Failed to retrieve output from " + endpoint,e);
		} 
	}

	public Map<String, ComputeDTO> getResources() {
		if (downSystems.isEmpty()) {
			init();
		}
		return downSystems;
	}
}
