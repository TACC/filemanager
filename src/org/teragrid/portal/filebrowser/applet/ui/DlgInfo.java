/* 
 * Created on Dec 10, 2007
 * 
 * Developed by: Rion Dooley - dooley [at] tacc [dot] utexas [dot] edu
 * 				 TACC, Texas Advanced Computing Center
 * 
 * https://www.tacc.utexas.edu/
 */

package org.teragrid.portal.filebrowser.applet.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

//import org.alfresco.service.cmr.version.Version;
import org.globus.ftp.FileInfo;
import org.globus.ftp.exception.ServerException;
import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.renderer.ComponentProvider;
import org.jdesktop.swingx.renderer.DefaultTreeRenderer;
import org.jdesktop.swingx.renderer.IconValue;
import org.jdesktop.swingx.renderer.LabelProvider;
import org.jdesktop.swingx.renderer.StringValue;
import org.jdesktop.swingx.treetable.DefaultTreeTableModel;
import org.jdesktop.swingx.treetable.TreeTableNode;
import org.teragrid.portal.filebrowser.applet.AppMain;
//import org.teragrid.portal.filebrowser.applet.file.TGShareFileInfo;
import org.teragrid.portal.filebrowser.applet.file.TGShareFileInfo;
import org.teragrid.portal.filebrowser.applet.transfer.FTPType;
import org.teragrid.portal.filebrowser.applet.ui.permissions.PermissionsPanel;
import org.teragrid.portal.filebrowser.applet.ui.table.DetailListModel;
import org.teragrid.portal.filebrowser.applet.ui.table.IconFactory;
import org.teragrid.portal.filebrowser.applet.ui.tree.AnimatedTreeUI;
import org.teragrid.portal.filebrowser.applet.util.LogManager;

import com.explodingpixels.macwidgets.IAppWidgetFactory;
import com.explodingpixels.macwidgets.MacFontUtils;

/**
 * Display dialog to show extended information on files. 
 * 
 * @author Rion Dooley < dooley [at] tacc [dot] utexas [dot] edu >
 * 
 */
@SuppressWarnings({"serial","unused"})
public class DlgInfo extends DlgEscape {
	private JPanel pnlMain = new JPanel();

	private JLabel lblTitle = new JLabel();

	private JTree treeDetailInfo = new JTree();
	private DefaultMutableTreeNode generalMenuTreeNode;
	private DefaultMutableTreeNode accessMenuTreeNode;
	private DefaultMutableTreeNode permissionMenuTreeNode;

	private JXTreeTable ttPermissions;
	private JXTreeTable ttAccess;
	private JXTreeTable ttVersion;
	private JXTreeTable ttGeneral;
	private PermissionsPanel pnlPermissions;
	private Box pemBox;
	private JTextArea txtVersion;
	private JScrollPane spVersion;
	
	private FTPThread tBrowse;
	private FileInfo fileInfo;
	private String path;

	private static boolean isGeneralNodeExpanded = true;
	private static boolean isAccessNodeExpanded = true;
	private static boolean isVersionNodeExpanded = false;
	private static boolean isPermissionNodeExpanded = true;

	class FileInfoTreeExpansionListener implements TreeExpansionListener {

		private DlgInfo adaptee = null;

		public FileInfoTreeExpansionListener(DlgInfo adaptee) {
			this.adaptee = adaptee;
		}
		
		public void treeCollapsed(TreeExpansionEvent e) {
			String nodeVal = ((FileInfoTreeNode)e.getPath().getLastPathComponent()).getString()[0];
			if (nodeVal.startsWith("Permission")) {
				isPermissionNodeExpanded = false;
				pemBox.setVisible(isPermissionNodeExpanded);
			} else if (nodeVal.startsWith("General")) {
				isGeneralNodeExpanded = false;
			} else if (nodeVal.startsWith("Access")) {
				isAccessNodeExpanded = false;
			} else if (nodeVal.startsWith("Version")) {
				isVersionNodeExpanded = false;
				spVersion.setVisible(isVersionNodeExpanded);
			}

			pnlMain.revalidate();
			validate();
			repaint();
		}

		public void treeExpanded(TreeExpansionEvent e) {
			String nodeVal = ((FileInfoTreeNode)e.getPath().getLastPathComponent()).getString()[0];
			if (nodeVal.startsWith("Permission")) {
				isPermissionNodeExpanded = true;
				pemBox.setVisible(isPermissionNodeExpanded);
			} else if (nodeVal.startsWith("General")) {
				isGeneralNodeExpanded = true;
			} else if (nodeVal.startsWith("Access")) {
				isAccessNodeExpanded = true;
			} else if (nodeVal.startsWith("Version")) {
//				isVersionNodeExpanded = true;
//				spVersion.setVisible(isVersionNodeExpanded);
			}
			pnlMain.revalidate();
//			pack();
			validate();
			repaint();
		}
	}

	public DlgInfo(Frame frame, FTPThread tBrowse, String path,
			FileInfo fileInfo, Component c) {
		super(frame, fileInfo.getName() + " Info", false);
		LogManager.debug("Displaying info for: " + fileInfo.toString());

		this.fileInfo = fileInfo;
		this.path = path;
		this.tBrowse = tBrowse;

		try {
			setDefaultCloseOperation(DISPOSE_ON_CLOSE);
			slInit();
//			jbInit();
			if (c == null) {
				locateDialog(frame);
			} else {
				setLocation(c.getX() + 25, c.getY() + 25);
			}
			setResizable(false);
			pack();
			treeDetailInfo.updateUI();
			setVisible(true);
			setFocusable(true);
			requestFocus();

		} catch (Exception ex) {
			LogManager.error("Failed to initialize the file info panel.", ex);
		}
	}

	private void slInit() {
		this.setTitle(fileInfo.getName() + " Info");
		// Create the top title label that serves as teh root node
		// that will never change.
		lblTitle.setIcon(getFileItemIcon());
		lblTitle.setText(getTitleHTML());
		lblTitle.setBorder(BorderFactory.createEmptyBorder(10, 5, 2, 5));
		
		String[][] generalTableRows = new String[][]{
				{"General:",""},
				{"Kind: ",DetailListModel.getTypeName(fileInfo)},
				{"Size: ",DetailListModel.getSize(fileInfo)},
				{"Where: ",getAddress()},
				{"Created: ",getCreated()},
				{"Modified: ",getCreated()}
		};
		ttGeneral = new JXTreeTable(new FileInfoTreeTableModel(generalTableRows));
//		ttGeneral.addTreeExpansionListener(new FileInfoTreeExpansionListener(
//				this));
		ttGeneral.setBorder(BorderFactory.createMatteBorder(1,0,0,0,Color.GRAY));
		ttGeneral.setTableHeader(null);
		ttGeneral.setRootVisible(true);
		ttGeneral.setEditable(false);
		ttGeneral.setColumnSelectionAllowed(false);
		ttGeneral.setCellSelectionEnabled(false);
		ttGeneral.setBackground(this.getBackground());
		ttGeneral.setSelectionBackground(this.getBackground());
		ttGeneral.setSelectionForeground(this.getForeground());
		
		ttGeneral.setTreeCellRenderer(new FileInfoTreeRenderer());
		ttGeneral.getColumn(0).setCellRenderer(new FileInfoTableCellRenderer());
		ttGeneral.getColumn(1).setCellRenderer(new FileInfoTableCellRenderer());
//		ttGeneral.setDefaultRenderer(Object.class, new FileInfoTableCellRenderer());
		ttGeneral.setLeafIcon(null);
		ttGeneral.setOpaque(false);
		ttGeneral.setEditable(false);
		ttGeneral.setSelectionBackground(this.getBackground());
		TableColumn col = ttGeneral.getColumn(0);
		col.setMinWidth(100);
		col.setMaxWidth(100);
		
		
		String[][] accessTableRows = new String[][]{
				{"Access:",""},
				{"Type:", FTPType.FTP_PROTOCOL[this.tBrowse.ftpServer.type]},
//				{"Type: ",DetailListModel.getTypeName(fileInfo)},
				{"System: ",this.tBrowse.ftpServer.name}
		};
		ttAccess = new JXTreeTable(new FileInfoTreeTableModel(accessTableRows));
//		ttAccess.addTreeExpansionListener(new FileInfoTreeExpansionListener(
//				this));
		ttAccess.setRowSelectionAllowed(false);
		ttAccess.setEditable(false);
		ttAccess.setColumnSelectionAllowed(false);
		ttAccess.setCellSelectionEnabled(false);
		ttAccess.setBorder(BorderFactory.createMatteBorder(1,0,0,0,Color.GRAY));
		ttAccess.setBackground(this.getBackground());
		ttAccess.setTableHeader(null);
		ttAccess.setRootVisible(true);
		ttAccess.setTreeCellRenderer(new FileInfoTreeRenderer());
		ttAccess.getColumn(0).setCellRenderer(new FileInfoTableCellRenderer());
		ttAccess.getColumn(1).setCellRenderer(new FileInfoTableCellRenderer());
//		ttAccess.setDefaultRenderer(Object.class, new FileInfoTableCellRenderer());
		ttAccess.setLeafIcon(null);
		ttAccess.setOpaque(false);
		ttAccess.setEditable(false);
		ttAccess.setSelectionBackground(this.getBackground());
		col = ttAccess.getColumn(0);
		col.setMinWidth(100);
		col.setMaxWidth(100);
		// Version info for TGShare files -- uncomment to add back support
//		if (tBrowse.ftpServer.type == FTPType.TGSHARE) {
//			Version version;
//			String[][] versionTableRows;
//			try {
//				if (((TGShareFileInfo)fileInfo).isVersioned()) {
//					version = tBrowse.ftpSrvConn.getCurrentVersion(path + "/" + fileInfo.getName());
//					versionTableRows = new String[][]{
//							{"Version:",""},
//							{"Label: ",version.getVersionLabel()},
//							{"Date: ",formatDate(version.getCreatedDate())},
//							{"Creator: ",version.getCreator()},
//							{"Comments: ",null}
//					};
//					txtVersion = new JTextArea((String)version.getVersionProperty(Version.PROP_DESCRIPTION));
////					txtVersion.setAlignmentX(LEFT_ALIGNMENT);
//				} else {
//					versionTableRows = new String[][]{
//							{"Version:",""},
//							{"No version info",null}
//					};
//					txtVersion = new JTextArea();
//				}
//			} catch (ServerException e) {
//				versionTableRows = new String[][]{
//						{"Version:",""},
//						{"No version info",null}
//				};
//				txtVersion = new JTextArea();
//			}
//			
//			ttVersion = new JXTreeTable(new FileInfoTreeTableModel(versionTableRows));
//			ttVersion.addTreeExpansionListener(new FileInfoTreeExpansionListener(
//					this));
//			ttVersion.setRowSelectionAllowed(false);
//			ttVersion.setEditable(false);
//			ttVersion.setColumnSelectionAllowed(false);
//			ttVersion.setCellSelectionEnabled(false);
//			ttVersion.setBorder(BorderFactory.createMatteBorder(1,0,0,0,Color.GRAY));
//			ttVersion.setBackground(this.getBackground());
//			ttVersion.setTableHeader(null);
//			ttVersion.setRootVisible(true);
//			ttVersion.setTreeCellRenderer(new FileInfoTreeRenderer());
//			ttVersion.getColumn(0).setCellRenderer(new FileInfoTableCellRenderer());
//			ttVersion.getColumn(1).setCellRenderer(new FileInfoTableCellRenderer());
//	//		ttAccess.setDefaultRenderer(Object.class, new FileInfoTableCellRenderer());
//			ttVersion.setLeafIcon(null);
//			ttVersion.setOpaque(false);
//			ttVersion.setEditable(false);
//			ttVersion.setSelectionBackground(this.getBackground());
//			col = ttVersion.getColumn(0);
//			col.setMinWidth(150);
//			col.setMaxWidth(170);
//			
//			txtVersion.setBackground(Color.WHITE);
//			txtVersion.setFont(MacFontUtils.SOURCE_LIST_ITEM_FONT);
//			txtVersion.setLineWrap(true);
//			txtVersion.setWrapStyleWord(true);
//			txtVersion.addFocusListener(new VersionCommentFocusListener(this));
//			
//			spVersion = new JScrollPane(txtVersion);
//			spVersion.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
//			spVersion.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
//			spVersion.setWheelScrollingEnabled(true);
//			spVersion.setPreferredSize(new Dimension(225,75));
//			spVersion.setMaximumSize(new Dimension(280,100));
//			spVersion.setVisible(isVersionNodeExpanded);
//			IAppWidgetFactory.makeIAppScrollPane(spVersion);
//			
//		}
		
		
		
		String[][] permissionTableRows = new String[][]{
				{"Permissions:",""},
				{"You can: ",getPermission(fileInfo.userCanRead(), fileInfo
						.userCanWrite(), fileInfo.userCanExecute())}
		};
		ttPermissions = new JXTreeTable(new FileInfoTreeTableModel(permissionTableRows));
		ttPermissions.addTreeExpansionListener(new FileInfoTreeExpansionListener(
				this));
		ttPermissions.setBorder(BorderFactory.createMatteBorder(1,0,0,0,Color.GRAY));
		ttPermissions.setTableHeader(null);
		ttPermissions.setRootVisible(true);
		ttPermissions.setEditable(false);
		ttPermissions.setColumnSelectionAllowed(false);
		ttPermissions.setCellSelectionEnabled(false);
		ttPermissions.setBackground(this.getBackground());
		ttPermissions.setTreeCellRenderer(new FileInfoTreeRenderer());
		ttGeneral.getColumn(0).setCellRenderer(new FileInfoTableCellRenderer());
		ttPermissions.getColumn(1).setCellRenderer(new FileInfoTableCellRenderer());
//		ttPermissions.setLeafIcon(null);
		ttPermissions.setOpaque(false);
		ttPermissions.setSelectionBackground(this.getBackground());
		col = ttPermissions.getColumn(0);
		col.setMinWidth(100);
		col.setMaxWidth(120);
		
		pnlPermissions = PermissionPanelFactory.getPermissionsPanel(tBrowse,
				path, fileInfo);
		pnlPermissions.setVisible(isPermissionNodeExpanded);

		JPanel treePanel = new JPanel();
		treePanel.setLayout(new BoxLayout(treePanel,BoxLayout.Y_AXIS));
		treePanel.add(ttGeneral);
		treePanel.add(ttAccess);
		if (tBrowse.ftpServer.type == FTPType.XSHARE) {
//			Box bVersion = Box.createHorizontalBox();
//			bVersion.add(spVersion);
//			bVersion.add(Box.createHorizontalGlue());
//			bVersion.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
//			treePanel.add(ttVersion);
////			treePanel.add(new JLabel("test label"));
//			treePanel.add(bVersion);
		}
		treePanel.add(ttPermissions);
		treePanel.setOpaque(true);
		treePanel.setBackground(this.getBackground());
		
		treeDetailInfo.setAlignmentX(LEFT_ALIGNMENT);

		pemBox = Box.createHorizontalBox();
		pemBox.add(pnlPermissions);
		pemBox.add(Box.createHorizontalGlue());
		pnlPermissions.setAlignmentX(Component.LEFT_ALIGNMENT);

		Box scrollBox = Box.createVerticalBox();
		scrollBox.add(treePanel);
		scrollBox.add(pemBox);
		scrollBox.add(Box.createGlue());
		scrollBox.setOpaque(true);
		scrollBox.setBackground(this.getBackground());
		
		if (isGeneralNodeExpanded)
			ttGeneral.expandAll();
		if (isAccessNodeExpanded)
			ttAccess.expandAll();
		if (isVersionNodeExpanded && (tBrowse.ftpServer.type == FTPType.XSHARE))
//			ttVersion.expandAll();
		if (isPermissionNodeExpanded)
			ttPermissions.expandAll();
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.getViewport().add(scrollBox);
		scrollPane.setWheelScrollingEnabled(true);
		IAppWidgetFactory.makeIAppScrollPane(scrollPane);
		scrollPane.setAutoscrolls(true);

		pnlMain.setLayout(new BoxLayout(pnlMain, BoxLayout.Y_AXIS));
		pnlMain.add(lblTitle, Component.TOP_ALIGNMENT);
		pnlMain.add(scrollPane, Component.CENTER_ALIGNMENT);
		pnlMain.add(Box.createGlue());
		lblTitle.setAlignmentX(JPanel.TOP_ALIGNMENT);
		scrollPane.setAlignmentX(JPanel.CENTER_ALIGNMENT);
		scrollPane.setPreferredSize(new Dimension(280, 600));
		
		
		// pnlMain.setMinimumSize(new Dimension(280, 480));
		pnlMain.setPreferredSize(new Dimension(280, 620));
		pnlMain.setOpaque(true);
		
		getContentPane().add(pnlMain);// , JDialog.LEFT_ALIGNMENT);

	}

	private void jbInit() {

		this.setTitle(fileInfo.getName() + " Info");
		treeDetailInfo.setUI(new AnimatedTreeUI());
		// treeDetailInfo.setBackground(this.getBackground());
		treeDetailInfo.setOpaque(false);
		treeDetailInfo.setCellRenderer(new FileInfoTreeCellRenderer());
		treeDetailInfo.setShowsRootHandles(true);
		treeDetailInfo.setEditable(false);

		// Create the top title label that serves as teh root node
		// that will never change.
		lblTitle.setIcon(getFileItemIcon());
		lblTitle.setText(getTitleHTML());
		// Create the expanding General Info field
		DefaultMutableTreeNode root = new DefaultMutableTreeNode();
		treeDetailInfo.setModel(new DefaultTreeModel(root));
		treeDetailInfo.setRootVisible(false);

		generalMenuTreeNode = new DefaultMutableTreeNode("General:");
		// "<html><body><p>General:</p></body></html>");
		root.add(generalMenuTreeNode);

		DefaultMutableTreeNode nKind = new DefaultMutableTreeNode(new String[] {
				"Kind", DetailListModel.getTypeName(fileInfo) });
		nKind.setAllowsChildren(false);
		generalMenuTreeNode.add(nKind);

		DefaultMutableTreeNode nSize = new DefaultMutableTreeNode(new String[] {
				"Size", DetailListModel.getSize(fileInfo) });
		nSize.setAllowsChildren(false);
		generalMenuTreeNode.add(nSize);

		DefaultMutableTreeNode nWhere = new DefaultMutableTreeNode(
				new String[] { "Where", getAddress() });
		nWhere.setAllowsChildren(false);
		generalMenuTreeNode.add(nWhere);

		DefaultMutableTreeNode nCreated = new DefaultMutableTreeNode(
				new String[] { "Created", getCreated() });
		nCreated.setAllowsChildren(false);
		generalMenuTreeNode.add(nCreated);

		DefaultMutableTreeNode nModified = new DefaultMutableTreeNode(
				new String[] { "Modified", getCreated() });
		nModified.setAllowsChildren(false);
		generalMenuTreeNode.add(nModified);

		// Create the expandable access panel
		accessMenuTreeNode = new DefaultMutableTreeNode(
				"<html><body><p>Access:</p></body></html>");
		root.add(accessMenuTreeNode);

		DefaultMutableTreeNode nAccess = new DefaultMutableTreeNode(
				new String[] { "Type",
						FTPType.FTP_PROTOCOL[this.tBrowse.ftpServer.type] });
		nAccess.setAllowsChildren(false);
		accessMenuTreeNode.add(nAccess);

		DefaultMutableTreeNode nServer = new DefaultMutableTreeNode(
				new String[] { "System", this.tBrowse.ftpServer.name });
		nServer.setAllowsChildren(false);
		accessMenuTreeNode.add(nServer);

		// Create the expandable permissions panel
		permissionMenuTreeNode = new DefaultMutableTreeNode(
				"<html><body><p>Ownership & Permissions:</p></body></html>");
		permissionMenuTreeNode.setAllowsChildren(true);
		root.add(permissionMenuTreeNode);

		DefaultMutableTreeNode youNode = new DefaultMutableTreeNode(
				new String[] {
						"You can",
						getPermission(fileInfo.userCanRead(), fileInfo
								.userCanWrite(), fileInfo.userCanExecute()) });

		youNode.setAllowsChildren(false);
		permissionMenuTreeNode.add(youNode);

		pnlPermissions = PermissionPanelFactory.getPermissionsPanel(tBrowse,
				path, fileInfo);
		pnlPermissions.setVisible(isPermissionNodeExpanded);

		// expand the General info node by default
		treeDetailInfo
				.addTreeExpansionListener(new FileInfoTreeExpansionListener(
						this));
		// treeDetailInfo.setBackground(this.getBackground());

		if (isGeneralNodeExpanded)
			treeDetailInfo.expandPath(new TreePath(generalMenuTreeNode
					.getPath()));
		if (isAccessNodeExpanded)
			treeDetailInfo
					.expandPath(new TreePath(accessMenuTreeNode.getPath()));
		if (isPermissionNodeExpanded)
			treeDetailInfo.expandPath(new TreePath(permissionMenuTreeNode
					.getPath()));

		JPanel treePanel = new JPanel();
		// treePanel.setLayout(new BoxLayout(treePanel,BoxLayout.LINE_AXIS));
		treePanel.add(treeDetailInfo);
		treePanel.setOpaque(false);
		treeDetailInfo.setAlignmentX(LEFT_ALIGNMENT);

		Box pemBox = Box.createHorizontalBox();
		pemBox.add(pnlPermissions);
		pemBox.add(Box.createHorizontalGlue());
		pnlPermissions.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		Box scrollBox = Box.createVerticalBox();
		scrollBox.add(treePanel);
		scrollBox.add(pemBox);
		scrollBox.add(Box.createGlue());
		scrollBox.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.getViewport().add(scrollBox);
		scrollPane.getViewport().setBackground(this.getBackground());
		scrollPane.getViewport().setOpaque(true);
		scrollPane.setWheelScrollingEnabled(true);
		IAppWidgetFactory.makeIAppScrollPane(scrollPane);
		scrollPane.setAutoscrolls(true);
		
		pnlMain.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		pnlMain.setLayout(new BoxLayout(pnlMain, BoxLayout.Y_AXIS));
		pnlMain.add(lblTitle, Component.TOP_ALIGNMENT);
		pnlMain.add(scrollPane, Component.CENTER_ALIGNMENT);
		pnlMain.add(Box.createGlue());
		lblTitle.setAlignmentX(JPanel.TOP_ALIGNMENT);
		scrollPane.setAlignmentX(JPanel.CENTER_ALIGNMENT);
		scrollPane.setPreferredSize(new Dimension(280, 450));
		scrollPane.setBackground(Color.GRAY);
		
		// pnlMain.setMinimumSize(new Dimension(280, 480));
		pnlMain.setPreferredSize(new Dimension(280, 480));
		pnlMain.setBackground(Color.BLACK);
		
		getContentPane().add(pnlMain);// , JDialog.LEFT_ALIGNMENT);

	}

	private Icon getFileItemIcon() {
		Icon icn = null;

		if (this.tBrowse.ftpServer.type == FTPType.FILE) {
			try {
				JFileChooser chooser = new JFileChooser();
				File file = new File(path + File.separator + fileInfo.getName());
				if (file.exists())
					icn = chooser.getIcon(file);
				else
					icn = (fileInfo.isDirectory() ? AppMain.icoFolderLarge
							: AppMain.icoFileLarge);
			} catch (Exception e) {
				icn = (fileInfo.isDirectory() ? AppMain.icoFolderLarge
						: AppMain.icoFileLarge);
			}
		} else if (this.tBrowse.ftpServer.type == FTPType.XSHARE) {
			icn = fileInfo.isDirectory() ? AppMain.icoFolderLarge
					: AppMain.icoFileLarge;
			
			if (((TGShareFileInfo)fileInfo).isShared()) {
				icn = IconFactory.getOverlayIcon(this, new ImageIcon(IconFactory.iconToImage(icn)), 
        			AppMain.icoShareItem16, SwingConstants.SOUTH_EAST);
			}
		} else {
			icn = (fileInfo.isDirectory() ? AppMain.icoFolderLarge
					: AppMain.icoFileLarge);
		}

		return icn;
	}

	private String getPermission(boolean read, boolean write, boolean execute) {
		return (read ? "Read" + (write ? " & Write" : "")
				+ (execute ? " & Execute" : "") : (write ? "Write"
				+ (execute ? " & Execute" : "") : (execute ? "Execute"
				: "No Access")));
	}

	private String getTitleHTML() {
		String txtTitle = "<html><body><table>";
		txtTitle += "<tr><td><b>" + fileInfo.getName() + "</b></td>" + "<td>"
				+ DetailListModel.getSize(fileInfo) + "</td></tr>";
		txtTitle += "<tr colspan=\"2\"><td><small>Modified: " + getCreated()
				+ "</small></td></tr>";
		txtTitle += "</body></html>";

		return txtTitle;
	}

	private String getCreated() {
		String[] daytime = fileInfo.getTime().split(
				((fileInfo.getTime().indexOf(":") > -1) ? ":" : " "));

		if (daytime.length < 2) {
			return fileInfo.getDate() + ", " + fileInfo.getTime();
		}

		int hour = new Integer(daytime[0]).intValue();
		int minutes = new Integer(daytime[1]).intValue();
		int seconds = daytime.length > 2 ? new Integer(daytime[2]).intValue()
				: -1;
		String meridian = " AM";
		if (hour > 12) {
			hour -= 12;
			meridian = " PM";
		}

		return fileInfo.getDate() + ", " + hour + ":" + minutes
				+ ((seconds > -1) ? ":" + seconds : "") + meridian;

	}
	
	private String getAddress() {
		String address = path + (path.indexOf("\\") > -1 ? "\\" : "/")
				+ fileInfo.getName();
		return address;
	}
	
	// uncomment when adding share support
//	protected void updateVersionComment() {
//		try {
//			tBrowse.ftpSrvConn.setVersionComment(path + "/" + fileInfo.getName(),txtVersion.getText());
//		} catch (ServerException e) {
//			AppMain.Error(this,e.getMessage());
//		}
//	}
	
	private String formatDate(Date date) {
		String formatString;
		Calendar cal = Calendar.getInstance();
		Calendar now = Calendar.getInstance();
		cal.setTime(date);
		
		if ((cal.get(Calendar.DAY_OF_YEAR) == now.get(Calendar.DAY_OF_YEAR)) && 
				(cal.get(Calendar.YEAR) == now.get(Calendar.YEAR))) {
			formatString = "K:mm a";
		} else {
			cal.add(Calendar.DAY_OF_YEAR, 7);
			
			if ((cal.after(now))) {
				formatString = "E K:mm a";
			} else {
				formatString = "MMM d, k:mm a";
			}
		}
		return new SimpleDateFormat(formatString).format(date);
	}

	
}

//uncomment when adding share support
//class VersionCommentFocusListener implements FocusListener {
//	DlgInfo adaptee;
//	String oldText = "";
//	public VersionCommentFocusListener(DlgInfo adaptee) {
//		this.adaptee = adaptee;
//	}
//	
//	public void focusGained(FocusEvent e) {
//		oldText = ((JTextArea)e.getSource()).getText();
//	}
//
//	public void focusLost(FocusEvent e) {
//		if (!((JTextArea)e.getSource()).getText().equals(oldText)) {
//			adaptee.updateVersionComment();
//		}
//	}
//	
//}

@SuppressWarnings("serial")
class FileInfoTreeRenderer extends DefaultTreeRenderer {
	LabelProvider provider = new LabelProvider();
	
	public FileInfoTreeRenderer() {
		super(IconValue.NONE, new StringValue() {
			public String getString(Object value) {
				return ((String[])value)[0];
			}
		});
	}

	
	@SuppressWarnings("unchecked")
	@Override
	protected ComponentProvider createDefaultComponentProvider() {
		ComponentProvider provider = super.createDefaultComponentProvider();
		provider.setHorizontalAlignment(JLabel.RIGHT);
		return provider;
	}


	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean selected, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {
		
		Component c = super.getTreeCellRendererComponent(tree, value, selected, expanded,
				leaf, row, hasFocus);
		c.setFont(MacFontUtils.ITUNES_FONT);
		c.setForeground(tree.getForeground());
		c.setBackground(tree.getBackground());
		setBackground(tree.getBackground());
		
		return c;
	}
	
}


@SuppressWarnings("serial")
class FileInfoTableCellRenderer extends JLabel implements TableCellRenderer {

	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		
		setFont(MacFontUtils.ITUNES_FONT);
		setText((String)value);
//		setBackground(table.getBackground());
//		setMaximumSize(new Dimension(115,25));
		setOpaque(false);
//		if (column == 0) {
//			setHorizontalAlignment(JLabel.RIGHT);
//		}
		
		return this;
	}
	
}

@SuppressWarnings("serial")
class FileInfoTreeCellRenderer extends JLabel implements TreeCellRenderer {

	protected Color m_textSelectionColor;
	protected Color m_textNonSelectionColor;
	protected Color m_bkSelectionColor;
	protected Color m_bkNonSelectionColor;
	protected Color m_borderSelectionColor;

	protected boolean m_selected;

	public FileInfoTreeCellRenderer() {
		super();
	}

	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean sel, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;

		Object obj = node.getUserObject();

//		if (obj instanceof String[]) {
//			setText(((String[]) obj)[0], ((String[]) obj)[1]);
//			// setText(((String[])obj)[0] + ": " + ((String[])obj)[1]);
//			setToolTipText(((String[]) obj)[1]);
		if (obj instanceof String) {
			setText((String) obj);
			if (((String)obj).contains(":"))
				setHorizontalAlignment(JLabel.RIGHT);
		}
		setBackground(null);
		setFont(MacFontUtils.ITUNES_FONT);
		m_selected = sel;
		return this;
	}

	public void setText(String label, String value) {
		setText("<html>"
				+ "<body>"
				+ "<table>"
				+ "<tr>"
				+ "<td align=\"right\" width=\"25\">"
				+ label
				+ ": </td>"
				+ "<td align=\"left\">"
				+ (((label.length() + value.length()) > 30) ? (value.substring(
						0, (30 - label.length())) + "...") : value) + "</td>"
				+ "</tr>" + "</table>" + "</body>" + "</html>");
	}
}

class FileInfoTreeNode implements TreeTableNode {
	private String[] lineItem;
	private FileInfoTreeNode parent;
	private List<TreeTableNode> children = new ArrayList<TreeTableNode>();
	private boolean allowsChildren;

	public FileInfoTreeNode(String[][] lineItems, FileInfoTreeNode aParent) {
		
		this.lineItem = lineItems[0];
		parent = aParent;
		if (parent != null) parent.allowsChildren = true;

		for (int i=1;i<lineItems.length;i++) {
			FileInfoTreeNode childNode = new FileInfoTreeNode(new String[][]{lineItems[i]}, this);
			childNode.allowsChildren = false;
			children.add(childNode);
		}
	}

	public String[] getString() {
		return lineItem;
	}

	public TreeTableNode getChildAt(int aIndex) {
		return children.get(aIndex);
	}

	public int getChildCount() {
		return children.size();
	}

	public TreeTableNode getParent() {
		return parent;
	}

	public int getIndex(TreeNode aNode) {
		return children.indexOf(aNode);
	}

	public boolean getAllowsChildren() {
		return allowsChildren;
	}

	public boolean isLeaf() {
		return false;
	}

	@SuppressWarnings("unchecked")
	public Enumeration children() {
		return new IteratorEnumeration(children.iterator());
	}

	public String toString() {
		return lineItem[0];
	}

	public int getColumnCount() {
		return lineItem.length;
	}

	public Object getUserObject() {
		return lineItem;
	}

	public Object getValueAt(int col) {
		return lineItem[col];
	}

	public boolean isEditable(int arg0) {
		return false;
	}

	public void setUserObject(Object arg0) {}

	public void setValueAt(Object arg0, int arg1) {}
}

class FileInfoTreeTableModel extends DefaultTreeTableModel {
	private String[][] lineItems;

	public FileInfoTreeTableModel(String[][] lineItems) {
		super(new FileInfoTreeNode(lineItems,null));
		this.lineItems = lineItems;
	}

	public String[][]  getLineItems() {
		return lineItems;
	}

	public int getColumnCount() {
		return 2;
	}

	public String getColumnName(int column) {
		if(column == 0) {
			return "Property";
		} else {
			return "Value";
		}
	}

	public Object getValueAt(Object aObject, int aColumn) {
		FileInfoTreeNode vTreeNode = (FileInfoTreeNode)aObject;
		return vTreeNode.getValueAt(aColumn);
	}
}

@SuppressWarnings("unchecked")
class IteratorEnumeration implements Enumeration {
	Iterator iterator;

	public IteratorEnumeration(Iterator aIterator) {
		iterator = aIterator;
	}

	public boolean hasMoreElements() {
		return iterator.hasNext();
	}

	public Object nextElement()	{
		return iterator.next();
	}

}
