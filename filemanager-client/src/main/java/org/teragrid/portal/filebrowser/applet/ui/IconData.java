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

import javax.swing.Icon;

import org.teragrid.portal.filebrowser.applet.transfer.FTPSettings;


public class IconData {
	protected Icon m_icon;
    protected Icon m_expandedIcon;
    protected Object m_data;


    public IconData(Icon icon, Object data) {
    	this.m_icon = icon;
    	this.m_expandedIcon = null;
    	this.m_data = data;
    }


    public IconData(Icon icon, Icon expandedIcon, Object data) {
    	this.m_icon = icon;
    	this.m_expandedIcon = expandedIcon;
    	this.m_data = data;
    }


    public Icon getIcon() {
        return this.m_icon;
    }


    public Icon getExpandedIcon() {
        return this.m_expandedIcon != null ? this.m_expandedIcon : this.m_icon;
    }


    public Object getObject() {
        return this.m_data;
    }


    public String toString() {
        if(this.m_data instanceof FTPSettings) {
        	return ((FTPSettings)this.m_data).name;
        }
        return this.m_data.toString();
    }
}
