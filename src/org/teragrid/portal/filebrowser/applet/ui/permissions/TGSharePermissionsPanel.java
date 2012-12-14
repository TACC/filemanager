/**
 * 
 */
package org.teragrid.portal.filebrowser.applet.ui.permissions;

import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.io.IOException;
import java.util.Map;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import org.globus.ftp.FileInfo;
import org.globus.ftp.exception.ServerException;
import org.teragrid.portal.filebrowser.applet.AppMain;
import org.teragrid.portal.filebrowser.applet.file.TGShareFileInfo;
import org.teragrid.portal.filebrowser.applet.ui.DlgFindUser;
import org.teragrid.portal.filebrowser.applet.ui.FTPThread;
import org.teragrid.portal.filebrowser.applet.util.LogManager;

import com.explodingpixels.macwidgets.MacFontUtils;

/**
 * @author dooley
 *
 */
@SuppressWarnings("serial")
public class TGSharePermissionsPanel extends PermissionsPanel {

	private FTPThread tBrowse; // current active thread updating the file listing
	private String path; // relative path to the current file
	private JCheckBox chkInheritParentPermissions;
	private boolean haveInitChkInheritParentPermissionsValue = false;
	
	/**
	 * @param fileInfo
	 */
	public TGSharePermissionsPanel(FTPThread tBrowse, String path, FileInfo fileInfo) {
		super(fileInfo, true, true);
		this.tBrowse = tBrowse;
		this.path = path;
		if (!((TGShareFileInfo)fileInfo).isOwner()) {
			this.btnAdd.setEnabled(false);
			this.btnSub.setEnabled(false);
			this.tblPermissions.setEnabled(false);
		}
		
		//addInheritsPermissionCheckBox();
	}
	
	@SuppressWarnings("unused")
	private void addInheritsPermissionCheckBox() {
		chkInheritParentPermissions = new JCheckBox("Inherit Permissions");
		chkInheritParentPermissions.setFont(MacFontUtils.ITUNES_FONT);
		chkInheritParentPermissions.addChangeListener(new ChangeListener() {

			public void stateChanged(ChangeEvent e) {
				// toggle the inherited permissions
				if (haveInitChkInheritParentPermissionsValue) {
					tBrowse.cmdAdd("SetInherits", 
						path + "/" + fileInfo.getName(), 
						chkInheritParentPermissions.isSelected());
				}
			}
			
		});
		buttonBox.add(chkInheritParentPermissions);
		
		// find if it's selected
		tBrowse.cmdAdd("GetInherits",path + "/" + fileInfo.getName(),this);
	}

	public void setChkInheritParentPermissions(boolean inherits) {
		chkInheritParentPermissions.setSelected(inherits);
		haveInitChkInheritParentPermissionsValue = true; 
	}
	
	/* (non-Javadoc)
	 * @see org.teragrid.portal.filebrowser.applet.ui.permissions.PermissionsPanel#buttonActionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void permissionStateChangePerformed(ItemEvent e) {
		try {
//			String currentVal = (String)((JComboBox)e.getSource()).getSelectedItem();
			
			//String eventVal = (String)e.getItem();
//			if (!currentVal.equals(eventVal)) {
				updatePermissions((JComboBox)e.getSource(), 
					tblPermissions.rowAtPoint(((JComboBox)e.getSource()).getLocation()));
//			}
		} catch (Exception se) {
			LogManager.error("Failed to update permissions.",se);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	protected TableModel createPermissionTableModel(Object permissions) {
	
		Map<String,Integer> map = (Map<String,Integer>)permissions;
//		((TGShareFileInfo)fileInfo).getPermissions();
		
		String[][] values = new String[map.keySet().size()][2];
		int i=0;
		// We translated the TGShare permissions into unix permission values in
		// the fileinfo object.  Here we can parse the values for display.
		for(String uname: map.keySet()) {
			values[i++] = new String[]{uname,UnixPermissions.getStringValue((map.get(uname)))};
		}

		
		DefaultTableModel tableModel = new DefaultTableModel(values, columnNames);
		
		return tableModel;
	}
	
	
	

	/**
	 * Update the permissions on the file by calling the FTPClient's site() method
	 * via the SHGridFTP controller.
	 * 
	 * @throws ServerException
	 * @throws IOException
	 */
	public void updatePermissions(JComboBox cmbPermissions, int row) throws ServerException, IOException {
		
		// disallow duplicate permission entries
		if (isPresent((String)cmbPermissions.getSelectedItem(), row)) {
			throw new ServerException(1,"Permission already exists.");
		}
		
		if (row < 0 || row > tblPermissions.getRowCount()) 
			return;
		String username = (String)tblPermissions.getValueAt(row,0);
		
//		// delete the old permission
//		tBrowse.cmdAdd("Chmod", 
//				path + "/" + fileInfo.getName(), 
//				UnixPermissions.getUnixValue((String)tblPermissions.getValueAt(row,1)) * -1, 
//				true, 
//				username,
//				true);

		// add the new one
		tBrowse.cmdAdd("Chmod", 
				path + "/" + fileInfo.getName(), 
				UnixPermissions.getUnixValue((String)cmbPermissions.getSelectedItem()), 
				true, 
				username,
				true);
	}
	
	
	public boolean isPresent(String pString, int row) {
		String authorityName = (String)tblPermissions.getValueAt(row,0);

		// for all rows other than the currently changing one, check that the value pair does not
		// already exist.
		for(int i=0;i<tblPermissions.getRowCount();i++) {
			if (i != row && tblPermissions.getValueAt(i, 0).equals(authorityName) && 
					tblPermissions.getValueAt(i,1).equals(pString)) {
				return true;
			}
		}
		
		return false;
	}

//	private String resolveUnixPermission(String unixPermission) {
//		if (unixPermission.equals(UnixPermissions.READ)) {
//			return TGShareClient.READ_PERMISSION;
//		} else if (unixPermission.equals(UnixPermissions.WRITE)) {
//			return TGShareClient.WRITE_PERMISSION;
//		} else {
//			return TGShareClient.ALL_PERMISSION;
//		}
//	}
	
	@Override
	public void permissionUpdateActionPerformed(ActionEvent e) {
		if (e.getSource() == btnAdd) {
			DlgFindUser dlgFindUser = new DlgFindUser(AppMain.getFrame());
			String username = dlgFindUser.getSelectedUsername();
			
			if (username != null) {
				// remove the selected user permission
				
				tBrowse.cmdAdd("Chmod", 
						path + "/" + fileInfo.getName(), 
						UnixPermissions.getUnixValue(UnixPermissions.READ), 
						true, 
						username,
						true);
				if (username.equals("GROUP_EVERYONE")) username = "everyone";
				((DefaultTableModel)tblPermissions.getModel()).addRow(new String[]{username,UnixPermissions.READ});
				editors.add(new MyComboBoxEditor(UnixPermissions.getStringValues()));
			}
		} else if (e.getSource() == btnSub) {
			// disallow duplicate permission entries
			int row = tblPermissions.getSelectedRow();
			
			if (row < 0) {
				// no row selected
				return;
			}
			String username = (String)tblPermissions.getValueAt(row,0);
//			if (username.equals("everyone")) username = "GROUP_EVERYONE";
			// remove the selected user permission
			tBrowse.cmdAdd("Chmod", 
					path + "/" + fileInfo.getName(), 
					0, 
					true, 
					username,
					true);
			
			((DefaultTableModel)tblPermissions.getModel()).removeRow(row);
			editors.remove(row);
		}
	}

}
