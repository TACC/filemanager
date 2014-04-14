/* 
 * Created on Aug 16, 2008
 * 
 * Developed by: Rion Dooley - dooley [at] tacc [dot] utexas [dot] edu
 * 				 TACC, Texas Advanced Computing Center
 * 
 * https://www.tacc.utexas.edu/
 */

package org.teragrid.portal.filebrowser.applet.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.table.AbstractTableModel;

import org.globus.ftp.FileInfo;
import org.jdesktop.swingx.JXBusyLabel;
import org.jdesktop.swingx.icon.EmptyIcon;
import org.jdesktop.swingx.painter.BusyPainter;
import org.teragrid.portal.filebrowser.applet.AppMain;
import org.teragrid.portal.filebrowser.applet.ConfigOperation;
import org.teragrid.portal.filebrowser.applet.transfer.FTPSettings;
import org.teragrid.portal.filebrowser.applet.transfer.SearchResult;
import org.teragrid.portal.filebrowser.applet.transfer.UrlSearch;
import org.teragrid.portal.filebrowser.applet.ui.permissions.StripedTable;
import org.teragrid.portal.filebrowser.applet.ui.table.DetailListModel;
import org.teragrid.portal.filebrowser.applet.util.LogManager;
import org.teragrid.portal.filebrowser.applet.util.ResourceName;
import org.teragrid.portal.filebrowser.applet.util.SGGCResourceBundle;

import com.explodingpixels.macwidgets.IAppWidgetFactory;

import edu.utexas.tacc.wcs.filemanager.common.model.enumeration.FileProtocolType;

/**
 * Search panel to replace the browsing panel when looking for folders and
 * files.
 * 
 * @author Rion Dooley < dooley [at] tacc [dot] utexas [dot] edu >
 * 
 */
@SuppressWarnings({"serial","unused","unchecked"})
public class PnlIrodsMetadataQuery extends JPanel {

	private PnlBrowse parent;

	private JPanel pnlSearchForm;
	private JPanel pnlProgress;

	private JXBusyLabel lblBusy;

	private StripedTable tblSearchResults;

	private SearchResultsTableModel mdlSearchResults;

	private JScrollPane spSearchResults;

	private RoundField txtSearch;

	private JPopupMenu mnuFile = new JPopupMenu();
	private JMenuItem mnuFileDown; // Download Selected
	private JMenuItem mnuFileInfo; // Show file info
	private JMenuItem mnuFileACL; // Show s3 ACL panel
	private JMenuItem mnuFileDel; // Deleted Selected
	private JMenuItem mnuFileRen; // Rename
	private JMenuItem mnuFileBookmark; // Go Home
	private JMenuItem mnuFileOpen;
	private JButton btnStop; // stop the search;

	// private SearchThread fSearch;

	private JLabel lblProgress;

	private Dimension preferredRowSize = new Dimension(175, 20);
	private Dimension maxRowSize = new Dimension(Integer.MAX_VALUE, 25);

	private UrlSearch urlSearch;

	public PnlIrodsMetadataQuery(PnlBrowse pnlBrowse) {
		this();
		this.parent = pnlBrowse;
	}

	public PnlIrodsMetadataQuery() {
		super();
		psInit();
	}

	private void psInit() {

		initSearchFormPanel();

		initSearchResultPanel();

		initPopMenu();

		initProgressPanel();

		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		initLayout();
	}

	private void initSearchFormPanel() {
		txtSearch = new RoundField();
//		txtSearch.setMinimumSize(minRowSize);
		txtSearch.setPreferredSize(preferredRowSize);
		txtSearch.setMaximumSize(preferredRowSize);
		txtSearch.addKeyListener(new KeyListener() {

			public void keyPressed(KeyEvent arg0) {
			}

			public void keyReleased(KeyEvent arg0) {
			}

			public void keyTyped(KeyEvent event) {
				if (event.getKeyChar() == KeyEvent.VK_ENTER) {
					beginSearch(txtSearch.getText());
				}

			}

		});

		btnStop = new JButton(new ShortCutAction("", AppMain.icoStop, "Stop",
				KeyEvent.VK_X));
		btnStop.setEnabled(false);

		pnlSearchForm = new JPanel();
		pnlSearchForm.setBackground(Color.GRAY);
		pnlSearchForm.setBorder(BorderFactory.createEmptyBorder(3,3,3,3));
		pnlSearchForm.setLayout(new BoxLayout(pnlSearchForm, BoxLayout.LINE_AXIS));
		pnlSearchForm.add(Box.createHorizontalGlue());
		pnlSearchForm.add(txtSearch);
		pnlSearchForm.add(btnStop);
		pnlSearchForm.add(Box.createRigidArea(new Dimension(5,20)));
		
		
//		pnlSearchForm.setMinimumSize(minRowSize);
//		pnlSearchForm.setPreferredSize(preferredRowSize);
//		pnlSearchForm.setMaximumSize(maxRowSize);

	}

	private void initSearchResultPanel() {
		mdlSearchResults = new SearchResultsTableModel();
		tblSearchResults = new StripedTable(mdlSearchResults);
		spSearchResults = new JScrollPane();

		tblSearchResults.addMouseListener(new SearchTableMouseAdapter());
//		tblSearchResults.setBackground(getBackground());
		tblSearchResults.setMinimumSize(new Dimension(100, 100));
		// tblSearchResults.setPreferredSize(new Dimension(200,100));
		tblSearchResults.setMaximumSize(new Dimension(Integer.MAX_VALUE,
				Integer.MAX_VALUE));

		// spSearchResults.setHorizontalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		// spSearchResults.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		spSearchResults.getViewport().add(tblSearchResults);
		// spSearchResults.addMouseListener(new SearchTableMouseAdapter());
		spSearchResults.getViewport().setBackground(getBackground());
		IAppWidgetFactory.makeIAppScrollPane(spSearchResults);

	}

	private void initPopMenu() {
		mnuFileDown = new JMenuItem(new ShortCutAction(SGGCResourceBundle
				.getResourceString(ResourceName.KEY_DISPLAY_DOWNLOAD),
				AppMain.icoDownload, "Download", KeyEvent.VK_Y));
		mnuFileInfo = new JMenuItem(new ShortCutAction(SGGCResourceBundle
				.getResourceString(ResourceName.KEY_DISPLAY_INFO),
				AppMain.icoInfo, "Info", KeyEvent.VK_I));
		mnuFileACL = new JMenuItem(new ShortCutAction("Sharing...",
				AppMain.icoDownload, "ACL", KeyEvent.VK_A));
		mnuFileACL.setVisible(false);
		mnuFileDel = new JMenuItem(new ShortCutAction(SGGCResourceBundle
				.getResourceString(ResourceName.KEY_DISPLAY_DELSEL),
				AppMain.icoDelete, "Delete", KeyEvent.VK_D));
		mnuFileRen = new JMenuItem(new ShortCutAction(SGGCResourceBundle
				.getResourceString(ResourceName.KEY_DISPLAY_RENAME),
				AppMain.icoRename, "Rename", KeyEvent.VK_R));
		mnuFileBookmark = new JMenuItem(new ShortCutAction("Bookmark",
				AppMain.icoBook, "Bookmark", KeyEvent.VK_B));
		mnuFileOpen = new JMenuItem(new ShortCutAction(SGGCResourceBundle
				.getResourceString(ResourceName.KEY_DISPLAY_OPEN),
				AppMain.icoDownload, "Open", KeyEvent.VK_G));

		mnuFile.add(mnuFileDown);
		mnuFile.add(mnuFileOpen);
		mnuFile.addSeparator();
		mnuFile.add(mnuFileInfo);
		mnuFile.add(mnuFileACL);
		mnuFile.add(mnuFileRen);
		mnuFile.add(mnuFileDel);
		mnuFile.addSeparator();
		mnuFile.add(mnuFileBookmark);
	}

	private void initProgressPanel() {
		pnlProgress = new JPanel();
		sizeRow(pnlProgress);

		// PnlBusyLoading pnlBusy = new PnlBusyLoading();
		// Dimension dim = new Dimension(16,16);
		// lblBusy = new JXBusyLabel(dim);
		// BusyPainter painter = new BusyPainter(
		// new RoundRectangle2D.Float(0, 0,15.5f,4.1f,10.0f,10.0f),
		// new Ellipse2D.Float(7.5f,7.5f,30.26f,34.0f));
		// painter.setTrailLength(4);
		// painter.setPoints(8);
		// painter.setFrame(-1);
		// lblBusy.setPreferredSize(dim);
		// lblBusy.setIcon(new EmptyIcon(dim.width,dim.height));
		// lblBusy.setBusyPainter(painter);
		// lblBusy.setVisible(false);

		lblBusy = new JXBusyLabel(new Dimension(22, 18));
		BusyPainter painter = new BusyPainter(new RoundRectangle2D.Float(0, 0,
				4.5f, 2.5f, 10.0f, 10.0f), new Ellipse2D.Float(2.5f, 2.5f,
				13.0f, 13.0f));
		painter.setTrailLength(4);
		painter.setPoints(8);
		painter.setFrame(-1);
		lblBusy.setPreferredSize(new Dimension(22, 18));
		lblBusy.setIcon(new EmptyIcon(22, 18));
		lblBusy.setBusyPainter(painter);
		lblBusy.setVisible(false);
		lblBusy.setAlignmentX(RIGHT_ALIGNMENT);
		lblBusy.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent arg0) {
				super.mouseClicked(arg0);
				stopSearch();
			}
		});

		lblProgress = new JLabel();
		lblProgress.setLayout(new BoxLayout(lblProgress, BoxLayout.X_AXIS));
		lblProgress.setAlignmentX(CENTER_ALIGNMENT);

		pnlProgress.add(lblProgress, BorderLayout.CENTER);
		pnlProgress.add(Box.createHorizontalGlue());
		pnlProgress.add(lblBusy, BorderLayout.EAST);
		pnlProgress.setBorder(BorderFactory.createLineBorder(this.getBackground(), 5));

	}

	private void initLayout() {
		setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
//		add(Box.createRigidArea(new Dimension(10,5)));
		add(pnlSearchForm, BorderLayout.NORTH);
//		add(Box.createRigidArea(new Dimension(10,5)));
		add(spSearchResults, BorderLayout.CENTER);
		add(pnlProgress, BorderLayout.SOUTH);
		
	}
	private void sizeRow(Component c) {
		c.setMinimumSize(preferredRowSize);
		c.setPreferredSize(preferredRowSize);
		c.setMaximumSize(maxRowSize);
	}

	private SearchResult getSelectedSearchResult() {
		return mdlSearchResults.getSearchResultAtRow(tblSearchResults
				.getSelectedRow());
	}

	private Hashtable<String, ArrayList<FileInfo>> getSelectedSearchResultsTable() {
		Hashtable<String, ArrayList<FileInfo>> resultsTable = new Hashtable<String, ArrayList<FileInfo>>();

		int[] nRows = tblSearchResults.getSelectedRows();

		LogManager.debug("There are " + nRows.length + " selected.");

		for (int i = 0; i < nRows.length; i++) {
			SearchResult result = mdlSearchResults
					.getSearchResultAtRow(nRows[i]);
			if (resultsTable.contains(result.getPath())) {
				if (!resultsTable.get(result.getPath()).contains(
						result.getFile())) {
					resultsTable.get(result.getPath()).add(result.getFile());
				}
			} else {
				ArrayList<FileInfo> fileList = new ArrayList<FileInfo>();
				fileList.add(result.getFile());
				resultsTable.put(result.getPath(), fileList);
			}
		}

		return resultsTable;
	}

	private void showPopupMenu(Point p) {

		SearchResult result = getSelectedSearchResult();

		// disable bookmark specific actions if no
		// bookmark is selected.
		mnuFileACL.setVisible(parent.ftpServer.protocol.equals(FileProtocolType.S3));

		mnuFileBookmark.setVisible(result.getFile().isDirectory());

		mnuFile.show(this, p.x, p.y);
	}

	protected void suspendLayout() {
		btnStop.setEnabled(true);
		txtSearch.setEnabled(false);
		lblProgress.setText("");
		lblBusy.setVisible(true);
		lblBusy.setBusy(true);

		validate();
		repaint();
	}

	protected void resumeLayout() {
		btnStop.setEnabled(false);
		txtSearch.setEnabled(true);
		lblBusy.setVisible(false);
		lblBusy.setBusy(false);
		lblProgress.setText("Search returned " + mdlSearchResults.getRowCount()
				+ " results");
		tblSearchResults.setCursor(Cursor.getDefaultCursor());

	}

	private void beginSearch(String regex) {
		suspendLayout();

		mdlSearchResults.clear();

		parent.tBrowse.cmdAdd("Find", null, regex);
	}

	protected void stopSearch() {
		if (urlSearch != null)
			urlSearch.cancel();

		urlSearch = null;

		resumeLayout();
	}

	public void setUrlSearch(UrlSearch urlSearch) {
		this.urlSearch = urlSearch;
	}

	public void addSearchResults(List<SearchResult> searchResults) {

		mdlSearchResults.addSearchResults(searchResults);

	}

	public void addSearchResult(SearchResult searchResult) {

		mdlSearchResults.addSearchResult(searchResult);

	}

	public void list(List<SearchResult> searchResults) {

		mdlSearchResults.setSearchResultsList(searchResults);

		resumeLayout();

		tblSearchResults.validate();
		tblSearchResults.repaint();
	}

	public void updateSearchResultSummary(String path) {
		lblProgress.setFont(lblProgress.getFont().deriveFont((float) 9));
		lblProgress.setText("Searching " + path);

		//        
		// validate();
		// repaint();
	}

	public void updateSearchCompleted() {
		urlSearch = null;

		resumeLayout();
	}

	@SuppressWarnings("rawtypes")
	private void mnuFile_actionPerformed(ActionEvent e) {

		Hashtable<String, ArrayList<FileInfo>> selectedTable = getSelectedSearchResultsTable();

		if (selectedTable.size() == 0)
			return;

		if (e.getActionCommand().compareTo("Stop") == 0) {

			stopSearch();

		} else if (e.getSource() == mnuFileDown
				|| e.getActionCommand().compareTo("Download") == 0) {

			for (String file : (Set<String>) selectedTable.keySet()) {
				TransferProxy.transfer(parent.frmParent, (List) selectedTable
						.get(file), parent.ftpServer, FTPSettings.Local
						.clone(parent), file, parent.frmParent
						.getLocalBrowser().getCurrentDir(), parent.frmParent
						.getLocalBrowser().getFileList());
			}

		} else if (e.getSource() == mnuFileInfo
				|| e.getActionCommand().compareTo("Info") == 0) {

			for (String path : (Set<String>) selectedTable.keySet()) {
				for (FileInfo file : selectedTable.get(path)) {
					DlgInfo f = new DlgInfo(AppMain.getFrame(),
							parent.tBrowse, path,
							file, null);
				}
			}

		} else if (e.getSource() == mnuFileOpen
				|| e.getActionCommand().compareTo("Open") == 0) {

			SearchResult result = getSelectedSearchResult();
			parent.tBrowse.cmdAdd("Cwd", result.toString(), null);
			parent.btnSearch.doClick();

		} else if (e.getSource() == mnuFileRen
				|| e.getActionCommand().compareTo("Rename") == 0) {
			SearchResult result = getSelectedSearchResult();
			String s0 = result.getFile().getName();
			String s = (String) AppMain
					.Prompt(
							this,
							SGGCResourceBundle
									.getResourceString(ResourceName.KEY_MSG_PNLBROWSE_RENAME),
							SGGCResourceBundle
									.getResourceString(ResourceName.KEY_DISPLAY_PNLBROWSE_RENAME_TITLE),
							s0);
			if (null != s && !s.equals("")) {
				parent.tBrowse.cmdAdd("FileRen", result.toString(), result
						.getPath()
						+ parent.separator + s);
				result.getFile().setName(s);
			}

		} else if (e.getSource() == mnuFileBookmark
				|| e.getActionCommand().compareTo("Bookmark") == 0) {
			SearchResult result = getSelectedSearchResult();

			String name = AppMain.Prompt(this,
					"Please enter name of new bookmark");

			if (name == null) {
				return;
			} else if (name.equals("")) {
				name = result.getFile().getName();
			}

			String path = result.getFile().isDirectory() ? result.toString()
					: result.getPath();

			ConfigOperation.getInstance().addBookmark(parent.ftpServer.host,
					new Bookmark(name, path));

		} else if (e.getSource() == mnuFileDel
				|| e.getActionCommand().compareTo("Delete") == 0) {
			if (JOptionPane.YES_OPTION != AppMain
					.Confirm(
							this,
							SGGCResourceBundle
									.getResourceString(ResourceName.KEY_MSG_PNLBROWSE_DELETE))) {
				return;
			}

			synchronized (parent.tBrowse) {
				for (String dir : (Set<String>) selectedTable.keySet()) {

					System.out.println("Current directory of delete item is: "
							+ dir);
					if (dir.equals("..")) {// || dir.equals(getCurrentDir())){
						continue; // Don't operate in ".." folder
					}

					List<FileInfo> files = (List<FileInfo>) selectedTable
							.get(dir);
					for (FileInfo fileInfo : files) {
//						System.out.println("Adding file " + dir
//								+ parent.separator + fileInfo.getName()
//								+ " to the list of files to delete.");
						parent.tBrowse.cmdAdd("FileDel", dir + parent.separator
								+ fileInfo.getName(), new Integer(ListModel
								.getType(fileInfo)));
					}
				}
			}
		}
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame("Bookmark Demo");
		PnlIrodsMetadataQuery s = new PnlIrodsMetadataQuery();
		frame.setContentPane(s);
		frame.pack();
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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

	class SearchTableMouseAdapter extends MouseAdapter {

		/*
		 * Overrides the default mouseadaptor behavior by displaying a popup
		 * menu on right click (platform independent) and closing the window and
		 * listing the contents of the directory in the other window.
		 * 
		 * @see
		 * java.awt.event.MouseAdapter#mouseClicked(java.awt.event.MouseEvent)
		 */
		@Override
		public void mouseClicked(MouseEvent e) {
			super.mouseClicked(e);

			if (isRightClickEvent(e)) {
				int clickedRow = tblSearchResults.rowAtPoint(e.getPoint());
				int clickedCol = tblSearchResults.columnAtPoint(e.getPoint());
				// could be clicking on empty table area
				if (clickedRow >= 0) {
					tblSearchResults.setRowSelectionInterval(clickedRow,
							clickedRow);
					tblSearchResults.setColumnSelectionInterval(clickedCol,
							clickedCol);
				} else
					tblSearchResults.clearSelection();

				showPopupMenu(e.getPoint());
			} else if (e.getClickCount() == 2) {
				SearchResult result = getSelectedSearchResult();
				if (result != null) {
					if (result.getFile().isDirectory()
							|| result.getFile().isSoftLink()) {
						mnuFile_actionPerformed(new ActionEvent(mnuFileOpen,
								ActionEvent.ACTION_PERFORMED, "Click"));
					} else {
						mnuFile_actionPerformed(new ActionEvent(mnuFileDown,
								ActionEvent.ACTION_PERFORMED, "Click"));
						// parent.tBrowse.cmdAdd("Cwd",result.getPath(),null);
						// int row =
						// ((DetailListModel)parent.tblListing.getModel()).getRow(result.getFile());
						// parent.tblListing.getSelectionModel().setSelectionInterval(row,row);
						// parent.mnuFile_actionPerformed(
						// new
						// ActionEvent(parent.mnuFileDown,ActionEvent.ACTION_PERFORMED,"Click"));
					}
				}
			}
		}

		/**
		 * Check to see if the user right clicks with a single button mouse.
		 * This is needed for Mac laptops.
		 * 
		 * @param ev
		 * @return
		 */
		private boolean isRightClickEvent(MouseEvent ev) {
			int mask = InputEvent.BUTTON1_MASK - 1;
			int mods = ev.getModifiers() & mask;
			if (mods == 0) {
				return false;
			} else {
				return true;
			}
		}

	}

	class SearchResultsTableModel extends AbstractTableModel {
		private final String[] TABLE_COLUMN_NAMES = new String[] {
				SGGCResourceBundle
						.getResourceString(ResourceName.KEY_DISPLAY_LISTMODEL_COLUMN_NAME),
				SGGCResourceBundle
						.getResourceString(ResourceName.KEY_DISPLAY_LISTMODEL_COLUMN_SIZE),
				SGGCResourceBundle
						.getResourceString(ResourceName.KEY_DISPLAY_LISTMODEL_COLUMN_TYPE),
				SGGCResourceBundle
						.getResourceString(ResourceName.KEY_DISPLAY_LISTMODEL_COLUMN_MODIFIED),
				SGGCResourceBundle
						.getResourceString(ResourceName.KEY_DISPLAY_LISTMODEL_COLUMN_ATTRIBUTES) };

		public int sortedColumnIndex = 2;
		public boolean sortAscending = true;

		private List<SearchResult> searchResults;

		private int editableRow = -1;

		public SearchResultsTableModel() {
			searchResults = new ArrayList<SearchResult>();
		}

		public SearchResultsTableModel(List<SearchResult> searchResults) {
			this.searchResults = searchResults;
		}

		public String getColumnName(int col) {
			return TABLE_COLUMN_NAMES[col].toString();
		}

		public int getColumnCount() {
			return TABLE_COLUMN_NAMES.length;
		}

		public int getRowCount() {
			return searchResults.size();
		}

		@SuppressWarnings("rawtypes")
		public Class getColumnClass(int c) {
			return getValueAt(0, c).getClass();
		}

		public Object getValueAt(int row, int column) {
			Object value = null;
			if (row >= searchResults.size()) {
				return null;
			}
			FileInfo file = (FileInfo) searchResults.get(row).getFile();
			switch (column) {
			case 0:
				// Name
				value = new IconData(
						(file.isDirectory() || file.isSoftLink() ? AppMain.icoFolder
								: AppMain.icoFile), DetailListModel
								.getOldFileName(file));

				break;
			case 1:
				// Size
				value = DetailListModel.getSize(file);
				break;
			case 2:
				// Type
				value = DetailListModel.getTypeName(file);
				break;
			case 3:
				// Modified
				value = (0 == row) ? "" : file.getDate() + ", "
						+ file.getTime();
				break;
			case 4:
				// Attributes
				value = (0 == row) ? "" : DetailListModel.getTypeShort(file)
						+ DetailListModel.getMode(file.getMode());
				break;
			case 5:
				// Description
				value = "";
				break;
			case 6:
				// Owner
				value = "";
				break;
			default:
				value = "";
				break;
			}
			return value;
		}

		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return false; // return (rowIndex>0)&&(columnIndex==0);
		}

		public void setSearchResultsList(List<SearchResult> results) {
			searchResults = results;
			fireTableDataChanged();
		}

		public List<SearchResult> getSearchResultsList() {
			return searchResults;
		}

		public void addSearchResults(List<SearchResult> results) {
			searchResults.addAll(results);
			fireTableRowsInserted(searchResults.size() - results.size() - 1,
					searchResults.size() - 1);
		}

		public void addSearchResult(SearchResult result) {
			searchResults.add(result);
			fireTableRowsInserted(searchResults.size() - 1, searchResults
					.size() - 1);
		}

		public int getRow(SearchResult searchResult) {
			if (searchResult == null || searchResults == null
					|| searchResults.size() == 0) {
				return -1;
			}
			return searchResults.indexOf(searchResult);
		}

		public SearchResult getSearchResultAtRow(int row) {
			if (searchResults == null || searchResults.size() <= row) {
				return null;
			}
			return searchResults.get(row);
		}

		public void clear() {
			int size = searchResults.size();
			searchResults.clear();
			if (size > 0)
				fireTableRowsDeleted(0, size - 1);
			System.gc();
		}

		public Icon getColumnIcon(int column) {
			if (column == sortedColumnIndex) {
				return sortAscending ? DetailListModel.COLUMN_UP
						: DetailListModel.COLUMN_DOWN;
			}
			return null;
		}

		public void columnAdded(TableColumnModelEvent arg0) {
			// TODO Auto-generated method stub

		}

		public void columnMarginChanged(ChangeEvent arg0) {
			// TODO Auto-generated method stub

		}

		public void columnMoved(TableColumnModelEvent arg0) {
			// TODO Auto-generated method stub

		}

		public void columnRemoved(TableColumnModelEvent arg0) {
			// TODO Auto-generated method stub

		}

		public void columnSelectionChanged(ListSelectionEvent arg0) {
			// TODO Auto-generated method stub

		}
	}
}
