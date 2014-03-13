package edu.utexas.tacc.wcs.filemanager.service.resources;

import java.util.List;

import org.restlet.resource.Post;

import edu.utexas.tacc.wcs.filemanager.common.model.User;
import edu.utexas.tacc.wcs.filemanager.common.model.UserQuery;

public interface UsersResource {

	@Post
	public abstract List<User> findUsers(UserQuery userQuery);

}