/**
 * 
 */
package org.teragrid.portal.filebrowser.applet.ui.permissions;

import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import org.globus.ftp.FileInfo;
import org.teragrid.portal.filebrowser.applet.util.LogManager;

/**
 * Default permissions class for files.  Does not allow modification
 * of any permissions.  Strictly for display purposes
 * 
 * @author dooley
 *
 */
@SuppressWarnings("serial")
public class DefaultPermissionsPanel extends PermissionsPanel {

	
	public DefaultPermissionsPanel(FileInfo fileInfo) {
		super(fileInfo);
		
	}

	@Override
	public void permissionStateChangePerformed(ItemEvent e) {
		LogManager.debug("This can't happen. Editing is not supported in " + 
				this.getClass().getName());
	}
	
	@Override
	public void permissionUpdateActionPerformed(ActionEvent e) {
		LogManager.debug("This can't happen. Editing is not supported in " + 
				this.getClass().getName());
	}

	@Override
	protected TableModel createPermissionTableModel(Object permissions) {
		
		int userPem = ((fileInfo.userCanRead()?4:0) + (fileInfo.userCanWrite()?2:0));
		
		String[][] values = new String[][]{
				{"you",UnixPermissions.getStringValue(userPem)},
				{"group",UnixPermissions.getStringValue(0)},
				{"everyone",UnixPermissions.getStringValue(0)}
		};
		
		DefaultTableModel tableModel = new DefaultTableModel(values, columnNames);
		
		return tableModel;
	}
	
	public static void main(String[] args) {
//		try {
//			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		
//		File file = new File("test.properties");
//		LocalFileInfo lf = new LocalFileInfo(file);
//		lf.setPermissions(644);
//		
//		DefaultPermissionsPanel panel = new DefaultPermissionsPanel(lf);
//		
//		JFrame frame = new JFrame("Permissions Panel");
//		frame.add(panel);
//		frame.pack();
//		frame.setVisible(true);
//		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
	}


}
