/**
 * 
 */
package edu.utexas.tacc.wcs.filemanager.service.dao;

/**
 * @author dooley
 *
 */
import java.util.List;

import javax.persistence.PersistenceException;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import edu.utexas.tacc.wcs.filemanager.common.model.Acctv;
import edu.utexas.tacc.wcs.filemanager.service.persistence.HibernateUtil;

@SuppressWarnings({"unchecked"})
public class AcctvDAO {
    private static Logger log = LogManager.getLogger(AcctvDAO.class);
    
    // ********************************************************** //

    public static List<Acctv> findAcctvByPersonId(int personId) {
        
        HibernateUtil.beginTransaction();
        
        Session session = HibernateUtil.getSession();
        
        List<Acctv> acctvs = null;
        
        try {
            
        	String sql = "FROM Acctv acctv where acctv.personId = :personId and acct_state = 'active'";
            
            acctvs = session.createQuery(sql).setInteger("personId",personId).list();
            
        }  catch (HibernateException ex) {
            log.error("Failed to retreive accounts", ex);
        }
        
        return acctvs;
    }
    
   // ********************************************************** //

    public static List<Acctv> findAcctvByDn(String dn) {
        
        //clearCache();
        
        int personId = UserDAO.findUserByDN(dn).getId().intValue();
        
        return findAcctvByPersonId(personId);
    }
    
    // ********************************************************** //
    
    public static List<Acctv> findAcctvByRequestId(String requestId) {
        
        clearCache();
        
        HibernateUtil.beginTransaction();
        
        Session session = HibernateUtil.getSession();
        
        List<Acctv> acctvs = null;
        
        try {
            session.flush();
            
            acctvs = session.createCriteria(Acctv.class).add(Restrictions.eq("requestId",Integer.valueOf(requestId))).list();
            
            if (acctvs == null || acctvs.size() == 0) {
                throw new PersistenceException("No allocation for id " + requestId + " found.");
            }
        }  catch (HibernateException ex) {
        	log.error("Failed to retreive accounts", ex);
        }
        
        return acctvs;
    }
    
    public static void clearCache() {
        HibernateUtil.beginTransaction();

        try {
            
            Session s = HibernateUtil.getSession();
            
            s.flush();
            
            s.clear();
            
        } catch (HibernateException e) {
            throw new PersistenceException(e);
        }
        
        HibernateUtil.commitTransaction();
        
    }
}