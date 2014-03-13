package org.teragrid.service.profile.wsclients;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.teragrid.service.profile.wsclients.model.ComputeDTO;

public class IncaDowntimeClient extends AbstractHttpClient {
	
	private static Logger log = Logger.getLogger(IncaDowntimeClient.class);
	
	private Set<ComputeDTO> downSystems = new HashSet<ComputeDTO>();
	
	public IncaDowntimeClient(String endpoint) 
	{
		InputStream in = null;
		try 
		{
			Properties props = new Properties();
			String response = doGet(endpoint);
			if (!StringUtils.isEmpty(response)) 
			{
				in = new ByteArrayInputStream(response.getBytes("UTF-8"));
				props.load(in);
			
				// downtime list comes in the form <ResourceID>=<rss.id>
				if (!props.isEmpty()) {
					for (Object key: props.keySet()) {
						ComputeDTO systemDto = new ComputeDTO();
						systemDto.setResourceId((String)key);
						downSystems.add(systemDto);
					}
				}
			}
		} catch (Exception e) {
			log.error("Failed to retrieve down resources.",e);
		} finally {
			try { in.close(); } catch (Exception e) { log.error(e);}
		}
		
	}
	
	public Set<ComputeDTO> getResources() {
		return this.downSystems;
	}
}
