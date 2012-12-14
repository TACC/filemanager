/* 
 * Created on Oct 16, 2007
 * 
 * Developed by: Rion Dooley - dooley [at] tacc [dot] utexas [dot] edu
 * 				 TACC, Texas Advanced Computing Center
 * 
 * https://www.tacc.utexas.edu/
 */

package org.teragrid.portal.filebrowser.applet.ui;

import java.awt.Graphics;
import java.awt.Image;

import javax.swing.BorderFactory;
import javax.swing.JTextField;

import org.teragrid.portal.filebrowser.applet.AppMain;

@SuppressWarnings("serial")
public class RoundField extends JTextField {

    public RoundField() {
        this(0);
    }
    
    public RoundField(int cols) {
        super();

        // We must be non-opaque since we won't fill all pixels.
        // This will also stop the UI from filling our background.
        setOpaque(false);

        // Add an empty border around us to compensate for
        // the rounded corners.
        setBorder(BorderFactory.createEmptyBorder(3, 5, 3, 5));
        
        
    }

    protected void paintComponent(Graphics g) {
        int width = getWidth();
        int height = getHeight();
        Image imgSearch = AppMain.icoSearch.getImage();
//        Image imgStop = AppMain.icoStop.getImage();
        // Paint a rounded rectangle in the background.
        g.setColor(getBackground());
        g.fillRoundRect(0, 0, width, height, height, height);
        g.drawImage(imgSearch,width-25,0,this);
//        g.drawImage(imgStop,width-25,0,this);
        // Now call the superclass behavior to paint the foreground.
        super.paintComponent(g);
        
    }
    
    public void setText(String s) {
//        super.setText("\t" + s);
    	super.setText(s);
    }
}