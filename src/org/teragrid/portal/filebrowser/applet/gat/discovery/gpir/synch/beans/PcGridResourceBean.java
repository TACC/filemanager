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
public class PcGridResourceBean extends AbstractResourceBean {   
    
    private String hostname;
    private String center;
    private String platform;
    private String system;
    private String totalPc;
    private String activePc;
    private String totalCpu;
    private String activeCpu;
    private String totalMemory;
    private String totalDisk;
    private String status;
    
    /** Creates a new instance of PcGridResourceBean */
    public PcGridResourceBean() { 
        setResourceType(GpirProperties.PC_GRID); 
    }

    public PcGridResourceBean(AbstractResourceBean aBean) {
        resourceType = GpirProperties.PC_GRID;
        hostname = aBean.getHostname();
        name = aBean.getName();
        status = aBean.getStatus();
        system = aBean.getSystem();
        departmentName = aBean.getDepartmentName();
        departmentUrl = aBean.getDepartmentUrl();
        institutionName = aBean.getInstitutionName();
        institutionUrl = aBean.getInstitutionUrl();
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getCenter() {
        return center;
    }

    public void setCenter(String center) {
        this.center = center;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getSystem() {
        return system;
    }

    public void setSystem(String system) {
        this.system = system;
    }

    public String getTotalPc() {
        return totalPc;
    }

    public void setTotalPc(String totalPc) {
        this.totalPc = totalPc;
    }

    public String getActivePc() {
        return activePc;
    }

    public void setActivePc(String activePc) {
        this.activePc = activePc;
    }

    public String getTotalCpu() {
        return totalCpu;
    }

    public void setTotalCpu(String totalCpu) {
        this.totalCpu = totalCpu;
    }

    public String getActiveCpu() {
        return activeCpu;
    }

    public void setActiveCpu(String activeCpu) {
        this.activeCpu = activeCpu;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTotalMemory() {
        return totalMemory;
    }

    public void setTotalMemory(String totalMemory) {
        this.totalMemory = totalMemory;
    }

    public String getTotalDisk() {
        return totalDisk;
    }

    public void setTotalDisk(String totalDisk) {
        this.totalDisk = totalDisk;
    }
    
   

       
}
