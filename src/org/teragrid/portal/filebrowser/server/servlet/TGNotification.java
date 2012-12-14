/* 
 * Created on Dec 17, 2007
 * 
 * Developed by: Rion Dooley - dooley [at] tacc [dot] utexas [dot] edu
 * 				 TACC, Texas Advanced Computing Center
 * 
 * https://www.tacc.utexas.edu/
 */

package org.teragrid.portal.filebrowser.server.servlet;

public interface TGNotification {

    /**
     * Sets the notification flag for a given file transfer.
     * @param dn
     * @param transferId
     * @param notificationType
     * @param enabled
     */
    public abstract int add(String dn, String transferId, String notificationType);
    
    /**
     * Removes the notification flag for a given file transfer.
     * @param dn
     * @param transferId
     * @param notificationType
     * @return
     */
    public abstract int remove(String dn, String transferId, String notificationType);
    

    /**
     * Sets the notification flag for a given list of file transfers.
     * @param dn
     * @param serializedTransferIds
     * @param notificationType
     * @param enabled
     */
    public abstract int addAll(String dn, String serializedTransferIds, String notificationType);

    
    /**
     * Removes the notification flag for a given list of file transfers.
     * @param dn
     * @param serializedTransferIds
     * @param notificationType
     * @return
     */
    public abstract int removeAll(String dn, String serializedTransferIds, String notificationType);
    
    /**
     * Removes all notifications for a given file transfer.
     * @param dn
     * @param serializedTransferIds
     * @param notificationType
     * @param enabled
     */
    public abstract int clear(String dn, String serializedTransferIds);
    

}