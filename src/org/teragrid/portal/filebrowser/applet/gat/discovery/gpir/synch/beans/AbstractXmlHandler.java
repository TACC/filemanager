/*
 * AbstractXmlHandler.java
 *
 * Created on April 19, 2005, 2:13 PM
 */

package org.teragrid.portal.filebrowser.applet.gat.discovery.gpir.synch.beans;


/**
 *
 * @author ericrobe
 */
public abstract class AbstractXmlHandler implements XmlHandler{
    //ResourceParser parser;
    GPIRCollection beans; 
    String strXml;
    
    /** Creates a new instance of AbstractXmlHandler */
    public AbstractXmlHandler(String xml) {
        this.strXml = xml;
        beans = null; 
    }
    
    public GPIRCollection getBeans() { 
        return beans;
    }
    
    
}
