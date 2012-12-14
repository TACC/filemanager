/* 
 * Created on Jun 20, 2008
 * 
 * Developed by: Rion Dooley - dooley [at] tacc [dot] utexas [dot] edu
 * 				 TACC, Texas Advanced Computing Center
 * 
 * https://www.tacc.utexas.edu/
 */

package org.teragrid.portal.filebrowser.server.servlet.model.user;

import java.io.Serializable;


/**
 * Generic DN class to handle OR mapping
 * 
 * @author Rion Dooley < dooley [at] tacc [dot] utexas [dot] edu >
 *
 */

public class DN {
    
 // ******************* Begin Inner composite Id class ******************* //
    @SuppressWarnings("serial")
	public static class Id implements Serializable {
        private String username;
        private Long personId;
        private String dn;
        
        public Id() {}

        /**
         * @param username
         * @param personId
         * @param dn
         */
        public Id(String username, Long personId, String dn) {
            this.username = username;
            this.personId = personId;
            this.dn = dn;
        }

        public boolean equals(Object o) {
            if (o instanceof Id) {
                Id that = (Id)o;
                return this.username.equals(that.username) &&
                       this.personId.equals(that.personId) &&
                       this.dn.equals(that.dn) ;
            } else {
                return false;
            }
        }
        
        public int compareTo(Object o) {
            // CategorizedUsers are sorted by date
            if (o instanceof Id)
                return new Integer(hashCode()).compareTo(new Integer(((Id)o).hashCode()));
            return 0;
        }

        public int hashCode() {
            return username.hashCode() + personId.intValue() + 
                dn.hashCode() + dn.hashCode();
        }
    }
    // ******************* End Inner composite Id class ******************* //

    private Id id = new Id();
    
    private String dn = "";
    private User user = null;
    private String username = "";
    
    public DN() {}

    /**
     * @return the person_id
     */
    public Id getId() {
        return id;
    }

    /**
     * @param person_id the person_id to set
     */
    public void setId(Id id) {
        this.id = id;
    }

    /**
     * @return the dn
     */
    public String getDn() {
        return dn;
    }

    /**
     * @param dn the dn to set
     */
    public void setDn(String dn) {
        this.dn = dn;
    }

    /**
     * @return the user
     */
    public User getUser() {
        return user;
    }

    /**
     * @param user the user to set
     */
    public void setUser(User user) {
        this.user = user;
    }
    
    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    public int hashCode() {
        return (dn.hashCode() + user.hashCode());
    }
    
    public int compareTo(Object o) {
        // CategorizedUsers are sorted by date
        if (o instanceof DN)
            return getId().compareTo(((DN)o).getId());
        return 0;
    }
    
    public boolean equals(Object o) {
        if (o instanceof DN) {
            DN userDn = (DN)o;
            return (user.getId().equals((userDn.user.getId())) &&
                dn.equals(userDn.dn) &&
                user.getUsername().equals(userDn.user.getUsername()));
        }
        return false;
    }
    
    public String toString() {
        return dn;
    }

}
