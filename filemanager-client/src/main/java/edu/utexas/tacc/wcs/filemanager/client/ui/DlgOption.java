/*
 * Portions of this file Copyright 2004-2007 Shanghai Jiaotong University
 * 
 * This file or a portion of this file is licensed under the
 * terms of the Globus Toolkit Public License, found at
 * http://www.globus.org/toolkit/legal/4.0/
 * If you redistribute this file, with or without
 * modifications, you must include this notice in the file.
 */

package edu.utexas.tacc.wcs.filemanager.client.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.KeyStroke;
import javax.swing.ListCellRenderer;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.utexas.tacc.wcs.filemanager.client.AppMain;
import edu.utexas.tacc.wcs.filemanager.client.ConfigOperation;
import edu.utexas.tacc.wcs.filemanager.client.ConfigSettings;
import edu.utexas.tacc.wcs.filemanager.client.transfer.FTPLogin;
import edu.utexas.tacc.wcs.filemanager.client.transfer.FTPSettings;
import edu.utexas.tacc.wcs.filemanager.client.util.LogManager;
import edu.utexas.tacc.wcs.filemanager.client.util.ResourceName;
import edu.utexas.tacc.wcs.filemanager.client.util.SGGCResourceBundle;
import edu.utexas.tacc.wcs.filemanager.common.model.enumeration.FileProtocolType;
import edu.utexas.tacc.wcs.filemanager.common.model.enumeration.NotificationType;
import edu.utexas.tacc.wcs.filemanager.common.model.enumeration.SystemType;

//TODO: optimize the buffer window based on resource bandwith predictions

@SuppressWarnings("serial")
public class DlgOption extends DlgEscape {
    
	private static int MIN_CONNECTIONS = 2;
    private static int MIN_RETRY = 1;
    
    private KeyListener returnListener = new KeyAdapter() {

		@Override
		public void keyPressed(KeyEvent e) {
			// when the user hits enter in the amazon or s3 text boxes, it
			// is the same as pressing the OK button.
			if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				btnOK.doClick();
			}
		}
    };
    private JPanel pnlMain = new JPanel();
    private JButton btnOK = new JButton();
    private JButton btnCancel = new JButton();
    private JTabbedPane pnlTabbed = new JTabbedPane();
    private JPanel pnlGeneral = new JPanel();
    private JPanel pnlConnection = new JPanel();
    private JPanel pnlSecurity = new JPanel();
    private JPanel pnlNotification = new JPanel();
    private JPanel pnlS3 = new JPanel();
    private JPanel pnlTGShare = new JPanel();
//    private JPanel pnlMyProxy = new JPanel();
    
    private JTextField txtLog = new JTextField();
    private JLabel lblLog = new JLabel();
    private JButton btnLog = new JButton();
    private JLabel lblConnPara = new JLabel();
    private JSpinner spnConnPara = new JSpinner();
    private JLabel lblConnMax = new JLabel();
    private JSpinner spnConnMax = new JSpinner();
    private JLabel lblConnRetry = new JLabel();
    private JSpinner spnConnRetry = new JSpinner();
    private JLabel lblConnDelay = new JLabel();
    private JSpinner spnConnDelay = new JSpinner();
    private JLabel lblConnData = new JLabel();
    private JComboBox cmbConnData = new JComboBox(new Object[]{"PASV","PORT"});
    private JLabel lblConnKeep = new JLabel();
    private JSpinner spnConnKeep = new JSpinner();
    private JLabel lblCert = new JLabel();
    private JTextField txtCert = new JTextField();
    private JButton btnCert = new JButton();
    private JLabel lblDownload = new JLabel();
    private JTextField txtDownload = new JTextField();
    private JButton btnDownload = new JButton();
    private JCheckBox chkShowHidden = new JCheckBox();
    private JCheckBox chkStripeTransfer = new JCheckBox();
    private JPanel pnlRFT = new JPanel();
    private JComboBox cbxFactories = new JComboBox();
    private JComboBox cbxServices = new JComboBox();
    private JComboBox cbxNotifications = new JComboBox();
    private JComboBox cbxSite = new JComboBox();
    private JLabel lblRFTFactory = new JLabel();
    private JLabel lblRFT = new JLabel();
    private JLabel lblRFTService = new JLabel();
    private JLabel lblNotifications = new JLabel();
    private JToggleButton btnAdd = new JToggleButton();
    private JToggleButton btnEdit = new JToggleButton();
//    private TitledBorder titledBorder1 = new TitledBorder("");
    private JLabel jLabel1 = new JLabel();
    private JLabel jLabel2 = new JLabel();
    private JTextField txtService = new JTextField();
    private JTextField txtFactory = new JTextField();
    private JButton btnConfirm = new JButton();
    private ButtonGroup bgRFT = new ButtonGroup();
    private JToggleButton btnDelete = new JToggleButton();
    private JToggleButton btnDefault = new JToggleButton();
    private JButton btnAbort = new JButton();
    private JLabel lblBufferSize = new JLabel();
    private JSpinner spnBufferSize = new JSpinner();
    private JButton btnOptimizeBuffer = new JButton();
    
    // Objects for search options tab
    private JPanel pnlSearch = new JPanel();
    private JLabel lblSearchDepth = new JLabel();
    private JSpinner spnSearchDepth = new JSpinner();
    private JLabel lblMaxSearchResults = new JLabel();
    private JSpinner spnMaxSearchResults = new JSpinner();
    private JLabel lblExcludeResources = new JLabel();
    private JButton btnExcludeResources = new JButton();
    private JCheckBox[] chkBoxes; 
    
    // Objects for S3 options
    private JCheckBox chkEnableS3 = new JCheckBox();
    private JLabel lblAwsAccessKeyId = new JLabel();
    private JTextField txtAwsAccessKeyId = new JTextField();
    private JLabel lblAwsSecretAccessKey = new JLabel();
    private JPasswordField pwdAwsSecretAccessKey = new JPasswordField();
    
 // Objects for $SHARE options
//    private JCheckBox chkEnableTGShare = new JCheckBox();
//    private JLabel lblTGShareUsername = new JLabel();
//    private JTextField txtTGShareUsername = new JTextField();
//    private JLabel lblTGSharePassword = new JLabel();
//    private JPasswordField pwdTGSharePassword = new JPasswordField();
    
    private JLabel lblSched = new JLabel();
    private JComboBox cmbSched = new JComboBox(AppMain.scheduleName);
    private AppMain owner = null;
    //private FTPSettings[] ftpSettings = null;//new FTPSettings[]{};
    private String[] sites;
    private String excludes = "";
    
    
    public DlgOption(AppMain owner, boolean modal) {
        super(AppMain.getFrame(), "Site Dialog Options", modal);
        this.owner = owner;

        ArrayList<FTPSettings> servers = new ArrayList<FTPSettings>();
        
        try {
            
            servers.add(owner.getBrowsingPanel(0).ftpServer.clone(owner.getBrowsingPanel(0)));
            
            if(!owner.getBrowsingPanel(0).ftpServer.host.equals(owner.getBrowsingPanel(1).ftpServer.host)) {
                servers.add(owner.getBrowsingPanel(1).ftpServer.clone(owner.getBrowsingPanel(1)));
            }
            
        } catch (Exception e) {
            LogManager.error(((owner.getBrowsingPanel(0) == null)?"Left":"Right") + " server is null");
        }
        
        try {
            ConfigOperation config = ConfigOperation.getInstance();
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            jbInit();
            Dimension dlgSize = getPreferredSize();
            Dimension frmSize = owner.getSize();
            Point loc = owner.getLocation();
            setLocation((frmSize.width - dlgSize.width) / 2 + loc.x, (frmSize.height - dlgSize.height) / 2 + loc.y);
            pack();
            
            txtLog.setText(FTPSettings.DefaultLogfile);
            chkShowHidden.setSelected(servers.get(0).showHidden);
            chkStripeTransfer.setSelected(servers.get(0).stripeTransfers);
            spnConnRetry.setModel(new SpinnerNumberModel((servers.get(0).connMaxNum<MIN_RETRY)?MIN_RETRY:servers.get(0).connRetry,MIN_RETRY,50,1));
            spnConnDelay.setModel(new SpinnerNumberModel(servers.get(0).connDelay,0,120,5));
            spnConnKeep.setModel(new SpinnerNumberModel(servers.get(0).connKeepAlive,0,120,5));
            
            spnBufferSize.setModel(new SpinnerNumberModel(
                    ((servers.get(0).bufferSize > 0)?servers.get(0).bufferSize:FTPSettings.DefaultBufferSize),
                    Speedpage.DEFAULT_MIN_BUFFER_SIZE,
                    Speedpage.DEFAULT_THIRD_PARTY_BUFFER_SIZE*2,
                    1));
            spnConnPara.setModel(new SpinnerNumberModel(servers.get(0).connParallel,1,20,1));
            cmbConnData.setSelectedIndex(servers.get(0).passiveMode?0:1);
            txtCert.setText(config.getConfigValue("proxy"));
            String downloadDir = config.getConfigValue("download_dir");
            txtDownload.setText((downloadDir==null||downloadDir.equals(""))?System.getProperty("user.home"):downloadDir);
            cmbSched.setSelectedIndex(AppMain.getScheduleType());
            spnMaxSearchResults.setModel(new SpinnerNumberModel(servers.get(0).maxSearchResults,0,500,1));
            spnSearchDepth.setModel(new SpinnerNumberModel(servers.get(0).maxSearchDepth,1,20,1));
            excludes = config.getConfigValue("excludeFromSearch");
            
            // enable/disable S3 access
            FTPSettings s3Site = config.getSiteByName("Amazon S3");
            
            chkEnableS3.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    txtAwsAccessKeyId.setEnabled(chkEnableS3.isSelected());
                    pwdAwsSecretAccessKey.setEnabled(chkEnableS3.isSelected());
                }
            });
            txtAwsAccessKeyId.setText(s3Site == null?"":s3Site.userName);
            pwdAwsSecretAccessKey.setText(s3Site == null?"":s3Site.password);
            chkEnableS3.setSelected(s3Site != null);
            
            // enable/disable TeraGrid $SHARE access
//            FTPSettings tgShareSite = config.getSiteByName("TeraGrid $SHARE");
//            
//            chkEnableTGShare.addActionListener(new ActionListener() {
//                public void actionPerformed(ActionEvent e) {
//                    txtTGShareUsername.setEnabled(chkEnableTGShare.isSelected());
//                    pwdTGSharePassword.setEnabled(chkEnableTGShare.isSelected());
//                }
//            });
//            txtTGShareUsername.setText(tgShareSite == null?"":tgShareSite.userName);
//            pwdTGSharePassword.setText(tgShareSite == null?"":tgShareSite.password);
//            chkEnableTGShare.setSelected(tgShareSite != null);
            
            sites = config.getSiteNames();
            
            chkBoxes = new JCheckBox[sites.length];
            
            cbxSite.setModel(new DefaultComboBoxModel(servers.toArray()));
            cbxSite.setRenderer(new ListCellRenderer() {

				public Component getListCellRendererComponent(JList list,
						Object value, int index, boolean isSelected,
						boolean cellHasFocus) {
					JLabel label = new JLabel(((FTPSettings)value).name);
					if (isSelected) {
						label.setBackground(list.getSelectionBackground());
						label.setForeground(list.getSelectionForeground());
			        } else {
			        	label.setBackground(list.getBackground());
			        	label.setForeground(list.getForeground());
			        }
					label.setOpaque(true);
					return label;
				}
            	
            });
//            Iterator iterator = (config.getRFTServices().keySet()).iterator();
//            
//            while(iterator.hasNext()){
//                String service = (String)iterator.next();
//                String factory = (String)config.getRFTServices().get(service);
//
//                this.cbxServices.addItem(service);
//                this.cbxFactories.addItem(factory);
//
//            }

            cbxServices.addItemListener(new frmOption_cbxServices_itemAdapter(this));
            
            setEditVisible(false);
            
            setVisible(true);
            setFocusable(true);
            requestFocus();
            
            
            ActionExample action = new ActionExample(this);
            Object binding = action.getValue("escape");
            KeyStroke escape = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE,ActionEvent.CTRL_MASK,false);
            this.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escape, binding);
            this.getRootPane().getActionMap().put(binding, action);
            
        } catch (Exception exception) {
            exception.printStackTrace();
            edu.utexas.tacc.wcs.filemanager.client.util.LogManager.debug(exception.getLocalizedMessage() + " at " + (exception.getStackTrace())[0]);  
        }
    }

    protected void exit() {
        this.dispose();
    }
//    public DlgOption() {
//        this(new Frame(), SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_DLGOPTION_TITLE), false);
//    }

    private void jbInit() throws Exception {
        pnlTabbed.setBounds(new Rectangle(19, 8, 464, 382));
        pnlGeneral.setLayout(null);
        pnlConnection.setLayout(null);
        pnlSecurity.setLayout(null);
        pnlNotification.setLayout(null);
        pnlSearch.setLayout(null);
        pnlS3.setLayout(null);
        pnlTGShare.setLayout(null);
        lblLog.setText(SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_DLGOPTION_LBLLOG_TITLE));
        lblLog.setBounds(new Rectangle(8, 16, 130, 22));
        txtLog.setBounds(new Rectangle(144, 18, 213, 22));
        btnLog.setBounds(new Rectangle(360, 18, 88, 22));
        btnLog.setText(SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_DLGOPTION_BTNLOG_TITLE));
        btnLog.addActionListener(new frmOption_btnLog_actionAdapter(this));
//        chkShowHidden.setText(SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_DLGOPTION_CHKSHOWHIDDEN_TITLE));
//        chkShowHidden.setBounds(new Rectangle(14, 76, 207, 23));
//        lblConnMax.setText(SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_DLGOPTION_LBLCONNMAX_TITLE));
//        lblConnMax.setBounds(new Rectangle(14, 12, 160, 23));
//        lblConnRetry.setText(SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_DLGOPTION_LBLCONNRETRY_TITLE));
//        lblConnRetry.setBounds(new Rectangle(14, 44, 160, 23));
//        lblConnDelay.setText(SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_DLGOPTION_LBLCONNDELAY_TITLE));
//        lblConnDelay.setBounds(new Rectangle(14, 76, 160, 23));
//        lblConnKeep.setText(SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_DLGOPTION_LBLCONNKEEP_TITLE));
//        lblConnKeep.setBounds(new Rectangle(14, 108, 160, 23));
//        lblConnPara.setText(SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_DLGOPTION_LBLCONNPARA_TITLE));
//        lblConnPara.setBounds(new Rectangle(14, 140, 160, 23));
//        lblBufferSize.setText(SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_DLGOPTION_LBLBUFSIZE_TITLE));
//        lblBufferSize.setBounds(new Rectangle(14, 172, 160, 23));
//        lblConnData.setText(SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_DLGOPTION_LBLCONNDATA_TITLE));
//        lblConnData.setBounds(new Rectangle(14, 205, 160, 23));
//        lblSched.setBounds(new Rectangle(14, 236, 160, 23));
        chkShowHidden.setText(SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_DLGOPTION_CHKSHOWHIDDEN_TITLE));
        chkShowHidden.setBounds(new Rectangle(14, 12, 207, 23));
        chkStripeTransfer.setText("Stripe Transfers");
        chkStripeTransfer.setBounds(new Rectangle(14, 44, 207, 23));
        lblConnMax.setText(SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_DLGOPTION_LBLCONNMAX_TITLE));
        lblConnMax.setBounds(new Rectangle(14, 76, 160, 23));
        lblConnRetry.setText(SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_DLGOPTION_LBLCONNRETRY_TITLE));
        lblConnRetry.setBounds(new Rectangle(14, 108, 160, 23));
        lblConnDelay.setText(SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_DLGOPTION_LBLCONNDELAY_TITLE));
        lblConnDelay.setBounds(new Rectangle(14, 140, 160, 23));
        lblConnKeep.setText(SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_DLGOPTION_LBLCONNKEEP_TITLE));
        lblConnKeep.setBounds(new Rectangle(14, 172, 160, 23));
        lblConnPara.setText(SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_DLGOPTION_LBLCONNPARA_TITLE));
        lblConnPara.setBounds(new Rectangle(14, 205, 160, 23));
        lblBufferSize.setText(SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_DLGOPTION_LBLBUFSIZE_TITLE));
        lblBufferSize.setBounds(new Rectangle(14, 236, 160, 23));
        btnOptimizeBuffer.setText("Optimize");
        btnOptimizeBuffer.setBounds(new Rectangle(256, 236, 95, 25));
        btnOptimizeBuffer.addActionListener(new frmOption_btnOptimizeBuffer_actionListener(this));
        lblConnData.setText(SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_DLGOPTION_LBLCONNDATA_TITLE));
        lblConnData.setBounds(new Rectangle(14, 267, 160, 23));
        lblSched.setBounds(new Rectangle(14, 299, 160, 23));
        lblSched.setText("Scheduler");
        spnConnMax.setBounds(new Rectangle(176, 78, 78, 20));
        spnConnRetry.setBounds(new Rectangle(176, 110, 78, 20));
        spnConnDelay.setBounds(new Rectangle(176, 142, 78, 20));
        spnConnKeep.setBounds(new Rectangle(176, 174, 78, 20));
        spnConnPara.setBounds(new Rectangle(176, 205, 78, 20));
        spnBufferSize.setBounds(new Rectangle(176, 236, 78, 20));
        cmbConnData.setBounds(new Rectangle(176, 268, 78, 20));
        cmbSched.setBounds(new Rectangle(176, 300, 160, 23));
        lblCert.setText(SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_DLGOPTION_LBLCERT_TITLE));
        lblCert.setBounds(new Rectangle(9, 19, 99, 22));
        txtCert.setEnabled(false);
        txtCert.setBounds(new Rectangle(111, 18, 225, 22));
        btnCert.setBounds(new Rectangle(347, 18, 95, 22));
        btnCert.setText(SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_DLGOPTION_BTNCERT_TITLE));
        btnCert.addActionListener(new frmOption_btnCert_actionAdapter(this));
        lblDownload.setText("Download To:");
        lblDownload.setBounds(new Rectangle(9, 51, 99, 22));
        txtDownload.setEnabled(false);
        txtDownload.setBounds(new Rectangle(111, 50, 225, 22));
        btnDownload.setBounds(new Rectangle(347, 50, 95, 22));
        btnDownload.setText(SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_DLGOPTION_BTNCERT_TITLE));
        btnDownload.addActionListener(new frmOption_btnDownload_actionAdapter(this));
        cbxFactories.setEnabled(false);
        cbxFactories.setEditable(true);
        cbxFactories.setBounds(new Rectangle(80, 90, 368, 22));
        cbxServices.setEditable(false);
        cbxServices.setBounds(new Rectangle(79, 44, 369, 22));
        lblRFTFactory.setText(SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_DLGOPTION_LBLRFTFACTORY_TITLE));
        lblRFTFactory.setBounds(new Rectangle(11, 46, 71, 17));
        lblRFT.setText(SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_DLGOPTION_LBLRFT_TITLE));
        lblRFT.setBounds(new Rectangle(10, 13, 81, 17));
        pnlRFT.setLayout(null);
        lblRFTService.setText(SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_DLGOPTION_LBLRFTSVC_TITLE));
        lblRFTService.setBounds(new Rectangle(10, 90, 71, 17));
        btnAdd.setText(SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_ADD));
        btnAdd.setBounds(new Rectangle(19, 130, 67, 29));
        btnAdd.addItemListener(new frmOption_btnAdd_itemAdapter(this));
        btnEdit.setText(SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_EDIT));
        btnEdit.setBounds(new Rectangle(101, 130, 71, 29));
        btnEdit.addItemListener(new frmOption_btnEdit_itemAdapter(this));
        jLabel1.setText(SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_DLGOPTION_JLABEL1_TITLE));
        jLabel1.setBounds(new Rectangle(11, 183, 50, 24));
        jLabel2.setText(SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_DLGOPTION_JLABEL2_TITLE));
        jLabel2.setBounds(new Rectangle(10, 220, 51, 22));
        txtService.setBounds(new Rectangle(69, 180, 384, 25));
        txtFactory.setBounds(new Rectangle(67, 223, 385, 23));
        btnConfirm.setBounds(new Rectangle(359, 131, 89, 27));
        btnConfirm.addActionListener(new frmOption_btnConfirm_actionAdapter(this));
        btnDelete.setText(SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_DELETE));
        btnDelete.setBounds(new Rectangle(182, 130, 79, 29));
        btnDelete.addItemListener(new frmOption_btnDelete_itemAdapter(this));
        btnDefault.setText(SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_DLGOPTION_BTNDEFAULT_TITLE));
        btnDefault.setBounds(new Rectangle(275, 131, 50, 28));
        btnAbort.setBounds(new Rectangle(290, 131, 68, 27));
        btnAbort.setText(SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_DLGOPTION_BTNABORT_TITLE));
        btnAbort.addActionListener(new DlgOption_btnAbort_actionAdapter(this));
        
        // Notification layout
        lblNotifications.setText(SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_DLGOPTION_LBLNOTIFICATION_TITLE));
        lblNotifications.setBounds(new Rectangle(14, 12, 160, 23));
        //disable IM and text messaging right now
//        cbxNotifications.setModel(new DefaultComboBoxModel(NotificationType.values()));
        cbxNotifications.setModel(new DefaultComboBoxModel(new NotificationType[]{NotificationType.EMAIL,NotificationType.NONE}));
        cbxNotifications.setSelectedItem(NotificationType.getType(ConfigOperation.getInstance().getConfigValue("notification")));
        cbxNotifications.setBounds(new Rectangle(176, 14, 160, 23));
        
        // Search layout
        lblSearchDepth.setText("Maximum Search Depth:");
        lblSearchDepth.setBounds(new Rectangle(14, 14, 170, 23));
        lblMaxSearchResults.setText("Maximum Search Results:");
        lblMaxSearchResults.setBounds(new Rectangle(14, 45, 170, 23));
        lblExcludeResources.setText("Excluded Resources:");
        lblExcludeResources.setBounds(new Rectangle(14, 76, 170, 23));
        
        spnSearchDepth.setBounds(new Rectangle(186, 14, 78, 23));
        spnMaxSearchResults.setBounds(new Rectangle(186, 45, 78, 23));
        btnExcludeResources.setText("Select");
        btnExcludeResources.addActionListener(new DlgOption_btnExcludeResources_actionAdapter(this));
        btnExcludeResources.setBounds(new Rectangle(186, 76, 78, 20));
        
        // S3 layout
        chkEnableS3.setText("Enable Amazon S3");
        chkEnableS3.setBounds(new Rectangle(14, 12, 207, 23));
        lblAwsAccessKeyId.setText("AWS Key Id:");
        lblAwsAccessKeyId.setBounds(new Rectangle(14, 44, 207, 23));
        lblAwsSecretAccessKey.setText("AWS Secret Key:");
        lblAwsSecretAccessKey.setBounds(new Rectangle(14, 76, 207, 23));
        txtAwsAccessKeyId.setBounds(new Rectangle(116, 44, 225, 22));
        txtAwsAccessKeyId.addKeyListener(returnListener);
        pwdAwsSecretAccessKey.setBounds(new Rectangle(116, 76, 225, 22));
        pwdAwsSecretAccessKey.addKeyListener(returnListener);
        
        // TeraGrid $SHARE layout
//        chkEnableTGShare.setText("Enable TeraGrid $SHARE");
//        chkEnableTGShare.setBounds(new Rectangle(14, 12, 207, 23));
//        lblTGShareUsername.setText("TGUP Username:");
//        lblTGShareUsername.setBounds(new Rectangle(14, 44, 207, 23));
//        lblTGSharePassword.setText("TGUP Password:");
//        lblTGSharePassword.setBounds(new Rectangle(14, 76, 207, 23));
//        txtTGShareUsername.setBounds(new Rectangle(116, 44, 225, 22));
//        txtTGShareUsername.addKeyListener(returnListener);
//        pwdTGSharePassword.setBounds(new Rectangle(116, 76, 225, 22));
//        pwdTGSharePassword.addKeyListener(returnListener);
        
        pnlMain.add(btnCancel);
        pnlMain.add(btnOK);
        pnlMain.add(cbxSite);
        pnlMain.add(pnlTabbed);
        
//        pnlTabbed.add(pnlGeneral, SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_DLGOPTION_PNLGENERAL_TITLE));		//
        //pnlTabbed.add(pnlConnection, SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_DLGOPTION_PNLCONNECT_TITLE));
        pnlTabbed.add(pnlSecurity, SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_DLGOPTION_PNLSECURITY_TITLE));
        pnlTabbed.add(pnlNotification, SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_DLGOPTION_PNLNOTIFICATION_TITLE));
        pnlTabbed.add(pnlSearch,"Search");
        //pnlTabbed.add(pnlS3,"S3");
        //pnlTabbed.add(pnlTGShare,"$SHARE");
        pnlTabbed.addChangeListener(new DlgOption_pnlTabbed_changeListener(this));
        
        pnlSecurity.add(lblCert);
        pnlSecurity.add(txtCert);
        pnlSecurity.add(btnCert);
        pnlSecurity.add(lblDownload);
        pnlSecurity.add(txtDownload);
        pnlSecurity.add(btnDownload);
        
        pnlConnection.add(chkStripeTransfer);
        pnlConnection.add(chkShowHidden);
        pnlConnection.add(spnConnRetry);
        pnlConnection.add(lblConnRetry);
        pnlConnection.add(lblConnMax);
        pnlConnection.add(spnConnMax);
        pnlConnection.add(lblConnDelay);
        pnlConnection.add(spnConnDelay);
        pnlConnection.add(lblConnKeep);
        pnlConnection.add(spnConnKeep);
        pnlConnection.add(lblConnPara);
        pnlConnection.add(spnConnPara);
        pnlConnection.add(cmbConnData);
        pnlConnection.add(lblConnData);
        pnlConnection.add(lblBufferSize);
        pnlConnection.add(spnBufferSize);
        pnlConnection.add(btnOptimizeBuffer);
        pnlConnection.add(lblSched);
        pnlConnection.add(cmbSched);
        
        pnlNotification.add(lblNotifications);
        pnlNotification.add(cbxNotifications);
        
        pnlSearch.add(lblSearchDepth);
        pnlSearch.add(spnSearchDepth);
        pnlSearch.add(lblMaxSearchResults);
        pnlSearch.add(spnMaxSearchResults);
//        pnlSearch.add(lblExcludeResources);
//        pnlSearch.add(btnExcludeResources);
        pnlS3.add(chkEnableS3);
        pnlS3.add(lblAwsAccessKeyId);
        pnlS3.add(txtAwsAccessKeyId);
        pnlS3.add(lblAwsSecretAccessKey);
        pnlS3.add(pwdAwsSecretAccessKey);
        
//        pnlTGShare.add(chkEnableTGShare);
//        pnlTGShare.add(lblTGShareUsername);
//        pnlTGShare.add(txtTGShareUsername);
//        pnlTGShare.add(lblTGSharePassword);
//        pnlTGShare.add(pwdTGSharePassword);
        
        pnlGeneral.add(txtLog);
        pnlGeneral.add(btnLog);
        pnlGeneral.add(lblLog);
//        pnlGeneral.add(chkShowHidden);
        getContentPane().add(pnlMain);
//        pnlTabbed.add(pnlRFT, SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_DLGOPTION_PNLRTF_TITLE));	//
        pnlRFT.add(lblRFT);
        pnlRFT.add(cbxServices);
        pnlRFT.add(cbxFactories);
        pnlRFT.add(lblRFTService);
        pnlRFT.add(lblRFTFactory);
        pnlRFT.add(btnAdd);
        pnlRFT.add(jLabel1);
        pnlRFT.add(jLabel2);
        pnlRFT.add(txtService);
        pnlRFT.add(txtFactory);
        pnlRFT.add(btnConfirm);
        pnlRFT.add(btnDelete);
        pnlRFT.add(btnEdit);
        pnlRFT.add(btnDefault);
        pnlRFT.add(btnAbort);
        btnOK.setBounds(new Rectangle(300, 387, 85, 26));
        btnOK.setText(SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_OK));
        btnOK.addActionListener(new frmOption_btnOK_actionAdapter(this));
        btnCancel.setBounds(new Rectangle(385, 387, 100, 26));
        btnCancel.setText(SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_CANCEL));
        btnCancel.addActionListener(new frmOption_btnCancel_actionAdapter(this));
        cbxSite.setBounds(new Rectangle(33, 387, 160, 26));
        cbxSite.addActionListener(new frmOption_cbxSite_ActionListener(this));
        cbxSite.setEditable(false);
        pnlMain.setLayout(null);
        pnlMain.setMinimumSize(new Dimension(500, 418));
        pnlMain.setPreferredSize(new Dimension(500, 418));
        bgRFT.add(btnAdd);
        bgRFT.add(btnEdit);
        bgRFT.add(btnDelete);
        bgRFT.add(btnDefault);
        btnDefault.setVisible(false);
        setResizable(false);
    }

    private void setEditVisible(boolean visible){
        jLabel1.setVisible(visible);
        jLabel2.setVisible(visible);
        txtService.setVisible(visible);
        txtFactory.setVisible(visible);
        btnConfirm.setVisible(visible);
        btnAbort.setVisible(visible);
    }
    
    /**
     * Store the current form settings in the local FTPSettings object of the
     * selected server.
     * 
     * @param index
     */
    private void storeFields(int index) {
        
    	FileProtocolType ftpType = ((FTPSettings)cbxSite.getItemAt(index)).protocol;
        ((FTPSettings)cbxSite.getItemAt(index)).passiveMode=(0==cmbConnData.getSelectedIndex());
        ((FTPSettings)cbxSite.getItemAt(index)).connRetry=Integer.parseInt(spnConnRetry.getValue().toString());
        ((FTPSettings)cbxSite.getItemAt(index)).connDelay=Integer.parseInt(spnConnDelay.getValue().toString());
        ((FTPSettings)cbxSite.getItemAt(index)).connParallel=Integer.parseInt(spnConnPara.getValue().toString());
        ((FTPSettings)cbxSite.getItemAt(index)).connMaxNum=Integer.parseInt(spnConnMax.getValue().toString());
        ((FTPSettings)cbxSite.getItemAt(index)).connKeepAlive=Integer.parseInt(spnConnKeep.getValue().toString());
        ((FTPSettings)cbxSite.getItemAt(index)).bufferSize=Integer.parseInt(spnBufferSize.getValue().toString());
        ((FTPSettings)cbxSite.getItemAt(index)).showHidden=(chkShowHidden.isSelected());
        ((FTPSettings)cbxSite.getItemAt(index)).stripeTransfers=(chkStripeTransfer.isSelected());
        ((FTPSettings)cbxSite.getItemAt(index)).maxSearchDepth = Integer.parseInt(spnSearchDepth.getValue().toString());
        ((FTPSettings)cbxSite.getItemAt(index)).maxSearchResults = Integer.parseInt(spnMaxSearchResults.getValue().toString());
        if (ftpType.equals(FileProtocolType.S3)) {
        	((FTPSettings)cbxSite.getItemAt(index)).userName = txtAwsAccessKeyId.getText();
            ((FTPSettings)cbxSite.getItemAt(index)).password = new String(pwdAwsSecretAccessKey.getPassword());
        } 
//        else {
//        	((FTPSettings)cbxSite.getItemAt(index)).userName = txtTGShareUsername.getText();
//            ((FTPSettings)cbxSite.getItemAt(index)).password = new String(pwdTGSharePassword.getPassword());
//        }
        
    }
    
    /**
     * Update the form with the values of the FTPSettings object represented
     * in the combo box at that index. 
     * 
     * @param index
     */
    private void updateFields(int index)  {
    	FileProtocolType ftpType = ((FTPSettings)cbxSite.getItemAt(index)).protocol;
        if (ftpType.equals(FileProtocolType.S3)) {
            cmbConnData.setEnabled(false);
            spnConnRetry.setValue(new Integer(((FTPSettings)cbxSite.getItemAt(index)).connRetry));
            spnConnDelay.setValue(new Integer(((FTPSettings)cbxSite.getItemAt(index)).connDelay));
            spnConnPara.setEnabled(false);
            spnConnMax.setValue(new Integer(((FTPSettings)cbxSite.getItemAt(index)).connMaxNum));
            spnConnKeep.setEnabled(false);
            spnBufferSize.setValue(new Integer(((FTPSettings)cbxSite.getItemAt(index)).bufferSize));
            chkShowHidden.setSelected(true);
            chkShowHidden.setEnabled(false);
            chkStripeTransfer.setSelected(false);
            chkStripeTransfer.setEnabled(false);
            spnMaxSearchResults.setValue(new Integer(((FTPSettings)cbxSite.getItemAt(index)).maxSearchResults));
            spnSearchDepth.setValue(new Integer(((FTPSettings)cbxSite.getItemAt(index)).maxSearchDepth));
            txtAwsAccessKeyId.setText(((FTPSettings)cbxSite.getItemAt(index)).userName);
	        pwdAwsSecretAccessKey.setText(((FTPSettings)cbxSite.getItemAt(index)).password);
             
//            else {
//            	txtTGShareUsername.setText(((FTPSettings)cbxSite.getItemAt(index)).userName);
//            	pwdTGSharePassword.setText(((FTPSettings)cbxSite.getItemAt(index)).userName);
//            }
        } else {
            cmbConnData.setSelectedIndex((((FTPSettings)cbxSite.getItemAt(index)).passiveMode)?0:1);
            cmbConnData.setEnabled(true);
            spnConnRetry.setValue(new Integer(((FTPSettings)cbxSite.getItemAt(index)).connRetry));
            spnConnDelay.setValue(new Integer(((FTPSettings)cbxSite.getItemAt(index)).connDelay));
            spnConnPara.setValue(new Integer(((FTPSettings)cbxSite.getItemAt(index)).connParallel));
            spnConnPara.setEnabled(true);
            spnConnMax.setValue(new Integer(((FTPSettings)cbxSite.getItemAt(index)).connMaxNum));
            spnConnKeep.setValue(new Integer(((FTPSettings)cbxSite.getItemAt(index)).connKeepAlive));
            spnBufferSize.setValue(new Integer(((FTPSettings)cbxSite.getItemAt(index)).bufferSize));
            chkShowHidden.setSelected(((FTPSettings)cbxSite.getItemAt(index)).showHidden);
            chkShowHidden.setEnabled(true);
            chkStripeTransfer.setSelected(((FTPSettings)cbxSite.getItemAt(index)).stripeTransfers);
            chkStripeTransfer.setEnabled(true);
            spnMaxSearchResults.setValue(new Integer(((FTPSettings)cbxSite.getItemAt(index)).maxSearchResults));
            spnSearchDepth.setValue(new Integer(((FTPSettings)cbxSite.getItemAt(index)).maxSearchDepth));
//            if (ftpType == FTPType.XSHARE) {
//	            txtTGShareUsername.setText(((FTPSettings)cbxSite.getItemAt(index)).userName);
//	            pwdTGSharePassword.setText(((FTPSettings)cbxSite.getItemAt(index)).password);
//            }
            
//            txtAwsAccessKeyId.setText(((FTPSettings)cbxSite.getItemAt(index)).userName);
//            pwdAwsSecretAccessKey.setText(((FTPSettings)cbxSite.getItemAt(index)).password);
        }
        LogManager.debug("Site values are " + ((FTPSettings)cbxSite.getItemAt(index)).maxSearchDepth + " " + ((FTPSettings)cbxSite.getItemAt(index)).maxSearchResults);
        
    }

    public void btnOK_actionPerformed(ActionEvent e) {
        ConfigOperation config = ConfigOperation.getInstance();
        // Save the current selected server settings
        storeFields(this.cbxSite.getSelectedIndex());
        
        // enable or disable S3 based on the S3 panel
        if (chkEnableS3.isSelected()) {
            if (!validateS3(config)) {
            	if (txtAwsAccessKeyId.getText().equals("")) {
            		txtAwsAccessKeyId.hasFocus();
            	} else {
            		pwdAwsSecretAccessKey.hasFocus();
            	}
            	return;
            }
        } else {
            if (config.getSiteByName("Amazon S3") != null) {
                config.deleteSite(config.getSiteByName("Amazon S3"));
            }
        }
        
        // enable or disable S3 based on the S3 panel
//        if (chkEnableTGShare.isSelected()) {
//            validateTGShare(config);
//        } else {
//            if (config.getSiteByName("TeraGrid $SHARE") != null) {
//                config.deleteSite(config.getSiteByName("TeraGrid $SHARE"));
//                
//            }
//        }
        
        // update the server settings in both panels
        for (int i=0;i<cbxSite.getItemCount();i++) {
            owner.getBrowsingPanel(i).ftpServer.stripeTransfers=((FTPSettings)cbxSite.getItemAt(i)).stripeTransfers;
            owner.getBrowsingPanel(i).ftpServer.showHidden=((FTPSettings)cbxSite.getItemAt(i)).showHidden;
            owner.getBrowsingPanel(i).ftpServer.connMaxNum=((FTPSettings)cbxSite.getItemAt(i)).connMaxNum;
            owner.getBrowsingPanel(i).ftpServer.connRetry=((FTPSettings)cbxSite.getItemAt(i)).connRetry;
            owner.getBrowsingPanel(i).ftpServer.connDelay=((FTPSettings)cbxSite.getItemAt(i)).connDelay;
            owner.getBrowsingPanel(i).ftpServer.connKeepAlive=((FTPSettings)cbxSite.getItemAt(i)).connKeepAlive;
            owner.getBrowsingPanel(i).ftpServer.connParallel=((FTPSettings)cbxSite.getItemAt(i)).connParallel;
            owner.getBrowsingPanel(i).ftpServer.passiveMode=((FTPSettings)cbxSite.getItemAt(i)).passiveMode;
            owner.getBrowsingPanel(i).ftpServer.bufferSize=((FTPSettings)cbxSite.getItemAt(i)).bufferSize;
//            if (owner.getBrowsingPanel(i).ftpServer.name.equals(ConfigSettings.RESOURCE_NAME_TGSHARE)) {
//        		owner.getBrowsingPanel(i).ftpServer.userName = txtTGShareUsername.getText();
//        		owner.getBrowsingPanel(i).ftpServer.password = new String(pwdTGSharePassword.getPassword());
//            }else 
            if (owner.getBrowsingPanel(i).ftpServer.name.equals(ConfigSettings.RESOURCE_NAME_AMAZONS3)) {
        		owner.getBrowsingPanel(i).ftpServer.userName = txtAwsAccessKeyId.getText();
        		owner.getBrowsingPanel(i).ftpServer.password = new String(pwdAwsSecretAccessKey.getPassword());
            } else {
	        	owner.getBrowsingPanel(i).ftpServer.userName=(((FTPSettings)cbxSite.getItemAt(i)).userName);
	            owner.getBrowsingPanel(i).ftpServer.password=(((FTPSettings)cbxSite.getItemAt(i)).password);
        	}
            owner.getBrowsingPanel(i).ftpServer.maxSearchDepth=(((FTPSettings)cbxSite.getItemAt(i)).maxSearchDepth);
            owner.getBrowsingPanel(i).ftpServer.maxSearchResults=(((FTPSettings)cbxSite.getItemAt(i)).maxSearchResults);
            config.modifySite(owner.getBrowsingPanel(i).ftpServer);
//            owner.getBrowsingPanel(i).updateSitesComboBox();
        }
        
        // update the global server settings        
        // how we schedule transfers is common across all transfers
        AppMain.setScheduleType(cmbSched.getSelectedIndex());
        // we're locked into using the TG proxy, so we keep this global across resources
        FTPSettings.DefaultCertificate=txtCert.getText();
        config.setConfigValue("proxy_dir", txtCert.getText());
        
        // update the default download directory
        owner.getLocalBrowser().tBrowse.cmdAdd("Cwd",txtDownload.getText(),null);
        config.setConfigValue("download_dir", txtDownload.getText());
        
        // update the global default notifications.  These can be updated on a per-transfer
        // basis in the history panel.
        config.setConfigValue("notification", 
                String.valueOf(cbxNotifications.getSelectedItem()));

        // kill this panel after the update.
        dispose();
    }

    public void btnCancel_actionPerformed(ActionEvent e) {
        dispose();
    }

    public void btnLog_actionPerformed(ActionEvent e) {
        File f=new File(txtLog.getText());
        JFileChooser fc=new JFileChooser(f);
        if(JFileChooser.APPROVE_OPTION==fc.showSaveDialog(this)){
            f=fc.getSelectedFile();
            txtLog.setText(f.getPath());
        }
    }

    public void pnlTabbed_tabChanged(ChangeEvent e) {
    	JTabbedPane tabSource = (JTabbedPane) e.getSource();

        cbxSite.setVisible((tabSource.getSelectedIndex() < 3));
    }
    
    public void btnCert_actionPerformed(ActionEvent e) {
        File f=new File(txtCert.getText());
        JFileChooser fc=new JFileChooser(f);
        if(JFileChooser.APPROVE_OPTION==fc.showOpenDialog(this)){
            f=fc.getSelectedFile();
            txtCert.setText(f.getPath());
        }
    }
    
    public void btnDownload_actionPerformed(ActionEvent e) {
        File f=new File(txtDownload.getText());
        JFileChooser fc=new JFileChooser(f);
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if(JFileChooser.APPROVE_OPTION==fc.showDialog(this, "Select")){
            f=fc.getSelectedFile();
            txtDownload.setText(f.getPath());
        }
    }
    
    public void cbxServices_itemStateChanged(ItemEvent e) {
        int index = cbxServices.getSelectedIndex();
        this.cbxFactories.setSelectedIndex(index);
    }
    
    public void cbxNotifications_itemStateChanged(ItemEvent e) {
        int index = cbxNotifications.getSelectedIndex();
        this.cbxNotifications.setSelectedIndex(index);
    }
    
    public void cbxSite_actionPerformed(ActionEvent e) {
        int selectedIndex = this.cbxSite.getSelectedIndex();
        if (this.cbxSite.getItemCount() > 1) {
            storeFields((selectedIndex==0)?1:0);
        }
        updateFields(selectedIndex);
    }

    public void btnAdd_itemStateChanged(ItemEvent e) {
        if(btnAdd.isSelected()){
            setEditVisible(true);
            btnConfirm.setText(SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_DLGOPTION_BTNCONFIRM_TITLE));

            txtService.setText("");
            txtFactory.setText("");
        }else{
            btnDefault.setSelected(true);
            setEditVisible(false);
        }
    }

    public void btnEdit_itemStateChanged(ItemEvent e) {
        if(btnEdit.isSelected()){
            setEditVisible(true);
            btnConfirm.setText(SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_DLGOPTION_BTNMODIFY_TITLE));
            txtService.setText(cbxServices.getSelectedItem().toString());
            txtFactory.setText(cbxFactories.getSelectedItem().toString());
        }else{
            btnDefault.setSelected(true);
            setEditVisible(false);
        }
    }

    public void btnDelete_itemStateChanged(ItemEvent e) {
        if(btnDelete.isSelected()){
            if(JOptionPane.YES_OPTION==AppMain.Confirm(this.owner,SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_DLGOPTION_DELETE_CONFIRM))){
                String service = cbxServices.getSelectedItem().toString();
                String factory = cbxFactories.getSelectedItem().toString();

                cbxServices.removeItem(service);
                cbxFactories.removeItem(factory);

                cbxServices.setSelectedIndex(0);
                cbxFactories.setSelectedIndex(0);

                // services no longer enabled
//                ConfigOperation.getInstance().deleteService(service);
            }

            //btnDelete.setSelected(false);
            btnDefault.setSelected(true);
        }
    }
    
    @SuppressWarnings("unchecked")
	public void btnOptimizeBuffer_actionPerformed(ActionEvent e) {
        int bufferVal = 0;
        if (cbxSite.getSelectedIndex() == 0) {
           bufferVal = new Speedpage().calculateBufferSize((FTPSettings)cbxSite.getItemAt(0), (FTPSettings)cbxSite.getItemAt(1));
        } else {
           bufferVal = new Speedpage().calculateBufferSize((FTPSettings)cbxSite.getItemAt(1), (FTPSettings)cbxSite.getItemAt(0));
        }
        
        if (((SpinnerNumberModel)spnBufferSize.getModel()).getMaximum().compareTo(Integer.valueOf(bufferVal)) == -1) {
            spnBufferSize.setModel(new SpinnerNumberModel(bufferVal,Speedpage.DEFAULT_MIN_BUFFER_SIZE,(int)(bufferVal*2),1));
        }
        
        spnBufferSize.setValue(bufferVal);
    }

    public void btnConfirm_actionPerformed(ActionEvent e) {
        String service = txtService.getText();
        String factory = txtFactory.getText();

        if(service.length() == 0 || factory.length() == 0){
            AppMain.Error(this.owner,SGGCResourceBundle.getResourceString(ResourceName.KEY_ERROR_DLGOPTION_CONFIRM),
            		SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_ERROR));
            return;
        }

        if(btnAdd.isSelected()){
            cbxServices.addItem(service);
            cbxFactories.addItem(factory);

            cbxServices.setSelectedItem(service);
            cbxFactories.setSelectedItem(factory);

            // disabled rft services
//            ConfigOperation.getInstance().addRFTServices(service, factory);

            //btnAdd.setSelected(false);
        }else if(btnEdit.isSelected()){
            int index = cbxServices.getSelectedIndex();
            @SuppressWarnings("unused")
			String originalService = cbxServices.getSelectedItem().toString();

            cbxFactories.removeItemAt(index);
            cbxServices.removeItemAt(index);

            cbxFactories.addItem(factory);
            cbxServices.addItem(service);

            cbxServices.setSelectedItem(service);

            // disabled rft services
//            ConfigOperation.getInstance().modifyService(originalService, service, factory);

            //btnEdit.setSelected(false);
        }

        btnDefault.setSelected(true);
        setEditVisible(false);
    }

    public void btnAbort_actionPerformed(ActionEvent e) {
        btnDefault.setSelected(true);
    }
    
    public void btnExcludeResources_actionPerformed(ActionEvent e) {
        
        if (constCheckBox("Exclude Resources", 
                "Select the resources to exclude from global searching",
                sites) 
                    != JOptionPane.YES_OPTION) {
            return;
        } else {
                excludes = "";
                
                for (int i = 0;i < chkBoxes.length; i++) {
                    if (chkBoxes[i].isSelected()) {
                        excludes += sites[i] + ";";
                    }
                }
                
        }
        
        System.out.println("Excluding sites: " + excludes);
        
//        ConfigOperation.getInstance().setConfigValue("excludeFromSearch", excludes);
        
        
        
    }
    
    public int constCheckBox(String labelTxt, String footerTxt, String chkBoxTxt[]) {

        
        System.out.println("Excluded resources are " + excludes);
        
        //Ask which data we should show
        //This is the panel that everything goes into
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        //This is the panel that contains the banner
        JPanel bannerPanel = new JPanel();
        bannerPanel.setLayout(new BorderLayout());
        //This is the panel that contains the checkboxes. We're using a grid here for an even
        //number of checkboxes only. Any other type we may want to use something other than a grid
        JPanel chkboxPanel = new JPanel();
        chkboxPanel.setLayout(new GridLayout(6,3));
        //This is the panel that contains the footer
        JPanel footerPanel = new JPanel();
        footerPanel.setLayout(new BorderLayout());

        //Build the dialog box...
        //Include instructions in a label and format that label
        JLabel askLabel = new JLabel(labelTxt, SwingConstants.CENTER);
        askLabel.setForeground(Color.black);
        askLabel.setOpaque(true);
        bannerPanel.add(askLabel,BorderLayout.NORTH);

        //Start defining checkboxes
        for (int i = 0;i < chkBoxTxt.length; i++) {
                if (chkBoxes[i] == null) 
                    chkBoxes[i] = new JCheckBox (chkBoxTxt[i],(excludes.indexOf(chkBoxTxt[i]) > -1));
                else {
                    chkBoxes[i].setSelected((excludes.indexOf(chkBoxTxt[i]) > -1));
                }
                
                chkboxPanel.add(chkBoxes[i]);
        }

        //Include the footer text in a label and format that label
        JLabel askFooter = new JLabel(footerTxt, SwingConstants.CENTER);
        askFooter.setForeground(Color.black);
        askFooter.setOpaque(true);
        footerPanel.add(askFooter);

        //add the bannerPanel and chkboxPanels to the main panel
        panel.add(bannerPanel,BorderLayout.NORTH);
        panel.add(chkboxPanel,BorderLayout.CENTER);
        panel.add(footerPanel,BorderLayout.SOUTH);

        //Determine which button was clicked...
        int answer = JOptionPane.showConfirmDialog(null, panel,"Choose Data",JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE, AppMain.icoPrompt);
        
        return answer;
    }
    
    private boolean validateS3(ConfigOperation config) {
    	if (txtAwsAccessKeyId.getText() == null || txtAwsAccessKeyId.getText().trim().equals("")) {
            pnlTabbed.setSelectedIndex(3);
            AppMain.Message(this, "<html>Please enter a valid AWS Access<br>" +
                   "Key Id to enable Amazon S3 access.<html>");
            
            return false;
        } else if (pwdAwsSecretAccessKey.getPassword() == null ||  
                new String(pwdAwsSecretAccessKey.getPassword()).trim().equals("")) {
            pnlTabbed.setSelectedIndex(3);
            AppMain.Message(this, "<html>Please enter a valid AWS Secret<br>" +
                    "Access Key to enable Amazon S3<br>" +
                    "access.<html>");
            
            return false;
        } else {
        	FTPSettings s3Server = config.getSiteByName(ConfigSettings.RESOURCE_NAME_AMAZONS3);
            if (s3Server == null) {
             // Always add the s3Server by default;
                s3Server = new FTPSettings(ConfigSettings.RESOURCE_NAME_AMAZONS3,FileProtocolType.S3.getDefaultPort(), FileProtocolType.S3);
                s3Server.loginMode = FTPLogin.LOGIN_ASUSER;
                s3Server.hostType = SystemType.ARCHIVE;
                s3Server.userName = txtAwsAccessKeyId.getText();
                s3Server.password = new String(pwdAwsSecretAccessKey.getPassword());
                config.addSite(s3Server);
            } else {
            	if (!owner.getBrowsingPanel(0).ftpServer.name.equals(ConfigSettings.RESOURCE_NAME_AMAZONS3) &&
            			owner.getBrowsingPanel(1).ftpServer != null &&
            			!owner.getBrowsingPanel(1).ftpServer.name.equals(ConfigSettings.RESOURCE_NAME_AMAZONS3)) {
	                s3Server.userName = txtAwsAccessKeyId.getText();
	                s3Server.password = new String(pwdAwsSecretAccessKey.getPassword());
	                config.modifySite(s3Server);
            	}
            }
            return true;
        }
    }
    
//    private void validateTGShare(ConfigOperation config) {
//    	if (txtTGShareUsername.getText() == null || txtTGShareUsername.getText().trim().equals("")) {
//            pnlTabbed.setSelectedIndex(4);
//            AppMain.Message(this, "<html>Please enter a valid TeraGrid<br>" +
//                   						"username to enable TeraGrid <br>" +
//                   						"$SHARE access.<html>");
//            
//        } else if (pwdTGSharePassword.getPassword() == null ||  
//                new String(pwdTGSharePassword.getPassword()).trim().equals("")) {
//            pnlTabbed.setSelectedIndex(4);
//            AppMain.Message(this, "<html>Please enter a valid TeraGrid<br>" +
//						"password to enable TeraGrid <br>" +
//				"$SHARE access.<html>");
//            
//        } else {
//        	FTPSettings tgShareServer = config.getSiteByName(ConfigSettings.RESOURCE_NAME_TGSHARE);
//            if (tgShareServer == null) {
//             // Always add the s3Server by default;
//                tgShareServer = new FTPSettings(ConfigSettings.RESOURCE_NAME_TGSHARE,FTPPort.PORT_TGSHARE,FTPType.XSHARE);
//                tgShareServer.loginMode = FTPLogin.LOGIN_ASUSER;
//                tgShareServer.host = ConfigSettings.SERVICE_TGSHARE_SERVICE;
//                tgShareServer.hostType = "archive";
//                tgShareServer.userName = txtTGShareUsername.getText();
//                tgShareServer.password = new String(pwdTGSharePassword.getPassword());
//                config.addSite(tgShareServer);
//            } else {
//            	if (!owner.getBrowsingPanel(0).ftpServer.name.equals(ConfigSettings.RESOURCE_NAME_TGSHARE) && 
//            			owner.getBrowsingPanel(1).ftpServer != null &&
//            			!owner.getBrowsingPanel(1).ftpServer.name.equals(ConfigSettings.RESOURCE_NAME_TGSHARE)) {
//            		tgShareServer.userName = txtTGShareUsername.getText();
//                    tgShareServer.password = new String(pwdTGSharePassword.getPassword());
//                    config.modifySite(tgShareServer);
//            	}
//            	
//            }
//        }
//    }
}

@SuppressWarnings("serial")
class ActionExample extends AbstractAction {
    private DlgOption adaptee;
    public ActionExample(DlgOption adaptee) {
        super("escape");
        this.adaptee = adaptee;
    }
 
    public void actionPerformed(ActionEvent evt) {
        System.out.println("action triggered");
        adaptee.exit();
    }
}

class DlgOption_pnlTabbed_changeListener implements ChangeListener {
    private DlgOption adaptee;
    public DlgOption_pnlTabbed_changeListener(DlgOption parent) {
        this.adaptee = parent;
    }
    public void stateChanged(ChangeEvent e) {
        //super.mouseClicked(e);
        adaptee.pnlTabbed_tabChanged(e);
    }
    
}
class DlgOption_btnAbort_actionAdapter implements ActionListener {
    private DlgOption adaptee;
    DlgOption_btnAbort_actionAdapter(DlgOption adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.btnAbort_actionPerformed(e);
    }
}

class DlgOption_btnExcludeResources_actionAdapter implements ActionListener {
    private DlgOption adaptee;
    DlgOption_btnExcludeResources_actionAdapter(DlgOption adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.btnExcludeResources_actionPerformed(e);
    }
}

class frmOption_btnConfirm_actionAdapter implements ActionListener {
    private DlgOption adaptee;
    frmOption_btnConfirm_actionAdapter(DlgOption adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {

        adaptee.btnConfirm_actionPerformed(e);
    }
}


class frmOption_btnDelete_itemAdapter implements ItemListener {
    private DlgOption adaptee;
    frmOption_btnDelete_itemAdapter(DlgOption adaptee) {
        this.adaptee = adaptee;
    }

    public void itemStateChanged(ItemEvent e) {
        adaptee.btnDelete_itemStateChanged(e);
    }
}


class frmOption_btnEdit_itemAdapter implements ItemListener {
    private DlgOption adaptee;
    frmOption_btnEdit_itemAdapter(DlgOption adaptee) {
        this.adaptee = adaptee;
    }

    public void itemStateChanged(ItemEvent e) {
        adaptee.btnEdit_itemStateChanged(e);
    }
}


class frmOption_btnAdd_itemAdapter implements ItemListener {
    private DlgOption adaptee;
    frmOption_btnAdd_itemAdapter(DlgOption adaptee) {
        this.adaptee = adaptee;
    }

    public void itemStateChanged(ItemEvent e) {

        adaptee.btnAdd_itemStateChanged(e);
    }
}

class frmOption_cbxServices_itemAdapter implements ItemListener {
    private DlgOption adaptee;
    frmOption_cbxServices_itemAdapter(DlgOption adaptee) {
        this.adaptee = adaptee;
    }

    public void itemStateChanged(ItemEvent e) {
        adaptee.cbxServices_itemStateChanged(e);
    }
}

class frmOption_btnOptimizeBuffer_actionListener implements ActionListener {
    private DlgOption adaptee;
    frmOption_btnOptimizeBuffer_actionListener(DlgOption adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {

        adaptee.btnOptimizeBuffer_actionPerformed(e);
    }
}

class frmOption_cbxSite_ActionListener implements ActionListener {
    private DlgOption adaptee;
    frmOption_cbxSite_ActionListener(DlgOption adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.cbxSite_actionPerformed(e);
    }
}

class frmOption_btnLog_actionAdapter implements ActionListener {
    private DlgOption adaptee;
    frmOption_btnLog_actionAdapter(DlgOption adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.btnLog_actionPerformed(e);
    }
}


class frmOption_btnCert_actionAdapter implements ActionListener {
    private DlgOption adaptee;
    frmOption_btnCert_actionAdapter(DlgOption adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.btnCert_actionPerformed(e);
    }
}

class frmOption_btnDownload_actionAdapter implements ActionListener {
    private DlgOption adaptee;
    frmOption_btnDownload_actionAdapter(DlgOption adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.btnDownload_actionPerformed(e);
    }
}


class frmOption_btnOK_actionAdapter implements ActionListener {
    private DlgOption adaptee;
    frmOption_btnOK_actionAdapter(DlgOption adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.btnOK_actionPerformed(e);
    }
}


class frmOption_btnCancel_actionAdapter implements ActionListener {
    private DlgOption adaptee;
    frmOption_btnCancel_actionAdapter(DlgOption adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.btnCancel_actionPerformed(e);
    }
}

