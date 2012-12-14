/*
 * SummaryResourceCollection.java
 *
 * Created on April 26, 2005, 11:00 AM
 */

package org.teragrid.portal.filebrowser.applet.gat.discovery.gpir.synch.beans;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ericrobe
 */
@SuppressWarnings("unchecked")
public class ResourceList implements ResourceCollection {
    ArrayList list;
    
    /** Creates a new instance of SummaryResourceCollection */
    public ResourceList() {
        list = new ArrayList();
    }
    
    public List get(String type) {
        ArrayList myBeans = new ArrayList();
        for(int i = 0; i < list.size(); i++) {
            AbstractResourceBean bean = (AbstractResourceBean)list.get(i);
            if(bean.getResourceType().equals(type)) {
                myBeans.add(bean);
            }
        }
        return myBeans;
    }
    
    public boolean put(GPIRBean bean) {
        return list.add(bean);
    }
    
    public int size() {
        return list.size();
    }
    
    public List getAll() {
        return list;
    }
}
