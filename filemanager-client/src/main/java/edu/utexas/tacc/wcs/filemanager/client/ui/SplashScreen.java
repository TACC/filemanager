/* 
 * Created on Aug 14, 2008
 * 
 * Developed by: Rion Dooley - dooley [at] tacc [dot] utexas [dot] edu
 * 				 TACC, Texas Advanced Computing Center
 * 
 * https://www.tacc.utexas.edu/
 */

package edu.utexas.tacc.wcs.filemanager.client.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import edu.utexas.tacc.wcs.filemanager.client.AppMain;

/**
 * Splash screen to display startup progress of File Manager. The progress bar
 * and message are updated in real time by the AppMain and LogManager
 * classes.  By default, it updates on every LogManager.info() call 
 * during startup.
 * 
 * @author Rion Dooley < dooley [at] tacc [dot] utexas [dot] edu >
 *
 */
@SuppressWarnings("serial")
public class SplashScreen extends JDialog {
    
    private static int SPLASH_TASK_COUNT = 200;
    
    private JPanel pnlProgress;
    private JPanel pnlTitle;
    private JPanel pnlMessage;
    
    private JTextArea txtMessage;
    
    private JLabel lblTask;
    
    protected int dykIndex = 0;
    protected String[] didYouKnow = {
    		"You can manage your IRODS metadata directy from " +
    		"the IRODS resource panel while browsing your data. " +
    		"Simply right click on a file or collection and select " +
    		"\"Show Metadata.\"",
    		"You can add resources outside of the XSEDE by " +
    		"clicking on the '+' button of the resource listing " +
    		"panel and entering the resource type, nickname, and " +
    		"hostname in the add resource dialog.",
            "You can copy entire directories between any XSEDE " +
            "resources on which you have accounts simply by " +
            "dragging the folder icon from one panel to the other.",
            "The File Manager supports moving data between XSEDE " +
            "resources, your local desktop, the IRODS and Amazon " + 
            " S3 service.",
            "You can subscribe to email notifications for long " +
            "running file transfers by right clicking on the file in the " +
            "history table and selecting Notification -> EMAIL.",
            "To quickly move to a different folder in your " +
            "current directory path, select the folder from the " +
            "path combo box or click on the folder link below " +
            "the directory listing.",
            "The File Manager provides hotkey support for each of the " +
            "shortcut buttons. Check the Help system for more " +
            "information.",
            "To copy any file to your local home directory, " +
            "simply double click on the file in the listing " +
            "panel.",
            "You can search for files and folders within " +
            "a resource by using the Search Panel. Seach " +
            "preferences such as maximum number of results " +
            "and maximum search depth are configurable via " +
            "the Preferences panel.",
            "You can bookmark any folder by right clicking " +
            "on the folder in the directory listing and selecting " +
            "'Bookmark Selected' from the popup menu. A listing of " +
            "all your bookmarks for a particular resource are " +
            "available via the Bookmark Panel. Just double click " +
            "a bookmark to open that folder directly.",
            "You can now maximize the size of your directory " +
            "listing windows by hiding the file transfer panel.",
            "The File Manager supports Amazon S3 access. To configure " +
            "the File Manager to use an Amazon S3 account, simply click " +
            "on the '+' button of the resource listing panel, " +
    		"select \"Amazon S3\" as the resource type, and enter " +
    		"your information.",
            "The File Manager supports IRODS access. To configure " +
            "the File Manager to use an IRODS resource, simply click " +
            "on the '+' button of the resource listing panel, " +
            "select \"IRODS\" as the resource type, and enter " +
            "your information.",
            "You can specify your preferred local download " +
            "directory in the Security panel of the Preferences " +
            "dialog. ",
            "You can get a public link to any file or folder in your " +
            "\"public\" folder in XSEDE $SHARE by right clicking " +
            "on that file or folder and selecting \"Get Shared URL\"",
            "Not enough space to view your files and transfer history? " +
            "Try opening another window. Select 'File' -> 'Open current " +
            "instance' from the top menu to open up your current sessions" +
            "in another window.",
            };
    private JProgressBar progressBar;
    
    /**
     * Default constructor. Displays splash screen with zero progress.
     */
    public SplashScreen(Frame frame) {

        super(frame,false);
        
        init(frame);
        
    }
    
    public void init(Frame frame) {
        getContentPane().removeAll();
        
        titleInit();
        getContentPane().add(pnlTitle, BorderLayout.NORTH);
        messageInit();
        getContentPane().add(pnlMessage, BorderLayout.CENTER);
        progressInit();
        getContentPane().add(pnlProgress, BorderLayout.SOUTH);
        
        getContentPane().getInsets().set(3,3,3,3);
        setPreferredSize(new Dimension(400,330));
        
        pack();
        
        Dimension screenSize = frame.getPreferredSize();
        Dimension labelSize = getPreferredSize();
        
        Point p = frame.getLocation();
        int x = p.x + ((screenSize.width) - (labelSize.width))/2;
        int y = p.y + ((screenSize.height) - (labelSize.height))/2;
        setLocationRelativeTo(frame);
        setBackground(Color.decode("#eeebea"));
        setVisible(true);
    }
    
    private void titleInit() {
        
        pnlTitle = new JPanel();
        pnlTitle.setPreferredSize(new Dimension(350,100));
        pnlTitle.setBorder(new EmptyBorder(0,10,0,10));
        
        JLabel lblLogo = new JLabel(AppMain.imgSplash);
        AppMain.imgSplash.getImage().getScaledInstance(350,117,0);
        lblLogo.setPreferredSize(new Dimension(350,100));
        
        JLabel lblTitle = new JLabel("<html>XSEDE File Manager</html>");
        lblTitle.setHorizontalTextPosition(SwingConstants.CENTER);

        
        pnlTitle.add(lblLogo,BorderLayout.NORTH);
        //pnlTitle.add(lblTitle,BorderLayout.SOUTH);
        
    }
    
    private void messageInit() {
        pnlMessage = new JPanel(new BorderLayout());
        pnlMessage.setPreferredSize(new Dimension(400,270));
        
        JLabel lblDYK = new JLabel("<html><p style=\"font-size: 20px;\"><b>DID YOU KNOW...</b></p></html>");
        lblDYK.setForeground(Color.decode("#333333"));
        lblDYK.setBackground(Color.decode("#F4F4F4"));
        lblDYK.setOpaque(true);
        lblDYK.setHorizontalTextPosition(SwingConstants.LEFT);
        lblDYK.setBorder(new EmptyBorder(0,25,10,3));
        Random generator = new Random();
        txtMessage = new JTextArea();
        txtMessage.setEditable(false);
        txtMessage.setBorder(new EmptyBorder(0,25,10,10));
        txtMessage.setBackground(Color.decode("#F4F4F4"));
        txtMessage.setForeground(Color.decode("#333333"));
        //txtMessage.setFont(lblDYK.getFont());
        txtMessage.setText(didYouKnow[generator.nextInt(didYouKnow.length)]);
        txtMessage.setLineWrap(true);
        txtMessage.setWrapStyleWord(true);
        txtMessage.addMouseListener(new MouseAdapter() {
            
            public void mouseClicked(MouseEvent arg0) {
                dykIndex++;
                
                if (dykIndex == didYouKnow.length) {
                    dykIndex = 0;
                } 
                
                txtMessage.setText(didYouKnow[dykIndex]);
//                init();
            }
            
        });
        
        pnlMessage.add(lblDYK, BorderLayout.PAGE_START);
        pnlMessage.add(txtMessage,BorderLayout.CENTER);
        pnlMessage.setBorder(BorderFactory.createCompoundBorder(
        		BorderFactory.createEmptyBorder(0, 30, 0, 30), 
        		BorderFactory.createCompoundBorder(
                		BorderFactory.createEmptyBorder(5, 0, 5, 0), 
                		new LineBorder(Color.decode("#e5e5e5"), 2, true))));
        
    }
    
    private void progressInit() {
    	UIManager.put("ProgressBar.selectionBackground",Color.decode("#ec6023"));
        UIManager.put("ProgressBar.selectionForeground",Color.WHITE);
        
        pnlProgress = new JPanel(new BorderLayout());
        
        lblTask = new JLabel("Loading...");
        lblTask.setHorizontalTextPosition(SwingConstants.LEFT);
        lblTask.setBorder(BorderFactory.createEmptyBorder());
        //lblTask.setForeground(Color.decode("#FFFFFF"));
        
        progressBar = new JProgressBar(0,SPLASH_TASK_COUNT);
        progressBar.setIndeterminate(false);
        
        pnlProgress.add(lblTask,BorderLayout.PAGE_START);
        pnlProgress.add(progressBar,BorderLayout.CENTER);
       
        pnlProgress.setBorder(new EmptyBorder(0,30,15,30));
    }

    public void update(int value, String message) {
        progressBar.setValue(value<0?progressBar.getValue() + 1: value);
        lblTask.setText(message ==null?lblTask.getText():message);
    }
    
    public void update(String message) {
        progressBar.setValue(progressBar.getValue() + 1);
        lblTask.setText(message ==null?lblTask.getText():message);
    }
    
    public void setMessage(String message) {
        txtMessage.setText(message);
    }
    
    public void reset() {
        progressBar.setValue(0);
        lblTask.setText("");
        txtMessage.setText("");
    }

    
    public static void main(String[] args) {
    	final JFrame frame = new JFrame();
    	
        SplashScreen ss = new SplashScreen(frame);
        
        int i=0;
        while (i<=10) {
            try { Thread.sleep(500);}catch (Exception e) {e.printStackTrace();}
            ss.update(i*(ss.progressBar.getMaximum()/10), "Update " + i);
            i++;
        }
    }
}
