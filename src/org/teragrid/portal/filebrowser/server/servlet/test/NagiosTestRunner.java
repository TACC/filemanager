/* 
 * Created on Jul 9, 2008
 * 
 * Developed by: Rion Dooley - dooley [at] tacc [dot] utexas [dot] edu
 * 				 TACC, Texas Advanced Computing Center
 * 
 * https://www.tacc.utexas.edu/
 */

package org.teragrid.portal.filebrowser.server.servlet.test;

import java.net.ConnectException;
import java.util.Arrays;
import java.util.Vector;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.xmlrpc.XmlRpcException;
import org.teragrid.portal.filebrowser.applet.ConfigSettings;
import org.teragrid.portal.filebrowser.applet.transfer.FTPSettings;
import org.teragrid.portal.filebrowser.applet.util.LogManager;
import org.teragrid.portal.filebrowser.applet.util.ServletUtil;
import org.teragrid.portal.filebrowser.server.servlet.exception.InfrastructureException;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * Test class to check that the service is up and functioning
 * properly.  This is not part of the JUnit tests because it is
 * not meant to be an assurance test.  It is simply to see that
 * the service is running and responsive.
 * 
 * @author Rion Dooley < dooley [at] tacc [dot] utexas [dot] edu >
 *
 */
@SuppressWarnings({"unchecked","unused"})
public class NagiosTestRunner {

    private static String DN = "";
    
    private static final int SUCCESS = 0;
    private static final int SERVICE_REJECT_CONNECTION = 1;
    private static final int SERVICE_NOT_RESPONSIVE = 2;
    private static final int SERVICE_DATABASE_DOWN = 3;
    private static final int NO_DATABASE_CONNECTION = 4;
    private static final int SERVICE_UNKNOWN_FAILURE = 5;
    
    private static XStream xstream = new XStream(new DomDriver());

	private String defaultFormat = "MMM d, yyyy h:mm:ss a";

	private String[] acceptableFormats = new String[] { "MM/dd/yyyy HH:mm:ss",
			"yyyy-MM-dd HH:mm:ss.S z", "yyyy-MM-dd HH:mm:ss.S a",
			"yyyy-MM-dd HH:mm:ssz", "yyyy-MM-dd HH:mm:ss z", // JDK 1.3 needs
																// both prev
																// versions
			"yyyy-MM-dd HH:mm:ssa" }; // backwards compatability
	
    /**
     * @param args
     */
    public static void main(String[] args) {
        
        Logger.getRootLogger().setLevel(Level.OFF);
        
        DN = "/C=US/O=UTAustin/OU=TACC/CN=Rion Dooley/UID=dooley";
        
        testService();
        
    }
    
    private static void testService() {
        try {
            
            Vector params = new Vector();
            params.addElement(DN);
         
//            System.out.println("Connecting to " + ConfigSettings.SERVICE_TGFM_SERVLET);
            Object sites = ServletUtil.getClient().execute(
                 ServletUtil.GET_RESOURCES,params);
//            for(Object serializedSite: Arrays.asList((Object[]) sites)) {
//				FTPSettings site = (FTPSettings) xstream.fromXML((String) serializedSite);
//				System.out.println(site.toString());
//			}
            System.out.println(SUCCESS);
            
        } catch (XmlRpcException e) {
            if (e.getCause() instanceof InfrastructureException) {
                System.out.println(SERVICE_DATABASE_DOWN);
            } else if (e.getCause() instanceof ConnectException) {
                System.out.println(SERVICE_REJECT_CONNECTION);
            } else {
                System.out.println(SERVICE_UNKNOWN_FAILURE);
            }
//            e.printStackTrace();
        } catch (Exception e) {
            System.out.println(SERVICE_UNKNOWN_FAILURE);
//            e.printStackTrace();
        }
    }
}
