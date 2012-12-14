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

package org.teragrid.portal.filebrowser.server.servlet.model.notification;

import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;
import org.teragrid.portal.filebrowser.server.servlet.Settings;
import org.teragrid.portal.filebrowser.server.servlet.exception.NotificationException;
import org.teragrid.portal.filebrowser.server.servlet.exception.PermissionException;
import org.teragrid.portal.filebrowser.server.servlet.model.user.User;


/**
 * Simple email class using the javamail API to send a text message to the 
 * user using their cell phone number in the DB.  The actual mechanics of the
 * process are handled by the Teleflip service.  We simply convert the user's 
 * cell number into an email address and send an email to Teleflip with the
 * desired text.  If the message runs long, multiple emails are sent.  If no
 * cell number is provided, we default to sending an email to the user.
 * Good times to send notification would be when their account is activated, 
 * job start, stop, and migrations.
 * 
 * @author Rion Dooley < dooley [at] tacc [dot] utexas [dot] edu >
 *
 */
public class TextMessage {
    
	private static final Logger logger = Logger.getLogger(TextMessage.class);
	
    public static void send(User user, String subject, String text) throws NotificationException {
        Session session = null;
        
        try {
            
            Settings.init();
            
            Authenticator auth = new TextAuthenticator();
            
            session = Session.getInstance(Settings.props,auth);
            
            String address = user.getCell();
            
            if (address == null || address.equals("none provided")) {
                address = user.getEmail();
                text = "You are receiving this email because you have not " + 
                    "specified a cell phone number to receive text and sms " + 
                    "message notifications. \n\n"  + text;
                
            } else {
                address.replaceAll("\\p{Punct}", "").replaceAll("\\s", "");
                address = address + "@teleflip.com";
            }    
            
            MimeMessage message = createMessageObject(session, 
                    subject, text, address, user.getFirstName() + " " + user.getLastName());
            
            Transport transport = session.getTransport("smtps");
           
            transport.connect(Settings.MAIL_SERVER,Settings.MAILLOGIN, Settings.MAILPASSWORD);
            
            transport.sendMessage(message, message.getRecipients(Message.RecipientType.TO));
            
            logger.debug("Sent text to " + address);
            
        } catch (Exception e) {
            
            e.printStackTrace();
            throw new NotificationException("Text notification failed.",e);
            
        }
    }
    
    public static void send(Notification n) throws NotificationException {
        
        Session session = null;
        
        try {
            
            Settings.init();
            
            Authenticator auth = new TextAuthenticator();
            
            session = Session.getInstance(Settings.props,auth);
            
            MimeMessage message = createMessageObject(session, 
                    "", 
                    n.getSubject(), 
                    n.getAddress(),
                    n.getUsername());
            
            Transport transport = session.getTransport("smtps");
           
            transport.connect(Settings.MAIL_SERVER,Settings.MAILLOGIN, Settings.MAILPASSWORD);
            
            transport.sendMessage(message, message.getRecipients(Message.RecipientType.TO));
            
            logger.debug("Sent text to " + n.getAddress());
            
            n.setDeliveryDate(java.util.Calendar.getInstance());
            
            n.persist();
            
//            // Mark the notification as sent.
//            org.hibernate.Session s = HibernateUtil.getSession();
//            s.beginTransaction();
//            n.setDelivered(1);
//            s.saveOrUpdate(n);
//            s.getTransaction().commit();
            
        } catch (Exception e) {
            
            e.printStackTrace();
            throw new NotificationException("Text notification failed.",e);
            
        }
        
    }
    
    /**
     * Create the message object sent to the destination.  Here we
     * parse the user's cell phone number into a numeric string that will
     * make the subject of the teleflip email address.  If no cell phone
     * number is provided, the user's default email address will be used
     * and the body of the message will be augmented with a notice of
     * why they are not receiving a text.
     * 
     * @param user
     * @return
     */
    private static MimeMessage createMessageObject(Session session, 
                                                    String subject, 
                                                    String text, 
                                                    String address,
                                                    String username) 
    throws Exception {
        
        MimeMessage message = new MimeMessage(session);
        
        Address toAddress = null;
        Address fromAddress = null;
        
        fromAddress = new InternetAddress(Settings.MAILACCOUNT, "TG Notification Service");
        
        message.setFrom(fromAddress);
        
        message.setSubject(subject);
        
//        if (user.getCell() == null || user.getCell().equals("none provided") || 
//                user.getCell().equals("")) {
//        
//        if (address.equals("none provided")) {
//            text = "You are receiving this email because you have not " + 
//                "specified a cell phone number to receive text " + 
//                "message notifications. \n\n"  + text;
//        
//            toAddress = new InternetAddress(user.getEmail(), 
//                    user.getFirstName() + " " + user.getLastName());
//            
//            log.debug("No cell phone number provided. Using default email instead " + 
//                    user.getEmail());
//            
//
//            
////            log.debug("Teleflip address is: " + email);
//            System.out.println("No cell phone on record. Sent txt message to " + user.getEmail());
////            
//        } else {
            
//            String email = user.getCell().replaceAll("\\p{Punct}", "")
//            .replaceAll("\\s", "");
//        
//            email = email + "@teleflip.com";
            
            toAddress = new InternetAddress(address, username);
            
//            log.debug("Teleflip address is: " + email);
            System.out.println("Sent txt message to " + address);
//        }
        
        message.setText(text);
        
        message.setRecipient(Message.RecipientType.TO, toAddress);
        return message;
    }
    
}


class TextAuthenticator extends Authenticator {
    private String username = null;
    private String password = null;
    
    public TextAuthenticator() throws Exception{
        
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