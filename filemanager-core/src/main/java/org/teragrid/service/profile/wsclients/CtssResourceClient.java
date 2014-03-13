package org.teragrid.service.profile.wsclients;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.teragrid.service.profile.wsclients.model.ComputeDTO;
import org.teragrid.service.profile.wsclients.model.Service;

public class CtssResourceClient extends AbstractHttpClient 
{
	private static Logger log = Logger.getLogger(CtssResourceClient.class);
	
	private Set<ComputeDTO> ctssSystems = new HashSet<ComputeDTO>();
	
	public CtssResourceClient(String endpoint) {
		InputStream in = null;
		try 
		{
			String resources = doGet(endpoint);
			
			if (resources != null && !resources.equals("")) {
				for (String line: resources.split("\n")) { 
					if (line.startsWith("Site")) continue;
					log.debug("Processing line " + line);
					String[] values = line.split(",");
					if (values.length == 1) continue;
					ComputeDTO sysDto = new ComputeDTO();
					sysDto.setSite(values[0].replaceAll("\"", ""));
					sysDto.setResourceId(values[1].replaceAll("\"", ""));
					sysDto.setTgcdbName(values[2].replaceAll("\"", ""));
					sysDto.setName(values[3].replaceAll("\"","").equals("")?values[1]:values[3].replaceAll("\"", ""));
					sysDto.setType("compute");
					sysDto.setStatus(Service.UP);
	//				sysDto.setGridftpHostname(values[4].replaceAll("\"", ""));
					
					ctssSystems.add(sysDto);
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
