package edu.utexas.tacc.wcs.filemanager.service.resources;

import java.util.List;
import edu.utexas.tacc.wcs.filemanager.common.model.System;
import org.restlet.resource.Get;

public interface SystemsResource {

	@Get
	public abstract List<System> retrieveResources();

}
