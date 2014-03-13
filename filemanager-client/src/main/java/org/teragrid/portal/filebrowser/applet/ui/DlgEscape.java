package org.teragrid.portal.filebrowser.applet.ui;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;

@SuppressWarnings("serial")
public class DlgEscape extends JDialog {
  public DlgEscape() {
    this((Frame)null, false);
  }
  public DlgEscape(Frame owner) {
    this(owner, false);
  }
  public DlgEscape(Frame owner, boolean modal) {
    this(owner, null, modal);
  }
  public DlgEscape(Frame owner, String title) {
    this(owner, title, false);     
  }
  public DlgEscape(Frame owner, String title, boolean modal) {
    super(owner, title, modal);
  }
  public DlgEscape(Dialog owner) {
    this(owner, false);
  }
  public DlgEscape(Dialog owner, boolean modal) {
    this(owner, null, modal);
  }
  public DlgEscape(Dialog owner, String title) {
    this(owner, title, false);     
  }
  public DlgEscape(Dialog owner, String title, boolean modal) {
    super(owner, title, modal);
  }
  protected JRootPane createRootPane() {
    ActionListener actionListener = new ActionListener() {
      public void actionPerformed(ActionEvent actionEvent) {
        setVisible(false);
      }
    };
    JRootPane rootPane = new JRootPane();
    KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
    rootPane.registerKeyboardAction(actionListener, stroke, JComponent.WHEN_IN_FOCUSED_WINDOW);
    return rootPane;
  }
  
  protected void locateDialog(Component component) {
		Dimension dlgSize = getPreferredSize();
		Dimension cmpSize = component.getSize();
		Point loc = component.getLocation();
		setLocation((cmpSize.width - dlgSize.width) / 2 + loc.x,
				(cmpSize.height - dlgSize.height) / 2 + loc.y);
	}

}

