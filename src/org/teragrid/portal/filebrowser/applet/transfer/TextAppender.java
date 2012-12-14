/*
 * Portions of this file Copyright 2004-2007 Shanghai Jiaotong University
 * 
 * This file or a portion of this file is licensed under the
 * terms of the Globus Toolkit Public License, found at
 * http://www.globus.org/toolkit/legal/4.0/
 * If you redistribute this file, with or without
 * modifications, you must include this notice in the file.
 */

package org.teragrid.portal.filebrowser.applet.transfer;

//import org.apache.log4j.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;

import javax.swing.JTextArea;

import org.apache.log4j.Appender;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.spi.LoggingEvent;
import org.teragrid.portal.filebrowser.applet.util.LogManager;


class TextBoxMap {
    private JTextArea textBox;
    private String threadName;

    TextBoxMap(JTextArea text, String thName) {
        this.textBox = text;
        this.threadName = thName;
    }

    public String getThreadName() {
        return this.threadName;
    }

    public void addLog(String sLog){
    	this.textBox.append(sLog);
    	this.textBox.select(this.textBox.getText().length()-1,0);
    	this.textBox.setCaretPosition(this.textBox.getText().indexOf(sLog));
    }
    
    public boolean equals(Object o) {
    	if (o instanceof TextBoxMap) {
    		return (((TextBoxMap)o).threadName.equals(threadName));
    	} if (o instanceof String) {
    		return (((String)o).equals(threadName));
    	}
    	return false;
    }
}

public class TextAppender extends AppenderSkeleton{
	@SuppressWarnings("unused")
	private String message;
    private ArrayList<TextBoxMap> logBoxes;
    private static TextAppender instance = null;
    private static int logPanelTextBoxMapIndex = -1;
    
    public TextAppender() {
        String pattern =  "[%d{yyyy.MM.dd HH:mm:ss} %c{2}] %-5p %m%n";
        this.layout = new PatternLayout(pattern);
        this.logBoxes = new ArrayList<TextBoxMap>();
        instance = this;
    }

    public void append(LoggingEvent event) {
        String threadNm = event.getThreadName();
        String logName = event.getLoggerName().toString();
        String content = event.getMessage().toString();
        
        if (logName.equalsIgnoreCase("org.globus.ftp.vanilla.FTPControlChannel")) {
            Iterator<TextBoxMap> iter = this.logBoxes.iterator();
            while (iter.hasNext()) {
                TextBoxMap mapper = iter.next();
                if (threadNm.equalsIgnoreCase(mapper.getThreadName())) {
                    content=content.replaceFirst("Control channel","");
                    mapper.addLog(content);
                    if(!content.endsWith("\n")) mapper.addLog("\n");
                    break;
                }
            }
        } 
        
        // we log everything to the logging pane
        this.logBoxes.get(logPanelTextBoxMapIndex).addLog(layout.format(event));
//        if(!content.endsWith("\n")) this.logBoxes.get(logPanelTextBoxMapIndex).addLog("\n");

        if(layout.ignoresThrowable()) {
            String[] s = event.getThrowableStrRep();
            if (s != null) {
                int len = s.length;
                for (int i = 0; i < len; i++) {
                	LogManager.debug(s[i]);
//                    System.err.print(s[i]);
//                    System.err.println("");
                }
            }
        }
    }

    public void addTextBox(JTextArea ePane, String nm) {
    	this.logBoxes.add(new TextBoxMap(ePane, nm));
    	
    	// get easy access to the main logging box
    	if (nm.equals("AppMain")) {
    		logPanelTextBoxMapIndex = logBoxes.size() -1;
    	}
    }

    public void removeTextBox(String thName) {
        Iterator<TextBoxMap> iter = this.logBoxes.iterator();
        int toDel = 0;
        while(iter.hasNext()) {
            TextBoxMap mapper = (TextBoxMap) iter.next();
            if(mapper.getThreadName().equalsIgnoreCase(thName)) {
            	this.logBoxes.remove(toDel);
                break;
            }
            toDel++;
        }
    }

    @SuppressWarnings("unchecked")
	public static TextAppender getInstance() {
        if (instance == null) {
            Logger logger = Logger.getRootLogger();
            Enumeration allApp = logger.getAllAppenders();
            for(;allApp.hasMoreElements();){
                Appender one = (Appender) allApp.nextElement();
                if (one instanceof TextAppender) {
                    instance = (TextAppender) one;
                    break;
                }
            }
        }
        return instance;
    }

  /**
     The WriterAppender requires a layout. Hence, this method returns
     <code>true</code>.
  */
    public boolean requiresLayout() {
        return true;
    }

  /**
     Close this appender instance. The underlying stream or writer is
     also closed.
     <p>Closed appenders cannot be reused.*/
    public synchronized void close() {
        if(this.closed)
            return;
        this.closed = true;
    }

}
