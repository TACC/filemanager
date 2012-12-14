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
public class VizResourceBean extends AbstractResourceBean {
    
    private String processors;
    private String performance;
    private String memory;
    private String scratchDisk;
    private String peakPolygons;
    private String graphicsHardware;
    
    /** Creates a new instance of ComputeResourceBean */
    public VizResourceBean() {
        resourceType = GpirProperties.VIZ;
    }
    
    public VizResourceBean(AbstractResourceBean aBean) {
        resourceType = GpirProperties.VIZ;
        hostname = aBean.getHostname();
        name = aBean.getName();
        status = aBean.getStatus();
        system = aBean.getSystem();
        departmentName = aBean.getDepartmentName();
        departmentUrl = aBean.getDepartmentUrl();
        institutionName = aBean.getInstitutionName();
        institutionUrl = aBean.getInstitutionUrl();
    }

    public String getPeakPolygons() {
        return peakPolygons;
    }

    public void setPeakPolygons(String peakPolygons) {
        this.peakPolygons = peakPolygons;
    }

    public String getGraphicsHardware() {
        return graphicsHardware;
    }

    public void setGraphicsHardware(String graphicsHardware) {
        this.graphicsHardware = graphicsHardware;
    }

    public String getProcessors() {
        return processors;
    }

    public void setProcessors(String processors) {
        this.processors = processors;
    }

    public String getPerformance() {
        return performance;
    }

    public void setPerformance(String performance) {
        this.performance = performance;
    }

    public String getMemory() {
        return memory;
    }

    public void setMemory(String memory) {
        this.memory = memory;
    }

    public String getScratchDisk() {
        return scratchDisk;
    }

    public void setScratchDisk(String scratchDisk) {
        this.scratchDisk = scratchDisk;
    }
    
   
}
