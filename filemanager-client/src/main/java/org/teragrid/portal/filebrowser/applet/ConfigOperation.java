/*
 * Portions of this file Copyright 2004-2007 Shanghai Jiaotong University
 * 
 * This file or a portion of this file is licensed under the
 * terms of the Globus Toolkit Public License, found at
 * http://www.globus.org/toolkit/legal/4.0/
 * If you redistribute this file, with or without
 * modifications, you must include this notice in the file.
 */

package org.teragrid.portal.filebrowser.applet;

import static org.teragrid.portal.filebrowser.applet.ConfigSettings.BUNDLED_JAR_HELP;
import static org.teragrid.portal.filebrowser.applet.ConfigSettings.FILE_NAME_COG_PROPERTIES;
import static org.teragrid.portal.filebrowser.applet.ConfigSettings.FILE_NAME_LOGGING;
import static org.teragrid.portal.filebrowser.applet.ConfigSettings.FILE_NAME_USER_PROXY;
import static org.teragrid.portal.filebrowser.applet.ConfigSettings.RESOURCE_NAME_LOCAL;
import static org.teragrid.portal.filebrowser.applet.ConfigSettings.SERVICE_MYPROXY_PORT;
import static org.teragrid.portal.filebrowser.applet.ConfigSettings.SERVICE_MYPROXY_SERVER;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.security.cert.CertificateEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import javax.swing.JOptionPane;

import org.apache.commons.lang3.StringUtils;
import org.globus.common.CoGProperties;
import org.globus.gsi.CredentialException;
import org.globus.gsi.X509Credential;
import org.globus.gsi.gssapi.GlobusGSSCredentialImpl;
import org.globus.myproxy.MyProxy;
import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSException;
import org.restlet.resource.ClientResource;
import org.teragrid.portal.filebrowser.applet.exception.ResourceException;
import org.teragrid.portal.filebrowser.applet.transfer.FTPSettings;
import org.teragrid.portal.filebrowser.applet.transfer.GridFTP;
import org.teragrid.portal.filebrowser.applet.ui.Bookmark;
import org.teragrid.portal.filebrowser.applet.util.FileUtils;
import org.teragrid.portal.filebrowser.applet.util.LogManager;
import org.teragrid.portal.filebrowser.applet.util.ResourceName;
import org.teragrid.portal.filebrowser.applet.util.SGGCResourceBundle;
import org.teragrid.portal.filebrowser.applet.util.ServletUtil;
import org.teragrid.portal.filebrowser.applet.util.SiteUtil;
import org.teragrid.service.profile.wsclients.ProfileServiceClient;
import org.teragrid.service.profile.wsclients.model.ComputeDTO;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.basic.DateConverter;
import com.thoughtworks.xstream.io.xml.DomDriver;

import edu.utexas.tacc.wcs.filemanager.common.model.enumeration.FileProtocolType;
import edu.utexas.tacc.wcs.filemanager.service.resources.SystemsResource;

public class ConfigOperation {

	private static String DEFAULT_CONFIGURE_FILE = "defaults.xml";
	private static String SITES_FILE = "sites.xml";
	private static String BOOKMARKS_FILE = "bookmarks.xml";
	
	public static String appHome = null;

	private static ConfigOperation instance = null;

	private static boolean enableLogging = true;

	// Part of the configure infomation
	private Properties settings = new Properties();

	// saved site infomation list
	private List<FTPSettings> siteList = new ArrayList<FTPSettings>();

	private Hashtable<String, List<Bookmark>> bookmarksTable = new Hashtable<String, List<Bookmark>>();

	private String[] keys = { "logfile", "showHidden", "passive", "connMax",
			"connRetry", "connDelay", "connKeepAlive", "parallel",
			"bufferSize", "deploy_dir", "dn", "notification",
			"certificates", "excludeFromSearch", "proxy", "showHidden",
			"stripeTransfers", "download_dir", "myproxy_hostname",
			"myproxy_port", "myproxy_username", "myproxy_lifetime",
			"myproxy_credentialname", "myproxy_trustroots" };
	
	private String proxyHome = getProxyDir() + FILE_NAME_USER_PROXY;
	private String logHome = getDataDir() + FILE_NAME_LOGGING;
	
	private String[] values = { logHome, "false", "true", "10", "1", "5", "30",
			"1", "1000", getDataDir(), "", "NONE", getCertificateDir(), "",
			proxyHome, "false", "false",
			(String) System.getProperty("user.home"),
			SERVICE_MYPROXY_SERVER, SERVICE_MYPROXY_PORT, "", "12", "", "yes" };

	private static XStream xstream = new XStream(new DomDriver());

	private String defaultFormat = "MMM d, yyyy h:mm:ss a";

	private String[] acceptableFormats = new String[] { "MM/dd/yyyy HH:mm:ss",
			"yyyy-MM-dd HH:mm:ss.S z", "yyyy-MM-dd HH:mm:ss.S a",
			"yyyy-MM-dd HH:mm:ssz", "yyyy-MM-dd HH:mm:ss z", // JDK 1.3 needs
																// both prev
																// versions
			"yyyy-MM-dd HH:mm:ssa" }; // backwards compatability

	
	// private constructor
	private ConfigOperation() {

		xstream.registerConverter(new DateConverter(defaultFormat,
				acceptableFormats));
		
		try {

			AppMain.updateSplash(0, "Refreshing user preferences...");

			loadDefaultSettings();

			LogManager.info("Finished refreshing user preferences.");

		} catch (Exception e) {}

		try {
			AppMain.updateSplash(10, "Setting up user environment...");
			setup();
			LogManager.info("Finished setting up user environment.");
		} catch (Exception e) {
			LogManager.error("Failed to setup directory structure.", e);
		}
	}

	public void loadExtendedSettings() {

		try {
			AppMain.updateSplash(150, "Retrieving user authentication...");
			this.refreshAuthn();
			LogManager.info("Finished retrieving user authentication.");

		} catch (Exception e) {
			if (!StringUtils.isEmpty(e.getMessage()) && e.getMessage().indexOf("Unexpected certificate type") > -1 ) {
				LogManager.error("Your JVM has created an internal conflict loading this applet. " +
						"Please restart your browser and open this applet again to fix the problem.", e);
			} else {
				LogManager.error("Failed retrieving proxy from portal!!", e);
			}
		}

		try {
			AppMain.updateSplash(160,
					"Retrieving available TeraGrid resources...");
			
			refreshSites();

			FTPSettings.Local = getSiteByName(RESOURCE_NAME_LOCAL);
			LogManager.info("Finished refreshing TeraGrid resources.");

		} catch (Exception e) {
			LogManager.error("Failed retrieving resource list!!", e);

		}

		try {
			AppMain.updateSplash(185, "Refreshing user bookmarks...");
			
			loadBookmarks();

			LogManager.info("Finished refreshing user bookmarks.");

		} catch (Exception e) {
			LogManager.debug("Failed refreshing user bookmarks!!");
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * Deploy the bundled files and unpack several jars including help, 
	 * and the TeraGrid trusted ca's.
	 */
	private void setup() {

		appHome = getApplicationHome();

		try {
			JarFile tgfmJar = null;

			URL jarname = Class.forName(
					"org.teragrid.portal.filebrowser.applet.ConfigOperation")
					.getResource("ConfigOperation.class");
			
			readVersionInformation();
			
			// delete the old certs directory
			FileUtils.deleteRecursive(getCertificateDir());
			
//			for (String caURL: TRUSTED_CA_TARBALLS) {
//				deployRemoteCATarball(caURL);
//			}

			if (jarname.getProtocol().startsWith("jar")
					|| jarname.getProtocol().startsWith("htt")) {

				JarURLConnection c = (JarURLConnection) jarname
						.openConnection();
				tgfmJar = c.getJarFile();
				AppMain.updateSplash(-1, "Unpacking help system...");
				try {
					FileUtils.unpackJarEntry(tgfmJar, BUNDLED_JAR_HELP,
							getHelpDir());
				} catch (Exception e) {
					LogManager.error("Failed to unpack help files.", e);
				}
				
				// unpack keystore
				try {
					JarEntry keystoreEntry = new JarEntry(tgfmJar.getEntry("security/keystore"));
					File destKeystore = new File(getKeystoreDir() + File.separator + "keystore");
					FileUtils.extractJarEntry(tgfmJar, keystoreEntry, destKeystore);
				} catch (Exception e) {
					LogManager.error("Failed to unpack keystore.", e);
				}
				
			} else {
				LogManager
						.debug("Not running from jar. Structure already in place.");
				
				// copy help
				AppMain.updateSplash(-1, "Unpacking help system...");
				File srcHelpDir = new File(appHome + "help");
				if (!srcHelpDir.exists()) {
					throw new IOException("Failed to copy help files from "
							+ srcHelpDir.getAbsolutePath());
				}
				try {
					org.apache.commons.io.FileUtils.copyDirectory(srcHelpDir, new File(getHelpDir()));
				} catch (Exception e) {
					LogManager.error("Failed to copy help files.", e);
				}
				
				
				File srcKeystore = new File(appHome + "security" + File.separator + "keystore");
				if (!srcKeystore.exists()) {
					throw new IOException("Failed to copy keystore from "
							+ srcKeystore.getAbsolutePath());
				}
				
				try {
					File destKeystore = new File(getKeystoreDir() + File.separator + "keystore");
					org.apache.commons.io.FileUtils.copyFile(srcKeystore,destKeystore);
					
					System.setProperty("javax.net.ssl.trustStore", destKeystore.getAbsolutePath());
			        System.setProperty("javax.net.ssl.trustStorePassword", "changeit");
			        System.setProperty("javax.net.ssl.trustStoreType", "JKS");
		        
				} catch (Exception e) {
					LogManager.error("Failed to copy keystore.", e);
				}
				
			}
			
//			rebuildCoGProperties();

		} catch (Exception e) {
			LogManager.error("Failed to set up user environment.", e);
		}
	}

	public void rebuildCoGProperties() {
		String calist = "";

		File certDir = new File(getCertificateDir());
		for (File caFile : certDir.listFiles()) {
			if (caFile.getName().endsWith(".0")) {
				calist = caFile.getAbsolutePath() + "," + calist;
			}
		}

		calist = calist.substring(0, calist.length() - 1);

		LogManager.info("Finished with calist " + calist);

		// set the globus environment
		System.setProperty("org.globus.config.file", getDataDir()
				+ "cog.properties");
		String s = System.getProperty("GLOBUS_LOCATION");
		if (null == s) {
			LogManager.warn(SGGCResourceBundle.getResourceString(ResourceName.KEY_WARN_APPMAIN_ENVVARNOTFOUND));
			s = "";
		}
		System.setProperty("globus.location", s);
		System.setProperty("X509_CERT_DIR", getCertificateDir());
		
		// set the default ca directory to the one we create
		try {
			File cogPropertiesFile = new File(getDataDir() + FILE_NAME_COG_PROPERTIES);
			cogPropertiesFile.createNewFile();

			CoGProperties props = new CoGProperties(cogPropertiesFile
					.getAbsolutePath());
			props.setCaCertLocations(calist);
			props.setProxyFile(getConfigValue("proxy"));

			LogManager.info("Writing cog props to " + getDataDir()
					+ "cog.properties");
			props.store(new FileOutputStream(cogPropertiesFile), "");
			CoGProperties.setDefault(props);
			
		} catch (Exception e) {
			LogManager.error("something happened",e);
			CoGProperties props = CoGProperties.getDefault();
			props.setCaCertLocations(calist);
			props.setProxyFile(getConfigValue("proxy"));
			LogManager.info("Writing cog props to " + getDataDir()
					+ "cog.properties");
		}
	}

	private void initDefaultSettings() {

		this.settings.clear();
		
		for (int i = 0; i < keys.length; i++) {
			LogManager.debug("adding " + keys[i] + " = " + values[i]);
			this.settings.put(keys[i], values[i]);
		}

	}
	
	/**
	 * Clears out all the automatically added sites and refreshes the site
	 * list by calling retrieveResources() again.
	 * 
	 * @throws IOException
	 */
	public void resetResources() throws IOException {
		List<FTPSettings> deleteList = new ArrayList<FTPSettings>();
		for (FTPSettings site: siteList) { 
			if (!site.userDefined) {
				deleteList.add(site);
			}
		}
		
		siteList.removeAll(deleteList);
		
		retrieveResources();
	}

	/**
	 * Call the TGResourceDiscovery service to get the list of sites that are
	 * authorized for use for the given dn.
	 * 
	 * @throws IOException
	 */
	private void retrieveResources() throws IOException {

		// Always add the local site by default;
		addSite(new FTPSettings(RESOURCE_NAME_LOCAL, FileProtocolType.FILE.getDefaultPort(), FileProtocolType.FILE));
		
		// next check to see if they provided a static list of resources
		try {
			String resourceList = AppMain.getApplet().getParameter(
					"resource_list");

			if (resourceList != null && !resourceList.equals("")) {
				String[] resources = resourceList.split(",");

				FTPSettings site = null;
				for (String resource : resources) {
					site = new FTPSettings(resource, FileProtocolType.GRIDFTP.getDefaultPort(),
							FileProtocolType.GRIDFTP);
					site.sshHost = resource;
					addSite(site);
				}
				return;
			}

		} catch (NullPointerException e) {
			LogManager.debug("No gpir service explicitly provided.");
		}
		
		// If no resource source was provided, default to calling the
		// TG Resource Discovery service for a user-specific list
		// of resources.
		try {
			if (!isLoggingEnabled() || !retrieveTGFMResources()) 
			{
				if (!StringUtils.isEmpty(AppMain.ssoUsername) && !StringUtils.isEmpty(AppMain.ssoPassword)) 
				{
					retrieveTGProfileResources();
				}
			} 
			
//			// this is running in standalone or out of the TGUP, so add TeraGrid Share support
//			if (AppMain.ssoUsername != null && AppMain.ssoUsername != null &&
//					AppMain.ssoPassword != null && AppMain.ssoPassword != null) {
//				FTPSettings site = new FTPSettings(ConfigSettings.RESOURCE_NAME_TGSHARE, 
//						FTPPort.PORT_TGSHARE, FTPType.XSHARE);
//				site.userName = AppMain.ssoUsername;
//				site.password = AppMain.ssoPassword;
//				site.host = ConfigSettings.SERVICE_TGSHARE_SERVICE;
//				addSite(site);
//			}
			
		} catch (NullPointerException e) {
			LogManager.debug("No SSO username/password provided for TeraGrid $SHARE");
		} catch(Exception e) {
			JOptionPane.showMessageDialog(AppMain.getFrame(),
                    "There was a problem retrieving your accounts\n" +
                    "from the middleware. We'll get the default set\n" +
                    "for you to use instead. Would you like to work\n" +
                    "offline for the rest of this session?",
                    "Authentication Error",JOptionPane.ERROR_MESSAGE, AppMain.icoError);
			
            AppMain.enableLogging(false);
			
		}
	}

	private boolean retrieveTGFMResources() {

		try {
			LogManager.info("Querying service for available resources...");

//			Vector<String> params = new Vector<String>();
//			params.addElement(((GlobusGSSCredentialImpl)AppMain.defaultCredential).getX509Credential().getIdentity());
//			//params.addElement("/C=US/O=National Center for Supercomputing Applications/OU=People/CN=Nada Cagle");
			
			ClientResource clientResource = ServletUtil.getClient(ServletUtil.SYSTEMS_SERVICE_URL);
					SystemsResource client = clientResource.wrap(SystemsResource.class);
			Object response = client.retrieveResources();
			List<edu.utexas.tacc.wcs.filemanager.common.model.System> sites = new ArrayList<edu.utexas.tacc.wcs.filemanager.common.model.System>();
			if (response == null || ((List)response).isEmpty()) {
				return false;
			} else { 
				sites = (List<edu.utexas.tacc.wcs.filemanager.common.model.System>)((List)response).get(0);
			}
		
			// for these dynamically discovered resources, we automatically remove
			// them from the saved resource list if they are not returned from the
			// middleware. This keeps the core TeraGrid resources always in the
			// user's listing.
			List<FTPSettings> deleteList = new ArrayList<FTPSettings>();
			List<FTPSettings> deserializedSites = new ArrayList<FTPSettings>();
			for(edu.utexas.tacc.wcs.filemanager.common.model.System site: sites) {
				deserializedSites.add(new FTPSettings(site));
				LogManager.debug(site.toString());
			}
			
			for (FTPSettings savedSite: siteList) {
				// we don't touch user defined resources
				if (savedSite.userDefined) continue;
				boolean found = false;
				for (FTPSettings site : deserializedSites) {
					LogManager.debug(site.toString());
					if (site.host == null) continue;
					if (savedSite.equals(site)) {
						addSite(site);
						found = true;
						break;
					}
				}
				// if the previously listed dynamic site was not returned from the 
				// middleware, then delete it from the list.
				if (!found && !savedSite.name.equals(RESOURCE_NAME_LOCAL)) {
					deleteList.add(savedSite);
				}
			}
			
			siteList.removeAll(deleteList);
			
			for (edu.utexas.tacc.wcs.filemanager.common.model.System site: sites) {
				FTPSettings dynamicSite = new FTPSettings(site);
				if (dynamicSite.host == null) {
					LogManager.info("Skipping " + dynamicSite.name + " due to null hostname.");
					continue;
				}
				addSite(dynamicSite);
			}

			LogManager.info("Retrieved " + sites.size()
					+ " resources from the TeraGrid File Manager service");

		} catch (Exception e) {
			LogManager.error("Failed to query TGFM Service :" + e.getMessage());

			return false;

		}
		
		return true;

	}

	private boolean retrieveTGProfileResources() {

		// Fall back on the XSEDE Profile service instance if the TG Resource Discovery
		// Service is not available, or returns no resources.
		try {
			
			ProfileServiceClient client = new ProfileServiceClient(
					ConfigSettings.SERVICE_TG_USER_PROFILE, AppMain.ssoUsername, AppMain.ssoPassword);
			int count = 0;
			for (ComputeDTO system: client.getResources()) {
				if (system.getName().equalsIgnoreCase("NICS Kraken Cray XT5")) {
					assert(true);
				}
				if (system.getGridftpHostname() == null) {
					deleteSite(SiteUtil.convert(system));
				} else {
					addSite(SiteUtil.convert(system));
					count++;
				}
			}

			LogManager.info("Retrieved " + count + " resources from the TeraGrid User Profile service");

		} catch (Exception e) {
			
			throw new ResourceException(
					"TG resource listing temporarily unavailable.", e);
		}

		return true;
	}
	
	/**
	 * 
	 * @return ConfigOperation
	 */
	public static ConfigOperation getInstance() {
		if (instance == null) {
			instance = new ConfigOperation();
		}

		return instance;
	}

	public static void delete() {
		instance = null;
	}

	// initialize the saved site infomation
	public void refreshSites() throws Exception {
		
		siteList = loadSites();
		
		// try to pull them from the server first
		try {
			retrieveResources();
		} catch (Exception e) {
			LogManager.error("Error retrieving the sites!!", e);
		}
		
		for (FTPSettings savedSite: siteList) {
//			if (savedSite.name.equals(ConfigSettings.RESOURCE_NAME_TGSHARE)) {
//				savedSite.host = ConfigSettings.SERVICE_TGSHARE_SERVICE;
//				savedSite.userName = AppMain.ssoUsername;
//				savedSite.password = AppMain.ssoPassword;
//				addSite(savedSite);
//			}
			// will only update the retrieved sites. 
			modifySite(savedSite);
		}
	}

	public void refreshAuthn() throws IOException, CredentialException, GSSException {

		String proxy = null;
		try {
			proxy = AppMain.getApplet().getParameter("filebrowser.gsscredential");
		} catch (NullPointerException e) {
			LogManager.error("No proxy retreived from portal.");
		}

		GSSCredential cred = null;
		int remainingTime = 0;
		
		if (proxy != null && !proxy.equals("")) {
			proxy = proxy.replaceAll("KEY-----", "KEY-----\n");
			proxy = proxy.replaceAll("-----BEGIN", "\n-----BEGIN");
			proxy = proxy.replaceAll("-----END", "\n-----END");
			proxy = proxy.replaceAll("CERTIFICATE-----", "CERTIFICATE-----\n");

			LogManager.debug("Retrieved credential from portal " + proxy);

			// Serialize the proxy and save to disk
			ByteArrayInputStream bisProxyString = null;
			try
			{
				bisProxyString = new ByteArrayInputStream(proxy.getBytes());
				X509Credential globusCred = new X509Credential(bisProxyString);
				cred = new GlobusGSSCredentialImpl(globusCred, GSSCredential.INITIATE_AND_ACCEPT);
				remainingTime = cred.getRemainingLifetime();
				LogManager.debug("Successfully retrieved proxy valid for " + (remainingTime/60) + " minutes.");
			} 
			catch (Exception e) {
				LogManager.error("Failed to parse credential from portal.");
			} 
			finally {
				try {bisProxyString.close(); } catch (Exception e) {}
			}
			
			
		}

		if (cred == null || remainingTime == 0) 
		{
			LogManager.debug("Could not retrieve proxy from portlet.");
			
			try 
			{
				AppMain.ssoUsername = AppMain.getApplet().getParameter("filebrowser.ssousername");
				AppMain.ssoPassword = AppMain.getApplet().getParameter("filebrowser.ssocredential");
			} 
			catch (NullPointerException e) {
				LogManager.error("No sso credentials retrieved from portal.");
			}
			
			if (!StringUtils.isEmpty(AppMain.ssoUsername) && !StringUtils.isEmpty(AppMain.ssoPassword)) {
				try {
					AppMain.ssoUsername = URLDecoder.decode(AppMain.ssoUsername, "utf-8");
					AppMain.ssoPassword = URLDecoder.decode(AppMain.ssoPassword, "utf-8");
					// pull a community proxy from the tacc myproxy server
					MyProxy myproxy = new MyProxy(ConfigSettings.SERVICE_MYPROXY_SERVER, new Integer(ConfigSettings.SERVICE_MYPROXY_PORT).intValue());
					cred = myproxy.get(AppMain.ssoUsername, AppMain.ssoPassword, 7200);
					LogManager.debug("Retrieved user credential from TeraGrid myproxy " + ConfigSettings.SERVICE_MYPROXY_SERVER);
					
				} catch (Exception e) {
					LogManager.error("Failed to retrieve proxy using portlet sso credentials",e);
					LogManager.debug("Trying myproxy disk.");
					
					cred = GridFTP.authorize();
				}
			} else {
				LogManager.debug("Trying MyProxy.");
				
				cred = GridFTP.authorize();
			}
		}

		// Set the user's credential for use throughout the life of this
		// application
		LogManager.debug("Loaded credential for DN " + ((GlobusGSSCredentialImpl)cred).getX509Credential().getIdentity() + " at startup.");
		
		if (!AppMain.isApplet()) {
			FileOutputStream out = null;
			try
			{
				out = new FileOutputStream(getConfigValue("proxy"));
				((GlobusGSSCredentialImpl)cred).getX509Credential().save(out);
			} 
			catch (CertificateEncodingException e) {
				throw new CredentialException("Failed to load credential from disk.", e);
			}
			finally {
				try {out.close();} catch (Exception e) {}
			}
		}
		
		AppMain.defaultCredential = cred;
		setConfigValue("dn", ((GlobusGSSCredentialImpl)cred).getX509Credential().getIdentity());
	}

	/**
	 * get a setting configure value
	 * 
	 * @param sKey
	 *            String
	 * @return String
	 */
	public String getConfigValue(String sKey) {
		if (sKey.equals("myproxy_username")) {
			LogManager.debug("MyProxy username: " + settings.getProperty(sKey));
		}
		String value = (String) this.settings.get(sKey);
		if (sKey.equals("dn")) {
			value = value.replaceAll(",", "/");
			if (value != null && !value.equals("") && value.charAt(0) != '/')
				value = "/" + value;
		}
		return value;
	}

	/**
	 * set a configure value
	 * 
	 * @param sKey
	 *            String
	 * @param sValue
	 *            String
	 */
	public void setConfigValue(String sKey, String sValue) {

		this.settings.put(sKey, sValue);

		saveDefaultSettings();
	}

	/**
	 * get all saved site list
	 * 
	 * @return List
	 * @throws Exception
	 */
	public List<FTPSettings> getSites() {
		return this.siteList;
	}
	
	public void setSites(List<FTPSettings> newSites) {
		this.siteList = newSites;
		saveSites();
	}

	public String getSiteName(String hostname) {
		for (FTPSettings site : this.siteList) {
			if (site.host.equals(hostname)) {
				return site.name;
			}
		}
		return null;
	}

	public String[] getSiteNames() {
		String[] names = new String[this.siteList.size()];
		int i = 0;
		for (FTPSettings site : this.siteList) {
			names[i] = site.host;
			i++;
		}

		return names;
	}

	public List<FTPSettings> getUnlistedSites() {
		ArrayList<FTPSettings> sites = new ArrayList<FTPSettings>();
		for (FTPSettings site : (List<FTPSettings>) siteList) {
			if (!site.listed) {
				sites.add(site);
			}
		}

		return sites;
	}
	
	/**
	 * get a ftp site setting from the saved ftp list
	 * 
	 * @param siteName
	 *            String -
	 * @return FTPSettings -
	 */
	public FTPSettings getSiteByName(String siteName) {
		for (FTPSettings currentSite: siteList) {
			if (currentSite.name.equals(siteName)) {
				return currentSite;
			}
		}

		return null;
	}

	/**
	 * get a ftp site setting from the saved ftp list
	 * 
	 * @param siteName
	 *            String -
	 * @return FTPSettings -
	 */
	public FTPSettings getSiteByHostame(String hostname) {
		if (StringUtils.isEmpty(hostname) || 
				StringUtils.equalsIgnoreCase(hostname,  "localhost.localdomain")) 
		{
			return getSiteByName("Local");
		}
		
		for (FTPSettings currentSite: siteList) {
			if (currentSite.host.equals(hostname)) {
				return currentSite;
			}
		}

		return null;
	}
	/**
	 * add a ftp site to the config file
	 * 
	 * @param site
	 *            FTPSettings -- the site to add
	 * @return boolean -- addition result
	 * @throws Exception
	 */
	public void addSite(FTPSettings site) {
		
		// TODO: delete duplicate sites.  Right now there are too many
		// artificats in the sites.xml file and it's cluttering the
		// resource listing seen by the user. Many times users cannot
		// delete the garbage resources because they were added by
		// gpir or tg in a previous run and the name changed.
		
		// LogManager.debug("Reviewing site " + site.toString());
		int index = this.siteList.indexOf(site);
		if (index > -1) {
			// use the current name of the site retrieved from the ws call
			this.siteList.get(index).name = site.name;
			this.siteList.get(index).host = site.host;
			this.siteList.get(index).userName = site.userName;
			this.siteList.get(index).password = site.password;
			this.siteList.get(index).resource = site.resource;
			this.siteList.get(index).zone = site.zone;
			this.siteList.get(index).available = site.available;
		} else {
			this.siteList.add(site);
		}
		
		saveSites();
	}	

	public void addBookmark(String hostname, Bookmark bookmark) {

		if (bookmarksTable.containsKey(hostname)) {
			bookmarksTable.get(hostname).add(bookmark);
		} else {
			List<Bookmark> bookmarks = new ArrayList<Bookmark>();
			bookmarks.add(bookmark);
			bookmarksTable.put(hostname, bookmarks);
		}
		
		saveBookmarks();
	}

	public void addBookmarks(String hostname, List<Bookmark> bookmarks) {

		if (bookmarksTable.containsKey(hostname)) {
			bookmarksTable.get(hostname).addAll(bookmarks);
		} else {
			bookmarksTable.put(hostname, bookmarks);
		}
		saveBookmarks();
	}

	public void updateBookmarks(String hostname, List<Bookmark> bookmarks) {

		bookmarksTable.get(hostname).clear();
		

		if (bookmarksTable.containsKey(hostname)) {
			List<Bookmark> siteBookmarks = bookmarksTable.get(hostname);
			for (Bookmark bookmark : bookmarks) {
				// if the bookmark entry is already in there
				if (siteBookmarks.contains(bookmark)) {
					Bookmark existingBookmark = siteBookmarks.get(siteBookmarks
							.indexOf(bookmark));
					
					// now update the table entry. again leaving creation
					// date alone
					try {
						existingBookmark.setName(bookmark.getName());
						existingBookmark.setPath(bookmark.getPath());
					} catch (IOException e) {
						LogManager.error("Failed to save bookmark "
								+ bookmark.toString(), e);
						return;
					}
				
				} else {
					// if the host is there, but the bookmark isn't, add the
					// bookmark.
					addBookmark(hostname, bookmark);
				}
			}
		} else {
			addBookmarks(hostname, bookmarks);
		}
		
		saveBookmarks();
	}

	public void updateBookmark(String hostname, Bookmark bookmark) {

		// if the host has an entry already
		if (bookmarksTable.containsKey(hostname)) {
			List<Bookmark> siteBookmarks = bookmarksTable.get(hostname);
			// if the bookmark entry is already in there
			if (siteBookmarks.contains(bookmark)) {
				Bookmark existingBookmark = siteBookmarks.get(siteBookmarks
						.indexOf(bookmark));
				
				// now update the table entry. again leaving creation
				// date alone
				try {
					existingBookmark.setName(bookmark.getName());
					existingBookmark.setPath(bookmark.getPath());
				} catch (IOException e) {
					LogManager.error("Failed to save bookmark "
							+ bookmark.toString(), e);
					return;
				}
			
			} else {
				// if the host is there, but the bookmark isn't, add the
				// bookmark.
				addBookmark(hostname, bookmark);
			}
				
		} else {
			// if the host isn't there, add the bookmark to a new host entry
			addBookmark(hostname, bookmark);
		}
		
		saveBookmarks();
	}

	public boolean deleteBookmark(String hostname, Bookmark bookmark) {

		if (bookmarksTable.containsKey(hostname)) {
			if (bookmarksTable.get(hostname).contains(bookmark)) {
				bookmarksTable.get(hostname).remove(bookmark);
			}
		}

		return saveBookmarks();

	}

	public List<Bookmark> getBookmarks(String hostname) {
		return bookmarksTable.get(hostname);
	}

	/**
	 * Delete an FTPSetting from the saved site list.
	 * 
	 * @param site
	 *            FTPSettings -- site to delete
	 * @return boolean -- delete result. true if succeeded
	 * @throws Exception
	 */
	public boolean deleteSite(FTPSettings site) {

		// remove the old site from the list
		this.siteList.remove(site);

		// delete the old site from the xml and the file
		return saveSites();

	}

	/**
	 * modify a ftp site in the config file
	 * 
	 * @param modifiedSite
	 *            FTPSettings -- the updated site setting
	 * @return boolean -- modification result. true if succeeded
	 * @throws Exception
	 */
	public boolean modifySite(FTPSettings modifiedSite) {

		if (siteList.contains(modifiedSite)) {
			int index = siteList.indexOf(modifiedSite);
			
			siteList.get(index).protocol = modifiedSite.protocol;
			siteList.get(index).passiveMode = modifiedSite.passiveMode;
			siteList.get(index).connRetry = modifiedSite.connRetry;
			siteList.get(index).connDelay = modifiedSite.connDelay;
			siteList.get(index).connParallel = modifiedSite.connParallel;
			siteList.get(index).connKeepAlive = modifiedSite.connKeepAlive;
			siteList.get(index).available = modifiedSite.available;
			siteList.get(index).connMaxNum = modifiedSite.connMaxNum;
			siteList.get(index).loginMode = modifiedSite.loginMode;
			siteList.get(index).userName = modifiedSite.userName;
			siteList.get(index).bufferSize = modifiedSite.bufferSize;
			siteList.get(index).password = modifiedSite.password;
			siteList.get(index).resource = modifiedSite.resource;
			siteList.get(index).zone = modifiedSite.zone;
			siteList.get(index).hostType = modifiedSite.hostType;
			siteList.get(index).listed = modifiedSite.listed;
			siteList.get(index).showHidden = modifiedSite.showHidden;
			siteList.get(index).stripeTransfers = modifiedSite.stripeTransfers;
			siteList.get(index).maxSearchDepth = modifiedSite.maxSearchDepth;
			siteList.get(index).maxSearchResults = modifiedSite.maxSearchResults;
			siteList.get(index).userDefined = modifiedSite.userDefined;
		} else {
			siteList.add(modifiedSite);
			LogManager.info("Added site " + modifiedSite.toString() + " to site list.");
		}
		
		return saveSites();
	}
	
	@SuppressWarnings("unused")
	private boolean isModified(FTPSettings originalSite,
			FTPSettings modifiedSite) {
		if (originalSite == null || modifiedSite == null) {
			return true;
		}
		if (!originalSite.name.equals(modifiedSite.name)) {
			return true;
		}
		if (!originalSite.host.equals(modifiedSite.host)) {
			return true;
		}
		if (!originalSite.sshHost.equals(modifiedSite.sshHost)) {
			return true;
		}
		if (originalSite.filePort != modifiedSite.filePort) {
			return true;
		}
		if (originalSite.protocol != modifiedSite.protocol) {
			return true;
		}
		if (originalSite.available != modifiedSite.available) {
			return true;
		}
		if (originalSite.passiveMode != modifiedSite.passiveMode) {
			return true;
		}
		if (originalSite.connRetry != modifiedSite.connRetry) {
			return true;
		}
		if (originalSite.connDelay != modifiedSite.connDelay) {
			return true;
		}
		if (originalSite.connParallel != modifiedSite.connParallel) {
			return true;
		}
		if (originalSite.connKeepAlive != modifiedSite.connKeepAlive) {
			return true;
		}
		if (originalSite.loginMode != modifiedSite.loginMode) {
			return true;
		}
		if (originalSite.userName != modifiedSite.userName) {
			return true;
		}
		if (originalSite.bufferSize != modifiedSite.bufferSize) {
			return true;
		}
		if (originalSite.password != modifiedSite.password) {
			return true;
		}
		if (originalSite.resource != modifiedSite.resource) {
			return true;
		}
		if (originalSite.zone != modifiedSite.zone) {
			return true;
		}
		if (!originalSite.hostType.equals(modifiedSite.hostType)) {
			return true;
		}
		if (originalSite.listed != modifiedSite.listed) {
			return true;
		}
		if (originalSite.showHidden != modifiedSite.showHidden) {
			return true;
		}
		if (originalSite.stripeTransfers != modifiedSite.stripeTransfers) {
			return true;
		}
		if (originalSite.maxSearchDepth != modifiedSite.maxSearchDepth) {
			return true;
		}
		if (originalSite.maxSearchResults != modifiedSite.maxSearchResults) {
			return true;
		}
		return false;
	}

	
	/**
	 * Load the default settings from disk or init new ones if not present.
	 * 
	 * @return true on success.
	 */
	private boolean loadDefaultSettings() {
		File settingsFile = new File(getDataDir() + DEFAULT_CONFIGURE_FILE);
		if (settingsFile.exists()) {
			FileInputStream in = null;
			try {
				in = new FileInputStream(settingsFile);
				settings.loadFromXML(in);
				verifySettings();
				return true;
			} catch (Exception e) {				
				LogManager.error("Unable to load configuration file "
						+ DEFAULT_CONFIGURE_FILE + ". Using default settings.", e);
				LogManager.info("Initializing default preferences...");
				initDefaultSettings();
				saveDefaultSettings();
				LogManager.info("Finished initializing default user preferences.");
				
				return true;
			} finally {
				if (in != null) {
					try {
						in.close();
					} catch (IOException e) {}
				}
			}
			
		} else {
			LogManager.error("Unable to load configuration file "
					+ DEFAULT_CONFIGURE_FILE + ". Using default settings.");
			LogManager.info("Initializing default preferences...");
			initDefaultSettings();
			saveDefaultSettings();
			LogManager.info("Finished initializing default user preferences.");
			
			return true;
		}

	}
	
	private void verifySettings() {
		int i=0;
		for (String key: keys) {
			if (!settings.containsKey(key)) {
				settings.put(key, values[i]);
			}
			i++;
		}
	}
	
	/**
	 * Settings are implemented as a java Properties object,
	 * so just use the native store method to save the default properties.
	 * 
	 * @return
	 */
	private boolean saveDefaultSettings() {
		if (settings.get("myproxy_username") == null || settings.get("myproxy_username").equals("")) {
			LogManager.debug("myproxy_username is now null!!");
		}
		try {
			File settingsFile = new File(getDataDir() + DEFAULT_CONFIGURE_FILE);
			
			if (!settingsFile.exists()) {
				settingsFile.createNewFile();
			}

			FileOutputStream out = new FileOutputStream(settingsFile);
		
			settings.storeToXML(out, "These are the default configuration settings for " +
					"\nthe TeraGrid File Manager.");
			out.close();
			
		} catch (Exception e) {
			AppMain
					.getLogger()
					.error(
							SGGCResourceBundle
									.getResourceString(ResourceName.KEY_ERROR_CONFIGOPERATION_WRITEFILE),e);
			return false;
		}

		return true;
	}
	
	/**
	 * Load the saved sites from disk or init new ones if not present.
	 * 
	 * @return true on success.
	 */ 
	
	@SuppressWarnings({ "unchecked" })
	private List<FTPSettings> loadSites() {
		File sitesFile = new File(getDataDir() + SITES_FILE);
		List<FTPSettings> sites = new ArrayList<FTPSettings>();
		if (sitesFile.exists()) {
			try {
				sites = (ArrayList<FTPSettings>)xstream.fromXML(org.apache.commons.io.FileUtils.readFileToString(sitesFile));
			} catch (Exception e) {				
				LogManager.error("Failed to load sites file",e);
			} 
		} 
		else 
		{
			try {
				sitesFile.createNewFile();
			} catch (IOException e) {
				LogManager.error("Failed to create sites file",e);
			}
		}
		
		return sites;
	}
	
	private boolean saveSites() {
		try {
			File sitesFile = new File(getDataDir() + SITES_FILE);
			
			if (!sitesFile.exists()) {
				sitesFile.createNewFile();
			}

			FileOutputStream out = new FileOutputStream(sitesFile);
		
			FileWriter writer = new FileWriter(sitesFile);
			writer.write(xstream.toXML(getDereferenceSiteList()));
			writer.close();
			
			out.close();
			
		} catch (Exception e) {
			AppMain.getLogger().error(
				SGGCResourceBundle.getResourceString(ResourceName.KEY_ERROR_CONFIGOPERATION_WRITEFILE),e);
			return false;
		}

		return true;
	}
	
	/**
	 * We dereference the connectin pool and the parent properties from the site list
	 * to avoid having to serialize the entire app. Same problem as with serializing
	 * hydrated data objects.
	 * 
	 * @return
	 */
	private ArrayList<FTPSettings> getDereferenceSiteList() {
		ArrayList<FTPSettings> dereferencedSites = new ArrayList<FTPSettings>();
		
		for(FTPSettings site: siteList) {
			FTPSettings dereferencedSite = new FTPSettings(site.name,site.filePort,site.protocol);
			dereferencedSite.host = site.host;
			dereferencedSite.sshHost = site.sshHost;
			dereferencedSite.protocol = site.protocol;
			dereferencedSite.available = site.available;
			dereferencedSite.passiveMode = site.passiveMode;
			dereferencedSite.connRetry = site.connRetry;
			dereferencedSite.connDelay = site.connDelay;
			dereferencedSite.connParallel = site.connParallel;
			dereferencedSite.connKeepAlive = site.connKeepAlive;
			dereferencedSite.connMaxNum = site.connMaxNum;
			dereferencedSite.loginMode = site.loginMode;
			dereferencedSite.userName = site.userName;
			dereferencedSite.bufferSize = site.bufferSize;
			dereferencedSite.password = site.password;
			dereferencedSite.resource = site.resource;
			dereferencedSite.zone = site.zone;
			dereferencedSite.hostType = site.hostType;
			dereferencedSite.listed = site.listed;
			dereferencedSite.showHidden = site.showHidden;
			dereferencedSite.stripeTransfers = site.stripeTransfers;
			dereferencedSite.maxSearchDepth = site.maxSearchDepth;
			dereferencedSite.maxSearchResults = site.maxSearchResults;
			dereferencedSite.hostType = site.hostType;
			dereferencedSite.maxSearchResults = site.maxSearchResults;
			dereferencedSite.userDefined = site.userDefined;
			dereferencedSites.add(dereferencedSite);
		}
		
		return dereferencedSites;
	}
	/**
	 * Load the saved sites from disk or init new ones if not present.
	 * 
	 * @return true on success.
	 */
	@SuppressWarnings({ "unchecked" })
	private boolean loadBookmarks() {
		File bookmarksFile = new File(getDataDir() + BOOKMARKS_FILE);
		if (bookmarksFile.exists()) {
			FileInputStream in = null;
			try {
				in = new FileInputStream(bookmarksFile);
				if (bookmarksFile.length() != 0) {
					bookmarksTable = (Hashtable<String, List<Bookmark>>)xstream.fromXML(in);
				}
			} catch (Exception e) {				
				LogManager.error("Failed to load bookmarks file",e);
				return false;
			} finally {
				if (in != null) {
					try {
						in.close();
					} catch (IOException e) {}
				}
			}
			
		} else {
			try {
				bookmarksFile.createNewFile();
			} catch (IOException e) {
				LogManager.error("Failed to create bookmarks file",e);
			}
		}
		
		return true;

	}
	
	private boolean saveBookmarks() {
		try {
			File bookmarksFile = new File(getDataDir() + BOOKMARKS_FILE);
			
			if (!bookmarksFile.exists()) {
				bookmarksFile.createNewFile();
			}

			FileOutputStream out = new FileOutputStream(bookmarksFile);
			
			FileWriter writer = new FileWriter(bookmarksFile);
			writer.write(xstream.toXML(bookmarksTable));
			writer.close();
			
			out.close();
			
		} catch (Exception e) {
			AppMain
					.getLogger()
					.error(
							SGGCResourceBundle
									.getResourceString(ResourceName.KEY_ERROR_CONFIGOPERATION_WRITEFILE),e );
			return false;
		}

		return true;
	}

	public static String getApplicationHome() {
		
		if (appHome != null) {
			return appHome;
		}

		try 
		{
			String home = System.getProperty("tgfm.home");
			if (home == null) {
				URL url = ClassLoader.getSystemResource("sggc.properties");
				if (url.getProtocol().startsWith("htt")
						|| url.getProtocol().startsWith("jar")) {
					home = System.getProperty("user.home");
					return home;
				} 
				if (os().equals("windows")) {
					home = url.getFile();
					if (home.indexOf("file") != -1)
						home = url.getPath().substring(0);
					else
						home = home.substring(0);

					home = home.replaceAll("%20", " ");

				} else if (os().equals("osx")) {
					home = url.getFile();
				} else {
					home = url.getFile();
				}

				if (home.indexOf("build") != -1) {
					home = home.substring(0, home.indexOf("build"));
				} else if (home.indexOf("bin") != -1) {
					home = home.substring(0, home.indexOf("bin"));
				} else if (home.indexOf("src") != -1) {
					home = home.substring(0, home.indexOf("src"));
				} else if (home.indexOf("lib") != -1) {
					home = home.substring(0, home.indexOf("lib"));
				} else if (home.indexOf("!") != -1) {
					home = home.substring(0, home.lastIndexOf("!"));
					System.out.println("jar path is: " + home);
					home = home.substring(0, home.lastIndexOf("/"));
					System.out.println("jar revised path is: " + home);
				} else {
					home = home.substring(0, home.lastIndexOf(File.separator));
				}
				home = home.replace('/', File.separator.charAt(0));
			}

			if (!home.endsWith(File.separator)) {
				home = home + File.separator;
			}
			return home;
		} catch (Exception e) { // Non Possible'
			LogManager.error("Error locating application home", e);
			return "";
		}
	}

	public static String getUserHome() {
		return System.getProperty("user.home") + File.separator;
	}
	
	public static String getKeystoreDir() {
		String certDir = getUserHome() + "tgup_filemanager"
				+ File.separator + "security";
		
		File dir = new File(certDir);
		if (!dir.exists()) {
			dir.mkdirs();
		}

		return certDir + File.separator;
	}

	/**
	 * @return the absolute path to the TGFM directory
	 */
	public String getDataDir() {
		String deployDir = getUserHome() + "tgup_filemanager"
				+ File.separator + "filebrowser";

		File dir = new File(deployDir);
		if (!dir.exists()) {
			dir.mkdirs();
		}

//		String oldDir = getConfigValue("deploy_dir");
//		if ((oldDir == null || oldDir.equals(""))) {
//			setConfigValue("deploy_dir", deployDir);
//		} else {
//			deployDir = getConfigValue("deploy_dir");
//		}

//		dir = new File(deployDir);
//		if (!dir.exists()) {
//			dir.mkdirs();
//		}

		return deployDir + File.separator;
	}

	/**
	 * @return the absolute path to the TGFM certificate directory
	 */
	public static String getCertificateDir() {
		String certDir = getUserHome() + "tgup_filemanager"
				+ File.separator + "certificates";

//		String oldDir = getConfigValue("certificates");
//		if (oldDir == null || oldDir.equals("")) {
//			setConfigValue("certificates", certDir);
//		} else {
//			certDir = getConfigValue("certificates");
//		}

		File dir = new File(certDir);
		if (!dir.exists()) {
			dir.mkdirs();
		}

		return certDir + File.separator;
	}

	/**
	 * @return the absolute path to the TGFM certificate directory
	 */
	public String getProxyDir() {
		String proxyDir = getUserHome() + "tgup_filemanager" + File.separator
				+ "proxies";
//		String oldDir = getConfigValue("proxy");
//		if (oldDir == null || oldDir.equals("")) {
//			setConfigValue("proxy", proxyDir + File.separator + "x509up_u501");
//		} else {
//			proxyDir = getConfigValue("proxy");
//			proxyDir = proxyDir.substring(0, proxyDir
//					.lastIndexOf(File.separator));
//		}

		File dir = new File(proxyDir);
		if (!dir.exists()) {
			dir.mkdirs();
		}

		return proxyDir + File.separator;
	}

	/**
	 * @return the absolute path to the TGFM helpset directory
	 */
	public String getHelpDir() {
		String helpDir = getUserHome() + "tgup_filemanager" + File.separator
				+ "help";
//		String oldDir = getConfigValue("help_dir");
//		if (oldDir == null || oldDir.equals("")) {
//			setConfigValue("help_dir", helpDir);
//		} else {
//			helpDir = getConfigValue("help_dir");
//		}

		File dir = new File(helpDir);
		if (!dir.exists()) {
			dir.mkdirs();
		}

		return helpDir + File.separator;
	}

	/**
	 * @return the absolute path to the user's .globus directory
	 */
	public String getGlobusDir() {
		String proxyDir = getUserHome() + ".globus";

		File dir = new File(proxyDir);
		if (!dir.exists()) {
			dir.mkdirs();
		}

		return proxyDir + File.separator;
	}

	public final static boolean isWindows() {
		if (os().equals("windows"))
			return true;
		return false;
	}

	/**
	 * Returns the machines operating system. This returns a single word which
	 * identifies the particular operating system. The identifier is returned in
	 * lower case so it can be used to identify directories of where various
	 * things are stored for different platforms e.g. </p>
	 * <p>
	 * <ol>
	 * <li>Windows 95/NT - <i>windows</i> is returned
	 * <li>Solaris - <i>solaris</i> is returned
	 * <li>IRIX - <i>irix</i> is returned
	 * <li>DEC - <i>dec</i> is returned
	 * <li>LINUX - <i>linux</i> is returned</li>
	 */
	public final static String os() {
		/*
		 * if (GUIEnv.isAnApplet()) // Doesn't allow access of properties in JDK
		 * 1.3 { return "windows"; }
		 */

		String os = System.getProperty("os.name");

		if (os.startsWith("Windows")) {
			return "windows";
		}

		if ((os.equals("SunOS")) || (os.equals("Solaris"))) {
			return "solaris";
		}

		if (os.equals("Digital Unix")) {
			return "dec";
		}

		if (os.equals("Linux")) {
			return "linux";
		}

		if ((os.equals("Irix")) || (os.equals("IRIX"))) {
			return "irix";
		}

		if (os.equals("Mac OS X")) {
			return "osx";
		}

		return "Not Recognised";
	}

	public static String getGatAdaptorPath() {

		String home = null;

		try {
			home = Class.forName("org.gridlab.gat.GAT")
					.getResource("GAT.class").getFile();

			// LogManager.debug("Application home is: " + home);

			if (home.indexOf("file") != -1) {
				home = home.substring(5);
			} else {
				home = home.substring(1);
			}

			home = home.substring(0, home.indexOf("GAT."));
			home = home.replaceAll("%20", " ");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		return home;
	}

	public static void setLoggingEnabled(boolean doLog) {
		enableLogging = doLog;
	}

	public static boolean isLoggingEnabled() {
		// LogManager.debug("Logging is " + (enableLogging?"":"not") +
		// " enabled.");
		return enableLogging;
	}
	
	public void deployRemoteCATarball(String url) {
		try {
			// Download and unpack current cert zipped archive
			URL caUrl = new URL(url);
			
			File certDir = new File(getCertificateDir());
			
			String dest = certDir.getParent() + File.separator
					+ "ca.tar.gz";

			FileUtils.download(caUrl, dest);

			LogManager.info("Downloaded trusted cert archive " + caUrl + " to " + dest);
			
			String packedFolder = FileUtils.getPackedFolderName(dest);
			
			if (packedFolder != null && packedFolder.equals("certificates/")) {
				FileUtils.unpackTGZ(dest, certDir.getParent());
			} else {
				FileUtils.unpackTGZ(dest, certDir);
			}
			
			FileUtils.delete(dest);

		} catch (Exception e) {
			LogManager.error("Failed to unpack certificate files.", e);
		}
	}
	
	private void readVersionInformation() 
	{
		InputStream stream = null;
		try 
		{
			JarFile tgfmJar = null;
			URL jarname = Class.forName(
				"org.teragrid.portal.filebrowser.applet.ConfigOperation")
				.getResource("ConfigOperation.class");
			if (jarname.getProtocol().startsWith("jar")
					|| jarname.getProtocol().startsWith("htt")) 
			{
				JarURLConnection c = (JarURLConnection) jarname.openConnection();
				tgfmJar = c.getJarFile();
				stream = tgfmJar.getInputStream(tgfmJar.getEntry("META-INF/MANIFEST.MF"));
				Manifest manifest = new Manifest(stream);
				Attributes attributes = manifest.getMainAttributes();
				for(Object attributeName: attributes.keySet()) 
				{
					if (((Attributes.Name)attributeName).toString().equals(("Implementation-Version"))) {
						ConfigSettings.SOFTWARE_VERSION = attributes.getValue("Implementation-Version");
					} else if (((Attributes.Name)attributeName).toString().equals(("Built-Date"))) {
						ConfigSettings.SOFTWARE_BUILD_DATE = attributes.getValue("Built-Date");
					} 
					
					LogManager.debug(attributeName + ": " + attributes.getValue((Attributes.Name)attributeName) + "\n");
				}
			}
			else
			{
				ConfigSettings.SOFTWARE_VERSION = "dev";
				ConfigSettings.SOFTWARE_BUILD_DATE = new Date().toString();
			}
		} 
		catch (Exception e) 
		{
			LogManager.error("Failed to retrieve version information.", e);
		} 
		finally 
		{
			try { stream.close(); } catch (Exception e) {}
		}
	}

}
