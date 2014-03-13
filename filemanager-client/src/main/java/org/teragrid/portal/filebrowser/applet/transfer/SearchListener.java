/* 
 * Created on Aug 20, 2008
 * 
 * Developed by: Rion Dooley - dooley [at] tacc [dot] utexas [dot] edu
 * 				 TACC, Texas Advanced Computing Center
 * 
 * https://www.tacc.utexas.edu/
 */

package org.teragrid.portal.filebrowser.applet.transfer;

import javax.swing.SwingUtilities;

import org.teragrid.portal.filebrowser.applet.AppMain;
import org.teragrid.portal.filebrowser.applet.ui.PnlSearch;
import org.teragrid.portal.filebrowser.applet.util.LogManager;


/**
 * Listener for Search thread. It forwards all updates to the gui. 
 * 
 * @author Rion Dooley < dooley [at] tacc [dot] utexas [dot] edu >
 *
 */
public class SearchListener {
    
    private PnlSearch pnlSearch = null;
    
    private int searchResultCount = 0;
    
    public SearchListener(PnlSearch pnlSearch) {
        this.pnlSearch = pnlSearch;
    }
    
    public void searchResult(final SearchResult searchResult) {
        searchResultCount++;
       
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                pnlSearch.addSearchResult(searchResult);
            }
        });
    }
    
    public void searchPathChanged(final String path) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                pnlSearch.updateSearchResultSummary(path);
            }
        });
    }
    
    public void searchError(Exception e) {
        AppMain.Error(pnlSearch, "Failed to perform search due to:\n"+
                e.getMessage(), "Search Error");
    }
    
    public void searchCompleted() {
        LogManager.debug("Search completed successfully and returned " + 
                searchResultCount + " results");
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                pnlSearch.updateSearchCompleted();
            }
        });
    }
}
