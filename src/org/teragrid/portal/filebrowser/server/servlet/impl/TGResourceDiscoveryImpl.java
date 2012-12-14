/* 
 * Created on Dec 16, 2007
 * 
 * Developed by: Rion Dooley - dooley [at] tacc [dot] utexas [dot] edu
 * 				 TACC, Texas Advanced Computing Center
 * 
 * https://www.tacc.utexas.edu/
 */

package org.teragrid.portal.filebrowser.server.servlet.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.teragrid.portal.filebrowser.applet.exception.ResourceException;
import org.teragrid.portal.filebrowser.applet.transfer.FTPSettings;
import org.teragrid.portal.filebrowser.server.servlet.Settings;
import org.teragrid.portal.filebrowser.server.servlet.TGResourceDiscovery;
import org.teragrid.portal.filebrowser.server.servlet.dao.TeraGridSystemDAO;
import org.teragrid.portal.filebrowser.server.servlet.dao.UserDAO;
import org.teragrid.portal.filebrowser.server.servlet.exception.AuthenticationException;
import org.teragrid.portal.filebrowser.server.servlet.exception.PermissionException;
import org.teragrid.portal.filebrowser.server.servlet.model.resource.Speedpage;
import org.teragrid.portal.filebrowser.server.servlet.model.resource.TeraGridSystem;
import org.teragrid.portal.filebrowser.server.servlet.model.resource.TeraGridSystemResolver;
import org.teragrid.portal.filebrowser.server.servlet.model.user.User;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.basic.DateConverter;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * User-specific TG resource discovery class. 
 * 
 * @author Rion Dooley < dooley [at] tacc [dot] utexas [dot] edu >
 *
 */
public class TGResourceDiscoveryImpl implements TGResourceDiscovery {

	private static final Logger logger = Logger.getLogger(TGFileTransferHistoryImpl.class);
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
    
    public TGResourceDiscoveryImpl() {}
    
    /* (non-Javadoc)
     * @see org.teragrid.portal.filebrowser.server.servlet.TGResourceDiscovery#retrieveResources(java.lang.String)
     */
    public String[] retrieveResources(String dn) throws IOException, ResourceException, PermissionException {
        
        if (!isValid(dn))
            throw new AuthenticationException("Please supply a valid DN " + dn);
        
        ArrayList<String> sites = new ArrayList<String>();
        
//        dn = "/C=US/O=UTAustin/OU=TACC/CN=Rion Dooley/UID=dooley";
        
        // Verify that the user's DN is valid
        User user = UserDAO.loadUserByDN(dn);
        
        logger.info("Retrieving resources for " + user.getUsername() + "...");
        
        // Get the resources on which the user has a valid account and resolve
        // the hostname issues caused by inaccuracies in the TGCDB
        List<TeraGridSystem> teraGridSystems = 
            TeraGridSystemResolver.resolveResources(
                    TeraGridSystemDAO.findSystemAccounts(user));
        
        logger.info("Found " + teraGridSystems.size() + " resources for user " + user.getUsername());
        
//        TeraGridSystemResolver.resolveResourceDowntimes(teraGridSystems);
//        logger.info("There are " + teraGridSystems.size() + " resources currently available for user " + user.getUsername());
        
        // Now serialize the resources and return them to the client.
        for(TeraGridSystem system: teraGridSystems) {
            
            logger.debug("Adding resource " + system.toString() + 
                " to list of available resources.");

            FTPSettings ftpServer = system.toFtpSetting();

            sites.add(xstream.toXML(ftpServer));
            
        }
        
        return sites.toArray(new String[sites.size()]);
    }
    
    private boolean isValid(String s) {
        return !(s==null || s.equals(""));
    }

	public String getBandwidth(String from, String to) {
		try {
			Speedpage speedpage = new Speedpage(Settings.SPEEDPAGE_SERVER);
			Double result = speedpage.getMeasuredBandwidth(from, to).getMeasurement();
			return result.doubleValue() + "";
			
		} catch (Exception e) {
			logger.error("Failed to resolve bandwidth",e);
			return "-1";
		}
	}
    

}
