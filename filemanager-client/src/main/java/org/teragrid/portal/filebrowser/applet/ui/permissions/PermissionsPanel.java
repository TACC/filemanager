/**
 * 
 */
package org.teragrid.portal.filebrowser.applet.ui.permissions;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.ItemSelectable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import org.globus.ftp.FileInfo;
import org.irods.jargon.core.protovalues.FilePermissionEnum;
import org.teragrid.portal.filebrowser.applet.AppMain;
import org.teragrid.portal.filebrowser.applet.file.GenericFileInfo;
import org.teragrid.portal.filebrowser.applet.file.IRODSFileInfo;
import org.teragrid.portal.filebrowser.applet.util.LogManager;

import com.explodingpixels.macwidgets.IAppWidgetFactory;
import com.explodingpixels.macwidgets.MacFontUtils;
import com.explodingpixels.macwidgets.TriAreaComponent;

/**
 * @author dooley
 *
 */
@SuppressWarnings("serial")
public abstract class PermissionsPanel extends JPanel {
	
	protected FileInfo fileInfo;
	protected String[] columnNames = {"Name","Privilege"};
	protected StripedTable tblPermissions;
	protected TableModel model;
	protected Vector<TableCellEditor> editors;
	protected JScrollPane scrollPane;
	protected Box buttonBox;
	protected TriAreaComponent bottomBar;
	protected JButton btnAdd;
	protected JButton btnSub;
	protected boolean canEdit = false;
	protected boolean canAdd = false;
	protected int lastRow = -1;
	
	public PermissionsPanel() {
		super();
	}
	
	public PermissionsPanel(FileInfo fileInfo) {
		this(fileInfo,false,false);
	}
	
	public PermissionsPanel(FileInfo fileInfo, boolean canEdit) {
		this(fileInfo,canEdit,false);
		
	}
	
	public PermissionsPanel(FileInfo fileInfo, boolean canEdit, boolean canAdd) {
		super();
		
		this.fileInfo = fileInfo;
		this.canEdit = canEdit;
		this.canAdd = canAdd;
		
		if (fileInfo instanceof GenericFileInfo)
			this.model = createPermissionTableModel(((GenericFileInfo)fileInfo).getPermissions());
		else if (fileInfo instanceof IRODSFileInfo) {
			// skipping this section 
		} else 
			this.model = createPermissionTableModel(fileInfo.getModeAsString());
		
		init();
		bottomBarInit();
		pbLayout();
		
	}
	
	protected void init() {
		tblPermissions = new StripedTable(model) {
		
			@Override
			public boolean isCellEditable(int row, int column) {
				if (column == 0) {
						return false;
					}
					return true;
			}
			
		//  Determine editor to be used by row
			public TableCellEditor getCellEditor(int row, int column) {
				int modelColumn = convertColumnIndexToModel( column );
				if (modelColumn == 1) {
					return editors.get(row);
				} else {
					return super.getCellEditor();
				}
			}
			
		};
		
		tblPermissions.setFont(MacFontUtils.ITUNES_FONT);
		
		if (canEdit) {
			// Set the combobox editor on the 1st visible column
			int vColIndex = 1;
		    TableColumn col = tblPermissions.getColumnModel().getColumn(vColIndex);
		    editors = new Vector<TableCellEditor>();
		    String[] permissionValues;
//		    if (fileInfo instanceof TGShareFileInfo) {
//		    	permissionValues = new String[]{UnixPermissions.READ, UnixPermissions.WRITE, UnixPermissions.READWRITE};
//		    } else 
		    if (fileInfo instanceof IRODSFileInfo) {
		    	permissionValues = new String[FilePermissionEnum.values().length];
		    	int i=0;
		    	for(FilePermissionEnum pemEnum: FilePermissionEnum.values()) {
		    		permissionValues[i] = pemEnum.name();
		    		i++;
		    	}
		    } else {
		    	permissionValues = UnixPermissions.getStringValues();
		    }
		    
		    for (int i=0;i<model.getRowCount();i++) {
		    	editors.add(new MyComboBoxEditor(permissionValues));
		    }
		    
		    // If the cell should appear like a combobox in its
		    // non-editing state, also set the combobox renderer
		    
		    col.setCellRenderer(new MyComboBoxRenderer(permissionValues));
		}
		
		scrollPane = new JScrollPane(tblPermissions);
		scrollPane.setWheelScrollingEnabled(true);
		IAppWidgetFactory.makeIAppScrollPane(scrollPane);
//		add(scrollPane);
		
	}
	
	protected void bottomBarInit() {
		btnAdd = new JButton(AppMain.icoAdd);
		btnAdd.addActionListener(new ButtonActionListener(this));
		btnAdd.setEnabled(canEdit);
		btnSub = new JButton(AppMain.icoRemove);
		btnSub.addActionListener(new ButtonActionListener(this));
		btnSub.setEnabled(canEdit);

		buttonBox = new Box(BoxLayout.LINE_AXIS);
		buttonBox.add(btnAdd, Component.LEFT_ALIGNMENT);
		buttonBox.add(btnSub, Component.LEFT_ALIGNMENT);
		buttonBox.add(Box.createGlue());
		
//		add(buttonBox);
	}
	
	protected void pbLayout() {
		Box box = new Box(BoxLayout.Y_AXIS);
		box.add(scrollPane);
		
		if (canAdd) {
			box.add(buttonBox);
		}
		
		box.add(Box.createVerticalGlue());
		add(box);
		box.setPreferredSize(new Dimension(225,150));
//		setBackground(Color.white);
	}
	
	/**
	 * Populates the table with permissions associated with this FileItem.
	 * 
	 * @param permissions object containing permission information
	 */
	protected abstract TableModel createPermissionTableModel(Object permissions);

	/**
	 * Method to handle action events from the permission check boxes.
	 * @param e
	 */
	public abstract void permissionStateChangePerformed(ItemEvent e);
	
	/**
	 * Method to handle action events from the add/remove buttons
	 * @param e
	 */
	public abstract void permissionUpdateActionPerformed(ActionEvent e);
	
	/**
	 * Class to render the contents of a cell into a combo box.
	 * @author dooley
	 *
	 */
	protected class MyComboBoxRenderer extends JComboBox implements TableCellRenderer {
        public MyComboBoxRenderer(String[] items) {
            super(items);
        }
    
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            if (isSelected) {
                setForeground(table.getForeground());
                setBackground(table.getBackground());
                setFont(MacFontUtils.ITUNES_FONT);
            } else {
            	setForeground(table.getForeground());
                setBackground(table.getBackground());
                setFont(MacFontUtils.ITUNES_FONT);
            }
            
        	// Select the current value
            setSelectedItem(value);
            setPreferredSize(new Dimension(50,20));
            return this;
        }
    }
    
	protected class MyComboBoxEditor extends DefaultCellEditor {
		private boolean alreadyHadFocus = false;
        public MyComboBoxEditor(String[] items) {
        	
        	super(new JComboBox(items));
            
        	final JComboBox comboBox = ((JComboBox)getComponent());
        	
        	comboBox.setOpaque(false);
            
        	comboBox.setEnabled(canEdit);
            
        	comboBox.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					if (alreadyHadFocus) {
						ItemEvent ie = new ItemEvent((ItemSelectable) e.getSource(), 1 , comboBox.getSelectedItem(), ItemEvent.SELECTED);
						permissionStateChangePerformed(ie);
					} else {
						alreadyHadFocus = true;
					}
					
				}
        	
        	});
//        	comboBox.addItemListener(new ItemListener() {
//            	public void itemStateChanged(ItemEvent e) {
//            		if (e.getStateChange() == ItemEvent.SELECTED) {
//            			if (alreadyHadFocus) {
//            				permissionStateChangePerformed(e);
//            			} else {
//            				alreadyHadFocus = true;
//            			}
//            		}
//            	}
//            });

        	comboBox.addFocusListener(new FocusListener() {
				public void focusGained(FocusEvent e) {
				}
				public void focusLost(FocusEvent e) {
					alreadyHadFocus = false;
					LogManager.debug("Focus lost");
				}
            });
            
        }
    }
	
}



class ButtonActionListener implements ActionListener {
	PermissionsPanel adaptee;
	public ButtonActionListener(PermissionsPanel adaptee) {
		this.adaptee = adaptee;
	}
	public void actionPerformed(ActionEvent e) {
		adaptee.permissionUpdateActionPerformed(e);
	}
}

