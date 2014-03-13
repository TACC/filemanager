package edu.utexas.tacc.wcs.filemanager.common.model;

/**
 * Holds parameters to query the BandwidthResource
 * 
 * @author dooley
 *
 */
public class BandwidthQuery 
{	
	private String source;
	private String dest;
	
	public BandwidthQuery(String source, String dest) {
		super();
		this.source = source;
		this.dest = dest;
	}
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
	 * @return the dest
	 */
	public String getDest() {
		return dest;
	}
	/**
	 * @param dest the dest to set
	 */
	public void setDest(String dest) {
		this.dest = dest;
	}
}
