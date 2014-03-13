package edu.utexas.tacc.wcs.filemanager.common.model;

import java.util.ArrayList;
import java.util.List;

import edu.utexas.tacc.wcs.filemanager.common.model.enumeration.NotificationType;

/**
 * Holds parameters to add multiple Notifications 
 * 
 * @author dooley
 *
 */
public class BulkNotificationRequest 
{
	private List<Long> transferIds = new ArrayList<Long>();
	private NotificationType type;
	
	public BulkNotificationRequest(List<Long> transferIds, NotificationType type) {
		this.transferIds = transferIds;
		this.type = type;
	}

	/**
	 * @return the transferIds
	 */
	public List<Long> getTransferIds() {
		return transferIds;
	}

	/**
	 * @param transferIds the transferIds to set
	 */
	public void setTransferIds(List<Long> transferIds) {
		if (transferIds == null) {
			this.transferIds.clear();
		} else {
			this.transferIds = transferIds;
		}
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
	
}
