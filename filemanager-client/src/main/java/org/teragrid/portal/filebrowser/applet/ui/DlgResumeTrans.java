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

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.TitledBorder;

import org.globus.ftp.FileInfo;
import org.teragrid.portal.filebrowser.applet.util.ResourceName;
import org.teragrid.portal.filebrowser.applet.util.SGGCResourceBundle;

@SuppressWarnings({"serial","unused"})
public class DlgResumeTrans extends DlgEscape {
    public static final int CHOICE_RESUME = 0;
    public static final int CHOICE_OVERWRITE = 1;
    public static final int CHOICE_RENAME = 2;
    public static final int CHOICE_SKIP = 3;

    //Input
    private FileInfo sourceFile = null;
    private FileInfo destFile = null;

    //Output
    private boolean succeed = false;
    private int choice = CHOICE_SKIP;
    private boolean remember = false;

    private JPanel pnlMain = new JPanel();
    private JButton btnOK = new JButton();
    private JButton btnCancel = new JButton();
    private JPanel jPanel1 = new JPanel();
    private ButtonGroup grpAction = new ButtonGroup();
    private TitledBorder titledBorder1 = new TitledBorder(SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_DLGRESUMETRANS_TITLEDBORDER1));
    private JRadioButton rdiResume = new JRadioButton();
    private JRadioButton rdiOverwrite = new JRadioButton();
    private JRadioButton rdiRename = new JRadioButton();
    private JRadioButton rdiSkip = new JRadioButton();
    private JCheckBox cbRemember = new JCheckBox();
    private JPanel pnlInfo = new JPanel();
    private JLabel lblMain = new JLabel();
    private JLabel jLabel1 = new JLabel();
    private JLabel lblSrcSize = new JLabel();
    private JLabel jLabel3 = new JLabel();
    private JLabel lblSrcTime = new JLabel();
    private JLabel jLabel5 = new JLabel();
    private JLabel lblDestSize = new JLabel();
    private JLabel jLabel7 = new JLabel();
    private JLabel lblDestTime = new JLabel();

    public DlgResumeTrans(Frame owner, String title, boolean modal, FileInfo sourceFile, FileInfo destFile) {
        this(owner, title, modal);

        this.sourceFile = sourceFile;
        this.destFile = destFile;
        String strLblMain = java.text.MessageFormat.format(
        		SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_DLGRESUMETRANS_LBLMAIN), 
        		new Object[] {ListModel.getFileName(sourceFile)});
        lblMain.setText(strLblMain);
        lblSrcSize.setText(ListModel.getSize(sourceFile));
        lblDestSize.setText(ListModel.getSize(destFile));
        lblSrcTime.setText(sourceFile.getDate());
        lblDestTime.setText(destFile.getDate());

        if(sourceFile.getSize() <= destFile.getSize()){
            rdiResume.setEnabled(false);
        }

        rdiSkip.setSelected(true);
    }

    public DlgResumeTrans(Frame owner, String title, boolean modal) {
        super(owner, title, modal);
        try {
            setResizable(false);
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            jbInit();
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

    public DlgResumeTrans() {
        this(new Frame(), SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_DLGRESUMETRANS_TITLE), false);
        try {
            jbInit();
        } catch (Exception ex) {
            ex.printStackTrace();
            org.teragrid.portal.filebrowser.applet.util.LogManager.debug(ex.getLocalizedMessage() + " at " + (ex.getStackTrace())[0]);  
        }

    }

    private void jbInit() throws Exception {
        pnlMain.setLayout(null);
        btnOK.setBounds(new Rectangle(224, 217, 70, 27));
        btnOK.setText(SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_OK));
        btnOK.addActionListener(new frmResumeTrans_btnOK_actionAdapter(this));
        btnCancel.setBounds(new Rectangle(307, 218, 77, 27));
        btnCancel.setText(SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_CANCEL));
        btnCancel.addActionListener(new frmResumeTrans_btnCancel_actionAdapter(this));
        jPanel1.setBorder(titledBorder1);
        jPanel1.setBounds(new Rectangle(20, 124, 359, 89));
        jPanel1.setLayout(null);
        rdiResume.setText(SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_DLGRESUMETRANS_RDIRESUME));
        rdiResume.setBounds(new Rectangle(67, 20, 92, 25));
        rdiOverwrite.setText(SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_DLGRESUMETRANS_RDIOVERWRITE));
        rdiOverwrite.setBounds(new Rectangle(149, 20, 102, 25));
        rdiRename.setText(SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_DLGRESUMETRANS_RDIRENAME));
        rdiRename.setBounds(new Rectangle(247, 20, 83, 25));
        rdiSkip.setText(SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_DLGRESUMETRANS_RDISKIP));
        rdiSkip.setBounds(new Rectangle(12, 20, 67, 25));
        cbRemember.setText(SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_DLGRESUMETRANS_CDREMEMBER));
        cbRemember.setBounds(new Rectangle(15, 51, 256, 25));
        pnlInfo.setBounds(new Rectangle(6, 11, 372, 115));
        pnlInfo.setLayout(null);
        lblMain.setText("jLabel1");
        lblMain.setBounds(new Rectangle(14, 4, 340, 22));
        jLabel1.setText(SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_DLGRESUMETRANS_JLABEL1));
        jLabel1.setBounds(new Rectangle(10, 36, 118, 20));
        lblSrcSize.setText("jLabel2");
        lblSrcSize.setBounds(new Rectangle(133, 34, 64, 22));
        jLabel3.setText(SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_DLGRESUMETRANS_JLABEL3));
        jLabel3.setBounds(new Rectangle(204, 33, 89, 22));
        lblSrcTime.setText("jLabel4");
        lblSrcTime.setBounds(new Rectangle(295, 29, 88, 27));
        jLabel5.setText(SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_DLGRESUMETRANS_JLABEL5));
        jLabel5.setBounds(new Rectangle(9, 70, 122, 18));
        lblDestSize.setText("jLabel6");
        lblDestSize.setBounds(new Rectangle(133, 69, 67, 18));
        jLabel7.setText(SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_DLGRESUMETRANS_JLABEL7));
        jLabel7.setBounds(new Rectangle(205, 67, 86, 21));
        lblDestTime.setText("jLabel8");
        lblDestTime.setBounds(new Rectangle(295, 64, 91, 23));
        pnlMain.add(btnOK);
        pnlMain.add(btnCancel);
        pnlMain.add(jPanel1);
        pnlMain.add(pnlInfo);
        pnlInfo.add(lblMain);
        pnlInfo.add(jLabel5);
        pnlInfo.add(lblDestTime);
        pnlInfo.add(lblSrcTime);
        pnlInfo.add(jLabel3);
        pnlInfo.add(jLabel7);
        pnlInfo.add(lblDestSize);
        pnlInfo.add(lblSrcSize);
        pnlInfo.add(jLabel1);
        jPanel1.add(cbRemember);
        jPanel1.add(rdiSkip);
        jPanel1.add(rdiResume);
        jPanel1.add(rdiRename);
        jPanel1.add(rdiOverwrite);
        grpAction.add(rdiRename);
        grpAction.add(rdiSkip);
        grpAction.add(rdiOverwrite);
        grpAction.add(rdiResume);
        pnlMain.setPreferredSize(new Dimension(405, 260));
        getContentPane().add(pnlMain);
    }

    public void btnCancel_actionPerformed(ActionEvent e) {
        this.dispose();
    }

    public void btnOK_actionPerformed(ActionEvent e) {
        this.succeed = true;

        if (rdiResume.isSelected()) {
            this.choice = DlgResumeTrans.CHOICE_RESUME;
        } else if (rdiOverwrite.isSelected()) {
            this.choice = DlgResumeTrans.CHOICE_OVERWRITE;
        } else if (rdiRename.isSelected()) {
            this.choice = DlgResumeTrans.CHOICE_RENAME;
        } else {
            this.choice = DlgResumeTrans.CHOICE_SKIP;
        }

        this.remember = cbRemember.isSelected();

        this.dispose();
    }

	public boolean isSucceed() {
		return this.succeed;
	}

	public boolean isRemember() {
		return this.remember;
	}

	public int getChoice() {
		return this.choice;
	}
}


class frmResumeTrans_btnOK_actionAdapter implements ActionListener {
    private DlgResumeTrans adaptee;
    frmResumeTrans_btnOK_actionAdapter(DlgResumeTrans adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.btnOK_actionPerformed(e);
    }
}


class frmResumeTrans_btnCancel_actionAdapter implements ActionListener {
    private DlgResumeTrans adaptee;
    frmResumeTrans_btnCancel_actionAdapter(DlgResumeTrans adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.btnCancel_actionPerformed(e);
    }
}
