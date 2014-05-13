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

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import edu.utexas.tacc.wcs.filemanager.client.AppMain;
import edu.utexas.tacc.wcs.filemanager.client.util.ResourceName;
import edu.utexas.tacc.wcs.filemanager.client.util.SGGCResourceBundle;


@SuppressWarnings("serial")
public class DlgPassword extends DlgEscape {
    
    public static String AnonyUser="anonymous";
    public static String AnonyPwd="anony@anony.net";
    private boolean result = false;
    private boolean anonymous = false;
    private String userName = null;
    private String pwd = null;

    private JPanel pnlMain = new JPanel();
    private JLabel lblName = new JLabel();
    private JLabel lblPwd = new JLabel();
    private JTextField txtName = new JTextField();
    private JPasswordField txtPwd = new JPasswordField();
    private JCheckBox chkAnonymous = new JCheckBox();
    private JButton btnOK = new JButton();
    private JButton btnCancel = new JButton();
    private JCheckBox chkSave = new JCheckBox();
    private Frame owner = null; 
    
    public DlgPassword(Frame owner, String title, boolean modal) {
        super(owner, title, modal);
        this.owner = owner;
        
        try {
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            jbInit();
            Dimension dlgSize = getPreferredSize();
            Dimension frmSize = owner.getSize();
            Point loc = owner.getLocation();
            setLocation((frmSize.width - dlgSize.width) / 2 + loc.x, (frmSize.height - dlgSize.height) / 2 + loc.y);
            pack();
            chkSave.setVisible(false);
        } catch (Exception exception) {
            exception.printStackTrace();
            edu.utexas.tacc.wcs.filemanager.client.util.LogManager.debug(exception.getLocalizedMessage() + " at " + (exception.getStackTrace())[0]);  
        }
    }

    public DlgPassword() {
        this(new Frame(), SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_DLGPASSWORD_TITLE), false);
    }

    private void jbInit() throws Exception {
        pnlMain.setLayout(null);
        lblName.setText(SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_DLGPASSWORD_LBLNAME_TITLE));
        lblName.setBounds(new Rectangle(23, 24, 75, 20));
        lblPwd.setText(SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_DLGPASSWORD_LBLPWD_TITLE));
        lblPwd.setBounds(new Rectangle(24, 63, 71, 20));
        txtName.setEditable(true);
        txtName.setBounds(new Rectangle(99, 23, 142, 22));
        txtPwd.setBounds(new Rectangle(98, 60, 143, 22));
        txtPwd.addActionListener(new DlgPassword_txtPwd_actionAdapter(this));
        chkAnonymous.setText(SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_DLGPASSWORD_CHKANONY_TITLE));
        chkAnonymous.setBounds(new Rectangle(244, 22, 95, 25));
        chkAnonymous.addItemListener(new DlgPassword_chkAnonymous_itemAdapter(this));
        btnOK.setBounds(new Rectangle(65, 104, 80, 27));
        btnOK.setSelectedIcon(null);
        btnOK.setText(SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_OK));
        btnOK.addActionListener(new DlgPassword_btnOK_actionAdapter(this));
        btnCancel.setBounds(new Rectangle(204, 104, 80, 27));
        btnCancel.setText(SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_CANCEL));
        btnCancel.addActionListener(new DlgPassword_btnCancel_actionAdapter(this));
        chkSave.setText(SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_DLGPASSWORD_CHKSAVE_TITLE));
        chkSave.setBounds(new Rectangle(246, 59, 123, 24));
        getContentPane().add(pnlMain);
        pnlMain.add(lblName);
        pnlMain.add(txtName);
        pnlMain.add(txtPwd);
        pnlMain.add(lblPwd);
        pnlMain.add(chkAnonymous);
        pnlMain.add(chkSave);
        pnlMain.add(btnOK);
        pnlMain.add(btnCancel);
        pnlMain.setPreferredSize(new Dimension(350, 150));
        setResizable(false);
    }

    public void btnCancel_actionPerformed(ActionEvent e) {
        this.dispose();
    }

    public void btnOK_actionPerformed(ActionEvent e) {
        //this.userName = txtName.getSelectedItem().toString();
        this.userName = txtName.getText();
        this.pwd = new String(txtPwd.getPassword());
        if(!this.anonymous && this.userName.length() == 0){
            AppMain.Error(this.owner,SGGCResourceBundle.getResourceString(ResourceName.KEY_ERROR_DLGPASSWORD_EMPTYUSERNAME),
            		SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_ERROR));
            return;
        }

        this.result = true;
        this.dispose();
    }

    public void chkAnonymous_itemStateChanged(ItemEvent e) {
        this.anonymous = chkAnonymous.isSelected();
        setAnonymous(this.anonymous);
    }

    public void setAnonymous(boolean anonymous){
        txtName.setEnabled(!anonymous);
        txtPwd.setEnabled(!anonymous);
        chkSave.setEnabled(!anonymous);
        if(anonymous){
            txtName.setText(AnonyUser);
            txtPwd.setText(AnonyPwd);
        }
        else{
            txtName.setText("");
            txtPwd.setText("");
        }
    }

    public void txtPwd_actionPerformed(ActionEvent e) {
        this.btnOK_actionPerformed(e);
    }

	public boolean getResult() {
		return result;
	}

	public String getUserName() {
		return userName;
	}

	public String getPassword() {
		return pwd;
	}
}


class DlgPassword_txtPwd_actionAdapter implements ActionListener {
    private DlgPassword adaptee;
    DlgPassword_txtPwd_actionAdapter(DlgPassword adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.txtPwd_actionPerformed(e);
    }
}


class DlgPassword_chkAnonymous_itemAdapter implements ItemListener {
    private DlgPassword adaptee;
    DlgPassword_chkAnonymous_itemAdapter(DlgPassword adaptee) {
        this.adaptee = adaptee;
    }

    public void itemStateChanged(ItemEvent e) {
        adaptee.chkAnonymous_itemStateChanged(e);
    }
}


class DlgPassword_btnOK_actionAdapter implements ActionListener {
    private DlgPassword adaptee;
    DlgPassword_btnOK_actionAdapter(DlgPassword adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.btnOK_actionPerformed(e);
    }
}


class DlgPassword_btnCancel_actionAdapter implements ActionListener {
    private DlgPassword adaptee;
    DlgPassword_btnCancel_actionAdapter(DlgPassword adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.btnCancel_actionPerformed(e);
    }
}
