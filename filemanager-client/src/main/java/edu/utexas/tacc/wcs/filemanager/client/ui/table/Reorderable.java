package edu.utexas.tacc.wcs.filemanager.client.ui.table;

/**
 * Interface to allow for reordering of table rows
 * 
 * @author dooley
 *
 */
public interface Reorderable {
   public void reorder(int fromIndex, int toIndex);
}