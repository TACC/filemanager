/* 
 * Created on Dec 11, 2007
 * 
 * Developed by: Rion Dooley - dooley [at] tacc [dot] utexas [dot] edu
 * 				 TACC, Texas Advanced Computing Center
 * 
 * https://www.tacc.utexas.edu/
 */

package edu.utexas.tacc.wcs.filemanager.service.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.PersistenceException;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import edu.utexas.tacc.wcs.filemanager.common.model.Transfer;
import edu.utexas.tacc.wcs.filemanager.common.model.User;
import edu.utexas.tacc.wcs.filemanager.service.persistence.HibernateUtil;

/**
 * Transaction class for Transfer reference objects wrapping
 * db queries and actions.
 * 
 * @author Rion Dooley < dooley [at] tacc [dot] utexas [dot] edu >
 *
 */
@SuppressWarnings({"unchecked","deprecation"})
public class TransferDAO {
	private static final Logger logger = Logger.getLogger(TransferDAO.class);
	private static int MAX_RESULTS = 150;
	
    public TransferDAO() {
        HibernateUtil.beginTransaction();
    }

    // ********************************************************** //

    public Transfer getTransferById(Long nId, boolean lock)
            throws PersistenceException {

        Session session = HibernateUtil.getSession();
        Transfer transfer = null;
        try {
            if (lock) {
                transfer = (Transfer) session.load(Transfer.class, nId, LockMode.UPGRADE);
            } else {
                transfer = (Transfer) session.load(Transfer.class, nId);
            }
        }  catch (HibernateException ex) {
            throw new PersistenceException(ex);
        } finally {
        	HibernateUtil.closeSession();
        }
        return transfer;
    }
    
    public boolean exists(Long transferId) {
        HibernateUtil.beginTransaction();
        
        Session session = HibernateUtil.getSession();
        
        try {
            Transfer t = (Transfer)session.get(Transfer.class, transferId);
            if (t == null || !t.isVisible()) {
                return false;
            } else {
                return true;
            }
        } catch (HibernateException e) {
            throw new PersistenceException(e);
        } finally {
        	HibernateUtil.closeSession();
        }
    }

    // ********************************************************** //

    public List<Transfer> getAllByDN(String dn)
            throws PersistenceException {
    	
    	Session session = HibernateUtil.getSession();
        
        List<Transfer> sites = null;
        
        try {
        	String sQuery = "from Transfer t where t.dn = :dn and t.visible = :visible ORDER by t.id DESC";
            Query q = session.createQuery(sQuery);
            q.setString("dn", dn);
            q.setBoolean("visible", true);
        	q.setMaxResults(150);
        	sites = q.list();
            
        }  catch (HibernateException ex) {
            throw new PersistenceException(ex);
        } catch (Exception e) {
            logger.error(e.toString());
        } finally {
        	HibernateUtil.closeSession();
        }
        return sites;
    }

    // ********************************************************** //

    public int getTransfersCount(String dn)
    throws PersistenceException {

        Session session = HibernateUtil.getSession();
        
        try {
            // simply count the number of rows in the result set.
            Criteria criteria = session.createCriteria(Transfer.class);
            criteria.add(Restrictions.eq("visible", true));
            criteria.setProjection(Projections.rowCount());
            return ((Integer)criteria.list().get(0)).intValue();
            
        }  catch (HibernateException ex) {
            throw new PersistenceException(ex);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.toString());
        } finally {
        	HibernateUtil.closeSession();
        }
        return 0;
    }

    // ********************************************************** //
    public List<Transfer> getAllTransfersByDN(String dn)
    throws PersistenceException {

    	int transferCount = getTransfersCount(dn);
    	
        Session session = HibernateUtil.getSession();
        
        List<Transfer> sites = new ArrayList<Transfer>();
        
        try 
        { 
            sites = session.createQuery(
                    "from Transfer t where t.dn = :dn and t.visible = :visible order by t.created desc")
                    .setString("dn",dn)
                    .setBoolean("visible", true)
                    .setFirstResult((transferCount >= MAX_RESULTS)?transferCount - MAX_RESULTS: 0)
        	        .setMaxResults(150)
                    .list();
            
        }  catch (HibernateException ex) {
            throw new PersistenceException(ex);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.toString());
        } finally {
        	HibernateUtil.closeSession();
        }
        return sites;
    }

    // ********************************************************** //

    public List<Transfer> getPageTransfersByDN(String dn, int page, int pageSize)
    throws PersistenceException {

        Session session = HibernateUtil.getSession();
        
        List<Transfer> sites = new ArrayList<Transfer>();
        
        try {
            
            sites = session.createQuery(
                    "select task from Transfers t where t.dn = :dn and t.visible = :visible order by t.created desc")
                .setString("dn",dn)
                .setBoolean("visible", true)
                .setFirstResult(page * pageSize)
                .setMaxResults(pageSize)
                .list();
            
        }  catch (HibernateException ex) {
            throw new PersistenceException(ex);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.toString());
        } finally {
        	HibernateUtil.closeSession();
        }
        return sites;
    }
    // ********************************************************** //
    
    public void delete(Long transferId)
    throws PersistenceException {

        Transfer transfer = null;
        
        try {
            transfer = (Transfer)HibernateUtil.getSession().get(Transfer.class,transferId);
            if (transfer.isVisible()) {
                transfer.setVisible(false);
            }
            
            makePersistent(transfer);
            
        }  catch (HibernateException ex) {
            throw new PersistenceException(ex);
        }
        
    }
    
    // ********************************************************** //
    
    public void deleteAll(String dn)
    throws PersistenceException {

        Collection<Transfer> transfers = getAllByDN(dn);
        
        if (transfers == null || transfers.size() == 0) {
            throw new PersistenceException("No transfer records found for DN = " + dn);
        } 
        
        for (Transfer transfer: transfers) {
            if (transfer.isVisible()) {
                transfer.setVisible(false);
            }
        }
        
        makePersistent(transfers);
        
    }
    
    public static boolean userHasPermission(Long transferId, User user) {
        HibernateUtil.beginTransaction();
        
        if (transferId == null || transferId.longValue() < 0) {
            throw new PersistenceException("Invalid transfer id");
        }
        
        Session session = HibernateUtil.getSession();
       
        try {
            Transfer transfer = (Transfer) session.get(Transfer.class, transferId);
            
            if (transfer == null) {
                throw new PersistenceException("Invalid transfer id " + transferId);
            } else {
                if (transfer.getDn().equals(user.getDn())) {
                    return true;
                } else {
                    return false;
                }
            }
        }  catch (HibernateException ex) {
            throw new PersistenceException(ex);
        } finally {
        	HibernateUtil.closeSession();
        }
        
        
    }
    
    public static boolean userHasPermission(List<Long> transferIds, User user) {
        HibernateUtil.beginTransaction();
        
        Session session = HibernateUtil.getSession();
        
        for (Long transferId: transferIds) {
        
            if (transferId.intValue() < 0) {
                throw new PersistenceException("Invalid transfer id " + transferId);
            }
            
            try {
                Transfer transfer = (Transfer) session.get(Transfer.class, transferId);
                
                if (transfer == null) {
                    throw new PersistenceException("Invalid transfer id");
                } else {
                    if (!transfer.getDn().equals(user.getDn())) {
                        return false;
                    }
                }
            }  catch (HibernateException ex) {
                throw new PersistenceException(ex);
            } finally {
            	HibernateUtil.closeSession();
            }
        }
        
        return true;
    }
    // ********************************************************** //
    
    public Object makePersistent(Object transfer)
    throws PersistenceException {

        try {
            
            Session s = HibernateUtil.getSession();
            
            Transaction tx = s.getTransaction();
            
            tx.begin();
            
            if (transfer instanceof ArrayList) {
                for (Transfer t: (ArrayList<Transfer>)transfer) {
                    s.saveOrUpdate(t);
                    logger.debug("Persisted " + t.toString() + " as id = " + t.getId());
                }
                
            } else {
                s.saveOrUpdate(transfer);
                logger.debug("Persisted " + transfer.toString() + " as id = " + ((Transfer)transfer).getId());
            }
            
            tx.commit();
            
            s.flush();
            
            s.clear();
            
            s.evict(transfer);
            
            s.close();
            
//            System.out.println("New transfer id = " + transfer.getId());
            
            return transfer;
            
        } catch (Exception e) {
            logger.error("Failed to finish add",e);
            HibernateUtil.rollbackTransaction();
            throw new PersistenceException(e);
        } finally {
            HibernateUtil.closeSession();
        }
    }
    
    // ********************************************************** //
    
    public void makeTransient(Object transfer)
        throws PersistenceException {
    
        try {
            Session s = HibernateUtil.getSession();
            
            Transaction tx = s.getTransaction();
            
            tx.begin();
            
            if (transfer instanceof ArrayList) {
                for (Transfer t: (ArrayList<Transfer>)transfer) {
                    s.delete(t);
                }
            } else {
                s.delete(transfer);
            }
            
            tx.commit();
            
            s.flush();
            
            s.clear();
            
            s.evict(transfer);
            
            s.close();
            
        } catch (Exception e) {
            logger.error("Failed to finish delete",e);
            HibernateUtil.rollbackTransaction();
            throw new PersistenceException(e);
        } finally {
            HibernateUtil.closeSession();
        }
    }
}