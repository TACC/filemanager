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
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.table.AbstractTableModel;

import org.apache.commons.lang3.StringUtils;
import org.teragrid.service.profile.wsclients.ProfileServiceClient;
import org.teragrid.service.profile.wsclients.model.EnvironmentVariable;

import com.explodingpixels.macwidgets.IAppWidgetFactory;

import edu.utexas.tacc.wcs.filemanager.client.AppMain;
import edu.utexas.tacc.wcs.filemanager.client.ConfigSettings;
import edu.utexas.tacc.wcs.filemanager.client.transfer.GsiSshClient;
import edu.utexas.tacc.wcs.filemanager.client.ui.permissions.StripedTable;
import edu.utexas.tacc.wcs.filemanager.client.util.LogManager;
import edu.utexas.tacc.wcs.filemanager.common.model.enumeration.FileProtocolType;
import edu.utexas.tacc.wcs.filemanager.common.model.enumeration.SystemType;

/**
 * Panel to hold user environment variables on a per-resource basis.
 * 
 * @author Rion Dooley < dooley [at] tacc [dot] utexas [dot] edu >
 *
 */
@SuppressWarnings({"serial","unused","unchecked"})
public class PnlEnvironment extends JPanel implements ClipboardOwner {
    
	private static DataFlavor ENVIRONMENT_FLAVOR = new DataFlavor(EnvironmentVariable.class, DataFlavor.javaSerializedObjectMimeType);
    
    private StripedTable tblEnvironment;
    
    private JScrollPane spEnvironment = new JScrollPane();
    
    private EnvironmentTableModel mdlEnvironment;
    
    private Clipboard systemClipboard = Toolkit.getDefaultToolkit().getSystemClipboard(); 
    
    private JPopupMenu rightClickPopup = new JPopupMenu();
    
    private JMenuItem mnuCopy; 
    private JMenuItem mnuOpen;
    
    private PnlBrowse parent = null;
    
    public PnlEnvironment(PnlBrowse pnlBrowse) {
        super();
        
        this.parent = pnlBrowse;
        
        List<EnvironmentVariable> environment = new ArrayList<EnvironmentVariable>();
        
//        if () {
//        	environment = new ArrayList<EnvironmentVariable>();
//        	// user environment is more or less meaningless on their local system since the 
//        	// code below gives the application env, which is a vanilla env, and using a
//        	// ProcessBuilder would give a default env, without sourcing their login script.
////        	Map<String, String> env = System.getenv();
////        	for(String name: env.keySet()) {
////        		environment.add(new EnvironmentVariable(name, env.get(name)));
////        	}
//        	
//        } 
//        else
        if (!pnlBrowse.ftpServer.isLocal() && 
        			!pnlBrowse.getFtpServer().hostType.equals(SystemType.ARCHIVE) &&
        			!StringUtils.isEmpty(pnlBrowse.getFtpServer().sshHost) && 
        			pnlBrowse.getFtpServer().protocol.equals(FileProtocolType.GRIDFTP))
        {
        	try 
        	{
	        	GsiSshClient client = new GsiSshClient(pnlBrowse.getFtpServer().sshHost, 
	        			pnlBrowse.getFtpServer().sshPort, AppMain.defaultCredential);
		        
		        Properties props = client.env();	       
		        for(Iterator<Object> iter = (Iterator<Object>)props.keySet().iterator(); iter.hasNext();) {
		        	String prop = (String)iter.next();
		        	environment.add(new EnvironmentVariable(prop, props.getProperty(prop)));
		        }
		        
		        // add the env to the browsing thread for use in goto path requests
		        pnlBrowse.tBrowse.setEnvProperties(environment);
		    	
		        pbInit(environment);
		        
        	} catch (Throwable t) {
    			LogManager.error("Failed to retrieve user environment", t);
    		}
    	}
    	else
    	{
    		AppMain.Error(pnlBrowse.getParent(), "Environment variable detection is not supported on this system.");
    		
    	}
        
    }
    
    public PnlEnvironment( List<EnvironmentVariable> environment) {
        super();
        
        pbInit(environment);
        
    }

    private void pbInit( List<EnvironmentVariable> environment) {
        
        // initialize the components
        initEnvironmentTable(environment);
        
        initPopupMenu();
        
        // layout the panel
        spEnvironment.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        spEnvironment.getViewport().add(tblEnvironment);
        spEnvironment.addMouseListener(new EnvironmentTableMouseAdapter());
        spEnvironment.getViewport().setBackground(getBackground());
        IAppWidgetFactory.makeIAppScrollPane(spEnvironment);
        
        setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
        add(spEnvironment,BorderLayout.CENTER);
        
    }
    
    private void initEnvironmentTable(List<EnvironmentVariable> environment) {
        
    	mdlEnvironment = new EnvironmentTableModel(environment);
    	
    	tblEnvironment = new StripedTable(mdlEnvironment);
        
    	tblEnvironment.addMouseListener(new EnvironmentTableMouseAdapter());
//        tblEnvironment.setBackground(getBackground());
        
    	tblEnvironment.setMinimumSize(new Dimension(100,100));
    	tblEnvironment.setMinimumSize(new Dimension(200,300));
    	tblEnvironment.setMaximumSize(new Dimension(Integer.MAX_VALUE,Integer.MAX_VALUE));
        
        
    }
    
    private JPopupMenu initPopupMenu() {
        
        rightClickPopup = new JPopupMenu();
        
        mnuCopy = new JMenuItem(new PopupAction("Copy","Copy",KeyEvent.VK_C));
        mnuOpen = new JMenuItem(new PopupAction("Open","Open",KeyEvent.VK_O));
        
        rightClickPopup.add(mnuOpen);
        rightClickPopup.add(mnuCopy);
        
        return rightClickPopup;
    }
    
    private EnvironmentVariable getSelectedEnvironmentVariable() {
        if (tblEnvironment.getSelectionModel().isSelectionEmpty()) {
            return null;
        } else {
            return ((EnvironmentTableModel)tblEnvironment.getModel()).getEnvironmentVariableAt(tblEnvironment.getSelectedRow());
        }
    }
    
    private void copyEnvironmentVariable(EnvironmentVariable environment) {
        if (environment != null) {
            EnvironmentTransferable bt = new EnvironmentTransferable(environment);
            systemClipboard.setContents(bt,this);
        }
    }
    
    public List<EnvironmentVariable> getEnvironmentTableList() {
        return mdlEnvironment.environment;
    }
    
    
    public void mnuPopup_actionPerformed(ActionEvent e) {
       if (e.getActionCommand().compareTo("Copy")==0) {
    	   copyEnvironmentVariable(getSelectedEnvironmentVariable());
       } else if (e.getActionCommand().compareTo("Open")==0) {
    	   EnvironmentVariable environmentVariable = getSelectedEnvironmentVariable();
           if (environmentVariable != null)
               parent.tBrowse.cmdAdd("Cwd",environmentVariable.getValue(),null);
       }
       
    }
    public void lostOwnership(Clipboard clipboard, Transferable transferable) {}
        
    private void showPopupMenu(Point p) {
        
    	EnvironmentVariable environmentVariable = getSelectedEnvironmentVariable();
        
        // disable environemnt variable specific actions if no
        // row is selected.
        boolean isSelected = (environmentVariable != null);
        mnuCopy.setVisible(isSelected);
        mnuOpen.setVisible(isSelected);
        
        rightClickPopup.show(tblEnvironment,p.x,p.y);
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

    class EnvironmentTableMouseAdapter extends MouseAdapter {
        int lastRow = -1;
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
            
//            if (lastRow != tblEnvironment.getSelectedRow()) {
//                mdlEnvironment.setEditableRow(-1);
//            }
            
            lastRow = tblEnvironment.getSelectedRow();
            
            if (isRightClickEvent(e)) {
                int clickedRow = tblEnvironment.rowAtPoint(e.getPoint());
                int clickedCol = tblEnvironment.columnAtPoint(e.getPoint());
                // could be clicking on empty table area
                if (clickedRow >=0) {
                    tblEnvironment.setRowSelectionInterval(clickedRow, clickedRow);
                    tblEnvironment.setColumnSelectionInterval(clickedCol, clickedCol);
                } else 
                    tblEnvironment.clearSelection();
                
                showPopupMenu(e.getPoint());
            }
            else if(e.getClickCount() == 2) {
            	EnvironmentVariable environmentVariable = getSelectedEnvironmentVariable();
                if (environmentVariable != null) {
                    parent.btnEnvironment.doClick();
                    parent.tblListing.requestFocusInWindow();
                    parent.tBrowse.cmdAdd("Cwd",environmentVariable.getValue(),null);
                    
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
        
    class EnvironmentTableModel extends AbstractTableModel {
        private String [] columns = new String[]{"Name","Value"};
        
        private List<EnvironmentVariable> environment = new ArrayList<EnvironmentVariable>();
        
        private int editableRow = -1;
        
        public EnvironmentTableModel() {}
        
        public EnvironmentTableModel(List<EnvironmentVariable> environment) {
            if (environment!=null)
                this.environment = environment;
        }
        
        public String getColumnName(int col) {
            return columns[col].toString();
        }

        public int getColumnCount() {
            return columns.length;
        }

        public int getRowCount() {
            return environment.size();
        }
        
        public Class getColumnClass(int col) {
            return String.class;
        }
        
        public boolean isCellEditable(int row, int col) {
            return false;
        }
        
        public Object getValueAt(int row, int column) {
            Object value = null;
            if(row>=environment.size()) {
                return null;
            }
            
            switch(column){
            case 0:
                //Name
                value = environment.get(row).getName();
                break;
            case 1:
                //Value
                value = environment.get(row).getValue();
                break;
            default:
                value = "";
                break;
            }
            return value;
        }
        
        public void setValueAt(Object value, int row, int col) {
            
        	EnvironmentVariable environmentVariable = environment.get(row);
            try {
                switch(col) {
                case 0:
                    try {
                    	environmentVariable.setName(value.toString());
                    } catch(IOException e) {
                    	environmentVariable.setName(environmentVariable.getValue());
                    }
                    break;
                case 1:
                	environmentVariable.setValue(value.toString());
                    break;
                }
                
                fireTableCellUpdated(row, col);
                
            } catch (IOException e) {
                LogManager.error("Failed to set environment " + 
                        ((col==0)?"name":"value") + " to " + value.toString(),e);
            }
            
        }
        
        public EnvironmentVariable getEnvironmentVariableAt(int row){
            return environment.get(row);
        }
        
        public void removeEnvironmentVariableAt(int row) {
        	environment.remove(row);
            fireTableRowsDeleted(row,row);
        }
        
        public void addEnvironmentVariable(EnvironmentVariable environmentVariable) {
        	environment.add(environmentVariable);
            fireTableRowsInserted(environment.size()-1, environment.size()-1);
        }
        
        public void addEnvironmentVariableList(List<EnvironmentVariable> environment) {
        	environment.addAll(environment);
            fireTableRowsInserted(this.environment.size()-environment.size()-1, environment.size()-1);
        }
        
        public void removeEnvironmentVariable(EnvironmentVariable environmentVariable) {
            int row = environment.indexOf(environmentVariable);
            environment.remove(environmentVariable);
            fireTableRowsDeleted(row,row);
        }
        
    }

    
    class EnvironmentTransferable implements Transferable {
        
        private EnvironmentVariable environmentVariable;
        
        private DataFlavor[] flavors = new DataFlavor[]{ENVIRONMENT_FLAVOR, 
                DataFlavor.stringFlavor};
        
        public EnvironmentTransferable(EnvironmentVariable environmentVariable){
            this.environmentVariable = environmentVariable;
        }
        
        public Object getTransferData(DataFlavor flavor)
        throws UnsupportedFlavorException, IOException {
            if (flavor == ENVIRONMENT_FLAVOR) {
                return environmentVariable;
            } else if (flavor == DataFlavor.stringFlavor) {
                return environmentVariable.getValue();
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
        JFrame frame = new JFrame("Environment Variable Demo");
        ArrayList<EnvironmentVariable> arrayList = loadData();
        PnlEnvironment p = new PnlEnvironment(arrayList);
        frame.setContentPane(p);
        frame.pack();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
       
    private static ArrayList<EnvironmentVariable> loadData() {
       ArrayList<EnvironmentVariable> environment = new ArrayList<EnvironmentVariable>();
       EnvironmentVariable env = new EnvironmentVariable("Test1","/usr/local/bin");
       environment.add(env);
       env = new EnvironmentVariable("Test2","/Users/dooley");
       environment.add(env);
       env = new EnvironmentVariable("Test2","/tmp");
       environment.add(env);
       return environment;
   }

    
}
