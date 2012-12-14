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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.teragrid.portal.filebrowser.applet.ConfigSettings;
import org.teragrid.portal.filebrowser.applet.util.ResourceName;
import org.teragrid.portal.filebrowser.applet.util.SGGCResourceBundle;


/**
 * Simple About dialog for the overall application.
 * 
 * @author dooley
 *
 */
@SuppressWarnings("serial")
public class DlgAbout extends DlgEscape implements ActionListener {
	private JPanel panel1 = new JPanel();
	private JPanel panel2 = new JPanel();
	private JPanel insetsPanel1 = new JPanel();
	private JPanel insetsPanel2 = new JPanel();
	private JPanel insetsPanel3 = new JPanel();
	private JButton button1 = new JButton();
	private JLabel imageLabel = new JLabel();
	private JLabel label1 = new JLabel();
	private JLabel label2 = new JLabel();
	private JLabel label3 = new JLabel();
	private JLabel label4 = new JLabel();
	private BorderLayout borderLayout1 = new BorderLayout();
	private BorderLayout borderLayout2 = new BorderLayout();
	private FlowLayout flowLayout1 = new FlowLayout();
	private GridLayout gridLayout1 = new GridLayout();

    public DlgAbout(Frame parent) {
        super(parent,"About",true);
        try {
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            jbInit();
            Dimension dlgSize = getPreferredSize();
            Dimension frmSize = parent.getSize();
            Point loc = parent.getLocation();
            setLocation((frmSize.width - dlgSize.width) / 2 + loc.x, (frmSize.height - dlgSize.height) / 2 + loc.y);
            pack();
            setVisible(true);
        } catch (Exception exception) {
            exception.printStackTrace();
            org.teragrid.portal.filebrowser.applet.util.LogManager.debug(exception.getLocalizedMessage() + " at " + (exception.getStackTrace())[0]);  
        }
    }

    public DlgAbout() {
        this(null);
    }

    /**
     * Component initialization.
     *
     * @throws java.lang.Exception
     */
    private void jbInit() throws Exception {
        imageLabel.setIcon(org.teragrid.portal.filebrowser.applet.AppMain.icoPrompt);
        panel1.setLayout(borderLayout1);
        panel2.setLayout(borderLayout2);
        insetsPanel1.setLayout(flowLayout1);
        insetsPanel2.setLayout(flowLayout1);
        insetsPanel2.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        gridLayout1.setRows(2);
        gridLayout1.setColumns(1);
        label1.setText("<html>" +
							 "<span style=\"font-size:18px; color:\"><b>EUDAT File Manager</b></span><br>" +
							 "<small><span style=\"font-weight: bold;color:#333333;\">version " + ConfigSettings.SOFTWARE_VERSION + "</span></small><br>" +
							 "<small><span style=\"font-weight: bold;color:#333333;\">build date " + ConfigSettings.SOFTWARE_BUILD_DATE + "</span></small><br>" +
						"</html>");
        label2.setText("<html>Based on code originally developed by SJTU. Currently<br>" +
        					 "developed and maintained by the Texas Advanced<br>" +
        					 "Computing Center and CINECA. Funding is provided<br>" +
        					 "by the US National Science Foundation via the<br>" +
        					 "XSEDE project, and the EU's Seventh Framework<br>" +
        					 "Programme via the EUDAT project." +
        				"</html>");
        label3.setText("");
        label4.setText("");
        insetsPanel3.setLayout(gridLayout1);
        insetsPanel3.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        button1.setText(SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_OK));
        button1.addActionListener(this);
        insetsPanel2.add(imageLabel, null);
        panel2.add(insetsPanel2, BorderLayout.WEST);
        getContentPane().add(panel1, null);
        insetsPanel3.add(label1, null);
        insetsPanel3.add(label2, null);
        //insetsPanel3.add(label3, null);
        //insetsPanel3.add(label4, null);
        panel2.add(insetsPanel3, BorderLayout.CENTER);
        insetsPanel1.add(button1, null);
        panel1.add(insetsPanel1, BorderLayout.SOUTH);
        panel1.add(panel2, BorderLayout.NORTH);
        setResizable(false);
    }

    /**
     * Close the dialog on a button event.
     *
     * @param actionEvent ActionEvent
     */
    public void actionPerformed(ActionEvent actionEvent) {
        if (actionEvent.getSource() == button1) {
            dispose();
        }
    }
}
