/* 
 * Created on Apr 10, 2008
 * 
 * Developed by: Rion Dooley - dooley [at] tacc [dot] utexas [dot] edu
 * 				 TACC, Texas Advanced Computing Center
 * 
 * https://www.tacc.utexas.edu/
 */

package org.teragrid.portal.filebrowser.applet.transfer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.teragrid.portal.filebrowser.applet.AppMain;
import org.teragrid.portal.filebrowser.applet.ui.PnlBrowse;
import org.teragrid.portal.filebrowser.applet.util.LogManager;
import org.teragrid.service.profile.wsclients.model.EnvironmentVariable;

import edu.utexas.tacc.wcs.filemanager.common.model.enumeration.FileProtocolType;
import edu.utexas.tacc.wcs.filemanager.common.model.enumeration.SystemType;


/**
 * Client utility to pull the user's environment from the profile service.
 * 
 * @author Rion Dooley < dooley [at] tacc [dot] utexas [dot] edu >
 *
 */
@SuppressWarnings("unused")
public class EnvironmentThread extends Thread{
    protected PnlBrowse pnlBrowse = null;
    private String threadName;
    private TextAppender app;
    
    public EnvironmentThread(PnlBrowse pnlBrowse){
        this.pnlBrowse=pnlBrowse;
    }
    
    public void run()
    {
        this.app = TextAppender.getInstance();
        this.threadName = Thread.currentThread().getName();

        this.app.addTextBox(this.pnlBrowse.getTxtLog(), this.threadName);

//        if () {
//        	pnlBrowse.tBrowse.setEnvProperties(new ArrayList<EnvironmentVariable>());
//        } else {
//	        
//	        ProfileServiceClient client = 
//	        	new ProfileServiceClient(ConfigSettings.SERVICE_TG_USER_PROFILE, 
//										AppMain.ssoUsername, 
//										AppMain.ssoPassword);
        	
    	List<EnvironmentVariable> environment = new ArrayList<EnvironmentVariable>();
    	FTPSettings currentServerSettings = pnlBrowse.getFtpServer();
    	GsiSshClient client = null;
    	if (!currentServerSettings.isLocal() && 
    			!currentServerSettings.hostType.equals(SystemType.ARCHIVE) &&
    			!StringUtils.isEmpty(currentServerSettings.sshHost) && 
    			currentServerSettings.protocol.equals(FileProtocolType.GRIDFTP)) 
    	{
    		try 
    		{
        		client = new GsiSshClient( currentServerSettings.sshHost, 
        								currentServerSettings.sshPort, 
        								AppMain.defaultCredential);
        		
        		Properties props = client.env();
    	        
    	        for(Iterator<Object> iter = (Iterator<Object>)props.keySet().iterator(); iter.hasNext();) 
    	        {
    	        	String prop = (String)iter.next();
    	        	environment.add(new EnvironmentVariable(prop, props.getProperty(prop)));
    	        	
    	        	// add the env to the browsing thread for use in goto path requests
    		        pnlBrowse.tBrowse.setEnvProperties(environment);
    		        
    		        pnlBrowse.enableEnvironmentButton();
    	        }
    		} catch (Throwable t) {
    			LogManager.error("Failed to retrieve user environment", t);
    		}
    	}
    	
    	close();
    }

    public boolean isConnected() {
    	return false;
    }

    public boolean close(){
        try {
            interrupt();
        }catch(Exception ex){}
        
        return true;
    }
}
