package edu.utexas.tacc.wcs.filemanager.common.model;

import java.util.ArrayList;
import java.util.List;

import edu.utexas.tacc.wcs.filemanager.common.model.enumeration.NotificationType;

public class BulkTransferRequest {
	private List<Transfer> transfers = new ArrayList<Transfer>(); 
	private String epr;
	private NotificationType notificationType;
	
	public BulkTransferRequest(List<Transfer> transfers, String epr,
			NotificationType notificationType) {
		super();
		this.transfers = transfers;
		this.epr = epr;
		this.notificationType = notificationType;
	}
	
	/**
	 * @return the transfers
	 */
	public List<Transfer> getTransfers() {
		return transfers;
	}
	/**
	 * @param transfers the transfers to set
	 */
	public void setTransfers(List<Transfer> transfers) {
		if (transfers == null) {
			this.transfers.clear();
		} else {
			this.transfers = transfers;
		}
	}
	
	/**
	 * @return the epr
	 */
	public String getEpr() {
		return epr;
	}
	/**
	 * @param epr the epr to set
	 */
	public void setEpr(String epr) {
		this.epr = epr;
	}
	/**
	 * @return the notificationType
	 */
	public NotificationType getNotificationType() {
		return notificationType;
	}
	/**
	 * @param notificationType the notificationType to set
	 */
	public void setNotificationType(NotificationType notificationType) {
		this.notificationType = notificationType;
	}
}
