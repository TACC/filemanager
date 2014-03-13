/**
 * 
 */
package org.teragrid.portal.filebrowser.applet.util.proxy;

import java.awt.Frame;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.cert.CertificateEncodingException;
import java.util.logging.Logger;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSException;


/**
 * Wrapper to expose MyProxyLogonGUI as a JDialog
 * 
 * @author dooley
 *
 */
@SuppressWarnings("serial")
public class MyProxyDialog extends JDialog {
	static Logger logger = Logger.getLogger(MyProxyDialog.class.getName());
	private MyProxyLogonGUI gui;
	
	public MyProxyDialog(Frame parent, String title, boolean modal) {
		super(parent,title,modal);
		gui = new MyProxyLogonGUI(this);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		add(gui);
		pack();
		if (gui.usernameField.getText() == null ||
				gui.usernameField.getText().equals("")) {
			gui.usernameField.setText(System.getProperty("user.name"));
			gui.usernameField.selectAll();
			gui.usernameField.requestFocusInWindow();
		} else {
			gui.passwordField.requestFocusInWindow();
		}
		
//		setModal(true);
//		setVisible(true);
	}
	
	public MyProxyDialog(Frame parent, String title) {
		this(parent,title,true);
	}
	
	public void saveCredentials(String fileName) throws IOException, GeneralSecurityException {
		gui.myproxy.saveCredentialsToFile(fileName);
	}
	
	public GSSCredential getGSSCredential() throws CertificateEncodingException, IOException, GSSException {
		return gui.myproxy.convertCredentialsToGlobusCredential();
	}
	
	/**
	 * Calls createAndShowGUI().
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				MyProxyLogonGUI.createAndShowGUI();
			}
		});
	}
}