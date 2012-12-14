/*
 * ComputeResourceBean.java
 *
 * Created on April 19, 2005, 2:37 PM
 */

package org.teragrid.portal.filebrowser.applet.gat.discovery.gpir.synch.beans;

/**
 *
 * @author ericrobe
 */
public class StorageResourceBean extends AbstractResourceBean {
    
    private String onlineStorage;
    private String offlineStorage;
    
    /** Creates a new instance of StorageResourceBean */
    public StorageResourceBean() {
        resourceType = GpirProperties.STORAGE;  
    }
    
    public StorageResourceBean(AbstractResourceBean aBean) {
        resourceType = GpirProperties.STORAGE;
        hostname = aBean.getHostname();
        name = aBean.getName();
        status = aBean.getStatus();
        system = aBean.getSystem();
        departmentName = aBean.getDepartmentName();
        departmentUrl = aBean.getDepartmentUrl();
        institutionName = aBean.getInstitutionName();
        institutionUrl = aBean.getInstitutionUrl();
    }

    public String getOnlineStorage() {
        return onlineStorage;
    }

    public void setOnlineStorage(String onlineStorage) {
        this.onlineStorage = onlineStorage;
    }

    public String getOfflineStorage() {
        return offlineStorage;
    }

    public void setOfflineStorage(String offlineStorage) {
        this.offlineStorage = offlineStorage;
    }
    
    
}
