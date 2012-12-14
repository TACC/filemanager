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
public class PcGridResourceFactory implements ResourceFactory {
    
    public AbstractResourceBean createBean() {
        return new PcGridResourceBean(); 
    }
    /*
    public ResourceParser createParser() {
        return new PcGridResourceParser();   
    }
    */
    public GenericParser createParser(Element element) {
        return new PcGridResourceParser(element); 
    }
    
    //static method create() would return a new ComputeResource
    // could then get rid of createBean and createParser since a Resource
    //type should encapsulate the parsing and creation of beans
         
    
}
