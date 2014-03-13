package edu.utexas.tacc.wcs.filemanager.service.resources;

import java.util.List;

import org.restlet.resource.Get;
import edu.utexas.tacc.wcs.filemanager.common.model.System;

public interface SystemsResource {

	@Get
	public abstract List<System> retrieveResources();

}