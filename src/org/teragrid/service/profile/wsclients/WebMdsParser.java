package org.teragrid.service.profile.wsclients;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.teragrid.service.profile.wsclients.model.ComputeDTO;
import org.teragrid.service.profile.wsclients.model.Service;

public class WebMdsParser {
	
	private static Logger log = Logger.getLogger(WebMdsParser.class);
	private Hashtable<ComputeDTO,List<Service>> resourceCache = new Hashtable<ComputeDTO,List<Service>>();
	
	public WebMdsParser(String endpoint) {
		// parse csv output from ctss-resource-v1 service
//		Client client = new Client(Protocol.HTTP);  
//		Response response = client.get(endpoint);  
//		  
//		// Parse the response into a SystemDTO object
//		Representation output = response.getEntity(); 
		try {
			resourceCache.clear();
			
			
			
			parseXml(retrieveXmlDocument(cacheWebMdsOutput(endpoint)));
			
//			FileInputStream in = new FileInputStream("/Users/dooley/Desktop/rawwebmds.xml");
//			parseXml(retrieveXmlDocument(in));
//			
		} catch (Exception e) {
			log.debug("Failed to retrieve webmds output from " + endpoint,e);
		}
	}
	
	private InputStream cacheWebMdsOutput(String endpoint) throws IOException {
		URL url = new URL(endpoint);
		@SuppressWarnings("unused")
		URLConnection conn = url.openConnection();
		InputStream in = new BufferedInputStream(url.openStream());
//		BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream("/Users/dooley/Desktop/webmdscache.xml"));
//		byte[] b = new byte[2048];
//		while ((in.read(b)) > -1) {
//			out.write(b);
//		}
//		in.close();
//		out.close();
//		return new FileInputStream("/Users/dooley/Desktop/webmdscache.xml");
		return in;
	}
	
	public static void main(String[] args) {
		String endpoint = "http://info.teragrid.org:8080/webmds/webmds?info=tgislocal";
		WebMdsParser parser = new WebMdsParser(endpoint);
		
		for (ComputeDTO resource: parser.getResources()) {
			System.out.println("Found resource: " + resource.toCsv());
			for (Service service: parser.getServices(resource)) {
				System.out.println("\tService: " + service.toCsv());
			}
		}
		
	}
	
	public Set<ComputeDTO> getResources() {
		return resourceCache.keySet();
	}
	
	public List<Service> getServices(ComputeDTO resource) {
		return resourceCache.get(resource);
	}
	
	private Document retrieveXmlDocument(InputStream in) throws IOException, JDOMException {
//		StringBuffer input = new StringBuffer();
//		byte[] b = new byte[1024];
//		boolean started = false;
//		boolean reading = false;
//		FileWriter w = new FileWriter("/Users/dooley/Desktop/webmds2.xml");
//		input.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
//		w.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
//		while ((in.read(b)) > -1) {
//			String line = new String(b);
//			if (started) {
//				// look for ending tag of v4kitsrp
//				if (line.indexOf("</V4KitsRP>") > -1) {
//					System.out.println(line);
//					input.append(line.substring(0,line.indexOf("</V4KitsRP>") + 11));
//					w.append(line.substring(0,line.indexOf("</V4KitsRP>") + 11));
//					in.close();
//					w.flush();
//					w.close();
//					break;
//				} else {
//					input.append(line);	
//					w.append(line);
//					System.out.println(line);
//				}
//			} else {
//				if (line.indexOf("<V4KitsRP xmlns=\"\">") > -1) {
//					started = true;
//					input.append(line.substring(line.indexOf("<V4KitsRP xmlns=\"\">")));
//					w.append(line.substring(line.indexOf("<V4KitsRP xmlns=\"\">")));
//				}
//				
//			}
//		}
		
//		input."<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + input;//.substring(input.indexOf("V4KitsRP xmlns=\"\"")-1, input.lastIndexOf("V4KitsRP")-6);
//		System.out.println(input.substring(0,100));
//		System.out.println(input.substring(input.length() - 50,input.length() -1));
		
//		Document doc = new SAXBuilder().build(new FileInputStream("/Users/dooley/Desktop/webmds2.xml"));
		
		Document doc = new SAXBuilder().build(in);
//        XMLOutputter out = new XMLOutputter(Format.getPrettyFormat());
//        FileWriter writer = new FileWriter("/Users/dooley/Desktop/webmds.xml");
//        out.output(doc, writer);
//        writer.close();
//        in.close();
//		w.close();
        return doc;
	}
	
	@SuppressWarnings("unchecked")
	private void parseXml(Document doc) throws JDOMException, IOException {
		
        
        Element root = doc.getRootElement();
        List<Element> resources = root.getChildren("KitRegistration");
        for (Element resource: resources) {
        	ComputeDTO system = new ComputeDTO();
        	List<Service> services = new ArrayList<Service>();
        	
        	String isid = resource.getChild("ResourceID").getTextTrim();
        	String name = resource.getChild("ResourceName").getTextTrim();
        	String site = resource.getChild("SiteID").getTextTrim();
        	system.setId(isid);
        	system.setName(name);
        	system.setSite(site);
        	system.setStatus(Service.UP);
        	system.setLocalUsername("");
        	
        	List<Element> kits = resource.getChildren("Kit");
        	for (Element kit: kits) {
		        if (kit.getChild("Name").getTextTrim().equals("data-movement.teragrid.org")) {
		        	for (Element service: (List<Element>)kit.getChildren("Service")) {
		        		if (service.getChild("Name").getTextTrim().equals("gridftp-default-server")) {
		        			String endpoint = service.getChild("Endpoint").getTextTrim();
		        			system.setGridftpHostname(endpoint);
		        			
		        			Service gridftp = new Service("gridftp",endpoint,Service.UP);
		        			services.add(gridftp);
		        			if (system.getType() == null) {
		        				system.setType("storage");
		        			}
		        			break;
		        		}
		        	}
		        } else if (kit.getChild("Name").getTextTrim().equals("login.teragrid.org")) {
		        	for (Element service: (List<Element>)kit.getChildren("Service")) {
		        		if (service.getChild("Name").getTextTrim().equals("gsi-openssh")) {
		        			String endpoint = service.getChild("Endpoint").getTextTrim();
		        			system.setLoginHostname(endpoint);
		        			
		        			Service gsissh = new Service("gsissh",endpoint,Service.UP);
		        			services.add(gsissh);
		        			break;
		        		}	
		        	}
		        } else if (kit.getChild("Name").getTextTrim().equals("remote-compute.teragrid.org")) {
		        	// it's a compute resource
		        	if (system.getId().indexOf("viz") > -1) {
		        		system.setType("viz");
		        	} else {
		        		system.setType("compute");
		        	}
		        }
        	}
        	resourceCache.put(system,services);
        }
	}

}
