/* 
 * Created on Dec 17, 2007
 * 
 * Developed by: Rion Dooley - dooley [at] tacc [dot] utexas [dot] edu
 * 				 TACC, Texas Advanced Computing Center
 * 
 * https://www.tacc.utexas.edu/
 */

package org.teragrid.portal.filebrowser.server.servlet;


public interface TGFileTransferHistory {

    /**
     * Get the user's entire file transfer history.
     * @param dn
     * @return
     */
    public abstract String get(String dn)
    throws Exception;

    /**
     * Get the given page of the user's file transfer history, where
     * page size is given as an argument.
     * 
     * @param dn
     * @param page
     * @param pageSize
     * @return
     */
    public abstract String[] getPage(String dn, Integer page, Integer pageSize) 
    throws Exception;

    /**
     * Store a new file transfer record in the db.
     * 
     * @param dn
     * @param ftt
     * @param epr
     * @param type
     */
    public abstract String add(String dn, String serializedTransfer, String epr, String type)
    throws Exception;

    /**
     * Delete all file transfer records with the given ids and remove any
     * scheduled notifications.
     *  
     * @param dn
     * @param ids
     */
    public abstract int update(String dn, String serializedTransfer);

    /**
     * Delete all file transfer records with the given ids and remove any
     * scheduled notifications.
     *  
     * @param dn
     * @param ids
     */
    public abstract int remove(String dn, String serializedTransferIds);
    
    /**
     * Delete all transfer records for user with given dn and remove the 
     * corresponding notifications.
     * 
     * @param dn
     * @return
     */
    public abstract int clear(String dn);

}