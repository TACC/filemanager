/* 
 * Created on Aug 5, 2008
 * 
 * Developed by: Rion Dooley - dooley [at] tacc [dot] utexas [dot] edu
 * 				 TACC, Texas Advanced Computing Center
 * 
 * https://www.tacc.utexas.edu/
 */

package org.teragrid.portal.filebrowser.applet.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;

import org.apache.axis.utils.StringUtils;
import org.irods.jargon.core.pub.domain.AvuData;
import org.irods.jargon.core.query.MetaDataAndDomainData;
import org.teragrid.portal.filebrowser.applet.AppMain;
import org.teragrid.portal.filebrowser.applet.util.LogManager;

/**
 * Input dialog box for metadata.
 * 
 * @author Rion Dooley < dooley [at] tacc [dot] utexas [dot] edu >
 *
 */
@SuppressWarnings("serial")
public class DlgMetaData extends DlgEscape implements KeyListener, ActionListener {

//    private AutoCompleter fac = null;
    private JTextField txtAttribute = null;
    private JTextField txtValue = null;
    private JTextField txtUnit = null;
    private JButton btnOk = null;
    private JButton btnCancel = null;
    private PnlIrodsMetadata pnlMetadata;
    private Dimension defaultLabelDimension = new Dimension(350, 15);
    private Dimension defaultTextDimension = new Dimension(350, 30);
    
    class EnterAction extends AbstractAction {
    	
    	DlgMetaData parent;
    	
    	public EnterAction(DlgMetaData parent) {
    		super();
    		this.parent = parent;
    	}
    	
        public void actionPerformed(ActionEvent e){ 
        	LogManager.debug("Enter pressed");
        	parent.btnOk.doClick(); 
        } 
    }; 
    
    public DlgMetaData(final PnlIrodsMetadata pnlMetadata) {
    	this(pnlMetadata, null);
    }
    
    public DlgMetaData(final PnlIrodsMetadata pnlIrodsMeta, MetaDataAndDomainData metadata) {
    	super((Frame)null, true);
    	setVisible(false);
    	
        this.pnlMetadata = pnlIrodsMeta;
        
        btnOk = new JButton("Save");
        btnCancel = new JButton("Cancel");
        
        txtAttribute = new JTextField();
        txtAttribute.addKeyListener(this);

        txtValue = new JTextField();
        txtValue.addKeyListener(this);
        
        txtUnit = new JTextField();
        txtUnit.addKeyListener(this);

        if (metadata != null) {
        	txtAttribute.setText(metadata.getAvuAttribute());
        	txtValue.setText(metadata.getAvuValue());
        	txtUnit.setText(metadata.getAvuUnit());
        	setTitle("Edit Metadata Entry");
        	btnOk.setToolTipText("Click to update this entry");
        } else {
        	setTitle("Add Metadata Entry");
        	btnOk.setToolTipText("Click to add this entry");
        }
        
        init();
        
        
        locateDialog(pnlIrodsMeta.getRootPane());
        setVisible(true);
        this.requestFocus();
        txtAttribute.requestFocus();
    }
    
    private void init() 
    {       
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10,10,0,0));
        mainPanel.addKeyListener(this);
        mainPanel.setPreferredSize(new Dimension(380,220));
        
        JLabel lblAttribute = new JLabel("Name:");
        lblAttribute.setLabelFor(txtAttribute);
        lblAttribute.setPreferredSize(defaultLabelDimension);       
        txtAttribute.setPreferredSize(defaultTextDimension);
        mainPanel.add(lblAttribute);
        mainPanel.add(txtAttribute);
        
        JLabel lblValue = new JLabel("Value:");
        lblValue.setLabelFor(txtValue);
        lblValue.setPreferredSize(defaultLabelDimension);       
        txtValue.setPreferredSize(defaultTextDimension);
        mainPanel.add(lblValue);
        mainPanel.add(txtValue);
        
        JLabel lblUnit = new JLabel("<html><body>Units <em>(optional)</em>:</body></html>");
        lblUnit.setLabelFor(txtUnit);
        lblUnit.setPreferredSize(defaultLabelDimension);       
        txtUnit.setPreferredSize(defaultTextDimension);
        mainPanel.add(lblUnit);
        mainPanel.add(txtUnit);
        
        mainPanel.add(createButtonPanel());
  
        setContentPane(mainPanel);
        setResizable(false);
        pack();
        
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }
    
    private JPanel createLabelPanel() {
    	
    	JPanel buttonInterior = new JPanel();
    	buttonInterior.setLayout(new GridLayout(1,2,5,0));
    	buttonInterior.add(new JLabel("Enter metadata values below: "));
    	
    	JPanel labelPanel = new JPanel();
    	labelPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
    	labelPanel.add(buttonInterior);
    	labelPanel.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
    	
        return labelPanel;
    }
    
    public AvuData getAvuData() {
    	if (StringUtils.isEmpty(txtAttribute.getText())) {
    		return null;
    	}
    	if (StringUtils.isEmpty(txtValue.getText())) {
    		return null;
    	}
    	if (StringUtils.isEmpty(txtUnit.getText())) {
    		txtUnit.setText("");
    	}
    	
    	try {
    		return new AvuData(txtAttribute.getText(), txtValue.getText(), txtUnit.getText());
    	} catch (Exception e) {
    		return null;
    	}
       
    }
    
    
    private Component createButtonPanel() 
    {   
        btnOk = new JButton("Save");
        btnOk.setVerticalTextPosition(AbstractButton.CENTER);
        btnOk.setHorizontalTextPosition(AbstractButton.RIGHT);
        btnOk.addKeyListener(this);
        btnOk.addActionListener(this);

        btnCancel.setVerticalTextPosition(AbstractButton.CENTER);
        btnCancel.setHorizontalTextPosition(AbstractButton.CENTER);
        btnCancel.setToolTipText("Click to close this window");
        btnCancel.addActionListener(this);
        btnCancel.addKeyListener(this); 
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.setPreferredSize(defaultTextDimension);
        //buttonPanel.setLayout(new GridLayout(1,2,5,0));
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(btnCancel);
        buttonPanel.add(btnOk);
        
//        JPanel buttonPanel = new JPanel();
//        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
//        buttonPanel.add(buttonInterior);
//        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
//        
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
        	txtAttribute.setText("");
        	txtValue.setText("");
        	txtUnit.setText("");
        	this.dispose();
        } else if (key == KeyEvent.VK_ENTER) {
        	btnOk.doClick();
        }
    }

    public void actionPerformed(ActionEvent evt) {
        if (evt.getSource() == btnOk) {
        	
        	if (StringUtils.isEmpty(txtAttribute.getText())) {
        		JOptionPane.showConfirmDialog(pnlMetadata, "Attribute name is required", "Metadata Input Error", JOptionPane.ERROR_MESSAGE);
        		txtAttribute.requestFocus();
        		return;
        	}
        	
        	if (StringUtils.isEmpty(txtValue.getText())) {
        		txtValue.setText("");
        	}
        	if (StringUtils.isEmpty(txtUnit.getText())) {
        		txtUnit.setText("");
        	}
        	
            this.setVisible(false);
           
        } else if (evt.getSource() == btnCancel) {
            setVisible(false);
            txtAttribute.setText("");
            txtValue.setText("");
            txtUnit.setText("");
            this.dispose();
        }
    }
    
}
