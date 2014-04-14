package org.teragrid.service.profile.wsclients;

import java.util.Properties;

public class AbstractClientTest {
	
	private static Properties props = new Properties();
	
	static {
		try {
		    props.load(AbstractClientTest.class.getClassLoader().getResourceAsStream("test.properties"));            
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	public static final String IIS_SPEEDPAGE_SERVICE    = props.getProperty("speedpage.server");
    
    public static final String CTSS_SERVER         		= props.getProperty("ctss-resources-v1.server");
    
    public static final String TG_RESOURCES_SERVER      = props.getProperty("tg-resources-v1.server");
    
    public static final String INCA_SERVER        		= props.getProperty("inca.server");
    
    public static final String GPIR_SERVER        		= props.getProperty("gpir.server");
    
    public static final String KIT_SERVICES_SERVER      = props.getProperty("kit-services.server");
    
    public static final String RDR_SERVICES_SERVER      = props.getProperty("rdr-services.server");
    
    public static final String IIS_OUTAGE_SERVER		= props.getProperty("iis-outage-server");
    
    public static final int REFRESH_INTERVAL         	= Integer.valueOf(props.getProperty("refresh.interval")) * 3600;
	
	public AbstractClientTest ()
	{	
		
	}
	
}
