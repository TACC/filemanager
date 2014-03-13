/**
 * 
 */
package edu.utexas.tacc.wcs.filemanager.common.model;

import java.text.SimpleDateFormat;
import java.util.Date;


//import org.json.JSONStringer;

/**
 * @author dooley
 *
 */
public class BandwidthMeasurement {
	private String fromHostname;
	private String toHostname;
	private Date lastUpdated;
	private Double measurement;
	
	public BandwidthMeasurement() {}
	
	
	
	public BandwidthMeasurement(String fromHostname, String toHostname,
			Date lastUpdated, Double measurement) {
		super();
		this.fromHostname = fromHostname;
		this.toHostname = toHostname;
		this.lastUpdated = lastUpdated;
		this.measurement = measurement;
	}
	
	/**
	 * @return the fromHostname
	 */
	public String getFromHostname() {
		return fromHostname;
	}

	/**
	 * @param fromHostname the fromHostname to set
	 */
	public void setFromHostname(String fromHostname) {
		this.fromHostname = fromHostname;
	}

	/**
	 * @return the toHostname
	 */
	public String getToHostname() {
		return toHostname;
	}

	/**
	 * @param toHostname the toHostname to set
	 */
	public void setToHostname(String toHostname) {
		this.toHostname = toHostname;
	}

	/**
	 * @return the lastUpdated
	 */
	public Date getLastUpdated() {
		return lastUpdated;
	}

	/**
	 * @param lastUpdated the lastUpdated to set
	 */
	public void setLastUpdated(Date lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

	/**
	 * @return the measurement
	 */
	public Double getMeasurement() {
		return measurement;
	}

	/**
	 * @param measurement the measurement to set
	 */
	public void setMeasurement(Double measurement) {
		this.measurement = measurement;
	}

	public boolean equals(Object o) {
		if (o instanceof String) {
			return toHostname.equals(o);
		} else if (o instanceof BandwidthMeasurement) {
			return (toHostname.equals(((BandwidthMeasurement)o).toHostname) && 
					fromHostname.equals(((BandwidthMeasurement)o).fromHostname));
		} else if (o instanceof TeraGridSystem) {
			return toHostname.equals(((TeraGridSystem)o).getResourceName());
		}
		return false;
	}
	
	public String toCsv() {
		return quote(fromHostname) + "," + quote(toHostname) + "," + 
			quote(formatDate(lastUpdated)) + "," + quote(measurement);
	}

	private String quote(Object o) {
		return "\"" + (o == null?o:o.toString()) + "\"";
	}
	
	public String toHtml() {
		return "<tr>" + formatColumn(fromHostname) + formatColumn(toHostname) + 
			formatColumn(formatDate(lastUpdated)) + formatColumn(measurement) + "</tr>";
	}


	private String formatColumn(Object o) {
		return "<td>" + (o == null?o:o.toString()) + "</td>";
	}
	
	public String toPerl() {
		return " {\n" + 
		"   source => '" + fromHostname + "',\n" +
		"   destination => '" + toHostname + "',\n" +
		"   last.updated => '" + formatDate(lastUpdated) + "',\n" +
		"   measurement => '" + measurement + "'},\n"; 
		
	}

//	public String toJSONString() {
//		String output = null;
//		JSONStringer js = new JSONStringer();
//		try {
//			js.object().key("measurement")
//			.object().key("source").value(fromHostname).endObject()
//			.object().key("destination").value(toHostname).endObject()
//			.object().key("last.updated").value(formatDate(lastUpdated)).endObject()
//			.object().key("measurement").value(measurement).endObject()
//			.endObject();
//			
//			output = js.toString();
//			
//		} catch(Exception e)  {
//			System.out.println("Error producing JSON output.");
//		}
//		
//		return output;
//	}
	
	public String toString() {
		return toCsv();
	}
	
	private String formatDate(Date date) {
		return new SimpleDateFormat("MM/dd/yyyy").format(date);
	}

}
