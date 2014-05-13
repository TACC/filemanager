/**
 * 
 */
package edu.utexas.tacc.wcs.filemanager.client.ui;

import org.globus.ftp.FileInfo;

import edu.utexas.tacc.wcs.filemanager.client.ui.permissions.DefaultPermissionsPanel;
import edu.utexas.tacc.wcs.filemanager.client.ui.permissions.GridFTPPermissionsPanel;
import edu.utexas.tacc.wcs.filemanager.client.ui.permissions.IrodsPermissionsPanel;
import edu.utexas.tacc.wcs.filemanager.client.ui.permissions.PermissionsPanel;
import edu.utexas.tacc.wcs.filemanager.common.model.enumeration.FileProtocolType;

/**
 * @author dooley
 *
 */
public class PermissionPanelFactory {

	public static PermissionsPanel getPermissionsPanel(FTPThread tBrowse, String path, FileInfo fileInfo) {
//		if (FTPType.XSHARE == tBrowse.ftpServer.type) {
//			return new TGSharePermissionsPanel(tBrowse,path,fileInfo);
//		} else 
		if (tBrowse.ftpServer.protocol.equals(FileProtocolType.GRIDFTP)) {
			return new GridFTPPermissionsPanel(tBrowse,path,fileInfo);
		} else if (tBrowse.ftpServer.protocol.equals(FileProtocolType.IRODS)) {
			return new IrodsPermissionsPanel(tBrowse,path,fileInfo);
		} else {
			return new DefaultPermissionsPanel(fileInfo);
		}
	}
}
