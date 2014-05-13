/* 
 * Created on Aug 16, 2008
 * 
 * Developed by: Rion Dooley - dooley [at] tacc [dot] utexas [dot] edu
 * 				 TACC, Texas Advanced Computing Center
 * 
 * https://www.tacc.utexas.edu/
 */

package edu.utexas.tacc.wcs.filemanager.client.ui;

import java.io.IOException;
import java.io.Serializable;
import java.util.Date;

/**
 * Class to hold bookmark information
 * 
 * @author Rion Dooley < dooley [at] tacc [dot] utexas [dot] edu >
 *
 */
public class Bookmark implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private String path;
    private Date created = new Date();
    
    public Bookmark() {}
    
    public Bookmark(String name, String path) {
        this.name = name;
        this.path = path;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     * @throws IOException 
     */
    public void setName(String name) throws IOException {
        if (name == null || name.equals("")) {
            throw new IOException("Bookmark must have a non-empty name");
        }
        this.name = name;
    }

    /**
     * @return the path
     */
    public String getPath() {
        return path;
    }

    /**
     * @param path the path to set
     * @throws IOException 
     */
    public void setPath(String path) throws IOException {
        if (path == null || path.equals("")) {
            throw new IOException("Bookmark must have a non-empty path");
        }
        this.path = path;
    }

    /**
     * @return the created
     */
    public Date getCreated() {
        return created;
    }

    /**
     * @param created the created to set
     */
    public void setCreated(Date created) {
        this.created = created;
    }
    
    public String toString() {
        return name + " " + path; 
        
    }
    
    public boolean equals(Object o) {
        if (o instanceof Bookmark)  {
//            return ((Bookmark)o).name.equals(name) && 
//                ((Bookmark)o).path.equals(path) && 
                return ((Bookmark)o).created.equals(created);
        }
        
        return false;
    }
    
}
