/*
 * ResourceBeanFactoryImpl.java
 *
 * Created on April 19, 2005, 2:59 PM
 */

package org.teragrid.portal.filebrowser.applet.gat.discovery.gpir.synch.beans;

import org.jdom.Element;
/**
 *
 * @author ericrobe
 */
public class StorageResourceFactory implements ResourceFactory {
    
    public AbstractResourceBean createBean() {
        return new StorageResourceBean();
    }
    /*
    public ResourceParser createParser() {
        return new StorageResourceParser();
    }
    */
    
    public GenericParser createParser(Element element) {
        return new StorageResourceParser(element); 
    }
         
    
}
