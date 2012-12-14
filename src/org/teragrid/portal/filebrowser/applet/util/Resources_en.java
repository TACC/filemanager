/* Portions of this file Copyright 2004-2007 Shanghai Jiaotong University
 * 
 * This file or a portion of this file is licensed under the
 * terms of the Globus Toolkit Public License, found at
 * http://www.globus.org/toolkit/legal/4.0/
 * If you redistribute this file, with or without
 * modifications, you must include this notice in the file.
 */

package org.teragrid.portal.filebrowser.applet.util;

import java.util.ListResourceBundle;

public class Resources_en extends ListResourceBundle {

	/* (non-Javadoc)
	 * @see java.util.ListResourceBundle#getContents()
	 */
	//@Override
	protected Object[][] getContents() {
		// TODO Auto-generated method stub
		return contents;
	}
	
	
	
	static final Object[][] contents = {
		//	public
		{ResourceName.KEY_DISPLAY_OK, "OK"},
		{ResourceName.KEY_DISPLAY_CLOSE, "Close"},
		{ResourceName.KEY_DISPLAY_REMOVE, "Remove"},
		{ResourceName.KEY_DISPLAY_CANCEL, "Cancel"},
		{ResourceName.KEY_DISPLAY_LOGIN, "Login"},
		{ResourceName.KEY_DISPLAY_ERROR, "Error"},
		{ResourceName.KEY_DISPLAY_ADD, "Add"},
		{ResourceName.KEY_DISPLAY_EDIT, "Edit"},
		{ResourceName.KEY_DISPLAY_DELETE, "Delete"},
		{ResourceName.KEY_DISPLAY_CONNECT, "Connect"},
		{ResourceName.KEY_DISPLAY_WARNING, "Warning"},
		{ResourceName.KEY_DISPLAY_PROPERTY, "Property"},
		{ResourceName.KEY_DISPLAY_SELECT, "Select"},
		{ResourceName.KEY_DISPLAY_STOP, "Stop"},
		{ResourceName.KEY_DISPLAY_DOWNLOAD, "Download Selected"},
		{ResourceName.KEY_DISPLAY_UPLOAD, "Upload Selected"},
		{ResourceName.KEY_DISPLAY_NEWDIR, "New Directory"},
		{ResourceName.KEY_DISPLAY_DELSEL, "Delete Selected"},
		{ResourceName.KEY_DISPLAY_RENAME, "Rename"},
		{ResourceName.KEY_ERROR_CONFIGFILE, "Configuration file could not be found or in error format!"},
		{ResourceName.KEY_DISPLAY_CONFIGFILEERROR, "Configuration Error"},
		{ResourceName.KEY_DISPLAY_OPEN, "Open"},
        {ResourceName.KEY_DISPLAY_INFO, "Get Info"},
		
		//sggc.AppMain
		{ResourceName.KEY_WARN_APPMAIN_ENVVARNOTFOUND, "Environment Variable 'GLOBUS_LOCATION' not set!"},
		{ResourceName.KEY_ERROR_APPMAIN_CONF, "Configuration file could not be found or in error format: "},
		{ResourceName.KEY_DISPLAY_APPMAIN_LARGEFIRST, "Large File First"},
		{ResourceName.KEY_DISPLAY_APPMAIN_SMALLFIRST, "Small File First"},
		{ResourceName.KEY_DISPLAY_APPMAIN_FIFO, "FIFO"},
		
		//sggc.ConfigOperation
		{ResourceName.KEY_ERROR_CONFIGOPERATION_WRITEFILE, "Failed to write configure file."},	
		{ResourceName.KEY_ERROR_CONFIGOPERATION_LOADCONF, "Loading config error: "},			
		
		//sggc.replica.DisplayAreaPanel
		{ResourceName.KEY_MSG_SUCCEED, "Succeed!"},

		//sggc.replica.DlgDisplay
		{ResourceName.KEY_DISPLAY_DLGDISPLAY, "DlgDisplay"}, 
		{ResourceName.KEY_DISPLAY_DLGDISPLAY_JLABEL1, "Using n connection to transfer a file:"},
		{ResourceName.KEY_DISPLAY_DLGDISPLAY_TOOLTIPS, "Transfer a file from {0} server(s):"},
		{ResourceName.KEY_DISPLAY_DLGDISPLAY_TITLE, "Multi-replica Transfer"}, 

		//sggc.replica.DlgMultiLoc
		{ResourceName.KEY_DISPLAY_DLGMULTILOC_LBLLOCTIP, "Location:"},
		{ResourceName.KEY_DISPLAY_DLGMULTILOC_TITLE, "Multi-replica transfer to: {0}"},
		{ResourceName.KEY_DISPLAY_DLGMULTILOC, "DlgMultiLoc"},
		{ResourceName.KEY_DISPLAY_DLGMULTILOC_JLABEL1, "Select Server:"},
		{ResourceName.KEY_DISPLAY_DLGMULTILOC_BTNCONNECT, "Connect"},
		{ResourceName.KEY_DISPLAY_DLGMULTILOC_JLABEL2, "The Selected Files:"},
		{ResourceName.KEY_ERROR_DLGMULTILOC_TOOMANYSELECT, "You should select one and only one file!"},
		{ResourceName.KEY_ERROR_DLGMULTILOC_NOTSELECTFILE, "You can only select a file."},
		{ResourceName.KEY_ERROR_DLGMULTILOC_NOITEMSELECT, "No Item selected!"},
		{ResourceName.KEY_ERROR_DLGMULTILOC_SIZENOTSAME, "The size of selected files don't have the same size!"},

		//sggc.replica.MultiThreadTransProxy
		{ResourceName.KEY_DISPLAY_MULTITHREADTRANSPROXY_TOOLTIPS, "Transfer a file using {0} Thread:"},
		{ResourceName.KEY_DISPLAY_MULTITHREADTRANSPROXY_DLGDISPLAY, "MultiThreadTransfer"},

		//sggc.transfer.BatchTransfer
		{ResourceName.KEY_ERROR_BATCHTRANSFER_CREATEDIR, "Cannot create directory "},	
		
		//sggc.transfer.FileSys
		{ResourceName.KEY_EXCEPTION_FILESYS_PERMISSIONDENIED, "Pemission denied."},		
		{ResourceName.KEY_EXCEPTION_FILESYS_DIRACCESSERROR, "Diretory not exists or access denied!"},
		{ResourceName.KEY_EXCEPTION_FILESYS_FILENOTEXIST, "File not exists or access denied!"},
		{ResourceName.KEY_EXCEPTION_FILESYS_FILEALREADYEXIST, "File already exists or access denied!"},
		{ResourceName.KEY_EXCEPTION_FILESYS_DIRALREADYEXIST, "File already exists or access denied!"},
		
		//sggc.transfer.FileTransferTask
		{ResourceName.KEY_EXCEPTION_FILETRANSFERTASK_INVALIDSTATUS, "Invalid status: {0}"},

		//sggc.transfer.FTPSettings
		{ResourceName.KEY_ERROR_FTPSETTINGS_INVALIDIP, "Invalid IP Address!"},

		//sggc.transfer.IOStream
		{ResourceName.KEY_EXCEPTION_IOSTREAM_CLOSE, "Close failed."},
		{ResourceName.KEY_EXCEPTION_IOSTREAM_FILENOTFOUND, "File {0} not found on the server."},
		{ResourceName.KEY_EXCEPTION_IOSTREAM_GETERROR, "Failed to retrieve file from server. Server returned error: {0} + {1} + )"},
		{ResourceName.KEY_EXCEPTION_IOSTREAM_INVALIDGETREPLY, "Failed to retrieve the file.  Invalid reply received."},
		{ResourceName.KEY_EXCEPTION_IOSTREAM_GASSPUTFAILED, "Gass PUT failed: {0}"},
		{ResourceName.KEY_EXCEPTION_IOSTREAM_GASSCLOSEFAILED, "Gass close failed."},

		//sggc.transfer.LocalControlChannel
		{ResourceName.KEY_EXCEPTION_LOCALCONTROLCHANNEL_WAITINTERRUPT, "Interrupted while waiting."},

		//sggc.transfer.UrlCopy
		{ResourceName.KEY_EXCEPTION_URLCOPY_SOURCENOTSPECIFIED, "Source site is not specified"},
		{ResourceName.KEY_EXCEPTION_URLCOPY_DESTNOTSPECIFIED, "Destination site is not specified"},
		{ResourceName.KEY_EXCEPTION_URLCOPY_URLTRANSFERFAILED, "UrlCopy transfer failed."},
		{ResourceName.KEY_EXCEPTION_URLCOPY_TRANSFERABORT, "Transfer Aborted"},

		//sggc.ui.DlgOption
		{ResourceName.KEY_DISPLAY_DLGOPTION_TITLE, "Option"},
		{ResourceName.KEY_DISPLAY_DLGOPTION_LBLLOG_TITLE, "Save Log for Transfer:"},
        {ResourceName.KEY_DISPLAY_DLGOPTION_LBLNOTIFICATION_TITLE, "Default Notification:"},
		{ResourceName.KEY_DISPLAY_DLGOPTION_BTNLOG_TITLE, "Browse..."},
		{ResourceName.KEY_DISPLAY_DLGOPTION_LBLCONNMAX_TITLE, "Max Connections:"},
		{ResourceName.KEY_DISPLAY_DLGOPTION_LBLCONNRETRY_TITLE, "Retry Attempts:"},
		{ResourceName.KEY_DISPLAY_DLGOPTION_LBLCONNDELAY_TITLE, "Delay Between Retries(s):"},
		{ResourceName.KEY_DISPLAY_DLGOPTION_LBLCONNKEEP_TITLE, "Keep Alive Time Interval:"},
		{ResourceName.KEY_DISPLAY_DLGOPTION_LBLCONNPARA_TITLE, "Default Parallelism:"},
		{ResourceName.KEY_DISPLAY_DLGOPTION_LBLBUFSIZE_TITLE, "Buffer Size(KB):"},
		{ResourceName.KEY_DISPLAY_DLGOPTION_LBLCONNDATA_TITLE, "Data Connection Type:"},
		{ResourceName.KEY_DISPLAY_DLGOPTION_LBLCERT_TITLE, "Use Certificate:"},
		{ResourceName.KEY_DISPLAY_DLGOPTION_BTNCERT_TITLE, "Browse..."},
		{ResourceName.KEY_DISPLAY_DLGOPTION_CHKSHOWHIDDEN_TITLE, "Show Hidden Files"},
		{ResourceName.KEY_DISPLAY_DLGOPTION_LBLRFTFACTORY_TITLE, "Services:"},
		{ResourceName.KEY_DISPLAY_DLGOPTION_LBLRFT_TITLE, "RFT Services:"},
		{ResourceName.KEY_DISPLAY_DLGOPTION_LBLRFTSVC_TITLE, "Factories:"},
		{ResourceName.KEY_DISPLAY_DLGOPTION_JLABEL1_TITLE, "Service:"},
		{ResourceName.KEY_DISPLAY_DLGOPTION_JLABEL2_TITLE, "Factory:"},
		{ResourceName.KEY_DISPLAY_DLGOPTION_BTNDEFAULT_TITLE, ""},
		{ResourceName.KEY_DISPLAY_DLGOPTION_BTNABORT_TITLE, "Abort"},
		{ResourceName.KEY_DISPLAY_DLGOPTION_PNLGENERAL_TITLE, "General"},
		{ResourceName.KEY_DISPLAY_DLGOPTION_PNLCONNECT_TITLE, "Connection"},
		{ResourceName.KEY_DISPLAY_DLGOPTION_PNLSECURITY_TITLE, "Security"},
        {ResourceName.KEY_DISPLAY_DLGOPTION_PNLNOTIFICATION_TITLE, "Notification"},
        {ResourceName.KEY_DISPLAY_DLGOPTION_PNLRTF_TITLE, "RTF"},
		{ResourceName.KEY_DISPLAY_DLGOPTION_BTNCONFIRM_TITLE, "Confirm"},
		{ResourceName.KEY_DISPLAY_DLGOPTION_BTNMODIFY_TITLE, "Modify"},
		{ResourceName.KEY_DISPLAY_DLGOPTION_DELETE_CONFIRM, "Are you sure you want to delete selected items?"},
		{ResourceName.KEY_ERROR_DLGOPTION_CONFIRM, "Please input correct information!"},
		
		//sggc.ui.DlgPartial
		{ResourceName.KEY_DISPLAY_DLGPARTIAL_TITLE, "Partial Dialog"},
		{ResourceName.KEY_DISPLAY_DLGPARTIAL_JL_TITLE, "Partial Transfer over the Grid"},
		{ResourceName.KEY_DISPLAY_DLGPARTIAL_JL_INFO, "The total size of the File is "},
		{ResourceName.KEY_DISPLAY_DLGPARTIAL_JL_CHOOSE, "Please input the range you choose, like \'22-333,444-999\'."},

		//sggc.ui.DlgPassword
		{ResourceName.KEY_DISPLAY_DLGPASSWORD_TITLE, "Password Dialog"},
		{ResourceName.KEY_DISPLAY_DLGPASSWORD_LBLNAME_TITLE, "User Name:"},	
		{ResourceName.KEY_DISPLAY_DLGPASSWORD_LBLPWD_TITLE, "Password:"},
		{ResourceName.KEY_DISPLAY_DLGPASSWORD_CHKANONY_TITLE, "Anonymous"},	
		{ResourceName.KEY_DISPLAY_DLGPASSWORD_CHKSAVE_TITLE, "Save Password"},
		{ResourceName.KEY_ERROR_DLGPASSWORD_EMPTYUSERNAME, "Username shouldn't be empty!"},		

		//sggc.ui.DlgResumeTrans
		{ResourceName.KEY_DISPLAY_DLGRESUMETRANS_TITLE, "Confirm"},
		{ResourceName.KEY_DISPLAY_DLGRESUMETRANS_TITLEDBORDER1, "Action"},
		{ResourceName.KEY_DISPLAY_DLGRESUMETRANS_LBLMAIN, "The File \"{0}\" has already existed!"},
		{ResourceName.KEY_DISPLAY_DLGRESUMETRANS_RDIRESUME, "Resume"},
		{ResourceName.KEY_DISPLAY_DLGRESUMETRANS_RDIOVERWRITE, "Overwrite"},
		{ResourceName.KEY_DISPLAY_DLGRESUMETRANS_RDIRENAME, "Rename"},
		{ResourceName.KEY_DISPLAY_DLGRESUMETRANS_RDISKIP, "Skip"},
		{ResourceName.KEY_DISPLAY_DLGRESUMETRANS_CDREMEMBER, "Remember my choice this time"},
		{ResourceName.KEY_DISPLAY_DLGRESUMETRANS_JLABEL1, "Source File Size:"},
		{ResourceName.KEY_DISPLAY_DLGRESUMETRANS_JLABEL3, "Modified Time:"},
		{ResourceName.KEY_DISPLAY_DLGRESUMETRANS_JLABEL5, "Destination File Size:"},
		{ResourceName.KEY_DISPLAY_DLGRESUMETRANS_JLABEL7, "Modified Time:"},

		//sggc.ui.DlgSite
		{ResourceName.KEY_DISPLAY_DLGSITE_LBLNAME, "Name:"},
		{ResourceName.KEY_DISPLAY_DLGSITE_LBLHOST, "HostName:"},
		{ResourceName.KEY_DISPLAY_DLGSITE_LBLPORT, "Port:"},
		{ResourceName.KEY_DISPLAY_DLGSITE_LBLTYPE, "Type:"},
		{ResourceName.KEY_DISPLAY_DLGSITE_LBLCONNRETRY, "Connection retries times:"},
		{ResourceName.KEY_DISPLAY_DLGSITE_LBLCONNDELAY, "Delay between retries(s):"},
		{ResourceName.KEY_DISPLAY_DLGSITE_LBLCONNPARA, "Default Parallelism:"},
		{ResourceName.KEY_DISPLAY_DLGSITE_LBLMAXCONN, "Max connections:"},
		{ResourceName.KEY_DISPLAY_DLGSITE_LBLCONNDATA, "Data connection type:"},
		{ResourceName.KEY_DISPLAY_DLGSITE_LBLLOGINMODE, "GridFTP Login Mode:"},
		{ResourceName.KEY_DISPLAY_DLGSITE_LBLUSER, "Username:"},
		{ResourceName.KEY_DISPLAY_DLGSITE_LBLPWD, "Password:"},
		{ResourceName.KEY_EXCEPTION_DLGSITE_NAMEHOSTPWDEMPTY, "Name or Host or Port can NOT be empty!"},
		{ResourceName.KEY_ERROR_DLGSITE_NAMEALREADYEXIST, "The site {0} has already been in the list, Please rename the site."},
		{ResourceName.KEY_DISPLAY_DLGSITE_PARAERROR, "Parameters Error"},

		//sggc.ui.DlgThirdPartyTrans
		{ResourceName.KEY_DISPLAY_DLGTHIRDPARTYTRANS_LBLRFT, "RFT Service:"},
		{ResourceName.KEY_DISPLAY_DLGTHIRDPARTYTRANS_RDIDIRECT, "Directly transfer"},
		{ResourceName.KEY_DISPLAY_DLGTHIRDPARTYTRANS_RDIRFT, "Use RFT Services"},
		{ResourceName.KEY_DISPLAY_DLGTHIRDPARTYTRANS_LBLTITLE, "Please choose a 3rd party transfer method:"},
		{ResourceName.KEY_DISPLAY_DLGTHIRDPARTYTRANS_LBLFACTORY, "Factory:"},
		
		//sggc.ui.DlgView
		{ResourceName.KEY_DISPLAY_DLGVIEW_LBLMAP, "Please Select a map: "},
		{ResourceName.KEY_DISPLAY_DLGVIEW_BTNLABEL, "Label selected FTP site"},
		{ResourceName.KEY_DISPLAY_DLGVIEW_BTNMODIFY, "Modify selected FTP site"},

		//sggc.ui.DrawState
		{ResourceName.KEY_DISPLAY_DRAWSTATE_WAITING, "Waiting ... "},
		{ResourceName.KEY_DISPLAY_DRAWSTATE_TRANSFERINFO, "Transferring: {0}\r\n"},
		{ResourceName.KEY_DISPLAY_DRAWSTATE_TIMEUSEDINFO, "Time Used: {0}\r\n"},
		{ResourceName.KEY_DISPLAY_DRAWSTATE_TIMELEFTINFO, "Time Left: {0}\r\n"},

		//sggc.ui.FrmMain
		{ResourceName.KEY_DISPLAY_FRMMAIN_TITLE, "TeraGrid Distributed File Browser"},
		{ResourceName.KEY_DISPLAY_FRMMAIN_MNULISTADD, "Add Site"},
		{ResourceName.KEY_DISPLAY_FRMMAIN_MNULISTOPEN , "Open"},
		{ResourceName.KEY_DISPLAY_FRMMAIN_MNULISTOPENDIALOG, "Open New Window"},
		{ResourceName.KEY_DISPLAY_FRMMAIN_MNULISTOPENDESKTOP, "Open New Tab"},
		{ResourceName.KEY_DISPLAY_FRMMAIN_MNUREFRESHSITES, "Refresh Sites"},
		{ResourceName.KEY_DISPLAY_FRMMAIN_MNUBRONORMAL, "Normalize"},
		{ResourceName.KEY_DISPLAY_FRMMAIN_MNUQUEDELSEL, "Remove Selected"},
		{ResourceName.KEY_DISPLAY_FRMMAIN_MNUQUEDELDONE, "Remove Finished"},
		{ResourceName.KEY_DISPLAY_FRMMAIN_MNUQUEDELALL, "Remove All"},
        {ResourceName.KEY_DISPLAY_FRMMAIN_MNUQUENOTIFY, "Notify"},
        {ResourceName.KEY_DISPLAY_FRMMAIN_MNUQUENOTIFYEMAIL, "Send Email"},
        {ResourceName.KEY_DISPLAY_FRMMAIN_MNUQUENOTIFYIM, "Send IM"},
        {ResourceName.KEY_DISPLAY_FRMMAIN_MNUQUENOTIFYSMS, "Send SMS"},
        {ResourceName.KEY_DISPLAY_FRMMAIN_MNUQUENOTIFYCLEAR, "Clear All"},
        
		{ResourceName.KEY_DISPLAY_FRMMAIN_MNUQUETRANS, "Transfer Selected"},
		{ResourceName.KEY_DISPLAY_FRMMAIN_MNUQUETRANSALL, "Transfer Selected"},
        {ResourceName.KEY_DISPLAY_FRMMAIN_MNUQUEREFRESH, "Refresh History"},
		{ResourceName.KEY_DISPLAY_FRMMAIN_MNUFile, "File"},
		{ResourceName.KEY_DISPLAY_FRMMAIN_MNUEXIT, "Exit"},
		{ResourceName.KEY_DISPLAY_FRMMAIN_MNUEXTERNAL, "New Window"},
		{ResourceName.KEY_DISPLAY_FRMMAIN_MNUEDIT, "Edit"},
		{ResourceName.KEY_DISPLAY_FRMMAIN_MNUUNDO, "Undo"},
		{ResourceName.KEY_DISPLAY_FRMMAIN_MNUREDO, "Redo"},
		{ResourceName.KEY_DISPLAY_FRMMAIN_MNUCUT, "Cut"},
		{ResourceName.KEY_DISPLAY_FRMMAIN_MNUCOPY, "Copy"},
		{ResourceName.KEY_DISPLAY_FRMMAIN_MNUPASTE, "Paste"},
		{ResourceName.KEY_DISPLAY_FRMMAIN_MNUVIEW, "Operation"},
		{ResourceName.KEY_DISPLAY_FRMMAIN_MNUTOOLS, "Tools"},
		{ResourceName.KEY_DISPLAY_FRMMAIN_MNUSITEVIEW, "Site View"},
		{ResourceName.KEY_DISPLAY_FRMMAIN_MNUMULTIREPLI, "Multi-replica Transfer..."},
		{ResourceName.KEY_DISPLAY_FRMMAIN_MNUPROXY, "Grid Proxy Init..."},
		{ResourceName.KEY_DISPLAY_FRMMAIN_MNUOPTIONS, "Options..."},
		{ResourceName.KEY_DISPLAY_FRMMAIN_MNUSITE, "Site"},
		{ResourceName.KEY_DISPLAY_FRMMAIN_MNUADDSITE, "Add Site"},
		{ResourceName.KEY_DISPLAY_FRMMAIN_MNUDELSITE, "Delete site"},
		{ResourceName.KEY_DISPLAY_FRMMAIN_MNUMODIFYSITE, "Modify site"},
		{ResourceName.KEY_DISPLAY_FRMMAIN_MNUHELP, "Help"},
		{ResourceName.KEY_DISPLAY_FRMMAIN_MNUHELPTOPIC, "Help topics"},
		{ResourceName.KEY_DISPLAY_FRMMAIN_MNUABOUT, "About"},
		{ResourceName.KEY_DISPLAY_FRMMAIN_BTNSITEADD, "Add a server"},
		{ResourceName.KEY_DISPLAY_FRMMAIN_BTNSITEDISCONN, "Disconnect from server"},
        {ResourceName.KEY_DISPLAY_FRMMAIN_BTNSITEDELETE, "Delete site"},
		{ResourceName.KEY_DISPLAY_FRMMAIN_BTNSITEVIEW, "Site View"},
		{ResourceName.KEY_DISPLAY_FRMMAIN_LBLIP, "HostName: "},
		{ResourceName.KEY_DISPLAY_FRMMAIN_LBLPORT,"   Port: "},
		{ResourceName.KEY_DISPLAY_FRMMAIN_LBLTYPE, "  Type: "},
		{ResourceName.KEY_DISPLAY_FRMMAIN_BTNCONNECT, "Connect  "},
		{ResourceName.KEY_DISPLAY_FRMMAIN_LOCAL, "Local"},
		{ResourceName.KEY_DISPLAY_FRMMAIN_SITELIST, "Site List"},
		{ResourceName.KEY_DISPLAY_FRMMAIN_DLGVIEW_TITLE, "Sites & Transfer View"},
		{ResourceName.KEY_DISPLAY_FRMMAIN_DLGOPTION_TITLE, "Options"},
		{ResourceName.KEY_DISPLAY_FRMMAIN_DLGMULTILOC_TITLE, "MultiLoc"},
		{ResourceName.KEY_DISPLAY_FRMMAIN_DLGPASSWORD_TITLE, "Login"},
        {ResourceName.KEY_MSG_FRMMAIN_ADDSITEEMPTYLIST, "All available TeraGrid sites are currently in your VO listing"},
        {ResourceName.KEY_MSG_FRMMAIN_ADDSITEEMPTYLIST_TITLE, "Add Site"},
        {ResourceName.KEY_MSG_FRMMAIN_DELETESITE, "Delete this site?"},
		{ResourceName.KEY_DISPLAY_FRMMAIN_DELETESITE_TITLE, "Delete Confirm"},
		{ResourceName.KEY_EXCEPTION_FRMMAIN_DELETESITEFAIL, "Delete site failed: {0}"},
		{ResourceName.KEY_DISPLAY_FRMMAIN_SITEROOT, "Resources"},
		{ResourceName.KEY_EXCEPTION_FRMMAIN_REFRESHSITESFAIL, "Refresh sites failed. Available site list remains unchanged."},
        
		//sggc.ui.QueueTableModel
		{ResourceName.KEY_DISPLAY_QUEUETABLEMODEL_ID, "ID"},
		{ResourceName.KEY_DISPLAY_QUEUETABLEMODEL_ITEMNAME, "Item Name"},
		{ResourceName.KEY_DISPLAY_QUEUETABLEMODEL_SOURCE, "Source"},
		{ResourceName.KEY_DISPLAY_QUEUETABLEMODEL_DEST, "Destination"},
		{ResourceName.KEY_DISPLAY_QUEUETABLEMODEL_PROGRESS, "Progress"},
		{ResourceName.KEY_DISPLAY_QUEUETABLEMODEL_PARA, "Parallelism"},
		{ResourceName.KEY_DISPLAY_QUEUETABLEMODEL_STRIPE, "Stripe"},
		{ResourceName.KEY_DISPLAY_QUEUETABLEMODEL_SPEED, "Speed"},
		{ResourceName.KEY_DISPLAY_QUEUETABLEMODEL_TOTALNAME, "Total Time"},
		{ResourceName.KEY_DISPLAY_QUEUETABLEMODEL_LEFTTIME, "Left Time"},
		{ResourceName.KEY_DISPLAY_QUEUETABLEMODEL_STATUS, "Status"},
		
		//sggc.ui.ListModel
		{ResourceName.KEY_DISPLAY_LISTMODEL_COLUMN_NAME, "Name"},
		{ResourceName.KEY_DISPLAY_LISTMODEL_COLUMN_SIZE, "Size"},	
		{ResourceName.KEY_DISPLAY_LISTMODEL_COLUMN_TYPE, "Type"},	
		{ResourceName.KEY_DISPLAY_LISTMODEL_COLUMN_MODIFIED, "Modified"},	
		{ResourceName.KEY_DISPLAY_LISTMODEL_COLUMN_ATTRIBUTES, "Attributes"},	
		{ResourceName.KEY_DISPLAY_LISTMODEL_COLUMN_DESCRIPTION, "Description"},	
		{ResourceName.KEY_DISPLAY_LISTMODEL_COLUMN_OWNER, "Owner"},	
		{ResourceName.KEY_DISPLAY_LISTMODEL_TYPE_DEVICE, "Device"},	
		{ResourceName.KEY_DISPLAY_LISTMODEL_TYPE_FOLDER, "Folder"},	
		{ResourceName.KEY_DISPLAY_LISTMODEL_TYPE_FILE, "File"},	
		{ResourceName.KEY_DISPLAY_LISTMODEL_TYPE_LINK, "Link"},	
		{ResourceName.KEY_DISPLAY_LISTMODEL_TYPE_UNKNOWN, "Unknown"},	
		
		//sggc.ui.FTPThread
		{ResourceName.KEY_MSG_FTPTHREAD_CONNECT, "Try to connect... {0} times remained."},
		{ResourceName.KEY_MSG_FTPTHREAD_CONNECTCLOSE, "Connection closed."},		
		{ResourceName.KEY_MSG_FTPTHREAD_FTPNOTIDLE, "The current session is transferring files. Please send the command later"},		

		//sggc.ui.MyFileChooser
		{ResourceName.KEY_DISPLAY_MYFILECHOOSER_WORD_DESC, "Word Files"},	
		{ResourceName.KEY_DISPLAY_MYFILECHOOSER_TXT_DESC, "Text Files"},	
		{ResourceName.KEY_ERROR_MYFILECHOOSER_NOEXTENSION, "An extension name must be specified!"},
		{ResourceName.KEY_ERROR_MYFILECHOOSER_FORMATERROR, "Only accept .txt or .doc file!"},
		{ResourceName.KEY_ERROR_MYFILECHOOSER_FILEALREADYEXIST, "The file already exists!\nOverwrite it or not?"},
		{ResourceName.KEY_DISPLAY_MYFILECHOOSER_INVALIDFILE, "Invalid File"},
		{ResourceName.KEY_DISPLAY_MYFILECHOOSER_FILEEXIST, "File Exists"},

		//sggc.ui.PnlBrowse
		{ResourceName.KEY_DISPLAY_PNLBROWSE_INPUTRAW, "Input Raw Command"},
		{ResourceName.KEY_DISPLAY_PNLBROWSE_GOUP, "Go Up"},
		{ResourceName.KEY_DISPLAY_PNLBROWSE_REFRESH, "Refresh"},
		{ResourceName.KEY_DISPLAY_PNLBROWSE_GOHOME, "Go Home"},
		{ResourceName.KEY_DISPLAY_PNLBROWSE_DISCONNECT, "Disconnect"},
        {ResourceName.KEY_DISPLAY_PNLBROWSE_DELETE, "Delete"},
		{ResourceName.KEY_DISPLAY_PNLBROWSE_MNUSELCOPY, "Copy the Selected Log to Clipboard"},
		{ResourceName.KEY_DISPLAY_PNLBROWSE_MNUCOPY, "Copy the Log to Clipboard"},
		{ResourceName.KEY_DISPLAY_PNLBROWSE_MNUSAVE, "Save the Log to File"},
		{ResourceName.KEY_DISPLAY_PNLBROWSE_MNUCLEAR, "Clear the Log"},
        {ResourceName.KEY_DISPLAY_PNLBROWSE_MNUGO, "Go to Location"},
        {ResourceName.KEY_DISPLAY_PNLBROWSE_BACK, "Go Backwards to Previous Folder"},
        {ResourceName.KEY_DISPLAY_PNLBROWSE_FWD, "Go Forward to Previous Folder"},
        {ResourceName.KEY_DISPLAY_PNLBROWSE_DOWNLOAD, "Download Selected Folder(s)/File(s)"},
        {ResourceName.KEY_DISPLAY_PNLBROWSE_LIST, "List View"},
        {ResourceName.KEY_DISPLAY_PNLBROWSE_DETAIL, "Detail View"},
        {ResourceName.KEY_DISPLAY_PNLBROWSE_ICON, "Icon View"},
		{ResourceName.KEY_MSG_PNLBROWSE_CLOSE, "Connected, close anyway?"},
		{ResourceName.KEY_DISPLAY_PNLBROWSE_CLOSEDLG_TITLE, "Close Confirm"},
		{ResourceName.KEY_MSG_PNLBROWSE_NEWDIR, "Please input new directory name: "},
		{ResourceName.KEY_DISPLAY_PNLBROWSE_NEWDIRDLG_TITLE, "Add Directory"},
		{ResourceName.KEY_MSG_PNLBROWSE_SAVELOG, "You've saved the log to file: "},
		{ResourceName.KEY_EXCEPTION_PNLBROWSE_EXEC, "Error executing command "},
		{ResourceName.KEY_MSG_PNLBROWSE_RENAME, "Please input new file name: "},
		{ResourceName.KEY_DISPLAY_PNLBROWSE_RENAME_TITLE, "Rename File"},
		{ResourceName.KEY_MSG_PNLBROWSE_DELETE, "Are you sure to delete selected items?"},
		
		//sggc.ui.TransferProxy
		{ResourceName.KEY_DISPLAY_TRANSFERPROXY_3PARTYDLGTITLE, "3rd Party Transfer"},
		{ResourceName.KEY_DISPLAY_TRANSFERPROXY_RESUMEDLGTITLE, "Confirm"},
		{ResourceName.KEY_DISPLAY_TRANSFERPROXY_NEWNAMEDLGTITLE, "Input Filename"},
		{ResourceName.KEY_DISPLAY_TRANSFERPROXY_NEWNAMEDLGPROMT, "File already exist. Please specify a new name: "},
		{ResourceName.KEY_ERROR_TRANSFERPROXY_INVALIDNAME, "Please input a valid file name!"},		

		//sggc.ui.TreeView
		{ResourceName.KEY_DISPLAY_TREEVIEW_RETRIEVINGDATA, "Retrieving data..."},	
		{ResourceName.KEY_ERROR_TREEVIEW_ERRORREADINGDIR, "Error reading directory {0}"},	
	};

}
