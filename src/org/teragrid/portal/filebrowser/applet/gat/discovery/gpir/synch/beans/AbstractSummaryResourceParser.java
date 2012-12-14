/*
 * ResourceParser.java
 *
 * Created on April 19, 2005, 2:16 PM
 */

package org.teragrid.portal.filebrowser.applet.gat.discovery.gpir.synch.beans;

import org.jdom.Element;
import org.jdom.Namespace;

/**
 *
 * @author ericrobe
 */
public abstract class AbstractSummaryResourceParser implements GenericParser {
    Element elResource;
    Namespace nsResource;
    AbstractResourceBean bean;
    
    public AbstractSummaryResourceParser() {
        
    }
    
    public AbstractSummaryResourceParser(Element element) {
        elResource = element;
        bean = new AbstractResourceBean();
        handleHostname();
        handleStatus();
        handleName();
        handleSystem();
        handleDepartment();
        handleInstitution();
    }
    
    public abstract void parse(Element element);
    public abstract AbstractResourceBean getBean();
    
    protected void handleHostname() {
        bean.setHostname(elResource.getAttribute("hostname").getValue());
    }
    
    protected void handleStatus() {
        bean.setStatus(elResource.getAttribute("status").getValue());
    }
    
    protected void handleName() {
        bean.setName(elResource.getChildTextTrim("Key"));
    }
    
    protected void handleSystem() {
        bean.setSystem(elResource.getChildTextTrim("System"));
    }
    
    protected void handleDepartment() {
        Element elDepartment = elResource.getChild("Department", nsResource);
        Namespace nsDepartment = elDepartment.getNamespace();
        bean.setDepartmentName(elDepartment.getChildTextTrim("Name", nsDepartment));
        bean.setDepartmentUrl(elDepartment.getChildTextTrim("Url", nsDepartment));
    }
    
    protected void handleInstitution() {
        Element elInstitution = elResource.getChild("Institution", nsResource);
        Namespace nsInstitution = elInstitution.getNamespace();
        bean.setInstitutionName(elInstitution.getChildTextTrim("Name", nsInstitution));
        bean.setInstitutionUrl(elInstitution.getChildTextTrim("Url", nsInstitution));
    }
    
}
