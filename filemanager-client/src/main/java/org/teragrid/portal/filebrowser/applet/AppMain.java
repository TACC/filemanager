/* 
 * Created on Jan 23, 2008
 * 
 * Developed by: Rion Dooley - dooley [at] tacc [dot] utexas [dot] edu
 * 				 TACC, Texas Advanced Computing Center
 * 
 * https://www.tacc.utexas.edu/
 */

package org.teragrid.portal.filebrowser.applet;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.net.URL;

import javax.help.CSH;
import javax.help.HelpBroker;
import javax.help.HelpSet;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JApplet;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileSystemView;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.ietf.jgss.GSSCredential;
import org.teragrid.portal.filebrowser.applet.transfer.FTPSettings;
import org.teragrid.portal.filebrowser.applet.transfer.FileTransferTask;
import org.teragrid.portal.filebrowser.applet.transfer.GridFTP;
import org.teragrid.portal.filebrowser.applet.transfer.HistoryManager;
import org.teragrid.portal.filebrowser.applet.ui.DlgAbout;
import org.teragrid.portal.filebrowser.applet.ui.DlgOption;
import org.teragrid.portal.filebrowser.applet.ui.IconData;
import org.teragrid.portal.filebrowser.applet.ui.LogWindow;
import org.teragrid.portal.filebrowser.applet.ui.PnlBrowse;
import org.teragrid.portal.filebrowser.applet.ui.QueueTableModel;
import org.teragrid.portal.filebrowser.applet.ui.SplashScreen;
import org.teragrid.portal.filebrowser.applet.ui.UIRefresher;
import org.teragrid.portal.filebrowser.applet.util.LogManager;
import org.teragrid.portal.filebrowser.applet.util.ResourceName;
import org.teragrid.portal.filebrowser.applet.util.SGGCResourceBundle;
import org.teragrid.portal.filebrowser.applet.util.SwingWorker;

import com.explodingpixels.macwidgets.IAppWidgetFactory;

import edu.utexas.tacc.wcs.filemanager.common.model.Task;
import edu.utexas.tacc.wcs.filemanager.common.model.enumeration.FileProtocolType;
import edu.utexas.tacc.wcs.filemanager.common.model.enumeration.NotificationType;

/**
 * Main class for XFM Applet.
 * 
 * @author Rion Dooley < dooley [at] tacc [dot] utexas [dot] edu >
 *
 */
@SuppressWarnings({"serial","unused"})
public class AppMain extends JApplet {
//public class AppMain extends JFrame {
    
    // Clipboard icons
    public static final ImageIcon icoCopy = new ImageIcon(ClassLoader.getSystemResource("images/copy.gif"));
    public static final ImageIcon icoCut = new ImageIcon(ClassLoader.getSystemResource("images/cut.gif"));
    public static final ImageIcon icoPaste = new ImageIcon(ClassLoader.getSystemResource("images/paste.gif"));
    
    // UI views icons
    public static final ImageIcon icoPreferences = new ImageIcon(ClassLoader.getSystemResource("images/preferences.png"));
    public static final ImageIcon icoSearch = new ImageIcon(ClassLoader.getSystemResource("images/search.png"));
    public static final ImageIcon icoSearchPressed = new ImageIcon(ClassLoader.getSystemResource("images/search_pressed.png"));
    public static final ImageIcon icoBook = new ImageIcon(ClassLoader.getSystemResource("images/bookmarks.png"));
    public static final ImageIcon icoBookPressed = new ImageIcon(ClassLoader.getSystemResource("images/bookmarks_pressed.png"));
    public static final ImageIcon icoBandwidth=new ImageIcon(ClassLoader.getSystemResource("images/bandwidth.png"));
    public static final ImageIcon icoBandwidthPressed=new ImageIcon(ClassLoader.getSystemResource("images/bandwidth_pressed.png"));
    public static final ImageIcon icoFavorite = new ImageIcon(ClassLoader.getSystemResource("images/favorite.gif"));
    public static final ImageIcon icoHelp = new ImageIcon(ClassLoader.getSystemResource("images/help.png"));
    public static final ImageIcon icoAboutbox = new ImageIcon(ClassLoader.getSystemResource("images/about.png"));
    public static final ImageIcon icoProperties = new ImageIcon(ClassLoader.getSystemResource("images/gear.jpg"));
    
    // File Listing views
    public static final ImageIcon icoDetailView = new ImageIcon(ClassLoader.getSystemResource("images/detail.png"));
    public static final ImageIcon icoIconView = new ImageIcon(ClassLoader.getSystemResource("images/icon.png"));
    public static final ImageIcon icoListView = new ImageIcon(ClassLoader.getSystemResource("images/list.png"));
    
    // Transfer control
    public static final ImageIcon icoStop = new ImageIcon(ClassLoader.getSystemResource("images/rstop.gif"));
    public static final ImageIcon icoStopConn=new ImageIcon(ClassLoader.getSystemResource("images/rstop.gif"));
    public static final ImageIcon icoDisconn=new ImageIcon(ClassLoader.getSystemResource("images/eject.png"));
    public static final ImageIcon icoDisconnPressed=new ImageIcon(ClassLoader.getSystemResource("images/eject_pressed.png"));
    public static final ImageIcon icoDownload=new ImageIcon(ClassLoader.getSystemResource("images/download.gif"));
    public static final ImageIcon icoConn=new ImageIcon(ClassLoader.getSystemResource("images/connect.jpg"));
    public static final ImageIcon icoUpload=new ImageIcon(ClassLoader.getSystemResource("images/upload.gif"));
    
    // Queue Status Icons
    public static final ImageIcon icoStatusGreen=new ImageIcon(ClassLoader.getSystemResource("images/statusGreen.png"));
    public static final ImageIcon icoStatusYellow=new ImageIcon(ClassLoader.getSystemResource("images/statusYellow.png"));
    public static final ImageIcon icoStatusRed=new ImageIcon(ClassLoader.getSystemResource("images/statusRed.png"));
    public static final ImageIcon icoStatusComplete=new ImageIcon(ClassLoader.getSystemResource("images/statusComplete.png"));
    
    // File Management Icons
    public static final ImageIcon icoRefresh=new ImageIcon(ClassLoader.getSystemResource("images/reload.png"));
    public static final ImageIcon icoRefreshPressed=new ImageIcon(ClassLoader.getSystemResource("images/reload_pressed.png"));
    public static final ImageIcon icoRename=new ImageIcon(ClassLoader.getSystemResource("images/rename.png"));
    public static final ImageIcon icoRenamePressed=new ImageIcon(ClassLoader.getSystemResource("images/rename_pressed.png"));
    public static final ImageIcon icoNewFolder = new ImageIcon(ClassLoader.getSystemResource("images/newfolder.png"));
    public static final ImageIcon icoNewFolderPressed = new ImageIcon(ClassLoader.getSystemResource("images/newfolder_pressed.png"));
    public static final ImageIcon icoInfo=new ImageIcon(ClassLoader.getSystemResource("images/info.png"));
    public static final ImageIcon icoInfoPressed=new ImageIcon(ClassLoader.getSystemResource("images/info_pressed.png"));
    public static final ImageIcon icoDelete=new ImageIcon(ClassLoader.getSystemResource("images/trash.png"));
    public static final ImageIcon icoDeletePressed=new ImageIcon(ClassLoader.getSystemResource("images/trash_pressed.png"));
    
    // Browsing Icons
    public static Icon icoFile = null;
    public static final ImageIcon icoFileLarge = new ImageIcon(ClassLoader.getSystemResource("images/file32.png"));
    public static final Icon icoFolder = FileSystemView.getFileSystemView().getSystemIcon(new File(System.getProperty("user.home")));
    public static final ImageIcon icoFolderLarge = new ImageIcon(ClassLoader.getSystemResource("images/folder32.png"));
    public static final ImageIcon icoNetwork=new ImageIcon(ClassLoader.getSystemResource("images/network.png"));
    public static final ImageIcon icoFolderExpanded = new ImageIcon(ClassLoader.getSystemResource("images/folderexpanded.gif"));
    public static final ImageIcon icoRightArrow = new ImageIcon(ClassLoader.getSystemResource("images/toarrow.png"));
    
    // Notification icons
    public static final ImageIcon icoIM=new ImageIcon(ClassLoader.getSystemResource("images/chat.png"));
    public static final ImageIcon icoEmail=new ImageIcon(ClassLoader.getSystemResource("images/email.png"));
    public static final ImageIcon icoNotify=new ImageIcon(ClassLoader.getSystemResource("images/notify.png"));
    public static final ImageIcon icoSMS=new ImageIcon(ClassLoader.getSystemResource("images/sms.png"));
    
    // Editing Icons
    public static final ImageIcon icoAdd=new ImageIcon(ClassLoader.getSystemResource("images/add.jpg"));
    public static final ImageIcon icoRemove=new ImageIcon(ClassLoader.getSystemResource("images/remove.jpg"));
    public static final ImageIcon icoEdit=new ImageIcon(ClassLoader.getSystemResource("images/edit.jpg"));
    public static final ImageIcon icoSortUp = new ImageIcon(ClassLoader.getSystemResource("images/sortup.gif"));
    public static final ImageIcon icoSortDown = new ImageIcon(ClassLoader.getSystemResource("images/sortdown.gif"));
    public static final ImageIcon icoLocked = new ImageIcon(ClassLoader.getSystemResource("images/locked.jpg"));
    public static final ImageIcon icoUnlocked = new ImageIcon(ClassLoader.getSystemResource("images/unlocked.jpg"));
    
    // Identity icons
    public static final ImageIcon icoUser = new ImageIcon(ClassLoader.getSystemResource("images/user.png"));
    public static final ImageIcon icoGroup = new ImageIcon(ClassLoader.getSystemResource("images/group.png"));
    public static final ImageIcon icoContact = new ImageIcon(ClassLoader.getSystemResource("images/contact.png"));
    
    // Navigation Icons
    public static final ImageIcon icoBack=new ImageIcon(ClassLoader.getSystemResource("images/back.jpg"));
    public static final ImageIcon icoFwd = new ImageIcon(ClassLoader.getSystemResource("images/forward.jpg"));
    public static final ImageIcon icoUpDir=new ImageIcon(ClassLoader.getSystemResource("images/up.gif"));
    public static final ImageIcon icoUpDirPressed=new ImageIcon(ClassLoader.getSystemResource("images/up_pressed.gif"));
    public static final ImageIcon icoGoTo=new ImageIcon(ClassLoader.getSystemResource("images/goto.png"));
    public static final ImageIcon icoGoToPressed=new ImageIcon(ClassLoader.getSystemResource("images/goto_pressed.png"));
    public static final ImageIcon icoHome=new ImageIcon(ClassLoader.getSystemResource("images/home.png"));
    public static final ImageIcon icoHomePressed=new ImageIcon(ClassLoader.getSystemResource("images/home_pressed.png"));
    public static final ImageIcon icoTGArchive = new ImageIcon(ClassLoader.getSystemResource("images/TG_ARCHIVE.png"));
    public static final ImageIcon icoTGScratch = new ImageIcon(ClassLoader.getSystemResource("images/TG_SLASH.png"));
    public static final ImageIcon icoTGWork = new ImageIcon(ClassLoader.getSystemResource("images/TG_WORK.png"));
    public static final ImageIcon icoEnvironment = new ImageIcon(ClassLoader.getSystemResource("images/environment.png"));
    public static final ImageIcon icoEnvironmentPressed = new ImageIcon(ClassLoader.getSystemResource("images/environment_pressed.png"));
    
    // Connection Type Icons
    public static final ImageIcon icoResourceTeraGridShare = new ImageIcon(ClassLoader.getSystemResource("images/idisk.png"));
    public static final ImageIcon icoResourceAmazon = new ImageIcon(ClassLoader.getSystemResource("images/amazon.png"));
    public static final ImageIcon icoResourceCompute = new ImageIcon(ClassLoader.getSystemResource("images/compute_resource.png"));
    public static final ImageIcon icoResourceArchive =new ImageIcon(ClassLoader.getSystemResource("images/storage_resource.png"));
    public static final ImageIcon icoResourceViz = new ImageIcon(ClassLoader.getSystemResource("images/viz-resource.png"));
    public static final ImageIcon icoResourceLocal = new ImageIcon(ClassLoader.getSystemResource("images/local-resource.png"));
    
    // Branding Icons
    public static final ImageIcon icoTeraGrid = new ImageIcon(ClassLoader.getSystemResource("images/xsede.png"));
    public static final ImageIcon icoTeraGridSmall = new ImageIcon(ClassLoader.getSystemResource("images/xsede-small.png"));
    public static final ImageIcon imgSplash = new ImageIcon(ClassLoader.getSystemResource("images/xsede-splash.png"));
    public static final ImageIcon icoError = new ImageIcon(ClassLoader.getSystemResource("images/error_message_icon.png"));
    public static final ImageIcon icoWarn = new ImageIcon(ClassLoader.getSystemResource("images/warning_message_icon.png"));
    public static final ImageIcon icoPrompt = new ImageIcon(ClassLoader.getSystemResource("images/xsede_prompt_message_icon.png"));
    public static final Image imgBackground = new ImageIcon(ClassLoader.getSystemResource("images/background.jpg")).getImage();

//    public static final ImageIcon icoTeraGrid = new ImageIcon(ClassLoader.getSystemResource("images/eudat.png"));
//    public static final ImageIcon icoTeraGridSmall = new ImageIcon(ClassLoader.getSystemResource("images/eudat-small.png"));
//    public static final ImageIcon imgSplash = new ImageIcon(ClassLoader.getSystemResource("images/eudat-splash.png"));
//    public static final ImageIcon icoError = new ImageIcon(ClassLoader.getSystemResource("images/eudat_error_message_icon.png"));
//    public static final ImageIcon icoWarn = new ImageIcon(ClassLoader.getSystemResource("images/eudat_warning_message_icon.png"));
//    public static final ImageIcon icoPrompt = new ImageIcon(ClassLoader.getSystemResource("images/eudat_prompt_message_icon.png"));
//    public static final Image imgBackground = new ImageIcon(ClassLoader.getSystemResource("images/eudat-background.jpg")).getImage();
    
    // Share Overlay Icons
    public static final ImageIcon icoShareItem = new ImageIcon(ClassLoader.getSystemResource("images/share-item.png"));
    public static final ImageIcon icoShareItem16 = new ImageIcon(ClassLoader.getSystemResource("images/share-item16.png"));
//    public static final ImageIcon icoShareChild = new ImageIcon(ClassLoader.getSystemResource("images/share-child.png"));
//    public static final ImageIcon icoShareChild16 = new ImageIcon(ClassLoader.getSystemResource("images/share-child16.png"));
    
    private static Logger logger = LogManager.getLogger();
    private static AppMain appMain = null;
    private PnlBrowse fchLocal=null;

    public static final String appOS=System.getProperty("os.name");
    public static final String appPath=System.getProperty("user.home");
    
    public static final int scheduleTypeLargeFile = 0;
    public static final int scheduleTypeSmallFile = 1;
    public static final int scheduleTypeFIFO = 2;
    public static final String scheduleName[] = new String[]{
	    SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_APPMAIN_LARGEFIRST),
	    SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_APPMAIN_SMALLFIRST), 
	    SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_APPMAIN_FIFO)};  
    
    public static GSSCredential defaultCredential=null;
    public static String ssoUsername; // read from portal
    public static String ssoPassword; // read from portal
    
    private static int scheduleType = scheduleTypeLargeFile;
    public JPanel main;
    public JPanel pnlTopButton = new JPanel();
    private JTable tblQueue = new JTable();
    private JScrollPane pneQueue = new JScrollPane();
    private JPanel pnlTableContainer = new JPanel();
    protected JSplitPane spViewers;
    public JComboBox cmbRightPath = new JComboBox();
    
    private JMenuBar mnuMain = new JMenuBar();
    private JMenu mnuTGFB = new JMenu();
    private JMenuItem mnuTGFBAbout = new JMenuItem();
    private JMenuItem mnuTGFBReload = new JMenuItem();
    private JMenuItem mnuTGFBPrefs = new JMenuItem();
    private JMenuItem mnuTGFBExit = new JMenuItem();
    
    private JMenu mnuView = new JMenu();
    private JMenuItem mnuViewShowTransfers = new JMenuItem();
    private JMenuItem mnuViewShowLog = new JMenuItem();
    private JMenuItem mnuViewTable = new JMenuItem();
    private JMenuItem mnuViewTree = new JMenuItem();
    private JMenuItem mnuViewIcon = new JMenuItem();
    
    private JMenu mnuFile = new JMenu();
    private JMenuItem mnuFileAuth = new JMenuItem();
    private JMenuItem mnuFileOpen = new JMenuItem();
    private JMenuItem mnuFileOpenNew = new JMenuItem();
    private JMenuItem mnuFileLogging = new JMenuItem();
    private JMenuItem mnuFileResetResources = new JMenuItem();
    
    private JMenu mnuEdit = new JMenu();
    private JMenuItem mnuEditUndo = new JMenuItem();
    private JMenuItem mnuEditRedo = new JMenuItem();
    private JMenuItem mnuEditCut = new JMenuItem();
    private JMenuItem mnuEditCopy = new JMenuItem();
    private JMenuItem mnuEditPaste = new JMenuItem();
    private JMenu mnuEditSite = new JMenu();
    private JMenuItem mnuEditSiteAdd= new JMenuItem();
    private JMenuItem mnuEditSiteDelete = new JMenuItem();
    private JMenuItem mnuEditSiteModify = new JMenuItem();
    private JMenuItem mnuEditSiteRefresh = new JMenuItem();
    
    private JMenu mnuGo = new JMenu();
    private JMenuItem mnuGoBack = new JMenuItem();
    private JMenuItem mnuGoForward = new JMenuItem();
    private JMenuItem mnuGoUp = new JMenuItem();
    private JMenuItem mnuGoTo = new JMenuItem();
    
    private JMenu mnuHelp = new JMenu();
    private JMenuItem mnuHelpTopics = new JMenuItem();
    
    private JPopupMenu mnuQueue = new JPopupMenu();
    private JMenuItem mnuQueStopSel = new JMenuItem();
    private JMenuItem mnuQueDelSel = new JMenuItem();
    private JMenuItem mnuQueDelDone = new JMenuItem();
    private JMenuItem mnuQueDelAll = new JMenuItem();
    private JMenu mnuQueNotify = new JMenu();
    private JMenuItem mnuQueNotifyEmail = new JMenuItem();
    private JMenuItem mnuQueNotifyIM = new JMenuItem();
    private JMenuItem mnuQueNotifySMS = new JMenuItem();
    private JMenuItem mnuQueNotifyClear = new JMenuItem();
    private JMenuItem mnuQueTrans = new JMenuItem();
    private JMenuItem mnuQueRefresh = new JMenuItem();
    private JMenuItem mnuQueTransAll = new JMenuItem();
    private JMenuItem mnuQueProp = new JMenuItem();
    
    private static LogWindow logWindow = null;
    
    protected PnlBrowse pbLeft;
    protected PnlBrowse pbRight;
    private static SplashScreen splashScreen;
    private ActionListener evtQueMenu = new frmMain_mnuQueue_actionAdapter(this);
    private ActionListener evtMenu = new frmMain_MenuItem_actionAdapter(this);
    private static JFrame frame;
    private static boolean isRunningAsApplet = true;
    
    public static void main(String[] args) {
    	JFrame frame = new JFrame();
        frame.setLayout(new GridLayout(1,0));
        frame.setPreferredSize(new Dimension(900,700));
        
        AppMain tgfmApplet = new AppMain();
        AppMain.frame = frame;
        isRunningAsApplet = false;
        
        JPanel pnlBackground = new JPanel() {
        	public void paintComponent(Graphics g) {
        		g.drawImage(imgBackground, 0, 0, null);
        	}
        };
        
        frame.add(pnlBackground);

        frame.pack();
        frame.setVisible(true);
        
        tgfmApplet.init();
        frame.remove(pnlBackground);
        frame.add(tgfmApplet);
        frame.pack();
        
        tgfmApplet.start();
        frame.validate();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
    }
    
    // end comments
    
    /* (non-Javadoc)
	 * @see java.applet.Applet#destroy()
	 */
	@Override
	public void destroy() {
		pbLeft.disConn();
		pbRight.disConn();
		super.destroy();
	}

	/* (non-Javadoc)
	 * @see java.applet.Applet#stop()
	 */
	@Override
	public void stop() {
		pbLeft.disConn();
		pbRight.disConn();
		super.stop();
	}

	public void init() {
    	
		appMain = this;
        
        setPreferredSize(new Dimension(900,700));
        
        Thread.currentThread().setName("AppMain");
        
        getFrame().setTitle("XSEDE File Manager");
        
        showSplashScreen(true);
        
        registerKeyListeners();
        
        LogManager.init();
        
        ConfigOperation.delete();
        
        ConfigOperation configOperation = ConfigOperation.getInstance();
        
        configOperation.loadExtendedSettings();
        
        jbInit();
        
        try {
            FTPSettings.DefaultLogfile=configOperation.getConfigValue("logfile");
            FTPSettings.DefaultShowHidden=Boolean.valueOf(configOperation.getConfigValue("showHidden")).booleanValue();
            FTPSettings.DefaultPassiveMode=Boolean.valueOf(configOperation.getConfigValue("passive")).booleanValue();
            FTPSettings.DefaultBufferSize=Integer.parseInt(configOperation.getConfigValue("bufferSize"));
            FTPSettings.DefaultConnMaxNum=Integer.parseInt(configOperation.getConfigValue("connMax"));
            FTPSettings.DefaultConnRetry=Integer.parseInt(configOperation.getConfigValue("connRetry"));
            FTPSettings.DefaultConnDelay=Integer.parseInt(configOperation.getConfigValue("connDelay"));
            FTPSettings.DefaultConnPara=Integer.parseInt(configOperation.getConfigValue("parallel"));
            FTPSettings.DefaultConnKeep=Integer.parseInt(configOperation.getConfigValue("connKeepAlive"));
            FTPSettings.DefaultCertificate=configOperation.getConfigValue("proxy");
        } catch (Exception e) {
            LogManager.error("Failed to load default FTP settings.", e);
        }
        
        HistoryManager.refreshTaskList();
        
        fchLocal = new PnlBrowse(this,new FTPSettings("Local",FileProtocolType.FILE.getDefaultPort(),FileProtocolType.FILE),configOperation.getConfigValue("download_dir"),false);
        
//        for (File f: java.util.Arrays.asList(new File(System.getProperty("user.home")).listFiles())) {
//            if (f.isFile()) {
//                icoFile = FileSystemView.getFileSystemView().getSystemIcon(f);
//                break;
//            }
//        }
        
        UIRefresher.setDisplayTable(this.tblQueue);
        UIRefresher.setDisplayScrollPane(this.pneQueue);
        
        this.tblQueue.setModel(new QueueTableModel(getFrame()));
        DefaultTableCellRenderer cellRenderer = new
                DefaultTableCellRenderer() {
            public void setValue(Object value) {
                if (value instanceof IconData) {
                    IconData c = (IconData) value;
                    setIcon(c.getIcon());
                    setText(c.toString());
                    setFont(new Font("Ariel",getFont().getStyle(),12));
                } else {
                    super.setValue(value);
                }
            }
        };
        
        TableColumn column;
        for (int i = 0; i < this.tblQueue.getColumnCount(); i++) {
            column = this.tblQueue.getColumnModel().getColumn(i);
            if (1 == i || 0 == i) {
                column.setCellRenderer(cellRenderer);
            }
            if (5 == i) {
                column.setCellRenderer(new TableCellRenderer() {
                    public Component getTableCellRendererComponent(
                            JTable table, Object value, boolean isSelected,
                            boolean hasFocus, int row, int column) {
                        return (JProgressBar) value;
                    }
                });
            }
            column.setPreferredWidth(0 == i || 3 == i ? 32 :
                                     2 == i || 4 == i ? 110 : 1 == i ? 160 :
                                     80);
        }
        
        // Install the look and feel
        try {
        	// Get the native look and feel class name
            //String nativeLF = UIManager.getSystemLookAndFeelClassName();
            
            //UIManager.setLookAndFeel(nativeLF);
            
    		
            
        } catch (Exception e) {}
        
        showSplashScreen(false);
        
     // layout browsers and queue table on applet
        main = new JPanel(new BorderLayout());
        main.removeAll();
        main.add(spViewers,BorderLayout.CENTER);
//        main.add(pneQueue,BorderLayout.SOUTH);
        
        getContentPane().remove(main);
        getContentPane().add(main);
        
        
        UIRefresher.refreshQueue();
        
    }
    
    private void jbInit() {
        
    	// create menu bar and menu items
        menuInit();
        
        // create file transfer history popup menu
        qpopInit();
        
        // create left panel opened to Local
        this.pbLeft = new PnlBrowse(this,new FTPSettings("Local",FileProtocolType.FILE.getDefaultPort(),FileProtocolType.FILE, this.pbLeft),false);
        
        // create empty right panel
        this.pbRight = new PnlBrowse(this);
        
        // layout form
        spViewers = new JSplitPane() {
        	public void paintComponent(Graphics g) {
                g.drawImage(imgBackground, 0, 0, null);
              }
        };
        spViewers.setLeftComponent(this.pbLeft);
        spViewers.setRightComponent(this.pbRight);
        spViewers.setBorder(BorderFactory.createBevelBorder(1));
//        spViewers.setResizeWeight(.5);
        spViewers.setDividerLocation(425);
//        spViewers.setMinimumSize(new Dimension(400, pbLeft.getHeight()));
        spViewers.setOneTouchExpandable(true);
        
        // set up file transfer history table and panel
//        pnlTableContainer.setLayout(new BorderLayout());
//        pnlTableContainer.add(this.tblQueue,BorderLayout.CENTER);
//        pnlTableContainer.add(this.tblQueue.getTableHeader(),BorderLayout.NORTH);
        pneQueue.getViewport().add(tblQueue);
        pneQueue.getViewport().setBackground(Color.white);
        pneQueue.setPreferredSize(new Dimension(100, 140));
        pneQueue.setWheelScrollingEnabled(true);
        pneQueue.setAutoscrolls(true);
        
        try {
        	IAppWidgetFactory.makeIAppScrollPane(pneQueue);
        } catch (Exception e) {}
        
        tblQueue.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tblQueue.setRowHeight(20);
        tblQueue.setIntercellSpacing(new Dimension(0, 1));
        tblQueue.setShowHorizontalLines(false);
        tblQueue.setShowVerticalLines(false);
        
        tblQueue.addMouseListener(new frmMain_tblQueue_mouseAdapter(this));
        pneQueue.addMouseListener(new frmMain_tblQueue_mouseAdapter(this));
        
    }
    
    private void menuInit() {

        mnuTGFBAbout.setText("About File Manager");
        mnuTGFBAbout.setMnemonic('A');
        mnuTGFBAbout.addActionListener(evtMenu);
        mnuTGFBReload.setText("Reload");
        mnuTGFBReload.setMnemonic('R');
        mnuTGFBReload.addActionListener(evtMenu);
        mnuTGFBPrefs.setText("Preferences");
        mnuTGFBPrefs.setMnemonic('P');
        mnuTGFBPrefs.addActionListener(evtMenu);
        mnuTGFBExit.setText("Quit");
        mnuTGFBExit.setMnemonic('Q');
        mnuTGFBExit.addActionListener(evtMenu);
        mnuTGFB.setText("File Manager");
        mnuTGFB.setMnemonic('T');
        mnuTGFB.add(mnuTGFBAbout);
        mnuTGFB.add(mnuTGFBReload);
        mnuTGFB.add(mnuTGFBPrefs);
        mnuTGFB.addSeparator();
        mnuTGFB.add(mnuTGFBExit);

//        if (isApplet()) {
//	        mnuFileOpen.setText("Open current instance");
//	        mnuFileOpen.setMnemonic('o');
//	        mnuFileOpen.addActionListener(evtMenu);
//	        mnuFileOpenNew.setText("Open new instance");
//	        mnuFileOpenNew.setMnemonic('n');
//	        mnuFileOpenNew.addActionListener(evtMenu);
//        }
        mnuFileAuth.setText("Login");
        mnuFileAuth.setMnemonic('l');
        mnuFileAuth.addActionListener(evtMenu);
        mnuFileLogging.setText("Work offline"); // uncomment this line to use the middleware discovery and logging service 
        //mnuFileLogging.setText("Work online"); // uncomment this line to run blind
        mnuFileLogging.setMnemonic('l');
        mnuFileLogging.addActionListener(evtMenu);
        mnuFileResetResources.setText("Refresh resources");
        mnuFileResetResources.setMnemonic('r');
        mnuFileResetResources.addActionListener(evtMenu);
//        mnuFileOpen.setEnabled(false);
        mnuFile.setText("File");
        mnuFile.setMnemonic('F');
        mnuFile.add(mnuFileAuth);
//        if (isApplet()) {
//	        mnuFile.add(mnuFileOpen);
//	        mnuFile.add(mnuFileOpenNew);
//        }
        mnuFile.add(mnuFileLogging);
        mnuFile.add(mnuFileResetResources);
        
        // commented out until we implement copy, paste, tree view, etc.
        mnuView.setText("View");
        mnuView.setMnemonic('v');
        mnuView.add(mnuViewShowTransfers);
        mnuViewShowTransfers.setText("Show transfers");
        mnuViewShowTransfers.addActionListener(evtMenu);
//        mnuView.add(mnuViewTable);
//        mnuView.add(mnuViewTree);
//        mnuView.add(mnuViewIcon);
        mnuView.add(mnuViewShowLog);
        mnuViewShowLog.setText("Show console");
        mnuViewShowLog.addActionListener(evtMenu);
//        mnuViewTable.setText("as Table");
//        mnuViewTable.setSelected(true);
//        mnuViewTable.addActionListener(evtMenu);
//        mnuViewTree.setText("as Tree");
//        mnuViewTree.setEnabled(false);
//        mnuViewTree.addActionListener(evtMenu);
//        mnuViewIcon.setText("as Icons");
//        mnuViewIcon.setEnabled(false);
//        mnuViewIcon.addActionListener(evtMenu);
        
        mnuEdit.setText("Edit");
        mnuEdit.setMnemonic('E');
        mnuEdit.add(mnuEditUndo);
        mnuEdit.add(mnuEditRedo);
        mnuEdit.addSeparator();
        mnuEdit.add(mnuEditCut);
        mnuEdit.add(mnuEditCopy);
        mnuEdit.add(mnuEditPaste);
        mnuEdit.add(mnuEditSite);
        mnuEditUndo.setText(SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_FRMMAIN_MNUUNDO));
        mnuEditUndo.setMnemonic('U');
        mnuEditUndo.addActionListener(evtMenu);
        mnuEditUndo.setEnabled(false);
        mnuEditRedo.setText(SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_FRMMAIN_MNUREDO));
        mnuEditRedo.setMnemonic('R');
        mnuEditRedo.addActionListener(evtMenu);
        mnuEditRedo.setEnabled(false);
//        mnuEditCut.setIcon(AppMain.icoCut);
        mnuEditCut.setText(SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_FRMMAIN_MNUCUT));
        mnuEditCut.setMnemonic('u');
        mnuEditCut.addActionListener(evtMenu);
        mnuEditCut.setEnabled(false);
//        mnuEditCopy.setIcon(AppMain.icoCopy);
        mnuEditCopy.setText(SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_FRMMAIN_MNUCOPY));
        mnuEditCopy.setMnemonic('C');
        mnuEditCopy.addActionListener(evtMenu);
        mnuEditCopy.setEnabled(false);
//        mnuEditPaste.setIcon(AppMain.icoPaste);
        mnuEditPaste.setText(SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_FRMMAIN_MNUPASTE));
        mnuEditPaste.setMnemonic('P');
        mnuEditPaste.addActionListener(evtMenu);
        mnuEditPaste.setEnabled(false);
//        mnuEditSite.setIcon(AppMain.icoEdit);
        mnuEditSite.setText(SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_FRMMAIN_MNUSITE));
        mnuEditSite.setMnemonic('S');
        mnuEditSite.add(mnuEditSiteAdd);
        mnuEditSite.add(mnuEditSiteDelete);
        mnuEditSite.add(mnuEditSiteModify);
        mnuEditSite.addSeparator();
        mnuEditSite.add(mnuEditSiteRefresh);
        mnuEditSite.setEnabled(false);
//        mnuEditSiteAdd.setIcon(AppMain.icoNewServer);
        mnuEditSiteAdd.setText(SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_FRMMAIN_MNUADDSITE));
        mnuEditSiteAdd.setMnemonic('A');
        mnuEditSiteAdd.addActionListener(evtMenu);
//        mnuEditSiteDelete.setIcon(AppMain.icoDelete);
        mnuEditSiteDelete.setText(SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_FRMMAIN_MNUDELSITE));
        mnuEditSiteDelete.setMnemonic('D');
        mnuEditSiteDelete.addActionListener(evtMenu);
//        mnuEditSiteModify.setIcon(AppMain.icoProperty);
        mnuEditSiteModify.setText(SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_FRMMAIN_MNUMODIFYSITE));
        mnuEditSiteModify.setMnemonic('M');
        mnuEditSiteModify.addActionListener(evtMenu);
        mnuEditSiteRefresh.setText(SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_FRMMAIN_MNUREFRESHSITES));
        mnuEditSiteRefresh.setMnemonic('R');
        mnuEditSiteRefresh.addActionListener(evtMenu);
        
        mnuGo.setText("Go");
        mnuGo.setMnemonic('G');
        mnuGo.add(mnuGoBack);
        mnuGo.add(mnuGoForward);
        mnuGo.add(mnuGoUp);
        mnuGo.addSeparator();
        mnuGo.add(mnuGoTo);
        mnuGoBack.setText("Back");
        mnuGoBack.setMnemonic('B');
        mnuGoBack.addActionListener(evtMenu);
        mnuGoBack.setEnabled(false);
        mnuGoForward.setText("Forward");
        mnuGoForward.setMnemonic('F');
        mnuGoForward.addActionListener(evtMenu);
        mnuGoForward.setEnabled(false);
        mnuGoUp.setText("Enclosing Folder");
        mnuGoUp.setMnemonic('U');
        mnuGoUp.addActionListener(evtMenu);
        mnuGoUp.setEnabled(false);
        mnuGoTo.setText("Go to...");
        mnuGoTo.setMnemonic('z');
        mnuGoTo.addActionListener(evtMenu);
        mnuGoTo.setEnabled(false);
        
        mnuHelpTopics = getHelpMenuItem();
        mnuHelpTopics.setText(SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_FRMMAIN_MNUHELPTOPIC));
//        mnuHelpTopics.setEnabled(false);
        mnuHelp.setMnemonic('H');
        mnuHelp.setText(SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_FRMMAIN_MNUHELP));
        mnuHelp.add(mnuHelpTopics);
        
        mnuMain.add(mnuTGFB);
        mnuMain.add(mnuFile);
//        mnuMain.add(mnuEdit);
        mnuMain.add(mnuView);
//        mnuMain.add(mnuGo);
        mnuMain.add(mnuHelp);
        
        setJMenuBar(mnuMain);
        
    }
    
    private void qpopInit() {
//        mnuQueStopSel.setIcon(AppMain.icoStop);
        mnuQueStopSel.setText(SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_STOP));
        mnuQueStopSel.setMnemonic('P');
        mnuQueStopSel.addActionListener(evtQueMenu);
//        mnuQueDelSel.setIcon(AppMain.icoDelete);
        mnuQueDelSel.setText(SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_FRMMAIN_MNUQUEDELSEL));
        mnuQueDelSel.setMnemonic('S');
        mnuQueDelSel.addActionListener(evtQueMenu);
//        mnuQueDelDone.setIcon(AppMain.icoDelete);
        mnuQueDelDone.setText(SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_FRMMAIN_MNUQUEDELDONE));
        mnuQueDelDone.setMnemonic('F');
        mnuQueDelDone.addActionListener(evtQueMenu);
//        mnuQueDelAll.setIcon(AppMain.icoDelete);
        mnuQueDelAll.setText(SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_FRMMAIN_MNUQUEDELALL));
        mnuQueDelAll.setMnemonic('R');
        mnuQueDelAll.addActionListener(evtQueMenu);
//        mnuQueNotify.setIcon(AppMain.icoNotify);
        mnuQueNotify.setText(SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_FRMMAIN_MNUQUENOTIFY));
        mnuQueNotify.setMnemonic('N');
        mnuQueNotifyEmail.setText(SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_FRMMAIN_MNUQUENOTIFYEMAIL));
        mnuQueNotifyEmail.setMnemonic('E');
        mnuQueNotifyEmail.addActionListener(evtQueMenu);
//        mnuQueNotifyEmail.setIcon(AppMain.icoEmail);
        mnuQueNotifyIM.setText(SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_FRMMAIN_MNUQUENOTIFYIM));
        mnuQueNotifyIM.setMnemonic('I');
        mnuQueNotifyIM.addActionListener(evtQueMenu);
//        mnuQueNotifyIM.setIcon(AppMain.icoIM);
        mnuQueNotifySMS.setText(SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_FRMMAIN_MNUQUENOTIFYSMS));
        mnuQueNotifySMS.setMnemonic('T');
        mnuQueNotifySMS.addActionListener(evtQueMenu);
//        mnuQueNotifySMS.setIcon(AppMain.icoSMS);
        mnuQueNotifyClear.setText(SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_FRMMAIN_MNUQUENOTIFYCLEAR));
        mnuQueNotifyClear.setMnemonic('U');
        mnuQueNotifyClear.addActionListener(evtQueMenu);
        mnuQueNotifyClear.setIcon(AppMain.icoDelete);
        mnuQueNotify.add(mnuQueNotifyEmail);
//        mnuQueNotify.add(mnuQueNotifyIM);
//        mnuQueNotify.add(mnuQueNotifySMS);
        mnuQueNotify.add(mnuQueNotifyClear);
//        mnuQueTrans.setIcon(AppMain.icoDownload);
        mnuQueTrans.setText(SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_FRMMAIN_MNUQUETRANS));
        mnuQueTrans.setMnemonic('E');
        mnuQueTrans.setEnabled(true);
        mnuQueTrans.addActionListener(evtQueMenu);
        mnuQueTransAll.setText(SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_FRMMAIN_MNUQUETRANSALL));
        mnuQueTransAll.setMnemonic('T');
        mnuQueTransAll.setEnabled(false);
        mnuQueTransAll.addActionListener(evtQueMenu);
//        mnuQueRefresh.setIcon(AppMain.icoRefresh);
        mnuQueRefresh.setText(SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_FRMMAIN_MNUQUEREFRESH));
        mnuQueRefresh.setMnemonic('R');
        mnuQueRefresh.setEnabled(true);
        mnuQueRefresh.addActionListener(evtQueMenu);
//        mnuQueProp.setIcon(AppMain.icoProperties);
        mnuQueProp.setText(SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_PROPERTY));
        mnuQueProp.setMnemonic('P');
        mnuQueProp.setEnabled(false);
        mnuQueProp.addActionListener(evtQueMenu);
        mnuQueue.add(mnuQueTrans);
        mnuQueue.add(mnuQueNotify);
        mnuQueue.add(mnuQueRefresh);
        mnuQueue.add(mnuQueStopSel);
        mnuQueue.addSeparator();
        mnuQueue.add(mnuQueDelSel);
        mnuQueue.add(mnuQueDelDone);
        mnuQueue.add(mnuQueDelAll);
    }
    
    @SuppressWarnings("deprecation")
	private JMenuItem getHelpMenuItem() {
        HelpSet hs = null;
        HelpBroker hb = null;
        URL url = null;
        try {
            LogManager.info("Loading helpset file");
            LogManager.debug("Loading helpset file from: " + ConfigOperation.getInstance().getHelpDir() + "Master.hs");//((ConfigOperation.os().equals("windows"))?"jhelpset_win.hs":"jhelpset.hs"));
            url = new File(ConfigOperation.getInstance().getHelpDir() + "Master.hs").toURL(); // replace this with the actual location of your help set
            hs = new HelpSet(null, url);
            
        } catch (Exception e1) {
            e1.printStackTrace();
        } 
        
        hb = hs.createHelpBroker();
        JMenuItem mi = new JMenuItem();
        mi.addActionListener(new CSH.DisplayHelpFromSource(hb));
        
        return mi;

    }
    
    
    private void showSplashScreen(boolean show) {
    	if (show) {
	        splashScreen = new SplashScreen(getFrame());
	        splashScreen.setVisible(show);
    	} else {
    		splashScreen.setVisible(show);
    		splashScreen.dispose();
    	}
    }
    
    public PnlBrowse getLocalBrowser() {
        return fchLocal;
    }
    public void mnuQueue_actionPerformed(ActionEvent e) {

        //Transfer Selected
        if(mnuQueTrans==e.getSource()){
            int[] selectedRows = this.tblQueue.getSelectedRows();
            ((QueueTableModel)this.tblQueue.getModel()).restartSelected(selectedRows);
            UIRefresher.refreshQueue();
        }
        //Transfer All
        else if(mnuQueTransAll==e.getSource()){

        }
        // Refresh history list
        else if(mnuQueRefresh==e.getSource()){
            ((QueueTableModel)this.tblQueue.getModel()).refreshTaskList();
            UIRefresher.refreshQueue();
            this.pneQueue.validate();
        }
        // Notify by Email
        else if(mnuQueNotifyEmail==e.getSource()){
            int [] selectedRows = this.tblQueue.getSelectedRows();
            ((QueueTableModel)this.tblQueue.getModel()).setNotification(selectedRows,NotificationType.EMAIL);
            UIRefresher.refreshQueue();
        }
        // Clear all notifications
        else if(mnuQueNotifyClear==e.getSource()){
            int [] selectedRows = this.tblQueue.getSelectedRows();
            ((QueueTableModel)this.tblQueue.getModel()).clearAllNotifications(selectedRows);
            UIRefresher.refreshQueue();
        }
        //Stop
        else if(mnuQueStopSel==e.getSource()){
            int [] selectedRows = this.tblQueue.getSelectedRows();
            ((QueueTableModel)this.tblQueue.getModel()).stopSelected(selectedRows);
            UIRefresher.refreshQueue();
        }
        //Remove Selected
        else if(mnuQueDelSel==e.getSource()){
            int [] selectedRows = this.tblQueue.getSelectedRows();
            ((QueueTableModel)this.tblQueue.getModel()).removeSelected(selectedRows);
            UIRefresher.refreshQueue();
        }
        //Remove Finished
        else if(mnuQueDelDone==e.getSource()){
            ((QueueTableModel)this.tblQueue.getModel()).removeFinished();
            UIRefresher.refreshQueue();
        }
        //Remove All
        else if(mnuQueDelAll==e.getSource()){
            ((QueueTableModel)this.tblQueue.getModel()).removeAll();
            UIRefresher.refreshQueue();
        }
        //Property
        else if(mnuQueProp==e.getSource()){

        }
    }
    
    public void tblQueue_mouseClicked(MouseEvent e) {
        if (e.isPopupTrigger()) {
            if (!ConfigOperation.isLoggingEnabled()) {
                
                mnuQueNotify.setEnabled(false);
                mnuQueRefresh.setEnabled(false);

            } else {
            
                mnuQueRefresh.setEnabled(true);
                
                if (areAnyDone(this.tblQueue.getSelectedRows())) {
                   mnuQueNotify.setEnabled(false);
                   mnuQueStopSel.setEnabled(false);
                } else {
                   mnuQueNotify.setEnabled(true);
                   mnuQueStopSel.setEnabled(true);
                }
            }
            mnuQueue.show(this.tblQueue,e.getX(),e.getY());
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
    
    private boolean areAnyDone(int[] rows) {
        for (int i=0;i<rows.length;i++) {
            String status = (String)((QueueTableModel)tblQueue.getModel()).getValueAt(rows[i],10);
            if (StringUtils.equals(status.toUpperCase(), FileTransferTask.getStatusString(Task.STOPPED)) || 
            		StringUtils.equals(status.toUpperCase(), FileTransferTask.getStatusString(Task.FAILED)) ||
                    StringUtils.equals(status.toUpperCase(), FileTransferTask.getStatusString(Task.DONE))) 
                return true;
        }
        
        return false;
    }
    
    public static Frame getFrame() {
        if (frame != null) return frame;
        Component parent = appMain.getParent();
        while (!(parent instanceof Frame)) {
            parent = parent.getParent();
        }
        
        return (Frame)parent;
    }
    
    public static void enableLogging(boolean doLog) {
        appMain.mnuFileLogging.setText(doLog?"Work offline":"Work online");
        ConfigOperation.setLoggingEnabled(doLog);
        
    }
    
//    public static AppMain getApplet() {
//        return appMain;
//    }
    
    public static JApplet getApplet() {
        return appMain;
    }
    
    public static boolean isApplet() {
        return isRunningAsApplet;
    }
    
    public static Logger getLogger() {
        return logger;
    }

    public static int getScheduleType() {
        return scheduleType;
    }

    public static void setScheduleType(int scheduleType) {
        AppMain.scheduleType = scheduleType;
    }
    
    public PnlBrowse getBrowsingPanel(int index) {
        if (index == 0) { 
            return pbLeft;
        } else {
            return pbRight;
        }
    }
    
    public static void updateSplash(int value, String message) {
        if (splashScreen != null) 
            splashScreen.update(value, message);
    }
    
    public static int Confirm(Component c, String sMsg){
        return Confirm(c, sMsg,"Confirm");
    }
    public static int Confirm(Component c, String sMsg,String sTitle){
        return JOptionPane.showConfirmDialog(c ,sMsg,sTitle,JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, (Icon)AppMain.icoPrompt);
    }

    public static void Error(Component c, String sMsg){
    	
        Error(c, sMsg,"Error");
    }
    public static void Error(Component c, String sMsg,String sTitle){
        JOptionPane.showMessageDialog(c,sMsg,sTitle,JOptionPane.ERROR_MESSAGE, (Icon)AppMain.icoPrompt);
        LogManager.error(sMsg);
    }

    public static void Message(Component c, String sMsg){
        Message(c, sMsg,"Message",JOptionPane.INFORMATION_MESSAGE);
    }
    public static void Message(Component c, String sMsg,String sTitle){
        Message(c, sMsg,sTitle,JOptionPane.INFORMATION_MESSAGE);
    }
    public static void Message(Component c, String sMsg, String sTitle, int nType){
        JOptionPane.showMessageDialog(c,sMsg,sTitle,nType, (Icon)AppMain.icoPrompt);
    }

    public static String Prompt(Component c, String sMsg){
        return (String)Prompt(c, sMsg,"Prompt",JOptionPane.INFORMATION_MESSAGE,null,null);
    }
    public static Object Prompt(Component c, String sMsg, String sTitle,Object oSelValue){
        return JOptionPane.showInputDialog(((getFrame()==null)?appMain.getParent():getFrame()),sMsg,sTitle,JOptionPane.INFORMATION_MESSAGE,null,null,oSelValue);
    }
    public static Object Prompt(Component c, String sMsg,String sTitle,int nType,Object[] oValues,Object oSelValue){
        return JOptionPane.showInputDialog(((getFrame()==null)?appMain.getParent():getFrame()),sMsg,sTitle,nType,(Icon)AppMain.icoPrompt,oValues,oSelValue );
    }
    
    
    public void MenuItem_actionPerformed(ActionEvent e) {
        if (e.getSource()==mnuGoTo) {
//            String dir = (String)Prompt((Component)e.getSource(),"Please enter the target directory.", "Go to folder:", JOptionPane.PLAIN_MESSAGE);
            pbRight.goHome();
        }
        else if (e.getSource()==mnuTGFBAbout) {
            new DlgAbout(getFrame());
        }
        else if (e.getSource()==mnuTGFBPrefs) {
            showPreferences();
        }
        else if (e.getSource()==mnuTGFBReload) {
            reloadApplet();
        }
        else if (e.getSource()==mnuTGFBExit) {
            pbLeft.disConn();
            pbRight.disConn();
            frame.dispose();
        }
        else if (e.getSource()==mnuFileAuth) {
        	try {
        		AppMain.defaultCredential = GridFTP.authorize();
        	} catch (Exception ex) {
        		AppMain.Error(AppMain.getFrame(), "Failed to reauthenticate you.");
        	}
        }
        else if (e.getSource()==mnuFileLogging) {
            AppMain.enableLogging(!ConfigOperation.isLoggingEnabled());
        }
        else if (e.getSource()==mnuFileOpenNew) {
            final JFrame frame = new JFrame("EUDAT File Manager");
            final AppMain app = new AppMain();
            AppMain.frame = frame;
            app.init();
            
            
            frame.addWindowListener(new WindowListener() {

                public void windowActivated(WindowEvent e) {}
                public void windowClosed(WindowEvent e) {}
                public void windowDeactivated(WindowEvent e) {}
                public void windowDeiconified(WindowEvent e) {}
                public void windowIconified(WindowEvent e) {}
                public void windowOpened(WindowEvent e) {}
                public void windowClosing(WindowEvent e) {
                    app.pbLeft.disConn();
                    app.pbRight.disConn();
                    frame.dispose();
                }                
            });
            
            frame.setJMenuBar(getJMenuBar());
            frame.getContentPane().add(app.getContentPane());
            
            frame.pack();
            frame.setVisible(true);
            app.mnuFileOpen.setEnabled(false);
            app.mnuTGFBExit.setEnabled(true);
        }
        else if (e.getSource()==mnuFileOpen) {
            final JFrame frame = new JFrame("EUDAT File Manager");
            final AppMain app = new AppMain();
            AppMain.frame = frame;
            app.init();
            
            // get a copy of the server settings for the current left panel
            FTPSettings svrLeft = ConfigOperation.getInstance().getSiteByName(pbLeft.getFtpServer().name);
            app.pbLeft = new PnlBrowse(app,svrLeft,pbLeft.getCurrentDir(),pbLeft.isRemote());
            app.spViewers.setLeftComponent(app.pbLeft);
            
         // get a copy of the server settings for the current right panel
            FTPSettings svrRight = ConfigOperation.getInstance().getSiteByName(pbRight.getFtpServer().name);
            app.pbRight = new PnlBrowse(app,svrRight,pbRight.getCurrentDir(),pbRight.isRemote());
            app.spViewers.setRightComponent(app.pbRight);
            
            app.validate();
            
            frame.addWindowListener(new WindowListener() {

                public void windowActivated(WindowEvent e) {}
                public void windowClosed(WindowEvent e) {}
                public void windowDeactivated(WindowEvent e) {}
                public void windowDeiconified(WindowEvent e) {}
                public void windowIconified(WindowEvent e) {}
                public void windowOpened(WindowEvent e) {}
                public void windowClosing(WindowEvent e) {
                    app.pbLeft.disConn();
                    app.pbRight.disConn();
                    frame.dispose();
                }                
            });
            
            frame.setJMenuBar(app.getJMenuBar());
            frame.getContentPane().add(app.getContentPane());
            frame.pack();
            frame.setVisible(true);
            app.mnuFileOpen.setEnabled(false);
            app.mnuTGFBExit.setEnabled(true);
        } 
        else if (mnuFileResetResources == e.getSource()) {
        	refreshResources();
        }
        else if (mnuViewShowTransfers == e.getSource()) {
            
            if (mnuViewShowTransfers.getText().equals("Hide transfers")) {
                mnuViewShowTransfers.setText("Show transfers");
                main.remove(pneQueue);
            } else {
                mnuViewShowTransfers.setText("Hide transfers");
                main.add(pneQueue,BorderLayout.SOUTH);
            }
            
            validate();
        }
        else if (mnuViewShowLog == e.getSource()) {
            setLogWindowVisible(!getLogWindow().isVisible());
        }
        
//        else if(mnuEditSiteAdd == e.getSource()){
//            mnuList_actionPerformed(new ActionEvent(mnuEditSiteAdd,ActionEvent.ACTION_PERFORMED,"Click"));
//        }
//        else if(mnuEditSiteDelete == e.getSource()){
//            mnuList_actionPerformed(new ActionEvent(mnuEditSiteDelete,ActionEvent.ACTION_PERFORMED,"Click"));
//        }
//        else if(mnuEditSiteModify == e.getSource()){
//            mnuList_actionPerformed(new ActionEvent(mnuEditSiteModify,ActionEvent.ACTION_PERFORMED,"Click"));
//        }
        
    }
    
    public void refreshResources() {
    	SwingWorker worker = new SwingWorker() {
			@Override
			public Object construct() {
				try {
		    		if (pbLeft.pnlSites != null && pbLeft.pnlSites.isVisible()) {
		    			pbLeft.pnlSites.startWaiting();
		    		}
		    		if (pbRight.pnlSites != null && pbRight.pnlSites.isVisible()) {
		    			pbRight.pnlSites.startWaiting();
		    		}
		
		    		ConfigOperation.getInstance().resetResources();
		    		
		    		pbLeft.updateSitesPanel();
		    		pbRight.updateSitesPanel();
		    		
		    	} catch (Exception e1) {
					LogManager.error("Failed retrieving resource list!!", e1);
				}
		    	return null;
			}
	    };
		
		worker.start();
    }
    /**
     * The applet jar will be cached on most systems. To get around this, we force load the applet
     * when the user clicks on the Reload menu item.
     */
    public void reloadApplet() {
    	getAppletContext().showDocument(getDocumentBase());
    	
    }
    
    public void showPreferences() {
		DlgOption dlg = new DlgOption(this, true);
    }
    
    public static LogWindow getLogWindow() {
    	if (logWindow == null) {
        	logWindow = new LogWindow(getFrame(),"File Manager Log",500,300);
        } 
    	return logWindow;
    }
    
    public void setLogWindowVisible(boolean visible) {
        
        mnuViewShowLog.setText(visible?"Hide console":"Show console");
        if (logWindow == null) {
        	logWindow = new LogWindow(getFrame(),"File Manager Log",500,300);
        }
        logWindow.setVisible(visible);

//        if (mnuViewShowLog.getText().equals("Hide Log")) {
//            	mnuViewShowLog.setText("Show Log");
//                logWindow.setVisible(false);
//            } else {
//            	mnuViewShowLog.setText("Hide Log");
//            	logWindow.setVisible(true);
//            }
//            
            validate();
    }
    
    private void registerKeyListeners() {
    	ActionListener actionListener = new ActionListener() {
	      public void actionPerformed(ActionEvent e) {
	    	  
	    	  if (e.getModifiers() == KeyEvent.CTRL_DOWN_MASK) {
	    		  if (e.getID() == KeyEvent.VK_P) {
		    		  showPreferences();
	    		  } else if (e.getID() == KeyEvent.VK_A) {
	    			  new DlgAbout(getFrame());
	    		  } else if (e.getID() == KeyEvent.VK_L) {
	    			  AppMain.enableLogging(!ConfigOperation.isLoggingEnabled());
	    		  } else if (e.getID() == KeyEvent.VK_T) {
	    			  if (mnuViewShowTransfers.getText().equals("Hide transfers")) {
	    				  mnuViewShowTransfers.setText("Show transfers");
	    				  main.remove(pneQueue);
	    			  } else {
	    				  mnuViewShowTransfers.setText("Hide transfers");
	    				  main.add(pneQueue,BorderLayout.SOUTH);
	    			  }
    	            
	    			  validate();
	    		  }
	    		  
	    	  }
	      }
	    };
	    
	    KeyStroke ksPrefs = KeyStroke.getKeyStroke(KeyEvent.VK_P, 0);
	    KeyStroke ksAbout = KeyStroke.getKeyStroke(KeyEvent.VK_A, KeyEvent.CTRL_DOWN_MASK);
	    KeyStroke ksViewLogging = KeyStroke.getKeyStroke(KeyEvent.VK_L, KeyEvent.META_DOWN_MASK);
	    KeyStroke ksViewTransfers = KeyStroke.getKeyStroke(KeyEvent.VK_T, KeyEvent.CTRL_DOWN_MASK);
	    getRootPane().registerKeyboardAction(actionListener, ksPrefs, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
	    getRootPane().registerKeyboardAction(actionListener, ksAbout, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
	    getRootPane().registerKeyboardAction(actionListener, ksViewLogging, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
	    getRootPane().registerKeyboardAction(actionListener, ksViewTransfers, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }
}

class frmMain_tblQueue_mouseAdapter extends MouseAdapter {
    private AppMain adaptee;
    frmMain_tblQueue_mouseAdapter(AppMain adaptee) {
        this.adaptee = adaptee;
    }

    public void mousePressed(MouseEvent e) {
        adaptee.tblQueue_mouseClicked(e);
    }
    
    public void mouseReleased(MouseEvent e) {
        adaptee.tblQueue_mouseClicked(e);
    }
}


class frmMain_mnuQueue_actionAdapter implements ActionListener {
    private AppMain adaptee;
    frmMain_mnuQueue_actionAdapter(AppMain adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.mnuQueue_actionPerformed(e);
    }
}

class frmMain_MenuItem_actionAdapter implements ActionListener {
    private AppMain adaptee;
    frmMain_MenuItem_actionAdapter(AppMain adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.MenuItem_actionPerformed(e);
    }
}
