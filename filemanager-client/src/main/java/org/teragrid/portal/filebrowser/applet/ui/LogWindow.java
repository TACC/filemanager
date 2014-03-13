package org.teragrid.portal.filebrowser.applet.ui;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

import org.teragrid.portal.filebrowser.applet.AppMain;
import org.teragrid.portal.filebrowser.applet.util.LogManager;
import org.teragrid.portal.filebrowser.applet.util.ResourceName;
import org.teragrid.portal.filebrowser.applet.util.SGGCResourceBundle;

import com.explodingpixels.macwidgets.IAppWidgetFactory;

/**
 * Simple panel to display the log4j output to the user so they don't
 * have to use the java console to get the debug info.
 * 
 * @author dooley
 *
 */
@SuppressWarnings("serial")
public class LogWindow extends JFrame implements WindowListener {

	private JTextArea textArea = null;

	private JScrollPane pane = null;

	protected JPopupMenu mnuLog = new JPopupMenu();
	
	protected JMenuItem mnuSelCopy = new JMenuItem();// Copy selected content to
														// clipboard
	protected JMenuItem mnuCopy = new JMenuItem();// Copy all the content to
													// clipboard
	protected JMenuItem mnuSave = new JMenuItem();// Save the log info to
													// certain file
	protected JMenuItem mnuClear = new JMenuItem();// Clear all the content in
													// the log box
	
	protected ActionListener evtMenu = new LogWindow_mnuFile_actionAdapter(this);
	protected LogWindow_txtLog_mouseAdapter evtMouse = new LogWindow_txtLog_mouseAdapter(this);
	
    public LogWindow(Frame frame, String title, int width, int height) {
		
    	super(title);
		
    	tInit();
		
		setPreferredSize(new Dimension(width, height));
		setResizable(true);
		pack();
		
		locateFrame(frame);
		
		addWindowListener(this);
	}

	public void tInit() {
		
		mnuSelCopy.setIcon(AppMain.icoCopy);
        mnuSelCopy.setText(SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_PNLBROWSE_MNUSELCOPY));
        mnuSelCopy.addActionListener(evtMenu);
        mnuCopy.setIcon(AppMain.icoCopy);
        mnuCopy.setText(SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_PNLBROWSE_MNUCOPY));
        mnuCopy.addActionListener(evtMenu);
        mnuSave.setIcon(AppMain.icoFile);
        mnuSave.setText(SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_PNLBROWSE_MNUSAVE));
        mnuSave.addActionListener(evtMenu);
        mnuClear.setIcon(AppMain.icoDelete);
        mnuClear.setText(SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_PNLBROWSE_MNUCLEAR));
        mnuClear.addActionListener(evtMenu);
        mnuLog.add(mnuSelCopy);
        mnuLog.add(mnuCopy);
        mnuLog.add(mnuSave);
        mnuLog.add(mnuClear);
        
		textArea = new JTextArea();
		textArea.setEditable(false);
		textArea.addMouseListener(evtMouse);
		
		pane = new JScrollPane(textArea);
		pane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		pane.setWheelScrollingEnabled(true);
		pane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		IAppWidgetFactory.makeIAppScrollPane(pane);

		add(pane);
		
	}
	
	public JTextArea getTextArea() {
		return this.textArea;
	}

	/**
	 * This method appends the data to the text area.
	 * 
	 * @param data
	 *            the Logging information data
	 */
	public void showInfo(String data) {
		textArea.append(data);
		this.getContentPane().validate();
	}

	public void mnuFile_actionPerformed(ActionEvent e) {
		// Copy the selected log contents
		if (mnuSelCopy == e.getSource()) {
			TextTransfer textTransfer = new TextTransfer();
			textTransfer.setClipboardContents(textArea.getSelectedText());
		}
		// Copy the log contents
		else if (mnuCopy == e.getSource()) {
			TextTransfer textTransfer = new TextTransfer();
			textTransfer.setClipboardContents(textArea.getText());
		}
		// Save the log to a file
		else if (mnuSave == e.getSource()) {
			MyFileChooser fileChooser = new MyFileChooser();
			fileChooser.actionPerformed(e);
			if (fileChooser.canSave()) {
				try {
					File outFile = new File(fileChooser.getFullFileName());
					PrintWriter out = new PrintWriter(new DataOutputStream(
							new FileOutputStream(outFile)));
					out.println(textArea.getText());
					out.close();

					JOptionPane
							.showMessageDialog(
									null,
									SGGCResourceBundle
											.getResourceString(ResourceName.KEY_MSG_PNLBROWSE_SAVELOG)
											+ fileChooser.getFullFileName());
				} catch (IOException ex) {
					ex.printStackTrace();
					LogManager.debug(ex.getLocalizedMessage() + " at "
							+ (ex.getStackTrace())[0]);
				}
			}
		}
	}

	protected void locateFrame(Frame frame) {
		Dimension dlgSize = getPreferredSize();
		Dimension frmSize = frame.getSize();
		Point loc = frame.getLocation();
		setLocation((frmSize.width - dlgSize.width) / 2 + loc.x,
				(frmSize.height - dlgSize.height) / 2 + loc.y);
	}
	
	public void txtLog_mousePressed(MouseEvent e) {
		if (isRightClickEvent(e)) {
			if (textArea.getSelectedText() == null) {
				mnuSelCopy.setEnabled(false);
			} else
				mnuSelCopy.setEnabled(true);
			mnuLog.show(textArea, e.getX(), e.getY());
		} 
	}

/**
     * Check to see if the user right clicks with a single button mouse.
     * This is needed for Mac laptops.
     * 
     * @param ev
     * @return
     */
    private boolean isRightClickEvent(MouseEvent ev) {
        int mask = InputEvent.BUTTON1_MASK - 1;
        int mods = ev.getModifiers() & mask;
        if (mods == 0) {
            return false;
        } else {
            return true;
        }
    }
    
	public void windowActivated(WindowEvent e) {}
	public void windowDeactivated(WindowEvent e) {}
	public void windowDeiconified(WindowEvent e) {}
	public void windowIconified(WindowEvent e) {}
	public void windowOpened(WindowEvent e) {}
	
	public void windowClosed(WindowEvent e) {
		((AppMain)AppMain.getApplet()).setLogWindowVisible(false);
	}

	public void windowClosing(WindowEvent e) {
		((AppMain)AppMain.getApplet()).setLogWindowVisible(false);
	}


}

class LogWindow_mnuFile_actionAdapter implements ActionListener {
	private LogWindow adaptee;
	LogWindow_mnuFile_actionAdapter(LogWindow adaptee) {
		this.adaptee = adaptee;
	}
	
	public void actionPerformed(ActionEvent e) {
		adaptee.mnuFile_actionPerformed(e);
	}
}

class LogWindow_txtLog_mouseAdapter extends MouseAdapter {
	private LogWindow adaptee;

	LogWindow_txtLog_mouseAdapter(LogWindow adaptee) {
		this.adaptee = adaptee;
	}

	public void mousePressed(MouseEvent e) {
		adaptee.txtLog_mousePressed(e);
	}
}
