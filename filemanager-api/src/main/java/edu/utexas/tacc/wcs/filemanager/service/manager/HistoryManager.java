/* 
 * Created on Dec 11, 2007
 * 
 * Developed by: Rion Dooley - dooley [at] tacc [dot] utexas [dot] edu
 * 				 TACC, Texas Advanced Computing Center
 * 
 * https://www.tacc.utexas.edu/
 */

package edu.utexas.tacc.wcs.filemanager.service.manager;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.persistence.PersistenceException;

import org.apache.log4j.Logger;

import edu.utexas.tacc.wcs.filemanager.common.model.Task;
import edu.utexas.tacc.wcs.filemanager.common.model.Transfer;
import edu.utexas.tacc.wcs.filemanager.common.model.User;
import edu.utexas.tacc.wcs.filemanager.common.model.enumeration.NotificationType;
import edu.utexas.tacc.wcs.filemanager.service.dao.TransferDAO;
import edu.utexas.tacc.wcs.filemanager.service.dao.UserDAO;
import edu.utexas.tacc.wcs.filemanager.service.exception.AuthenticationException;
import edu.utexas.tacc.wcs.filemanager.service.exception.PermissionException;


/**
 * File transfer history management class. Retrieves the file
 * history and paginates the results.
 * 
 * @author Rion Dooley < dooley [at] tacc [dot] utexas [dot] edu >
 *
 */
public class HistoryManager {
	
	private static final Logger logger = Logger.getLogger(HistoryManager.class);
	
    private TransferDAO _tDAO;
    
    private String _dn = "";
    private User _user = null;
    
    public HistoryManager(String dn) {
        _tDAO = new TransferDAO();
        _dn = dn;

        validate();
                
    }
    
    private boolean validate() {
        
        _user = UserDAO.loadUserByDN(_dn);
        
        if (_user == null) {
            throw new AuthenticationException("No user found matching DN = \"" + _dn + "\"");
        }
        
        return true;
    }
    
    public User getUser() {
        return this._user;
    }
    
    public List<Transfer> get() throws MalformedURLException {
        ArrayList<Transfer> tList = new ArrayList<Transfer>();
        List<Transfer> transfers = _tDAO.getAllByDN(_dn);
        
        for (Transfer t: transfers) {
            tList.add(t);
        }
        
        return tList;
    }
    
    public List<Transfer> get(int page, int pageSize) throws MalformedURLException {
        
        int transferCount = _tDAO.getTransfersCount(_dn);
        
        if (transferCount < pageSize)
            return get(); 
        
        if ((page*pageSize) > transferCount || page < 1)
            return new ArrayList<Transfer>();
        
        return _tDAO.getPageTransfersByDN(_dn, page, pageSize);
    }
    
    @SuppressWarnings("unchecked")
	public  List<Long> add(List<Transfer> transfers, String epr, NotificationType type) 
    throws MalformedURLException, URISyntaxException, PersistenceException {
        if (!_dn.equals(transfers.get(0).getDn()))
            throw new PermissionException("Permission denied. User DN does not match transfer DN");
        
        for (Transfer transfer : transfers) {
            if (transfer.getId() != null) {
                transfer.setId(null);
            }
        }
        
        transfers = (List<Transfer>)_tDAO.makePersistent(transfers);
        
        List<Long> transferIds = new ArrayList<Long>();
        
        for (Transfer transfer : transfers) {
            logger.debug("list: Added transfer id = " + transfer.getId());
            transferIds.add(transfer.getId());
        }
        
        if (!type.equals(NotificationType.NONE)) {
            notify(transferIds, type);
        }
        
        return transferIds;
    }
    
    public Long add(Transfer transfer, String epr, NotificationType type) 
    throws PersistenceException, MalformedURLException, URISyntaxException {
        
        if (!_dn.equals(transfer.getDn()))
            throw new PermissionException("Permission denied. User DN does not match transfer DN");
        
        if (transfer.getId() != null) {
            transfer.setId(null);
        }
        
        _tDAO.makePersistent(transfer);
        logger.debug("solo: Added transfer id = " + transfer.getId());
        if (!type.equals(NotificationType.NONE))
            notify(transfer.getId(),type);
        
        return transfer.getId();
    }
    
    public Long update(Transfer updatedTransfer) {
        
        if (!TransferDAO.userHasPermission(updatedTransfer.getId(), _user)) {
            throw new PermissionException("Permission denied. User does not have permission to subscribe to this transfer.");
        }
        
        Transfer currentTransfer = _tDAO.getTransferById(updatedTransfer.getId(), true);
        
        currentTransfer.setStatus(updatedTransfer.getStatus());
        if (currentTransfer.getStatus() == Task.DONE || currentTransfer.getStatus() == Task.FAILED) {
            currentTransfer.setStop(Calendar.getInstance());
        } else if (currentTransfer.getStatus() == Task.ONGOING) {
            currentTransfer.setStart(Calendar.getInstance());
        } else if (currentTransfer.getStatus() == Task.STOPPED) {
            currentTransfer.setStop(Calendar.getInstance());
            NotificationManager.unregister(currentTransfer.getId(), _user);
        } else {
            throw new PersistenceException("Invalid transfer status: " + updatedTransfer.getStatus());
        }
        currentTransfer.setPara(updatedTransfer.getPara());
        currentTransfer.setParaId(updatedTransfer.getParaId());
        currentTransfer.setSpeed(updatedTransfer.getSpeed());
        currentTransfer.setProgress(updatedTransfer.getProgress());
        
        _tDAO.makePersistent(currentTransfer);
        
        NotificationManager.sendAll(currentTransfer);
        
        return currentTransfer.getId();
    }
    
//public void add(FileTransferTask task, String epr, String t) {
//        
//        NotificationType t = NotificationType.getType(type);
//        Transfer t = new Transfer(task, epr, _dn);
//        
//        _tDAO.makePersistent(t);
//        
//        if (!NotificationType.getType(type).equals(NotificationType.NONE))
//            notify(t.getId(),NotificationType.getType(type));
//    }
    
    public void remove(Long transferId) {
        if (!TransferDAO.userHasPermission(transferId, _user)) {
            throw new PermissionException("Permission denied. User does not have permission to subscribe to this transfer.");
        }
        
        _tDAO.delete(transferId);
        NotificationManager.unregister(transferId, _user);
    }
    
    public void remove(List<Long> transferIds) {
        if (!TransferDAO.userHasPermission(transferIds, _user)) {
            throw new PermissionException("Permission denied. User does not have permission to subscribe to this transfer.");
        }
        
        for (Long transferId: transferIds) {
            
            logger.debug("Removing id=" + transferId);
            remove(transferId);
        }
    }
    
    public void removeAll() {
        
        _tDAO.deleteAll(_dn);
        
        NotificationManager.unregisterAll(_user);
    }
    
    public void notify(List<Long> transferIds, NotificationType type) {
        for (Long transferId : transferIds) {
            if (!TransferDAO.userHasPermission(transferId, _user)) {
                throw new PermissionException("Permission denied. User does not have permission to subscribe to this transfer.");
            }
        }
        
        NotificationManager.register(transferIds, type, _user);
    }
    
    public void notify(Long transferId, NotificationType type) {
        if (!TransferDAO.userHasPermission(transferId, _user)) {
            throw new PermissionException("Permission denied. User does not have permission to subscribe to this transfer.");
        }
        
        NotificationManager.register(transferId, type, _user);
    }
    
    public void denotify(Long transferId, NotificationType type) {
        if (!TransferDAO.userHasPermission(transferId, _user)) {
            throw new PermissionException("Permission denied. User does not have permission to subscribe to this transfer.");
        }
        
        if (type == null) {
        	NotificationManager.unregister(transferId, _user);
        }
        else {
        	NotificationManager.unregister(transferId, type, _user);
        }
    }
    
    public void denotify(Long transferId) {
        if (!TransferDAO.userHasPermission(transferId, _user)) {
            throw new PermissionException("Permission denied. User does not have permission to subscribe to this transfer.");
        }
        
        NotificationManager.unregister(transferId, _user);
    }
    
    public void denotifyAll() {
        NotificationManager.unregisterAll(_user);
    }
    
}
