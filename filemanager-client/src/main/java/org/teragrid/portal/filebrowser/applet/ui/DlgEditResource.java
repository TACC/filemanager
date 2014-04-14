/**
 * 
 */
package org.teragrid.portal.filebrowser.applet.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.commons.lang.StringUtils;
import org.teragrid.portal.filebrowser.applet.AppMain;
import org.teragrid.portal.filebrowser.applet.ConfigOperation;
import org.teragrid.portal.filebrowser.applet.transfer.FTPSettings;
import org.teragrid.portal.filebrowser.applet.util.ResourceName;
import org.teragrid.portal.filebrowser.applet.util.SGGCResourceBundle;

import edu.utexas.tacc.wcs.filemanager.common.model.enumeration.FileProtocolType;
import edu.utexas.tacc.wcs.filemanager.common.model.enumeration.SystemType;
import edu.utexas.tacc.wcs.filemanager.common.util.DBUtil;

/**
 * @author dooley
 *
 */
@SuppressWarnings({"serial","unused"})
public class DlgEditResource extends DlgEscape implements ActionListener {
	
	private static int MIN_CONNECTIONS = 2;
    private static int MIN_RETRY = 1;
    
    private JPanel pnlName = new JPanel();
    private JPanel pnlLogin = new JPanel();
    private JPanel pnlConnection = new JPanel();
    private JPanel pnlButtons = new JPanel();
    private final JPanel pnlUsername = new JPanel();
    private final JPanel pnlPassword = new JPanel();
    private final JPanel pnlResource = new JPanel();
    private final JPanel pnlZone = new JPanel();
    private final JPanel pnlIrodsGridAuth = new JPanel();
	
	private JLabel lblNickname = new JLabel("Name:");
	private JTextField txtNickname = new JTextField();
	private JLabel lblHostname = new JLabel("Hostname:");
	private JTextField txtHostname = new JTextField();
	private JLabel lblPort = new JLabel("Port:");
	private JTextField txtPort = new JTextField();
	private JLabel lblUsername = new JLabel("Username:");
	private JTextField txtUsername = new JTextField();
	private JLabel lblPassword = new JLabel("Password:");
	private JPasswordField pwdPassword = new JPasswordField();
	
	private JLabel lblZone = new JLabel("Zone:");
	private JTextField txtZone = new JTextField();
	private JLabel lblResource = new JLabel("Resource:");
	private JTextField txtResource = new JTextField();
	private JLabel lblIrodsGridAuth = new JLabel("Select to use X.509 authentication");
	private JCheckBox chkIrodsGridAuth = new JCheckBox();
	
	private JCheckBox chkShowHidden = new JCheckBox();
	private JCheckBox chkStripeTransfer = new JCheckBox();
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
    private JLabel lblBufferSize = new JLabel();
    private JSpinner spnBufferSize = new JSpinner();
    private JButton btnOptimizeBuffer = new JButton();
    private JLabel lblSched = new JLabel();
    private JComboBox cmbSched = new JComboBox(AppMain.scheduleName);
    
	private JButton btnOK = new JButton("OK");
	private JButton btnCancel = new JButton("Cancel");
	
	private JLabel lblType = new JLabel("Type");
	private JComboBox cmbType = new JComboBox(new String[]{ 
			FileProtocolType.GRIDFTP.name(), 
			FileProtocolType.IRODS.name(),
			FileProtocolType.S3.name()});
	
	private JLabel lblCategory = new JLabel("Category");
	private JComboBox cmbCategory = new JComboBox(new String[]{SystemType.HPC.name(), SystemType.ARCHIVE.name(), SystemType.VIZ.name()});
	
	private FTPSettings resource = null;
	private Component parent;
	
	public DlgEditResource(Component parent, FTPSettings resource) {
		super();
		this.resource = resource;
		this.parent = parent;
		
		setModal(true);
		
		setTitle("Edit " + resource.name);
		
		init();
	}
	
	public DlgEditResource(Component parent) {
		super();
		
		this.resource = new FTPSettings("gridftp.example.com",FileProtocolType.GRIDFTP.getDefaultPort(),FileProtocolType.GRIDFTP);
		this.resource.name = "My Resource";
		this.resource.userDefined = true;
		this.parent = parent;
		
		setModal(true);
		setTitle("Add New Resource");
		
		init();
	}
	
	private void init() {
		
		layoutNicknamePanel();
		
		layoutLoginPanel();
		
		layoutConnectionPanel();
		
		layoutButtonPanel();
		
		JPanel pnlMain = new JPanel();
		pnlMain.setLayout(new BoxLayout(pnlMain,BoxLayout.Y_AXIS));
		pnlMain.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		pnlMain.add(pnlName);
		pnlMain.add(pnlLogin);
		pnlMain.add(pnlConnection);
		pnlMain.add(pnlButtons,RIGHT_ALIGNMENT);
		
		add(pnlMain);
		pack();
		
		locateDialog(AppMain.getFrame());
		//setResizable(false);
	}
	
	private void layoutNicknamePanel() {
		pnlName.setLayout(new BoxLayout(pnlName,BoxLayout.Y_AXIS));
		pnlName.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		
		
		JPanel pnlType = new JPanel();
		pnlType.setLayout(new BoxLayout(pnlType,BoxLayout.X_AXIS));
		lblType.setPreferredSize(new Dimension(70,25));
		cmbType.setSelectedItem(this.resource.protocol.name());
		cmbType.setEnabled(resource.userDefined);
		cmbType.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event)
			{
				if (((JComboBox)event.getSource()).getSelectedItem() == FileProtocolType.IRODS.name()) {
					pnlConnection.setVisible(false);
					lblResource.setVisible(true);
					txtResource.setVisible(true);
					lblZone.setVisible(true);
					txtZone.setVisible(true);
					lblIrodsGridAuth.setVisible(true);
					chkIrodsGridAuth.setVisible(true);
					
					boolean hideUsernamePasswordFields = chkIrodsGridAuth.isSelected();
					
					lblPassword.setText("Password:");
					lblUsername.setText("Username:");
					
					if (hideUsernamePasswordFields) {
						pnlLogin.remove(pnlPassword);
						pnlLogin.remove(pnlUsername);
					} else {
						pnlLogin.add(pnlUsername);
						pnlLogin.add(pnlPassword);
						pnlLogin.add(pnlZone);
						pnlLogin.add(pnlResource);
						pnlLogin.add(pnlIrodsGridAuth);
					}
					pnlLogin.updateUI();
					
					if (StringUtils.isEmpty(txtPort.getText()) 
							|| txtPort.getText().equals(String.valueOf(FileProtocolType.S3.getDefaultPort()))
							|| txtPort.getText().equals(String.valueOf(FileProtocolType.GRIDFTP.getDefaultPort()))) {
						txtPort.setText(String.valueOf(String.valueOf(FileProtocolType.IRODS.getDefaultPort())));
					}
					if (txtHostname.getText().equals(net.spy.s3.Utils.DEFAULT_HOST) || 
							txtHostname.getText().equals("gridftp.example.com")) {
						txtHostname.setText("irods.example.com");
					}
					txtHostname.setEnabled(true);
					txtPort.setEnabled(true);
					
					pnlLogin.setVisible(true);
				} else if(((JComboBox)event.getSource()).getSelectedItem() == FileProtocolType.S3.name()) {
					pnlConnection.setVisible(false);
					txtHostname.setText(net.spy.s3.Utils.DEFAULT_HOST);
					txtHostname.setEnabled(false);
					txtPort.setText(String.valueOf(FileProtocolType.S3.getDefaultPort()));
					txtPort.setEnabled(false);
					lblResource.setVisible(false);
					txtResource.setVisible(false);
					lblZone.setVisible(false);
					txtZone.setVisible(false);
					lblPassword.setText("Secret:");
					lblUsername.setText("Key:");
					
					pnlLogin.add(pnlUsername);
					pnlLogin.add(pnlPassword);
					pnlLogin.add(pnlZone);
					pnlLogin.add(pnlResource);
					pnlLogin.add(pnlIrodsGridAuth);
					lblIrodsGridAuth.setVisible(false);
					chkIrodsGridAuth.setVisible(false);
					
					pnlLogin.updateUI();
					
					pnlLogin.setVisible(true);
				} else {
					if (StringUtils.isEmpty(txtPort.getText()) 
							|| txtPort.getText().equals(String.valueOf(FileProtocolType.S3.getDefaultPort()))
							|| txtPort.getText().equals(String.valueOf(FileProtocolType.IRODS.getDefaultPort()))) {
						txtPort.setText(String.valueOf(FileProtocolType.GRIDFTP.getDefaultPort()));
					}
					if (txtHostname.getText().equals(net.spy.s3.Utils.DEFAULT_HOST)  || 
							txtHostname.getText().equals("irods.example.com")) {
						txtHostname.setText("gridftp.example.com");
					}
					txtHostname.setEnabled(true);
					txtPort.setEnabled(true);
					pnlConnection.setVisible(true);
					pnlLogin.setVisible(false);
				}
				
				if (StringUtils.isEmpty(txtNickname.getText())) {
					txtNickname.setText("My Resource");
				}
			}
		});
		pnlType.add(lblType);
		pnlType.add(cmbType);
		
		JPanel pnlCategory = new JPanel();
		pnlCategory.setLayout(new BoxLayout(pnlCategory,BoxLayout.X_AXIS));
		lblCategory.setPreferredSize(new Dimension(70,25));
		cmbCategory.setSelectedItem(this.resource.hostType);
		cmbCategory.setEnabled(resource.userDefined);
		cmbCategory.setRenderer(new ListCellRenderer() {

			public Component getListCellRendererComponent(JList list,
					Object value, int index, boolean isSelected,
					boolean cellHasFocus) {
				JLabel item = new JLabel((String)value);
				ImageIcon ico;
				if (((String)value).equals(DBUtil.VIZ)) {
					ico = AppMain.icoResourceViz;
				} else if (((String)value).equals(DBUtil.ARCHIVE)) {
					ico = AppMain.icoResourceArchive;
				} else {
					ico = AppMain.icoResourceCompute;
				}
				item.setIcon(ico);
				
				return item;
			}
		});
		pnlCategory.add(lblCategory);
		pnlCategory.add(cmbCategory);
		
		JPanel pnlNickname = new JPanel();
		pnlNickname.setLayout(new BoxLayout(pnlNickname,BoxLayout.X_AXIS));
		lblNickname.setPreferredSize(new Dimension(70,25));
		txtNickname.setText(resource.name);
		txtNickname.setEnabled(resource.userDefined);
		pnlNickname.add(lblNickname);
		pnlNickname.add(txtNickname);
		
		JPanel pnlHostname = new JPanel();
		pnlHostname.setLayout(new BoxLayout(pnlHostname,BoxLayout.X_AXIS));
		lblHostname.setPreferredSize(new Dimension(70,25));
		txtHostname.setText(resource.host);
		txtHostname.setEnabled(resource.userDefined);
		pnlHostname.add(lblHostname);
		pnlHostname.add(txtHostname);
		
		JPanel pnlPort = new JPanel();
		pnlPort.setLayout(new BoxLayout(pnlPort,BoxLayout.X_AXIS));
		lblPort.setPreferredSize(new Dimension(70,25));
		txtPort.setText(String.valueOf(resource.filePort));
		txtPort.setEnabled(resource.userDefined);
		pnlPort.add(lblPort);
		pnlPort.add(txtPort);
		
		pnlName.add(pnlType);
		pnlName.add(pnlCategory);
		pnlName.add(pnlHostname);
		pnlName.add(pnlPort);
		pnlName.add(pnlNickname);
		
	}
	
	private void layoutLoginPanel() {
		
		//pnlLogin.setLayout(new BoxLayout(pnlLogin,BoxLayout.Y_AXIS));
		pnlLogin.setLayout(new GridLayout(12,2));
		pnlLogin.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		
		pnlUsername.setLayout(new BoxLayout(pnlUsername,BoxLayout.X_AXIS));
		lblUsername.setPreferredSize(new Dimension(70,25));
		txtUsername.setText(resource.userName);
		txtUsername.setEnabled(resource.userDefined);
		pnlUsername.add(lblUsername);
		pnlUsername.add(txtUsername);
		
		pnlPassword.setLayout(new BoxLayout(pnlPassword,BoxLayout.X_AXIS));
		lblPassword.setPreferredSize(new Dimension(70,25));
		pwdPassword.setText(resource.password);
		pwdPassword.setEnabled(resource.userDefined);
		pnlPassword.add(lblPassword);
		pnlPassword.add(pwdPassword);
		
		pnlResource.setLayout(new BoxLayout(pnlResource,BoxLayout.X_AXIS));
		lblResource.setPreferredSize(new Dimension(70,25));
		txtResource.setText(resource.resource);
		txtResource.setEnabled(resource.userDefined);
		pnlResource.add(lblResource);
		pnlResource.add(txtResource);
		
		pnlZone.setLayout(new BoxLayout(pnlZone,BoxLayout.X_AXIS));
		lblZone.setPreferredSize(new Dimension(70,25));
		txtZone.setText(resource.zone);
		txtZone.setEnabled(resource.userDefined);
		pnlZone.add(lblZone);
		pnlZone.add(txtZone);
		
		pnlIrodsGridAuth.setLayout(new BoxLayout(pnlIrodsGridAuth,BoxLayout.X_AXIS));
		chkIrodsGridAuth.setPreferredSize(new Dimension(20,25));
		chkIrodsGridAuth.setSelected(
				(StringUtils.isEmpty(resource.password) && StringUtils.isEmpty(resource.userName)));
		chkIrodsGridAuth.addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent event)
			{
				boolean hideUsernamePasswordFields = ((JCheckBox)event.getSource()).isSelected();
				
				if (hideUsernamePasswordFields) {
					pnlLogin.remove(pnlPassword);
					pnlLogin.remove(pnlUsername);
				} else {
					pnlLogin.add(pnlUsername);
					pnlLogin.add(pnlPassword);
					pnlLogin.add(pnlZone);
					pnlLogin.add(pnlResource);
					pnlLogin.add(pnlIrodsGridAuth);
				}
				pnlLogin.updateUI();
			}
		});
		pnlIrodsGridAuth.add(chkIrodsGridAuth);
		pnlIrodsGridAuth.add(lblIrodsGridAuth);
		
		
		
		pnlLogin.add(pnlUsername);
		pnlLogin.add(pnlPassword);
		pnlLogin.add(pnlZone);
		pnlLogin.add(pnlResource);
		pnlLogin.add(pnlIrodsGridAuth);
		
		if (resource.protocol.equals(FileProtocolType.IRODS)) {
			lblPassword.setText("Password:");
			lblUsername.setText("Username:");
			
			if (chkIrodsGridAuth.isSelected()) {
				pnlLogin.remove(pnlPassword);
				pnlLogin.remove(pnlUsername);
			}
				
			pnlConnection.setVisible(false);
			
		} else if (resource.protocol.equals(FileProtocolType.S3)) {
			lblPassword.setText("AWS Secret Key:");
			lblUsername.setText("AWS Key Id:");
			pnlConnection.setVisible(false);
			lblResource.setVisible(false);
			txtResource.setVisible(false);
			lblZone.setVisible(false);
			lblIrodsGridAuth.setVisible(false);
			chkIrodsGridAuth.setVisible(false);
		} else {
			pnlLogin.setVisible(false);
		}
	}
	
	private void layoutConnectionPanel() {
//		pnlConnection.setLayout(new BoxLayout(pnlConnection,BoxLayout.Y_AXIS));
		pnlConnection.setLayout(new GridLayout(11,2));
//		pnlConnection.setBounds(new Rectangle(19, 8, 464, 382));
		pnlConnection.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		
		chkShowHidden.setSelected(resource.showHidden);
		chkStripeTransfer.setSelected(resource.stripeTransfers);
        spnConnMax.setModel(new SpinnerNumberModel((resource.connMaxNum<MIN_CONNECTIONS)?MIN_CONNECTIONS:resource.connMaxNum,MIN_CONNECTIONS,20,1));
        spnConnRetry.setModel(new SpinnerNumberModel((resource.connMaxNum<MIN_RETRY)?MIN_RETRY:resource.connRetry,MIN_RETRY,50,1));
        spnConnDelay.setModel(new SpinnerNumberModel(resource.connDelay,0,120,5));
        spnConnKeep.setModel(new SpinnerNumberModel(resource.connKeepAlive,0,120,5));
        
        spnBufferSize.setModel(new SpinnerNumberModel(
                ((resource.bufferSize > 0)?resource.bufferSize:FTPSettings.DefaultBufferSize),
                Speedpage.DEFAULT_MIN_BUFFER_SIZE,
                Speedpage.DEFAULT_THIRD_PARTY_BUFFER_SIZE*2,
                1));
        spnConnPara.setModel(new SpinnerNumberModel(resource.connParallel,1,20,1));
        cmbConnData.setSelectedIndex(resource.passiveMode?0:1);
		
        chkShowHidden.setText(SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_DLGOPTION_CHKSHOWHIDDEN_TITLE));
        chkShowHidden.setBounds(new Rectangle(14, 44, 207, 23));
//        chkShowHidden.setHorizontalTextPosition(SwingConstants.LEFT);
        chkStripeTransfer.setText("Stripe Transfers");
//        chkStripeTransfer.setHorizontalTextPosition(SwingConstants.LEFT);
        chkStripeTransfer.setBounds(new Rectangle(14, 12, 207, 23));
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
        btnOptimizeBuffer.addActionListener(this);
        lblSched.setBounds(new Rectangle(14, 299, 160, 23));
        lblSched.setText("Scheduler");
        lblConnData.setText(SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_DLGOPTION_LBLCONNDATA_TITLE));
        lblConnData.setBounds(new Rectangle(14, 267, 160, 23));
        
        pnlConnection.add(chkShowHidden);
        pnlConnection.add(new JLabel());
        pnlConnection.add(chkStripeTransfer);
        pnlConnection.add(new JLabel());
        pnlConnection.add(lblConnRetry);
        pnlConnection.add(spnConnRetry);
        pnlConnection.add(lblConnMax);
        pnlConnection.add(spnConnMax);
        pnlConnection.add(lblConnDelay);
        pnlConnection.add(spnConnDelay);
        pnlConnection.add(lblConnKeep);
        pnlConnection.add(spnConnKeep);
        pnlConnection.add(lblConnPara);
        pnlConnection.add(spnConnPara);
        pnlConnection.add(lblConnData);
        pnlConnection.add(cmbConnData);
        pnlConnection.add(lblBufferSize);
        pnlConnection.add(spnBufferSize);
        pnlConnection.add(new JLabel());
        pnlConnection.add(btnOptimizeBuffer);
        pnlConnection.add(lblSched);
        pnlConnection.add(cmbSched);
	}
	
	private void layoutButtonPanel() {
		pnlButtons.setLayout(new BoxLayout(pnlButtons,BoxLayout.X_AXIS));
		pnlButtons.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		pnlButtons.add(Box.createHorizontalGlue());
		pnlButtons.add(btnOK);
		pnlButtons.add(btnCancel);
		btnOK.addActionListener(this);
		btnCancel.addActionListener(this);
	}
	
	public static void main(String[] args) {
		new DlgEditResource(new Frame()).setVisible(true);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnOK) {
			updateResource();
			setVisible(false);
		} else if (e.getSource() == btnCancel) {
			setVisible(false);
			dispose();
		} else if (e.getSource() == btnOptimizeBuffer) {
			optimizeBuffer();
		}
		
	}
	
	@SuppressWarnings("unchecked")
	public void optimizeBuffer() {
        int bufferVal = 0;
        if (resource.protocol.equals(FileProtocolType.GRIDFTP)) {
           bufferVal = 33000;
        } else {
           bufferVal = 1000;
        }
        
        if (((SpinnerNumberModel)spnBufferSize.getModel()).getMaximum().compareTo(Integer.valueOf(bufferVal)) == -1) {
            spnBufferSize.setModel(new SpinnerNumberModel(bufferVal,Speedpage.DEFAULT_MIN_BUFFER_SIZE,(int)(bufferVal*2),1));
        }
        
        spnBufferSize.setValue(bufferVal);
    }
 
	private void updateResource() {
		ConfigOperation config = ConfigOperation.getInstance();
        // update the server settings in both panels
		FTPSettings site = config.getSiteByName(txtNickname.getText());
		if (site != null && site.userDefined != resource.userDefined) {
			AppMain.Error(this, "Resource named " + txtNickname.getText() + " already exists!");
			txtNickname.selectAll();
			txtNickname.requestFocus();
			return;
		} 
		
		site = config.getSiteByHostame(txtHostname.getText());
		if (site != null && site.userDefined != resource.userDefined) {
			AppMain.Error(this, "Resource named " + txtNickname.getText() + " already exists!");
			txtHostname.selectAll();
			txtHostname.requestFocus();
			return;
		}
		
		resource.name = txtNickname.getText();
		resource.host = txtHostname.getText();
		
		try {
			resource.filePort = Integer.parseInt(txtPort.getText());
		} catch (Exception e) {
			AppMain.Error(this, "Please specify a valid port number. The default GridFTP port is 2811.");
			txtPort.selectAll();
			txtPort.requestFocus();
			return;
		}
		resource.protocol = FileProtocolType.valueOf((String)cmbType.getSelectedItem());
		resource.hostType = SystemType.valueOf((String)cmbCategory.getSelectedItem());
        resource.stripeTransfers = chkStripeTransfer.isSelected();
        resource.showHidden = chkShowHidden.isSelected();
        if ( resource.protocol.equals(FileProtocolType.IRODS) ) {
        	if (chkIrodsGridAuth.isSelected()) {
        		resource.userName = "";
        		resource.password = "";
        	} else {
        		resource.userName = txtUsername.getText();
        		resource.password = new String(pwdPassword.getPassword());
        	}
        	resource.resource = txtResource.getText();
        	resource.zone = txtZone.getText();
        } else if ( resource.protocol.equals(FileProtocolType.S3) ) {
        	resource.userName = txtUsername.getText();
        	resource.password = new String(pwdPassword.getPassword());
        }
        resource.passiveMode=(0==cmbConnData.getSelectedIndex());
        resource.connRetry=Integer.parseInt(spnConnRetry.getValue().toString());
        resource.connDelay=Integer.parseInt(spnConnDelay.getValue().toString());
        resource.connParallel=Integer.parseInt(spnConnPara.getValue().toString());
        resource.connMaxNum=Integer.parseInt(spnConnMax.getValue().toString());
        resource.connKeepAlive=Integer.parseInt(spnConnKeep.getValue().toString());
        resource.bufferSize=Integer.parseInt(spnBufferSize.getValue().toString());
        resource.showHidden=(chkShowHidden.isSelected());
        resource.stripeTransfers=(chkStripeTransfer.isSelected());
//        System.out.println("Updated resource hidden to " + resource.showHidden);
//        resource.maxSearchDepth = Integer.parseInt(spnSearchDepth.getValue().toString());
//        resource.maxSearchResults = Integer.parseInt(spnMaxSearchResults.getValue().toString());
//        resource.userDefined = true;
        config.modifySite(resource);
        
	}
	
	public FTPSettings getResource() {
		return this.resource;
	}

}