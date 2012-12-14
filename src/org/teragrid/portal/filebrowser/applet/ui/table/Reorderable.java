package org.teragrid.portal.filebrowser.applet.ui.table;

/**
 * Interface to allow for reordering of table rows
 * 
 * @author dooley
 *
 */
public interface Reorderable {
   public void reorder(int fromIndex, int toIndex);
}