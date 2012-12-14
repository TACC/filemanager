package org.teragrid.portal.filebrowser.applet.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DragSource;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.activation.ActivationDataFlavor;
import javax.activation.DataHandler;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DropMode;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.ListSelectionModel;
import javax.swing.TransferHandler;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;

import org.apache.commons.collections.ListUtils;
import org.teragrid.portal.filebrowser.applet.AppMain;
import org.teragrid.portal.filebrowser.applet.ConfigOperation;
import org.teragrid.portal.filebrowser.applet.transfer.FTPPort;
import org.teragrid.portal.filebrowser.applet.transfer.FTPSettings;
import org.teragrid.portal.filebrowser.applet.transfer.FTPType;
import org.teragrid.portal.filebrowser.applet.ui.permissions.StripedTable;
import org.teragrid.portal.filebrowser.applet.ui.table.Reorderable;
import org.teragrid.portal.filebrowser.server.servlet.model.resource.DBUtil;

import com.explodingpixels.macwidgets.IAppWidgetFactory;

@SuppressWarnings("serial")
public class PnlSites extends JPanel {
	
    private StripedTable tblSites;
    
    private JScrollPane spSites = new JScrollPane();
    
    private JPanel pnlButtons = new JPanel();
    private PnlBusyLoading pnlBusy = new PnlBusyLoading();
    
    private SiteTableModel tmSites;
    
    private JPopupMenu rightClickPopup = new JPopupMenu();
    
    private JMenuItem mnuOpen;
    private JMenuItem mnuEdit;
    private JMenuItem mnuOk;
    private JMenuItem mnuDelete;
    
    private JButton btnAdd;
    private JButton btnDelete;
    private JButton btnRefresh;
    
    private PnlBrowse parent = null;
    
    public PnlSites(PnlBrowse parent, List<FTPSettings> sites) {
    	this.parent = parent;
    	
    	pbInit(sites);
    }
    
    public PnlSites(List<FTPSettings> sites) {
    	pbInit(sites);
    }
    
    private void pbInit(List<FTPSettings> sites) {
        
        // initialize the components
        initSitesTable(sites);
        
        initPopupMenu();
        
        initButtonPanel();
        
        // layout the panel
        spSites.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        spSites.getViewport().add(tblSites);
        spSites.setWheelScrollingEnabled(true);
        spSites.addMouseListener(new SitesTableMouseAdapter());
        spSites.getViewport().setBackground(getBackground());
        IAppWidgetFactory.makeIAppScrollPane(spSites);
        
        setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
        add(spSites,BorderLayout.CENTER);
        add(pnlButtons,BorderLayout.PAGE_END);
        
    }
    
    private void initSitesTable(List<FTPSettings> sites) {
        
        tmSites = new SiteTableModel(sites);
        
        tblSites = new StripedTable(tmSites);
        tblSites.setDragEnabled(true);
        tblSites.setDropMode(DropMode.INSERT_ROWS);
        tblSites.setTransferHandler(new SitesTableRowTransferHandler(tblSites));
        tblSites.setRowHeight(35);
        tblSites.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblSites.setTableHeader(null);
        tblSites.addMouseListener(new SitesTableMouseAdapter());
        //tblSites.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        tblSites.setDefaultRenderer(Object.class, new TableCellRenderer() {

			public Component getTableCellRendererComponent(JTable table,
					Object value, boolean isSelected, boolean hasFocus,
					int row, int column) {
				FTPSettings site = (FTPSettings) value;
				String formattedHostname = site.host;
				
				ImageIcon ico = null;
				if (site.type == FTPType.FILE) {
					ico = AppMain.icoResourceLocal;
					formattedHostname = "127.0.0.1";
				} else if (site.type == FTPType.S3) {
					ico = AppMain.icoResourceAmazon;
				} else if (site.type == FTPType.XSHARE) {
					ico = AppMain.icoResourceTeraGridShare;
					try {
						formattedHostname = new URL(site.host).getHost();
					} catch (MalformedURLException e) {
						formattedHostname = site.host;
					}
				} else if (site.hostType.equals(DBUtil.VIZ)) {
					ico = AppMain.icoResourceViz;
				} else if (site.hostType.equals(DBUtil.ARCHIVE)) {
					ico = AppMain.icoResourceArchive;
				} else {
					ico = AppMain.icoResourceCompute;
				}
				
				String decoration = site.isAvailable() ? "none" : "italic";
				String titleColor = isSelected? "white" : site.isAvailable() ? "black" : "gray";
				String urlColor = isSelected? "white" : site.isAvailable() ? "blue" : "gray";
				JLabel lblSite = new JLabel("<html><body>" +
						"<table cellpadding=\"0\" cellspacing=\"0\">" +
							"<tr><td>" +
								"<span style=\"font-weight:bold;font-style:" + decoration + ";color:"+ titleColor + "\">"+site.name+"</span>" +
							"</td></tr>" +
							"<tr><td>" +
								"<span style=\"font-size:x-small;color:" + urlColor + ";text-style:" + decoration + "\">"+FTPType.FTP_PROTOCOL[site.type].toLowerCase().trim() + "://"+formattedHostname+"</span>" +
							"</td></tr></body></html>");
				
				lblSite.setIcon(ico);
				lblSite.setOpaque(true);
				lblSite.setBorder(BorderFactory.createCompoundBorder(
						BorderFactory.createMatteBorder(0,0,0,0, Color.decode("#D7D7D7")), 
						BorderFactory.createEmptyBorder(5,15,5,5)));
				
				return lblSite;
			}
        	
        });
        tblSites.setMinimumSize(new Dimension(100,100));
        tblSites.setMinimumSize(new Dimension(200,300));
        tblSites.setMaximumSize(new Dimension(Integer.MAX_VALUE,Integer.MAX_VALUE));
        
        
    }
    
    public void update(List<FTPSettings> sites) {
    	
    	stopWaiting();
    	
    	tmSites = new SiteTableModel(sites);
    	
    	tblSites.setModel(tmSites);
    	
    	tblSites.revalidate();
    }
    
    private JPopupMenu initPopupMenu() {
        
        rightClickPopup = new JPopupMenu();
        
        mnuOpen = new JMenuItem(new PopupAction("Open","Open",KeyEvent.VK_O));
        mnuEdit = new JMenuItem(new PopupAction("Edit","Edit",KeyEvent.VK_E));
        mnuOk = new JMenuItem(new PopupAction("Add","Add",KeyEvent.VK_N));
        mnuDelete = new JMenuItem(new PopupAction("Delete","Delete",KeyEvent.VK_D));
        
        rightClickPopup.add(mnuOpen);
        rightClickPopup.add(mnuEdit);
        rightClickPopup.addSeparator();
        rightClickPopup.add(mnuOk);
        rightClickPopup.add(mnuDelete);
        
        return rightClickPopup;
    }
    
    private void initButtonPanel() {
        // buttons like menu items have shortcuts
        btnAdd = new JButton(new PopupAction("",AppMain.icoAdd,"Add",KeyEvent.VK_N));
        btnAdd.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnDelete = new JButton(new PopupAction("",AppMain.icoRemove,"Delete",KeyEvent.VK_D));
        btnDelete.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnRefresh = new JButton(new PopupAction("",AppMain.icoRefresh,"Refresh",KeyEvent.VK_R));
        btnAdd.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // layout with apple list appearance
        pnlButtons.setLayout(new BoxLayout(pnlButtons,BoxLayout.X_AXIS));
        pnlButtons.add(btnAdd,BorderLayout.LINE_START);
        pnlButtons.add(btnDelete);
        pnlButtons.add(btnRefresh);
        pnlButtons.add(Box.createHorizontalGlue());
    }
    
    private FTPSettings getSelectedSite() {
    	int row = tblSites.getSelectedRow();
    	if (row != -1) {
    		return (FTPSettings)tblSites.getModel().getValueAt(row, 0);
    	} else {
    		return null;
    	}
    }
    
    public void mnuPopup_actionPerformed(ActionEvent e) {
        if (e.getActionCommand().compareTo("Open")==0) {
            FTPSettings site = getSelectedSite();
            if (site != null) {
            	parent.setServer(site);
                // connect to resource and hide this panel
            }
        } else if (e.getActionCommand().compareTo("Edit")==0) {
            DlgEditResource dialog = new DlgEditResource(this,getSelectedSite());
            dialog.setVisible(true);
            if (dialog != null && dialog.getResource() != null) { 
            	// update resource
            	int selectedRow = tblSites.getSelectedRow();
            	tmSites = new SiteTableModel(ConfigOperation.getInstance().getSites());
            	tblSites.setModel(tmSites);
            	tblSites.setRowSelectionInterval(selectedRow, selectedRow);
            	scrollToVisible(tblSites, selectedRow, 0);
            }
        } else if (btnAdd == e.getSource() || e.getActionCommand().compareTo("Add")==0) {
        	DlgEditResource dialog = new DlgEditResource(this);
        	dialog.setVisible(true);
        	if (dialog != null && dialog.getResource() != null) {
        		// add the new resource
        		tmSites = new SiteTableModel(ConfigOperation.getInstance().getSites());
        		tblSites.setModel(tmSites);
        		int newRowIndex = tblSites.getRowCount()-1;
        		tblSites.setRowSelectionInterval(newRowIndex, newRowIndex);
        		scrollToVisible(tblSites, newRowIndex, 0);
        	}
        } else if (btnDelete == e.getSource() || e.getActionCommand().compareTo("Delete")==0) {
        	FTPSettings site = getSelectedSite();
        	//int selectedRow = tblSites.getSelectedRow();
        	// delete the selected resource
        	if (site.userDefined) {
        		ConfigOperation config = ConfigOperation.getInstance();
        		config.deleteSite(site);
        		tmSites = new SiteTableModel(config.getSites());
        		tblSites.setModel(tmSites);
        	} else {
        		AppMain.Error(this, "Cannot remove dynamically created accounts.");
        	}
        } else if (btnRefresh == e.getSource() || e.getActionCommand().compareTo("Refresh")==0) {
        	
        	((AppMain)AppMain.getApplet()).refreshResources();
        }
        
     }
    
    public void startWaiting() {
    	pnlButtons.setVisible(false);
    	spSites.setVisible(false);
    	pnlBusy.setSize(this.getSize());
    	pnlBusy.setBusy(true);
    	add(pnlBusy);
    }
    
    public void stopWaiting() {
    	remove(pnlBusy);
    	pnlButtons.setVisible(true);
    	spSites.setVisible(true);
    }
    
 // Assumes table is contained in a JScrollPane. Scrolls the
    // cell (rowIndex, vColIndex) so that it is visible within the viewport.
    public void scrollToVisible(JTable table, int rowIndex, int vColIndex) {
        if (!(table.getParent() instanceof JViewport)) {
            return;
        }
        JViewport viewport = (JViewport)table.getParent();
    
        // This rectangle is relative to the table where the
        // northwest corner of cell (0,0) is always (0,0).
        Rectangle rect = table.getCellRect(rowIndex, vColIndex, true);
    
        // The location of the viewport relative to the table
        Point pt = viewport.getViewPosition();
    
        // Translate the cell location so that it is relative
        // to the view, assuming the northwest corner of the
        // view is (0,0)
        rect.setLocation(rect.x-pt.x, rect.y-pt.y);
    
        // Scroll the area into view
        viewport.scrollRectToVisible(rect);
    }
    
    private void showPopupMenu(Point p) {
        
        FTPSettings site = getSelectedSite();
        
        // disable bookmark specific actions if no
        // bookmark is selected.
        boolean isSelected = (site != null);
        mnuOpen.setVisible(isSelected);
        mnuEdit.setVisible(isSelected);
        
        rightClickPopup.show(tblSites,p.x,p.y);
    }
    
    /* Action for button and menuitem events.
    * 
    * @author Rion Dooley < dooley [at] tacc [dot] utexas [dot] edu >
    *
    */
   class PopupAction extends AbstractAction {
       
       public PopupAction(String text,
               String desc, Integer mnemonic) {
           super(text);
           putValue(SHORT_DESCRIPTION, desc);
           putValue(MNEMONIC_KEY, mnemonic);
       }

       public PopupAction(String text, ImageIcon img,
               String desc, Integer mnemonic) {
           super(text, img);
           putValue(SHORT_DESCRIPTION, desc);
           putValue(MNEMONIC_KEY, mnemonic);
       }
       
       public void actionPerformed(ActionEvent e) {
           mnuPopup_actionPerformed(e);
       }
   }
    
	class SitesTableMouseAdapter extends MouseAdapter {
		
		public void mouseClicked(MouseEvent e) {
			 super.mouseClicked(e);
			 
			 if (isRightClickEvent(e)) {
	             int clickedRow = tblSites.rowAtPoint(e.getPoint());
	             int clickedCol = tblSites.columnAtPoint(e.getPoint());
	             // could be clicking on empty table area
	             if (clickedRow >= 0 ) {
	            	 tblSites.setRowSelectionInterval(clickedRow, clickedRow);
	            	 tblSites.setColumnSelectionInterval(clickedCol, clickedCol);
	             } else {
	            	 tblSites.clearSelection();
	             }
	             
	             showPopupMenu(e.getPoint());
	         
			 } else if(e.getClickCount() == 2) {
				 FTPSettings site = getSelectedSite();
				 if (site != null) {
					 if (site.isAvailable()) {
						 parent.setServer(site);
					 } else {
						 AppMain.Error(parent, site.name + " is currently down for maintenance.\n" +
						 		"This site will be re-enabled automatically once maintenance\n" +
						 		" is completed.");
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

	 public static void main(String[] args) {
        JFrame frame = new JFrame("Sites Demo");
        ArrayList<FTPSettings> sites = loadData();
        PnlSites pnlSites = new PnlSites(sites);
        frame.setContentPane(pnlSites);
        frame.pack();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
       
    private static ArrayList<FTPSettings> loadData() {
       ArrayList<FTPSettings> sites = new ArrayList<FTPSettings>();
       FTPSettings site = new FTPSettings("Local",FTPPort.PORT_NONE,FTPType.FILE);
       site.host = "localhost";
       sites.add(site);
       site = new FTPSettings("Ranger",FTPPort.PORT_GRIDFTP,FTPType.GRIDFTP);
       site.hostType = DBUtil.HPC;
       site.host = "ranger.tacc.utexas.edu";
       sites.add(site);
       site = new FTPSettings("Spur",FTPPort.PORT_GRIDFTP,FTPType.GRIDFTP);
       site.hostType = DBUtil.VIZ;
       site.host = "spur.tacc.utexas.edu";
       sites.add(site);
       site = new FTPSettings("Ranch",FTPPort.PORT_GRIDFTP, FTPType.GRIDFTP);
       site.hostType = DBUtil.ARCHIVE;
       site.host = "ranch.tacc.utexas.edu";
       sites.add(site);
       site = new FTPSettings("$SHARE",FTPPort.PORT_TGSHARE, FTPType.XSHARE);
       site.host = "goodnight.corral.tacc.utexas.edu";
       sites.add(site);
       site = new FTPSettings("Amazon S3",FTPPort.PORT_S3,FTPType.S3);
       site.host = "s3.amazonws.com";
       sites.add(site);
       return sites;
   }
}

@SuppressWarnings("serial")
class SiteTableModel extends AbstractTableModel implements Reorderable{
	List<FTPSettings> sites;
	
	public SiteTableModel(List<FTPSettings> sites) {
		this.sites = sites;
	}
	
	public void addSites(List<FTPSettings> sites) {
		//System.out.println("Adding " + sites.size() + " sites in model");
		this.sites.addAll(sites);
		fireTableDataChanged();
	}
	
	public void addSite(FTPSettings site) {
		
		sites.add(site);
		fireTableDataChanged();
//		fireTableRowsInserted(sites.size()-1, sites.size()-1);
	}
	
	public void removeSite(FTPSettings site) {
		sites.remove(site);
		fireTableDataChanged();
	}
	
	public void clearSites() {
		int size = sites.size();
		sites.clear();
		fireTableRowsDeleted(0, size == 0? 0:size-1);
	}
	
	public void reorder(int fromIndex, int toIndex) {
		FTPSettings site = sites.get(fromIndex);
		List<FTPSettings> reOrderedSites = new ArrayList<FTPSettings>();
		
		for(int i=0; i<sites.size(); i++) {
			if (i != fromIndex) {
				if (i == toIndex) {
					reOrderedSites.add(site);
				}
				reOrderedSites.add(sites.get(i));
			}
		}
		sites.clear();
		sites = reOrderedSites;
		ConfigOperation config = ConfigOperation.getInstance();
		config.setSites(sites);
		fireTableDataChanged();
	}
	
	public int getColumnCount() {
		return 1;
	}

	public int getRowCount() {
		return sites.size();
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		return sites.get(rowIndex);
	}
}

class SitesTableRowTransferHandler extends TransferHandler {
   private final DataFlavor localObjectFlavor = new DataFlavor(Integer.class, "Integer Row Index");
   private JTable           table             = null;

   public SitesTableRowTransferHandler(JTable table) {
      this.table = table;
   }

   @Override
   protected Transferable createTransferable(JComponent c) {
      assert (c == table);
      return new DataHandler(new Integer(table.getSelectedRow()), localObjectFlavor.getMimeType());
   }

   @Override
   public boolean canImport(TransferHandler.TransferSupport info) {
      boolean b = info.getComponent() == table && info.isDrop() && info.isDataFlavorSupported(localObjectFlavor);
      table.setCursor(b ? DragSource.DefaultMoveDrop : DragSource.DefaultMoveNoDrop);
      return b;
   }

   @Override
   public int getSourceActions(JComponent c) {
      return TransferHandler.COPY_OR_MOVE;
   }

   @Override
   public boolean importData(TransferHandler.TransferSupport info) {
      JTable target = (JTable) info.getComponent();
      JTable.DropLocation dl = (JTable.DropLocation) info.getDropLocation();
      int index = dl.getRow();
      int max = table.getModel().getRowCount();
      if (index < 0 || index > max)
         index = max;
      target.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
      try {
         Integer rowFrom = (Integer) info.getTransferable().getTransferData(localObjectFlavor);
         if (rowFrom != -1 && rowFrom != index) {
            ((Reorderable)table.getModel()).reorder(rowFrom, index);
            if (index > rowFrom)
               index--;
            target.getSelectionModel().addSelectionInterval(index, index);
            return true;
         }
      } catch (Exception e) {
         e.printStackTrace();
      }
      return false;
   }

   @Override
   protected void exportDone(JComponent c, Transferable t, int act) {
      if (act == TransferHandler.MOVE) {
         table.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
      }
   }

}