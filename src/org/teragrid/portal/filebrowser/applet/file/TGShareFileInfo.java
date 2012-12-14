/* 
 * Created on Aug 6, 2008
 * 
 * Developed by: Rion Dooley - dooley [at] tacc [dot] utexas [dot] edu
 * 				 TACC, Texas Advanced Computing Center
 * 
 * https://www.tacc.utexas.edu/
 */

package org.teragrid.portal.filebrowser.applet.file;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.globus.ftp.FileInfo;
import org.globus.ftp.exception.FTPException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.teragrid.portal.filebrowser.applet.ConfigOperation;
import org.teragrid.portal.filebrowser.applet.ConfigSettings;
import org.teragrid.portal.filebrowser.applet.util.LogManager;

/**
 * Extension class to map TeraGrid $SHARE ACLs to unix file permissions. $SHARE
 * nodes have 4 AC levels:
 * 
 * @author Rion Dooley < dooley [at] tacc [dot] utexas [dot] edu >
 * 
 */
public class TGShareFileInfo extends FileInfo implements GenericFileInfo {

	public static final String ALL = "all";
	public static final String READ = "read";
	public static final String WRITE = "write";
	public static final String OWNER = "owner";
	
	private String nonce = "";

	private int mode = 0;

	private Map<String,Integer> permissionMap;
	private String path;
	private boolean owner = false;
	private boolean shared = false;
	private String username = ConfigOperation.getInstance().getSiteByName(ConfigSettings.RESOURCE_NAME_TGSHARE).userName;
	
//	private boolean versioned = false;
	
	public TGShareFileInfo() {
		super();
	}

	public TGShareFileInfo(JSONObject jFile) throws JSONException {
		
		setName(jFile.getString("name"));
		setSize(jFile.getLong("length"));
		setDate(new SimpleDateFormat("MMM dd").format(getModified(jFile)));
		setTime(new SimpleDateFormat("HH:mm").format(getModified(jFile)));
		this.owner = jFile.getBoolean("owner");
		setFileType(getFileType(jFile));
		setPermissions(jFile.getJSONArray("permissions"));
//		setVersioned(jFile);
		this.path = jFile.getString("path");
		
		this.shared = jFile.getBoolean("shared");
		try {
			this.nonce = jFile.getString("id");
		} catch (JSONException e) {}
	}

	public void setMode(int mode) {
		this.mode = mode;
	}

	public void setPermissions(Object permissions) {
		
		this.permissionMap = new HashMap<String,Integer>();
		
		try {
			for (int i=0; i<((JSONArray)permissions).length(); i++) {
				JSONObject jPermission = ((JSONArray)permissions).getJSONObject(i);
				String uname = jPermission.getString("username");
				
				if (uname.equals(username)) {
					uname = "you";
				} else {
					this.shared = true;
				}
				
				if (permissionMap.containsKey(uname)) {
					permissionMap.put(uname, updateValue(permissionMap.get(uname),jPermission.getString("type").toLowerCase()));
				} else {
					permissionMap.put(uname, updateValue(0,jPermission.getString("type").toLowerCase()));
				}
			}
			
			if (!permissionMap.containsKey("you") && isOwner()) {
				permissionMap.put("you", updateValue(0, ALL));
			}
		} catch (JSONException e) {
			LogManager.error("Failed to parse permissions for file",e);
		}
		
		// setting mode is handy for the listing display, but really need to 
		// create a map of usernames and unix permission values.  Doing this
		// makes it easy to populate the permissions file info table.
		
		this.mode = 0;
		
		// TODO:  May have to filter out this user's permissions from the other 
		// users since they may not be the owner and owner permissions may not
		// be returned with the file listing.
		
		String unixPems = "";
		if (permissionMap.size() > 0) {
			unixPems = (isDirectory()?"d":"-") +
						  parsePermissions("you") + 
						  parsePermissions("group") + 
						  parsePermissions(null);
		} else {
			unixPems = "dr--------";
		}
		
		try {
			for (int i = 1; i <= 9; i++) {
				if (unixPems.charAt(i) != '-') {
					mode += 1 << (9 - i);
				}
			}
		} catch (IndexOutOfBoundsException e) {
			LogManager.error("Failed to set $SHARE permissions.", new FTPException(FTPException.UNSPECIFIED,
					"Could not parse access permission bits"));
		}		
	}
	
	private Integer updateValue(Integer oldVal, String authTypeName) {
		if (oldVal.intValue() == 7 || authTypeName.equalsIgnoreCase(OWNER)) { 
			return new Integer(7);
		} else if (oldVal.intValue() == 6 || 
				authTypeName.equals(ALL) || 
				authTypeName.equals(WRITE)) {
			return new Integer(6);
		} else if (authTypeName.equals(READ)) {
			return new Integer(oldVal.intValue() >= 6?oldVal:4);
		} else if (authTypeName.equals(WRITE)) {
			return new Integer(oldVal.intValue() >= 4?oldVal:2);
		} 
		
		return null;
	}
	
	private String parsePermissions(String authority) {
		char[] unixPems = "---".toCharArray();
		if (authority != null) {
			if (permissionMap.containsKey(authority)) { 
				return resolvePermissionValue(permissionMap.get(authority));
			} else {
				return "---";
			}
		} else {
			for (String key: permissionMap.keySet()) {
				if (key.equals("you") || key.equals("group")) {
				} else {
					if (permissionMap.get(key).intValue() == 7) {
						return "rwx";
					} else if (permissionMap.get(key).intValue() == 4) {
						unixPems[0] = 'r';
					} else if (permissionMap.get(key).intValue() == 6) {
						unixPems[1] = 'w';
						unixPems[0] = 'r';
					}
				} 
			}
		}
		return new String(unixPems);
	}

	private String resolvePermissionValue(int val) {
		if (val == 7) {
			return "rwx";
		} else if (val == 4) {
			return "r--";
		} else if (val == 6) {
			return "rw-";
		} else return "---";
	}
	
	public Object getPermissions() {
		return this.permissionMap;
	}

	public int getMode() {
		return mode;
	}

	public String getModeAsString() {
		StringBuffer modeStr = new StringBuffer();
		for (int j = 2; j >= 0; j--) {
			int oct = 0;
			for (int i = 2; i >= 0; i--) {
				if ((mode & (1 << j * 3 + i)) != 0) {
					oct += (int) Math.pow(2, i);
				}
			}
			modeStr.append(String.valueOf(oct));
		}
		return modeStr.toString();
	}

	public boolean userCanRead() {
		return ((mode & (1 << 8)) != 0);
	}

	public boolean userCanWrite() {
		return ((mode & (1 << 7)) != 0);
	}

	public boolean userCanExecute() {
		return ((mode & (1 << 6)) != 0);
	}

	public boolean groupCanRead() {
		return ((mode & (1 << 5)) != 0);
	}

	public boolean groupCanWrite() {
		return ((mode & (1 << 4)) != 0);
	}

	public boolean groupCanExecute() {
		return ((mode & (1 << 3)) != 0);
	}

	public boolean allCanRead() {
		return ((mode & (1 << 2)) != 0);
	}

	public boolean allCanWrite() {
		return ((mode & (1 << 1)) != 0);
	}

	public boolean allCanExecute() {
		return ((mode & (1 << 0)) != 0);
	}

//	public boolean isVersioned() {
//		return versioned;
//	}
	
//	private void setVersioned(Node node) {
//		versioned = node.getAspects().contains(ContentModel.ASPECT_VERSIONABLE);
//	}

	public Date getModified(JSONObject jFile) {
		try {
			return new SimpleDateFormat("EEE MMM d k:m:s zzz yyyy").parse(jFile.getString("lastModified"));
		} catch (ParseException e) {
			LogManager.error("Failed to parse file date", e);
		} catch (JSONException e) {
			LogManager.error("Failed to parse json file", e);
		}
		
		return new Date();
	}

	public byte getFileType(JSONObject jFile) {
		try {
			if (jFile.getString("type").equals("file")) {
				return FILE_TYPE;
			} else { 
				return DIRECTORY_TYPE;
			}
		} catch (JSONException e) {
			LogManager.error("Failed to parse json file", e);
			return UNKNOWN_TYPE;
		}
	}	
	
	public boolean isShared() {
		return this.shared;
	}
	
	public boolean isOwner() {
		return this.owner;
	}
	
	public String getPath() {
		return this.path;
	}
	
	public String getNonce() {
		return this.nonce;
	}
}