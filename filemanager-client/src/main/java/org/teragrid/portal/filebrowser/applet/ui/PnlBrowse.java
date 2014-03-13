/* 
 * Created on Jan 23, 2008
 * 
 * Developed by: Rion Dooley - dooley [at] tacc [dot] utexas [dot] edu
 * 				 TACC, Texas Advanced Computing Center
 * 
 * https://www.tacc.utexas.edu/
 */

package org.teragrid.portal.filebrowser.applet.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JToggleButton;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.AbstractBorder;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.globus.ftp.FileInfo;
import org.globus.ftp.exception.ClientException;
import org.globus.ftp.exception.ServerException;
import org.teragrid.portal.filebrowser.applet.AppMain;
import org.teragrid.portal.filebrowser.applet.ConfigOperation;
import org.teragrid.portal.filebrowser.applet.ConfigSettings;
import org.teragrid.portal.filebrowser.applet.file.IRODSFileInfo;
import org.teragrid.portal.filebrowser.applet.transfer.EnvironmentThread;
import org.teragrid.portal.filebrowser.applet.transfer.FTPSettings;
import org.teragrid.portal.filebrowser.applet.transfer.FTPType;
import org.teragrid.portal.filebrowser.applet.transfer.Irods;
import org.teragrid.portal.filebrowser.applet.ui.table.CTable;
import org.teragrid.portal.filebrowser.applet.ui.table.DetailListModel;
import org.teragrid.portal.filebrowser.applet.util.FileUtils;
import org.teragrid.portal.filebrowser.applet.util.LogManager;
import org.teragrid.portal.filebrowser.applet.util.ResourceName;
import org.teragrid.portal.filebrowser.applet.util.SGGCResourceBundle;
import org.teragrid.portal.filebrowser.applet.util.SwingWorker;
import org.teragrid.service.profile.wsclients.model.EnvironmentVariable;

import com.explodingpixels.macwidgets.IAppWidgetFactory;

import edu.utexas.tacc.wcs.filemanager.common.util.DBUtil;

/**
 * Panel displaying directory listings and navigation dropdown.
 * 
 * @author Rion Dooley < dooley [at] tacc [dot] utexas [dot] edu >
 * 
 */
@SuppressWarnings({"serial","unused","unchecked"})
public class PnlBrowse extends JPanel {
	protected String separator = "/";
	protected AppMain frmParent;
	protected FTPSettings ftpServer;
	protected boolean bRemoteMode;
	public FTPThread tBrowse = null;
	public EnvironmentThread tEnv = null;

	private SystemPathComboBoxModel mdlSystemPath;

	private String currentDir = "";
	public JComboBox cmbPath = new JComboBox();
//	public JComboBox cmbSite;
	public JLabel lblSite;
	public CTable tblListing;

	public JPanel pnlPath = new JPanel();
	public JButton btnUpDir = new JButton();

	protected ActionListener evtMenu = new PnlBrowse_mnuFile_actionAdapter(this);
	protected ActionListener evtCmb = new PnlBrowse_cmbDir_actionAdapter(this);

	protected JButton btnHome = new JButton();
	protected JButton btnHistory = new JButton();
	protected JButton btnGoTo = new JButton();
	protected JButton btnReload = new JButton();
	protected JButton btnInfo = new JButton();
	protected JButton btnEdit = new JButton();
	protected JButton btnNewFolder = new JButton();
	protected JButton btnDelete = new JButton();
	protected JButton btnDisconnect = new JButton();
	protected JButton btnPrefs = new JButton();
	protected JButton btnPrediction = new JButton();
	
	protected JButton btnTGHome = new JButton();
	protected JButton btnTGWork = new JButton();
	protected JButton btnTGScratch = new JButton();
	protected JButton btnTGArchive = new JButton();

	protected JToggleButton btnBookmarks;
	protected JToggleButton btnSearch;
	protected JToggleButton btnSites;
	protected JToggleButton btnEnvironment;
	
	protected JPopupMenu mnuFile = new JPopupMenu();
	protected JMenuItem mnuFileUp = new JMenuItem();// Upload Selected
	protected JMenuItem mnuFileDown = new JMenuItem();// Download Selected
	protected JMenuItem mnuFileInfo = new JMenuItem();// Show file info
	protected JMenuItem mnuFileACL = new JMenuItem(); // Show s3 ACL panel
	protected JMenuItem mnuFileAddDir = new JMenuItem();// New Directory
	protected JMenuItem mnuFileAdd = new JMenuItem();// New File
	protected JMenuItem mnuFileDel = new JMenuItem();// Deleted Selected
	protected JMenuItem mnuFileRen = new JMenuItem();// Rename
	protected JMenuItem mnuFileDirUp = new JMenuItem();// Go up
	protected JMenuItem mnuFileRefresh = new JMenuItem();// Refresh
	protected JMenuItem mnuFileOpen = new JMenuItem();// Open
	protected JMenuItem mnuFileDisconn = new JMenuItem();// Disconnect
	protected JMenuItem mnuFileHome = new JMenuItem();// Go Home
	protected JMenuItem mnuFileBookmark = new JMenuItem();// Go Home
	protected JMenuItem mnuFileCmd = new JMenuItem();// Input Raw Command
	protected JMenuItem mnuFileGo = new JMenuItem();// go to specified directory
	protected JMenuItem mnuFilePublish = new JMenuItem();
	protected JMenu mnuFileVersion = new JMenu();
	protected JMenuItem mnuFileVersionHistory = new JMenuItem();
	protected JMenuItem mnuFileVersionEnable = new JMenuItem(); // check file out
	protected JMenuItem mnuFileVersionIn = new JMenuItem(); // check file back in
	protected JMenu mnuFileMetadata = new JMenu();
	protected JMenuItem mnuFileMetadataShow = new JMenuItem();
	protected JMenuItem mnuFileMetadataQuery = new JMenuItem();
	
	protected JScrollPane scrollPane = new JScrollPane();
	protected JScrollPane pneLog = new JScrollPane();
	protected JTextArea txtLog = new JTextArea();
	protected PnlBusyLoading pnlBusy = new PnlBusyLoading();
	protected PnlBookmarks pnlBookmarks;
	protected PnlSearch pnlSearch;
	protected PnlIrodsMetadata pnlIrodsMetadata;
	protected PnlEnvironment pnlEnvironment;
	public PnlSites pnlSites;
	
	private ArrayList<FileInfo> fileList = new ArrayList<FileInfo>();
	private ArrayList<FTPSettings> siteList = new ArrayList<FTPSettings>();
	private boolean listingChanging = false;
	private boolean sitesChanging = false;
	private String shareFolderNonce;
	private String shareFolderName;

	private Dimension minRowSize = new Dimension(400, 25);
	private Dimension preferredRowSize = new Dimension(400, 25);
	private Dimension maxRowSize = new Dimension(Integer.MAX_VALUE, 25);
	private Color backgroundColor = Color.decode("#F4F4F4");
	
	private Clipboard clipboard;

	public PnlBrowse(AppMain parent) {
		this(parent, null);
	}

	public PnlBrowse(AppMain frmParent, FTPSettings ftpServer) {
		this(frmParent, ftpServer, true);
	}

	public PnlBrowse(AppMain frmParent, FTPSettings ftpServer,
			String initialDir, boolean bRemoteMode) {
		super();

		currentDir = initialDir == null ? "" : initialDir;

		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		setFocusable(true);

		this.frmParent = frmParent;
		this.ftpServer = ftpServer;
		this.bRemoteMode = bRemoteMode;

		init();
		
//		this.ftpServer.parent = scrollPane;

	}

	public PnlBrowse(AppMain frmParent, FTPSettings ftpServer,
			boolean bRemoteMode) {
		this(frmParent, ftpServer, "", bRemoteMode);
	}

	protected void init() {
		separator = ((bRemoteMode) ? "/" : ftpServer.getSeparator());
		// currentDir = System.getProperty("user.home");

		try {
			jbInit();
			
			if (ftpServer == null) {
				btnSites.setSelected(true);
				showSitesPanel(true);
			} else {
				refresh();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		revalidate();

	}

	class ColumnListener extends MouseAdapter {

		public void mouseClicked(MouseEvent e) {
			TableColumnModel colModel = tblListing.getColumnModel();
			int columnModelIndex = colModel.getColumnIndexAtX(e.getX());
			int modelIndex = colModel.getColumn(columnModelIndex)
					.getModelIndex();
			DetailListModel m_data = ((DetailListModel) tblListing.getModel());
			int colIndex = ((DetailListModel) tblListing.getModel()).sortedColumnIndex;
			boolean sortAsc = ((DetailListModel) tblListing.getModel()).sortAscending;

			if (modelIndex < 0)
				return;
			if (m_data.sortedColumnIndex == modelIndex)
				m_data.sortAscending = !m_data.sortAscending;
			else
				m_data.sortedColumnIndex = modelIndex;

			for (int i = 0; i < m_data.getColumnCount(); i++) {
				TableColumn column = tblListing.getColumn(m_data
						.getColumnName(i));
				int index = column.getModelIndex();
				JLabel renderer = (JLabel) column.getHeaderRenderer();
				renderer.setIcon(m_data.getColumnIcon(index));
			}
			tblListing.getTableHeader().repaint();

			m_data.sortData();
			tblListing.tableChanged(new TableModelEvent(m_data));
			repaint();
		}
	}

	private void jbInit() {
		
		// create the button menu bar
		btnInit();
		IAppWidgetFactory.makeIAppScrollPane(scrollPane);
		// create the right click popup menu
		popInit();

		tblInit();

		// populate the site combo box
		JPanel pnlSite = new JPanel();
//		siteList = ConfigOperation.getInstance().getSites();
		// List sites = new ArrayList<FTPSettings>();
		
		btnSites = new JToggleButton(AppMain.icoTeraGridSmall);
		btnSites.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				showSitesPanel(btnSites.isSelected());
			}
			
		});
		if (ftpServer == null) {
			lblSite = new JLabel("Please select a resource");
		} else {
			lblSite = new JLabel(ftpServer.name);
		}
		
		JPanel pnlCurrentSite = new JPanel();
		pnlCurrentSite.setBackground(backgroundColor);
		pnlCurrentSite.setLayout(new BoxLayout(pnlCurrentSite,BoxLayout.X_AXIS));
		pnlCurrentSite.add(btnSites);
		pnlCurrentSite.add(Box.createHorizontalGlue());
		pnlCurrentSite.add(lblSite);
		pnlCurrentSite.add(Box.createHorizontalGlue());

		sizeRow(pnlCurrentSite);

		mdlSystemPath = new SystemPathComboBoxModel();

		cmbPath.setModel(mdlSystemPath);
		cmbPath
				.setToolTipText("Select a directory in the current path to navigate");
		cmbPath.setBackground(Color.WHITE);
		cmbPath.setRenderer(new ListCellRenderer() {

			private JFileChooser fileChooser = new JFileChooser();

			public Component getListCellRendererComponent(JList list,
					Object value, int index, boolean isSelected,
					boolean cellHasFocus) {
				if (value != null) {
					String dir = (String) value;
					JLabel lblSite = new JLabel(dir);
					// System.out.println("Path has " +
					// cmbPath.getComponentCount() +
					// " components. Resolving number " + index);
					
					lblSite.setBackground(Color.WHITE);
					lblSite.setBorder(BorderFactory
							.createEmptyBorder(2, (2 + 10 * (cmbPath.getModel()
									.getSize() - 1 - index)), 2, 2));
				
					if (ftpServer.name.equals(ConfigSettings.RESOURCE_NAME_LOCAL)) {
						// recreate path to get the folder icon
						String path = (ConfigOperation.isWindows()) ? ""
								: File.separator;

						for (int i = cmbPath.getModel().getSize() - 1; i >= ((SystemPathComboBoxModel) cmbPath
								.getModel()).getIndexOf(value); i--) {
							path += ((SystemPathComboBoxModel) cmbPath
									.getModel()).getElementAt(i)
									+ File.separator;
						}
						lblSite.setIcon(fileChooser.getIcon(new File(path)));
					} else {
						lblSite.setIcon(AppMain.icoFolder);
					}

					return lblSite;
				} else
					return new JLabel("/");
			}

		});

		cmbPath.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent ie) {
				if (ie.getStateChange() == ItemEvent.SELECTED) {
					System.out.println(cmbPath.getSelectedIndex());
					String dir = (String) cmbPath.getModel().getSelectedItem();
					currentDir = currentDir.substring(0, currentDir
							.indexOf(dir))
							+ dir;

					try {
						if (tBrowse != null)
							list(tBrowse.list(currentDir));
					} catch (ServerException e1) {
						e1.printStackTrace();
					} catch (ClientException e1) {
						e1.printStackTrace();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		});

		sizeRow(cmbPath);
		
		JPanel pnlPathCombo = new JPanel();
		pnlPathCombo.setBackground(backgroundColor);
		pnlPathCombo.add(cmbPath);
		pnlPathCombo.setMinimumSize(new Dimension(400,30));
		pnlPathCombo.setPreferredSize(new Dimension(400,30));
		pnlPathCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE,30));
		
		txtLog.setEditable(false);
		txtLog.setEnabled(false);
		// txtLog.setPreferredSize(new Dimension(220, 80));

		JPanel pnlNav = new JPanel();
		pnlNav.setLayout(new GridBagLayout());
		pnlNav.setBackground(backgroundColor);
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridy = 0;
		constraints.anchor = GridBagConstraints.EAST;
		constraints.insets = new Insets(1,0,1,0);
		constraints.weightx = 1;
		
		pnlNav.add(btnGoTo, constraints);
		// pnlNav.add(btnSearch);
		// pnlNav.add(btnUpDir);
		pnlNav.add(btnReload, constraints);
		pnlNav.add(btnHome, constraints);
		pnlNav.add(btnInfo, constraints);
		pnlNav.add(btnEdit, constraints);
		pnlNav.add(btnNewFolder, constraints);
		pnlNav.add(btnDelete, constraints);
		pnlNav.add(btnPrediction, constraints);
		pnlNav.add(btnBookmarks, constraints);
		pnlNav.add(btnSearch, constraints);
		pnlNav.add(btnEnvironment, constraints);
		pnlNav.add(btnDisconnect, constraints);
		
		// disable shortcut buttons until the gsissh problem is solved.
		// pnlNav.add(btnTGWork);
		// pnlNav.add(btnTGScratch);
		// pnlNav.add(btnTGArchive);
		// invisible preference button for pref shortcut.
		pnlNav.add(btnPrefs, constraints);
		sizeRow(pnlNav);

		// pnlBusy.setSize(new Dimension(175,400));
		sizeRow(pnlPath);
		pnlPath.setBackground(backgroundColor);
		
		// scrollPane.setPreferredSize(new Dimension(175,400));
		this.add(pnlCurrentSite);
		this.add(pnlPathCombo);
		this.add(pnlNav);
		this.add(scrollPane);
		this.add(pnlPath);
		this.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createEmptyBorder(10, 10, 10, 10),
				BorderFactory.createLineBorder(Color.white, 3)));
		setOpaque(false);
        
        
		this.setVisible(true);
	}

	private void tblInit() {

		tblListing = new CTable(this, bRemoteMode);
		DetailListModel listModel = new DetailListModel();
		tblListing.setModel(listModel);
		tblListing.setAutoCreateColumnsFromModel(false);
		tblListing.addMouseListener(new PnlBrowse_tblListView_mouseAdapter(this));
		
		DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer() {

			/* (non-Javadoc)
			 * @see javax.swing.table.DefaultTableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
			 */
			public void setValue(Object value) {
				if (value instanceof IconData) {
					Icon icn = null;
					IconData c = (IconData) value;

//					if (ftpServer.type == FTPType.XSHARE) {
//						FileInfo file = ((DetailListModel)tblListing.getModel()).getFile(c.toString());
//						Icon baseIcon = FileUtils.getLocalFileIcon(file);
//						if (baseIcon == null) {
//							baseIcon = c.getIcon();
//						}
////						if (file instanceof TGShareFileInfo) {
////							if (((TGShareFileInfo)file).isShared()) {
////								icn = IconFactory.getOverlayIcon(this, new ImageIcon(IconFactory.iconToImage(baseIcon)), 
////			            			AppMain.icoShareItem, SwingConstants.SOUTH_EAST);
////							} else {
////								
////								icn = baseIcon;
////							}
////						} else {
//							icn = baseIcon;
////						}
//					} else {
						FileInfo file = ((DetailListModel)tblListing.getModel()).getFile(c.toString());
						icn = FileUtils.getLocalFileIcon(file);
						if (icn == null) {
							icn = c.getIcon();
						}
//					}
					setIcon(icn);
					setText(c.toString());
				} else {
					if (value instanceof String) {
						if (((String) value).indexOf(" KB") > -1
								|| ((String) value).indexOf(" MB") > -1
								|| ((String) value).indexOf(" GB") > -1
								|| ((String) value).indexOf(" B") > -1) {
							setHorizontalAlignment(JLabel.RIGHT);
						}
					}
					super.setValue(value);
				}
				super.setBorder(BorderFactory.createMatteBorder(0,0,1,0, Color.decode("#D7D7D7")));
			}
		};

		DefaultTableCellRenderer nameCellRenderer = new DefaultTableCellRenderer() {
			public void setValue(Object value) {
				super.setValue(value);
				setBorder(BorderFactory.createMatteBorder(-1,0,1,0, Color.decode("#D7D7D7")));
			}
		};
		nameCellRenderer.setHorizontalAlignment(JLabel.RIGHT);

		for (int i = 0; i < listModel.getColumnCount(); i++) {

			TableColumn column = tblListing.getColumn(listModel
					.getColumnName(i));
			column.setModelIndex(i);
			column.setPreferredWidth(0 == i ? 160 : 1 == i ? 50 : 3 == i ? 100
					: 80);
			column.setCellRenderer(i == 0 ? cellRenderer : nameCellRenderer);
			column.setHeaderRenderer(createDefaultRenderer());
			// tblListing.addColumn(column);
		}

		JTableHeader header = tblListing.getTableHeader();
		header.setUpdateTableInRealTime(true);
		header.addMouseListener(new ColumnListener());
		header.setReorderingAllowed(true);

		scrollPane.getViewport().add(pnlBusy);
		scrollPane.getViewport().setBackground(Color.WHITE);
		//scrollPane.setDropTarget(tblListing.getDropTarget());
		scrollPane.setAutoscrolls(true);
		scrollPane.setBorder(BorderFactory.createEmptyBorder());

	}

	protected void setServer(FTPSettings site) {
		
		if (site != ftpServer || tBrowse == null) {
			
			disConn();

			bRemoteMode = (!site.name.toLowerCase().equals(
					"local"));

			separator = ((bRemoteMode) ? "/" : File.separator);

			ftpServer = site;
			ftpServer.parent = scrollPane;
			
			// clear the connection log panel to display the new
			// connection
			// attempts when the new listing is called.
			txtLog.selectAll();
			txtLog.setText("");

		} 
			
		btnSites.setSelected(false);
		lblSite.setText(site.name);
		scrollPane.getViewport().remove(tblListing);
		pnlBusy.setSize(scrollPane.getSize());
		scrollPane.getViewport().add(pnlBusy);
		
//		showSitesPanel(false);

		refresh();
		
	}
	
	protected TableCellRenderer createDefaultRenderer() {
		DefaultTableCellRenderer label = new DefaultTableCellRenderer() {
			public Component getTableCellRendererComponent(JTable table,
					Object value, boolean isSelected, boolean hasFocus,
					int row, int column) {
				if (table != null) {
					JTableHeader header = table.getTableHeader();
					if (header != null) {
						setForeground(header.getForeground());
						setBackground(header.getBackground());
						setFont(header.getFont());
					}
				}

				setText((value == null) ? "" : value.toString());
				setBorder(UIManager.getBorder("TableHeader.cellBorder"));
				return this;
			}
		};
		label.setHorizontalAlignment(JLabel.CENTER);
		return label;
	}

	private void sizeRow(Component c) {
		c.setMinimumSize(minRowSize);
		c.setPreferredSize(preferredRowSize);
		c.setMaximumSize(maxRowSize);
	}

	public void disConn() {
		if (null == tBrowse) {
			return;
		}

		if (tBrowse.close()) {
			tBrowse = null;

			this.list(new ArrayList());
			this.currentDir = "";
			
			cmbPath.setModel(new DefaultComboBoxModel());
			pnlPath.removeAll();
			
			pnlSearch = null;
			// Runs tha garbage collection
			System.gc();
		}

		if (tEnv != null)
			tEnv.close();
		

		enableNavigationButtons(false);
		enableShortcutButtons(false);
	}

	public void updateSitesPanel() {
		if (pnlSites != null && pnlSites.isVisible()) {
			pnlSites.update(ConfigOperation.getInstance().getSites());
		}
	}
	
	protected void showSitesPanel(boolean visible) {
		if (visible) {
			pnlSites = new PnlSites(this, ConfigOperation.getInstance().getSites());
		}

		pnlSites.setPreferredSize(scrollPane.getViewport().getVisibleRect()
				.getSize());
		scrollPane.getViewport().remove(tblListing);

		if (visible) {
			lblSite.setText("Please select a resource");
		} else {
			lblSite.setText((tBrowse == null)?"":ftpServer.name);
		}
		
		if (tBrowse != null) {
			tBrowse.bPaused = visible;
			enableNavigationButtons(!visible  && tBrowse.isConnected());
		} else {
			enableNavigationButtons(false);
		}
		
		scrollPane.getViewport().add(visible ? pnlSites : tblListing);
		
		if (tEnv != null && tEnv.isConnected()) {
			enableShortcutButtons(!visible);
		}
		
	}
	
	protected void showBookmarksPanel(boolean visible) {
		if (visible) {// && pnlBookmarks == null) {
			pnlBookmarks = new PnlBookmarks(this, ConfigOperation.getInstance()
					.getBookmarks(ftpServer.host));
		}

		pnlBookmarks.setPreferredSize(scrollPane.getViewport().getVisibleRect()
				.getSize());
		scrollPane.getViewport().remove(tblListing);

		tBrowse.bPaused = visible;
		scrollPane.getViewport().add(visible ? pnlBookmarks : tblListing);
		enableNavigationButtons(!visible);
		btnBookmarks.setEnabled(true);
		
		pnlPath.setVisible(!visible);
		
		if (tEnv != null && tEnv.isConnected())
			enableShortcutButtons(!visible);

	}
	
	protected void showIrodsMetadataPanel(boolean visible) {
		if (visible) {
			String irodsPath = ((IRODSFileInfo)tblListing.getSelectedFiles().get(0)).getAbsolutePath();
			try
			{
				pnlIrodsMetadata = new PnlIrodsMetadata(this, 
						((Irods)tBrowse.ftpSrvConn.getFtpClient()).getAllPathMetadata(irodsPath), 
						irodsPath);
				pnlIrodsMetadata.setPreferredSize(scrollPane.getViewport().getVisibleRect()
						.getSize());
				scrollPane.getViewport().remove(tblListing);
				scrollPane.getViewport().add(pnlIrodsMetadata);
				pnlPath.setVisible(false);
				enableNavigationButtons(false);
				tBrowse.bPaused = true;
			}
			catch (IOException e)
			{
				frmParent.Error(this, "Failed to retrieve metadata for " + irodsPath);
				scrollPane.getViewport().remove(pnlIrodsMetadata);
				scrollPane.getViewport().add(tblListing);
				enableNavigationButtons(true);
				tBrowse.bPaused = false;
			}
			
			
		} else {
			scrollPane.getViewport().remove(pnlIrodsMetadata);
			scrollPane.getViewport().add(tblListing);
			pnlPath.setVisible(true);
			pnlIrodsMetadata = null;
			tBrowse.bPaused = false;
			enableNavigationButtons(true);
		}
		
//		tBrowse.bPaused = visible;
//		
//		//scrollPane.getViewport().add(visible ? pnlIrodsMetadata : tblListing);
//		
//		
//		pnlPath.setVisible(!visible);
		
		if (tEnv != null && tEnv.isConnected())
			enableShortcutButtons(!visible);
	}

	protected void showIrodsMetadataQueryPanel(boolean visible) {
//		if (visible) {
//			// reuse the current instance so we have the previous search results
//			// handy.
//			if (pnlIrodsMetadataQuery == null) {
//				pnlIrodsMetadataQuery = new PnlIrodsMetadataQuery(this);
//			}
//			// window size may have changed since last viewed.
//			pnlIrodsMetadataQuery.setPreferredSize(scrollPane.getViewport()
//					.getVisibleRect().getSize());
//		} else {
//			// search shares a thread with pnlbrowse, thus if hiding the metadata
//			// pane, make sure the search stops so we can use the thread for listings.
//			pnlIrodsMetadataQuery.stopSearch();
//		}
//
//		// pnlSearch.setVisible(visible);
//
//		scrollPane.getViewport().remove(tblListing);
//
//		// we suspend the file listing refresh timer while the search is
//		// open so we can use the current thread and save time having
//		// to open another connection.
//		tBrowse.holdRefresh(visible);
//
//		// don't open up new instances, just display the proper one.
//		scrollPane.getViewport().add(visible ? pnlIrodsMetadata : tblListing);
//
//		// only the search button should be visible when the search
//		// panel is visible
//		enableNavigationButtons(!visible);
//		
//		// disable the shortcut paths since they don't mean anything
//		// during a search
//		pnlPath.setVisible(!visible);
//
//		// here to disable the shortcut buttons if the env variables
//		// are available.
//		if (tEnv != null && tEnv.isConnected())
//			enableShortcutButtons(!visible);
	}
	
	protected void showSearchPanel(boolean visible) {
		if (visible) {
			// reuse the current instance so we have the previous search results
			// handy.
			if (pnlSearch == null) {
				pnlSearch = new PnlSearch(this);
			}
			// window size may have changed since last viewed.
			pnlSearch.setPreferredSize(scrollPane.getViewport()
					.getVisibleRect().getSize());
		} else {
			// search shares a thread with pnlbrowse, thus if hiding the search
			// pane,
			// make sure the search stops so we can use the thread for listings.
			pnlSearch.stopSearch();
		}
	
		// pnlSearch.setVisible(visible);
	
		scrollPane.getViewport().remove(tblListing);
	
		// we suspend the file listing refresh timer while the search is
		// open so we can use the current thread and save time having
		// to open another connection.
		tBrowse.holdRefresh(visible);
	
		// don't open up new instances, just display the proper one.
		scrollPane.getViewport().add(visible ? pnlSearch : tblListing);
	
		// only the search button should be visible when the search
		// panel is visible
		enableNavigationButtons(!visible);
		btnSearch.setEnabled(true);
	
		// disable the shortcut paths since they don't mean anything
		// during a search
		pnlPath.setVisible(!visible);
	
		// here to disable the shortcut buttons if the env variables
		// are available.
		if (tEnv != null && tEnv.isConnected())
			enableShortcutButtons(!visible);
	
	}

	public void enableEnvironmentButton() {
		btnEnvironment.setEnabled(true);
	}
	
	protected void showEnvironmentPanel(boolean visible) {
		
		if (visible) {
			
			scrollPane.getViewport().remove(tblListing);
			pnlBusy.setSize(scrollPane.getSize());
			scrollPane.getViewport().add(pnlBusy);
			
			// reuse the current instance so we have the previous search results
			// handy.
			final PnlBrowse pnlBrowse = this;
			
			SwingWorker worker = new SwingWorker() {
				@Override
				public Object construct() {
					pnlEnvironment = new PnlEnvironment(pnlBrowse);
					
					// window size may have changed since last viewed.
					pnlEnvironment.setPreferredSize(scrollPane.getViewport()
							.getVisibleRect().getSize());
					
					scrollPane.getViewport().remove(pnlBusy);
					scrollPane.getViewport().add(pnlEnvironment);
					pnlBusy.setBusy(false);
					return pnlEnvironment;
				}
			};
		
			worker.start();
			
		} else {
			scrollPane.getViewport().remove(pnlBusy);
			scrollPane.getViewport().add(tblListing);
			
		}
		
		// we suspend the file listing refresh timer while the search is
		// open so we can use the current thread and save time having
		// to open another connection.
		tBrowse.holdRefresh(visible);

		// don't open up new instances, just display the proper one.
		//scrollPane.getViewport().add(visible ? pnlEnvironment : tblListing);
		pnlBusy.setBusy(visible);
		
		// only the search button should be visible when the search
		// panel is visible
		enableNavigationButtons(!visible);
		btnEnvironment.setEnabled(true);

		// disable the shortcut paths since they don't mean anything
		// during a search
		pnlPath.setVisible(!visible);

	}

	public void refreshPathLinkPane() {
		pnlPath.removeAll();
		String pathSeparator = null;

		if (ConfigOperation.os().equals("windows") && !bRemoteMode) {
			pathSeparator = "\\";
		} else {
			pathSeparator = "/";
		}

		int excessPathCharacters = currentDir.length() * 4 - this.getWidth();
		
		// no need to resolve the shared folder name from the nonce value
		// because the cmbPath model is updated before this is called.
		for (int i = cmbPath.getItemCount(); i > 0; i--) {
			String folder = (String) cmbPath.getModel().getElementAt(i - 1);
			folder = folder == null ? "" : folder;
			if ((excessPathCharacters - folder.length() * 4) <= 0) {
				pnlPath.add(new Hyperlink(new LinkAction(folder
						.equals(separator) ? folder : (folder + separator),
						folder)));
			}
			excessPathCharacters -= folder.length() * 4;
		}

		// resize the panel to hold the entire path length
		pnlPath.setSize(new Dimension(Math.min(currentDir.length() * 5, this
				.getWidth()), pnlPath.getHeight()));
		pnlPath.revalidate();
	}

	public void refresh() {

		if (null != tBrowse && tBrowse.isConnected()) {
			if (tBrowse.cmdEmpty()) {
				tBrowse.cmdAdd("List");
			}
		} else {
			disConn();
			tBrowse = new FTPThread(ftpServer, bRemoteMode, this);
			LogManager.info("Connecting to " + ftpServer.name);
			tBrowse.start();
			if (!currentDir.equals("")) {
				tBrowse.cmdAdd("Cwd", currentDir, null);
			}

			if (ftpServer.isTeraGridResource()) {
				 tEnv = new EnvironmentThread(this);
				 tEnv.start();
			}
		}
	}

	private void btnInit() {
		int systemKeyModifier = Toolkit.getDefaultToolkit()
				.getMenuShortcutKeyMask();

		NavActionListener navActionListener = new NavActionListener(this);
		btnPrefs = createButton(new ShortCutAction("", null, "Prefs",
				KeyEvent.VK_COMMA), null);
		btnPrefs.setEnabled(true);
		btnPrefs.setVisible(false);
		btnUpDir = createButton(new ShortCutAction("", AppMain.icoUpDir, "Up",
				KeyEvent.VK_F), AppMain.icoUpDirPressed);
		btnSearch = createToggleButton(new ShortCutAction("", AppMain.icoSearch,
				"Search", KeyEvent.VK_S), AppMain.icoSearchPressed);
		// btnSearch.setEnabled(false);
		btnHome = createButton(new ShortCutAction("", AppMain.icoHome, "Home",
				KeyEvent.VK_H), AppMain.icoHomePressed);
		btnHistory = createButton(new ShortCutAction("", AppMain.icoBook,
				"History", KeyEvent.VK_L), AppMain.icoBookPressed);
		btnGoTo = createButton(new ShortCutAction("", AppMain.icoGoTo,
				"Go To Folder", KeyEvent.VK_G), AppMain.icoGoToPressed);
		btnReload = createButton(
				new ShortCutAction(
						"",
						AppMain.icoRefresh,
						SGGCResourceBundle
								.getResourceString(ResourceName.KEY_DISPLAY_PNLBROWSE_REFRESH),
						KeyEvent.VK_R), AppMain.icoRefreshPressed);
		btnInfo = createButton(new ShortCutAction("", AppMain.icoInfo,
				"Get Info", KeyEvent.VK_I), AppMain.icoInfoPressed);
		btnEdit = createButton(new ShortCutAction("", AppMain.icoRename,
				"Rename", KeyEvent.VK_M), AppMain.icoRenamePressed);
		btnNewFolder = createButton(new ShortCutAction("", AppMain.icoNewFolder,
				"New Folder", KeyEvent.VK_ADD), AppMain.icoNewFolderPressed);
		btnDelete = createButton(
				new ShortCutAction(
						"",
						AppMain.icoDelete,
						SGGCResourceBundle
								.getResourceString(ResourceName.KEY_DISPLAY_PNLBROWSE_DELETE),
						KeyEvent.VK_D), AppMain.icoDeletePressed);
		btnDisconnect = createButton(
				new ShortCutAction(
						"",
						AppMain.icoDisconn,
						SGGCResourceBundle
								.getResourceString(ResourceName.KEY_DISPLAY_PNLBROWSE_DISCONNECT),
						KeyEvent.VK_X), AppMain.icoDisconnPressed);
		btnBookmarks = createToggleButton(new ShortCutAction("",
				AppMain.icoBook, "Bookmarks", KeyEvent.VK_B), AppMain.icoBookPressed);
		btnEnvironment = createToggleButton(new ShortCutAction("",
				AppMain.icoEnvironment, "Environment Variables", KeyEvent.VK_E), AppMain.icoEnvironmentPressed);
		
		// TG Navigation Buttons
//		btnTGWork = new JButton(
//				new ShortCutAction(
//						"$WORK",
//						null,
//						"<html><p>Go to the directory represented by<br>the <em>$TG_WORK</em> environmental variable<br>on this system.</p></html>",
//						KeyEvent.VK_W));
//		resizeButton(btnTGWork, new Dimension(50, 30));
//		btnTGScratch = new JButton(
//				new ShortCutAction(
//						"$SCRATCH",
//						null,
//						"<html><p>Go to the directory represented by<br>the <em>$TG_CLUSTER_SCRATCH</em> environmental variable<br>on this system.</p></html>",
//						KeyEvent.VK_SLASH));
//		resizeButton(btnTGScratch, new Dimension(65, 30));
//		btnTGArchive = new JButton(
//				new ShortCutAction(
//						"$ARCHIVE",
//						null,
//						"<html><p>Go to the directory represented by<br>the <em>$TG_ARCHIVE</em> environmental variable<br>on this system.</p></html>",
//						KeyEvent.VK_A));
//		resizeButton(btnTGArchive, new Dimension(65, 30));
		
		btnPrediction = createButton(new ShortCutAction("", AppMain.icoBandwidth, "Predict Transfer Time",
				KeyEvent.VK_B), AppMain.icoBandwidthPressed);
	}

	private JButton createButton(ShortCutAction action, Icon pressedIcon) {

		JButton button = new JButton(action);
		Font font = button.getFont();
		button.setFont(new Font(font.getFontName(), font.getStyle(), 10));
		button.setFocusPainted(false);
		button.setBorderPainted(true);
		button.setContentAreaFilled(false);
		button.setPressedIcon(pressedIcon);
		button.setEnabled(false);
		return button;
	}
	
	private JToggleButton createToggleButton(ShortCutAction action, Icon pressedIcon) {

		JToggleButton button = new JToggleButton(action);
		Font font = button.getFont();
		button.setFont(new Font(font.getFontName(), font.getStyle(), 10));
		button.setFocusPainted(false);
		button.setBorderPainted(true);
		button.setContentAreaFilled(false);
		button.setPressedIcon(pressedIcon);
		button.setEnabled(false);
		return button;
	}

	private void popInit() {
		// mnuFileUp.setIcon(AppMain.icoUpload);
		// mnuFileUp.setText(SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_UPLOAD));
		// mnuFileUp.addActionListener(evtMenu);

		mnuFileDown.setIcon(AppMain.icoDownload);
		mnuFileDown.setText(SGGCResourceBundle
				.getResourceString(ResourceName.KEY_DISPLAY_DOWNLOAD));
		mnuFileDown.addActionListener(evtMenu);
		mnuFileInfo.setIcon(AppMain.icoInfo);
		mnuFileInfo.setText(SGGCResourceBundle
				.getResourceString(ResourceName.KEY_DISPLAY_INFO));
		mnuFileInfo.addActionListener(evtMenu);
		// mnuFileInfo.setIcon(AppMain.icoACL);
		mnuFileACL.setText("Sharing...");
		mnuFileACL.addActionListener(evtMenu);
		mnuFileACL.setVisible(false);
		mnuFileOpen.setIcon(AppMain.icoFolderExpanded);
		mnuFileOpen.setText(SGGCResourceBundle
				.getResourceString(ResourceName.KEY_DISPLAY_OPEN));
		mnuFileOpen.addActionListener(evtMenu);
		mnuFileAddDir.setIcon(AppMain.icoNewFolder);
		mnuFileAddDir.setText(SGGCResourceBundle
				.getResourceString(ResourceName.KEY_DISPLAY_NEWDIR));
		mnuFileAddDir.addActionListener(evtMenu);
		mnuFileDel.setIcon(AppMain.icoDelete);
		mnuFileDel.setText(SGGCResourceBundle
				.getResourceString(ResourceName.KEY_DISPLAY_DELSEL));
		mnuFileDel.addActionListener(evtMenu);
		mnuFileRen.setIcon(AppMain.icoRename);
		mnuFileRen.setText(SGGCResourceBundle
				.getResourceString(ResourceName.KEY_DISPLAY_RENAME));
		mnuFileRen.addActionListener(evtMenu);
//		mnuFileCmd.setIcon(AppMain.icoRun);
//		mnuFileCmd
//				.setText(SGGCResourceBundle
//						.getResourceString(ResourceName.KEY_DISPLAY_PNLBROWSE_INPUTRAW));
//		mnuFileCmd.addActionListener(evtMenu);
		mnuFileDirUp.setIcon(AppMain.icoUpDir);
		mnuFileDirUp.setText(SGGCResourceBundle
				.getResourceString(ResourceName.KEY_DISPLAY_PNLBROWSE_GOUP));
		mnuFileDirUp.addActionListener(evtMenu);
		mnuFileRefresh.setIcon(AppMain.icoRefresh);
		mnuFileRefresh.setText(SGGCResourceBundle
				.getResourceString(ResourceName.KEY_DISPLAY_PNLBROWSE_REFRESH));
		mnuFileRefresh.addActionListener(evtMenu);
		mnuFileHome.setIcon(AppMain.icoHome);
		mnuFileHome.setText(SGGCResourceBundle
				.getResourceString(ResourceName.KEY_DISPLAY_PNLBROWSE_GOHOME));
		mnuFileHome.addActionListener(evtMenu);
		mnuFileBookmark.setIcon(AppMain.icoBook);
		mnuFileBookmark.setText("Bookmark Selected");
		mnuFileBookmark.addActionListener(evtMenu);
		mnuFileDisconn.setIcon(AppMain.icoDisconn);
		mnuFileDisconn
				.setText(SGGCResourceBundle
						.getResourceString(ResourceName.KEY_DISPLAY_PNLBROWSE_DISCONNECT));
		mnuFileDisconn.addActionListener(evtMenu);
		mnuFileGo.setIcon(AppMain.icoGoTo);
		mnuFileGo.setText(SGGCResourceBundle
				.getResourceString(ResourceName.KEY_DISPLAY_PNLBROWSE_MNUGO));
		mnuFileGo.addActionListener(evtMenu);
		
		mnuFilePublish.setText("Copy Public URL");
		mnuFilePublish.addActionListener(evtMenu);
		mnuFileVersion.setText("Version");
		mnuFileVersionHistory.setText("View History");
		mnuFileVersionHistory.addActionListener(evtMenu);
		mnuFileVersionEnable.setText("Enable Versioning");
		mnuFileVersionEnable.addActionListener(evtMenu);
		mnuFileVersionIn.setText("Check In");
		mnuFileVersionIn.addActionListener(evtMenu);
		mnuFileVersion.add(mnuFileVersionHistory);
		mnuFileVersion.add(mnuFileVersionEnable);
//		mnuFileVersion.add(mnuFileVersionIn);
		
		mnuFileMetadataShow.setText("Show File Metadata");
		mnuFileMetadataShow.addActionListener(evtMenu);
		mnuFileMetadataShow.setVisible(false);
		//mnuFileMetadata.add(mnuFileMetadataShow);
		mnuFileMetadataQuery.setText("Query Metadata");
		mnuFileMetadataQuery.addActionListener(evtMenu);
		//mnuFileMetadata.add(mnuFileMetadataQuery);
		mnuFileMetadataQuery.setVisible(false);
		// mnuFile.add(mnuFileUp);
		
		mnuFile.add(mnuFileDown);
		mnuFile.addSeparator();
		mnuFile.add(mnuFileInfo);
		mnuFile.add(mnuFileACL);
		mnuFile.add(mnuFileAddDir);
		mnuFile.add(mnuFileRen);
		mnuFile.add(mnuFileDel);
		mnuFile.addSeparator();
		mnuFile.add(mnuFileRefresh);
		mnuFile.add(mnuFileBookmark);
		mnuFile.add(mnuFileHome);
		mnuFile.add(mnuFileDisconn);
		mnuFile.addSeparator();
		mnuFile.add(mnuFilePublish);
		mnuFile.add(mnuFileMetadataShow);
//		mnuFile.add(mnuFileMetadataQuery);
//		mnuFile.add(mnuFileVersion); // diabled version support until figured out
		
	}

	class ShortCutAction extends AbstractAction {

		public ShortCutAction(String text, ImageIcon icon, String desc,
				Integer mnemonic) {
			super(text, icon);
			putValue(SHORT_DESCRIPTION, desc);
			putValue(MNEMONIC_KEY, mnemonic);
		}

		public void actionPerformed(ActionEvent e) {
			mnuFile_actionPerformed(e);
		}
	}

	public void list(List files) {
		suspendLayout();

		fileList = (ArrayList<FileInfo>) files;

		//currentDir = tBrowse.ftpSrvConn.getDir();
		
		// if the listing has not changed and a selection is already made
		if (tblListing.getSelectedRowCount() > 0) {
			// preserve the existing selection
			int[] selectedRows = tblListing.getSelectedRows();
			Set<String> selectedFileNames = new HashSet<String>();
			for(int i=0;i<selectedRows.length;i++) {
				selectedFileNames.add(((DetailListModel) tblListing.getModel()).getFile(selectedRows[i]).getName());
			}
			
			// update the model;
			((DetailListModel) tblListing.getModel()).setFileList(fileList);
			
			// restore the selections
			for(int i=0;i<fileList.size();i++) {
				if (selectedFileNames.contains(fileList.get(i).getName())) {
					tblListing.getSelectionModel().addSelectionInterval(i,i);
				}
			}
		} else {
			// update the model;
			((DetailListModel) tblListing.getModel()).setFileList(fileList);
		}

		cmbPath.setModel(new SystemPathComboBoxModel(currentDir, separator
				.equals("/")));

		refreshPathLinkPane();

		scrollPane.getViewport().removeAll();
		scrollPane.getViewport().add(tblListing);

		tblListing.repaint();
		tblListing.revalidate();

		resumeLayout();
	}

//	public ArrayList<FTPSettings> getSites() {
//		// Query GPIR for robust resource information including IP and DN
//		try {
//			String gpirLocation = AppMain.getApplet().getParameter("gpir_url");
//
//			if (gpirLocation == null || gpirLocation.equals("")) {
//				gpirLocation = ConfigSettings.SERVICE_GPIR_SERVICE;
//			}
//			List<AbstractResourceBean> resourceBeans = new ResourceDiscovery(gpirLocation)
//					.getResources();
//
//			return SiteUtil.convert(resourceBeans);
//
//		} catch (Exception e1) {
//			throw new ResourceException(
//					"TG resource listing temporarily unavailable.", e1);
//		}
//	}

	public FTPSettings getFtpServer() {
		return this.ftpServer;
	}

	public ArrayList<FileInfo> getFileList() {
		return this.fileList;
	}

	public void setFileList(ArrayList<FileInfo> list) {
		this.fileList = list;
	}

	public String getCurrentDir() {
		return currentDir;
	}

	public void setCurrentDir(String currentDir) {
		this.currentDir = currentDir;
	}

	public void resumeLayout() {
		if (btnSearch.isSelected()) {
			pnlSearch.resumeLayout();
			return;
		}

		if (tBrowse != null && tBrowse.bConnected) {
			enableNavigationButtons(true);
		} else {
			enableNavigationButtons(false);
		}

		tblListing.setCursor(Cursor.getDefaultCursor());
		listingChanging = true;
		pnlBusy.setBusy(false);
	}

	public void setShortcuts(List<EnvironmentVariable> environment) {
		if (environment == null || environment.isEmpty()) {
			enableShortcutButtons(false);
		} else {
			this.tBrowse.setEnvProperties(environment);
			enableShortcutButtons(true);
		}
	}

	public void goHome() {
		mnuFile_actionPerformed(new ActionEvent(mnuFileHome,
				ActionEvent.ACTION_PERFORMED, "Click"));
	}

	public void suspendLayout() {
		tblListing.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		listingChanging = true;
		pnlBusy.setBusy(true);
	}

	public void enableNavigationButtons(boolean enabled) {
		cmbPath.setEnabled(enabled);
		btnUpDir.setEnabled(enabled);
		btnHome.setEnabled(enabled);
		btnHistory.setEnabled(enabled);
		btnGoTo.setEnabled(enabled);
		btnReload.setEnabled(enabled);
		btnInfo.setEnabled(enabled);
		btnEdit.setEnabled(enabled);
		btnNewFolder.setEnabled(enabled);
		btnDelete.setEnabled(enabled);
		btnDisconnect.setEnabled(enabled);
		btnPrediction.setEnabled(enabled);
		btnSearch.setEnabled(enabled);
		btnBookmarks.setEnabled(enabled);
		if (tEnv != null && !tEnv.isAlive())
			btnEnvironment.setEnabled(enabled);
	}

	public void enableShortcutButtons(final boolean enabled) {
		final SwingWorker worker = new SwingWorker() {
			public Object construct() {
				btnTGHome.setEnabled(enabled);
				btnTGWork.setEnabled(enabled);
				btnTGScratch.setEnabled(enabled);
				btnTGArchive.setEnabled(enabled);
				return null;
			}
		};
		worker.start();

	}

	public void mnuFile_actionPerformed(ActionEvent e) {
//        System.out.println("Button " + e.getSource().toString() + " was pressed.");
//        if (e.getSource()==mnuFileUp || btnReload==e.getSource()) {
//            currentDir = currentDir.substring(0,currentDir.lastIndexOf(separator));
//            refresh();
//            return;
//        }
        if (btnPrefs==e.getSource()) {
            frmParent.showPreferences();
            return;
        }
        
        if (btnBookmarks==e.getSource()) {
            showBookmarksPanel(btnBookmarks.isSelected());
            return;
        }
        
        if (btnEnvironment==e.getSource()) {
            showEnvironmentPanel(btnEnvironment.isSelected());
            return;
        }
        
        if (btnGoTo==e.getSource() || e.getActionCommand().compareTo("goto")==0 ) {
            DlgGoTo dlgGoTo = new DlgGoTo(this);
            return;
        }
        
        if (btnSearch==e.getSource() || e.getActionCommand().compareTo("search")==0) {
//            String searchString = AppMain.Prompt(this,"Search for (regex support): ");
//            if (searchString != null && !searchString.equals("")) {
//                if(tBrowse.cmdEmpty()) {
//                    tBrowse.cmdAdd("Find",searchString,null);
//                }
//            }
            showSearchPanel(btnSearch.isSelected());
            return;
        }
        
        if (mnuFileMetadataShow==e.getSource() || e.getActionCommand().compareTo("showmetadata")==0) {
          showIrodsMetadataPanel(true);
          return;
        }
        
        if (mnuFileMetadataQuery==e.getSource() || e.getActionCommand().compareTo("querymetadata")==0) {
            showIrodsMetadataQueryPanel(true);
            return;
        }
        
        //Refresh
        if(mnuFileRefresh==e.getSource() || btnReload==e.getSource() || e.getActionCommand().compareTo("refresh")==0){
            enableNavigationButtons(false);
            refresh();
            return;
        }
        
        //Disconnect
        else if(mnuFileDisconn==e.getSource() || btnDisconnect==e.getSource() || e.getActionCommand().compareTo("disconnect")==0){
            disConn();
            return;
        }
        
        //The following operations need to obtain a "Thread"!
        if(null==tBrowse || !tBrowse.isConnected()) {
            return;
        }

        //New Directory
        if(mnuFileAddDir == e.getSource() || btnNewFolder==e.getSource() || e.getActionCommand().compareTo("new")==0){
            String s=(String)AppMain.Prompt(this,
                    SGGCResourceBundle.getResourceString(ResourceName.KEY_MSG_PNLBROWSE_NEWDIR),
                    SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_PNLBROWSE_NEWDIRDLG_TITLE),
                    ((tBrowse.ftpServer.type == FTPType.S3)?tBrowse.ftpServer.userName+".bucket1":null));
            if(null!=s && !s.equals("")) {
                tBrowse.cmdAdd("FileAdd",s,new Integer(FileInfo.DIRECTORY_TYPE));
            }
            return;
        }
        
        //New File
        else if(mnuFileAdd == e.getSource()){
//            String s=(String)AppMain.Prompt("Please input new file name: ","Add File",null);
//            if(null!=s && !s.equals("")) {
//              tBrowse.cmdAdd("FileAdd",s,new Integer(FileInfo.FILE_TYPE));
//            }
        }
        
        //Go up
        else if(mnuFileDirUp == e.getSource() || btnUpDir == e.getSource() || e.getActionCommand().compareTo("up")==0){
            
            enableNavigationButtons(false);
            
            if(tBrowse.cmdEmpty()) {
                tBrowse.cmdAdd("Cwd","..",null);
            }

            int index = this.currentDir.lastIndexOf(separator);
            
            if(index != -1){
                this.currentDir = this.currentDir.substring(0, index);
            }
            
            if (shareFolderName != null) {
            	shareFolderName = null;
            	shareFolderNonce = null;
            }
            
            return;
            
        }
        
        // TG WORK
        else if(btnTGWork == e.getSource() || e.getActionCommand().compareTo("work")==0){
            
            enableNavigationButtons(false);
            
            if(tBrowse.cmdEmpty()) {
                tBrowse.cmdAdd("Cwd","$TG_WORK",null);
            }      
            
            return;
            
        }
        
        // TG SLASH
        else if(btnTGScratch == e.getSource() || e.getActionCommand().compareTo("scratch")==0){
            
            enableNavigationButtons(false);
            
            if(tBrowse.cmdEmpty()) {
                tBrowse.cmdAdd("Cwd","$TG_SCRATCH",null);
            } 
            
            return;
            
        }
        
        // TG ARCHIVE
        else if(btnTGArchive == e.getSource() || e.getActionCommand().compareTo("archive")==0){
            
            enableNavigationButtons(false);
            
            if(tBrowse.cmdEmpty()) {
                tBrowse.cmdAdd("Cwd","$TG_ARCHIVE",null);
            }  
            
            return;
            
        }
        //Go Home
        else if(mnuFileHome == e.getSource() || btnHome==e.getSource() || e.getActionCommand().compareTo("home")==0){
            if(tBrowse.cmdEmpty()) {
                enableNavigationButtons(false);
                tBrowse.cmdAdd("CwdHome");
            }
            return;
        }
        
        Hashtable<String,ArrayList<FileInfo>> selectedTable = tblListing.getSelectedFileTable();
        int nSel=selectedTable.size();//Return the index of the first selected row, -1 if no row is selected.
        
        if(nSel <= 0) {
            AppMain.Message(AppMain.getFrame(), "Please selected a file or folder.");
        	return;
        }
        
        FileInfo file = null;
        if (selectedTable.size() > 0) {
            ArrayList<FileInfo> fileInfo = selectedTable.get(selectedTable.keySet().iterator().next());
            if (fileInfo != null && fileInfo.size() > 0) {
                file = fileInfo.get(0);
            } else {
//                return;
            }
        }
        
        // Open
        if (e.getSource()==mnuFileOpen) {
            enableNavigationButtons(false);
            if(ListModel.getType(file)==FileInfo.DIRECTORY_TYPE || ListModel.getType(file)==FileInfo.SOFTLINK_TYPE){
                
                if(tBrowse.cmdEmpty()) {
                	String dir = getCurrentDir() + ((ftpServer.name.equals(ConfigSettings.RESOURCE_NAME_LOCAL))?File.separator:"/");
                	
//                	if (ftpServer.type == FTPType.XSHARE && currentDir.startsWith("/shared") && !file.getName().equals("..")) {
//                		String[] pathElements = StringUtils.split(dir, '/');
//                		if (pathElements.length == 1) {
//	                		shareFolderName = file.getName();
//	                		shareFolderNonce = ((TGShareFileInfo)file).getNonce();
//	                		dir += shareFolderNonce;
//                		} else {
//                			dir += ListModel.getFileName(file);
//                		}
//                	} else {
                		shareFolderName = null;
                		shareFolderNonce = null;
                		dir +=  ListModel.getFileName(file);
//                	}
                    
                	System.out.println("Opening dir " + dir + " from dir " + getCurrentDir());
                    tBrowse.cmdAdd("Cwd", dir, null);
                }
            }
            else if(!bRemoteMode){
                try{
                    Process p = Runtime.getRuntime().exec(currentDir+File.separator+ListModel.getFileName(file));
                }catch(IOException ex){
                    AppMain.Error(this,ex.getMessage(),SGGCResourceBundle.getResourceString(ResourceName.KEY_EXCEPTION_PNLBROWSE_EXEC));
                }
            }
        }
        else if (mnuFileBookmark==e.getSource()) {
            String path = currentDir;
            String name = AppMain.Prompt(this, "Please enter name of new bookmark");
            
            //if (file.isDirectory()) {
                path += separator + file.getName();
            //}  
            
            if (name == null ) {
                return;
            } else if (name.equals("")){
                name = path.substring(path.lastIndexOf(separator));
            } 
            
            ConfigOperation.getInstance().addBookmark(ftpServer.host,new Bookmark(name,path));
            
            
        }
        //Info
        else if(mnuFileInfo==e.getSource() || btnInfo==e.getSource() || e.getActionCommand().compareTo("info")==0) {
            List<FileInfo> files = tblListing.getSelectedFiles();
            
            if (files.isEmpty()) {
                AppMain.Message(AppMain.getFrame(), "Please selected a file or folder.");
                return;
            } else {
            	//int counter = 0;
            	DlgInfo previousDialog = null;
            	for(FileInfo selectedFile : files) {
            		DlgInfo f = new DlgInfo(AppMain.getFrame(),tBrowse,getCurrentDir(), selectedFile, previousDialog);
            		previousDialog = f;
            	}
            }
        }
        
        else if (btnPrediction==e.getSource() || e.getActionCommand().compareTo("predict")==0 ) {
        	if (file == null) {
                AppMain.Message(AppMain.getFrame(), "Please selected a file or folder.");
                return;
            }
        	FTPSettings toServer = (frmParent.getBrowsingPanel(0) == this)?
        			frmParent.getBrowsingPanel(1).ftpServer:
        				frmParent.getBrowsingPanel(0).ftpServer;
        	if (toServer == null) {
        		AppMain.Message(AppMain.getFrame(), "<html>Please selected a destination resource<br>in the other panel.</html>");
                return;
        	}
        	DlgBandwidth dlgBandwidth = new DlgBandwidth(AppMain.getFrame(),ftpServer,toServer,file);
        }
        
        else if(mnuFileACL==e.getSource()) {
            DlgS3Sharing dACL = new DlgS3Sharing(AppMain.getFrame(),getCurrentDir(),file);
        }
//        //Upload
//        if(mnuFileUp==e.getSource()){
//          PnlBrowse pnlRemote = frmParent;
//          if(null != pnlRemote){
//              
//              java.util.List destFileList = pnlRemote.getFileList();
//              
//              for (String dir: (Set<String>)selectedTable.keySet()) {
//                  TransferProxy.transfer((List)selectedTable.get(dir),this.ftpServer,pnlRemote.ftpServer,this.getCurrentDir(),frmParent.getLocalBrowser().getCurrentDir(),destFileList);
//              }
//
//             
//          }
//      }
        //Download
        else if(mnuFileDown==e.getSource()){
            for (String dir: (Set<String>)selectedTable.keySet()) {
              TransferProxy.transfer(scrollPane,(List)selectedTable.get(dir), this.ftpServer, FTPSettings.Local.clone(scrollPane), dir, frmParent.getLocalBrowser().getCurrentDir(), frmParent.getLocalBrowser().getFileList());
            }
          
        }
//        //Open
//        else if(mnuFileOpen==e.getSource()){
//          if(ListModel.getType(file)==FileInfo.DIRECTORY_TYPE || ListModel.getType(file)==FileInfo.SOFTLINK_TYPE){
//              
//              if(tBrowse.cmdEmpty()) {
//                  // if in tree view and the node was expanded, the node is already the current directory
//                  // so we just use that
//                  tBrowse.cmdAdd("Cwd", currentDir, null);
//              }
//          }
//          else if(!bRemoteMode){
//              try{
//                  Process p = Runtime.getRuntime().exec(getCurrentDir()+separator+ListModel.getFileName(file));
//              }catch(IOException ex){
//                  AppMain.Error(ex.getMessage(),SGGCResourceBundle.getResourceString(ResourceName.KEY_EXCEPTION_PNLBROWSE_EXEC));
//              }
//          }
//
//          
//      }
      //Rename
      else if(mnuFileRen==e.getSource() || btnEdit==e.getSource() || e.getActionCommand().compareTo("rename")==0){
          if (file == null) {
              AppMain.Message(AppMain.getFrame(), "Please selected a file or folder to rename.");
              return;
          }
          enableNavigationButtons(false);
          String s0 = ListModel.getFileName(file);
          String s = (String)AppMain.Prompt(scrollPane,
                  SGGCResourceBundle.getResourceString(ResourceName.KEY_MSG_PNLBROWSE_RENAME),
                  SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_PNLBROWSE_RENAME_TITLE)
                  ,s0);
          if(null!=s && !s.equals("")) {
              tBrowse.cmdAdd("FileRen",s0,s);
          } else {
        	  enableNavigationButtons(true);
          }
      }
      //Delete Selected
      else if(mnuFileDel==e.getSource() || btnDelete==e.getSource() || e.getActionCommand().compareTo("delete")==0 ){
          if (file == null) {
              AppMain.Message(AppMain.getFrame(), "Please selected a file or folder to delete.");
              return;
          }
          
          if(JOptionPane.YES_OPTION!=AppMain.Confirm(scrollPane,
                  SGGCResourceBundle.getResourceString(ResourceName.KEY_MSG_PNLBROWSE_DELETE))) {
              return;
          }
          
          synchronized(tBrowse){
              enableNavigationButtons(false);
              for (String dir: (Set<String>)selectedTable.keySet()) {

                  if (dir.equals("..") ){//|| dir.equals(getCurrentDir())){
                      continue; //Don't operate in ".." folder
                  }
                  
                  List<FileInfo> files = (List<FileInfo>)selectedTable.get(dir);
                  for (FileInfo fileInfo: files) {
                      // TODO: this probably won't work with the table view.  let's see
                      tBrowse.cmdAdd("FileDel", ListModel.getFileName(fileInfo), new Integer(ListModel.getType(fileInfo)));
                  }
              }
          }
      }
//      else if (mnuFilePublish == e.getSource()) { // uncomment when adding share support
//    	  // copy link to clipboard
//      		LogManager.debug("URL copies to system clipboard");
//      		if (clipboard == null) {
//      			clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
//      		}
//      	
//      		String url = "";
//      		try {
//      			url = ((TGShare)tBrowse.ftpSrvConn.getFtpClient()).getUrl((TGShareFileInfo)tblListing.getSelectedFileInfo());
//				if (file.isDirectory() || file.isSoftLink()) 
//					url += "/";
//				StringSelection stsel  = new StringSelection(url);
//		        clipboard.setContents(stsel,stsel);
//			} catch (Exception e1) {
//				LogManager.error("Failed to copy url to clipboard: " + url, e1);
//			}
//      } 
      else if (mnuFileVersionEnable == e.getSource()) {
      	// lock the file and check out a working copy
      	LogManager.debug("Enable versioning for file");
      	tBrowse.cmdAdd("Version",getCurrentDir() + separator + file.getName(),mnuFileVersionEnable.getText().indexOf("Enable") > -1);
      }
      else if (mnuFileVersionIn == e.getSource()) {
      	// check the file back in and unlock it
      	LogManager.debug("Check in file");
      } 
    }

	public void cmbDir_actionPerformed(ActionEvent e) {
		System.out.println("Action performed on combo box "
				+ e.getSource().toString());
	}

	public void tblListView_mouseClicked(MouseEvent e) {
		int clickedRow = tblListing.rowAtPoint(e.getPoint());
		
		if (clickedRow != -1) {
			FileInfo file = ((DetailListModel) tblListing.getModel()).getFile(clickedRow);
			if (SwingUtilities.isRightMouseButton(e)) {
				// show/hide contextual popup menu items
//				if (tBrowse.ftpServer.type == FTPType.XSHARE) {
//					if (getCurrentDir().equalsIgnoreCase("/Public")) {
//						mnuFilePublish.setText("Get Public URL");
//						mnuFilePublish.setVisible(true);
//					} else {
//						mnuFilePublish.setText("Get Shared URL");
//						mnuFilePublish.setVisible(true);
//					}
//					mnuFileMetadataShow.setVisible(false);
//				} else 
				if (tBrowse.ftpServer.type == FTPType.IRODS) { 
					mnuFilePublish.setVisible(false);
					mnuFileMetadataShow.setVisible(true);
				} else {
					mnuFilePublish.setVisible(false);
					mnuFileMetadataShow.setVisible(false);
				}
				
				// select the row if not already selected
				tblListing.addRowSelectionInterval(clickedRow, clickedRow);

				mnuFile.show(e.getComponent(), e.getX(), e.getY());
	
			} else if (2 == e.getClickCount() && !isRightClickEvent(e)) { // double click the selected file
				// Change dir, if it's a directory
				if (file == null) {
					// ignore when clicking on unselected panel
				} else if (ListModel.getType(file) == FileInfo.DIRECTORY_TYPE
						|| ListModel.getType(file) == FileInfo.SOFTLINK_TYPE) {
					mnuFile_actionPerformed(new ActionEvent(mnuFileOpen,
							ActionEvent.ACTION_PERFORMED, "Click"));
				}
				// Download or Upload file, if it's a file
				else {
					mnuFile_actionPerformed(new ActionEvent(mnuFileDown,
							ActionEvent.ACTION_PERFORMED, "Click"));
				}
			}
		} else {
			// deselect all rows
			tblListing.clearSelection();
		}

	}

	/**
	 * Check to see if the user right clicks with a single button mouse. This is
	 * needed for Mac laptops.
	 * 
	 * @param ev
	 * @return
	 */
	private boolean isRightClickEvent(MouseEvent ev) {
		int onmask = InputEvent.CTRL_DOWN_MASK | InputEvent.BUTTON1_DOWN_MASK;
	    int offmask = InputEvent.SHIFT_DOWN_MASK;
	    if ((ev.getModifiersEx() & (onmask | offmask)) == onmask) {
	    	return true;
	    } else {
	    	return false;
	    }
	}

	public JTextArea getTxtLog() {
		return this.txtLog;
	}

	public boolean isRemote() {
		return this.bRemoteMode;
	}

	private class LinkAction extends AbstractAction {

		public LinkAction(String linkText, String link) {
			// Save the link's text and the actual link for later recall when
			// the
			// user clicks the hyperlink.

			putValue(Action.NAME, linkText);
			putValue(Action.SHORT_DESCRIPTION, link);
		}

		public void actionPerformed(ActionEvent e) {

			// Retrieve the actual link and output link to the console (for test
			// purposes).
			String link = (String) getValue(Action.SHORT_DESCRIPTION);
			LogManager
					.debug("User clicked on the link to path element " + link);
			if (currentDir.endsWith(link)) {
				LogManager
						.debug("User clicked on the current directory. Ignoring.");
				return;
			}
			System.out.println("Action performed on combo box "
					+ e.getSource().toString());
			int index = mdlSystemPath.getIndexOf(link);

			// for (int i=cmbPath.getItemCount()-1;i>index;i--) {
			// mdlSystemPath.removeElementAt(i);
			// }
			System.out.println(currentDir);
			if (!link.equals(shareFolderName)) {
				currentDir = currentDir.substring(0, currentDir.indexOf(link))
					+ link;
			}
			try {
				if (tBrowse != null)
					list(tBrowse.list(currentDir));
			} catch (ServerException e1) {
				e1.printStackTrace();
			} catch (ClientException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}

			// int pathsize = mdlSystemPath.getSize();

			// System.out.println("Path has " + pathsize +
			// " elements.\nSetting path index to " +
			// (pathsize-1) + "\nDirectory is " +
			// cmbPath.getItemAt(pathsize-1));
			// mdlSystemPath.setSelectedItem(cmbPath.getItemAt(pathsize-1));
			// cmbPath.revalidate();
			// mdlSystemPath.fireMe(cmbPath,index,index);
		}
	}

	class SystemPathComboBoxModel extends DefaultComboBoxModel {
	
		private boolean loadingPath = false;
	
		public SystemPathComboBoxModel() {
			super();
		}
	
		/**
		 * Set the contents of the model to the folders listed in the given path.
		 * 
		 * @param path
		 *            path to be held in the model.
		 */
		public SystemPathComboBoxModel(String path, boolean isNix) {
			super();
	
			List<String> pathElements = null;
	
			if (isNix) {
				// if the os isn't windows, the leading slash will be disregarded.
				// Thus, we need to insert the root directory into the array.
				pathElements = Arrays.asList(path.split("/"));
				if (pathElements.size() > 0) {
					pathElements.set(0, "/");
				} else {
					pathElements = new ArrayList<String>();
					pathElements.add("/");
				}
			} else {
				pathElements = Arrays.asList(path.split("\\\\"));
			}
			
			// TODO: how do we resolve the nonce to share in the path?
//			if (ftpServer.type == FTPType.XSHARE) {
//				if (pathElements.size() == 3 && pathElements.get(1).equals("shared")) {
//					String nonce = pathElements.get(2);
//					if (nonce.equals(shareFolderNonce)) {
//						pathElements.set(2, shareFolderName);
//					} 
//				}
//			}
	
			for (int i = pathElements.size() - 1; i >= 0; i--) {
				addElement(pathElements.get(i));
			}
		}
	
		public void fireMe(JComboBox box, int oIndex, int nIndex) {
			super.fireContentsChanged(box, oIndex, nIndex);
		}
	
	}
}
@SuppressWarnings("serial")
class ResourceComboBoxModel extends DefaultComboBoxModel {
	private ArrayList<FTPSettings> siteList;

	public ResourceComboBoxModel(ArrayList<FTPSettings> siteList) {
		super();

		this.siteList = siteList;

		addElement("Select Resource");

		// add all compute resources
		addElement("Compute");
		ArrayList<FTPSettings> prunedSiteList = getResourceByType(DBUtil.HPC);
		for (int i = 0; i < prunedSiteList.size(); i++) {
			addElement(prunedSiteList.get(i));
		}

		// add all visualization resources
		addElement("Viz");
		prunedSiteList = getResourceByType(DBUtil.VIZ);
		for (int i = 0; i < prunedSiteList.size(); i++) {
			addElement(prunedSiteList.get(i));
		}

		// add all archive resources
		addElement("Archive");
		prunedSiteList = getResourceByType(DBUtil.ARCHIVE);
		for (int i = 0; i < prunedSiteList.size(); i++) {
			addElement(prunedSiteList.get(i));
		}
		
		addElement("Add/Remove");

	}

	public void merge(List<FTPSettings> sites) {
		for (FTPSettings site : sites) {
			if (!siteList.contains(site)) {
				siteList.add(site);
				addElement(site);
			}
		}
	}

	private ArrayList<FTPSettings> getResourceByType(String type) {
		ArrayList<FTPSettings> listResourceByType = new ArrayList<FTPSettings>();

		for (FTPSettings site : siteList) {
			if (site.hostType.equals(type)) {
				listResourceByType.add(site);
			}
		}

		return listResourceByType;
	}
}

class PnlBrowse_mnuFile_actionAdapter implements ActionListener {
	private PnlBrowse adaptee;

	PnlBrowse_mnuFile_actionAdapter(PnlBrowse adaptee) {
		this.adaptee = adaptee;
	}

	public void actionPerformed(ActionEvent e) {
		adaptee.mnuFile_actionPerformed(e);
	}
}

class PnlBrowse_cmbDir_actionAdapter implements ActionListener {
	private PnlBrowse adaptee;

	PnlBrowse_cmbDir_actionAdapter(PnlBrowse adaptee) {
		this.adaptee = adaptee;
	}

	public void actionPerformed(ActionEvent e) {
		adaptee.cmbDir_actionPerformed(e);
	}
}

class PnlBrowse_tblListView_mouseAdapter implements MouseListener {
	private PnlBrowse adaptee;

	PnlBrowse_tblListView_mouseAdapter(PnlBrowse adaptee) {
		this.adaptee = adaptee;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		adaptee.tblListView_mouseClicked(e);
	}

	@Override
	public void mouseReleased(MouseEvent e) {}

	@Override
	public void mousePressed(MouseEvent e) {}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}
	
	
}

class NavActionListener implements java.awt.event.ActionListener {
	private PnlBrowse parent = null;

	public NavActionListener(PnlBrowse parent) {
		this.parent = parent;
	}

	public void actionPerformed(ActionEvent e) {
		LogManager.debug("Pressed the "
				+ ((JButton) e.getSource()).getToolTipText() + " button.");
		parent.mnuFile_actionPerformed(e);
	}
}

@SuppressWarnings("serial")
class FileTableModel extends AbstractTableModel {
	private static double[] arrSize = new double[] { 1 << 10, 1 << 20, 1 << 30,
			1 << 40 };

	private final String[] TABLE_COLUMN_NAMES = new String[] {
			SGGCResourceBundle
					.getResourceString(ResourceName.KEY_DISPLAY_LISTMODEL_COLUMN_NAME),
			SGGCResourceBundle
					.getResourceString(ResourceName.KEY_DISPLAY_LISTMODEL_COLUMN_SIZE)
	// SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_LISTMODEL_COLUMN_TYPE),
	// SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_LISTMODEL_COLUMN_MODIFIED),
	// SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_LISTMODEL_COLUMN_ATTRIBUTES),
	// SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_LISTMODEL_COLUMN_DESCRIPTION),
	// SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_LISTMODEL_COLUMN_OWNER),
	};

	private ArrayList<File> files = new ArrayList<File>();

	public FileTableModel(String currentDir) {
		try {
			File dir = new File(currentDir);
			if (dir.exists())
				files.addAll(java.util.Arrays.asList(dir.listFiles()));
			else
				AppMain.Error(null, "Unable to access " + currentDir,
						"Directory Listing Error");
		} catch (Exception e) {
			AppMain.Error(null, e.getMessage(), "Directory Listing Error");
		}
	}

	public String getColumnName(int col) {
		return TABLE_COLUMN_NAMES[col].toString();
	}

	public int getColumnCount() {
		return TABLE_COLUMN_NAMES.length;
	}

	@SuppressWarnings("unchecked")
	public Class getColumnClass(int c) {
		return getValueAt(0, c).getClass();
	}

	public int getRowCount() {
		return files.size();
	}

	public Object getValueAt(int row, int column) {
		Object value = null;
		if (row >= files.size()) {
			return null;
		}
		File file = (File) files.get(row);
		switch (column) {
		case 0:
			// Name
			// value = new
			// IconData((file.isDirectory()?AppMain.icoFolder:AppMain.icoFile),
			// getOldFileName(file));
			value = file.getName();
			break;
		case 1:
			// Size
			value = getSize(file);
			break;
		// case 2:
		// //Type
		// value = getTypeName(file);
		// break;
		// case 3:
		// //Modified
		// value = (0==row) ? "" : file.getDate() + ", " + file.getTime();
		// break;
		// case 4:
		// //Attributes
		// value = (0==row) ? "" : getTypeShort(file) + getMode(file.getMode());
		// break;
		// case 5:
		// //Description
		// value = "";
		// break;
		// case 6:
		// //Owner
		// value = "";
		// break;
		default:
			value = "";
			break;
		}
		return value;
	}

	public static String getSize(File file) {
		String sSize;
		long nSize = file.length();
		NumberFormat f = NumberFormat.getInstance();
		f.setMaximumFractionDigits(1);

		if (nSize < arrSize[0]) {
			sSize = String.valueOf(nSize) + " B";
		} else if (nSize < arrSize[1]) {
			sSize = String.valueOf(f.format(nSize / arrSize[0])) + " KB";
		} else if (nSize < arrSize[2]) {
			sSize = String.valueOf(f.format(nSize / arrSize[1])) + "MB";
		} else {
			sSize = String.valueOf(f.format(nSize / arrSize[2])) + "GB";
		}
		return sSize;
	}
}

@SuppressWarnings({"serial","unused"})
class MenuButton extends JButton implements MouseListener {
	private Border margin = BorderFactory.createEmptyBorder(1, 5, 1, 5);
	private Border empty = BorderFactory.createEmptyBorder(1, 1, 1, 1);
	private Border raised = new OneLineBevelBorder(BevelBorder.RAISED);
	private Border lowered = new OneLineBevelBorder(BevelBorder.LOWERED);
	private Border normal = BorderFactory.createCompoundBorder(empty, margin);
	private Border entered = BorderFactory.createCompoundBorder(raised, margin);
	private Border pressed = BorderFactory
			.createCompoundBorder(lowered, margin);

	private String oldttt = null;

	public MenuButton() {
		setBorder(normal);
		setBackground(null);
		setContentAreaFilled(false);
		setFocusPainted(false);
		addMouseListener(this);
	}

	// public void setEnabled(boolean enabled) {
	// super.setEnabled(enabled);
	// if (!enabled) {
	// oldttt = getToolTipText();
	// setToolTipText("Shortcuts cannot be resolved.");
	// } else {
	// if (!getToolTipText().equals("")) {
	// setToolTipText(oldttt);
	// }
	// }
	// }

	public void mouseClicked(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {

		if (isEnabled())
			setBorder(entered);

	}

	public void mouseExited(MouseEvent e) {
		if (isEnabled())
			setBorder(normal);
	}

	public void mousePressed(MouseEvent e) {
		if (isEnabled())
			setBorder(pressed);
	}

	public void mouseReleased(MouseEvent e) {
		if (isEnabled()) {
			if (getBorder() == pressed)
				setBorder(entered);
		}
	}

}

@SuppressWarnings("serial")
class Hyperlink extends JButton {
	// Hyperlink color.

	private static final Color LINK_COLOR = new Color(190, 31, 36);//Color.blue;

	// This border erases the single underline from the HOVER_BORDER.

	private static final Border LINK_BORDER = BorderFactory.createEmptyBorder(
			0, 0, 0, 0);

	// This border presents a single underline in the LINK_COLOR.

	private static final Border HOVER_BORDER = BorderFactory.createMatteBorder(
			0, 0, 0, 0, LINK_COLOR);

	Hyperlink(final Action action) {
		// Replace the default button border with a LINK_BORDER. This border
		// results in no border being displayed. (Borders give buttons their
		// distinctive appearance.)

		setBorder(LINK_BORDER);

		// Do not paint button background in J2SE 5.0's new Metal Look and Feel.
		// This also applies to Windows XP and other look and feels.

		setContentAreaFilled(false);

		// If this component has a JFrame ancestor, change the mouse pointer to
		// a
		// hand when it is positioned over the button's text.

		setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

		// Do not paint a focus rectangle (which will not have the correct size
		// anyway) on the button when the button has the focus.

		setFocusPainted(false);

		// The button's text appears in the color specified by LINK_COLOR.

		setForeground(Color.decode("#000000"));

		// Establish an action listener to invoke the action's actionPerformed()
		// method when the user selects the button (or we can think about this
		// as
		// the user selecting a hyperlink).

		addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				action.actionPerformed(e);
			}
		});

		// Install a focus listener that underlines the button text when the
		// button receives focus and removes this underline when focus is lost.

		addFocusListener(new LinkFocusListener());

		// Install a mouse listener that causes focus to shift to the button
		// when
		// the mouse enters the button.

		addMouseListener(new LinkMouseListener());

		// Obtain the contents of the action's NAME property as the button's
		// text. This is the link's text.

		setText((String) action.getValue(Action.NAME));

		// Obtain the contents of the action's SHORT_DESCRIPTION property as the
		// button's tooltip text. This is the link.

		setToolTipText((String) action.getValue(Action.SHORT_DESCRIPTION));
	}

	private class LinkFocusListener extends FocusAdapter {
		public void focusGained(FocusEvent e) {
			((JComponent) e.getComponent()).setBorder(HOVER_BORDER);
		}

		public void focusLost(FocusEvent e) {
			((JComponent) e.getComponent()).setBorder(LINK_BORDER);
		}
	}

	private class LinkMouseListener extends MouseAdapter {
		public void mouseEntered(MouseEvent e) {
			((JButton)e.getSource()).setForeground(Color.decode("#2379BF"));
			((JComponent) e.getComponent()).requestFocusInWindow();
		}
		public void mouseExited(MouseEvent e) {
			((JButton)e.getSource()).setForeground(Color.decode("#000000"));
		}
	}
}

@SuppressWarnings("serial")
class OneLineBevelBorder extends AbstractBorder {
	/** Raised bevel type. */
	public static final int RAISED = 0;
	/** Lowered bevel type. */
	public static final int LOWERED = 1;

	protected int bevelType;
	protected Color highlight;
	protected Color shadow;

	/**
	 * Creates a bevel border with the specified type and whose colors will be
	 * derived from the background color of the component passed into the
	 * paintBorder method.
	 * 
	 * @param bevelType
	 *            the type of bevel for the border
	 */
	public OneLineBevelBorder(int bevelType) {
		this.bevelType = bevelType;
	}

	/**
	 * Creates a bevel border with the specified type, highlight and shadow
	 * colors.
	 * 
	 * @param bevelType
	 *            the type of bevel for the border
	 * @param highlight
	 *            the color to use for the bevel highlight
	 * @param shadow
	 *            the color to use for the bevel shadow
	 */
	public OneLineBevelBorder(int bevelType, Color highlight, Color shadow) {
		this(bevelType);
		this.highlight = highlight;
		this.shadow = shadow;
	}

	/**
	 * Paints the border for the specified component with the specified position
	 * and size.
	 * 
	 * @param c
	 *            the component for which this border is being painted
	 * @param g
	 *            the paint graphics
	 * @param x
	 *            the x position of the painted border
	 * @param y
	 *            the y position of the painted border
	 * @param width
	 *            the width of the painted border
	 * @param height
	 *            the height of the painted border
	 */
	public void paintBorder(Component c, Graphics g, int x, int y, int width,
			int height) {
		if (bevelType == RAISED) {
			paintRaisedBevel(c, g, x, y, width, height);
		} else if (bevelType == LOWERED) {
			paintLoweredBevel(c, g, x, y, width, height);
		}
	}

	/**
	 * Returns the insets of the border.
	 * 
	 * @param c
	 *            the component for which this border insets value applies
	 */
	public Insets getBorderInsets(Component c) {
		return new Insets(1, 1, 1, 1);
	}

	/**
	 * Reinitialize the insets parameter with this Border's current Insets.
	 * 
	 * @param c
	 *            the component for which this border insets value applies
	 * @param insets
	 *            the object to be reinitialized
	 */
	public Insets getBorderInsets(Component c, Insets insets) {
		insets.left = insets.top = insets.right = insets.bottom = 1;
		return insets;
	}

	/**
	 * Returns the highlight color of the bevel border when rendered on the
	 * specified component. If no highlight color was specified at
	 * instantiation, the highlight color is derived from the specified
	 * component's background color.
	 * 
	 * @param c
	 *            the component for which the highlight may be derived
	 */
	public Color getHighlightColor(Component c) {
		Color highlight = getHighlightColor();
		return highlight != null ? highlight : c.getBackground().brighter();
	}

	/**
	 * Returns the shadow color of the bevel border when rendered on the
	 * specified component. If no shadow color was specified at instantiation,
	 * the shadow color is derived from the specified component's background
	 * color.
	 * 
	 * @param c
	 *            the component for which the shadow may be derived
	 */
	public Color getShadowColor(Component c) {
		Color shadow = getShadowColor();
		return shadow != null ? shadow : c.getBackground().darker();
	}

	/**
	 * Returns the highlight color of the bevel border. Will return null if no
	 * highlight color was specified at instantiation.
	 */
	public Color getHighlightColor() {
		return highlight;
	}

	/**
	 * Returns the shadow color of the bevel border. Will return null if no
	 * shadow color was specified at instantiation.
	 */
	public Color getShadowColor() {
		return shadow;
	}

	/**
	 * Returns the type of the bevel border.
	 */
	public int getBevelType() {
		return bevelType;
	}

	/**
	 * Returns whether or not the border is opaque.
	 */
	public boolean isBorderOpaque() {
		return true;
	}

	protected void paintRaisedBevel(Component c, Graphics g, int x, int y,
			int width, int height) {
		Color oldColor = g.getColor();
		int h = height;
		int w = width;

		g.translate(x, y);

		g.setColor(getHighlightColor(c));
		g.drawLine(0, 0, 0, h - 2);
		g.drawLine(0, 0, w - 2, 0);

		g.setColor(getShadowColor(c));
		g.drawLine(0, h - 1, w - 1, h - 1);
		g.drawLine(w - 1, 0, w - 1, h - 2);

		g.translate(-x, -y);
		g.setColor(oldColor);
	}

	protected void paintLoweredBevel(Component c, Graphics g, int x, int y,
			int width, int height) {
		Color oldColor = g.getColor();
		int h = height;
		int w = width;

		g.translate(x, y);

		g.setColor(getShadowColor(c));
		g.drawLine(0, 0, 0, h - 1);
		g.drawLine(1, 0, w - 1, 0);

		g.setColor(getHighlightColor(c));
		g.drawLine(1, h - 1, w - 1, h - 1);
		g.drawLine(w - 1, 1, w - 1, h - 2);

		g.translate(-x, -y);
		g.setColor(oldColor);
	}

}
