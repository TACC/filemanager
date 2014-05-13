/* 
 * Created on Aug 16, 2008
 * 
 * Developed by: Rion Dooley - dooley [at] tacc [dot] utexas [dot] edu
 * 				 TACC, Texas Advanced Computing Center
 * 
 * https://www.tacc.utexas.edu/
 */

package edu.utexas.tacc.wcs.filemanager.client.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;

import org.apache.commons.io.FilenameUtils;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.pub.domain.AvuData;
import org.irods.jargon.core.query.MetaDataAndDomainData;
import org.irods.jargon.core.query.MetaDataAndDomainData.MetadataDomain;

import com.explodingpixels.macwidgets.IAppWidgetFactory;

import edu.utexas.tacc.wcs.filemanager.client.AppMain;
import edu.utexas.tacc.wcs.filemanager.client.transfer.Irods;
import edu.utexas.tacc.wcs.filemanager.client.ui.permissions.StripedTable;
import edu.utexas.tacc.wcs.filemanager.client.util.LogManager;

/**
 * Panel to hold IRODS metadata on a per-instance basis.
 * 
 * @author Rion Dooley < dooley [at] tacc [dot] utexas [dot] edu >
 *
 */
@SuppressWarnings({"serial","unused","unchecked"})
public class PnlIrodsMetadata extends JPanel implements ClipboardOwner {
    private static List<String> QUERY_RESULT_COLUMNS = Arrays.asList("Key","Value");
    private static DataFlavor METADATA_FLAVOR = new DataFlavor(MetaDataAndDomainData.class, DataFlavor.javaSerializedObjectMimeType);
    
    private String absolutePath;
    
    private StripedTable tblMetadata;
    
    private JScrollPane spMetadata = new JScrollPane();
    
    private JPanel pnlButtons = new JPanel();
    
    private MetadataTableModel mdlMetadata;
    
    private Clipboard systemClipboard = Toolkit.getDefaultToolkit().getSystemClipboard(); 
    
    private JPopupMenu rightClickPopup = new JPopupMenu();
    
    private JMenuItem mnuCopy; 
    private JMenuItem mnuPaste;
    private JMenuItem mnuOpen;
    private JMenuItem mnuInfo;
    private JMenuItem mnuEdit;
    private JMenuItem mnuAdd;
    private JMenuItem mnuDelete;
    
    private JButton btnAdd;
    private JButton btnDelete;
    private JButton btnClose;
    
    private boolean editingBookmark = false;
    
    private PnlBrowse parent = null;
    
    public PnlIrodsMetadata(PnlBrowse pnlBrowse, List<MetaDataAndDomainData> metadata, String absolutePath) {
        this(metadata, absolutePath);
        
        this.parent = pnlBrowse;
        
    }
    
    public PnlIrodsMetadata(List<MetaDataAndDomainData> metadata, String absolutePath) {
        super();
        
        pbInit(metadata, absolutePath);
        
    }

    private void pbInit(List<MetaDataAndDomainData> metadata, String absolutePath) {
        
    	this.absolutePath = absolutePath;
    	
        // initialize the components
        initMetadataTable(metadata);
        
        initPopupMenu();
        
        initButtonPanel();
        
        // layout the panel
        spMetadata.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        spMetadata.getViewport().add(tblMetadata);
        spMetadata.addMouseListener(new MetadataTableMouseAdapter(this));
        spMetadata.getViewport().setBackground(getBackground());
        IAppWidgetFactory.makeIAppScrollPane(spMetadata);
        
        setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
        add(spMetadata,BorderLayout.CENTER);
        add(pnlButtons,BorderLayout.PAGE_END);
        
    }
    
    private void initMetadataTable(List<MetaDataAndDomainData> bookmarks) {
        
        mdlMetadata = new MetadataTableModel(bookmarks);
        //mdlMetadata.addTableModelListener(new BookmarkTableModelListener());
        
        tblMetadata = new StripedTable(mdlMetadata) {
            public boolean editCellAt(int row, int col) {
                editingBookmark = true;
                mdlMetadata.setEditableRow(row);
                return super.editCellAt(row, col);
            }
        };
        
        tblMetadata.addMouseListener(new MetadataTableMouseAdapter(this));
//        tblBookmarks.setBackground(getBackground());
        
        //tblMetadata.setMinimumSize(new Dimension(100,100));
        tblMetadata.setMinimumSize(new Dimension(200,300));
        tblMetadata.setMaximumSize(new Dimension(Integer.MAX_VALUE,Integer.MAX_VALUE));
        
        
    }
    
    private JPopupMenu initPopupMenu() {
        
        rightClickPopup = new JPopupMenu();
        
        mnuCopy = new JMenuItem(new PopupAction("Copy","Copy",KeyEvent.VK_C));
        mnuPaste = new JMenuItem(new PopupAction("Paste","Paste",KeyEvent.VK_P));
//        mnuOpen = new JMenuItem(new PopupAction("Open","Open",KeyEvent.VK_O));
//        mnuInfo = new JMenuItem(new PopupAction("Info","Info",KeyEvent.VK_I));
        mnuEdit = new JMenuItem(new PopupAction("Edit","Edit",KeyEvent.VK_E));
        mnuAdd = new JMenuItem(new PopupAction("Add","Add",KeyEvent.VK_N));
        mnuDelete = new JMenuItem(new PopupAction("Delete","Delete",KeyEvent.VK_D));
        
        rightClickPopup.add(mnuAdd);
        rightClickPopup.add(mnuDelete);
        rightClickPopup.addSeparator();
        //rightClickPopup.add(mnuOpen);
        rightClickPopup.add(mnuCopy);
        rightClickPopup.add(mnuPaste);
        rightClickPopup.add(mnuEdit);
        
//        rightClickPopup.add(mnuInfo);
        
        
        return rightClickPopup;
    }
    
    private void initButtonPanel() {
        // buttons like menu items have shortcuts
        btnAdd = new JButton(new PopupAction("",AppMain.icoAdd,"Add",KeyEvent.VK_N));
        btnAdd.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnDelete = new JButton(new PopupAction("",AppMain.icoRemove,"Delete",KeyEvent.VK_D));
        btnDelete.setAlignmentX(Component.LEFT_ALIGNMENT);
        if (mdlMetadata.getRowCount() == 0) {
   			btnDelete.setEnabled(false);
   		}
        
        btnClose = new JButton("Close");
        btnClose.setAlignmentX(Component.RIGHT_ALIGNMENT);
        btnClose.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e)
			{
				parent.showIrodsMetadataPanel(false);
			}
        	
        });
        // layout with apple list appearance
        pnlButtons.setLayout(new BoxLayout(pnlButtons,BoxLayout.X_AXIS));
        pnlButtons.add(btnAdd,BorderLayout.LINE_START);
        pnlButtons.add(btnDelete);
        pnlButtons.add(Box.createHorizontalGlue());
        String filename = FilenameUtils.getName(absolutePath);
        if (filename.length() > 35) {
        	filename = filename.substring(0,15) + "...." + filename.substring(filename.length()-15,filename.length()-1);
        }
        JLabel pathLabel = new JLabel(filename);
        pathLabel.setToolTipText(absolutePath);
        pnlButtons.add(pathLabel);
        pnlButtons.add(Box.createHorizontalGlue());
        pnlButtons.add(btnClose);
    }
    
    private MetaDataAndDomainData getSelectedMetadata() {
        if (tblMetadata.getSelectionModel().isSelectionEmpty()) {
            return null;
        } else {
            return ((MetadataTableModel)tblMetadata.getModel()).getMetadataAt(tblMetadata.getSelectedRow());
        }
    }
    
    private void copyBookmark(MetaDataAndDomainData metadata) {
        if (metadata != null) {
            MetadataTransferable bt = new MetadataTransferable(metadata);
            systemClipboard.setContents(bt,this);
        }
    }
    
    public List<MetaDataAndDomainData> getBookmarkTableList() {
        return mdlMetadata.metadata;
    }
    
    private void pasteMetadata() {
    	AvuData newAvuData = null;
        try {
            if (systemClipboard.isDataFlavorAvailable(METADATA_FLAVOR)) {
            	MetaDataAndDomainData tmp = ((MetaDataAndDomainData)systemClipboard.getData(METADATA_FLAVOR));
            	try {
            		newAvuData = AvuData.instance(tmp.getAvuAttribute() + (mdlMetadata.getRowCount() + 1), 
					            				tmp.getAvuValue(), 
					            				tmp.getAvuUnit());
            		
            		((Irods)parent.tBrowse.ftpSrvConn.getFtpClient()).addMetadata(absolutePath, newAvuData);
        			
					mdlMetadata.addMetadata(newAvuData);
					btnDelete.setEnabled(true);
            	} catch (Exception e) {
            		throw new IOException("Failed to create new metadata element", e);
            	}
            } else {
                if (systemClipboard.isDataFlavorAvailable(DataFlavor.getTextPlainUnicodeFlavor()) ||
                		systemClipboard.isDataFlavorAvailable(DataFlavor.stringFlavor)) 
                {
                	try {
                		newAvuData = AvuData.instance("attribute" + (mdlMetadata.getRowCount() + 1), 
						            				(String)systemClipboard.getData(DataFlavor.stringFlavor), 
						            				"");
                		((Irods)parent.tBrowse.ftpSrvConn.getFtpClient()).addMetadata(absolutePath, newAvuData);
            			
    					mdlMetadata.addMetadata(newAvuData);
    					btnDelete.setEnabled(true);
                	} catch (Exception e) {
                		throw new IOException("Failed to create new metadata element", e);
                	}
                	
                }
            }
        } catch (Exception e) {
            LogManager.error("Failed to paste metadata from clipboard.",e);
            return;
        } 
    }
    
    
    public void mnuPopup_actionPerformed(ActionEvent e) {
       if (e.getActionCommand().compareTo("Copy")==0) {
           copyBookmark(getSelectedMetadata());
       } else if (e.getActionCommand().compareTo("Paste")==0) {
           pasteMetadata();
       } else if (e.getActionCommand().compareTo("Info")==0) {
//            not implemented...doesn't make sense.
       } else if (e.getActionCommand().compareTo("Edit")==0) {
    	   int row = tblMetadata.getSelectedRow();
    	   int col = tblMetadata.getSelectedColumn();
    	   MetaDataAndDomainData oldValue = mdlMetadata.getMetadataAt(row);
    	   DlgMetaData metaDialog = new DlgMetaData(this, oldValue);
			
    	   AvuData newAvuData = metaDialog.getAvuData();
			
    	   if (newAvuData != null) {
    		   try {
    			   AvuData oldAvuData = new AvuData(oldValue.getAvuAttribute(), 
			   				oldValue.getAvuValue(), 
			   				oldValue.getAvuUnit());
    			   ((Irods)parent.tBrowse.ftpSrvConn.getFtpClient()).updateMetadata(absolutePath, oldAvuData, newAvuData);
    			   mdlMetadata.setValueAt(newAvuData.getAttribute(), row, 0);
    			   mdlMetadata.setValueAt(newAvuData.getValue(), row, 1);
    			   mdlMetadata.setValueAt(newAvuData.getUnit(), row, 2);
    		   } catch (Exception e2) {
    			   JOptionPane.showMessageDialog(this, "Failed to update new metadata record", "Metadata Error", JOptionPane.OK_OPTION);
    		   }
    	   }
//           tblMetadata.editCellAt(row, col);
//           focusEditingCell(row,col);
       } else if (btnAdd == e.getSource() || e.getActionCommand().compareTo("Add")==0) {
           int newRow = tblMetadata.getRowCount();
//           try {
//        	   MetaDataAndDomainData newData = MetaDataAndDomainData.instance(MetadataDomain.DATA, 
//							            				"objectId", 
//							            				"objectName",
//							            				"attribute" + (newRow+1), 
//							            				"value", 
//							            				" ");
//        	   mdlMetadata.addMetadata(newData);//parent.getCurrentDir()));
//               tblMetadata.setRowSelectionInterval(newRow, newRow);
//               tblMetadata.editCellAt(newRow, 0);
//               focusEditingCell(newRow,0);
//	       	}
//	      	} catch (Exception e1) {
//	      		e1.printStackTrace();
//	      	}
        	DlgMetaData metaDialog = new DlgMetaData(this);
           	AvuData newAvuData = metaDialog.getAvuData();
           	
           	if (newAvuData != null) {
           		try
				{
           			((Irods)parent.tBrowse.ftpSrvConn.getFtpClient()).addMetadata(absolutePath, newAvuData);
       			
					mdlMetadata.addMetadata(newAvuData);
					btnDelete.setEnabled(true);
				}
				catch (IOException e1)
				{
					JOptionPane.showMessageDialog(this, "Failed to add new metadata record", "Metadata Error", JOptionPane.OK_OPTION);
				}
           	}
       } else if (btnDelete == e.getSource() || e.getActionCommand().compareTo("Delete")==0) {
    	   	try
			{
    	   		int row = tblMetadata.getSelectedRow();
    	   		if (row > -1) {
		       		MetaDataAndDomainData metadata = mdlMetadata.getMetadataAt(row);
		       		AvuData data = new AvuData(metadata.getAvuAttribute(), 
		       									metadata.getAvuValue(), 
		       									metadata.getAvuUnit());
		       		
		       		((Irods)parent.tBrowse.ftpSrvConn.getFtpClient()).deleteMetadata(absolutePath, data);
		       		mdlMetadata.removeMetadataAt(tblMetadata.getSelectedRow());	
		       		
		       		if (mdlMetadata.getRowCount() == 0) {
		       			btnDelete.setEnabled(false);
		       		}
    	   		} else {
    	   			JOptionPane.showMessageDialog(this, "Please select a record to delete", "Metadata Error", JOptionPane.OK_OPTION);
    	   		}
			}
			catch (Exception e1)
			{
				JOptionPane.showMessageDialog(this, "Failed to delete metadata record", "Metadata Error", JOptionPane.OK_OPTION);
			}
       }
       
    }
    
    private void focusEditingCell(int row, int col) {
        JTextField txtField = ((JTextField)((DefaultCellEditor)tblMetadata.getCellEditor(row, col))
                .getTableCellEditorComponent(tblMetadata, 
                        (String)mdlMetadata.getValueAt(row,col), 
                        true, 
                        row, 
                        col));
        txtField.requestFocus();
        txtField.selectAll();
    }
    
    public void lostOwnership(Clipboard clipboard, Transferable transferable) {}
        
    private void showPopupMenu(Point p) {
        
    	MetaDataAndDomainData metadata = getSelectedMetadata();
        
        // enable paste if a Metadata is on the clipboard
        mnuPaste.setVisible(systemClipboard.isDataFlavorAvailable(METADATA_FLAVOR));
        
        // disable metadata specific actions if no
        // metadata is selected.
        boolean isSelected = (metadata != null);
        mnuCopy.setVisible(isSelected);
        mnuEdit.setVisible(isSelected);
        mnuDelete.setVisible(mdlMetadata.getRowCount() > 0 && metadata != null);
        
        rightClickPopup.show(tblMetadata,p.x,p.y);
    }
    
    protected void addMetadata(MetaDataAndDomainData metadata) {
        mdlMetadata.addMetadata(metadata);
    }
    
    /**
     * Action for button and menuitem events.
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

    class MetadataTableMouseAdapter extends MouseAdapter {
        int lastRow = -1;
        private PnlIrodsMetadata pnlIrodsMetadata;
        
        public MetadataTableMouseAdapter(PnlIrodsMetadata pnlIrodsMetadata) {
        	super();
        	this.pnlIrodsMetadata = pnlIrodsMetadata;
        }
        /* 
         * Overrides the default mouseadaptor behavior by displaying a popup menu
         * on right click (platform independent) and closing the window and listing
         * the contents of the directory in the other window.
         * 
         * @see java.awt.event.MouseAdapter#mouseClicked(java.awt.event.MouseEvent)
         */
        @Override
        public void mouseClicked(MouseEvent e) {
            super.mouseClicked(e);
            
            if (editingBookmark && lastRow != tblMetadata.getSelectedRow()) {
                editingBookmark = false;
                mdlMetadata.setEditableRow(-1);
            }
            
            lastRow = tblMetadata.getSelectedRow();
            
            if (isRightClickEvent(e)) {
                int clickedRow = tblMetadata.rowAtPoint(e.getPoint());
                int clickedCol = tblMetadata.columnAtPoint(e.getPoint());
                // could be clicking on empty table area
                if (clickedRow >= 0) {
                    tblMetadata.setRowSelectionInterval(clickedRow, clickedRow);
                    tblMetadata.setColumnSelectionInterval(clickedCol, clickedCol);
                } else {
                	tblMetadata.clearSelection();
                }
                
                showPopupMenu(e.getPoint());
            }
            else if(e.getClickCount() == 2) {
            	int row = tblMetadata.rowAtPoint(e.getPoint());
            	 
            	if (row >= 0) {
            		MetaDataAndDomainData oldValue = mdlMetadata.getMetadataAt(row);
            		DlgMetaData metaDialog = new DlgMetaData(pnlIrodsMetadata, oldValue);
	            	
            		AvuData newAvuData = metaDialog.getAvuData();
	            	
	            	if (newAvuData != null) {
	            		try {
		            		AvuData oldAvuData = new AvuData(oldValue.getAvuAttribute(), 
		            				oldValue.getAvuValue(), 
		            				oldValue.getAvuUnit());
		            		((Irods)parent.tBrowse.ftpSrvConn.getFtpClient()).updateMetadata(absolutePath, oldAvuData, newAvuData);
		            		mdlMetadata.setValueAt(newAvuData.getAttribute(), row, 0);
		     			   	mdlMetadata.setValueAt(newAvuData.getValue(), row, 1);
		     			   	mdlMetadata.setValueAt(newAvuData.getUnit(), row, 2);
	            		} catch (Exception e2) {
	            			JOptionPane.showMessageDialog(pnlIrodsMetadata, "Failed to update new metadata record", "Metadata Error", JOptionPane.OK_OPTION);
	            		}
	            	}
	
	        		    // retrieve data from the JTextFields and do things
	            } else {
	            	DlgMetaData metaDialog = new DlgMetaData(pnlIrodsMetadata);
	            	AvuData newAvuData = metaDialog.getAvuData();
	            	
	            	if (newAvuData != null) {
	            		try
						{
	            			((Irods)parent.tBrowse.ftpSrvConn.getFtpClient()).addMetadata(absolutePath, newAvuData);
	            			
							mdlMetadata.addMetadata(newAvuData);
							btnDelete.setEnabled(true);
						}
						catch (IOException e1)
						{
							JOptionPane.showMessageDialog(pnlIrodsMetadata, "Failed to add new metadata record", "Metadata Error", JOptionPane.OK_OPTION);
						}
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
        
//    class MetadataTableModelListener implements TableModelListener {
//        
//        public void tableChanged(TableModelEvent e) {
//            int row = e.getFirstRow();
////            int column = e.getColumn();
//            MetadataTableModel model = (MetadataTableModel)e.getSource();
////            Object data = model.getValueAt(row, column);
//             if (e.getType() == TableModelEvent.UPDATE) {
//                //now you have the data in the cell and the place in the grid where the 
//                //cell is so you can use the data as you want
//            	MetaDataAndDomainData data = model.getMetadataAt(row);
//            	AvuData avuData;
//				try
//				{
//					avuData = new AvuData(data.getAvuAttribute(), data.getAvuValue(), data.getAvuUnit());
//					((Irods)parent.tBrowse.ftpSrvConn.getFtpClient()).updateMetadata(absolutePath, avuData, avuData);
//				}
//				catch (Exception e1)
//				{
//					e1.printStackTrace();
//				}
//                
//             } else if (e.getType() == TableModelEvent.INSERT) {
//            	MetaDataAndDomainData metadata = mdlMetadata.getMetadataAt(row);
// 	       		try
//				{
// 	       			AvuData data = new AvuData(metadata.getAvuAttribute(), 
//							metadata.getAvuValue(), 
//							metadata.getAvuUnit());
//
//					((Irods)parent.tBrowse.ftpSrvConn.getFtpClient()).addMetadata(absolutePath, data);
//				}
//				catch (Exception e1)
//				{
//					e1.printStackTrace();
//				}
//             }
//        }
//    }
    
    class MetadataTableModel extends AbstractTableModel {
        private String[] columns = {"Attribute", "Value", "Units"};
        
        private List<MetaDataAndDomainData> metadata = new ArrayList<MetaDataAndDomainData>();
        
        private int editableRow = -1;
        
        public MetadataTableModel() {
            metadata = new ArrayList<MetaDataAndDomainData>();
            
        }
        
        public void addMetadata(AvuData newAvuData)
		{
        	try
			{
				metadata.add(MetaDataAndDomainData.instance(MetadataDomain.DATA, 
						"objectID", 
						"UniqueName", 
						newAvuData.getAttribute(), 
						newAvuData.getValue(), 
						newAvuData.getUnit()));
			}
			catch (JargonException e)
			{
				JOptionPane.showMessageDialog(null, "Failed to add new metadata record", "Metadata Input Error", JOptionPane.OK_OPTION);
			}
		}

		public MetadataTableModel(List<MetaDataAndDomainData> bookmarks) {
            if (bookmarks!=null)
                this.metadata = bookmarks;
        }
        
        public String getColumnName(int index) {
            return columns[index];
        }

        public int getColumnCount() {
            return columns.length;
        }

        public int getRowCount() {
            return metadata.size();
        }
        
        public Class getColumnClass(int col) {
            return String.class;
        }
        
        public boolean isCellEditable(int row, int col) {
            return row == editableRow;
        }
        
        public void setEditableRow(int row) {
            editableRow = row;
        }
        
        public Object getValueAt(int row, int column) {
            Object value = null;
            if ( row >= metadata.size() ) {
                return null;
            }
            if (column == 0)
            	return metadata.get(row).getAvuAttribute();
            else if (column == 1) 
            	return metadata.get(row).getAvuValue();
            else {
            	return metadata.get(row).getAvuUnit();
            }
        }
        
        public void setValueAt(Object value, int row, int col) {
            
        	MetaDataAndDomainData tuple = metadata.get(row);
        	MetaDataAndDomainData newData = null;
            try 
            {
                switch(col) 
                {
	                case 0:
	                	try {
		                	newData = MetaDataAndDomainData.instance(	tuple.getMetadataDomain(), 
		                										tuple.getDomainObjectId(), 
		                										tuple.getDomainObjectUniqueName(), 
		                										value.toString(), 
		                										tuple.getAvuValue(), 
		                										tuple.getAvuUnit());
		                	metadata.set(row, newData);
		                	fireTableDataChanged();
	                	} catch (Exception e) {
	                		throw new IOException("Failed to create new metadata element", e);
	                	}
	                	
	                	
	                    break;
	                case 1:
	                	try {
	                		newData = MetaDataAndDomainData.instance(tuple.getMetadataDomain(), 
	                										tuple.getDomainObjectId(), 
	                										tuple.getDomainObjectUniqueName(), 
	                										tuple.getAvuAttribute(), 
	                										value.toString(), 
	                										tuple.getAvuUnit());
	                		metadata.set(row, newData);
	                		fireTableDataChanged();
	                	} catch (Exception e) {
	                		throw new IOException("Failed to create new metadata element", e);
	                	}
	                	
	                    break;
	                case 2:
	                	try {
	                		newData = MetaDataAndDomainData.instance(tuple.getMetadataDomain(), 
	                										tuple.getDomainObjectId(), 
	                										tuple.getDomainObjectUniqueName(), 
	                										tuple.getAvuAttribute(), 
	                										tuple.getAvuValue(), 
	                										value.toString());
	                		metadata.set(row, newData);
	                		fireTableDataChanged();
	                	} catch (Exception e) {
	                		throw new IOException("Failed to create new metadata element", e);
	                	}
	                	
	                    break;
                }
                
                if (newData != null) {
                	fireTableCellUpdated(row, col);
                }
                
            } catch (IOException e) {
                LogManager.error("Failed to set metadata " + 
                        ((col==0)?"attribute":"value") + " to " + value.toString(),e);
            }
            
        }
        
        public MetaDataAndDomainData getMetadataAt(int row){
            return metadata.get(row);
        }
        
        public void removeMetadataAt(int row) {
            metadata.remove(row);
            fireTableRowsDeleted(row,row);
        }
        
        public void addMetadata(MetaDataAndDomainData entry) {
            metadata.add(entry);
            fireTableRowsInserted(metadata.size()-1, metadata.size()-1);
        }
        
        public void addMetdataList(List<MetaDataAndDomainData> entries) {
        	metadata.addAll(entries);
            fireTableRowsInserted(this.metadata.size() - entries.size() - 1, this.metadata.size()-1);
        }
        
        public void removeBookmark(MetaDataAndDomainData entry) {
            int row = metadata.indexOf(entry);
            metadata.remove(entry);
            fireTableRowsDeleted(row,row);
        }
        
    }

    
    class MetadataTransferable implements Transferable {
        
        private MetaDataAndDomainData metadata;
        
        private DataFlavor[] flavors = new DataFlavor[]{METADATA_FLAVOR, 
                DataFlavor.stringFlavor};
        
        public MetadataTransferable(MetaDataAndDomainData metadata){
            this.metadata = metadata;
        }
        
        public Object getTransferData(DataFlavor flavor)
        throws UnsupportedFlavorException, IOException {
            if (flavor == METADATA_FLAVOR) {
                return metadata;
            } else if (flavor == DataFlavor.stringFlavor) {
                return metadata.getAvuValue();
            } else {
                throw new UnsupportedFlavorException(flavor);
            }
        }
        
        public DataFlavor[] getTransferDataFlavors() {
            return flavors;
        }
        
        public boolean isDataFlavorSupported(DataFlavor flavor) {
            for(int i=0; i<flavors.length;i++){
                if(flavors[i]==flavor){
                    return true;
                }
            }
            return false;
        }
        
    }
    
    public static void main(String[] args) {
        JFrame frame = new JFrame("Metadata Demo");
        ArrayList<MetaDataAndDomainData> arrayList = loadData();
        PnlIrodsMetadata p = new PnlIrodsMetadata(arrayList, "rally.png");
        frame.setContentPane(p);
        frame.pack();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
       
    private static ArrayList<MetaDataAndDomainData> loadData() {
       ArrayList<MetaDataAndDomainData> entries = new ArrayList<MetaDataAndDomainData>();
//       MetaDataAndDomainData b = new MetaDataAndDomainData("Test1","/usr/local/bin");
//       bookmarks.add(b);
//       b = new MetaDataAndDomainData("Test2","/Users/dooley");
//       bookmarks.add(b);
//       b = new MetaDataAndDomainData("Test2","/tmp");
//       bookmarks.add(b);
       return entries;
   }

    
}
