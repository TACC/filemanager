/* 
 * Created on Dec 19, 2007
 * 
 * Developed by: Rion Dooley - dooley [at] tacc [dot] utexas [dot] edu
 * 				 TACC, Texas Advanced Computing Center
 * 
 * https://www.tacc.utexas.edu/
 */

package org.teragrid.portal.filebrowser.applet.ui.table;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.tree.TreePath;

import org.globus.ftp.FTPClient;
import org.globus.ftp.FileInfo;
import org.globus.io.streams.GlobusFileInputStream;
import org.globus.io.streams.GlobusInputStream;
import org.teragrid.portal.filebrowser.applet.transfer.FTPSettings;
import org.teragrid.portal.filebrowser.applet.transfer.streams.BBFtpInputStream;
import org.teragrid.portal.filebrowser.applet.transfer.streams.FTPInputStream;
import org.teragrid.portal.filebrowser.applet.transfer.streams.GridFTPInputStream;
import org.teragrid.portal.filebrowser.applet.transfer.streams.HTTPInputStream;
import org.teragrid.portal.filebrowser.applet.transfer.streams.IRODSInputStream;
import org.teragrid.portal.filebrowser.applet.transfer.streams.S3InputStream;
import org.teragrid.portal.filebrowser.applet.transfer.streams.SFTPInputStream;

import edu.utexas.tacc.wcs.filemanager.common.model.enumeration.FileProtocolType;

@SuppressWarnings({"rawtypes","unchecked"})
public class FileTransferable implements Transferable
{
    // The type of DnD object being dragged...
    public static DataFlavor TREEPATH_FLAVOR = null;//new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType, "TreePath");
    public static DataFlavor SITE_FLAVOR = null;
    public static DataFlavor DIRECTORY_FLAVOR = null;
    public static DataFlavor SELECTED_FLAVOR = null;
    public static DataFlavor URILIST_FLAVOR = null;
    public static DataFlavor FILELIST_FLAVOR = null;
    public static DataFlavor INPUTSTREAM_FLAVOR = null;
    
    private static String mimeType_Site = DataFlavor.javaJVMLocalObjectMimeType + ";class=" + FTPSettings.class.getName();
    private static String mimeType_Selected = DataFlavor.javaJVMLocalObjectMimeType + ";class=java.util.Vector";
    private static String mimeType_Path = DataFlavor.javaJVMLocalObjectMimeType + ";class=" + TreePath.class.getName();
    private static String mimeType_UriList = "text/uri-list";
    private static String mimeType_InputStream = DataFlavor.javaJVMLocalObjectMimeType + ";class=" + java.io.InputStream.class.getName();
    
    static{
        try {
    
            SITE_FLAVOR = new DataFlavor(mimeType_Site);
            DIRECTORY_FLAVOR = DataFlavor.stringFlavor;
            SELECTED_FLAVOR = new DataFlavor(mimeType_Selected);
            TREEPATH_FLAVOR = new DataFlavor(mimeType_Path);
            URILIST_FLAVOR = new DataFlavor(mimeType_UriList);
            FILELIST_FLAVOR = DataFlavor.javaFileListFlavor;
            INPUTSTREAM_FLAVOR = new DataFlavor(mimeType_InputStream);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public FTPSettings          _ftpSite = null; //the drag ftp site
    public String               _directory = null; //the ftp directory
	public List       			_lstFileInfo = new ArrayList(); //the drag file list
    public List       			_lstFiles = new ArrayList(); //the drag file list
    public List	     		  	_lstURI = new ArrayList(); //the drag file list
    
    private TreePath            _path;

    private List<DataFlavor>    _flavors = new ArrayList<DataFlavor>(); 
                            
    
    /**
    * Constructs a transferrable tree path object for the specified path.
    */
    public FileTransferable(TreePath path,
                            FTPSettings ftpSite, 
                            String directory, 
                            java.util.List fileList)
    {
        _path = path;
        
        _ftpSite = ftpSite;
        _directory = directory;
        _lstFileInfo = fileList;
        
        _flavors.add(TREEPATH_FLAVOR);
        _flavors.add(SITE_FLAVOR);
        _flavors.add(DIRECTORY_FLAVOR);
        _flavors.add(SELECTED_FLAVOR);
        _flavors.add(INPUTSTREAM_FLAVOR);
        _flavors.add(FILELIST_FLAVOR);
        
        if (ftpSite.protocol.equals(FileProtocolType.FILE)) {
            // create a list of local uri's and file objects
            for (FileInfo f: (List<FileInfo>)_lstFileInfo ) {
                if (f.getName().equals(".") || f.getName().equals("..")) {
                    continue;
                }
                String dir = directory + File.separator + f.getName();
                File fTrans = new File(dir);
                _lstFiles.add(fTrans);
                _lstURI.add(fTrans.toURI());
            }
        
            // add the new data flavors only if the dnd is from a local listing
            _flavors.add(URILIST_FLAVOR);
            _flavors.add(FILELIST_FLAVOR);
        }
    }
    
    /**
     * Constructs a transferrable file path object for the specified path.
     */
     public FileTransferable(FTPSettings ftpSite, 
                             String directory, 
                             java.util.List fileList)
     {
         this(null,ftpSite,directory,fileList);
     }
    
    // Transferable interface methods...
    public DataFlavor[] getTransferDataFlavors()
    {
        return _flavors.toArray(new DataFlavor[_flavors.size()]);
    }
    
    public boolean isDataFlavorSupported(DataFlavor flavor)
    {
        return _flavors.contains(flavor);
    }
    
    public synchronized Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException
    {
        if (TREEPATH_FLAVOR.equals(flavor)) { // DataFlavor.javaJVMLocalObjectMimeType))
        	return _path;
        }
        else if(SITE_FLAVOR.equals(flavor)) {
            return _ftpSite;
        }
        else if(DIRECTORY_FLAVOR.equals(flavor)) {
        	// Called when dropping to the local desktop...we could try: 
        	// create the local file
        	// start a download of the file to the local file in a separate thread
        	// returns from the _directory of the local file
        	// of the remote file LogManager.debug("Transfer data was requested.");
//        	LogManager.debug("Transfer data was requested as a directory.");
//        	try {
//        		LogManager.debug("Transfer data was requested.");
//				SHGridFTP conn = new SHGridFTP(_ftpSite);
//				FileTransferTask fileTask = new FileTransferTask(_lst
//				BatchTransfer(null,conn,)
//				return getInputStream(conn.getFtpClient());
//			} catch (Exception e) {
//				LogManager.error("Failed to connect to remote resource.",e);
//				throw new UnsupportedFlavorException(flavor);
//			} 
			
        	return _directory;
        }
        else if(SELECTED_FLAVOR.equals(flavor)) {
            return _lstFileInfo;
        }
        else if(URILIST_FLAVOR.equals(flavor)) {
            return _lstURI;
        }
        else if(FILELIST_FLAVOR.equals(flavor)) {
//        	LogManager.debug("Transfer data was requested as a filelist.");
            return _lstFiles;
        } else
            throw new UnsupportedFlavorException(flavor);
         
    }

    
	 /**
    * Returns input stream based on the source url
    */
   protected GlobusInputStream getInputStream(FTPClient client)
       throws Exception {

	   GlobusInputStream in = null;

       switch(_ftpSite.protocol) {
       case GRIDFTP:
            in = new GridFTPInputStream(client, _directory, _ftpSite.passiveMode);
            break;
        case BBFTP:
            in = new BBFtpInputStream();
            break;
        case SFTP:
            in = new SFTPInputStream();
            break;
        case FTP:
            in = new FTPInputStream(client, _directory, _ftpSite.passiveMode);
            break;
        case FILE:
            in = new GlobusFileInputStream(_directory);
            break;
        case HTTP:
            in = new HTTPInputStream(client,_directory);
            break;
        case S3:
            in = new S3InputStream(client,_directory);
            break;
//        case TGSHARE:
//            in = new TGShareInputStream(client,_directory);
//            break;
        case IRODS:
            in = new IRODSInputStream(client,_directory);
            break;
        default:

            break;
        }
       return in;
   }
    
}
    
