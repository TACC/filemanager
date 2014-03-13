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

import java.io.File;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.swing.filechooser.FileFilter;

@SuppressWarnings({"unused","unchecked"})
public class MyFileFilter extends FileFilter {
    private static String TYPE_UNKNOWN = "Type Unknown";
    private static String HIDDEN_FILE = "Hidden File";

    private Hashtable filters = null;
    private String extensionName = null;
    private String description = null;
    private String fullDescription = null;
    private boolean useExtensionsInDescription = true;

    public MyFileFilter() {
        this.filters = new Hashtable();
    }

    public MyFileFilter(String extension) {
        this(extension,null);
    }

    public MyFileFilter(String extension, String description) {
        this();
        if(extension!=null) {
        	addExtension(extension);
        }
        if(description!=null) {
        	setDescription(description);
        }
    }

    public MyFileFilter(String[] filters) {
        this(filters, null);
    }

    public MyFileFilter(String[] filters, String description) {
        this();
        for (int i = 0; i < filters.length; i++) {
            addExtension(filters[i]);
        }
        if(description!=null) {
        	setDescription(description);
        }
    }

    public boolean accept(File f) {
        if(f != null) {
            if(f.isDirectory()) {
                return true;
            }
            String extension = getExtension(f);
            if(extension != null && this.filters.get(getExtension(f)) != null) {
                return true;
            };
        }
        return false;
    }

    public String getExtension(File f) {
        int pos = 0;
        if(f != null) {
            String filename = f.getName();
            pos = filename.lastIndexOf('.');
            if(pos>0 && pos<filename.length()-1) {
                return filename.substring(pos+1).toLowerCase();
            };
        }
        if (pos == -1 && this.useExtensionsInDescription) {
            return this.extensionName;
        }
        return null;
    }

    public void addExtension(String extension) {
        if(this.filters == null) {
        	this.filters = new Hashtable(5);
        }
        this.filters.put(extension.toLowerCase(), this);
        this.extensionName = extension.toLowerCase();
        this.fullDescription = null;
    }

    public String getDescription() {
        if(this.fullDescription == null) {
            if(this.description == null || isExtensionListInDescription()) {
            	this.fullDescription = this.description==null ? "(" : this.description + " (";
                Enumeration extensions = this.filters.keys();
                if(extensions != null) {
                	this.fullDescription += "." + (String) extensions.nextElement();
                    while (extensions.hasMoreElements()) {
                    	this.fullDescription += ", ." + (String) extensions.nextElement();
                    }
                }
                this.fullDescription += ")";
            } else {
            	this.fullDescription = this.description;
            }
        }
    return this.fullDescription;
}

public String toString() {
    return this.extensionName;
}

public void setDescription(String description) {
    this.description = description;
    this.fullDescription = null;
  }

  public void setExtensionListInDescription(boolean b) {
	  this.useExtensionsInDescription = b;
      this.fullDescription = null;
  }

  public boolean isExtensionListInDescription() {
      return this.useExtensionsInDescription;
  }
}
