/**
 * 
 */
package org.teragrid.portal.filebrowser.applet.ui;

import org.globus.ftp.FileInfo;
import org.teragrid.portal.filebrowser.applet.transfer.FTPType;
import org.teragrid.portal.filebrowser.applet.ui.permissions.DefaultPermissionsPanel;
import org.teragrid.portal.filebrowser.applet.ui.permissions.GridFTPPermissionsPanel;
import org.teragrid.portal.filebrowser.applet.ui.permissions.IrodsPermissionsPanel;
import org.teragrid.portal.filebrowser.applet.ui.permissions.PermissionsPanel;
import org.teragrid.portal.filebrowser.applet.ui.permissions.TGSharePermissionsPanel;

/**
 * @author dooley
 *
 */
public class PermissionPanelFactory {

	public static PermissionsPanel getPermissionsPanel(FTPThread tBrowse, String path, FileInfo fileInfo) {
		if (FTPType.XSHARE == tBrowse.ftpServer.type) {
			return new TGSharePermissionsPanel(tBrowse,path,fileInfo);
		} else if (FTPType.GRIDFTP == tBrowse.ftpServer.type) {
			return new GridFTPPermissionsPanel(tBrowse,path,fileInfo);
		} else if (FTPType.IRODS == tBrowse.ftpServer.type) {
			return new IrodsPermissionsPanel(tBrowse,path,fileInfo);
		} else {
			return new DefaultPermissionsPanel(fileInfo);
		}
	}
}
