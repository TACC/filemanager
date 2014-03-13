/**
 * 
 */
package org.teragrid.service.profile.wsclients.model;

/**
 * @author dooley
 *
 */
public class SpeedpageEntry {
	private String source;
	private long timestamp;
	private String destination;
	private String sourceId;
	private String destinationId;
	private String destinationUrl;
	private String sourceUrl;
	private float transferRate;
	
	public SpeedpageEntry(){}

	/**
	 * @return the source
	 */
	public String getSource() {
		return source;
	}

	/**
	 * @param source the source to set
	 */
	public void setSource(String source) {
		this.source = source;
	}

	/**
	 * @return the timestamp
	 */
	public long getTimestamp() {
		return timestamp;
	}

	/**
	 * @param timestamp the timestamp to set
	 */
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	/**
	 * @return the destination
	 */
	public String getDestination() {
		return destination;
	}

	/**
	 * @param destination the destination to set
	 */
	public void setDestination(String destination) {
		this.destination = destination;
	}

	/**
	 * @return the sourceId
	 */
	public String getSourceId() {
		return sourceId;
	}

	/**
	 * @param sourceId the sourceId to set
	 */
	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}

	/**
	 * @return the destinationId
	 */
	public String getDestinationId() {
		return destinationId;
	}

	/**
	 * @param destinationId the destinationId to set
	 */
	public void setDestinationId(String destinationId) {
		this.destinationId = destinationId;
	}

	/**
	 * @return the destinationUrl
	 */
	public String getDestinationUrl() {
		return destinationUrl;
	}

	/**
	 * @param destinationUrl the destinationUrl to set
	 */
	public void setDestinationUrl(String destinationUrl) {
		this.destinationUrl = destinationUrl;
	}

	/**
	 * @return the sourceUrl
	 */
	public String getSourceUrl() {
		return sourceUrl;
	}

	/**
	 * @param sourceUrl the sourceUrl to set
	 */
	public void setSourceUrl(String sourceUrl) {
		this.sourceUrl = sourceUrl;
	}

	/**
	 * @return the transferRate
	 */
	public float getTransferRate() {
		return transferRate;
	}

	/**
	 * @param transferRate the transferRate to set
	 */
	public void setTransferRate(float transferRate) {
		this.transferRate = transferRate;
	}
	
	public boolean equals(Object o) {
		if (o instanceof SpeedpageEntry) {
			return ((SpeedpageEntry) o).getSourceId().equals(sourceId) && 
			((SpeedpageEntry) o).getDestinationId().equals(destinationId);
		}
		return false;
	}
	
	public String toString() {
		return source + " => " + destination + " is " + transferRate;
	}
}
