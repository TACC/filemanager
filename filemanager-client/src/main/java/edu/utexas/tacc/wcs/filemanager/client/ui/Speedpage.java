/* 
 * Created on March 27, 2009
 * 
 * Developed by: Rion Dooley - dooley [at] tacc [dot] utexas [dot] edu
 * 				 TACC, Texas Advanced Computing Center
 * 
 * https://www.tacc.utexas.edu/
 */

package edu.utexas.tacc.wcs.filemanager.client.ui;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import edu.utexas.tacc.wcs.filemanager.client.transfer.FTPSettings;
import edu.utexas.tacc.wcs.filemanager.client.util.LogManager;
import edu.utexas.tacc.wcs.filemanager.common.model.enumeration.FileProtocolType;

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

    public static int DEFAULT_MIN_BUFFER_SIZE = 32;

    public static int DEFAULT_LOCAL_BUFFER_SIZE = 1000;
    
    public static int DEFAULT_THIRD_PARTY_BUFFER_SIZE = 33000;
    
    private Map<String,Hashtable<String,Double>> measurementCache = new HashMap<String,Hashtable<String,Double>>();
    
    
    
    public Speedpage() {
    	
    }
    
    public double getMeasuredBandwidth(String sourceName, String destName) throws Exception {
    	String source = getSpeedpageResourceName(sourceName);
        String dest = getSpeedpageResourceName(destName);
    	
        if (measurementCache.containsKey(source)) {
        	if (!measurementCache.get(source).containsKey(dest)) {
        		cacheMeasurements(source);
        	}
        } else { 
        	cacheMeasurements(source);
        }
        
        return measurementCache.get(source).get(dest).doubleValue();
    }
    
    /**
     * Query speedpage for all bandwidth measurement from the source resource.  This is 
     * necessary because Speedpage does not have the ability to do a single query from
     * one resource to another.
     * 
     * @param source
     * @param dest
     * @throws Exception
     */
    private void cacheMeasurements(String source) throws Exception {
    	
    	String date = new SimpleDateFormat("MM/dd/yyyy").format(new Date());
        
        String arguments = source.replaceAll(" ", "+") + "&" +
            "begin=" + date + "&days_ago=1";
        
        URL url = new URL(
                "http://quipu.psc.teragrid.org/speedpage/www/speedpage.php?list=" + arguments);
        
    	
    	String xml = getWebpage(url);
    	
    	SpeedpageParser parser = new SpeedpageParser(xml);
    	
    	measurementCache.put(source, parser.getBandwidthMeasurements());
    	
    	LogManager.debug("Key entered was " + measurementCache.keySet().iterator().next());
    }
    
    /**
     * Calculate the idea buffer size based on the formula:
     * 
     * bufferSize =  bandwidth in Megabits per second (Mbs) * RTT in milliseconds (ms) * 1000 / 8
     * 
     * as listing in the globus-url-copy documentation.
     * 
     * @param host
     * @return
     */
    public int calculateBufferSize(FTPSettings source, FTPSettings dest) {
        
        if (source.protocol.equals(FileProtocolType.GRIDFTP) && dest.protocol.equals(FileProtocolType.GRIDFTP)) {
        	return DEFAULT_THIRD_PARTY_BUFFER_SIZE;
        } else {
        	return DEFAULT_LOCAL_BUFFER_SIZE; 
        }
        
//            // ping the remote resource to see how far apart it is.  If
//            // the resource is in the same network, or close by, then
//            // we need to cap the buffer so we don't flood the network.
//            int rtt = Pinger.getRountTripTime(source.host, dest.host);
//           
//            bufferSize = (long)(calculateBandwidth(source.name,dest.name) * rtt * 1000.0 / (8.0 * 1024));
//            
//        } catch (IOException e) {
//            LogManager.error("Failed to ping " + source + " in buffer calculation.",e);
//        } catch (BandwidthCalculationException e) {
//            LogManager.error("Failed to retrieve bandwidth from " + source + " to " + dest,e);
//        }
//        
//        return bufferSize;
    }
    
    public static String getWebpage(URL url) throws IOException {
    	
    	// get page
        URLConnection conn = url.openConnection();
        
        DataInputStream in = new DataInputStream ( conn.getInputStream (  )  ) ;
        BufferedReader d = new BufferedReader(new InputStreamReader(in));
        String htmlPage = "";
        while(d.ready()) {
            htmlPage += d.readLine();
        }
        
        return htmlPage;
    }
    
    private static String getSpeedpageResourceName(String name) throws IOException{
     //PSC Rachel    SDSC DTF    SDSC DataStar    TACC Lonestar    TACC Maverick    TACC Ranger    UC/ANL DTF
        if (name.equals("Big Red")) return "IU BigRed";
        if (name.equals("Queenbee")) return "LONI Queenbee";
        if (name.equals("Frost")) return "NCAR frost";
        if (name.equals("Abe")) return "NCSA Abe";
        if (name.equals("Cobalt")) return "NCSA Cobalt";
        if (name.equals("Mercury")) return "NCSA Mercury";
        if (name.equals("Tungsten")) return "NCSA Tungsten";
        if (name.equals("NSTG")) return "ORNL Neutron Science TeraGrid Gateway";
        if (name.equals("BigBen")) return "PSC Bigben";
        if (name.equals("Rachel")) return "PSC Rachel";
        if (name.equals("SDSC TeraGrid Cluster")) return "SDSC DTF";
        if (name.equals("DataStar")) return "SDSC DataStar";
        if (name.equals("Lonestar")) return "TACC Lonestar";
        if (name.equals("Maverick")) return "TACC Maverick";
        if (name.equals("Ranger")) return "TACC Ranger";
        if (name.equals("UC/ANL TeraGrid Cluster")) return "UC/ANL DTF";
        
        throw new IOException("Failed to map resource");
            
    }
    
    public static void main(String[] args) {
        LogManager.init();
        try {
			System.out.println("Bandwidth is: " + new Speedpage().getMeasuredBandwidth("Big Red","SDSC TeraGrid Cluster"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
    }

}

class SpeedpageParser {
    
    private Hashtable<String,Double> measurements = new Hashtable<String,Double>();
    private Document doc;
    
    @SuppressWarnings("unchecked")
	public SpeedpageParser(String html) throws Exception {
        doc = new SAXBuilder().build(new StringReader(html));
    
        Element root = doc.getRootElement();
        Element body = root.getChild("body",root.getNamespace());
        Element table = body.getChild("table",root.getNamespace());
        List<Element> rows = table.getChildren();
    
        for(int i=1;i<rows.size();i++) {
        	Element row = rows.get(i);
        	List<Element> columns = row.getChildren();
        	String sDate = columns.get(0).getTextTrim();
        	String source = columns.get(1).getTextTrim();
        	String dest = columns.get(2).getTextTrim();
        	String tput = columns.get(3).getTextTrim();
        	LogManager.debug("Table entries: " + sDate + ", " + source + ", " + dest + ", " + tput);
        	
//        	Date date = new SimpleDateFormat("HH:mm:ss MM/dd/yyyy").parse(sDate);
        	double rate = Double.valueOf(tput).doubleValue();
        	
        	measurements.put(dest,rate);
        	
        }
        
        LogManager.debug("Parsed " + measurements.size() + " measurements");
    }
    
    public Hashtable<String,Double> getBandwidthMeasurements() {
    	return measurements;
    }
    
    public Double getBandwidthMeasurement(String dest) {
    	return measurements.get(dest);
    }
    
}

class BandwidthMeasurement {
	String name;
	Date date;
	double value;
	
	public BandwidthMeasurement(String name, Date date, double value) {
		this.name = name;
		this.date = date;
		this.value = value;
	}
	
	public boolean equals(Object o) {
		if (o instanceof BandwidthMeasurement) {
			return name.equals(((BandwidthMeasurement)o).name);
		}
		return false;
	}
}

class Pinger {
    
    public static long getRountTripTime(String source, String dest) throws IOException{
        InetAddress inet = null;
        
        if (dest.equals(FTPSettings.Local.name)) {
            inet = InetAddress.getByName(dest);
        } else if (source.equals(FTPSettings.Local.name)) {
            inet = InetAddress.getByName(source);
        }
        
        long rtt = System.currentTimeMillis();
        boolean alive = inet.isReachable(3000);
        if (alive) {
            rtt = System.currentTimeMillis() - rtt;
            LogManager.debug("Able to ping " + inet.getHostName() + ": "+ alive);
            LogManager.debug("RTT was " + rtt);
            return rtt;
        } else {
            return -1;
        }
    }
}