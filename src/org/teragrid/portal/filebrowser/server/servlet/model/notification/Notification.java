/*Copyright (c) 2004,University of Illinois at Urbana-Champaign.  All rights reserved.
 * 
 * Created on Jun 19, 2006
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

import java.util.Calendar;

import org.teragrid.portal.filebrowser.applet.transfer.Task;
import org.teragrid.portal.filebrowser.server.servlet.dao.NotificationDAO;
import org.teragrid.portal.filebrowser.server.servlet.dao.TransferDAO;
import org.teragrid.portal.filebrowser.server.servlet.exception.NotificationException;
import org.teragrid.portal.filebrowser.server.servlet.model.filetransfer.Transfer;
import org.teragrid.portal.filebrowser.server.servlet.model.user.User;


/**
 * Parent class for all notification classes.
 * 
 * @author Rion Dooley < dooley [at] tacc [dot] utexas [dot] edu >
 *
 * @see NotificationManager, EmailNotification, IMNotification
 */
public class Notification {
    
    
    // ******************* End Inner composite Id class ******************* //
//    private Id id = new Id();   
    private Integer id = null;
    private Integer transferId;
    private NotificationType type = NotificationType.EMAIL;
    
    private String subject = "TG Notification";
    private String message = "You are receiving this message in response to your notification request."; 
    private String address = "";
    private Calendar created = Calendar.getInstance();
    private Calendar deliveryDate = Calendar.getInstance();
    
    private String username = null;;
        
    public Notification() {}
    
    public Notification(Integer transferId, String subject, String message, User user, NotificationType type) 
    throws NotificationException {
        this.subject = subject;
        this.message = message;
        try {
            this.transferId = transferId;
        } catch (Exception e) {
            throw new NotificationException(e);
        }
        this.username = user.getWholeName();
        this.type = type;
        
        if (this.type.equals(NotificationType.IM)) {
            this.address = user.getIm();
            
            if (this.address == null || this.address.equals("none provided")) {
                message = "You are receiving this email because you have not " + 
                    "specified an aim handle to receive instant " + 
                    "message notifications. \n\n"  + message;
                this.type = NotificationType.EMAIL;
                
            }
        } 
        else if (this.type.equals(NotificationType.SMS) || this.type.equals(NotificationType.TEXT)) {
            
            String sms = user.getCell();
            
            if (sms == null || sms.equals("") || sms.equals("none provided")) {
                sms = user.getEmail();
                message = "You are receiving this email because you have not " + 
                    "specified a cell phone number to receive text and sms " + 
                    "message notifications. \n\n"  + message;
                
            } else {
                sms.replaceAll("\\p{Punct}", "").replaceAll("\\s", "").replaceAll("\\-", "");
                sms = sms + "@teleflip.com";
            }    
            
            this.address = sms;
        } 
        else
            this.address = user.getEmail();
            
        
    }
    
    /**
     * @return the user
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param user the user to set
     */
    public void setUsername(String username) {
        this.username = username;
    }
    /**
     * @return the created
     */
    public Calendar getCreated() {
        return created;
    }

    /**
     * @param created the created to set
     */
    public void setCreated(Calendar created) {
        this.created = created;
    }

    /**
     * @return the delivered
     */
    public Calendar getDeliveryDate() {
        return deliveryDate;
    }

    /**
     * @param delivered the delivered to set
     */
    public void setDeliveryDate  (Calendar deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    /**
     * @return the address
     */
    public String getAddress() {
        return address;
    }

    /**
     * @param address the address to set
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * @return the id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * @return the subject
     */
    public String getSubject() {
        String subject = "";
        Transfer t = getTransfer();
        if (t.getStatus() == Task.ONGOING) {
            subject = "Transfer of " + t.getFileName() + " has begun.";
        } else if (t.getStatus() == Task.DONE) {
            subject = "Transfer of " + t.getFileName() + " has finished.";
        } else if (t.getStatus() == Task.FAILED) {
            subject = "Transfer of " + t.getFileName() + " has failed.";
        } else {
            subject = this.subject;
        }
        
        return subject;
    }

    /**
     * @param subject the subject to set
     */
    public void setSubject(String subject) {
        this.subject = subject;
    }

    /**
     * @return the transfer
     */
    public Transfer getTransfer() {
        return new TransferDAO().getTransferById(this.transferId, false);
    }

    /**
     * @param transfer the transfer to set
     */
    public void setTransfer(Transfer transfer) {
        this.transferId = transfer.getId();
    }

    /**
     * @return the type
     */
    public NotificationType getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(NotificationType type) {
        this.type = type;
    }

    /**
     * @return the transferId
     */
    public Integer getTransferId() {
        return transferId;
    }

    /**
     * @param transferId the transferId to set
     */
    public void setTransferId(Integer transferId) {
        this.transferId = transferId;
    }

    //  ********************** Common Methods ********************** //

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Notification)) return false;
        final Notification n = (Notification) o;
        if (!this.transferId.equals(n.getTransferId())) return false;
        if (!this.message.equals(n.getMessage())) return false;
        if (!this.type.equals(n.getType())) return false;
        if (!this.subject.equals(n.getSubject())) return false;
        if (!this.created.equals(n.getCreated())) return false;
        if (this.deliveryDate != n.deliveryDate) return false;
        if (this.username != n.username) return false;
        return true;
    }
    
    public String toString() {
        return  getType() + " notification for transfer[" + getTransfer().getId()+ "] ";
    }
    
    public int compareTo(Object o) {
        if (o instanceof Notification) {
            if (this.getTransferId().compareTo(((Notification)o).getTransferId()) > 0||  
                   this.getType().compareTo(((Notification)o).getType()) > 0 ||
                   this.deliveryDate != ((Notification)o).deliveryDate ||
                   this.getCreated().compareTo(((Notification)o).getCreated()) > 0) {
                return 1;
            }
        }
        return 0;
    }
    
    public void persist() {
        NotificationDAO.makePersistent(this);
    }
    
    public void delete() {
        NotificationDAO.makeTransient(this);
    }
}