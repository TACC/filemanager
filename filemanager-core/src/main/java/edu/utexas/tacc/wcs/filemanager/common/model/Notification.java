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

package edu.utexas.tacc.wcs.filemanager.common.model;

import java.util.Calendar;

import edu.utexas.tacc.wcs.filemanager.common.exception.NotificationException;
import edu.utexas.tacc.wcs.filemanager.common.model.enumeration.NotificationType;


/**
 * Parent class for all notification classes.
 * 
 * @author Rion Dooley < dooley [at] tacc [dot] utexas [dot] edu >
 *
 * @see NotificationManager, EmailNotification, IMNotification
 */
public class Notification 
{   
	private Long id = null;
    private Long transferId;
    private NotificationType type = NotificationType.EMAIL;
    private String message = "You are receiving this message in response to your notification request."; 
    private String address = "";
    private String subject = "";
    private Calendar created = Calendar.getInstance();
    private Calendar deliveryDate = Calendar.getInstance();
    private String username = null;;
        
    public Notification() {}
    
    public Notification(Long transferId, String subject, String message, User user, NotificationType type) 
    throws NotificationException 
    {
    	try {
            this.transferId = transferId;
        } catch (Exception e) {
            throw new NotificationException(e);
        }
    	
    	this.message = message;
        this.username = user.getWholeName();
        this.type = type;
        this.address = user.getEmail();
    }
    
    /**
	 * @return the id
	 */
    public Long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the transferId
	 */
	public Long getTransferId() {
		return transferId;
	}

	/**
	 * @param transferId the transferId to set
	 */
	public void setTransferId(Long transferId) {
		this.transferId = transferId;
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

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
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
	 * @return the deliveryDate
	 */
	public Calendar getDeliveryDate() {
		return deliveryDate;
	}

	/**
	 * @param deliveryDate the deliveryDate to set
	 */
	public void setDeliveryDate(Calendar deliveryDate) {
		this.deliveryDate = deliveryDate;
	}

	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
     * @param transfer the transfer to set
     */
    public void setTransfer(Transfer transfer) {
        this.transferId = transfer.getId();
    }

    //  ********************** Common Methods ********************** //

//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (!(o instanceof Notification)) return false;
//        final Notification n = (Notification) o;
//        if (!this.transferId.equals(n.getTransferId())) return false;
//        if (!this.subject.equals(n.getMessage())) return false;
//        if (!this.message.equals(n.getMessage())) return false;
//        if (!this.type.equals(n.getType())) return false;
//        if (!this.created.equals(n.getCreated())) return false;
//        if (this.deliveryDate != n.deliveryDate) return false;
//        if (this.username != n.username) return false;
//        return true;
//    }
    
    public String toString() {
        return  getType() + " notification for transfer[" + getTransferId() + "] ";
    }
    
//    public int compareTo(Object o) {
//        if (o instanceof Notification) {
//            if (this.getTransferId().compareTo(((Notification)o).getTransferId()) > 0||  
//                   this.getType().compareTo(((Notification)o).getType()) > 0 ||
//                   this.deliveryDate != ((Notification)o).deliveryDate ||
//                   this.getCreated().compareTo(((Notification)o).getCreated()) > 0) {
//                return 1;
//            }
//        }
//        return 0;
//    }
}