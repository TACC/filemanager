package edu.utexas.tacc.wcs.filemanager.client.ui.table;
 
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.tree.TreePath;

import org.globus.ftp.FileInfo;

import edu.utexas.tacc.wcs.filemanager.client.AppMain;
import edu.utexas.tacc.wcs.filemanager.client.transfer.FTPSettings;

 
public class TableTransferable implements Transferable {
    public FTPSettings ftpSite = null; //the drag ftp site
    public String directory = null; //the ftp directory
    public List<FileInfo> fileList = new ArrayList<FileInfo>(); //the drag file list

    public static DataFlavor DATAFLAVOR_SITE = null;
    public static DataFlavor DATAFLAVOR_DIR = null;
    public static DataFlavor DATAFLAVOR_SELECTED = null;
    
    Icon icon = null;
    
    static{
        try {
            DATAFLAVOR_SITE = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType + ";class=" + FTPSettings.class.getName());
            DATAFLAVOR_DIR = DataFlavor.stringFlavor;
            DATAFLAVOR_SELECTED = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType + ";class=java.util.Vector");
        }catch(Exception e){
            e.printStackTrace();
        }
    }
   

    public TableTransferable(FTPSettings ftpSite, String directory, List<FileInfo> fileList){
        this.ftpSite = ftpSite;
        this.directory = directory;
        this.fileList = fileList;
        this.icon = (fileList.size() > 0)? AppMain.icoFolder:AppMain.icoFile;
    }

    public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[]{DATAFLAVOR_SITE, DATAFLAVOR_DIR, DATAFLAVOR_SELECTED};
    }

    public boolean isDataFlavorSupported(DataFlavor flavor) {
        if(flavor == null) {
            return false;
        }

        if(flavor.equals(DATAFLAVOR_SITE) || flavor.equals(DATAFLAVOR_DIR) || flavor.equals(DATAFLAVOR_SELECTED)){
            return true;
        }

        return false;
    }

    public Object getTransferData(DataFlavor flavor) throws
            UnsupportedFlavorException, IOException {
        if(DATAFLAVOR_SITE.equals(flavor)) {
            return this.ftpSite;
        }
        if(DATAFLAVOR_DIR.equals(flavor)) {
            return this.directory;
        }
        if(DATAFLAVOR_SELECTED.equals(flavor)) {
            return this.fileList;
        }

        return null;
    }
    
    
}
