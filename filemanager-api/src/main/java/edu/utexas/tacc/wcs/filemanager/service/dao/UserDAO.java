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
import org.hibernate.criterion.Restrictions;

import edu.utexas.tacc.wcs.filemanager.common.model.Acctv;
import edu.utexas.tacc.wcs.filemanager.common.model.DN;
import edu.utexas.tacc.wcs.filemanager.common.model.User;
import edu.utexas.tacc.wcs.filemanager.service.exception.AuthenticationException;
import edu.utexas.tacc.wcs.filemanager.service.persistence.HibernateUtil;

/**
 * Data access methods for the User database.
 * 
 * @author Rion Dooley < dooley [at] tacc [dot] utexas [dot] edu >
 *
 */
@SuppressWarnings("unchecked")
public class UserDAO {
	private static final Logger logger = Logger.getLogger(UserDAO.class);
	
    public static User findUserByDN(String dn) throws AuthenticationException{
        HibernateUtil.beginTransaction();
        
        Session session = HibernateUtil.getSession();
        
        List<Object[]> users = null;
        
        try {
            
//            users = session.getNamedQuery("findUser").setString("userDN",dn).list();
            
            String sql = "select {u.*}, {disName.*} from user_info u, dns disName where u.person_id = disName.person_id and disName.dn = :userDN";
            
            users = session.createSQLQuery(sql)
                .addEntity("u",User.class)
                .addEntity("disName",DN.class)
                .setString("userDN",dn).list();
            
//            String sql = "SELECT u.person_id, " +
//            		            "u.username, " +
//            		            "userDns.dn, " +
//            		            "u.first_name, " +
//            		            "u.last_name, " +
//            		            "u.email, " +
//            		            "u.home_phone_number \n" +
//            		      "FROM user_info u \n" +
//            		      "JOIN dns userDns \n" +
//            		          "ON u.person_id = userDns.person_id \n" +
//            		      "WHERE userDns.dn = :userDN";
//            
//            users = session.createSQLQuery(sql).setString("userDN",dn).list();
            
            if (users == null || users.size() == 0) {
                throw new AuthenticationException("User for dn " + dn + " not found.");
            } else if (users.size() > 1) { 
                throw new AuthenticationException("Multiple users found for DN = " + dn);
            }
//            Object[] resultsArray = users.get(0);
//            User user = new User();
//            user.setId(new Long(((Integer)resultsArray[0]).intValue()));
//            user.setUsername((String)resultsArray[1]);
//            user.setDn((String)resultsArray[2]);
//            user.setFirstName((String)resultsArray[3]);
//            user.setLastName((String)resultsArray[4]);
//            user.setEmail((String)resultsArray[5]);
//            user.setCell((String)resultsArray[6]);
//            
//            
            User user = (User)users.get(0)[0];
            user.setDn(((DN)users.get(0)[1]).getDn());
            
            return user;
            
        }  catch (HibernateException ex) {
//            session.close();
            throw new AuthenticationException(ex);
        } catch (Exception e) {
//            session.close();
            throw new AuthenticationException("Failed to retrieve user.",e);
        }  finally {
        	//HibernateUtil.closeSession();
        }
        
    }
    
    public static List<User> findByEmail(String searchString) throws Exception {
    	HibernateUtil.beginTransaction();
        
        Session session = HibernateUtil.getSession();
        
        List<User> users = new ArrayList<User>();
        String hql = "select new User(firstName, lastName, username) from User where email like :searchString";
        try {
        	
            users = session.createQuery(hql).setString("searchString", searchString+"%").setMaxResults(20).list();
        }  catch (HibernateException ex) {
            session.close();
        }  finally {
        	HibernateUtil.closeSession();
        }
        
        return users;
        
    }
    
    public static List<User> findByName(String searchString) throws Exception {
    	HibernateUtil.beginTransaction();
        
        Session session = HibernateUtil.getSession();
        
        List<User> users = new ArrayList<User>();
//        String hql = "from User u where ";
        String hql = "select new User(u.firstName, u.lastName, u.username) from User u where ";
        if (searchString.contains(" ")) { 
        	String[] tokens = searchString.trim().split(" ");
    		hql += "lower(first_name) like '" + tokens[0].toLowerCase() + "%' and ";
    		hql += "lower(last_name) like '" + tokens[1].toLowerCase() + "%'";
        } else {
        	hql += "lower(first_name) like '" + searchString.toLowerCase() + "%' or ";
    		hql += "lower(last_name) like '" + searchString.toLowerCase() + "%'";
        }

        try {
            users = session.createQuery(hql)
            		.setMaxResults(20)
            		.list();
        
        }  catch (HibernateException ex) {
            logger.error("Failed to find user",ex);
        } finally {
        	HibernateUtil.closeSession();
        }
        
        return users;
        
    }

    public static List<User> findByOrganization(String searchString) throws Exception {
    	HibernateUtil.beginTransaction();
        
        Session session = HibernateUtil.getSession();
        
        List<User> users = new ArrayList<User>();
        
        String hql = "select new User(firstName, lastName, username) from User where organization like ':searchString'";
        try {
        	
            users = session.createQuery(hql).setString("organization", searchString+"%").setMaxResults(20).list();
        }  catch (HibernateException ex) {
            session.close();
        }  finally {
        	HibernateUtil.closeSession();
        }
        
        return users;
        
    }
    
    public static List<User> findByDepartment(String searchString) throws Exception {
    	HibernateUtil.beginTransaction();
        
        Session session = HibernateUtil.getSession();
        
        List<User> users = new ArrayList<User>();
        
        String hql = "select new User(firstName, lastName, username) from User where department like :searchString";
        try {
        	
            users = session.createQuery(hql).setString("department", searchString+"%").setMaxResults(20).list();
        }  catch (HibernateException ex) {
            session.close();
        }  finally {
        	HibernateUtil.closeSession();
        }
        
        return users;
        
    }
    
    public static List<User> findByUsername(String searchString) throws Exception {
    	HibernateUtil.beginTransaction();
        
        Session session = HibernateUtil.getSession();
        
        List<User> users = new ArrayList<User>();
        String hql = "select new User(firstName, lastName, username) from User where username like :searchString";
        try {
        	
            users = session.createQuery(hql).setString("username", searchString+"%").setMaxResults(20).list();
        }  catch (HibernateException ex) {
            session.close();
        }  finally {
        	HibernateUtil.closeSession();
        }
        
        return users;
        
    }
    
    public static List<User> findColleagues(String dn) throws Exception {
    	
    	HibernateUtil.beginTransaction();
        
        Session session = HibernateUtil.getSession();
        
        List<User> users = new ArrayList<User>();
        User user = UserDAO.findUserByDN(dn);
        List<Acctv> accounts = AcctvDAO.findAcctvByPersonId(user.getId().intValue());
        
        String sql = "select distinct a.first_name, a.last_name, u.username from portal.acctv a right outer join portal.user_info u on u.person_id = a.person_id where u.organization = '" +
        		user.getOrganization() + "' or a.grant_number in (";
        
        for (Acctv account: accounts) {
        	sql += "'" + account.getGrantNumber() + "',";
        }
        sql = sql.substring(0,sql.length()-1);
        sql += ") order by a.last_name ASC";
        
        try {
        	logger.debug(sql);
        	List<Object[]> results = session.createSQLQuery(sql).list();
            
            for (Object[] record: results) {
            	users.add(new User((String)record[0],(String)record[1],(String)record[2]));
            }
            
        }  catch (HibernateException ex) {
        	ex.printStackTrace();
            session.close();
        } finally {
        	HibernateUtil.closeSession();
        }
        
        return users;
    }
    
    public static List<User> findProjectPartners(String dn) throws Exception {
    	
    	HibernateUtil.beginTransaction();
        
        Session session = HibernateUtil.getSession();
        
        List<User> users = new ArrayList<User>();
        
        List<Acctv> accounts = AcctvDAO.findAcctvByDn(dn);
        
        String sql = "select distinct a.first_name, a.last_name, u.username from portal.acctv a right outer join portal.user_info u on u.person_id = a.person_id where a.grant_number in (";
        
        for (Acctv account: accounts) {
        	sql += "'" + account.getGrantNumber() + "',";
        }
        sql = sql.substring(0,sql.length()-1);
        sql += ") order by a.last_name ASC";
        
        try {
        	
            List<Object[]> results = session.createSQLQuery(sql).list();
            
            for (Object[] record: results) {
            	users.add(new User((String)record[0],(String)record[1],(String)record[2]));
            }
            
        }  catch (HibernateException ex) {
        	ex.printStackTrace();
            session.close();
        } finally {
        	HibernateUtil.closeSession();
        }
        
        return users;
    }
    

    public static User loadUserByDN(String dn) throws AuthenticationException{
        HibernateUtil.beginTransaction();
        
        Session session = HibernateUtil.getSession();
        
        List<DN> dns = null;
        
        try {
            dns = session.createCriteria(DN.class)
                .add(Restrictions.eq("dn",dn)).list();
                
            if (dns == null || dns.size() == 0) {
                throw new AuthenticationException("User for dn " + dn + " not found.");
            } else if (dns.size() > 1) { 
                throw new AuthenticationException("Multiple users found for DN = " + dn);
            }
            
            User user = dns.get(0).getUser();
            user.setDn(dn);
            return user.shallowCopy();
            
        }  catch (HibernateException ex) {
            session.close();
            throw new AuthenticationException(ex);
        } catch (Exception e) {
            session.close();
            throw new AuthenticationException("Failed to retrieve user.",e);
        } finally {
        	HibernateUtil.closeSession();
        }
    }

}
