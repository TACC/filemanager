/**
 * 
 */
package org.teragrid.portal.filebrowser.applet.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import org.jdesktop.swingx.JXBusyLabel;
import org.teragrid.portal.filebrowser.applet.AppMain;
import org.teragrid.portal.filebrowser.applet.transfer.FTPSettings;
import org.teragrid.portal.filebrowser.applet.ui.permissions.StripedTable;
import org.teragrid.portal.filebrowser.applet.util.LogManager;
import org.teragrid.portal.filebrowser.applet.util.SwingWorker;

import com.explodingpixels.macwidgets.IAppWidgetFactory;

import edu.utexas.tacc.wcs.filemanager.common.model.User;
import edu.utexas.tacc.wcs.filemanager.common.model.enumeration.FileProtocolType;

/**
 * Dialog box to make interactive queries to the middeware service to find users.
 * 
 * @author dooley
 *
 */
@SuppressWarnings({"serial","unchecked","unused"})
public class DlgFindIrodsUser extends DlgEscape {
	
	private FTPThread tBrowse; // current active thread updating the file listing
	protected static final String USERS = "Users";
	protected static final String COLLEAGUES = "Colleagues";
	//protected static final String PROJECTS = "Projects";
	protected static final String EVERYONE = "Everyone";
	
	private  List<User> searchResults = new ArrayList<User>();
	private  List<User> colleagues = null;
	//private  List<User> partners = null;
	
	private JPanel pnlSearchForm;
	private RoundField txtSearch;
	
	private JScrollPane spResults;
	protected StripedTable tblResults;
	private IrodsResultsTableModel mResults;
	
	private JPanel pnlCategory;
	private JList lstCategory;
	
	private JPanel pnlButton;
	private JButton btnSelect;
	private JButton btnCancel;
	private JXBusyLabel lblBusy;
	
	private String lastQuery = "";
	private String selectedUsername;
	
	private IrodsDialogKeyListener keyListener;
	private Dimension preferredRowSize = new Dimension(125, 20);
	
	private SwingWorker worker;
	
	public DlgFindIrodsUser(Frame frame, FTPThread tBrowse) {
	
		super(frame,"Select a user:", true);
		
		this.tBrowse = tBrowse;
		
		initKeyListeners();
		
		initSearchPanel();
		
		initResultsPanel();
		
		initCategoryPanel();
		
		initButtonPanel();
		
		initLayout();
		
		locateDialog(frame);
		
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setResizable(false);
		setVisible(true);
		requestFocus();
		txtSearch.requestFocusInWindow();
		
	}
	
	private void initKeyListeners() {
		// register the context sensitive enter action key listener
		keyListener = new IrodsDialogKeyListener(this);
		
	}
	private void initSearchPanel() {
		txtSearch = new RoundField();
		txtSearch.setPreferredSize(preferredRowSize);
		txtSearch.setMaximumSize(preferredRowSize);
		txtSearch.addKeyListener(keyListener);
		txtSearch.setHorizontalAlignment(JTextField.LEFT);
//		txtSearch.setForeground(Color.LIGHT_GRAY);
		txtSearch.setText("Search for user");
		txtSearch.setFocusable(true);
		txtSearch.addMouseListener(new MouseAdapter() {

			/* (non-Javadoc)
			 * @see java.awt.event.MouseAdapter#mouseClicked(java.awt.event.MouseEvent)
			 */
			@Override
			public void mouseClicked(MouseEvent e) {
				if (!((RoundField)e.getSource()).hasFocus()) {
					txtSearch.selectAll();
				}
			}
			
		});
		
		txtSearch.addFocusListener(new FocusListener() {

			public void focusGained(FocusEvent e) {
				if (((RoundField)e.getSource()).getText().startsWith("Search for user")) {
					((RoundField)e.getSource()).setText("");
				} else {
					((RoundField)e.getSource()).selectAll();
				}
				((RoundField)e.getSource()).setForeground(Color.BLACK);
			}

			public void focusLost(FocusEvent e) {
				if (((RoundField)e.getSource()).getText().equals("")) {
					((RoundField)e.getSource()).removeAll();
					((RoundField)e.getSource()).setText("Search for user");
					((RoundField)e.getSource()).setForeground(Color.LIGHT_GRAY);
				}
			}
			
		});
		
		pnlSearchForm = new JPanel();
		pnlSearchForm.setBorder(BorderFactory.createEmptyBorder(3,3,3,3));
		pnlSearchForm.setLayout(new BoxLayout(pnlSearchForm, BoxLayout.LINE_AXIS));
		pnlSearchForm.add(Box.createHorizontalGlue());
		pnlSearchForm.add(txtSearch);
		pnlSearchForm.add(Box.createRigidArea(new Dimension(5,20)));
	}
	
	private void initResultsPanel() {

		mResults = new IrodsResultsTableModel();
		tblResults = new StripedTable(mResults);
		tblResults.setTableHeader(null);
		
		// add double click support to the table
		tblResults.addMouseListener(new IrodsResultsTableMouseAdaptor(this));
		tblResults.getSelectionModel().addListSelectionListener(new IrodsResultsTableSelectionListener(this));
		tblResults.setDefaultRenderer(User.class, new IrodsResultsTableCellRenderer());
		tblResults.getColumnModel().getColumn(0).setCellRenderer(new IrodsResultsTableCellRenderer());
		tblResults.addKeyListener(keyListener);
		tblResults.setAlignmentX(LEFT_ALIGNMENT);
		tblResults.setAlignmentY(TOP_ALIGNMENT);
		// decorate the scroll pane with the leopard laf
		spResults = new JScrollPane(tblResults);
		IAppWidgetFactory.makeIAppScrollPane(spResults);
	}
	
	private void initCategoryPanel() {
		pnlCategory = new JPanel();
		pnlCategory.setLayout(new BoxLayout(pnlCategory,BoxLayout.LINE_AXIS));
		pnlCategory.setBorder(BorderFactory.createMatteBorder(0,0,0,1,Color.BLACK));
		
		//lstCategory = new JList(new String[]{USERS,COLLEAGUES,PROJECTS});
		lstCategory = new JList(new String[]{USERS,COLLEAGUES});
		lstCategory.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		lstCategory.getSelectionModel().addListSelectionListener(new IrodsCategoryListSelectionListener(this));
		lstCategory.setCellRenderer(new IrodsCategoryListCellRenderer());
		lstCategory.setPreferredSize(new Dimension(175,250));
		lstCategory.setBorder(BorderFactory.createMatteBorder(0,0,0,1,Color.BLACK));
		lstCategory.setSelectedIndex(0);
		
		JScrollPane scrollPane = new JScrollPane(lstCategory);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
		scrollPane.setAlignmentX(LEFT_ALIGNMENT);
		scrollPane.setBorder(BorderFactory.createMatteBorder(0,0,0,1,Color.BLACK));
		IAppWidgetFactory.makeIAppScrollPane(scrollPane);
		
		pnlCategory.add(scrollPane);
	}

	private void initButtonPanel() {
		pnlButton = new JPanel();
		pnlButton.setLayout(new BoxLayout(pnlButton,BoxLayout.LINE_AXIS));
		pnlButton.add(Box.createRigidArea(new Dimension(15,15)));
		
		lblBusy = PnlBusyLoading.createSmallBusyLabel();
		setBusyWaiting(false);
		pnlButton.add(lblBusy);
		
		pnlButton.add(Box.createHorizontalGlue());
		
		btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new IrodsButtonActionListener(this));
		pnlButton.add(btnCancel);
		
		btnSelect = new JButton("Select");
		btnSelect.addActionListener(new IrodsButtonActionListener(this));
		pnlButton.add(btnSelect);	
	}
	
	private void initLayout() {
		JPanel pnlMain = new JPanel();
		pnlMain.setLayout(new BoxLayout(pnlMain,BoxLayout.Y_AXIS));
		pnlMain.add(Box.createRigidArea(new Dimension(5,5)));
		pnlMain.add(pnlSearchForm);

		JPanel pnlDisplay = new JPanel();
		pnlDisplay.setLayout(new BoxLayout(pnlDisplay,BoxLayout.LINE_AXIS));
		pnlDisplay.setBackground(Color.WHITE);
		pnlDisplay.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, Color.BLACK));
		pnlDisplay.add(pnlCategory);
		pnlDisplay.add(spResults);
		pnlMain.add(pnlDisplay);
		
		pnlCategory.setAlignmentX(LEFT_ALIGNMENT);
		pnlCategory.setAlignmentY(TOP_ALIGNMENT);
		spResults.setAlignmentX(LEFT_ALIGNMENT);
		spResults.setAlignmentY(TOP_ALIGNMENT);
		
		pnlMain.add(Box.createRigidArea(new Dimension(10,10)));
		pnlMain.add(pnlButton);
		pnlMain.add(Box.createRigidArea(new Dimension(10,10)));
		getContentPane().add(pnlMain);
		setPreferredSize(new Dimension(355,270));
		addKeyListener(keyListener);
		pack();
	}
	
    private void setBusyWaiting(final boolean start) {
    	lblBusy.setBusy(start);
    	pnlButton.revalidate();
    	lblBusy.setVisible(start);
    }
    
	/**
	 * Queries the middleware for users matching the given search string.
	 * 
	 * @param searchString a partial first or last name of another TG user
	 * @return list of users matching the search criteria
	 */
	private void queryForUser(final String searchString) {
		worker = new SwingWorker() {
			@Override
			public Object construct() 
			{
				setBusyWaiting(true);
				
				 
				List<User> users = new ArrayList<User>();
				
				try {
					users = tBrowse.findUsers(searchString);
				} catch (Exception e) {
					LogManager.error("Failed to retrieve users", e);
				}
		        
				return users;
			}


			@Override
			public void finished() {
				searchResults = (List<User>)get();
				mResults.removeAll();
				mResults.add(searchResults);
				setBusyWaiting(false);
			}
			
			public void interrupted() {
				mResults.removeAll();
				setBusyWaiting(false);
			}
			
		};
		
		worker.start();
	}
	
	/**
	 * Queries the middleware for users sharing a project or at the same 
	 * university as the given user. 
	 * 
	 * @return list of users matching the search criteria
	 */
	private void queryForColleagues() {
		queryForUser("");
	}
	
	/**
	 * Queries the middleware for users sharing a project with the given user. 
	 * 
	 * @return list of users matching the search criteria
	 */
	private void queryForProjectPartners() {
		queryForUser("");
	}

	public String getSelectedUsername() {
		return this.selectedUsername;
	}
	
	public void DlgFindUser_tblResults_mouseClicked(MouseEvent e) {
		if (e.getClickCount() == 2) {
			int row = tblResults.rowAtPoint(e.getPoint());
			if (row != -1) {
				selectedUsername = ((User)mResults.getValueAt(row,0)).getUsername();
				LogManager.debug("User selected " + ((User)mResults.getValueAt(row,0)).getWholeName() + ", " + selectedUsername);
				setVisible(false);
			}
		}
	}

	public void DlgFindUser_actionPeformed_ActionEvent(ActionEvent e) {
		if (e.getSource() == btnSelect) {
			int row = tblResults.getSelectedRow();
			if (row > -1) {
				selectedUsername = ((User)tblResults.getValueAt(row, 0)).getUsername();
			}
		} else if (e.getSource() == btnCancel || e.getSource() instanceof KeyStroke) {
			selectedUsername = null;
		} 
		
		setVisible(false);
	}

	public void DlgFindUser_lstCategory_valueChanged(ListSelectionEvent e) {
		String category = (String)lstCategory.getSelectedValue();
		if (category.equals(USERS)) {
			mResults.removeAll();
			mResults.add(searchResults);
		} else if (category.equals(COLLEAGUES)) {
			if (colleagues == null) {
				queryForColleagues();
			} else {
				mResults.removeAll();
				mResults.add(colleagues);
			}
//		} else if (category.equals(PROJECTS)) {
//			if (partners == null) {
//				queryForProjectPartners();
//			} else {
//				mResults.removeAll();
//				mResults.add(partners);
//			}
		} else if (category.equals(EVERYONE)) {
			mResults.removeAll();
			
			User everyUser = new User();
			everyUser.setFirstName("XSEDE");
			everyUser.setLastName("Users");
			everyUser.setUsername("GROUP_EVERYONE");
			mResults.add(everyUser);
			
			User guestUser = new User();
			guestUser.setFirstName("Public");
			guestUser.setLastName("Users");
			guestUser.setUsername("guest");
			mResults.add(guestUser);
			
		}
	}

	public void DlgFindUser_tblResults_valueChanged(ListSelectionEvent e) {
		// button is only enabled when a selection is made
		btnSelect.setEnabled(tblResults.getSelectedRow() > -1);
	}

	public void DlgFindUser_keyEvent(KeyEvent event) {
		if (event.getKeyChar() == KeyEvent.VK_ENTER) {
			if (event.getSource() == txtSearch) {
				lstCategory.setSelectedIndex(0);
				if (!lastQuery.equals(txtSearch.getText())) {
					queryForUser(txtSearch.getText().trim());
					lastQuery = txtSearch.getText();
				}
			} else {
				int row = tblResults.getSelectedRow();
				if (row != -1) {
					selectedUsername = ((User)mResults.getValueAt(row,0)).getUsername();
					LogManager.debug("User selected " + ((User)mResults.getValueAt(row,0)).getWholeName() + ", " + selectedUsername);
					setVisible(false);
				}
			}
		} 
	}
	
	public static void main(String[] args) 
	{
		FTPSettings server = new FTPSettings("data.iplantcollaborative.org",FileProtocolType.IRODS.getDefaultPort(),FileProtocolType.IRODS);
		FTPThread tBrowse = new FTPThread(server,true,null);
		DlgFindIrodsUser dlgFindUser = new DlgFindIrodsUser(null, tBrowse);
		
		if (dlgFindUser != null) {
			String uname = dlgFindUser.getSelectedUsername();
			if (uname == null || uname.equals("")) {
				System.out.println("No user selected");
			} else {
				System.out.println("You selected user " + uname);
			}
		} else {
			System.out.println("No user selected");
		}
	}
}

class IrodsDialogKeyListener implements KeyListener {
	DlgFindIrodsUser adaptee;
	public IrodsDialogKeyListener(DlgFindIrodsUser adaptee) {
		this.adaptee = adaptee;
	}
	
	public void keyPressed(KeyEvent arg0) {}
	public void keyReleased(KeyEvent arg0) {}
	public void keyTyped(KeyEvent event) {
		this.adaptee.DlgFindUser_keyEvent(event);
	}
}

class IrodsResultsTableMouseAdaptor extends MouseAdapter {
	DlgFindIrodsUser adaptee;
	public IrodsResultsTableMouseAdaptor(DlgFindIrodsUser adaptee) {
		this.adaptee = adaptee;
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		adaptee.DlgFindUser_tblResults_mouseClicked(e);
	}
}

class IrodsResultsTableSelectionListener implements ListSelectionListener {
	DlgFindIrodsUser adaptee;
	public IrodsResultsTableSelectionListener(DlgFindIrodsUser adaptee) {
		this.adaptee = adaptee;
	}
	
	public void valueChanged(ListSelectionEvent e) {
		adaptee.DlgFindUser_tblResults_valueChanged(e);
	}
}

class IrodsCategoryListSelectionListener implements ListSelectionListener {
	DlgFindIrodsUser adaptee;
	public IrodsCategoryListSelectionListener(DlgFindIrodsUser adaptee) {
		this.adaptee = adaptee;
	}
	
	public void valueChanged(ListSelectionEvent e) {
		adaptee.DlgFindUser_lstCategory_valueChanged(e);
	}
}

class IrodsButtonActionListener implements ActionListener {
	DlgFindIrodsUser adaptee;
	public IrodsButtonActionListener(DlgFindIrodsUser adaptee) {
		this.adaptee = adaptee;
	}
	
	public void actionPerformed(ActionEvent e) {
		adaptee.DlgFindUser_actionPeformed_ActionEvent(e);
	}
}

/**
 * Table model for the results table. Contains a vector of
 * user objects.
 * 
 * @author dooley
 *
 */
@SuppressWarnings("serial")
class IrodsResultsTableModel extends AbstractTableModel {
	
	List<User> users;
	
	public IrodsResultsTableModel() {
		super();
		users = new ArrayList<User>();
	}
	
	public IrodsResultsTableModel(List<User> users) {
		super();
		this.users = users;
		fireTableDataChanged();
	}
	
	public void removeAll() {
		this.users.clear();
		fireTableDataChanged();
	}
	
	public void add(List<User> users) {
		this.users.addAll(users);
		fireTableDataChanged();
	}
	
	public void add(User user) {
		this.users.add(user);
		fireTableDataChanged();
	}
	
	public int getColumnCount() {
		return 1;
	}

	public int getRowCount() {
		return users.size();
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		if (columnIndex > 1) {
			return null;
		}
		
		return users.get(rowIndex);
	}
}

@SuppressWarnings("serial")
class IrodsResultsTableCellRenderer extends DefaultTableCellRenderer {
	
	public void setValue(Object value) {
		setIcon(AppMain.icoContact);
		setText(((User)value).toString());
	}
}

class IrodsCategoryListCellRenderer implements ListCellRenderer {

	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {
		JLabel label = new JLabel(value.toString());
		label.setOpaque(true);
		if (isSelected) {
			label.setForeground(Color.BLACK);
			label.setBackground(Color.LIGHT_GRAY);
		} else {
			label.setForeground(list.getForeground());
			label.setBackground(Color.WHITE);
		}
		if (value.equals(DlgFindIrodsUser.USERS)) {
			label.setToolTipText("<html>Clicking on this category displays the<br>" +
									   "results of your search for XSEDE<br>" +
									   "users using the search bar above.<html>");
			label.setIcon(AppMain.icoUser);
		} else {
			if (value.equals(DlgFindIrodsUser.COLLEAGUES)) {
				label.setToolTipText("<html>Clicking on this category displays all<br>" +
										   "XSEDE users at your institution as<br>" +
										   "well as those with whom you share a<br>" +
										   "project.<html>");
//			} else if (value.equals(DlgFindIrodsUser.PROJECTS)) {
//				label.setToolTipText("<html>Clicking on this category displays all<br>" +
//										   "XSEDE users with whom you share<br>" +
//										   "a project.<html>");
			} else if (value.equals(DlgFindIrodsUser.EVERYONE)) {
				label.setToolTipText("<html>Clicking on this category displays the<br>" +
										   "groups 'XSEDE Users' and 'Public <br>" +
										   "Users'. These allow you to share your <br>" +
										   "files with all authenticated users and <br>" +
										   "unauthenticated users respectively.<html>");
			}
			label.setIcon(AppMain.icoGroup);
		}
		
		return label;
	}
}