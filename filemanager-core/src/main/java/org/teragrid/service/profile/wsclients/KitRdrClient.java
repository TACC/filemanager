package org.teragrid.service.profile.wsclients;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.teragrid.service.profile.wsclients.model.ComputeDTO;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.utexas.tacc.wcs.filemanager.common.model.enumeration.SystemType;

public class KitRdrClient extends AbstractHttpClient 
{
	private static Logger log = Logger.getLogger(KitRdrClient.class);
	private String endpoint;
	
	private Map<String, ComputeDTO> rdrSystems = new HashMap<String, ComputeDTO>();
	
	public KitRdrClient(String endpoint) 
	{
		this.endpoint = endpoint;
	}
	
	public void init()
	{
		try 
		{
			String results = doGet(endpoint);
			ObjectMapper mapper = new ObjectMapper();
			List<String> decommissionedResourceIds = new ArrayList<String>();
			if (!StringUtils.isEmpty(results)) 
			{
				JsonNode jsonResults = mapper.readTree(results);
				JsonNode rootResource = jsonResults.get("RDR_Resources");
				JsonNode jsonResources = rootResource.get("RDR_Resource");
				for (Iterator<JsonNode> iter = jsonResources.elements(); iter.hasNext();) 
				{
					JsonNode jsonSystemNode = iter.next();
					try {
						ComputeDTO sysDto = null;
						String resourceId = jsonSystemNode.get("ResourceID").textValue();
						if (rdrSystems.containsKey(resourceId)) {
							sysDto = rdrSystems.get(resourceId);
						} else {
							sysDto = new ComputeDTO();
							sysDto.setResourceId(resourceId);
							sysDto.setSite(jsonSystemNode.get("SiteID").textValue());
						}
						
						if (jsonSystemNode.has("Resource"))
						{
							JsonNode jsonSystemResourceNode = jsonSystemNode.get("Resource");
							String type = jsonSystemResourceNode.get("ResourceType").textValue();
							if (StringUtils.equalsIgnoreCase(type,"storage")) {
								type = SystemType.ARCHIVE.name();
							} else {
								type = SystemType.HPC.name();
							}
							sysDto.setType(type);
						
							for (Iterator<JsonNode> statusIter = jsonSystemResourceNode.get("ResourceStatus").elements(); statusIter.hasNext();) {
								JsonNode jsonStatusNode = statusIter.next();
								// if decomission start date has passed
								if (StringUtils.equalsIgnoreCase(jsonStatusNode.get("ResourceStatusType").textValue(), "Decommissioned"))
								{
									String startDate = jsonStatusNode.get("StartDate").textValue();
									if (!StringUtils.isEmpty(startDate)) 
									{
										DateTime decomissionDate = DateTime.parse(startDate, DateTimeFormat.forPattern("yyyy-MM-dd"));
										if (decomissionDate.isBeforeNow()) {
//											System.out.println("Removing system: " + resourceId + " decommissioned on " + decomissionDate.toString());
											decommissionedResourceIds.add(resourceId);
											break;
										}
									}
								}
							}
							
							if (StringUtils.isEmpty(sysDto.getName())) {
								String name = jsonSystemResourceNode.get("DescriptiveName").textValue();
								if (name.indexOf("(") > -1) {
									name = name.substring(name.indexOf("(") + 1);
								}
								if (name.indexOf(")") > -1) {
									name = name.substring(0, name.indexOf(")"));
								}
								sysDto.setName(name);
							}
						}
						else if (jsonSystemNode.has("ComputeResource"))
						{
							JsonNode jsonSystemComputeResourceNode = jsonSystemNode.get("ComputeResource");
							if (jsonSystemComputeResourceNode.get("HasVisualization").asBoolean()) {
								sysDto.setType(SystemType.VIZ.name());
							}
							
							sysDto.setName(jsonSystemComputeResourceNode.get("Nickname").textValue());
						}
						
						rdrSystems.put(resourceId, sysDto);
					} 
					catch (Exception e) 
					{
						e.printStackTrace();
						continue;
					}
				}
//				System.out.println("Removing systems: " + StringUtils.join(decommissionedResourceIds.toArray(), ", "));
				for (String decommissionedResourceId: decommissionedResourceIds) {
					rdrSystems.remove(decommissionedResourceId);
				}
			}
		} catch (Exception e) {
			log.error("Failed to retrieve output from " + endpoint, e);
		} 
	}

	public Map<String, ComputeDTO> getResources() {
		if (rdrSystems.isEmpty()) {
			init();
		}
		return rdrSystems;
	}
}
