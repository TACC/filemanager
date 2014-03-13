package org.teragrid.portal.filebrowser.applet.ui.permissions;

public class UnixPermissions {
	public static String NONE = "None";
	public static String READ = "Read only";
	public static String WRITE = "Write only";
	public static String EXECUTE = "Execute only";
	public static String READWRITE = "Read & write";
	public static String READEXECUTE = "Read & execute";
	public static String WRITEEXECUTE = "Write & execute";
	public static String ALL = "All";
	
	private static final String[] permissionValues = new String[]{NONE,EXECUTE,WRITE,WRITEEXECUTE,READ,READEXECUTE,READWRITE,ALL};
	
	public static int getUnixValue(String sValue) {
		for(int i=0;i<permissionValues.length;i++) {
			if (permissionValues[i].equals(sValue)) 
				return i;
		}
		
		return permissionValues.length + 1; 
	}
	
	public static String getStringValue(int uValue) {
		if (uValue > permissionValues.length || uValue < 0) {
			return "Unknown";
		} else {
			return permissionValues[uValue];
		}
	}
	
	public static String[] getStringValues() {
		return permissionValues;
	}
}
