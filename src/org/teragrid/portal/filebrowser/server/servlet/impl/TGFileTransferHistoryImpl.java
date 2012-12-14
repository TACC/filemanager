/* 
 * Created on Dec 13, 2007
 * 
 * Developed by: Rion Dooley - dooley [at] tacc [dot] utexas [dot] edu
 * 				 TACC, Texas Advanced Computing Center
 * 
 * https://www.tacc.utexas.edu/
 */

package org.teragrid.portal.filebrowser.server.servlet.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.teragrid.portal.filebrowser.applet.transfer.FileTransferTask;
import org.teragrid.portal.filebrowser.server.servlet.TGFileTransferHistory;
import org.teragrid.portal.filebrowser.server.servlet.exception.AuthenticationException;
import org.teragrid.portal.filebrowser.server.servlet.exception.PersistenceException;
import org.teragrid.portal.filebrowser.server.servlet.model.filetransfer.History;
import org.teragrid.portal.filebrowser.server.servlet.model.filetransfer.Transfer;
import org.teragrid.portal.filebrowser.server.servlet.model.notification.NotificationType;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.basic.DateConverter;
import com.thoughtworks.xstream.io.xml.DomDriver;


/**
 * Class to query and manage the user's file transfer history. Records
 * are persisted in a back end database.
 * 
 * @author Rion Dooley < dooley [at] tacc [dot] utexas [dot] edu >
 *
 */
public class TGFileTransferHistoryImpl implements TGFileTransferHistory {
	private static final Logger logger = Logger.getLogger(TGFileTransferHistoryImpl.class);
	
    private static XStream xstream = new XStream(new DomDriver());
    
    private static String defaultFormat = "MMM d, yyyy h:mm:ss a";
    
    private static String[] acceptableFormats = new String[] {
            "MM/dd/yyyy HH:mm:ss",
            "yyyy-MM-dd HH:mm:ss.S z",
            "yyyy-MM-dd HH:mm:ss.S a",
            "yyyy-MM-dd HH:mm:ssz",
            "yyyy-MM-dd HH:mm:ss z", // JDK 1.3 needs both prev versions
            "yyyy-MM-dd HH:mm:ssa" }; // backwards compatability
    
    {
        xstream.registerConverter(new DateConverter(defaultFormat, acceptableFormats));
    }
    
    public TGFileTransferHistoryImpl(){}
    
    /* (non-Javadoc)
     * @see org.teragrid.portal.filebrowser.server.servlet.TGFileTransferHistory#get(java.lang.String)
     */
    public String get(String dn) throws Exception {
        // retrieve the user's history in the paging manner requested.

        if (!isValid(dn))
            throw new AuthenticationException("Please supply a valid DN " + dn);
        
        logger.debug("Retrieving user history for " + dn);
        
        History history = new History(dn);
        
        List<FileTransferTask> taskList = history.get();
        
        logger.debug("Retrieved " + taskList.size() + 
                    " transfer records for " + history.getUser().getUsername());
            
        return xstream.toXML(taskList);
    }
    
    /* (non-Javadoc)
     * @see org.teragrid.portal.filebrowser.server.servlet.TGFileTransferHistory#get(java.lang.String, int, int)
     */
    public String[] getPage(String dn, Integer page, Integer pageSize) throws Exception{
        // retrieve the user's history in the paging manner requested.
        if (!isValid(dn))
            throw new AuthenticationException("Please supply a valid DN " + dn);
        
        logger.debug("Retrieving page " + page + " of user history...");
        History history = new History(dn);
        
        List<FileTransferTask> taskList = history.get(page.intValue(),pageSize.intValue());
        
        if (taskList.size() > 0) {
            int i=0;
            String[] xmlTaskList = new String[taskList.size()];
            for(FileTransferTask ftt: taskList) {
                xmlTaskList[i++] = xstream.toXML(ftt);
            }
            
            logger.debug("Retrieved " + taskList.size() + 
                    " transfer records for " + history.getUser().getUsername());
            
            return xmlTaskList;
        } else {
            return new String[]{new String("none")};
        }
        
    }
    
    /* (non-Javadoc)
     * @see org.teragrid.portal.filebrowser.server.servlet.TGFileTransferHistory#add(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @SuppressWarnings("unchecked")
	public String add(String dn, String serializedTransfer, String epr, String type) throws Exception {
        
        if (!isValid(dn))
            throw new AuthenticationException("Please supply a valid DN " + dn);
        
        if (!isValid(serializedTransfer))
            throw new PersistenceException("Please supply a valid file transfer.");
        
        History history = new History(dn);
        
//        logger.debug("Adding transfer " + dewebify(transfer) + " to user history...\n");
        
        List<Transfer> transfers = (List<Transfer>)xstream.fromXML(serializedTransfer);
        
        logger.debug("Adding " + transfers.size() + " transfers to user history...");
        
        List<Integer> transferIds = history.add(transfers, epr, NotificationType.getType(type));
        
        return xstream.toXML(transferIds);
       
    }
    
    
    /* (non-Javadoc)
     * @see org.teragrid.portal.filebrowser.server.servlet.TGFileTransferHistory#update(java.lang.String, long, int)
     */
    @SuppressWarnings("unchecked")
	public int update(String dn, String serializedTransfer) {
        
        if (!isValid(dn))
            throw new AuthenticationException("Please supply a valid DN " + dn);
        
        if (!isValid(serializedTransfer))
            throw new PersistenceException("Please supply a valid file transfer.");
        
        History history = new History(dn);
        
        List<Transfer> transfers = (List<Transfer>)xstream.fromXML(serializedTransfer);
        
        for (Transfer transfer: transfers) {
            if (transfer.getId() == null)
                throw new PersistenceException("Please supply a valid file transfer ID.");
        
            logger.debug("Updating status of file transfer " + transfer.getId() + 
                    " to " + FileTransferTask.getStatusString(transfer.getStatus()) + 
                    " with " + transfer.getProgress() + "% completed.");
            
            history.update(transfer);
        }    
        return 0;
    }
    
    /* (non-Javadoc)
     * @see org.teragrid.portal.filebrowser.server.servlet.TGFileTransferHistory#remove(java.lang.String, long[])
     */
    @SuppressWarnings("unchecked")
	public int remove(String dn, String serializedTransferIds) {
        if (!isValid(dn))
            throw new AuthenticationException("Please supply a valid DN " + dn);
        
        if (!isValid(serializedTransferIds)) 
            throw new PersistenceException("Please supply a valid set of transfer IDs.");
        
        History history = new History(dn);
        
        try {
            Integer transferId = new Integer(serializedTransferIds);
            
            logger.debug("Removing transfer " + serializedTransferIds + " transfers from user history...");
            
            history.remove(transferId);
            
        } catch (NumberFormatException e) {
            ArrayList<Integer> transferIds = (ArrayList<Integer>)xstream.fromXML(serializedTransferIds);
            
            logger.debug("Removing " + transferIds.size() + " transfers from user history...");
            
            history.remove(transferIds);
        } 
        
        return 0;
    }
    
    
    public int clear(String dn) {
       
        if (!isValid(dn))
            throw new AuthenticationException("Please supply a valid DN " + dn);
        
        History history = new History(dn);
        
        logger.debug("Clearing all transfers for dn =  " + dn);
        
        history.removeAll();
        
        return 0;
         
    }
    
    private boolean isValid(String s) {
        return !(s==null || s.equals(""));
    }
    
    @SuppressWarnings("unused")
	private String dewebify(String s) {
        return s.replaceAll("&lt;", "<")
        .replaceAll("&gt;", ">")
        .replaceAll("&quot;","\"")
        .replaceAll("&apos;", "'");
    }
    
}
