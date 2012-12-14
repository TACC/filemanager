/* 
 * Created on Dec 11, 2007
 * 
 * Developed by: Rion Dooley - dooley [at] tacc [dot] utexas [dot] edu
 * 				 TACC, Texas Advanced Computing Center
 * 
 * https://www.tacc.utexas.edu/
 */

package org.teragrid.portal.filebrowser.server.servlet;

import java.util.Properties;

public class Settings {
    public static Properties props = new Properties();
    
    static {
        try {
            props.load(Settings.class.getClassLoader().getResourceAsStream("sggc.properties"));            
        } catch (Exception e) {
            e.printStackTrace();
        }
    
    }
    
    public static boolean DEBUG                         = true;  // print verbose output
    
    public static final int NOTIFCATION_DELAY           = 6000;  // delay between notification batch processing
    
    /******************* Third Party Service Locations ******************/
    
    public static final String AIM_SERVER               = props.getProperty("im.server");
    
    public static final String AIM_PORT                 = props.getProperty("im.port");
    
    public static final String MAIL_SERVER              = props.getProperty("mail.smtps.host");
    
    public static final String DATABASE                 = props.getProperty("db.server");
    
    public static final String SPEEDPAGE_SERVER         = props.getProperty("speedpage.server");
    
    public static final String CTSS_SERVER         		= props.getProperty("ctss-resources-v1.server");
    
    public static final String TG_RESOURCES_SERVER      = props.getProperty("tg-resources-v1.server");
    
    public static final String INCA_SERVER        		= props.getProperty("inca.server");
    
    public static final String GPIR_SERVER        		= props.getProperty("gpir.server");
    
    public static final String KIT_SERVICES_SERVER      = props.getProperty("kit-services.server");
    
    public static final String IIS_OUTAGE_SERVER		= props.getProperty("iis-outage-server");
    
    public static final int REFRESH_INTERVAL         = Integer.valueOf(props.getProperty("refresh.interval")) * 3600;
    
    
    /******************* External Authentication Options *******/
    
    public static final String IMLOGIN                 = props.getProperty("im.handle");  /* directory containing user proxies */
    
    public static final String IMPASSWORD              = props.getProperty("im.password");  /* directory to store cached files from mss */
     
    public static final String MAILLOGIN                = props.getProperty("mail.smtps.user");  /* service email account */
    
    public static final String MAILPASSWORD             = props.getProperty("mail.smtps.passwd");  /* service email password */
    
    public static final String MAILSMTPSPROTOCOL        = props.getProperty("mail.smtps.auth");  /* service email password */
       
    public static final String MAILACCOUNT              = props.getProperty("mail.smtps.account");  /* service email password */
    
    public static final String DBUSERNAME               = props.getProperty("db.user");  /* database username */
    
    public static final String DBPASSWORD               = props.getProperty("db.passwd");  /* database password */

    
  
    public static void init() {}
}
