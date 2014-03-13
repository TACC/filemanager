/* 
 * Created on Aug 6, 2008
 * 
 * Developed by: Rion Dooley - dooley [at] tacc [dot] utexas [dot] edu
 * 				 TACC, Texas Advanced Computing Center
 * 
 * https://www.tacc.utexas.edu/
 */

package org.teragrid.portal.filebrowser.applet.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

import org.globus.ftp.FileInfo;
import org.jets3t.service.acl.AccessControlList;
import org.jets3t.service.acl.GrantAndPermission;
import org.jets3t.service.acl.GranteeInterface;
import org.jets3t.service.acl.Permission;
import org.teragrid.portal.filebrowser.applet.AppMain;
import org.teragrid.portal.filebrowser.applet.file.S3FileInfo;
import org.teragrid.portal.filebrowser.applet.transfer.S3;
import org.teragrid.portal.filebrowser.applet.util.LogManager;

/**
 * Insert Template description here.
 * 
 * @author Rion Dooley < dooley [at] tacc [dot] utexas [dot] edu >
 *
 */
@SuppressWarnings({"serial","unused","unchecked"})
public class DlgS3Sharing extends DlgEscape implements ActionListener {

    private static final String SHARE_TG_USER = "With Teragrid User";
    private static final String SHARE_EMAIL_ADDRESS = "With Email Address";
    private static final String SHARE_S3_ID = "With User ID";
    private JPanel pnlMain = new JPanel();
    private JPanel pnlACLControlLayout = new JPanel();
    private JPanel pnlACLControlButtons = new JPanel();
    private JPanel pnlDialogButtons = new JPanel();
    
    private JLabel lblTitle = new JLabel();
    
    private JScrollPane spACLTable = new JScrollPane();
    
    private JTable tblACL; // display table for all entries in acl
    
    private JComboBox cmbShare; // combo box with sharing options
    
    private JButton btnRemove; // remove the currently selected entry
    private JButton btnOK; // save and apply the current settings
    private JButton btnCancel; // cancel the dialog without saving
    
    private JCheckBox chkRecursive; // apply the settings to all bucket entries
    
    private S3FileInfo fileInfo;
    private String path;
    
    
    public DlgS3Sharing(Frame frame, String currentDir, FileInfo file) {
        super(frame,file.getName() + " Access Control");
        
        this.fileInfo = (S3FileInfo)file;
        this.path = currentDir;
        
        jbInit();
        
        Dimension dlgSize = getPreferredSize();
        Dimension frmSize = frame.getSize();
        Point loc = frame.getLocation();
        setLocation((frmSize.width - dlgSize.width) / 2 + loc.x, (frmSize.height - dlgSize.height) / 2 + loc.y);
        setResizable(true);
        pack();
        validate();
        setVisible(true);
        setFocusable(true);
        requestFocus();
        
    }
    
    private void jbInit() {
        
        lblTitle.setText("Managing ACL for '" + fileInfo.getName() + "'");
        
        initACLTable((AccessControlList)fileInfo.getPermissions());
        
        initACLControlButtons();
        
        initACLControlLayout();
        
        initDialogButtons();
        
        pnlMain.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        pnlMain.setLayout(new BorderLayout());
        pnlMain.add(pnlACLControlLayout, BorderLayout.NORTH);
        pnlMain.add(pnlDialogButtons,BorderLayout.CENTER);
        pnlMain.setMinimumSize(new Dimension(350, 400));
        pnlMain.setPreferredSize(new Dimension(350, 400));
        
        getContentPane().add(pnlMain);
        
    }
    
    private void initACLTable(AccessControlList acl) {
    
        tblACL = new JTable(new AccessControlListTableModel(acl));
        
        tblACL.addMouseListener(new MouseAdapter() {

            public void mouseClicked(MouseEvent arg0) {
                
                int col = tblACL.getSelectedColumn();
                int row = tblACL.getSelectedRow();
                
                if (col > 0) {
                    ((AccessControlListTableModel)tblACL.getModel())
                        .getElementAtRow(row).togglePermission(col);
                    
                    ((AccessControlListTableModel)tblACL.getModel()).fireTableDataChanged();
                }
            }
            
        });
        
        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer() {

            public void setValue(Object value) {
                if (value instanceof ImageIcon) {
                    setIcon((ImageIcon)value);
                } else {
                    super.setValue(value);
                }
            }
            
        };
        
        for (int i = 0; i < tblACL.getColumnCount(); i++) {
            
            TableColumn column = tblACL.getColumn(tblACL.getModel().getColumnName(i));
            column.setModelIndex(i);
            column.setPreferredWidth(0==i?160:50);
            cellRenderer.setHorizontalAlignment(0==i?JLabel.LEFT:JLabel.CENTER);
            column.setCellRenderer(cellRenderer);
        }
        
        spACLTable.setAutoscrolls(true);
        spACLTable.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        spACLTable.getViewport().add(tblACL);
        
    }
    
    private void initACLControlButtons() {
        cmbShare = new JComboBox(new String[] {SHARE_TG_USER,
                                               SHARE_EMAIL_ADDRESS,
                                               SHARE_S3_ID});
        cmbShare.addActionListener(this);
        
        btnRemove = new JButton("Remove");
        btnRemove.addActionListener(this);
        
        chkRecursive = new JCheckBox("Apply to bucket contents");
        chkRecursive.setEnabled(fileInfo.isDirectory());
        
        pnlACLControlButtons.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        pnlACLControlButtons.setLayout(new BorderLayout());
        pnlACLControlButtons.add(cmbShare,BorderLayout.WEST);
        pnlACLControlButtons.add(btnRemove,BorderLayout.CENTER);
        pnlACLControlButtons.add(chkRecursive,BorderLayout.EAST);
        
    }
    
    private void initACLControlLayout() {
        
        pnlACLControlLayout.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        pnlACLControlLayout.setLayout(new BorderLayout());
        pnlACLControlLayout.add(lblTitle, BorderLayout.NORTH);
        pnlACLControlLayout.add(spACLTable,BorderLayout.CENTER);
        pnlACLControlLayout.add(pnlACLControlButtons,BorderLayout.SOUTH);
           
    }
    
    private void initDialogButtons() {
        btnOK = new JButton("OK");
        btnOK.addActionListener(this);
        
        btnCancel = new JButton("Cancel");
        btnCancel.addActionListener(this);
        
        pnlDialogButtons.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        pnlDialogButtons.setLayout(new BorderLayout());
        pnlDialogButtons.add(btnCancel,BorderLayout.WEST);
        pnlDialogButtons.add(btnOK,BorderLayout.CENTER);
    }
    
    private void updateACL() {
        AccessControlList acl = (AccessControlList)fileInfo.getPermissions();
        
        Set grants = acl.getGrants();
        grants.clear();
        
        for (GAPListItem item: ((AccessControlListTableModel)tblACL.getModel()).getAllElements()) {
            for (Permission permission: item.permissions) {
                grants.add(new GrantAndPermission(item.grant,permission));
            }
        }
        
        
//        if (fileInfo.isDirectory()) {
//            ((S3)pnlBrowse.tBrowse.ftpSvrConn.getFTPClient).putDirectoryACL(acl, path);
//        } else {
//            ((S3)pnlBrowse.tBrowse.ftpSvrConn.getFTPClient).putFileACL(acl, path + "/" + fileInfo.getName());
//        }
    }
    

    /**
     * @param args
     */
    public static void main(String[] args) {
        final String bucket = "1jfy56b1xfgxh7s2sj82.test";
        String userName = "1JFY56B1XFGXH7S2SJ82";
        String password = "DlLnFRZAvKbvyh1o/iI+KsARzf5OiEDr1FKx31sP";
         
        
        try {
            final S3 s3Client = new S3(userName,password);
            final String currentDir = s3Client.getCurrentDir();
            final S3FileInfo file = s3Client.getDetailedListing(bucket);
            
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    DlgS3Sharing d = new DlgS3Sharing(new Frame("S3 ACL Dialog Test"),
                            currentDir,
                            file);
                }
            });
        } catch (Exception e) {
            LogManager.error("Failed to run file info dialog.",e);
        }
    
    }

    
    public void actionPerformed(ActionEvent e) {
        
        if (e.getSource()==btnCancel) {
        
            this.dispose();
    
        } else if (e.getSource()==btnOK) {

            LogManager.debug("Updating ACL for " + fileInfo.getName());
            
            updateACL();
            
        } else if (e.getSource()==btnRemove) {

            LogManager.debug("Removing ACL entry " + 
                    ((AccessControlListTableModel)tblACL.getModel())
                        .getValueAt(tblACL.getSelectedRow(),0));
            
            ((AccessControlListTableModel)tblACL.getModel()).removeElementAt(tblACL.getSelectedRow());
            
        } else if (e.getSource()==cmbShare) {
            
            LogManager.debug("Sharing data...");
            
            if (cmbShare.getSelectedItem().equals(SHARE_TG_USER)) {
                AppMain.Prompt(AppMain.getApplet(), "Please enter the name of the TG\n" +
                		"user with whom you would like\n" +
                		"to share this info.");
            } else if (cmbShare.getSelectedItem().equals(SHARE_EMAIL_ADDRESS)) {
                AppMain.Prompt(AppMain.getApplet(), "Please enter the email address of the\n" +
                		"user with whom you would like to share\n" +
                		"this info.");
            } else if (cmbShare.getSelectedItem().equals(SHARE_TG_USER)) {
                AppMain.Prompt(AppMain.getApplet(), "Please enter the S3 ID of the user\n" +
                        "with whom you would like to share\n" +
                        "this info.");
            } else  {
                AppMain.Error(AppMain.getApplet(),"This can't happen.");
            }
        } 
    }

}


@SuppressWarnings({"serial","unchecked"})
class AccessControlListTableModel extends AbstractTableModel {

    public static final String [] TABLE_COLUMN_NAMES = 
        new String[]{"User Name","Read","Write","Full Control"};
    
    private List<GAPListItem> gapList = new ArrayList<GAPListItem>();
    
    public AccessControlListTableModel(AccessControlList acl) {
        
        for (GrantAndPermission gap: (Set<GrantAndPermission>)acl.getGrants()) {
            GAPListItem newGapItem = new GAPListItem(gap);
            
            int index = gapList.indexOf(newGapItem);
            if (index > -1) {
                gapList.get(index).addPermission(gap.getPermission());
            } else {
                gapList.add(newGapItem);
            }
        }
    }
    
    public void addElement(GAPListItem item) {
        gapList.add(item);
        sort();
        fireTableDataChanged();
    }
    
    public void removeElement(GAPListItem item) {
        int index = gapList.indexOf(item);
        gapList.remove(item);
        fireTableRowsDeleted(index,index);
    }
    
    public void removeElementAt(int row) {
        gapList.remove(row);
        fireTableRowsDeleted(row,row);
    }
    
    public String getColumnName(int column) {
        return TABLE_COLUMN_NAMES[column];
    }
    
    public int getColumnCount() {
       return TABLE_COLUMN_NAMES.length;
    }

    public int getRowCount() {
        return gapList.size();
    }

    public List<GAPListItem> getAllElements() {
        return gapList;
    }
    
    public GAPListItem getElementAtRow(int row) {
        return gapList.get(row);
    }
    public Object getValueAt(int row, int column) {
        Object value = null;
        
        GAPListItem gapItem = gapList.get(row); 
        
        switch(column){
        case 0:
            //Name
            value = gapItem.name;
            break;
        case 1:
            //Read permission
            value = gapItem.isReadable()?AppMain.icoGoTo:AppMain.icoStop;
            break;
        case 2:
            //Read permission
            value = gapItem.isWritable()?AppMain.icoGoTo:AppMain.icoStop;
            break;
        case 3:
            //Read permission
            value = gapItem.isFullControl()?AppMain.icoGoTo:AppMain.icoStop;
            break;
        default:
            break;
        }
        
        return value;
    }
    
    private void sort() {
        Collections.sort(gapList);
    }
    
}

@SuppressWarnings("unchecked")
class GAPListItem implements Comparable{
    public static final int READ = 0;
    public static final int WRITE = 1;
    public static final int FULL_CONTROL = 2;
    
    String name;
    List<Permission> permissions = new ArrayList<Permission>();
    GranteeInterface grant;
    public GAPListItem(GrantAndPermission gap) {
        grant = gap.getGrantee();
        name = grant.getIdentifier();
        permissions.add(gap.getPermission());
    }
    
    public void togglePermission(int col) {
        switch(col) {
            case READ:
                if (isReadable()) {
                    permissions.remove(Permission.PERMISSION_READ);
                } else {
                    permissions.add(Permission.PERMISSION_READ);
                }
                break;
            case WRITE:
                if (isWritable()) {
                    permissions.remove(Permission.PERMISSION_WRITE);
                } else {
                    permissions.add(Permission.PERMISSION_WRITE);
                }
                break;
            case FULL_CONTROL:
                if (isFullControl()) {
                    permissions.remove(Permission.PERMISSION_FULL_CONTROL);
                } else {
                    permissions.add(Permission.PERMISSION_FULL_CONTROL);
                }
                break;
            default:
        }
        
    }

    public GAPListItem(String name, Permission p) {
        this.name = name;
        permissions.add(p);
    }
    
    public void addPermission(Permission p) {
        if (!permissions.contains(p)) {
            permissions.add(p);
        }
    }
    
    public boolean isReadable() {
        return permissions.contains(Permission.PERMISSION_READ) ||
            permissions.contains(Permission.PERMISSION_FULL_CONTROL);
    }
    
    public boolean isWritable() {
        return permissions.contains(Permission.PERMISSION_WRITE) ||
            permissions.contains(Permission.PERMISSION_FULL_CONTROL);
    }
    
    public boolean isFullControl() {
        return permissions.contains(Permission.PERMISSION_FULL_CONTROL);
    }
    
    public boolean equals(Object o) {
        if (o instanceof GAPListItem) {
            return name.equals(((GAPListItem)o).name);
        }
        return false;
    }
    
    public int compareTo(Object arg0) {
        if (arg0 instanceof GAPListItem) {
            return name.compareTo(((GAPListItem)arg0).name);
        }
        return 0;
    }
}


