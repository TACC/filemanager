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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.teragrid.portal.filebrowser.applet.util.LogManager;
import org.teragrid.portal.filebrowser.applet.util.ResourceName;
import org.teragrid.portal.filebrowser.applet.util.SGGCResourceBundle;


@SuppressWarnings("serial")
public class MyFileChooser extends JFileChooser implements ActionListener {
    private MyFileFilter myFilter;
    private String fileName;
    private boolean save;

    public MyFileChooser() {
        this.setAcceptAllFileFilterUsed(true);

        this.myFilter = new MyFileFilter("doc", 
        		SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_MYFILECHOOSER_WORD_DESC));
        this.addChoosableFileFilter(this.myFilter);
        this.myFilter = new MyFileFilter("txt", 
        		SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_MYFILECHOOSER_TXT_DESC));
        this.addChoosableFileFilter(this.myFilter);

        this.setFileSelectionMode(JFileChooser.FILES_ONLY);
        this.setMultiSelectionEnabled(false);
        try {
            jbInit();
        } catch (Exception ex) {
            ex.printStackTrace();
            LogManager.debug(ex.getLocalizedMessage() + " at " + (ex.getStackTrace())[0]);  
        }
    }

    public void actionPerformed(ActionEvent e) {
    	this.save = false;
        int retval =  this.showSaveDialog(null);
        if (retval == JFileChooser.APPROVE_OPTION) {
            File theFile = this.getSelectedFile();
            this.fileName = theFile.getName();
            if (this.getFileFilter().getClass().toString().equalsIgnoreCase(
                    "class javax.swing.plaf.basic.BasicFileChooserUI$AcceptAllFileFilter")) {
                int pos = this.fileName.lastIndexOf('.');
                if (pos == -1) {
                    JOptionPane.showMessageDialog(null,
                    		SGGCResourceBundle.getResourceString(ResourceName.KEY_ERROR_MYFILECHOOSER_NOEXTENSION),
                    		SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_MYFILECHOOSER_INVALIDFILE), 
                    		2);
                    this.actionPerformed(e);
                } else
                	this.save = true;
            } else if (this.getFileFilter().accept(theFile)) {
            	this.save = true;
                int pos = this.fileName.lastIndexOf('.');
                if (pos == -1) {
                	this.fileName = theFile.getPath().concat("."+this.getFileFilter().toString());
                } else
                	this.fileName = theFile.getPath();
            } else {
                JOptionPane.showMessageDialog(null,
                		SGGCResourceBundle.getResourceString(ResourceName.KEY_ERROR_MYFILECHOOSER_FORMATERROR),
                		SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_MYFILECHOOSER_INVALIDFILE), 
                		2);
                this.actionPerformed(e);
            }
        } else if (retval == JFileChooser.CANCEL_OPTION) {
        } else if (retval == JFileChooser.ERROR_OPTION) {
        } else {
        }
        if (this.save) {
            File theFile = new File(this.fileName);
            if (theFile.exists()) {
                int decision = JOptionPane.showConfirmDialog(null,
                		SGGCResourceBundle.getResourceString(ResourceName.KEY_ERROR_MYFILECHOOSER_FILEALREADYEXIST),
                		SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_MYFILECHOOSER_FILEEXIST), 
                		2);
                if (decision == 2) {
                	this.save = false;
                    this.actionPerformed(e);
                }
            }
        }
    }

    private void jbInit() throws Exception {
    }

    public boolean canSave() {
        return this.save;
    }

    public String getFullFileName() {
        return this.fileName;
    }
}
