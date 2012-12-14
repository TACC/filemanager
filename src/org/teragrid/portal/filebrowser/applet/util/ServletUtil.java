/* 
 * Created on Dec 18, 2007
 * 
 * Developed by: Rion Dooley - dooley [at] tacc [dot] utexas [dot] edu
 * 				 TACC, Texas Advanced Computing Center
 * 
 * https://www.tacc.utexas.edu/
 */

package org.teragrid.portal.filebrowser.applet.util;

import java.io.File;
import java.net.URL;
import java.security.cert.X509Certificate;
import java.util.List;

import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.apache.xmlrpc.client.XmlRpcCommonsTransportFactory;
import org.teragrid.portal.filebrowser.applet.ConfigOperation;
import org.teragrid.portal.filebrowser.applet.ConfigSettings;
import org.teragrid.portal.filebrowser.server.servlet.TGFileTransferHistory;
import org.teragrid.portal.filebrowser.server.servlet.TGNotification;
import org.teragrid.portal.filebrowser.server.servlet.TGProfile;
import org.teragrid.portal.filebrowser.server.servlet.TGResourceDiscovery;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.basic.DateConverter;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * Utility class to get an XmlRpcClient object for communication
 * with the TG history servlet.
 * 
 * @author Rion Dooley < dooley [at] tacc [dot] utexas [dot] edu >
 *
 */
public class ServletUtil {

    public static final String GET_RESOURCES = TGResourceDiscovery.class.getName() + ".retrieveResources";
    public static final String GET_PAGED_RESOURCES = TGFileTransferHistory.class.getName() + ".getPage";
    public static final String GET_HISTORY = TGFileTransferHistory.class.getName() + ".get";
    public static final String GET_BANDWIDTH = TGResourceDiscovery.class.getName() + ".getBandwidth";
    public static final String ADD_RECORD = TGFileTransferHistory.class.getName() + ".add";
    public static final String UPDATE_RECORD = TGFileTransferHistory.class.getName() + ".update";
    public static final String REMOVE_RECORD = TGFileTransferHistory.class.getName() + ".remove";
    public static final String CLEAR_RECORDS = TGFileTransferHistory.class.getName() + ".clear";
    public static final String ADD_NOTIFICATION = TGNotification.class.getName() + ".add";
    public static final String ADD_NOTIFICATIONS = TGNotification.class.getName() + ".addAll";
    public static final String REMOVE_NOTIFICATION = TGNotification.class.getName() + ".remove";
    public static final String REMOVE_NOTIFICATIONS = TGNotification.class.getName() + ".removeAll";
    public static final String CLEAR_NOTIFICATIONS = TGNotification.class.getName() + ".clear";
    public static final String FIND_USER = TGProfile.class.getName() + ".findUsers";
    public static final String FIND_COLLEAGUES = TGProfile.class.getName() + ".findColleagues";
    public static final String FIND_PROJECT_PARTNERS = TGProfile.class.getName() + ".findProjectPartners";
    
	private static XStream xstream = new XStream(new DomDriver());
    
    private static String defaultFormat = "MMM d, yyyy h:mm:ss a";
    
    private static String[] acceptableFormats = new String[] {
            "MM/dd/yyyy HH:mm:ss",
            "yyyy-MM-dd HH:mm:ss.S z",
            "yyyy-MM-dd HH:mm:ss.S a",
            "yyyy-MM-dd HH:mm:ssz",
            "yyyy-MM-dd HH:mm:ss z", // JDK 1.3 needs both prev versions
            "yyyy-MM-dd HH:mm:ssa" }; // backwards compatability
    

    {
        xstream.registerConverter(new DateConverter(defaultFormat, acceptableFormats));
    }

    
    
    public static XStream getXStream() {
    	return xstream;
    }
    
    public static XmlRpcClient getClient() {

    	
    	// Create a trust manager that does not validate certificate chains
        @SuppressWarnings("unused")
		TrustManager[] trustAllCerts = new TrustManager[] {
            new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
     
                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                    // Trust always
                }
     
                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                    // Trust always
                }
            }
        };
     
        try {
	       
        } catch (Exception e) {
        	LogManager.error("Failed to enable SSL", e);
        }
        
        XmlRpcClient client = null;
//        
        try {
//        	 // Install the all-trusting trust manager
//	        SSLContext sc = SSLContext.getInstance("SSL");
//	        // Create empty HostnameVerifier
//	        HostnameVerifier hv = new HostnameVerifier() {
//                    public boolean verify(String arg0, SSLSession arg1) {
//                            return true;
//                    }
//	        };
//	
//	        sc.init(null, trustAllCerts, new java.security.SecureRandom());
//	        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
//	        HttpsURLConnection.setDefaultHostnameVerifier(hv);
	        
	    	System.setProperty("javax.net.ssl.trustStore", ConfigOperation.getUserHome() + "tgup_filemanager"
					+ File.separator + "certificates" + File.separator + "keystore");
	        System.setProperty("javax.net.ssl.trustStorePassword", "changeit");
	        System.setProperty("javax.net.ssl.trustStoreType", "JKS");
	        
	    	//LogManager.debug("Trust store location: " + System.getProperty("javax.net.ssl.trustStore"));
	    	
	    	XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
	        config.setEnabledForExceptions(true); 
	        config.setEnabledForExtensions(true);
            config.setServerURL(new URL(ConfigSettings.SERVICE_TGFM_SERVLET));
            
            // use Commons HttpClient as transport
            client = new XmlRpcClient();
            client.setTransportFactory(
                new XmlRpcCommonsTransportFactory(client));
            
            // set configuration
            client.setConfig(config);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return client;
    }
    
    public static String dewebify(String s) {
        return s.replaceAll("&lt;", "<")
        .replaceAll("&gt;", ">")
        .replaceAll("&quot;","\"")
        .replaceAll("&apos;", "'");
    }
    
    public static boolean isValid(String s) {
    	return (s != null && !s.equals(""));
    }
    
    public static String[] implode(String separator, String tags)
	{
		if (!org.apache.axis.utils.StringUtils.isEmpty(tags)) 
		{
			return new String[]{""};
		}
		else if (!tags.contains(separator))
		{
			return new String[]{tags};
		}
		else
		{
			return tags.split(separator);
		}
	}
    
    public static String explode(String glue, List<?> list)
	{
		
		String explodedList = "";
		
		if (list == null || list.isEmpty()) return explodedList;
		
		for(Object field: list)
			explodedList += glue + field.toString();
		
		return explodedList.substring(glue.length());
	}
}
