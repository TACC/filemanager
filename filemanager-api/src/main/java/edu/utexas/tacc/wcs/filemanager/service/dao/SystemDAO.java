/* 
 * Created on Jun 20, 2008
 * 
 * Developed by: Rion Dooley - dooley [at] tacc [dot] utexas [dot] edu
 * 				 TACC, Texas Advanced Computing Center
 * 
 * https://www.tacc.utexas.edu/
 */

package edu.utexas.tacc.wcs.filemanager.service.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;

import edu.utexas.tacc.wcs.filemanager.common.model.System;
import edu.utexas.tacc.wcs.filemanager.common.model.User;
import edu.utexas.tacc.wcs.filemanager.service.exception.ResourceException;
import edu.utexas.tacc.wcs.filemanager.service.persistence.HibernateUtil;

/**
 * Retrieves the systems for which the user has a valid account
 * 
 * @author Rion Dooley < dooley [at] tacc [dot] utexas [dot] edu >
 *
 */
@SuppressWarnings("unchecked")
public class SystemDAO {

	private static final Logger logger = Logger.getLogger(SystemDAO.class);
	
    public static List<System> findSystemAccounts(User user) throws ResourceException{
        
        HibernateUtil.beginTransaction();
        
        Session session = HibernateUtil.getSession();
        
        List<System> activeSystems = new ArrayList<System>();
        try {
        	// find all resources for the user
            String sql = "select s.username, s.resource_name " + 
				"from portal.sav s where s.person_id = " + user.getId() + " and s.is_active is TRUE " + 
				" and s.resource_name not in (SELECT ar.resource_name FROM portal.allocable_resources ar WHERE ar.is_active is FALSE)";
            
            List<Object[]> results = session.createSQLQuery(sql).list();
            
            logger.debug("Found " + results.size() + " compute results.");
            
            for (Object[] result: results) {
                System system = new System();
                system.setResourceId((String)result[1]);
                system.setUserId(user.getId());
                system.setUserName((String)result[0]);
                
                activeSystems.add(system);
            }
            
            sql = "SELECT ar.pops_name as username, ar.resource_name FROM portal.allocable_resources ar " +
    			"WHERE ar.is_active is FALSE";
            
//            List<Object[]> inactiveResults = session.createSQLQuery(sql).list();
//            
//            logger.debug("Found " + inactiveResults.size() + " inactive resources.");
            
            
//            for (Object[] result: inactiveResults) {
//                String resourceName = (String)result[1];
////                logger.debug("Resolving inactive resource " + result);
//                        
//                for (System system: allSystems) {
//                    // if the system is not active delete it.
//                    if (system.getResourceName().equals(resourceName) {
//                    	if (!resourceName.equals("lonestar.tacc.teragrid")) {
//                    	allSystems.remove(system);
//                        logger.debug("Removing inactive resource " + system.getResourceName() + " from the result set");
//                    	} else {
//                    		
//                    	}
//                    	break;
//                }
//            }
            
            return activeSystems;
            
        } catch (HibernateException ex) {
            
            throw new ResourceException("Error retrieving system resources for " + user.getUsername(),ex);
            
        } finally {
            session.close();
        }
    }
    
    public static List<System> findStorageAccounts(User user) throws ResourceException {
        HibernateUtil.beginTransaction();
        
        Session session = HibernateUtil.getSession();
        
        List<System> systems = new ArrayList<System>();
        
        try {
            String sql = "SELECT DISTINCT site_person_id, site_resource_name, proj_on_resource_state " + 
            "FROM acctv2 WHERE person_id = " + user.getId() + 
            " and proj_on_resource_state != 'inactive' and username != ''";
            
            List<Object[]> results = session.createSQLQuery(sql).list();
            
            logger.debug("Found " + results.size() + " storage results.");
            
            for (Object[] result: results) {
                System system = new System();
                system.setResourceName((String)result[1]);
                system.setUserId(user.getId());
                system.setUserName((String)result[0]);
                system.setUserState((String)result[2]);
                
                systems.add(system);
            }
            
            return systems;
            
        } catch (HibernateException ex) {
            
            throw new ResourceException("Error retrieving system resources for " + user.getUsername(),ex);
            
        } finally {
            session.close();
        }
    }
}
