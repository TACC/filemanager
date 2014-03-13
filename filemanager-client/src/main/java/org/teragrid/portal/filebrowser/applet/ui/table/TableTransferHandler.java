package org.teragrid.portal.filebrowser.applet.ui.table;
 
import java.awt.Component;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.TransferHandler;
import javax.swing.tree.TreePath;

import org.globus.ftp.FileInfo;
import org.teragrid.portal.filebrowser.applet.transfer.FTPSettings;
import org.teragrid.portal.filebrowser.applet.ui.FileListingModel;
import org.teragrid.portal.filebrowser.applet.ui.IconData;
import org.teragrid.portal.filebrowser.applet.ui.PnlBrowse;
import org.teragrid.portal.filebrowser.applet.ui.TransferProxy;
import org.teragrid.portal.filebrowser.applet.util.LogManager;

 
@SuppressWarnings({"serial","unchecked","unused"})
public class TableTransferHandler extends TransferHandler {
 
    protected Point dragPoint;
    protected Point dropPoint;
    protected Component dragComponent;
    protected Component dropComponent;
    
//  PnlBrouse
    private PnlBrowse _pnlBrowse = null;

    private FTPSettings _ftpSite = null;

 // The type of DnD object being dragged...
    public static DataFlavor TREEPATH_FLAVOR = null;//new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType, "TreePath");
    public static DataFlavor SITE_FLAVOR = null;
    public static DataFlavor DIRECTORY_FLAVOR = null;
    public static DataFlavor SELECTED_FLAVOR = null;
    public static DataFlavor URILIST_FLAVOR = null;
    public static DataFlavor FILELIST_FLAVOR = null;
    
    private static String mimeType_Site = DataFlavor.javaJVMLocalObjectMimeType + ";class=" + FTPSettings.class.getName();
    private static String mimeType_Selected = DataFlavor.javaJVMLocalObjectMimeType + ";class=java.util.Vector";
    private static String mimeType_Path = DataFlavor.javaJVMLocalObjectMimeType + ";class=" + TreePath.class.getName();
    private static String mimeType_UriList = "text/uri-list";
    
    static{
        try {
    
            SITE_FLAVOR = new DataFlavor(mimeType_Site);
            DIRECTORY_FLAVOR = DataFlavor.stringFlavor;
            SELECTED_FLAVOR = new DataFlavor(mimeType_Selected);
            TREEPATH_FLAVOR = new DataFlavor(mimeType_Path);
            URILIST_FLAVOR = new DataFlavor(mimeType_UriList);
            FILELIST_FLAVOR = DataFlavor.javaFileListFlavor;
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    private String               _directory = null; //the ftp directory
    private List<DataFlavor>    _flavors = new ArrayList<DataFlavor>(); 
    private java.util.List       _lstFileInfo = new ArrayList(); //the drag file list
    private java.util.List       _lstFiles = new ArrayList(); //the drag file list
    private java.util.List       _lstURI = new ArrayList(); //the drag file list
    

    public TableTransferHandler(PnlBrowse pnlBrouse){
        super();

        this._pnlBrowse = pnlBrouse;
        this._ftpSite = pnlBrouse.getFtpServer();
        
        //this.directory = directory;
        _flavors.add(TREEPATH_FLAVOR);
        _flavors.add(SITE_FLAVOR);
        _flavors.add(DIRECTORY_FLAVOR);
        _flavors.add(SELECTED_FLAVOR);
        
//        if (_ftpSite.type == FTPType.FILE) {
//            // create a list of local uri's and file objects
//            for (FileInfo f: (List<FileInfo>)_lstFileInfo ) {
//                if (f.getName().equals(".") || f.getName().equals("..")) {
//                    continue;
//                }
//                String dir = _directory + File.separator + f.getName();
//                File fTrans = new File(dir);
//                _lstFiles.add(fTrans);
//                _lstURI.add(fTrans.toURI());
//            }
//        
//            // add the new data flavors only if the dnd is from a local listing
////            _flavors.add(URILIST_FLAVOR);
////            _flavors.add(FILELIST_FLAVOR);
//        }
    }
 
    public Point getDropPoint() {
        return dropPoint;
    }
 
    public void setDropPoint(Point dropPoint) {
        this.dropPoint = dropPoint;
    }
 
    public Component getDragComponent() {
        return dragComponent;
    }
 
    public void setDragComponent(Component dragComponent) {
        this.dragComponent = dragComponent;
    }
 
    public Point getDragPoint() {
        return dragPoint;
    }
 
    public void setDragPoint(Point dragPoint) {
        this.dragPoint = dragPoint;
    }
 
    public Component getDropComponent() {
        return dropComponent;
    }
 
    public void setDropComponent(Component dropComponent) {
        this.dropComponent = dropComponent;
    }
 
    public void exportAsDrag(JComponent comp, InputEvent e, int action) {
        setDragComponent(comp);
        setDragPoint(((MouseEvent) e).getPoint());
        super.exportAsDrag(comp, e, action);
    }
 
    protected Transferable createTransferable(JComponent c) {
        JTable tblListView = ((JTable)c);
        FileListingModel tableModel = (FileListingModel)tblListView.getModel();
        java.util.List fileList = tableModel.getFileList();
        //String directory = tableModel.getDirectory();
        String directory = _pnlBrowse.getCurrentDir();

        java.util.List selectedFileList = new ArrayList();

        int[] rows = _pnlBrowse.tblListing.getSelectedRows();
        
        for (int i=0;i<rows.length;i++) {
//        int[] nRows = getSelectedRows();
//        for (int i = 0, l = nRows.length; i < l; i++) {
//            if (0 == nRows[i]){
//              continue; //Don't operate in ".." folder
//            }
//            selectedFileList.add(fileList.get(nRows[i]));
//        }
            IconData data = (IconData)_pnlBrowse.tblListing.getValueAt(i,0);
            if (data.toString().equals("..") || data.toString().equals(_pnlBrowse.getCurrentDir())){
                continue; //Don't operate in ".." folder
            }

            Collections.addAll(selectedFileList, (List<FileInfo>)_pnlBrowse.getFileList().get(i));
        }
        LogManager.debug("This is a transfer from " + 
                _pnlBrowse.getFtpServer().host + "/" + directory + "/");
        
        return new TableTransferable(this._ftpSite, directory, selectedFileList);
    }

    public int getSourceActions(JComponent c) {
        return COPY;
    }

    //This method called when a drop event was caught by the table
    public boolean importData(JComponent c, Transferable t) {

        if (canImport(c, t.getTransferDataFlavors())) {
            try {
                //source ftp site
                FTPSettings sourceFTPSite = (FTPSettings)t.getTransferData(SITE_FLAVOR);
                //source directory
                String sourceDir = (String)t.getTransferData(DIRECTORY_FLAVOR);
                //file list
                java.util.List fileList = (java.util.List)t.getTransferData(SELECTED_FLAVOR);
                //destination directory-- this should pick up the component or table entry
                // on which the file was dropped
                String destDir = getDropDir(c);
                
                //destination directory file list
                
                java.util.List destFileList = null;
                
                if (c instanceof PnlBrowse) {
                    destFileList = ((PnlBrowse)c).getFileList();
                } else if (c instanceof JTable) {
                    destFileList = ((FileListingModel)((JTable)c).getModel()).getFileList();
                }

                TransferProxy.transfer(_pnlBrowse,fileList,sourceFTPSite,this._ftpSite,sourceDir,destDir,destFileList);
                
                return true;
            } catch (Exception e) {
//                System.out.println("Exception:");
                e.printStackTrace();
                LogManager.debug(e.getLocalizedMessage() + " at " + (e.getStackTrace())[0]);  
            }
        } else {
            LogManager.debug("could not import data from dnd event");
        }
        return false;
    }
    
    private String getDropDir(JComponent c) {
        String dropDir = _pnlBrowse.getCurrentDir();
        JTable view = _pnlBrowse.tblListing;
        
//        if (_pnlBrowse.getGridFileDisplay() instanceof CTable) {
//            CTable cTable = (CTable) view;
//            
//            
//        } else if (_pnlBrowse.getGridFileDisplay() instanceof CTree) {
//            CTree cTree = (CTree) view;
//            
//        }
        
        if (c instanceof CTable) {
            int row = ((CTable)c).getSelectedRow();
            FileInfo fileInfo = ((DetailListModel)((CTable)c).getModel()).getFile(row);
            if (fileInfo.isDirectory()) 
                dropDir += File.separator + fileInfo.getName();
        } 
        
        return dropDir;
    }
    
    
    protected void exportDone(JComponent c, Transferable data, int action) {
        try {
            LogManager.debug("Exported data from " + (FTPSettings)data.getTransferData(SITE_FLAVOR) + "\n "); 
                    
            java.util.List fileList = (java.util.List)data.getTransferData(SELECTED_FLAVOR);
            
            for (int i=0;i<fileList.size();i++) {
                FileInfo file = (FileInfo)fileList.get(i);
                System.out.println(data.getTransferData(DIRECTORY_FLAVOR) + 
                        File.separator + file.getName());
            }
            
            
        } catch (UnsupportedFlavorException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean canImport(JComponent c, DataFlavor[] flavors) {
        
        for (int i = 0; i < flavors.length; i++) {
            if (java.util.Arrays.asList(_flavors).contains(flavors[i])) {
            	
                return true;
            }
        }
        return false;
    }
 
}
