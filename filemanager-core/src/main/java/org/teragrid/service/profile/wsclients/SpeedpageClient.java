package org.teragrid.service.profile.wsclients;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.teragrid.service.profile.wsclients.model.SpeedpageEntry;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SpeedpageClient extends AbstractHttpClient 
{
	private static Logger log = Logger.getLogger(SpeedpageClient.class);
	
	private String endpoint;
	
	public SpeedpageClient(String endpoint) {
		this.endpoint = endpoint;
		
	}

	public List<SpeedpageEntry> getAllSpeedpageEntries() {
		return retrieveMeasurements(endpoint + "/range/today/");
	}
	
	public float getTransferRate(String sourceId, String destId) {
		List<SpeedpageEntry> entries = retrieveMeasurements(
				endpoint + "/source/" + sourceId + "/dest/" + destId + "/range/today");
		
		for (SpeedpageEntry entry: entries) {
			if (entry.getDestinationId().equalsIgnoreCase(destId)) {
				if (entry.getTransferRate() > 0) {
					return entry.getTransferRate();
				}
			}
		}
		
		return -1;
	}
	
	private List<SpeedpageEntry> retrieveMeasurements(String url) {
		// parse csv output from ctss-resource-v1 service
		List<SpeedpageEntry> entries = new ArrayList<SpeedpageEntry>();
		try {
			String results = doGet(url);
			ObjectMapper mapper = new ObjectMapper();
			if (!StringUtils.isEmpty(results)) 
			{
				JsonNode json = mapper.readTree(results);
				JsonNode jsonEntries = json.get("Speedpage");
				for (Iterator<JsonNode> iter = jsonEntries.elements(); iter.hasNext();) {
					JsonNode jsonEntry = iter.next();
					SpeedpageEntry entry = new SpeedpageEntry();
					entry.setSource(jsonEntry.get("source").asText());
					entry.setSourceUrl(jsonEntry.get("src_url").asText());
					entry.setSourceId(jsonEntry.get("sourceid").asText());
					entry.setDestination(jsonEntry.get("dest").asText());
					entry.setDestinationUrl(jsonEntry.get("dest_url").asText());
					entry.setDestinationId(jsonEntry.get("destid").asText());
					entry.setTimestamp(jsonEntry.get("tstamp").asLong());
					entry.setTransferRate(Float.parseFloat(jsonEntry.get("xfer_rate").asText()));
					entries.add(entry);
				}
			}
		} catch (Exception e) {
			log.error("Failed to retrieve output from " + endpoint,e);
		} 
		
		return entries;
	}
}
