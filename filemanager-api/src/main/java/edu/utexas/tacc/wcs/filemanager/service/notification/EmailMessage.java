/*Copyright (c) 2004,University of Illinois at Urbana-Champaign.  All rights reserved.
 * 
 * Created on May 10, 2007
 * 
 * Developed by: CCT, Center for Computation and Technology, 
 * 				NCSA, University of Illinois at Urbana-Champaign
 * 				OSC, Ohio Supercomputing Center
 * 				TACC, Texas Advanced Computing Center
 * 				UKy, University of Kentucky
 * 
 * https://www.gridchem.org/
 * 
 * Permission is hereby granted, free of charge, to any person 
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal with the Software without 
 * restriction, including without limitation the rights to use, 
 * copy, modify, merge, publish, distribute, sublicense, and/or 
 * sell copies of the Software, and to permit persons to whom 
 * the Software is furnished to do so, subject to the following conditions:
 * 1. Redistributions of source code must retain the above copyright notice, 
 *    this list of conditions and the following disclaimers.
 * 2. Redistributions in binary form must reproduce the above copyright notice, 
 *    this list of conditions and the following disclaimers in the documentation
 *    and/or other materials provided with the distribution.
 * 3. Neither the names of Chemistry and Computational Biology Group , NCSA, 
 *    University of Illinois at Urbana-Champaign, nor the names of its contributors 
 *    may be used to endorse or promote products derived from this Software without 
 *    specific prior written permission.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF 
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  
 * IN NO EVENT SHALL THE CONTRIBUTORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, 
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, 
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER 
 * DEALINGS WITH THE SOFTWARE.
*/

package edu.utexas.tacc.wcs.filemanager.service.notification;

import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;

import com.sun.mail.smtp.SMTPSSLTransport;

import edu.utexas.tacc.wcs.filemanager.common.model.Notification;
import edu.utexas.tacc.wcs.filemanager.common.model.Transfer;
import edu.utexas.tacc.wcs.filemanager.common.model.User;
import edu.utexas.tacc.wcs.filemanager.service.Settings;
import edu.utexas.tacc.wcs.filemanager.service.dao.TransferDAO;
import edu.utexas.tacc.wcs.filemanager.service.exception.NotificationException;
import edu.utexas.tacc.wcs.filemanager.service.exception.PermissionException;

/**
 * Simple email class using the javamail API to send an email to the 
 * user using their contact information from the DB.  Good times to send
 * notification would be when their account is activated, job start, stop,
 * and migrations.
 * 
 * @author Rion Dooley < dooley [at] tacc [dot] utexas [dot] edu >
 *
 */
public class EmailMessage {
	private static final Logger logger = Logger.getLogger(EmailMessage.class);
    
	public static void send(User user, String subject, String text) throws NotificationException {
        Session session = null;
        
        try {
            Settings.init();
            
            session = Session.getInstance(Settings.props);
            
            MimeMessage message = createMessageObject(session, 
                    subject, text, user.getEmail(),user.getFirstName() + " " + user.getLastName());
            
            SMTPSSLTransport transport = (SMTPSSLTransport)session.getTransport("smtps");
            
            transport.connect(Settings.MAIL_SERVER,Settings.MAILLOGIN, Settings.MAILPASSWORD);
            transport.sendMessage(message, message.getRecipients(Message.RecipientType.TO));
            
            logger.info("Sent email to " + user.getEmail());
            
        } catch (Exception e) {
            throw new NotificationException("Email notification failed.",e);
            
        }
    }
    
    public static void send(Notification notification) throws NotificationException {
        
        Session session = null;
        
        try {
            Settings.init();
            Authenticator auth = new MailAuthenticator();
            
            session = Session.getInstance(Settings.props,auth);
            Transfer transfer = new TransferDAO().getTransferById(notification.getTransferId(), false);
            MimeMessage message = createMessageObject(session, 
            		transfer.getSubject(),
                    "You are receiving this email because your file transfer from " + 
            		transfer.getSource() + " to " + 
            		transfer.getDest() + 
                    " has changed state to " + 
                    transfer.getStatusString(transfer.getStatus()),
                    notification.getAddress(),
                    notification.getUsername());
            
            Transport transport = session.getTransport("smtps");
            
            transport.connect(Settings.MAIL_SERVER,Settings.MAILLOGIN, Settings.MAILPASSWORD);
            
            transport.sendMessage(message, message.getRecipients(Message.RecipientType.TO));
            
            logger.info("Sent email to " + notification.getAddress());
            
        } catch (Exception e) {
            throw new NotificationException("Email notification failed.",e);
        }
        
    }
    
    private static MimeMessage createMessageObject(Session session, 
                                                    String subject, 
                                                    String text, 
                                                    String address,
                                                    String username) 
    throws Exception {
        
        MimeMessage message = new MimeMessage(session);
        
        message.setText(text);
        
        message.setSubject(subject);
        
        Address fromAddress = new InternetAddress(Settings.MAILACCOUNT, "TG File Transfer Notification Service");
        
        Address toAddress = new InternetAddress(address, 
                username);
        
        message.setFrom(fromAddress);
        
        message.setRecipient(Message.RecipientType.TO, toAddress);
        
        return message;
    }    
}


class MailAuthenticator extends Authenticator {
    private String username = null;
    private String password = null;
    
    public MailAuthenticator() throws Exception{
        
        this.username = Settings.MAILLOGIN;
        
        this.password = Settings.MAILPASSWORD;
    }
    
    public PasswordAuthentication getPasswordAuthentication() throws PermissionException {
        
        if (username != null && password != null) {
            
            return new PasswordAuthentication(username,password);
        
        } else {
        
            throw new PermissionException("No username and password found to authenticate with SMTP mail server.");
        
        }
    }
}