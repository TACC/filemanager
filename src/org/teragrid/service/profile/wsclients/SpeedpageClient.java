package org.teragrid.service.profile.wsclients;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.restlet.Client;
import org.restlet.data.Protocol;
import org.restlet.data.Response;
import org.restlet.resource.Representation;
import org.teragrid.portal.filebrowser.applet.util.LogManager;
import org.teragrid.service.profile.wsclients.model.SpeedpageEntry;

public class SpeedpageClient {
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
		Client client = new Client(Protocol.HTTP);  
		Response response = client.get(url);  
		  
		// Parse the response into a SystemDTO object
		Representation output = response.getEntity(); 
		InputStream in = null;
		try {
			in = output.getStream();
			byte[] b = new byte[1024];
			String results = "";
			while((in.read(b)) > -1) {
				results += new String(b);
			}
			JSONObject json = new JSONObject(results);
			JSONArray jsonEntries = json.getJSONArray("Speedpage");
			for (int i=0; i<jsonEntries.length(); i++) {
				JSONObject jsonEntry = jsonEntries.getJSONObject(i);
				SpeedpageEntry entry = new SpeedpageEntry();
				entry.setSource(jsonEntry.getString("source"));
				entry.setSourceUrl(jsonEntry.getString("src_url"));
				entry.setSourceId(jsonEntry.getString("sourceid"));
				entry.setDestination(jsonEntry.getString("dest"));
				entry.setDestinationUrl(jsonEntry.getString("dest_url"));
				entry.setDestinationId(jsonEntry.getString("destid"));
				entry.setTimestamp(jsonEntry.getLong("tstamp"));
				entry.setTransferRate(Float.parseFloat(jsonEntry.getString("xfer_rate")));
				entries.add(entry);
			}
		} catch (Exception e) {
			LogManager.error("Failed to retrieve output from " + endpoint,e);
		} finally {
			try { in.close(); } catch (Exception e) { log.error(e);}
		}
		
		return entries;
	}
}
