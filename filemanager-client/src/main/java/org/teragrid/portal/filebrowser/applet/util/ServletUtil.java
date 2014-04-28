/* 
 * Created on Dec 18, 2007
 * 
 * Developed by: Rion Dooley - dooley [at] tacc [dot] utexas [dot] edu
 * 				 TACC, Texas Advanced Computing Center
 * 
 * https://www.tacc.utexas.edu/
 */

package org.teragrid.portal.filebrowser.applet.util;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.lang.StringUtils;
import org.globus.gsi.gssapi.GlobusGSSCredentialImpl;
import org.restlet.Client;
import org.restlet.Context;
import org.restlet.data.Parameter;
import org.restlet.data.Protocol;
import org.restlet.engine.header.Header;
import org.restlet.ext.ssl.SslContextFactory;
import org.restlet.resource.ClientResource;
import org.restlet.util.Series;
import org.teragrid.portal.filebrowser.applet.AppMain;
import org.teragrid.portal.filebrowser.applet.ConfigSettings;

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

	public static final String TRANSFERS_SERVICE_URL = ConfigSettings.SERVICE_TGFM_API + "transfers";
	public static final String NOTIFICATIONS_ADD_SERVICE_URL = ConfigSettings.SERVICE_TGFM_API + "notifications/add";
	public static final String NOTIFICATIONS_DELETE_SERVICE_URL = ConfigSettings.SERVICE_TGFM_API + "notifications/delete";
	public static final String USERS_SERVICE_URL = ConfigSettings.SERVICE_TGFM_API + "users";
	public static final String COLLEAGUES_SERVICE_URL = ConfigSettings.SERVICE_TGFM_API + "colleagues";
	public static final String PARTNERS_SERVICE_URL = ConfigSettings.SERVICE_TGFM_API + "partners";
	public static final String SYSTEMS_SERVICE_URL = ConfigSettings.SERVICE_TGFM_API + "systems";
    public static Client client = null;
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
    
    static {
    	// Create all-trusting host name verifier
    	HostnameVerifier allHostsValid = new HostnameVerifier() {
    	      public boolean verify(String hostname, SSLSession session) {
    	        return true;
    	      }
    	    };
    	HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
    }

    public static XStream getXStream() {
    	return xstream;
    }
    
    @SuppressWarnings("unchecked")
	public static ClientResource getClient(String endpoint) 
    {
    	if (client == null) 
    	{
    		if (endpoint.startsWith("https")) {
    			client = new Client(new Context(), Protocol.HTTPS);
    		} else {
    			client = new Client(new Context(), Protocol.HTTP);
    		}
    	}
   
//    	try {
//	       
//    	} catch (Exception e) {
//    		LogManager.error("Failed to enable SSL", e);
//    	}
//      
//  	 	// Install the all-trusting trust manager
//        SSLContext sc = SSLContext.getInstance("SSL");
//        // Create empty HostnameVerifier
//        HostnameVerifier hv = new HostnameVerifier() {
//
//			public boolean verify(String arg0, SSLSession arg1) {
//				return true;
//			}
//             
//        };
//
//        sc.init(null, trustAllCerts, new java.security.SecureRandom());
//        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
//        HttpsURLConnection.setDefaultHostnameVerifier(hv);
//        
//    	System.setProperty("javax.net.ssl.trustStore", ConfigOperation.getUserHome() + "xup_filemanager"
//				+ File.separator + "security" + File.separator + "keystore");
//        System.setProperty("javax.net.ssl.trustStorePassword", "changeit");
//        System.setProperty("javax.net.ssl.trustStoreType", "JKS");
//        
//    	LogManager.debug("Trust store location: " + System.getProperty("javax.net.ssl.trustStore"));
    	
//    	Series<Parameter> parameters = client.getContext().getParameters();
//    	parameters.add("truststorePath", ConfigOperation.getUserHome() + "xup_filemanager"
//				+ File.separator + "security" + File.separator + "keystore");
//    	parameters.add("truststorePassword", "changeit");
//    	parameters.add("trustPassword", "changeit");
//    	parameters.add("truststoreType", "JKS");
    	
    	ClientResource service = new ClientResource(endpoint);
    	

		try
    	{
			
    		TrustManager tm = new X509TrustManager() {
	    	    public void checkClientTrusted(X509Certificate[] chain,
	    	                    String authType)
	    	                    throws CertificateException {
	    	    }
	
	    	    public X509Certificate[] getAcceptedIssuers() {
	    	        return new X509Certificate[0];
	    	    }
	
	    	    public void checkServerTrusted(X509Certificate[] chain,
	    	                    String authType)
	    	                    throws CertificateException {
	    	        // This will never throw an exception.
	    	        // This doesn't check anything at all: it's insecure.
	    	    }
	    	};
	
	    	final SSLContext sslContext = SSLContext.getInstance("TLS");
	    	sslContext.init(null, new TrustManager[] { tm }, null);
	    	
	    	Context context = client.getContext();
	    	context.getAttributes().put("sslContextFactory", new SslContextFactory() {
	    	    public void init(Series<Parameter> parameters) { }
	    	    public SSLContext createSslContext() { return sslContext; }
	    	});
	    } catch (Exception e) {
			LogManager.error("Failed to enable SSL", e);
		}
    	
    	Series<Header> headers = (Series<Header>) service.getRequest().getAttributes().get("org.restlet.http.headers");;
    	if (headers == null) { 
    		headers = new Series<Header>(Header.class); 
    	} 
    	headers.add("X-USER-DN", ((GlobusGSSCredentialImpl)AppMain.defaultCredential).getX509Credential().getIdentity());
    	service.setNext(client);
    	service.getRequest().getAttributes().put("org.restlet.http.headers", headers);
    	
    	return service;
    }
    
//    public static XmlRpcClient getClient() {
//
//    	
//    	// Create a trust manager that does not validate certificate chains
//        @SuppressWarnings("unused")
//		TrustManager[] trustAllCerts = new TrustManager[] {
//            new X509TrustManager() {
//                public X509Certificate[] getAcceptedIssuers() {
//                    return null;
//                }
//     
//                public void checkClientTrusted(X509Certificate[] certs, String authType) {
//                    // Trust always
//                }
//     
//                public void checkServerTrusted(X509Certificate[] certs, String authType) {
//                    // Trust always
//                }
//            }
//        };
//     
//        try {
//	       
//        } catch (Exception e) {
//        	LogManager.error("Failed to enable SSL", e);
//        }
//        
//        XmlRpcClient client = null;
////        
//        try {
////        	 // Install the all-trusting trust manager
////	        SSLContext sc = SSLContext.getInstance("SSL");
////	        // Create empty HostnameVerifier
////	        HostnameVerifier hv = new HostnameVerifier() {
////                    public boolean verify(String arg0, SSLSession arg1) {
////                            return true;
////                    }
////	        };
////	
////	        sc.init(null, trustAllCerts, new java.security.SecureRandom());
////	        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
////	        HttpsURLConnection.setDefaultHostnameVerifier(hv);
//	        
//	    	System.setProperty("javax.net.ssl.trustStore", ConfigOperation.getUserHome() + "xup_filemanager"
//					+ File.separator + "certificates" + File.separator + "keystore");
//	        System.setProperty("javax.net.ssl.trustStorePassword", "changeit");
//	        System.setProperty("javax.net.ssl.trustStoreType", "JKS");
//	        
//	    	//LogManager.debug("Trust store location: " + System.getProperty("javax.net.ssl.trustStore"));
//	    	
//	    	XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
//	        config.setEnabledForExceptions(true); 
//	        config.setEnabledForExtensions(true);
//            config.setServerURL(new URL(ConfigSettings.SERVICE_TGFM_SERVLET));
//            
//            // use Commons HttpClient as transport
//            client = new XmlRpcClient();
//            client.setTransportFactory(
//                new XmlRpcCommonsTransportFactory(client));
//            
//            // set configuration
//            client.setConfig(config);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        
//        return client;
//    }
//    
//    public static String dewebify(String s) {
//        return s.replaceAll("&lt;", "<")
//        .replaceAll("&gt;", ">")
//        .replaceAll("&quot;","\"")
//        .replaceAll("&apos;", "'");
//    }
    
    public static boolean isValid(String s) {
    	return (s != null && !s.equals(""));
    }
    
    public static String[] implode(String separator, String tags)
	{
		if (StringUtils.isEmpty(tags)) 
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
