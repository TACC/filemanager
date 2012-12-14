package org.teragrid.portal.filebrowser.applet.ui.table;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.TransferHandler;

import org.globus.ftp.FileInfo;
import org.teragrid.portal.filebrowser.applet.AppMain;
import org.teragrid.portal.filebrowser.applet.transfer.FTPSettings;
import org.teragrid.portal.filebrowser.applet.ui.PnlBrowse;
import org.teragrid.portal.filebrowser.applet.ui.TransferProxy;
import org.teragrid.portal.filebrowser.applet.util.FileUtils;
import org.teragrid.portal.filebrowser.applet.util.LogManager;

public class FileTransferHandler extends TransferHandler {
	
	private CTable cTable;

	public FileTransferHandler(CTable cTable) {
		super();
		this.cTable = cTable;
	}


	/* (non-Javadoc)
	 * @see javax.swing.TransferHandler#canImport(javax.swing.TransferHandler.TransferSupport)
	 */
	@Override
	public boolean canImport(TransferSupport support) {
	    // we don't support pasting
	    if (!support.isDrop()) {
	        return false;
	    }
	    
	    // we only support importing our custom transfer falvor
	    for(DataFlavor flavor: support.getTransferable().getTransferDataFlavors()) {
	    	if (!support.isDataFlavorSupported(flavor)) {
	    		return false;
	    	}
        }
	    
        if (support.getTransferable() instanceof TableTransferable) {
        	return (((TableTransferable)support.getTransferable()).ftpSite != cTable.frmParent.getFtpServer());
        }
            
        // Do this if you want to prohibit dropping onto the drag source...
        support.setShowDropLocation(false);
	    // fetch the drop location (it's a JTree.DropLocation for JTree)
//	    JTable.DropLocation dl = (JTable.DropLocation)info.getDropLocation();
//	    return dl.getRow() == -1 || (dl.getRow() == .get)
        return true;
	}
	
	@Override
	public int getSourceActions(JComponent c) {
		return TransferHandler.COPY;
	}

	
	@Override
	public Icon getVisualRepresentation(Transferable t) {
		return FileUtils.getLocalFileIcon(((TableTransferable)t).fileList.get(0));
	}

	/* (non-Javadoc)
	 * @see javax.swing.TransferHandler#importData(javax.swing.TransferHandler.TransferSupport)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean importData(TransferSupport support) {
		if (!canImport(support))
	    {
	        return false;
	    }
		
		Transferable transferable = support.getTransferable();
		
		//List<DataFlavor> flavors = Arrays.asList(transferable.getTransferDataFlavors());
        
        if (transferable.isDataFlavorSupported(TableTransferable.DATAFLAVOR_SELECTED)) {
            try {
                String strTarget = cTable.frmParent.getCurrentDir();
                FTPSettings svrTarget = cTable.frmParent.getFtpServer();
                
                FTPSettings svrSource = (FTPSettings) transferable.getTransferData(TableTransferable.DATAFLAVOR_SITE);
                List<FileInfo> lstSource = (List<FileInfo>) transferable.getTransferData(TableTransferable.DATAFLAVOR_SELECTED);
                String strSource = (String) transferable.getTransferData(TableTransferable.DATAFLAVOR_DIR);
                
                TransferProxy.transfer(cTable.frmParent,lstSource, svrSource, svrTarget, strSource, strTarget, cTable.frmParent.getFileList()); 
                
            } catch (Exception e) {
            	AppMain.Error(cTable, e.getMessage());
            	return false;
            }
        
        }// otherwise, they did a drag and drop from the user's desktop
        else {
        	LogManager.debug("Dropped from a source outside the file manager. TYPE: " );
        	for(DataFlavor flavor: transferable.getTransferDataFlavors()) {
        		LogManager.debug(flavor.getHumanPresentableName());
        	}
        	try {
        		 DataFlavor uriListFlavor = new DataFlavor("text/uri-list;class=java.lang.String");
        		 Point pt = support.getDropLocation().getDropPoint();
                 String strTarget = cTable.frmParent.getCurrentDir();
                 FTPSettings svrTarget = cTable.frmParent.getFtpServer();
                 //int row = cTable.rowAtPoint(pt);

                 FTPSettings svrSource = FTPSettings.Local;
                 
                 Hashtable<String,ArrayList<FileInfo>> fileTable = null;
                 if (transferable.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                	 List files = (List)transferable.getTransferData(DataFlavor.javaFileListFlavor);
                 	 // convert from files to fileInfo grouped by source directory
                	 fileTable = createFileTable(files);
                 } else if (transferable.isDataFlavorSupported(uriListFlavor)) {
                	 String data = (String)transferable.getTransferData(uriListFlavor);
                	 fileTable = createFileTable(data);
                 }
               
               for (String dir: fileTable.keySet()) {
                   TransferProxy.transfer(cTable.frmParent,fileTable.get(dir), svrSource, svrTarget, dir, strTarget, cTable.frmParent.getFileList()); 
               }

        	} catch (Exception e) {
            	AppMain.Error(cTable, e.getMessage());
            	return false;
            }
        }
        return true;
	}

	 private Hashtable<String,ArrayList<FileInfo>> createFileTable(List<File> files) {
	        Hashtable<String,ArrayList<FileInfo>> table = new Hashtable<String,ArrayList<FileInfo>>();
	        
	        for (File file: files) {
	            if (table.keySet().contains(file.getParent())) {
	                // add the new fileinfo object to the list of files in this directory key
	                FileInfo fileInfo = new FileInfo();
	                fileInfo.setName(file.getName());
	                fileInfo.setFileType(file.isDirectory()?FileInfo.DIRECTORY_TYPE:FileInfo.FILE_TYPE);
	                fileInfo.setSize(file.length());
	                table.get(file.getParent()).add(fileInfo);
	            } else {
	                ArrayList<FileInfo> fileList = new ArrayList<FileInfo>();
	                FileInfo fileInfo = new FileInfo();
	                fileInfo.setName(file.getName());
	                fileInfo.setFileType(file.isDirectory()?FileInfo.DIRECTORY_TYPE:FileInfo.FILE_TYPE);
	                fileInfo.setSize(file.length());
	                fileList.add(fileInfo);
	                table.put(file.getParent(), fileList);
	            }
	        }
	        
	        return table;
	    }
	    
	    private Hashtable<String,ArrayList<FileInfo>> createFileTable(String data) {
	        Hashtable<String,ArrayList<FileInfo>> table = new Hashtable<String,ArrayList<FileInfo>>();
	        
	        for (java.util.StringTokenizer st = new java.util.StringTokenizer(data, "\r\n");
	                st.hasMoreTokens();) {
	            String s = st.nextToken();
	            if (s.startsWith("#")) {
	                // the line is a comment (as per the RFC 2483)
	                continue;
	            }
	            try {
	                java.net.URI uri = new java.net.URI(s);
	                java.io.File file = new java.io.File(uri);
	                
	                if (table.keySet().contains(file.getParent())) {
	                    // add the new fileinfo object to the list of files in this directory key
	                    FileInfo fileInfo = new FileInfo();
	                    fileInfo.setName(file.getName());
	                    fileInfo.setFileType(file.isDirectory()?FileInfo.DIRECTORY_TYPE:FileInfo.FILE_TYPE);
	                    fileInfo.setSize(file.length());
	                    table.get(file.getParent()).add(fileInfo);
	                } else {
	                    ArrayList<FileInfo> fileList = new ArrayList<FileInfo>();
	                    FileInfo fileInfo = new FileInfo();
	                    fileInfo.setName(file.getName());
	                    fileInfo.setFileType(file.isDirectory()?FileInfo.DIRECTORY_TYPE:FileInfo.FILE_TYPE);
	                    fileInfo.setSize(file.length());
	                    fileList.add(fileInfo);
	                    table.put(file.getParent(), fileList);
	                }
	                
	            } catch (java.net.URISyntaxException e) {
	                // malformed URI
	            } catch (IllegalArgumentException e) {
	                // the URI is not a valid 'file:' URI
	            }
	        }
	        
	        return table;
	    }

	/* (non-Javadoc)
	 * @see javax.swing.TransferHandler#createTransferable(javax.swing.JComponent)
	 */
	@Override
	protected Transferable createTransferable(JComponent c) {
		PnlBrowse pnlBrowse = cTable.frmParent;
		return new TableTransferable(pnlBrowse.getFtpServer(), pnlBrowse.getCurrentDir(), cTable.getSelectedFiles());
	}

	@Override
	protected void exportDone(JComponent c, Transferable t, int act) {
		if (act == TransferHandler.MOVE) {
			cTable.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}
   }

}
