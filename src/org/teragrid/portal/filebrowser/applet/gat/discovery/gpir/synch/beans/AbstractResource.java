/*
 * GpirResource.java
 *
 * Created on April 19, 2005, 3:40 PM
 */

package org.teragrid.portal.filebrowser.applet.gat.discovery.gpir.synch.beans;

import org.jdom.Element;

/**
 *
 * @author ericrobe
 */
public abstract class AbstractResource {
    GenericParser parser;
    AbstractResourceBean bean;
    
    abstract void build(Element element);
    
    public AbstractResourceBean getBean() {
        return bean;
    }
    
}
