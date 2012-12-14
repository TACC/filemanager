/* 
 * Created on Feb 26, 2008
 * 
 * Developed by: Rion Dooley - dooley [at] tacc [dot] utexas [dot] edu
 * 				 TACC, Texas Advanced Computing Center
 * 
 * https://www.tacc.utexas.edu/
 */

package org.teragrid.portal.filebrowser.server.servlet.model.notification;

import net.sf.jml.Email;
import net.sf.jml.MsnMessenger;
import net.sf.jml.event.MsnAdapter;
import net.sf.jml.event.MsnContactListAdapter;
import net.sf.jml.impl.MsnMessengerFactory;

import org.apache.log4j.Logger;
import org.teragrid.portal.filebrowser.server.servlet.Settings;

public class BasicMessenger {
	private static final Logger logger = Logger.getLogger(BasicMessenger.class);
	protected static boolean finishedLogin = false;

    private String email;
    private String password;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
    public void setLoggedIn(boolean isLoggedIn) {
    	finishedLogin = isLoggedIn;
    }

    protected void initMessenger(MsnMessenger messenger, final String handle, final String message) {
    	
    	messenger.addListener(new MsnAdapter() {

			@Override
			public void loginCompleted(MsnMessenger arg0) {
				// TODO Auto-generated method stub
				super.loginCompleted(arg0);
//				System.out.println("Logged in");
			}

			@Override
			public void logout(MsnMessenger arg0) {
				// TODO Auto-generated method stub
				super.logout(arg0);
//				System.out.println("Logged out");
			}
    		
    	});
    	
    	messenger.addContactListListener(new MsnContactListAdapter() {

            public void contactListInitCompleted(MsnMessenger messenger) {
            	super.contactListInitCompleted(messenger);
                 
                //this is the simplest way to send text
            	logger.debug("Sending IM to user " + handle);
                messenger.sendText(Email.parseStr(handle), message);
                setLoggedIn(true);
            }

        });


    }

    public BasicMessenger() {
        
    }
    
    public void send(final String handle, final String message) {
    	//create MsnMessenger instance
    	MsnMessenger messenger = MsnMessengerFactory.createMsnMessenger(Settings.IMLOGIN,
                Settings.IMPASSWORD);
        
        //MsnMessenger support all protocols by default
        //messenger.setSupportedProtocol(new MsnProtocol[] { MsnProtocol.MSNP8 });

        //default init status is online, 
        //messenger.getOwner().setInitStatus(MsnUserStatus.BUSY);

        //log incoming message
        messenger.setLogIncoming(true);

        //log outgoing message
        messenger.setLogOutgoing(true);

        initMessenger(messenger, handle, message);
        
        messenger.login();
        
        while (!finishedLogin);
		        
    }
    
    public static void main(String[] args) {
    	new BasicMessenger().send("deardooley@hotmail.com","It's working");
    }
}
