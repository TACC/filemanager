/*
 * Portions of this file Copyright 2004-2007 Shanghai Jiaotong University
 * 
 * This file or a portion of this file is licensed under the
 * terms of the Globus Toolkit Public License, found at
 * http://www.globus.org/toolkit/legal/4.0/
 * If you redistribute this file, with or without
 * modifications, you must include this notice in the file.
 */

package org.teragrid.portal.filebrowser.applet.ui;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.teragrid.portal.filebrowser.applet.util.ResourceName;
import org.teragrid.portal.filebrowser.applet.util.SGGCResourceBundle;


@SuppressWarnings({"serial","unused","unchecked"})
public class DlgThirdPartyTrans extends DlgEscape {


    //output
	private boolean result = false;

    private boolean rftTrans = false;
    private String service = null;
    private String factory = null;

    private JPanel pnlMain = new JPanel();
    private JButton btnOK = new JButton();
    private JButton btnCancel = new JButton();
    private ButtonGroup grpAction = new ButtonGroup();
    private JLabel lblRFT = new JLabel();
    private JComboBox cmbService = new JComboBox();
    private JRadioButton rdiDirect = new JRadioButton();
    private JRadioButton rdiRFT = new JRadioButton();
    private JLabel lblTitle = new JLabel();
    private JLabel lblFactory = new JLabel();
    private JComboBox cmbFactory = new JComboBox();

    public DlgThirdPartyTrans(Frame owner, String title, boolean modal) {
        super(owner, title, modal);
        try {
            setResizable(false);
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            jbInit();

            int serviceCount = 0;
            HashMap hashMap = new HashMap();//(ConfigOperation.getInstance()).getRFTServices();
            Set keys = hashMap.keySet();
            Iterator iterator = keys.iterator();
            while(iterator.hasNext()){
                serviceCount ++;
                String service = (String)iterator.next();
                String factory = (String)hashMap.get(service);
                this.cmbService.addItem(service);
                this.cmbFactory.addItem(factory);
            }
            if(serviceCount == 0){
                this.rdiRFT.setEnabled(false);
            }
            cmbService.addItemListener(new
                                   DlgThirdPartyTrans_cmbService_itemAdapter(this));


            Dimension dlgSize = getPreferredSize();
            Dimension frmSize = owner.getSize();
            Point loc = owner.getLocation();
            setLocation((frmSize.width - dlgSize.width) / 2 + loc.x, (frmSize.height - dlgSize.height) / 2 + loc.y);
            pack();
        } catch (Exception exception) {
            exception.printStackTrace();
            org.teragrid.portal.filebrowser.applet.util.LogManager.debug(exception.getLocalizedMessage() + " at " + (exception.getStackTrace())[0]);  
        }
    }

    public DlgThirdPartyTrans() {
        this(new Frame(), "frmTrans", false);
        try {
            jbInit();
        } catch (Exception ex) {
            ex.printStackTrace();
            org.teragrid.portal.filebrowser.applet.util.LogManager.debug(ex.getLocalizedMessage() + " at " + (ex.getStackTrace())[0]);  
        }
    }

    private void jbInit() throws Exception {
        pnlMain.setLayout(null);
        btnOK.setBounds(new Rectangle(228, 225, 76, 26));
        btnOK.setText(SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_OK));
        btnOK.addActionListener(new DlgThirdPartyTrans_btnOK_actionAdapter(this));
        btnCancel.setBounds(new Rectangle(314, 225, 76, 26));
        btnCancel.setEnabled(true);
        btnCancel.setText(SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_CANCEL));
        btnCancel.addActionListener(new
                                    DlgThirdPartyTrans_btnCancel_actionAdapter(this));
        lblRFT.setText(SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_DLGTHIRDPARTYTRANS_LBLRFT));
        lblRFT.setBounds(new Rectangle(38, 124, 74, 17));
        cmbService.setPreferredSize(new Dimension(250, 22));
        cmbService.setEditable(false);
        cmbService.setBounds(new Rectangle(117, 122, 267, 22));
        rdiDirect.setSelected(true);
        rdiDirect.setText(SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_DLGTHIRDPARTYTRANS_RDIDIRECT));
        rdiDirect.setBounds(new Rectangle(28, 55, 184, 25));
        rdiDirect.addItemListener(new dlgThirdPartyTrans_rdiDirect_itemAdapter(this));
        rdiRFT.setText(SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_DLGTHIRDPARTYTRANS_RDIRFT));
        rdiRFT.setBounds(new Rectangle(28, 85, 180, 25));
        rdiRFT.addItemListener(new dlgThirdPartyTrans_rdiRFT_itemAdapter(this));
        lblTitle.setText(SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_DLGTHIRDPARTYTRANS_LBLTITLE));
        lblTitle.setBounds(new Rectangle(33, 25, 313, 26));
        lblFactory.setText(SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_DLGTHIRDPARTYTRANS_LBLFACTORY));
        lblFactory.setBounds(new Rectangle(36, 158, 80, 18));
        cmbFactory.setBounds(new Rectangle(118, 156, 264, 23));
        cmbFactory.setEnabled(false);
        cmbService.setEnabled(false);
        pnlMain.add(btnCancel);
        pnlMain.add(btnOK);
        pnlMain.add(lblTitle);
        pnlMain.add(rdiDirect);
        pnlMain.add(rdiRFT);
        pnlMain.add(cmbService);
        pnlMain.add(lblRFT);
        pnlMain.add(lblFactory);
        pnlMain.add(cmbFactory);
        pnlMain.setPreferredSize(new Dimension(400, 260));
        getContentPane().add(pnlMain);
        grpAction.add(rdiDirect);
        grpAction.add(rdiRFT);
    }

    public void btnOK_actionPerformed(ActionEvent e) {
        this.result = true;

        if(rdiDirect.isSelected()){
            this.rftTrans = false;
        }else{
            this.rftTrans = true;
            this.service = this.cmbService.getSelectedItem().toString();
            this.factory = this.cmbFactory.getSelectedItem().toString();
        }
        this.dispose();
    }

    private void setRFTEnable(boolean enable){
        this.cmbService.setEnabled(enable);
    }

    public void rdiDirect_itemStateChanged(ItemEvent e) {
        setRFTEnable(!rdiDirect.isSelected());
    }

    public void rdiRFT_itemStateChanged(ItemEvent e) {
        setRFTEnable(rdiRFT.isSelected());
    }

    public void btnCancel_actionPerformed(ActionEvent e) {
        this.dispose();
    }

    public void cmbService_itemStateChanged(ItemEvent e) {
        this.cmbFactory.setSelectedIndex(cmbService.getSelectedIndex());
    }

	public boolean getResult() {
		return this.result;
	}
}


class DlgThirdPartyTrans_cmbService_itemAdapter implements ItemListener {
    private DlgThirdPartyTrans adaptee;
    DlgThirdPartyTrans_cmbService_itemAdapter(DlgThirdPartyTrans adaptee) {
        this.adaptee = adaptee;
    }

    public void itemStateChanged(ItemEvent e) {
        adaptee.cmbService_itemStateChanged(e);
    }
}


class DlgThirdPartyTrans_btnCancel_actionAdapter implements ActionListener {
    private DlgThirdPartyTrans adaptee;
    DlgThirdPartyTrans_btnCancel_actionAdapter(DlgThirdPartyTrans adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.btnCancel_actionPerformed(e);
    }
}


class dlgThirdPartyTrans_rdiRFT_itemAdapter implements ItemListener {
    private DlgThirdPartyTrans adaptee;
    dlgThirdPartyTrans_rdiRFT_itemAdapter(DlgThirdPartyTrans adaptee) {
        this.adaptee = adaptee;
    }

    public void itemStateChanged(ItemEvent e) {
        adaptee.rdiRFT_itemStateChanged(e);
    }
}


class dlgThirdPartyTrans_rdiDirect_itemAdapter implements ItemListener {
    private DlgThirdPartyTrans adaptee;
    dlgThirdPartyTrans_rdiDirect_itemAdapter(DlgThirdPartyTrans adaptee) {
        this.adaptee = adaptee;
    }

    public void itemStateChanged(ItemEvent e) {
        adaptee.rdiDirect_itemStateChanged(e);
    }
}


class DlgThirdPartyTrans_btnOK_actionAdapter implements ActionListener {
    private DlgThirdPartyTrans adaptee;
    DlgThirdPartyTrans_btnOK_actionAdapter(DlgThirdPartyTrans adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.btnOK_actionPerformed(e);
    }
}
