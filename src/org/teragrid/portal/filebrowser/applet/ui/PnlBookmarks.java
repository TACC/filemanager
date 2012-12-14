/* 
 * Created on Aug 16, 2008
 * 
 * Developed by: Rion Dooley - dooley [at] tacc [dot] utexas [dot] edu
 * 				 TACC, Texas Advanced Computing Center
 * 
 * https://www.tacc.utexas.edu/
 */

package org.teragrid.portal.filebrowser.applet.ui;

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
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;

import org.teragrid.portal.filebrowser.applet.AppMain;
import org.teragrid.portal.filebrowser.applet.ConfigOperation;
import org.teragrid.portal.filebrowser.applet.ui.permissions.StripedTable;
import org.teragrid.portal.filebrowser.applet.util.LogManager;

import com.explodingpixels.macwidgets.IAppWidgetFactory;

/**
 * Panel to hold user bookmark selections on a per-resource basis.
 * 
 * @author Rion Dooley < dooley [at] tacc [dot] utexas [dot] edu >
 *
 */
@SuppressWarnings({"serial","unused","unchecked"})
public class PnlBookmarks extends JPanel implements ClipboardOwner {
    
    private static DataFlavor BOOKMARK_FLAVOR = new DataFlavor(Bookmark.class,DataFlavor.javaSerializedObjectMimeType);
    
    private StripedTable tblBookmarks;
    
    private JScrollPane spBookmarks = new JScrollPane();
    
    private JPanel pnlButtons = new JPanel();
    
    private BookmarkTableModel mdlBookmarks;
    
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
    
    private boolean editingBookmark = false;
    
    private PnlBrowse parent = null;
    
    public PnlBookmarks(PnlBrowse pnlBrowse, List<Bookmark> bookmarks) {
        this(bookmarks);
        
        this.parent = pnlBrowse;
        
    }
    
    public PnlBookmarks(List<Bookmark> bookmarks) {
        super();
        
        pbInit(bookmarks);
        
    }

    private void pbInit(List<Bookmark> bookmarks) {
        
        // initialize the components
        initBookmarkTable(bookmarks);
        
        initPopupMenu();
        
        initButtonPanel();
        
        // layout the panel
        spBookmarks.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        spBookmarks.getViewport().add(tblBookmarks);
        spBookmarks.addMouseListener(new BookmarkTableMouseAdapter());
        spBookmarks.getViewport().setBackground(getBackground());
        IAppWidgetFactory.makeIAppScrollPane(spBookmarks);
        
        setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
        add(spBookmarks,BorderLayout.CENTER);
        add(pnlButtons,BorderLayout.PAGE_END);
        
    }
    
    private void initBookmarkTable(List<Bookmark> bookmarks) {
        
        mdlBookmarks = new BookmarkTableModel(bookmarks);
        mdlBookmarks.addTableModelListener(new BookmarkTableModelListener());
        
        tblBookmarks = new StripedTable(mdlBookmarks) {
            public boolean editCellAt(int row, int col) {
                editingBookmark = true;
                mdlBookmarks.setEditableRow(row);
                return super.editCellAt(row, col);
            }
        };
        
        tblBookmarks.addMouseListener(new BookmarkTableMouseAdapter());
//        tblBookmarks.setBackground(getBackground());
        
        tblBookmarks.setMinimumSize(new Dimension(100,100));
        tblBookmarks.setMinimumSize(new Dimension(200,300));
        tblBookmarks.setMaximumSize(new Dimension(Integer.MAX_VALUE,Integer.MAX_VALUE));
        
        
    }
    
    private JPopupMenu initPopupMenu() {
        
        rightClickPopup = new JPopupMenu();
        
        mnuCopy = new JMenuItem(new PopupAction("Copy","Copy",KeyEvent.VK_C));
        mnuPaste = new JMenuItem(new PopupAction("Paste","Paste",KeyEvent.VK_P));
        mnuOpen = new JMenuItem(new PopupAction("Open","Open",KeyEvent.VK_O));
//        mnuInfo = new JMenuItem(new PopupAction("Info","Info",KeyEvent.VK_I));
        mnuEdit = new JMenuItem(new PopupAction("Edit","Edit",KeyEvent.VK_E));
        mnuAdd = new JMenuItem(new PopupAction("Add","Add",KeyEvent.VK_N));
        mnuDelete = new JMenuItem(new PopupAction("Delete","Delete",KeyEvent.VK_D));
        
        rightClickPopup.add(mnuOpen);
        rightClickPopup.add(mnuCopy);
        rightClickPopup.add(mnuPaste);
        rightClickPopup.add(mnuEdit);
        rightClickPopup.addSeparator();
//        rightClickPopup.add(mnuInfo);
        rightClickPopup.add(mnuAdd);
        rightClickPopup.add(mnuDelete);
        
        return rightClickPopup;
    }
    
    private void initButtonPanel() {
        // buttons like menu items have shortcuts
        btnAdd = new JButton(new PopupAction("",AppMain.icoAdd,"Add",KeyEvent.VK_N));
        btnAdd.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnDelete = new JButton(new PopupAction("",AppMain.icoRemove,"Delete",KeyEvent.VK_D));
        btnDelete.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // layout with apple list appearance
        pnlButtons.setLayout(new BoxLayout(pnlButtons,BoxLayout.X_AXIS));
        pnlButtons.add(btnAdd,BorderLayout.LINE_START);
        pnlButtons.add(btnDelete);
        pnlButtons.add(Box.createHorizontalGlue());
    }
    
    private Bookmark getSelectedBookmark() {
        if (tblBookmarks.getSelectionModel().isSelectionEmpty()) {
            return null;
        } else {
            return ((BookmarkTableModel)tblBookmarks.getModel()).getBookmarkAt(tblBookmarks.getSelectedRow());
        }
    }
    
    private void copyBookmark(Bookmark bookmark) {
        if (bookmark != null) {
            BookmarkTransferable bt = new BookmarkTransferable(bookmark);
            systemClipboard.setContents(bt,this);
        }
    }
    
    public List<Bookmark> getBookmarkTableList() {
        return mdlBookmarks.bookmarks;
    }
    
    private void pasteBookmark() {
        Bookmark bookmark = new Bookmark();
        try {
            if (systemClipboard.isDataFlavorAvailable(BOOKMARK_FLAVOR)) {
                bookmark.setName(((Bookmark)systemClipboard.getData(BOOKMARK_FLAVOR)).getName());
                bookmark.setPath(((Bookmark)systemClipboard.getData(BOOKMARK_FLAVOR)).getPath());
            } else {
                if (systemClipboard.isDataFlavorAvailable(DataFlavor.getTextPlainUnicodeFlavor())) {
                    bookmark.setPath((String)systemClipboard.getData(DataFlavor.getTextPlainUnicodeFlavor()));
                } else if (systemClipboard.isDataFlavorAvailable(DataFlavor.stringFlavor)) {
                    bookmark.setPath((String)systemClipboard.getData(DataFlavor.stringFlavor));
                }
                
                if (bookmark.getPath().indexOf("/") > -1) {
                    bookmark.setName(bookmark.getPath().substring(bookmark.getPath().lastIndexOf("/")));
                } else if (bookmark.getPath().indexOf("\\") > -1) {
                    bookmark.setName(bookmark.getPath().substring(bookmark.getPath().lastIndexOf("\\")));
                } else {
                    bookmark.setName("Bookmark" + mdlBookmarks.getRowCount());
                }
            }
        } catch (Exception e) {
            LogManager.error("Failed to paste bookmark from clipboard.",e);
            return;
        }
        mdlBookmarks.addBookmark(bookmark);
        
    }
    
    
    public void mnuPopup_actionPerformed(ActionEvent e) {
       if (e.getActionCommand().compareTo("Copy")==0) {
           copyBookmark(getSelectedBookmark());
       } else if (e.getActionCommand().compareTo("Paste")==0) {
           pasteBookmark();
       } else if (e.getActionCommand().compareTo("Open")==0) {
           Bookmark bookmark = getSelectedBookmark();
           if (bookmark != null)
               parent.tBrowse.cmdAdd("Cwd",bookmark.getPath(),null);
       } else if (e.getActionCommand().compareTo("Info")==0) {
//            not implemented...doesn't make sense.
       } else if (e.getActionCommand().compareTo("Edit")==0) {
           int row = tblBookmarks.getSelectedRow();
           int col = tblBookmarks.getSelectedColumn();
           tblBookmarks.editCellAt(row, col);
           focusEditingCell(row,col);
       } else if (btnAdd == e.getSource() || e.getActionCommand().compareTo("Add")==0) {
           int newRow = tblBookmarks.getRowCount();
           mdlBookmarks.addBookmark(new Bookmark("Bookmark" + (newRow+1),"$HOME"));//parent.getCurrentDir()));
           tblBookmarks.setRowSelectionInterval(newRow, newRow);
           tblBookmarks.editCellAt(newRow, 0);
           focusEditingCell(newRow,0);
       } else if (btnDelete == e.getSource() || e.getActionCommand().compareTo("Delete")==0) {
           mdlBookmarks.removeBookmarkAt(tblBookmarks.getSelectedRow());
       }
       
    }
    
    private void focusEditingCell(int row, int col) {
        JTextField txtField = ((JTextField)((DefaultCellEditor)tblBookmarks.getCellEditor(row, col))
                .getTableCellEditorComponent(tblBookmarks, 
                        (String)mdlBookmarks.getValueAt(row,col), 
                        true, 
                        row, 
                        col));
        txtField.requestFocus();
        txtField.selectAll();
    }
    
    public void lostOwnership(Clipboard clipboard, Transferable transferable) {}
        
    private void showPopupMenu(Point p) {
        
        Bookmark bookmark = getSelectedBookmark();
        
        // enable paste if a Bookmark is on the clipboard
        mnuPaste.setVisible(systemClipboard.isDataFlavorAvailable(BOOKMARK_FLAVOR));
        
        // disable bookmark specific actions if no
        // bookmark is selected.
        boolean isSelected = (bookmark != null);
        mnuCopy.setVisible(isSelected);
        mnuOpen.setVisible(isSelected);
        mnuEdit.setVisible(isSelected);
        
        rightClickPopup.show(tblBookmarks,p.x,p.y);
    }
    
    protected void addBookmark(Bookmark bookmark) {
        mdlBookmarks.addBookmark(bookmark);
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

    class BookmarkTableMouseAdapter extends MouseAdapter {
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
            
            if (editingBookmark && lastRow != tblBookmarks.getSelectedRow()) {
                editingBookmark = false;
                mdlBookmarks.setEditableRow(-1);
            }
            
            lastRow = tblBookmarks.getSelectedRow();
            
            if (isRightClickEvent(e)) {
                int clickedRow = tblBookmarks.rowAtPoint(e.getPoint());
                int clickedCol = tblBookmarks.columnAtPoint(e.getPoint());
                // could be clicking on empty table area
                if (clickedRow >=0) {
                    tblBookmarks.setRowSelectionInterval(clickedRow, clickedRow);
                    tblBookmarks.setColumnSelectionInterval(clickedCol, clickedCol);
                } else 
                    tblBookmarks.clearSelection();
                
                showPopupMenu(e.getPoint());
            }
            else if(e.getClickCount() == 2) {
                Bookmark bookmark = getSelectedBookmark();
                if (bookmark != null) {
                    parent.btnBookmarks.doClick();
                    parent.tblListing.requestFocusInWindow();
                    parent.tBrowse.cmdAdd("Cwd",bookmark.getPath(),null);
                    
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
        
    class BookmarkTableModelListener implements TableModelListener {
        
        public void tableChanged(TableModelEvent e) {
            int row = e.getFirstRow();
//            int column = e.getColumn();
            BookmarkTableModel model = (BookmarkTableModel)e.getSource();
//            Object data = model.getValueAt(row, column);
             if (e.getType() == TableModelEvent.UPDATE) {
                //now you have the data in the cell and the place in the grid where the 
                //cell is so you can use the data as you want
                ConfigOperation.getInstance().updateBookmark(parent.ftpServer.host,model.getBookmarkAt(row));
             } else if (e.getType() == TableModelEvent.INSERT) {
                 ConfigOperation.getInstance().addBookmark(parent.ftpServer.host,model.getBookmarkAt(row));
             }
        }
     
    }
    
    class BookmarkTableModel extends AbstractTableModel {
        private String [] columns = new String[]{"Name","Date Modified"};
        
        private List<Bookmark> bookmarks = new ArrayList<Bookmark>();
        
        private int editableRow = -1;
        
        public BookmarkTableModel() {
            bookmarks = new ArrayList<Bookmark>();
        }
        
        public BookmarkTableModel(List<Bookmark> bookmarks) {
            if (bookmarks!=null)
                this.bookmarks = bookmarks;
        }
        
        public String getColumnName(int col) {
            return columns[col].toString();
        }

        public int getColumnCount() {
            return columns.length;
        }

        public int getRowCount() {
            return bookmarks.size();
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
            if(row>=bookmarks.size()) {
                return null;
            }
            
            switch(column){
            case 0:
                //Name
                value = bookmarks.get(row).getName();
                break;
            case 1:
                //Path
                value = bookmarks.get(row).getPath();
                break;
            default:
                value = "";
                break;
            }
            return value;
        }
        
        public void setValueAt(Object value, int row, int col) {
            
            Bookmark b = bookmarks.get(row);
            try {
                switch(col) {
                case 0:
                    try {
                        b.setName(value.toString());
                    } catch(IOException e) {
                        b.setName(b.getPath());
                    }
                    break;
                case 1:
                    b.setPath(value.toString());
                    break;
                }
                
                fireTableCellUpdated(row, col);
                
            } catch (IOException e) {
                LogManager.error("Failed to set bookmark " + 
                        ((col==0)?"name":"path") + " to " + value.toString(),e);
            }
            
        }
        
        public Bookmark getBookmarkAt(int row){
            return bookmarks.get(row);
        }
        
        public void removeBookmarkAt(int row) {
            bookmarks.remove(row);
            fireTableRowsDeleted(row,row);
        }
        
        public void addBookmark(Bookmark bookmark) {
            bookmarks.add(bookmark);
            fireTableRowsInserted(bookmarks.size()-1, bookmarks.size()-1);
        }
        
        public void addBookmarkList(List<Bookmark> bookmarks) {
            bookmarks.addAll(bookmarks);
            fireTableRowsInserted(this.bookmarks.size()-bookmarks.size()-1, bookmarks.size()-1);
        }
        
        public void removeBookmark(Bookmark bookmark) {
            int row = bookmarks.indexOf(bookmark);
            bookmarks.remove(bookmark);
            fireTableRowsDeleted(row,row);
        }
        
    }

    
    class BookmarkTransferable implements Transferable {
        
        private Bookmark bookmark;
        
        private DataFlavor[] flavors = new DataFlavor[]{BOOKMARK_FLAVOR, 
                DataFlavor.stringFlavor};
        
        public BookmarkTransferable(Bookmark bookmark){
            this.bookmark = bookmark;
        }
        
        public Object getTransferData(DataFlavor flavor)
        throws UnsupportedFlavorException, IOException {
            if (flavor == BOOKMARK_FLAVOR) {
                return bookmark;
            } else if (flavor == DataFlavor.stringFlavor) {
                return bookmark.getPath();
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
        JFrame frame = new JFrame("Bookmark Demo");
        ArrayList<Bookmark> arrayList = loadData();
        PnlBookmarks p = new PnlBookmarks(arrayList);
        frame.setContentPane(p);
        frame.pack();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
       
    private static ArrayList<Bookmark> loadData() {
       ArrayList<Bookmark> bookmarks = new ArrayList<Bookmark>();
       Bookmark b = new Bookmark("Test1","/usr/local/bin");
       bookmarks.add(b);
       b = new Bookmark("Test2","/Users/dooley");
       bookmarks.add(b);
       b = new Bookmark("Test2","/tmp");
       bookmarks.add(b);
       return bookmarks;
   }

    
}
