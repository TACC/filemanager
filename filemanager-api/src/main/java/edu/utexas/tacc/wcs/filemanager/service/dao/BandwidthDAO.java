/* 
 * Created on March 27, 2009
 * 
 * Developed by: Rion Dooley - dooley [at] tacc [dot] utexas [dot] edu
 * 				 TACC, Texas Advanced Computing Center
 * 
 * https://www.tacc.utexas.edu/
 */

package edu.utexas.tacc.wcs.filemanager.service.dao;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.utexas.tacc.wcs.filemanager.common.model.BandwidthMeasurement;

/**
 * Simple utility for finding today's measured bandwith between two resources through 
 * scraping of the PSC Speedpage.
 * 
 * @author Rion Dooley < dooley [at] tacc [dot] utexas [dot] edu >
 * 
 * 
 * web-apps/resource-monitor-v1/bandwith : returns all to all measurements
 * web-apps/resource-monitor-v1/bandwith/resource_id/$ResourceID : returns all measurements from $ResourceId to everywhere else
 * web-apps/resource-monitor-v1/bandwith/tgcdb_id/$tgcdb_id : returns all measurements from $tgcdb to everywhere else
 * 
 */
public class BandwidthDAO {
	
	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(BandwidthDAO.class);
	
    private String endpoint;
    
    public BandwidthDAO(String endpoint) {
    	this.endpoint = endpoint;
    }
    
    public BandwidthMeasurement getMeasuredBandwidth(String source, String dest) 
    throws Exception 
    {
//    	String src = getSpeedpageResourceName(source);
//    	String dst = getSpeedpageResourceName(dest);
    	String url = String.format("%s/source/%s/destination/%s/range/today", endpoint, source, dest);
    	
    	JsonNode measurement = getMeasurement(url);
    	return new BandwidthMeasurement(source, 
    									dest, 
    									new Date(measurement.get("tstamp").asInt()), 
    									measurement.get("xfer_rate").asDouble());
    }
    
    public static String getSpeedpageResourceName(String name) throws IOException
    {
    	if (name.equalsIgnoreCase("bigred.iu.teragrid.org")) return "IU BigRed";
    	if (name.equalsIgnoreCase("queenbee.loni-lsu.teragrid.org")) return "LONI Queenbee";
    	if (name.equalsIgnoreCase("frost.ncar.teragrid.org")) return "NCAR frost";
    	if (name.equalsIgnoreCase("abe.ncsa.teragrid.org")) return "NCSA Abe";
    	if (name.equalsIgnoreCase("cobalt.ncsa.teragrid.org")) return "NCSA Cobalt Altix";
//    	if (name.equalsIgnoreCase("lincoln.ncsa.teragrid.org")) return "NCSA Lincoln";
    	if (name.equalsIgnoreCase("kraken.nics.teragrid.org")) return "NICS Kraken Cray XT5";
    	if (name.equalsIgnoreCase("tungsten.ncsa.teragrid.org")) return "NCSA Tungsten";
    	if (name.equalsIgnoreCase("nstg.ornl.teragrid.org")) return "ORNL Neutron Science TeraGrid Gateway";
    	if (name.equalsIgnoreCase("bigben.psc.teragrid.org")) return "PSC Bigben";
    	if (name.equalsIgnoreCase("pople.psc.teragrid.org")) return "PSC Pople";
    	if (name.equalsIgnoreCase("rachel.psc.teragrid.org")) return "PSC Rachel";
    	if (name.equalsIgnoreCase("steele.purdue.teragrid.org")) return "Purdue Steele";
    	if (name.equalsIgnoreCase("condor.purdue.teragrid.org")) return "Purdue Condor";
    	if (name.equalsIgnoreCase("dtf.sdsc.teragrid.org")) return "SDSC DTF";
    	if (name.equalsIgnoreCase("frost.ncar.teragrid.org")) return "frost.ncar.teragrid.org";
    	if (name.equalsIgnoreCase("dtf.ncsa.teragrid.org")) return "dtf.ncsa.teragrid.org";
    	if (name.equalsIgnoreCase("lonestar.tacc.teragrid.org")) return "TACC Lonestar";
    	if (name.equalsIgnoreCase("lonestar4.tacc.teragrid.org")) return "TACC Lonestar";
    	if (name.equalsIgnoreCase("maverick.tacc.teragrid.org")) return "TACC Maverick";
    	if (name.equalsIgnoreCase("stampede.tacc.teragrid.org")) return "TACC Maverick";
    	if (name.equalsIgnoreCase("spur.tacc.teragrid.org")) return "TACC Spur";
    	if (name.equalsIgnoreCase("ranger.tacc.teragrid.org")) return "TACC Ranger";
    	if (name.equalsIgnoreCase("stampede.tacc.xsede.org")) return "TACC Stampede";
    	if (name.equalsIgnoreCase("dtf.uc.teragrid.org")) return "UC/ANL DTF";
    	if (name.equalsIgnoreCase("dtf.sdsc.teragrid.org")) return "SDSC DTF";
    	
    	if (name.equalsIgnoreCase("Big Red")) return "IU BigRed";
    	if (name.equalsIgnoreCase("Queenbee")) return "LONI Queenbee";
    	if (name.equalsIgnoreCase("Frost")) return "frost.ncar.teragrid.org";
    	if (name.equalsIgnoreCase("Abe")) return "NCSA Abe";
//    	if (name.equalsIgnoreCase("Lincoln")) return "NCSA Lincoln";
    	if (name.equalsIgnoreCase("Cobalt")) return "NCSA Cobalt Altix";
    	if (name.equalsIgnoreCase("Kraken")) return "NICS Kraken Cray XT5";
    	if (name.equalsIgnoreCase("Tungsten")) return "NCSA Tungsten";
    	if (name.equalsIgnoreCase("NSTG")) return "ORNL Neutron Science TeraGrid Gateway";
    	if (name.equalsIgnoreCase("BigBen")) return "PSC Bigben";
    	if (name.equalsIgnoreCase("Pople")) return "PSC Pople";
    	if (name.equalsIgnoreCase("Rachel")) return "PSC Rachel";
    	if (name.equalsIgnoreCase("Steele")) return "Purdue Steele";
    	if (name.equalsIgnoreCase("NCSA TeraGrid Cluster")) return "dtf.ncsa.teragrid.org";
    	if (name.equalsIgnoreCase("SDSC TeraGrid Cluster")) return "SDSC DTF";
    	if (name.equalsIgnoreCase("Frost")) return "frost.ncar.teragrid.org";
    	if (name.equalsIgnoreCase("Lonestar")) return "TACC Lonestar";
    	if (name.equalsIgnoreCase("Maverick")) return "TACC Maverick";
    	if (name.equalsIgnoreCase("Ranger")) return "TACC Ranger";
    	if (name.equalsIgnoreCase("Ranch")) return "TACC Ranch";
    	if (name.equalsIgnoreCase("Stampede")) return "TACC Stampede";
    	if (name.equalsIgnoreCase("UC/ANL TeraGrid Cluster")) return "UC/ANL DTF";
           
    	throw new IOException("Failed to map resource " + name);
    }
    
    /**
     * Returns the content at the given url as a string. 
     * 
     * @param url
     * @return
     * @throws IOException
     */
    public static JsonNode getMeasurement(String url)
    {	
    	ObjectMapper mapper = new ObjectMapper();
        InputStream in = null;
    	URLConnection conn = null;
    	try {
	    	// get page
	        conn = new URL(url).openConnection();
	        in = new BufferedInputStream( conn.getInputStream ());
	        JsonNode json = mapper.readTree(in);
	        if (json.has("Speedpage") && json.get("Speedpage").size() != 0)
	        {
	        	return json.get("Speedpage").get(json.get("Speedpage").size() - 1);
	        }
	        else
	        {
	        	return mapper.createObjectNode().put("xfer_rate", "0").put("tstamp", String.valueOf(new Date().getTime()));
	        }
    	} 
    	catch (Exception e) {
    		return mapper.createObjectNode().put("xfer_rate", "0").put("tstamp", String.valueOf(new Date().getTime()));
    	}
    	finally {
    		try { in.close(); } catch (Exception e) {}
    	}
    }
}