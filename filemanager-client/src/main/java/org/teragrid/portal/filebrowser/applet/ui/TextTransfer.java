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

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import org.teragrid.portal.filebrowser.applet.util.LogManager;

public final class TextTransfer implements ClipboardOwner {
   /**
   * Empty implementation of the ClipboardOwner interface.
   */
    public void lostOwnership( Clipboard aClipboard, Transferable aContents) {
            //do nothing
    }

    /**
     * Place a String on the clipboard, and make this class the
     * owner of the Clipboard's contents.
     */
    public void setClipboardContents( String aString ){
        StringSelection stringSelection = new StringSelection( aString );
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents( stringSelection, this );
    }

    /**
     * Get the String residing on the clipboard.
     *
     * @return any text found on the Clipboard; if none found, return an
     * empty String.
     */
    public String getClipboardContents() {
        String result = "";
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        //odd: the Object param of getContents is not currently used
        Transferable contents = clipboard.getContents(null);
        boolean hasTransferableText = (contents != null) &&
                                      contents.isDataFlavorSupported(DataFlavor.stringFlavor);
        if ( hasTransferableText ) {
            try {
                result = (String)contents.getTransferData(DataFlavor.stringFlavor);
            } catch (UnsupportedFlavorException ex){
                //highly unlikely since we are using a standard DataFlavor
                ex.printStackTrace();
                LogManager.debug(ex.getLocalizedMessage() + " at " + (ex.getStackTrace())[0]);  
            } catch (IOException ex) {
                ex.printStackTrace();
                LogManager.debug(ex.getLocalizedMessage() + " at " + (ex.getStackTrace())[0]);  
            }
        }
        return result;
    }
}
