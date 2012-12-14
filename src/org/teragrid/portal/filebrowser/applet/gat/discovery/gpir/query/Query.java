package org.teragrid.portal.filebrowser.applet.gat.discovery.gpir.query;

import org.apache.axis.ConfigurationException;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.jdom.Document;

import org.teragrid.portal.filebrowser.server.servlet.Settings;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;

import javax.xml.namespace.QName;

/**
 * This is the utility class which queries the middleware service, parses the xml
 * and returns a string.  Methods are provided to query several different sources
 * of information as well as persist this information locally.
 * 
 * @author Rion Dooley < dooley [at] cct [dot] lsu [dot] edu >
 *
 */
public class Query {
    static Document document;
    
    @SuppressWarnings("unused")
	private String endpoint = null;
    
    /** Default constructor to be used when running main.
     */
    public Query(String endpoint) {
    	this.endpoint = endpoint;
    }

    /** Useful constructor to be used when running from code (hint, hint).
     *  @param queryType query by "vo" or "resource"
     *  @param queryName {jobs, nodes, load, motd, summary}.  There are more,
     *                   but this is all the info we're pushing at CCT right
     *                   now.
     *  @param queryArg  if queryType is "vo", then this is the name of the 
     *                   VO. Otherwise, if queryType is "resource", this is 
     *                   the resource of interest. 
     */
    public Query(String endpoint, String queryType, String queryName, String queryArg) {
    	
    	this(endpoint);
    	
        try {
	    // GPIR service is running on gridhub
            Service service = new Service();
            Call call = (Call) service.createCall();

            call.setTargetEndpointAddress(
                    new java.net.URL(endpoint) );

	    // Only 2 query operations are available and useful
            if(queryType.equals("vo")){
                call.setOperationName(
                    new QName("GPIRQuery", "getQueryByVo") );
            } else if(queryType.equals("resource")){
                call.setOperationName(
                    new QName("GPIRQuery", "getQueryByResource") );
            } else {
                printUsage();
            }

	    // invoke the service with the passed arguments
            Object ret = call.invoke( new Object[] { queryName, queryArg } );

	    // If you want all the messy xml output, uncomment the next line
            //System.out.println((String)ret);

	    // Otherwise, the xml will be cleaned up and printed to terminal
//            File xmlFile = new File("ccg_resources.xml");
//            FileOutputStream fos = new FileOutputStream(xmlFile);
//            PrintStream ps = new PrintStream(fos);
//            ps.println((String)ret);
            
            parseXML((String)ret);
	  
        } catch (Exception e) {
            System.err.println(e.toString());
            e.printStackTrace();
        }
    }

    /** Parse the XML passed from the web service into human-readable format.
     * 
     * @param xmlFile
     * @return
     */
    public void parseXML(String xml) {
        XMLReader reader;
        try {
            reader = XML.makeXMLReader();
            reader.setContentHandler( new Sink() ); 
            ByteArrayInputStream bais = new ByteArrayInputStream(xml.getBytes());
            reader.parse(new InputSource(bais));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    } // parseXLM

    /** Print correct command line usage of this class
     */
    public void printUsage(){
            System.err.println("Usage: \n" +
                //"\t queryGPIR resource {jobs,load,motd,nodes} <resourcename>\n" +
                "\t queryGPIR {jobs,load,motd,nodes} <voname>");
            System.exit(1);
    }
    
    /** Same as the programmatic constructor. Basically i just wanted a static method to
     *  call.
     *  @param queryType query by "vo" or "resource"
     *  @param queryName {jobs, nodes, load, motd, summary}.  There are more,
     *                   but this is all the info we're pushing at CCT right
     *                   now.
     *  @param queryArg  if queryType is "vo", then this is the name of the 
     *                   VO. Otherwise, if queryType is "resource", this is 
     *                   the resource of interest.
     *  @param xmlFile  the name of hte file to write the xml output to. 
     */
    public String getQuery(String queryType, String queryName, 
    								String queryArg, File xmlFile) {
    		Object ret = new String("");
    		try {
    	    	// GPIR service is running on gridhub
                Service service = new Service();
                Call call = (Call) service.createCall();

                call.setTargetEndpointAddress(
                        new java.net.URL(Settings.GPIR_SERVER) );

                // Only 2 query operations are available and useful
                if(queryType.equals("vo")){
                    call.setOperationName(
                        new QName("GPIRQuery", "getQueryByVo") );
                } else if(queryType.equals("resource")){
                    call.setOperationName(
                        new QName("GPIRQuery", "getQueryByResource") );
                } else {
                    printUsage();
                }

                // invoke the service with the passed arguments
                ret = call.invoke( new Object[] { queryName, queryArg } );

                // If you want all the messy xml output, uncomment the next line
                //System.out.println((String)ret);

                // Otherwise, the xml will be cleaned up and printed to terminal
                FileOutputStream fos = new FileOutputStream(xmlFile);
                PrintStream ps = new PrintStream(fos);
                ps.println((String)ret);
                fos.close();
             } catch (ConfigurationException e) {
    		 } catch (Exception e) {
//    		 	e.printStackTrace();
    		 }
    		 return (String)ret;
    }
}
