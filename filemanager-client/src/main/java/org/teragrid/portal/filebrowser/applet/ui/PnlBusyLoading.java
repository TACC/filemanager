/* 
 * Created on Aug 14, 2008
 * 
 * Developed by: Rion Dooley - dooley [at] tacc [dot] utexas [dot] edu
 * 				 TACC, Texas Advanced Computing Center
 * 
 * https://www.tacc.utexas.edu/
 */

package org.teragrid.portal.filebrowser.applet.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

import org.jdesktop.swingx.JXBusyLabel;
import org.jdesktop.swingx.icon.EmptyIcon;
import org.jdesktop.swingx.painter.BusyPainter;

/**
 * OS X style busy label. Exteded from the JXBusyLabel component
 * in the SwingX project.
 * 
 * @author Rion Dooley < dooley [at] tacc [dot] utexas [dot] edu >
 *
 */
@SuppressWarnings({"serial","unused","unchecked"})
public class PnlBusyLoading extends JPanel {
	private static final int CNSTS = 50;
	private static final int W = 100;
	private static final int H = 100;
	
    private JXBusyLabel lblBusy;
    
    /**
     * Create a default busy loading label to indicate
     * an action is currently taking place. Default size
     * is 50x49
     */
    public PnlBusyLoading() {
        this(new Dimension(50,49));
        
    }

    /**
     * Create a default busy loading label to indicate
     * an action is currently taking place.
     * 
     * @param dim Size of the label
     */
    public PnlBusyLoading(Dimension dim) {
        super();
        
        this.lblBusy = createBusyLabel(dim);
        
        setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
        
        add(lblBusy,BorderLayout.CENTER);
    }

    protected static JXBusyLabel createBusyLabel(Dimension dim) {
        JXBusyLabel lblBusy = new JXBusyLabel(dim);
        BusyPainter painter = new BusyPainter(
                new RoundRectangle2D.Float(0, 0,15.5f,4.1f,10.0f,10.0f),
                new Ellipse2D.Float(7.5f,7.5f,30.26f,34.0f));
        painter.setTrailLength(7);
        painter.setPoints(7);
        painter.setFrame(0);
        lblBusy.setPreferredSize(dim);
        lblBusy.setIcon(new EmptyIcon(dim.width,dim.height));
        lblBusy.setBusyPainter(painter);
        lblBusy.setBusy(true);
        
        return lblBusy;
    }
    
    protected static JXBusyLabel createBusyLabel(Dimension dim, int trailLength, int numPoints) {
        JXBusyLabel lblBusy = new JXBusyLabel(dim);
        BusyPainter painter = new BusyPainter(
                new RoundRectangle2D.Float(0, 0,14.5f,3.1f,9.0f,9.0f),
                new Ellipse2D.Float(6.5f,6.5f,29.25f,33.0f));
        painter.setTrailLength(trailLength);
        painter.setPoints(numPoints);
        painter.setFrame(0);
        lblBusy.setPreferredSize(dim);
        lblBusy.setIcon(new EmptyIcon(dim.width,dim.height));
        lblBusy.setBusyPainter(painter);
        
        return lblBusy;
    }
    
    protected static JXBusyLabel createSmallBusyLabel() {
    	JXBusyLabel label = new JXBusyLabel(new Dimension(21,21));
    	BusyPainter painter = new BusyPainter(
    	new RoundRectangle2D.Float(0, 0,7.0f,2.4f,10.0f,10.0f),
    	new Ellipse2D.Float(3.0f,3.0f,15.0f,15.0f));
    	painter.setTrailLength(4);
    	painter.setPoints(8);
    	painter.setFrame(-1);
    	label.setPreferredSize(new Dimension(21,21));
    	label.setIcon(new EmptyIcon(21,21));
    	label.setBusyPainter(painter);
    	return label;
    }	
    
    public void setBusy(boolean start) {
        lblBusy.setBusy(start);
        lblBusy.setVisible(start);
        
        if (start) {
            // center the busy label on the file listing scroll viewport
            removeAll();
            
            int xTop = (getSize().width - lblBusy.getWidth())/2;
            int yTop = (getSize().height - lblBusy.getHeight())/2;
            
//            Dimension minSize = new Dimension(xTop, yTop);
//            Dimension prefSize = new Dimension(xTop, yTop);
//            Dimension maxSize = new Dimension(Short.MAX_VALUE, yTop);
//            
            lblBusy.setAlignmentX(Component.CENTER_ALIGNMENT);
            lblBusy.setAlignmentY(Component.CENTER_ALIGNMENT);
            
            add(Box.createVerticalGlue());
            add(lblBusy,Component.CENTER_ALIGNMENT);
            add(Box.createVerticalGlue());
            
//            revalidate();
            
        } 
        
    }
}
