/*
 * ResourceBean.java
 *
 * Created on April 19, 2005, 2:17 PM
 */

package org.teragrid.portal.filebrowser.applet.gat.discovery.gpir.synch.beans;

/**
 *
 * @author ericrobe
 */
public class AbstractResourceBean implements GPIRBean{
    
    protected String resourceType;
    protected String name;
    protected String status;
    protected String hostname;
    protected String system;
    protected String departmentName;
    protected String departmentUrl;
    protected String institutionName;
    protected String institutionUrl;
    
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("[resourceType: " + resourceType + "],");
        sb.append("[name: " + name + "],");
        sb.append("[status: " + status + "],");
        sb.append("[hostname: " + hostname + "],");
        return sb.toString();
    }
    
    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSystem() {
        return system;
    }

    public void setSystem(String system) {
        this.system = system;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getDepartmentUrl() {
        return departmentUrl;
    }

    public void setDepartmentUrl(String departmentUrl) {
        this.departmentUrl = departmentUrl;
    }

    public String getInstitutionName() {
        return institutionName;
    }

    public void setInstitutionName(String institutionName) {
        this.institutionName = institutionName;
    }

    public String getInstitutionUrl() {
        return institutionUrl;
    }

    public void setInstitutionUrl(String institutionUrl) {
        this.institutionUrl = institutionUrl;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }
    
}
