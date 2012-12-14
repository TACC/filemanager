/* 
 * Created on Apr 10, 2008
 * 
 * Developed by: Rion Dooley - dooley [at] tacc [dot] utexas [dot] edu
 * 				 TACC, Texas Advanced Computing Center
 * 
 * https://www.tacc.utexas.edu/
 */

package org.teragrid.portal.filebrowser.applet.transfer;

import java.io.EOFException;
import java.io.IOException;
import java.net.SocketException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Properties;
import java.util.Vector;

import org.globus.ftp.FileInfo;
import org.globus.ftp.exception.ClientException;
import org.globus.ftp.exception.ServerException;
import org.teragrid.portal.filebrowser.applet.AppMain;
import org.teragrid.portal.filebrowser.applet.ConfigSettings;
import org.teragrid.portal.filebrowser.applet.ui.ListModel;
import org.teragrid.portal.filebrowser.applet.ui.PnlBrowse;
import org.teragrid.portal.filebrowser.applet.util.LogManager;
import org.teragrid.portal.filebrowser.applet.util.ResourceName;
import org.teragrid.portal.filebrowser.applet.util.SGGCResourceBundle;
import org.teragrid.service.profile.wsclients.ProfileServiceClient;
import org.teragrid.service.profile.wsclients.model.EnvironmentVariable;

import com.sshtools.j2ssh.session.SessionChannelClient;

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
    
    public void run(){
        this.app = TextAppender.getInstance();
        this.threadName = Thread.currentThread().getName();

        this.app.addTextBox(this.pnlBrowse.getTxtLog(), this.threadName);

        if (pnlBrowse.getFtpServer().isLocal()) {
        	pnlBrowse.tBrowse.setEnvProperties(new ArrayList<EnvironmentVariable>());
        } else {
	        
	        ProfileServiceClient client = 
	        	new ProfileServiceClient(ConfigSettings.SERVICE_TG_USER_PROFILE, 
										AppMain.ssoUsername, 
										AppMain.ssoPassword);
	        List<EnvironmentVariable> environment = client.getEnvironment(pnlBrowse.getFtpServer().resourceId);
	        
	        // add the env to the browsing thread for use in goto path requests
	        pnlBrowse.tBrowse.setEnvProperties(environment);
	        
	        pnlBrowse.enableEnvironmentButton();
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
