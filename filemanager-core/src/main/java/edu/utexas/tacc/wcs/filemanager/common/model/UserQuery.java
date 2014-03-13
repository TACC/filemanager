package edu.utexas.tacc.wcs.filemanager.common.model;

import edu.utexas.tacc.wcs.filemanager.common.model.enumeration.UserQueryType;

/**
 * Holds parameters to search for users 
 * 
 * @author dooley
 *
 */
public class UserQuery 
{
	private UserQueryType userQueryType;
	private String searchTerm;
	
	public UserQuery(UserQueryType userQueryType, String searchTerm) {
		super();
		this.userQueryType = userQueryType;
		this.searchTerm = searchTerm;
	}

	/**
	 * @return the queryType
	 */
	public UserQueryType getUserQueryType() {
		return userQueryType;
	}

	/**
	 * @param queryType the queryType to set
	 */
	public void setUserQueryType(UserQueryType userQueryType) {
		this.userQueryType = userQueryType;
	}

	/**
	 * @return the searchTerm
	 */
	public String getSearchTerm() {
		return searchTerm;
	}

	/**
	 * @param searchTerm the searchTerm to set
	 */
	public void setSearchTerm(String searchTerm) {
		this.searchTerm = searchTerm;
	}
}
