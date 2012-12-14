/* 
 * Created on Dec 17, 2007
 * 
 * Developed by: Rion Dooley - dooley [at] tacc [dot] utexas [dot] edu
 * 				 TACC, Texas Advanced Computing Center
 * 
 * https://www.tacc.utexas.edu/
 */

package org.teragrid.portal.filebrowser.server.servlet;

import java.io.IOException;

public interface TGResourceDiscovery {

    /**
     * Get the list of resources the user with the given dn has access to.
     * The list is obtained by cross-referencing the list of allocable 
     * resources for the user with the given dn agast the resources in 
     * GPIR.
     * 
     * @param dn
     * @return
     * @throws IOException
     */
    public abstract String[] retrieveResources(String dn)
            throws IOException;

    /**
     * Returns the bandwdith estimate between two resoruces in MB. If either or both
     * resources are not TG resources, it returns a -1.
     * 
     * @param from
     * @param to
     * @return
     */
    public abstract String getBandwidth(String from, String to);
}