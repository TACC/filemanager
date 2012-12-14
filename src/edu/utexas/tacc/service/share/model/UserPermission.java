/**
 * 
 */

package edu.utexas.tacc.service.share.model;

import java.util.UUID;

import edu.utexas.tacc.service.share.model.enumerations.PermissionType;

/**
 * @author dooley
 *
 */
public class UserPermission {

	private PermissionType type;
	private String username;
	private String path;
	private String nonce;
	
	public UserPermission() {}

	public UserPermission(PermissionType type, String username, String path) {
		this.type = type;
		this.username = username;
		this.path = path;
		this.setNonce(UUID.randomUUID().toString());
	}

	/**
	 * @return the type
	 */
	public PermissionType getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(PermissionType type) {
		this.type = type;
	}

	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @return the path
	 */
	public String getPath() {
		return path;
	}

	/**
	 * @param path the path to set
	 */
	public void setPath(String path) {
		this.path = path;
	}
	
	/**
	 * @param nonce the nonce to set
	 */
	public void setNonce(String nonce) {
		this.nonce = nonce;
	}

	/**
	 * @return the nonce
	 */
	public String getNonce() {
		return nonce;
	}

	public String toString() {
		return path + " = " + type.toString() + ":" + username + ":" + nonce;
	}
	
	public boolean equals(Object o) {
		if (o instanceof UserPermission) {
			UserPermission pem = (UserPermission)o;
			return (type.equals(pem.type) && 
					username.equals(pem.username) && 
					path.equals(pem.path));
		}
		return false;
	}
	
}
