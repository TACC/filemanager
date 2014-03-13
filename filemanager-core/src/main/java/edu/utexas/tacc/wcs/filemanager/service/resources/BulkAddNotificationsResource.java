package edu.utexas.tacc.wcs.filemanager.service.resources;

import org.restlet.resource.Post;

import edu.utexas.tacc.wcs.filemanager.common.model.BulkNotificationRequest;

public interface BulkAddNotificationsResource {

	@Post
	public abstract void addAll(BulkNotificationRequest bulkNotifications);

}