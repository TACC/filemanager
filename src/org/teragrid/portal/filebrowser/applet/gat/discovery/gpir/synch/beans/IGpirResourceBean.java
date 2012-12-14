package org.teragrid.portal.filebrowser.applet.gat.discovery.gpir.synch.beans;

public interface IGpirResourceBean {

    void setResourceType(String resourceType);
    String getResourceType();
    void setName(String name);
    String getName();
    void setSystem(String system);
    String getSystem();
    void setHostname(String hostname);
    String getHostname();
    void setStatus(String status);
    String getStatus();
    void setDepartmentName(String departmentName);
    String getDepartmentName();
    void setDepartmentUrl(String departmentUrl);
    String getDepartmentUrl();
    void setInstitutionName(String institutionName);
    String getInstitutionName();
    void setInstitutionUrl(String institutionUrl);
    String getInstitutionUrl();
}
