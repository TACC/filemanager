package edu.utexas.tacc.wcs.filemanager.service.resources;

import org.restlet.resource.Delete;
import org.restlet.resource.Put;

import edu.utexas.tacc.wcs.filemanager.common.model.Transfer;

public interface TransfersResource {

	@Put
	public abstract void update(Transfer transfer);

	@Delete
	public abstract void removeTransfer();

}