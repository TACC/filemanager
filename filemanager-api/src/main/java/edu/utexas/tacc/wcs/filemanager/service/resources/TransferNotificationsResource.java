package edu.utexas.tacc.wcs.filemanager.service.resources;

import org.restlet.resource.Delete;
import org.restlet.resource.Post;

public interface TransferNotificationsResource {

	@Post
	public abstract void addNotification();

	@Delete
	public abstract void removeNotification();

}