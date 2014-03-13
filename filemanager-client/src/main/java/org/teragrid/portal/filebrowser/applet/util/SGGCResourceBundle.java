/*
 * Portions of this file Copyright 2004-2007 Shanghai Jiaotong University
 * 
 * This file or a portion of this file is licensed under the
 * terms of the Globus Toolkit Public License, found at
 * http://www.globus.org/toolkit/legal/4.0/
 * If you redistribute this file, with or without
 * modifications, you must include this notice in the file.
 */

package org.teragrid.portal.filebrowser.applet.util;

import java.util.ResourceBundle;

public class SGGCResourceBundle {
	
	static final ResourceBundle resourceBundle = ResourceBundle.getBundle(Resources_en.class.getName());

	public static ResourceBundle getResourceBundle() {
		return resourceBundle;
	}
	
	public static String getResourceString(String key) {
		return resourceBundle.getString(key);
	}
}
