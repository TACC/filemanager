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

import java.net.URL;

import org.apache.log4j.Appender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.PropertyConfigurator;
import org.teragrid.portal.filebrowser.applet.AppMain;
import org.teragrid.portal.filebrowser.applet.transfer.TextAppender;


public class LogManager {
	private static Logger logger = Logger.getLogger(LogManager.class.getName());
    private static Appender textAreaAppender;
    
    private static URL logFile = LogManager.class.getClassLoader().getResource("log4j.properties");
    
	public static void init() {
	    
        textAreaAppender = new TextAppender();
        if (AppMain.getApplet() != null) {
        	((TextAppender)textAreaAppender).addTextBox(AppMain.getLogWindow().getTextArea(), Thread.currentThread().getName());
        	((TextAppender)textAreaAppender).setLayout(new PatternLayout(" [ %d{H:mm:ss} ] %-5p %m%n"));
        }
        Logger.getRootLogger().removeAllAppenders();
        PropertyConfigurator.configure(logFile);
        Logger.getRootLogger().addAppender(textAreaAppender);
	}

	public static Logger getLogger() {
		return logger;
	}
	
	public static void warn(String message) {
		logger.warn(message);
	}
	
	public static void debug(String message) {
		logger.debug(message);
	}
	
	public static void error(String message) {
		logger.error(message);
	}
	
	public static void error(String message,Throwable e) {
        logger.error(message,e);
    }
	
	public static void fatal(String message) {
		logger.fatal(message);
	}
	
	public static void info(String message) {
		// The swing issues kill the server. Check to see if running headless before
		// making this call and initializing the static AppMain variables.
	    if (System.getProperty("java.awt.headless") == null || 
	    		!System.getProperty("java.awt.headless").equals("true"))
	    	AppMain.updateSplash(-1, message);
		logger.info(message);
	}
}



