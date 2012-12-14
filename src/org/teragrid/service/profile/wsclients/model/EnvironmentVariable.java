/* 
 * Created on Aug 16, 2008
 * 
 * Developed by: Rion Dooley - dooley [at] tacc [dot] utexas [dot] edu
 * 				 TACC, Texas Advanced Computing Center
 * 
 * https://www.tacc.utexas.edu/
 */

package org.teragrid.service.profile.wsclients.model;

import java.io.IOException;
import java.io.Serializable;

/**
 * Class to hold environment variable information
 * 
 * @author Rion Dooley < dooley [at] tacc [dot] utexas [dot] edu >
 *
 */
@SuppressWarnings("unchecked")
public class EnvironmentVariable implements Serializable, Comparable {
    private static final long serialVersionUID = 1L;

    private String name;
    private String value;
    
    public EnvironmentVariable() {}
    
    public EnvironmentVariable(String name, String value) {
        this.name = name;
        this.value = value;
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
            throw new IOException("Environment variables must have a non-empty name");
        }
        this.name = name;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value the value to set
     * @throws IOException 
     */
    public void setValue(String value) throws IOException {
        this.value = value;
    }

    public String toString() {
        return name + " " + value; 
        
    }
    
    public boolean equals(Object o) {
        if (o instanceof EnvironmentVariable)  {
            return ((EnvironmentVariable)o).name.equals(name) && 
                ((EnvironmentVariable)o).value.equals(value); 
        }
        
        return false;
    }
    
   public int compareTo(Object o) {
		if (o instanceof EnvironmentVariable)  {
            return (-1 * ((EnvironmentVariable)o).name.compareTo(name)); 
        }
        
        return 0;
	}
    
}
