/**
 * 
 */
package edu.utexas.tacc.wcs.filemanager.client;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Properties;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

/**
 * Invariant configuration values for TGFM client
 * 
 * @author dooley
 * 
 */
public class ConfigSettings 
{	
	public static Properties props = new Properties();
    
    static {
        try {
            props.load(ConfigSettings.class.getClassLoader().getResourceAsStream("client.properties"));          
            System.setProperty("jsse.enableCBCProtection", "false");
        } catch (Exception e) {
            e.printStackTrace();
        }
    
    }
	/* Remote Service Locations */
	public static final String SERVICE_MYPROXY_SERVER = (String)props.getProperty("myproxy.host");
	public static final String SERVICE_MYPROXY_PORT = (String)props.getProperty("myproxy.port");
	public static final String SERVICE_SPEEDPAGE = (String)props.getProperty("xsede.api.speedpage");
	public static final String SERVICE_TGFM_API = (String)props.getProperty("xsede.api.xfm");
	public static final String SERVICE_TG_USER_PROFILE = (String)props.getProperty("xsede.api.profile");
	
	public static String SOFTWARE_VERSION;
	public static String SOFTWARE_BUILD_DATE;
	
	static {
		try {
			Enumeration<URL> resources = ConfigSettings.class.getClassLoader().getResources("META-INF/MANIFEST.MF");
			while (resources.hasMoreElements()) {
			    try {
			      Manifest manifest = new Manifest(resources.nextElement().openStream());
			      Attributes attributes = manifest.getMainAttributes();
			      if (attributes.containsKey("Implementation-Title") && attributes.getValue("Implementation-Title").equals("File Manager")) {
			    	  SOFTWARE_VERSION = attributes.getValue("Implementation-Version");
			    	  SOFTWARE_BUILD_DATE = attributes.getValue("Built-Date");
			      } else if (attributes.containsKey("Implementation-Title")){ 
			    	  System.out.println(attributes.getValue("Implementation-Title"));
			    	  
			      }
			    } catch (IOException E) {
			      // handle
			    }
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/* Bundled jars unpacked during startup */
	public static final String BUNDLED_JAR_HELP = "help.zip";
	public static final String BUNDLED_JAR_CERTS = "ca.jar";
	
	
	/* Work files used by the client */
	public static final String FILE_NAME_COG_PROPERTIES = "cog.properties";
	public static final String FILE_NAME_USER_PROXY = "x509up_u501";
	public static final String FILE_NAME_LOGGING = "tgfb.log";
	
	
	/* Dynamic Resource Names */
	public static final String RESOURCE_NAME_LOCAL = "Local";
	public static final String RESOURCE_NAME_AMAZONS3 = "Amazon S3";
//	public static final String RESOURCE_NAME_TGSHARE = "XSEDE $SHARE";
	public static final String RESOURCE_NAME_SRB = "XSEDE IRODS";
	
	/* Application Defaults XML Tag Values */
	public static final String XML_TAG_CONFIGURATION = "resources";
	public static final String XML_TAG_APPSETTINGS_GROUP = "appSettings";
	public static final String XML_TAG_APPSETTING_ITEM = "appSetting";
	public static final String XML_TAG_APPSETTING_LOGFILE ="logfile";
	public static final String XML_TAG_APPSETTING_SHOWHIDDEN = "showHidden";
	public static final String XML_TAG_APPSETTING_USE_PASSIVE = "passive";
	public static final String XML_TAG_APPSETTING_CONN_MAX = "connMax";
	public static final String XML_TAG_APPSETTING_CONN_RETRY = "connRetry";
	public static final String XML_TAG_APPSETTING_CONN_DELAY = "connDelay";
	public static final String XML_TAG_APPSETTING_CONN_KEEPALIVE = "connKeepAlive";
	public static final String XML_TAG_APPSETTING_PARALLEL = "parallel";
	public static final String XML_TAG_APPSETTING_BUFFER_SIZE = "bufferSize";
	public static final String XML_TAG_APPSETTING_DEPLOY_DIR = "deploy_dir";
	public static final String XML_TAG_APPSETTING_USER_DN = "dn";
	public static final String XML_TAG_APPSETTING_NOTIFICATION_DEFAULT = "notification";
	public static final String XML_TAG_APPSETTING_TRUSTED_CA_DIR = "certificates";
	public static final String XML_TAG_APPSETTING_SEARCH_EXCLUDE_LIST = "excludeFromSearch";
	public static final String XML_TAG_APPSETTING_USER_PROXY_NAME = "proxy";
	public static final String XML_TAG_APPSETTING_SHOW_HIDDEN = "showHidden";
	public static final String XML_TAG_APPSETTING_STRIPE_TRANSFER = "stripeTransfers";
	public static final String XML_TAG_APPSETTING_DOWNLOAD_DIR = "download_dir";
	public static final String XML_TAG_APPSETTING_MYPROXY_HOST = "myproxy_hostname";
	public static final String XML_TAG_APPSETTING_MYPROXY_PORT = "myproxy_port";
	public static final String XML_TAG_APPSETTING_MYPROXY_USERNAME =  "myproxy_username";
	public static final String XML_TAG_APPSETTING_MYPROXY_LIFETIME =  "myproxy_lifetime";
	public static final String XML_TAG_APPSETTING_MYPROXY_CRED_NAME = "myproxy_credentialname";
	public static final String XML_TAG_APPSETTING_MYPROXY_TRUSTROOTS = "myproxy_trustroots";
	
	/* Remote Resource XML Tag Values */
	public static final String XML_TAG_SITES_GROUP = "sites";
	public static final String XML_TAG_SITE_ITEM = "site";
	public static final String XML_TAG_SITE_NAME = "name";
	public static final String XML_TAG_SITE_HOST = "host";
	public static final String XML_TAG_SITE_PORT = "port";
	public static final String XML_TAG_SITE_CONN_DELAY = "connDelay";
	public static final String XML_TAG_SITE_USE_PASSIVE = "passive";
	public static final String XML_TAG_SITE_CONN_RETRY = "connRetry";
	public static final String XML_TAG_SITE_CONN_PARALLEL = "connParallel";
	public static final String XML_TAG_SITE_CONN_MAX = "connMax";
	public static final String XML_TAG_SITE_BUFFER_SIZE = "bufferSize";
	public static final String XML_TAG_SITE_LISTED = "listed";
	public static final String XML_TAG_SITE_HOST_TYPE = "hostType";
	public static final String XML_TAG_SITE_LOGIN_MODE = "loginmode";
	public static final String XML_TAG_SITE_HIDDEN_SHOWN = "showHidden";
	public static final String XML_TAG_SITE_STRIPING_ENABLED= "stripeTransfers";
	public static final String XML_TAG_SITE_CONN_KEEP_ALIVE = "connKeepAlive";
	public static final String XML_TAG_SITE_USERNAME = "user";
	public static final String XML_TAG_SITE_PASSWORD = "pwd";
	public static final String XML_TAG_SITE_SEARCH_MAX_RESULTS = "maxSearchResults";
	public static final String XML_TAG_SITE_SEARCH_MAX_DEPTH = "maxSearchDepth";
	
	/* Map Info XML Tag Values */
	public static final String XML_TAG_MAP_GROUP = "mapinfo";
	public static final String XML_TAG_MAP_ID = "map";
	public static final String XML_TAG_MAP_NAME = "name";
	public static final String XML_TAG_MAP_AREA = "area";
	public static final String XML_TAG_MAP_COORD_X = "x";
	public static final String XML_TAG_MAP_COORD_Y = "y";
	
	/* RFT Services XML Tag Values */
	public static final String XML_TAG_RFTSETTINGS_GROUP = "rftServices";
	public static final String XML_TAG_RFTSETTINGS_ID = "url";
	public static final String XML_TAG_RFTSETTINGS_SERVICE = "service";
	public static final String XML_TAG_RFTSETTINGS_FACTORY = "factory";
	
	/* Bookmark XML Tag Values */
	public static final String XML_TAG_BOOKMARK_GROUP = "bookmarks";
	public static final String XML_TAG_BOOKMARK_ID = "bookmark";
	public static final String XML_TAG_BOOKMARK_NAME = "name";
	public static final String XML_TAG_BOOKMARK_HOST = "host";
	public static final String XML_TAG_BOOKMARK_CREATED = "created";
	
}
