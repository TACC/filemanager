package edu.utexas.tacc.wcs.filemanager.service.resources;

import org.restlet.resource.Get;

import edu.utexas.tacc.wcs.filemanager.common.model.BandwidthQuery;


public interface BandwidthResource {

	@Get
	public abstract Double getBandwidth(BandwidthQuery query);

}