/* 
 * Created on Aug 5, 2008
 * 
 * Developed by: Rion Dooley - dooley [at] tacc [dot] utexas [dot] edu
 * 				 TACC, Texas Advanced Computing Center
 * 
 * https://www.tacc.utexas.edu/
 */

package edu.utexas.tacc.wcs.filemanager.client.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;

import edu.utexas.tacc.wcs.filemanager.client.AppMain;
import edu.utexas.tacc.wcs.filemanager.client.util.LogManager;

/**
 * Input dialog box with autocomplete text field.  When called from a 
 * PnlBrowse with local FTPSettings, it autocompletes file paths.  When
 * called from a PnlBrowse with remote FTPSettings, it autocompletes 
 * from a persisted history.
 * 
 * @author Rion Dooley < dooley [at] tacc [dot] utexas [dot] edu >
 *
 */
@SuppressWarnings("serial")
public class DlgGoTo extends DlgEscape implements KeyListener, ActionListener {

//    private AutoCompleter fac = null;
    private JTextField textField = null;
    private JButton btnOk = null;
    private JButton btnCancel = null;
    private PnlBrowse pnlBrowse;
    
    class EnterAction extends AbstractAction {
    	
    	DlgGoTo parent;
    	
    	public EnterAction(DlgGoTo parent) {
    		super();
    		this.parent = parent;
    	}
    	
        public void actionPerformed(ActionEvent e){ 
        	LogManager.debug("Enter presed");
        	parent.btnOk.doClick(); 
        } 
    }; 
    
    public DlgGoTo(PnlBrowse pnlBrowse) {
    	super(AppMain.getFrame(), false);
    	
        this.pnlBrowse = pnlBrowse;
        textField = new JTextField();
        textField.addKeyListener(this);
        
        // this just never worked out as well as I'd like it too.
//        if (pnlBrowse.ftpServer.type == FTPType.FILE) {
//            fac = new FileAutoCompleter(textField);
//        } else {
//            fac = new PropertiesAutoCompleter(textField,pnlBrowse.ftpServer.host);
//        }
        init();
        this.setFocusable(true);
        locateDialog(pnlBrowse);
//        this.requestFocus();
        textField.requestFocus();
    }
    
    private void init() {
            
        JPanel mainPanel = new JPanel(new GridLayout(3,1,10,10));
        
        ((GridLayout) mainPanel.getLayout()).setVgap(5);
        
        Border titledBorder = BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder());
        Border bufferBorder = BorderFactory.createEmptyBorder(10,10,10,10);
        
        mainPanel.setBorder(
                BorderFactory.createCompoundBorder(titledBorder, bufferBorder));
        
        mainPanel.addKeyListener(this);
        mainPanel.setPreferredSize(new Dimension(350,115));
        mainPanel.setLayout(new BoxLayout(mainPanel,BoxLayout.Y_AXIS));
       
        mainPanel.add(createLabelPanel());
        mainPanel.add(textField);
        mainPanel.add(createButtonPanel());
  
        setContentPane(mainPanel);
        setResizable(false);
        pack();
        
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        setVisible(true);
    }
    
    private JPanel createLabelPanel() {
    	
    	JPanel buttonInterior = new JPanel();
    	buttonInterior.setLayout(new GridLayout(1,2,5,0));
    	buttonInterior.add(new JLabel("Go to the folder: "));
    	
    	JPanel labelPanel = new JPanel();
    	labelPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
    	labelPanel.add(buttonInterior);
    	labelPanel.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
    	
        return labelPanel;
    }
    
    public String getNewDirectory() {
        return textField.getText();
    }
    
    
    private Component createButtonPanel() {
        
        btnOk = new JButton("Go");
        btnOk.setVerticalTextPosition(AbstractButton.CENTER);
        btnOk.setHorizontalTextPosition(AbstractButton.RIGHT);
        btnOk.addKeyListener(this);
        btnOk.addActionListener(this);

        btnCancel = new JButton("Cancel");
        btnCancel.setVerticalTextPosition(AbstractButton.CENTER);
        btnCancel.setHorizontalTextPosition(AbstractButton.CENTER);
        btnCancel.setToolTipText("Click to close this window");
        btnCancel.addActionListener(this);
        btnCancel.addKeyListener(this); 
        
        JPanel buttonInterior = new JPanel();
        buttonInterior.setLayout(new GridLayout(1,2,5,0));
        buttonInterior.add(btnCancel);
        buttonInterior.add(btnOk);
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(buttonInterior);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
        return buttonPanel;
    }
    
 // key listener interface implementation
    public void keyTyped(KeyEvent arg0) {}
    public void keyReleased(KeyEvent arg0) {}
    public void keyPressed(KeyEvent event) {
        int key = event.getKeyCode();
//          if (key == KeyEvent.VK_ENTER) {
//              setVisible(false);
//              if (textField.getText() != null && !textField.getText().equals("")) {
//                  pnlBrowse.setCurrentDir(textField.getText());
//                  fac.acceptedListItem(textField.getText());
//                  if(pnlBrowse.tBrowse.cmdEmpty()) {
//                      pnlBrowse.tBrowse.cmdAdd("Cwd",textField.getText(),null);
//                  }
//              }
//              this.dispose();
//          } else 
        if (key == KeyEvent.VK_ESCAPE) {
        	this.setVisible(false);
        	textField.setText("");
        	this.dispose();
        } else if (key == KeyEvent.VK_ENTER) {
        	LogManager.debug("Enter presed");
        	btnOk.doClick();
        }
    }

    public void actionPerformed(ActionEvent evt) {
        if (evt.getSource() == btnOk) {
            this.setVisible(false);
            
            if (textField.getText() != null && !textField.getText().equals("")) {
                
                pnlBrowse.setCurrentDir(textField.getText());
//                if (pnlBrowse.ftpServer.type != FTPType.FILE) {
//                    fac.acceptedListItem(textField.getText());
//                }
                
                if(pnlBrowse.tBrowse.cmdEmpty()) {
                    pnlBrowse.tBrowse.cmdAdd("Cwd",textField.getText(),null);
                }
            }
            this.dispose();
        } else if (evt.getSource() == btnCancel) {
            setVisible(false);
            textField.setText("");
            this.dispose();
        }
    }
    
}
