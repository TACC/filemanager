/* 
 * Created on March 27, 2009
 * 
 * Developed by: Rion Dooley - dooley [at] tacc [dot] utexas [dot] edu
 * 				 TACC, Texas Advanced Computing Center
 * 
 * https://www.tacc.utexas.edu/
 */

package org.teragrid.portal.filebrowser.server.servlet.model.resource;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

/**
 * Simple utility for finding today's measured bandwith between two resources through 
 * scraping of the PSC Speedpage.
 * 
 * @author Rion Dooley < dooley [at] tacc [dot] utexas [dot] edu >
 * 
 * TODO: turn into restful service:
 * web-apps/resource-monitor-v1/bandwith : returns all to all measurements
 * web-apps/resource-monitor-v1/bandwith/resource_id/$ResourceID : returns all measurements from $ResourceId to everywhere else
 * web-apps/resource-monitor-v1/bandwith/tgcdb_id/$tgcdb_id : returns all measurements from $tgcdb to everywhere else
 * 
 */
public class Speedpage {
	
	private static final Logger logger = Logger.getLogger(Speedpage.class);
	
    private static Map<String,List<BandwidthMeasurement>> measurementCache = 
    	new HashMap<String,List<BandwidthMeasurement>>();
    
    private String endpoint;
    
    public Speedpage(String endpoint) {
    	this.endpoint = endpoint;
    }
    
    public BandwidthMeasurement getMeasuredBandwidth(String source, String dest) throws Exception {
    	String src = getSpeedpageResourceName(source);
    	String dst = getSpeedpageResourceName(dest);
    	
        if (measurementCache.containsKey(src)
        		&& !measurementCache.isEmpty()) {
        	if (!measurementCache.get(src).contains(dst)) {
        		cacheMeasurements(src);
        	}
        } else { 
        	cacheMeasurements(src);
        }
        List<BandwidthMeasurement> measurements = measurementCache.get(src);
        for (BandwidthMeasurement bm: measurements) {
        	if (bm.getToHostname().equals(dst)) {
        		return bm;
        	}
        }
        return null;
    }
    
    public List<BandwidthMeasurement> getMeasuredBandwidthsFromSite(TeraGridSystem source) throws Exception {
    	String src = getSpeedpageResourceName(source.getResourceName());
    	
        if (!measurementCache.containsKey(src) 
        		|| measurementCache.isEmpty()) {
        	cacheMeasurements(src);
        }
        
        return measurementCache.get(src);
        
    }
    
    public Map<String,List<BandwidthMeasurement>> getAllMeasuredBandwidths() throws Exception {
    	
    	if (measurementCache.isEmpty()){
    		cacheAllMeasurements();
    	}
    	
    	return measurementCache;
    }
    
    public static String getSpeedpageResourceName(String name) throws IOException{
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
    	if (name.equalsIgnoreCase("maverick.tacc.teragrid.org")) return "TACC Maverick";
    	if (name.equalsIgnoreCase("spur.tacc.teragrid.org")) return "TACC Spur";
    	if (name.equalsIgnoreCase("ranger.tacc.teragrid.org")) return "TACC Ranger";
    	if (name.equalsIgnoreCase("ranch.tacc.teragrid.org")) return "TACC Ranch";
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
    	if (name.equalsIgnoreCase("UC/ANL TeraGrid Cluster")) return "UC/ANL DTF";
           
    	throw new IOException("Failed to map resource " + name);
    }
    
    /**
     * Query speedpage for all bandwidth measurement from the source resource. 
     * 
     * @param source
     * @param dest
     * @throws Exception
     */
    private void cacheMeasurements(String source) throws Exception {
    	
    	String date = new SimpleDateFormat("MM/dd/yyyy").format(new Date());
        
        String arguments = "?list=" + source.replaceAll(" ", "+") + "&" +
            "begin=" + date;
        
        URL url = new URL(endpoint + arguments);
        
    	SpeedpageParser parser = new SpeedpageParser(getWebpage(url));
    	
    	measurementCache.put(source, parser.getBandwidthMeasurementsAtResource(source));
    	
    	logger.debug("Key entered was " + measurementCache.keySet().iterator().next());
    }
    
    /**
     * Query speedpage for all to all bandwidth measurements.
     * 
     * @throws Exception
     */
    private void cacheAllMeasurements() throws Exception {
    	String date = new SimpleDateFormat("MM/dd/yyyy").format(new Date());
        
        String arguments = "?begin="+date;
        	
    	URL url = new URL(endpoint + arguments);
        
    	SpeedpageParser parser = new SpeedpageParser(getWebpage(url));
    	
    	measurementCache.clear();
    	
    	measurementCache.putAll(parser.getAllBandwidthMeasurements());
    	
    }
    
    /**
     * Returns the content at the given url as a string. 
     * 
     * @param url
     * @return
     * @throws IOException
     */
    public static InputStream getWebpage(URL url) throws IOException {
    	
    	// get page
        URLConnection conn = url.openConnection();
        return new DataInputStream ( conn.getInputStream (  )  ) ;
    }
    
    public static void main(String[] args) {
        try {
        	Speedpage speedpage = new Speedpage("http://quipu.psc.teragrid.org/speedpage/www/speedpage.php");
        	
//        	for(String key: speedpage.getAllMeasuredBandwidths().keySet()) {
//        		for(BandwidthMeasurement bm: speedpage.getAllMeasuredBandwidths().get(key)) {
//        			System.out.println(bm.toCsv());
//        		}
//        	}
        	
//        	TeraGridSystem source = new TeraGridSystem();
//        	source.setResourceName("bigred.iu.teragrid.org");
//        	TeraGridSystem dest = new TeraGridSystem();
//        	dest.setResourceName("dtf.sdsc.teragrid.org");
        	
			System.out.println("Bandwidth is: " + speedpage.getMeasuredBandwidth("bigred.iu.teragrid.org","dtf.sdsc.teragrid.org").getMeasurement());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Failed to retrieve measurement.",e);
		}
    }

}

class SpeedpageParser {
	private static final Logger logger = Logger.getLogger(SpeedpageParser.class);
	
    private Map<String,List<BandwidthMeasurement>> measurements = new HashMap<String,List<BandwidthMeasurement>>();
    private Document doc;
    
    @SuppressWarnings("unchecked")
	public SpeedpageParser(InputStream in) throws Exception {
        doc = new SAXBuilder(true).build(in);
        
        Element root = doc.getRootElement();
        Element body = root.getChild("body",root.getNamespace());
        Element table = body.getChild("table",root.getNamespace());
        Element siteTable = null;
        
        try {
        	siteTable = table.getChild("tr",root.getNamespace())
			.getChild("td",root.getNamespace())
			.getChild("table",root.getNamespace());
        } catch (Exception e) {}
        
        if (siteTable != null) {
        	for (Element t: (List<Element>)table.getChild("tr",root.getNamespace())
        			.getChild("td",root.getNamespace())
        			.getChildren("table",root.getNamespace())) {
        		parseFullTable(t);
        	}
        } else {
        	parseListingTable(table);
        	
        }
    }
        
    @SuppressWarnings("unchecked")
	private void parseListingTable(Element e) throws ParseException {
        List<Element> rows = e.getChildren();
        List<BandwidthMeasurement> bms = new ArrayList<BandwidthMeasurement>();
        
        for(int i=1;i<rows.size();i++) {
        	Element row = rows.get(i);
        	List<Element> columns = row.getChildren();
        	String sDate = columns.get(0).getTextTrim();
        	String source = columns.get(1).getTextTrim();
        	String dest = columns.get(2).getTextTrim();
        	String tput = columns.get(3).getTextTrim();
//        	logger.debug("Table entries: " + sDate + ", " + source + ", " + dest + ", " + tput);
        	
        	Date date = new SimpleDateFormat("HH:mm:ss MM/dd/yyyy").parse(sDate);
        	
        	BandwidthMeasurement bm = new BandwidthMeasurement(source,dest,date,formatThruput(tput));
        	
        	// only keep the most recent measurement
        	if (bms.contains(bm)) {
        		bms.remove(bm);
        	}
        	
        	bms.add(bm);
        	
        }
        
        measurements.put(bms.get(0).getFromHostname(), bms);
        
        logger.debug("Parsed " + bms.size() + " measurements");
    }
    
    @SuppressWarnings("unchecked")
	private void parseFullTable(Element eTable) throws ParseException {
        List<Element> rows = eTable.getChildren();
        List<BandwidthMeasurement> bms = new ArrayList<BandwidthMeasurement>();
        
        // get from hostname
        String from = rows.get(0).getChild("td",eTable.getNamespace()).getChildText("a",eTable.getNamespace());
    	
    	// get measurement date
    	List<Element> columns = rows.get(1).getChildren();
    	String sDate = columns.get(1).getTextTrim();
    	Date date = new SimpleDateFormat("MM/dd/yyyy").parse(sDate);
    	
    	logger.debug("Full table entries for " + from + " on " + date);
    	
        for(int i=3;i<rows.size();i++) {
        	columns = rows.get(i).getChildren();
        	String dest = columns.get(0).getTextTrim();
        	String tput = columns.get(3).getChildTextTrim("a", eTable.getNamespace());
        	logger.debug("Table entries: " + from + ", " + dest + ", " + date + ", " + tput);
        	
        	BandwidthMeasurement bm = new BandwidthMeasurement(from,dest,date,formatThruput((tput)));
        	
        	// only keep the most recent measurement
        	if (bms.contains(bm)) {
        		bms.remove(bm);
        	}
        	
        	bms.add(bm);
        	
        }
        
        measurements.put(from, bms);
        
        logger.debug("Parsed " + bms.size() + " measurements");
    }
    
    private Double formatThruput(String tput) {
    	if (tput == null || tput.equals("") || tput.equals("N/A")) {
    		return Double.valueOf(0.0);
    	} else {
    		return Double.valueOf(tput.replaceAll(",", ""));
    	}
    }
    
    public Map<String,List<BandwidthMeasurement>> getAllBandwidthMeasurements() {
    	return measurements;
    }
    
    public List<BandwidthMeasurement> getBandwidthMeasurementsAtResource(String source) {
    	return measurements.get(source);
    }
    
    public BandwidthMeasurement getBandwidthMeasurementBetweenResources(String source, String dest) {
    	return measurements.get(source).get(measurements.get(source).indexOf(dest));
    }
    
}
