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
import java.util.Vector;

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

import org.globus.gsi.gssapi.GlobusGSSCredentialImpl;
import org.jdesktop.swingx.JXBusyLabel;
import org.teragrid.portal.filebrowser.applet.AppMain;
import org.teragrid.portal.filebrowser.applet.ui.permissions.StripedTable;
import org.teragrid.portal.filebrowser.applet.util.LogManager;
import org.teragrid.portal.filebrowser.applet.util.ServletUtil;
import org.teragrid.portal.filebrowser.applet.util.SwingWorker;

import com.explodingpixels.macwidgets.IAppWidgetFactory;

import edu.utexas.tacc.wcs.filemanager.common.model.User;
import edu.utexas.tacc.wcs.filemanager.common.model.enumeration.UserQueryType;

/**
 * Dialog box to make interactive queries to the middeware service to find users.
 * 
 * @author dooley
 *
 */
@SuppressWarnings({"serial","unchecked","unused"})
public class DlgFindUser extends DlgEscape {
	
	protected static final String USERS = "Users";
	protected static final String COLLEAGUES = "Colleagues";
	protected static final String PROJECTS = "Projects";
	protected static final String EVERYONE = "Everyone";
	
	private  List<User> searchResults = new ArrayList<User>();
	private  List<User> colleagues = null;
	private  List<User> partners = null;
	
	private JPanel pnlSearchForm;
	private RoundField txtSearch;
	
	private JScrollPane spResults;
	protected StripedTable tblResults;
	private ResultsTableModel mResults;
	
	private JPanel pnlCategory;
	private JList lstCategory;
	
	private JPanel pnlButton;
	private JButton btnSelect;
	private JButton btnCancel;
	private JXBusyLabel lblBusy;
	
	private String lastQuery = "";
	private String selectedUsername;
	
	private DialogKeyListener keyListener;
	private Dimension preferredRowSize = new Dimension(125, 20);
	
	private SwingWorker worker;
	
	public DlgFindUser(Frame frame) {
	
		super(frame,"Select a user or group:",true);
		
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
		keyListener = new DialogKeyListener(this);
		
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
				System.out.println("Focus gained: " + e.getSource().getClass().getName());
				if (((RoundField)e.getSource()).getText().startsWith("Search for user")) {
					((RoundField)e.getSource()).setText("");
				} else {
					((RoundField)e.getSource()).selectAll();
				}
				((RoundField)e.getSource()).setForeground(Color.BLACK);
			}

			public void focusLost(FocusEvent e) {
				System.out.println("Focus lost: " + e.getSource().getClass().getName());
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

		mResults = new ResultsTableModel();
		tblResults = new StripedTable(mResults);
		tblResults.setTableHeader(null);
		
		// add double click support to the table
		tblResults.addMouseListener(new ResultsTableMouseAdaptor(this));
		tblResults.getSelectionModel().addListSelectionListener(new ResultsTableSelectionListener(this));
		tblResults.setDefaultRenderer(User.class, new ResultsTableCellRenderer());
		tblResults.getColumnModel().getColumn(0).setCellRenderer(new ResultsTableCellRenderer());
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
		
		lstCategory = new JList(new String[]{USERS,COLLEAGUES,PROJECTS});
		lstCategory.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		lstCategory.getSelectionModel().addListSelectionListener(new CategoryListSelectionListener(this));
		lstCategory.setCellRenderer(new CategoryListCellRenderer());
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
		btnCancel.addActionListener(new ButtonActionListener(this));
		pnlButton.add(btnCancel);
		
		btnSelect = new JButton("Select");
		btnSelect.addActionListener(new ButtonActionListener(this));
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
			public Object construct() {
				setBusyWaiting(true);
				// only supporting user's first and last name searches right now
				Vector<String> params = new Vector<String>();
		        params.addElement(((GlobusGSSCredentialImpl)AppMain.defaultCredential).getX509Credential().getIdentity());
		        params.addElement(UserQueryType.NAME.getValue());
		        params.addElement(searchString);
		        
		        LogManager.debug("Sending following query to service for logging:");
		        for(String s: (Vector<String>)params) {
		            LogManager.debug(ServletUtil.dewebify(s));
		        }
		        
		        String result = null;
				try {
					result = (String)ServletUtil.getClient().execute(
					        ServletUtil.FIND_USER,params);
				} catch (Exception e) {
					LogManager.error("Failed to retrieve users", e);
				}
		        
				if (result != null) {
					LogManager.debug(result);
					return ServletUtil.getXStream().fromXML(result);
				} else {
					return new ArrayList<User>();
				}
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
		worker = new SwingWorker() {
			@Override
			public Object construct() {
				setBusyWaiting(true);
				
				ClientResource clientResource = new ClientResource(
			            "http://localhost:8182/rest/test");
				Vector<String> params = new Vector<String>();
		        params.addElement(((GlobusGSSCredentialImpl)AppMain.defaultCredential).getX509Credential().getIdentity());
		        
		        
		        String result = null;
				try {
					result = (String)ServletUtil.getClient().execute(
					        ServletUtil.FIND_COLLEAGUES,params);
				} catch (Exception e) {
					LogManager.error("Failed to retrieve users", e);
				}
				  
				if (result != null) {
					LogManager.debug(result);
					return ServletUtil.getXStream().fromXML(result);
				} else {
					return new ArrayList<User>();
				}
			}

			@Override
			public void finished() {
				colleagues = (List<User>)get();
				mResults.removeAll();
				mResults.add(colleagues);
				setBusyWaiting(false);
			}
			
			public void interrupted() {
				setBusyWaiting(false);
			}
			
		};
		
		worker.start();
	}
	
	/**
	 * Queries the middleware for users sharing a project with the given user. 
	 * 
	 * @return list of users matching the search criteria
	 */
	private void queryForProjectPartners() {
		worker = new SwingWorker() {
			@Override
			public Object construct() {
				setBusyWaiting(true);
				
				Vector<String> params = new Vector<String>();
					
		        params.addElement(((GlobusGSSCredentialImpl)AppMain.defaultCredential).getX509Credential().getIdentity());
		       
		        String result = null;
				try {
					result = (String)ServletUtil.getClient().execute(
					        ServletUtil.FIND_PROJECT_PARTNERS,params);
				} catch (Exception e) {
					LogManager.error("Failed to retrieve users", e);
				}
				
				if (result != null) {
					LogManager.debug(result);
					return ServletUtil.getXStream().fromXML(result);
				} else {
					return new ArrayList<User>();
				}
			}

			@Override
			public void finished() {
				partners = (List<User>)get();
				mResults.removeAll();
				mResults.add(partners);
				setBusyWaiting(false);
			}
			
			public void interrupted() {
				setBusyWaiting(false);
			}
			
		};
		
		worker.start();
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
				System.out.println("User selected " + ((User)mResults.getValueAt(row,0)).getWholeName() + ", " + selectedUsername);
			}
		} else if (e.getSource() == btnCancel || e.getSource() instanceof KeyStroke) {
			selectedUsername = null;
			System.out.println("User pressed cancel");
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
		} else if (category.equals(PROJECTS)) {
			if (partners == null) {
				queryForProjectPartners();
			} else {
				mResults.removeAll();
				mResults.add(partners);
			}
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
	
	public static void main(String[] args) {
		
		DlgFindUser dlgFindUser = new DlgFindUser(null);
		
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

class DialogKeyListener implements KeyListener {
	DlgFindUser adaptee;
	public DialogKeyListener(DlgFindUser adaptee) {
		this.adaptee = adaptee;
	}
	
	public void keyPressed(KeyEvent arg0) {}
	public void keyReleased(KeyEvent arg0) {}
	public void keyTyped(KeyEvent event) {
		this.adaptee.DlgFindUser_keyEvent(event);
	}
}

class ResultsTableMouseAdaptor extends MouseAdapter {
	DlgFindUser adaptee;
	public ResultsTableMouseAdaptor(DlgFindUser adaptee) {
		this.adaptee = adaptee;
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		adaptee.DlgFindUser_tblResults_mouseClicked(e);
	}
}

class ResultsTableSelectionListener implements ListSelectionListener {
	DlgFindUser adaptee;
	public ResultsTableSelectionListener(DlgFindUser adaptee) {
		this.adaptee = adaptee;
	}
	
	public void valueChanged(ListSelectionEvent e) {
		adaptee.DlgFindUser_tblResults_valueChanged(e);
	}
}

class CategoryListSelectionListener implements ListSelectionListener {
	DlgFindUser adaptee;
	public CategoryListSelectionListener(DlgFindUser adaptee) {
		this.adaptee = adaptee;
	}
	
	public void valueChanged(ListSelectionEvent e) {
		adaptee.DlgFindUser_lstCategory_valueChanged(e);
	}
}

class ButtonActionListener implements ActionListener {
	DlgFindUser adaptee;
	public ButtonActionListener(DlgFindUser adaptee) {
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
class ResultsTableModel extends AbstractTableModel {
	
	List<User> users;
	
	public ResultsTableModel() {
		super();
		users = new ArrayList<User>();
	}
	
	public ResultsTableModel(List<User> users) {
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
class ResultsTableCellRenderer extends DefaultTableCellRenderer {
	
	public void setValue(Object value) {
		setIcon(AppMain.icoContact);
		setText(((User)value).toString());
	}
}

class CategoryListCellRenderer implements ListCellRenderer {

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
		if (value.equals(DlgFindUser.USERS)) {
			label.setToolTipText("<html>Clicking on this category displays the<br>" +
									   "results of your search for XSEDE<br>" +
									   "users using the search bar above.<html>");
			label.setIcon(AppMain.icoUser);
		} else {
			if (value.equals(DlgFindUser.COLLEAGUES)) {
				label.setToolTipText("<html>Clicking on this category displays all<br>" +
										   "XSEDE users at your institution as<br>" +
										   "well as those with whom you share a<br>" +
										   "project.<html>");
			} else if (value.equals(DlgFindUser.PROJECTS)) {
				label.setToolTipText("<html>Clicking on this category displays all<br>" +
										   "XSEDE users with whom you share<br>" +
										   "a project.<html>");
			} else if (value.equals(DlgFindUser.EVERYONE)) {
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