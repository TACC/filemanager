package org.teragrid.portal.filebrowser.applet.ui.table;
 
import java.awt.AlphaComposite;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceContext;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.dnd.InvalidDnDOperationException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.DropMode;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.TransferHandler;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.filechooser.FileSystemView;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.globus.ftp.FileInfo;
import org.teragrid.portal.filebrowser.applet.AppMain;
import org.teragrid.portal.filebrowser.applet.ConfigSettings;
import org.teragrid.portal.filebrowser.applet.transfer.FTPSettings;
import org.teragrid.portal.filebrowser.applet.ui.GridFileView;
import org.teragrid.portal.filebrowser.applet.ui.IconData;
import org.teragrid.portal.filebrowser.applet.ui.PnlBrowse;
import org.teragrid.portal.filebrowser.applet.ui.TransferProxy;
import org.teragrid.portal.filebrowser.applet.ui.permissions.StripedTable;
import org.teragrid.portal.filebrowser.applet.util.LogManager;

 
/**
 * Description of the class
 *
 * @author Dan Galitsky
 * @version 1.0
 */

@SuppressWarnings({"serial","unused","unchecked"})
public class CTablejdk5 extends StripedTable implements GridFileView, DragGestureListener, DragSourceListener {
    public static DataFlavor listFlavor = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType +
                                                           ";class=java.util.List", "List");
 
    public TableTransferHandler dndHandler;
    public boolean isDragged;
    public boolean bRemoteMode;
    private boolean enableMenu = true;
    private List fileList = null;
    private String dragnode;
    protected Rectangle2D     _raGhost        = new Rectangle2D.Float();
    protected Point             _ptOffset = new Point();    // Where, in the drag image, the mouse was clicked
    protected static BufferedImage buff;
    protected int _rowSource;                           // row being dragged
    private DropTarget _dropTarget;
    private DragSourceContext dsc;
    private DragSource dragSource;
    protected PnlBrowse frmParent = null;
    
    
    public CTablejdk5(PnlBrowse frmParent, boolean bRemoteMode) {
        super();
        
        this.frmParent = frmParent;
        this.bRemoteMode = bRemoteMode;
//        addMouseListener(this);
        
        // Make this JTree a drag source
        dragSource = DragSource.getDefaultDragSource();
        dragSource.createDefaultDragGestureRecognizer(this, DnDConstants.ACTION_COPY_OR_MOVE, this);

        // Also, make this JTree a drop target
        setDragEnabled(true);
        setDropMode(DropMode.ON_OR_INSERT);
        _dropTarget = new DropTarget(this, new CTableDropTargetListener());
        _dropTarget.setDefaultActions(DnDConstants.ACTION_COPY_OR_MOVE);
        setDropTarget(_dropTarget);
       
        
        setDropMode(DropMode.ON_OR_INSERT);
        
        setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        setIntercellSpacing(new Dimension(0, 0));
        setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        setRowHeight(20);
        setShowHorizontalLines(true);
        setShowVerticalLines(false);
        
    }  
    
    private void displayDropLocation(final String string) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JOptionPane.showMessageDialog(null, string);
            }
        });
    }
    
    public Hashtable<String,ArrayList<FileInfo>> getSelectedFileTable() {
        Hashtable<String,ArrayList<FileInfo>> table = new Hashtable<String,ArrayList<FileInfo>>();
        
        int[] nRows = getSelectedRows();

        ArrayList<FileInfo> fileList = new ArrayList<FileInfo>();
        
        for (int i = 0; i < nRows.length; i++) {
//            if (0 == nRows[i]) {
//                continue; //The selected row is the "..", just ignore!
//            }
            fileList.add((FileInfo)((DetailListModel)getModel()).getFileList().get(nRows[i]));
//            fileList.add((FileInfo)((IconData)((DetailListModel)getModel()).getFileList().get(nRows[i])).getObject());
        }

        if (!fileList.isEmpty()) {
        	table.put(frmParent.getCurrentDir(),fileList);
        }
        
        return table;
    }
    
    public FileInfo getSelectedFileInfo() {
    	int row = getSelectedRow();
    	if (row > -1) {
    		return (FileInfo)((DetailListModel)getModel()).getFileList().get(row);
    	} else {
    		return null;
    	}
    }
    
////  MouseListener Interface:
//    public void mouseClicked(MouseEvent e) {
//        
//        frmParent.tblListView_mouseClicked(e);
//        
//    }
//    
//	/**
//     * Check to see if the user right clicks with a single button mouse.
//     * This is needed for Mac laptops.
//     * 
//     * @param ev
//     * @return
//     */
//    private boolean isRightClickEvent(MouseEvent ev) {
//        int mask = InputEvent.BUTTON1_MASK - 1;
//        int mods = ev.getModifiers() & mask;
//        if (mods == 0) {
//            return false;
//        } else {
//            return true;
//        }
//        
//    }
//
//    public void mouseReleased(MouseEvent e) {}
//    public void mouseEntered(MouseEvent e) {}
//    public void mouseExited(MouseEvent e) {}
//    
 // Interface: DragGestureListener
    public void dragGestureRecognized(DragGestureEvent event) {
    	
        Hashtable tblSelected = getSelectedFileTable();
        
        if (tblSelected.isEmpty() || rowAtPoint(event.getDragOrigin()) == -1 || ((MouseEvent)event.getTriggerEvent()).isPopupTrigger()) return;
//        
        Transferable tt = new FileTransferable(frmParent.getFtpServer(), frmParent.getCurrentDir(), 
                (List)tblSelected.get(tblSelected.keySet().iterator().next()));
        
//          get the right icon
        Cursor cursor;
        FileSystemView fsv = FileSystemView.getFileSystemView();
        String file = ((IconData)getModel().getValueAt(getSelectedRow(), 0)).toString();
//        System.out.println(frmParent.getCurrentDir() + File.separator + file);
//            Icon icn = fsv.getSystemIcon(new File(currentDir + File.separator + file));
        Point p = event.getDragOrigin();
        IconData iconData = ((IconData)getModel().getValueAt(getSelectedRow(), 0));
        Icon icn = iconData.getIcon();
        JLabel label = (JLabel)getCellRenderer(getSelectedRow(), 0).getTableCellRendererComponent(
                this, iconData, false, true, getSelectedRow(), 0);
        label.setSize(new Dimension(175,25));
        label.setBorder(new EmptyBorder(0,0,0,0));
        
//          Get a buffered image of the selection for dragging a ghost image
        buff = new BufferedImage((int)label.getWidth(), (int)label.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = buff.createGraphics();

        // Ask the cell renderer to paint itself into the BufferedImage
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC, 0.5f));      // Make the image ghostlike
        label.paint(g2);

        // Now paint a gradient UNDER the ghosted JLabel text (but not under the icon if any)
        // Note: this will need tweaking if your icon is not positioned to the left of the text
//            Icon icon = label.getIcon();
//            int nStartOfText = (icon == null) ? 0 : icon.getIconWidth()+label.getIconTextGap();
//            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.DST_OVER, 0.5f)); // Make the gradient ghostlike
//            g2.setPaint(new GradientPaint(nStartOfText, 0, SystemColor.controlShadow, 
//                                          getWidth(),   0, new Color(255,255,255,0)));
//            g2.fillRect(nStartOfText, 0, getWidth(), buff.getHeight());

        g2.dispose();
        
//            label.setText((String)iconData.getObject());
//            label.setIcon(icn);
//            label.setSize(new Dimension(175,25));
//            
//            BufferedImage image = new BufferedImage(label.getWidth(), label.getHeight(), BufferedImage.TYPE_INT_ARGB);
//            Graphics g = image.getGraphics();
//            label.paint(g);
        
        // we could cast to an image icon, but it might not be one.
        // painting to a buffer first also solves the problem of passing in the
        // the right sized buffer because the cursor might scale it
        // convert to the right sized image
        Toolkit tk = Toolkit.getDefaultToolkit( );
        
        Point ptDragOrigin = event.getDragOrigin();
        _ptOffset.setLocation(ptDragOrigin.x-label.getWidth(), ptDragOrigin.y-label.getHeight());

        //            Dimension dim = tk.getBestCursorSize(
//                icn.getIconWidth(),icn.getIconHeight( ));
//            Image buff = new BufferedImage(dim.width,dim.height,
//                         BufferedImage.TYPE_INT_ARGB);
//            icn.paintIcon(tblListView,buff.getGraphics( ),0,0);

        // set up drag image
        try {
            if(DragSource.isDragImageSupported()) {
                event.startDrag(DragSource.DefaultCopyDrop, buff, new Point(12,0),
                            tt,
                            this);
            } else {
                cursor = tk.createCustomCursor(buff,new Point(0,0),"billybob");
                event.startDrag(cursor, null, new Point(12,0),
                        tt,
                        this);
            }
        } catch (InvalidDnDOperationException e) {
            LogManager.error("Multiple drags in process...");
        }
        List fList = getSelectedFileTable().get(frmParent.getCurrentDir());
        
        _rowSource = getSelectedRow();
        if(fList != null){
            dragnode = ((IconData)getModel().getValueAt(_rowSource, 0)).toString();
            try {
                dragSource.startDrag(event, DragSource.DefaultMoveDrop, buff, new Point(12,0), tt, this);
            } catch (InvalidDnDOperationException e) {}
        }
    }


 // Interface: DragSourceListener
    public void dragDropEnd(DragSourceDropEvent dsde) {
//    	LogManager.debug("Point of dsde is " + dsde.getLocation());
    	AppMain.getFrame().contains(dsde.getLocation());
    }

    public void dragExit(DragSourceEvent dse) {
    }

    public void dragOver(DragSourceDragEvent dsde) {
    }

    public void dropActionChanged(DragSourceDragEvent dsde) {
    }
    
    public void dragEnter(DragSourceDragEvent event){
        dsc = event.getDragSourceContext();
    }
    
    class ColumnListener extends MouseAdapter {

        public void mouseClicked(MouseEvent e) {
            TableColumnModel colModel = getColumnModel();
            int columnModelIndex = colModel.getColumnIndexAtX(e.getX());
            int modelIndex = colModel.getColumn(columnModelIndex).getModelIndex();
            DetailListModel m_data = ((DetailListModel)getModel());
            int colIndex = ((DetailListModel)getModel()).sortedColumnIndex;
            boolean sortAsc = ((DetailListModel)getModel()).sortAscending;
            
            if (modelIndex < 0)
                return;
            if (m_data.sortedColumnIndex == modelIndex)
                m_data.sortAscending = !m_data.sortAscending;
            else
                m_data.sortedColumnIndex = modelIndex;

            for (int i=0; i < m_data.getColumnCount(); i++) {
                TableColumn column = colModel.getColumn(i);
                int index = column.getModelIndex();
                JLabel renderer = (JLabel)column.getHeaderRenderer();
                renderer.setIcon(m_data.getColumnIcon(index));
            }
            getTableHeader().repaint();

            m_data.sortData();
            tableChanged(new TableModelEvent(m_data));
            repaint();
        }
    }
//        
////End of ListDNDTest
// 
////class TableDNDRecognizer extends MouseInputAdapter {
//// 
////    private boolean recognized;
////    protected Point pressedPoint;
//// 
////    public void mousePressed(MouseEvent e) {
////        pressedPoint = e.getPoint();
////    }
//// 
////    public void mouseDragged(MouseEvent e) {
////        O40Dools
////    }
//// 
////    public void mouseReleased(MouseEvent e) {
////        recognized = false;
////        CTable.isDragged = false;
////        pressedPoint = null;
////    }
////
////
////    
////}
//
//
class CTableDropTargetListener implements DropTargetListener, Serializable {
    
    private Point           _ptLast         = new Point();
    private int             _rowLast        = 0;
    private Timer           _timerHover;
    private boolean         canImport;

    public CTableDropTargetListener() {
        
        // Set up a hover timer, so that a node will be automatically expanded or collapsed
        // if the user lingers on it for more than a short time
        _timerHover = new Timer(1000, new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                expandNode(_rowLast);
            }
        });
        _timerHover.setRepeats(false);  // Set timer to one-shot mode
    }
    
    private void expandNode(int row) {
        
    }
    
    private boolean actionSupported(int action) {
        return (action & (DnDConstants.ACTION_COPY_OR_MOVE | DnDConstants.ACTION_LINK)) !=
               DnDConstants.ACTION_NONE;
    }

    // --- DropTargetListener methods -----------------------------------

    public void dragEnter(DropTargetDragEvent e) {
        
//        DataFlavor[] flavors = e.getCurrentDataFlavors();
//
//        JComponent c = (JComponent) e.getDropTargetContext().getComponent();
//        TransferHandler importer = c.getTransferHandler();
//
//        if (importer != null && importer.canImport(c, flavors)) {
//            canImport = true;
//        } else {
//            canImport = false;
//        }
//
//        int dropAction = e.getDropAction();
//
//        if (canImport && actionSupported(dropAction)) {
//            e.acceptDrag(dropAction);
//        } else {
//            e.rejectDrag();
//        }
        if (!isDragAcceptable(e))
            e.rejectDrag();
        else
            e.acceptDrag(e.getDropAction());
    }

    public void dragOver(DropTargetDragEvent e) {
        
        // Even if the mouse is not moving, this method is still invoked 10 times per second
        Point pt = e.getLocation();
        if (pt.equals(_ptLast))
            return;

        _ptLast = pt;
        
        int row = rowAtPoint(e.getLocation());
        
        int dropAction = e.getDropAction();
       
        if (!(row == _rowLast))           
        {
            _rowLast = row;
            _timerHover.restart();
        }
        
        Graphics2D g2 = (Graphics2D) getGraphics();

        // If a drag image is not supported by the platform, then draw my own drag image
        if (!DragSource.isDragImageSupported())
        {
            paintImmediately(_raGhost.getBounds()); // Rub out the last ghost image and cue line
            // And remember where we are about to draw the new ghost image
//            System.out.println("ghost image is " + _raGhost);
//            System.out.println("last point is " + pt);
//            System.out.println("offset is " + _ptOffset);
//            System.out.println("buffer is " + buff);
            
            _raGhost.setRect(pt.x - _ptOffset.x, pt.y - _ptOffset.y, buff.getWidth(), buff.getHeight());
            g2.drawImage(buff, AffineTransform.getTranslateInstance(_raGhost.getX(), _raGhost.getY()), null);
            
        }
                     

     // Do this if you want to prohibit dropping onto the drag source
        if (row == _rowSource)
            e.rejectDrag();
        else
            e.acceptDrag(e.getDropAction());
        
//        if (canImport && actionSupported(dropAction)) {
//            
//            getSelectionModel().setSelectionInterval(row, row);
//            
//            e.acceptDrag(dropAction);
//            
//        } else {
//            e.rejectDrag();
//        }
    }
    
    public void dragExit(DropTargetEvent e) {
//        System.out.println("Caught the drag exit event in the ctable");
        if (!DragSource.isDragImageSupported())
        {
            repaint(_raGhost.getBounds());              
        }
    }

//    public void drop(DropTargetDropEvent e) {
//        int dropAction = e.getDropAction();
//
//        JComponent c = (JComponent) e.getDropTargetContext().getComponent();
//        TableTransferHandler importer = (TableTransferHandler) c.getTransferHandler();
//
//        if (canImport && importer != null && actionSupported(dropAction)) {
//            e.acceptDrop(dropAction);
//
//            try {
//                Transferable t = e.getTransferable();
//                importer.setDropPoint(e.getLocation());
//                importer.setDropComponent(c);
//                System.out.println("Dropping ")
//                e.dropComplete(importer.importData(c, t));
//            } catch (RuntimeException re) {
//                e.dropComplete(false);
//            }
//        } else {
//            e.rejectDrop();
//        }
//    }

	public void drop(DropTargetDropEvent e) {
//        System.out.println("Caught the drop event in the ctable");
        
        if (!isDropAcceptable(e))
        {
            e.rejectDrop();
            return;
        }
        
        e.acceptDrop(e.getDropAction());
//        System.out.println("Drop occurred on an object of type: " + e.getDropTargetContext().getComponent().getClass().getName());
        
        Transferable transferable = e.getTransferable();
        
        List<DataFlavor> flavors = Arrays.asList(transferable.getTransferDataFlavors());
        
        if (transferable.isDataFlavorSupported(FileTransferable.SITE_FLAVOR)) {
            try {
                Point pt = e.getLocation();
                String strTarget = frmParent.getCurrentDir();
                FTPSettings svrTarget = frmParent.getFtpServer();
                int row = rowAtPoint(pt);
//                FileInfo fileTarget = ((DetailListModel)getModel()).getFile((row == -1)?0:row);
                
//                TreePath pathSource = (TreePath) transferable.getTransferData(FileTransferable.TREEPATH_FLAVOR);
                FTPSettings svrSource = (FTPSettings) transferable.getTransferData(FileTransferable.SITE_FLAVOR);
                List lstSource = (List) transferable.getTransferData(FileTransferable.SELECTED_FLAVOR);
                String strSource = (String) transferable.getTransferData(FileTransferable.DIRECTORY_FLAVOR);
                
//                LogManager("DROPPING: " + strSource);
                
//                  if (columnAtPoint(pt) == 0 && fileTarget.isDirectory()) // the mouse is hovering over a folder item
//                    TransferProxy.transfer(lstSource, svrSource, svrTarget, strSource,strTarget, fileList);
//                else // The mouse hovering anywhere else in the tree panel
                TransferProxy.transfer(frmParent,lstSource, svrSource, svrTarget, strSource, strTarget, frmParent.getFileList()); 
                
//            }
                    
            } catch (UnsupportedFlavorException ufe) {
	              LogManager.error(ufe.getMessage());
	              e.dropComplete(false);
	              return;
            } catch (IOException ioe) {
                LogManager.error(ioe.getMessage());
              	e.dropComplete(false);
              	return;
            } finally {
            	e.dropComplete(true);
            }
        
        }// otherwise, they did a drag and drop from the user's desktop
        else {
        	LogManager.debug("Dropped from a source outside the file manager. TYPE: " );
        	for(DataFlavor flavor: transferable.getTransferDataFlavors()) {
        		LogManager.debug(flavor.getHumanPresentableName());
        	}
        	
        	try {
        		DataFlavor uriListFlavor = new DataFlavor("text/uri-list;class=java.lang.String");
        		 Point pt = e.getLocation();
                 String strTarget = frmParent.getCurrentDir();
                 FTPSettings svrTarget = frmParent.getFtpServer();
                 int row = rowAtPoint(pt);

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
                   TransferProxy.transfer(frmParent,fileTable.get(dir), svrSource, svrTarget, dir, strTarget, frmParent.getFileList()); 
               } 
               
			} catch (UnsupportedFlavorException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			} catch (ClassNotFoundException e1) {
				e1.printStackTrace();
			} finally {
            	e.dropComplete(true);
            }
        }
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
    
    
    public void dropActionChanged(DropTargetDragEvent e) {
        if (!isDragAcceptable(e))
            e.rejectDrag();
        else
            e.acceptDrag(e.getDropAction());    
    }
    
    public boolean isDragAcceptable(DropTargetDragEvent e)
    {
        // Only accept COPY or MOVE gestures (ie LINK is not supported)
        if ((e.getDropAction() & DnDConstants.ACTION_COPY_OR_MOVE) == 0)
            return false;

        // Only accept this particular flavor
//        LogManager.debug("Dragged item has flavors:");
//        for (DataFlavor flavor: e.getCurrentDataFlavorsAsList()) {
//        	LogManager.debug(flavor.getDefaultRepresentationClassAsString());
//        }
        if (!(e.isDataFlavorSupported(FileTransferable.TREEPATH_FLAVOR) || 
        		e.isDataFlavorSupported(FileTransferable.FILELIST_FLAVOR)))
            return false;
            
            
        // Do this if you want to prohibit dropping onto the drag source...
        Point pt = e.getLocation();
        int row = rowAtPoint(pt);
        if (row == _rowSource )           
            return false;


            
/*              
        // Do this if you want to select the best flavor on offer...
        DataFlavor[] flavors = e.getCurrentDataFlavors();
        for (int i = 0; i < flavors.length; i++ )
        {
            DataFlavor flavor = flavors[i];
            if (flavor.isMimeTypeEqual(DataFlavor.javaJVMLocalObjectMimeType))
                return true;
        }
*/
        return true;
    }

    public boolean isDropAcceptable(DropTargetDropEvent e)
    {
        
        // Only accept COPY or MOVE gestures (ie LINK is not supported)
        if ((e.getDropAction() & DnDConstants.ACTION_COPY_OR_MOVE) == 0)
            return false;

        // Only accept this particular flavor   
        if (!(e.isDataFlavorSupported(FileTransferable.TREEPATH_FLAVOR) || 
        		e.isDataFlavorSupported(FileTransferable.FILELIST_FLAVOR)))
            return false;

            
        // Do this if you want to prohibit dropping onto the drag source...
        Point pt = e.getLocation();
        int row = rowAtPoint(pt);
        if (row == _rowSource) 
            return false;
        
//        FileInfo fileTarget = ((DetailListModel)getModel()).getFile((row == -1)?0:row);
//        String strTarget = frmParent.getCurrentDir();
//        String strSource = "";
//        
//        try {
//            strSource = (String) e.getTransferable().getTransferData(FileTransferable.DIRECTORY_FLAVOR);
//        } catch (UnsupportedFlavorException e1) {
//            
//            e1.printStackTrace();
//        } catch (IOException e1) {
//            
//            e1.printStackTrace();
//        }
//        
//        System.out.println("Drug from " + strSource + " to the " + ((fileTarget.isFile())?"file ":"folder ") + fileTarget.getName() + " at " + strTarget);
//        if (strTarget.equals(strSource) && !fileTarget.isDirectory()) {
//            return false;
//        }
            
/*              
        // Do this if you want to select the best flavor on offer...
        DataFlavor[] flavors = e.getCurrentDataFlavors();
        for (int i = 0; i < flavors.length; i++ )
        {
            DataFlavor flavor = flavors[i];
            if (flavor.isMimeTypeEqual(DataFlavor.javaJVMLocalObjectMimeType))
                return true;
        }
*/
        return true;
    }
}

}
@SuppressWarnings("serial")
class FileTableCellRenderer extends DefaultTableCellRenderer {
    private PnlBrowse browser = null;
    private JFileChooser fileChooser;
    
    public FileTableCellRenderer(PnlBrowse browser) {
        this.browser = browser;
        this.fileChooser = new JFileChooser();
    }
    
    public void setValue(Object value) {
        if (value instanceof IconData) {
            Icon icn = null;
            IconData c = (IconData) value;
            
            if (browser.getFtpServer().name.equals(ConfigSettings.RESOURCE_NAME_LOCAL)) {
                icn = fileChooser.getIcon(new File(browser.getCurrentDir() + File.separator + c.toString()));
//            } else if (browser.getFtpServer().name.equals(ConfigSettings.RESOURCE_NAME_TGSHARE)) {
//            	// TODO: use IconFactory to get overlay icon of shared file/folder 
//            	ImageIcon systemIcon = (ImageIcon)fileChooser.getIcon(new File(browser.getCurrentDir() + File.separator + c.toString()));
//            	icn = IconFactory.getOverlayIcon(browser.tblListing, systemIcon, AppMain.icoShareItem, SwingConstants.SOUTH_EAST);
            } else {
                icn = c.getIcon();
            }
            setIcon(icn);
            setText(c.toString());
        } else {
            if (value instanceof String) {
                if (((String)value).indexOf(" KB") > -1 || ((String)value).indexOf(" MB") > -1 || 
                        ((String)value).indexOf(" GB") > -1 || ((String)value).indexOf(" B") > -1 ) {
                    setHorizontalAlignment(JLabel.RIGHT);
                }
            }
            super.setValue(value);
        }
    }
}