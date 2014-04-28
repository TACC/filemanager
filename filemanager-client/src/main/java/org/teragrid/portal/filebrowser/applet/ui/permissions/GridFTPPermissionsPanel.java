/**
 * 
 */
package org.teragrid.portal.filebrowser.applet.ui.permissions;

import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.io.IOException;

import javax.swing.JComboBox;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import org.globus.ftp.FileInfo;
import org.globus.ftp.exception.ServerException;
import org.teragrid.portal.filebrowser.applet.ui.FTPThread;
import org.teragrid.portal.filebrowser.applet.util.LogManager;

/**
 * @author dooley
 *
 */
@SuppressWarnings("serial")
public class GridFTPPermissionsPanel extends PermissionsPanel {

	private FTPThread tBrowse;
	private String path;
	/**
	 * @param fileInfo
	 */
	public GridFTPPermissionsPanel(FTPThread tBrowse, String path, FileInfo fileInfo) {
		super(fileInfo, true);
		this.tBrowse = tBrowse;
		this.path = path;
	}

	/* (non-Javadoc)
	 * @see org.teragrid.portal.filebrowser.applet.ui.permissions.PermissionsPanel#buttonActionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void permissionStateChangePerformed(ItemEvent e) {
		try {
			
			updatePermissions((JComboBox)e.getSource(), 
					tblPermissions.rowAtPoint(((JComboBox)e.getSource()).getLocation()));
		} catch (Exception se) {
			LogManager.error("Failed to update permissions.",se);
		}
	}

	@Override
	protected TableModel createPermissionTableModel(Object permissions) {
		// GridFTP files have basic FileInfo permissions.  We can parse the 
		// mode value here.
		int user = 0;
		int group = 0;
		int all = 0;
		
		if (fileInfo.userCanRead()) user += 4;
		if (fileInfo.userCanWrite()) user += 2;
		if (fileInfo.userCanExecute()) user += 1;
		
		if (fileInfo.groupCanRead()) group += 4;
		if (fileInfo.groupCanWrite()) group += 2;
		if (fileInfo.groupCanExecute()) group += 1;
		
		if (fileInfo.allCanRead()) all += 4;
		if (fileInfo.allCanWrite()) all += 2;
		if (fileInfo.allCanExecute()) all += 1;
		
		String[][] values = new String[][]{
				{"you",UnixPermissions.getStringValue(user)},
				{"group",UnixPermissions.getStringValue(group)},
				{"everyone",UnixPermissions.getStringValue(all)}
		};
		
		DefaultTableModel tableModel = new DefaultTableModel(values, columnNames);
		
		return tableModel;
	}
	
	

	/**
	 * Update the permissions on the file by calling the FTPClient's setMode property
	 * via the SHGridFTP controller.
	 * 
	 * @throws ServerException
	 * @throws IOException
	 */
	public void updatePermissions(JComboBox cmbPermissions, int row) throws ServerException, IOException {
		
		if (row < 0 || row > 2) 
			return;

		int newPermissionCode = 0;
		
		for (int i=0;i<3;i++) {
			int value = 0;
			if (row == i) {
				value = UnixPermissions.getUnixValue((String)cmbPermissions.getSelectedItem());
			} else {
				value = UnixPermissions.getUnixValue((String)tblPermissions.getModel().getValueAt(i, 1));
			}
			newPermissionCode += value * Math.pow(10,2-i); 
		}
		
		tBrowse.cmdAdd("Chmod", path + "/" + fileInfo.getName(), new Integer(newPermissionCode), true, "", true);
	}
	
	@Override
	public void permissionUpdateActionPerformed(ActionEvent e) {
		if (e.getSource() == btnAdd) {
			LogManager.debug("This can't happen. Adding is not supported in " + 
					this.getClass().getName());
		} else if (e.getSource() == btnSub) {
			LogManager.debug("This can't happen. Subtracting is not supported in " + 
					this.getClass().getName());
		}
	}
	
	public static void main(String[] args) {
//		try {
//			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		
//		GridFTPPermissionsPanel panel = null;
//		AppMain.defaultCredential = GridFTP.authorize("/Users/dooley/xup_filemanager/proxies/x509up_u501");
//		
//		try {
//			FTPThread ftpThread = new FTPThread(new FTPSettings("ranger.tacc.utexas.edu"),true,this);
//            LogManager.info("Connecting to " + ftpServer.name);
//            tBrowse.start();
//            if(!currentDir.equals("")) {
//                tBrowse.cmdAdd("Cwd",currentDir,null);
//            }
//			SHGridFTP con = new SHGridFTP(new FTPSettings("ranger.tacc.utexas.edu"));
//			panel = new GridFTPPermissionsPanel(con,"",new GenericFileInfo((FileInfo)con.list("").get(1)));
//		} catch (ServerException e) {
//			e.printStackTrace();
//		} catch (ClientException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		
//		JFrame frame = new JFrame("Permissions Panel");
//		frame.add(panel);
//		frame.pack();
//		frame.setVisible(true);
//		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
	}

}
