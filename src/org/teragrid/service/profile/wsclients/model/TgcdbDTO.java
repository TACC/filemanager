package org.teragrid.service.profile.wsclients.model;

import org.json.JSONString;

public interface TgcdbDTO extends JSONString {
	
	public String toHtml();
	
	public String toCsv();
	
	public String toPerl();

}
