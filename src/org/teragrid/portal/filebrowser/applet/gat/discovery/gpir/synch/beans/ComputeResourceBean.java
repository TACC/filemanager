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
public class ComputeResourceBean extends AbstractResourceBean {   
    
    private String processors;
    private String processorsSum;
    private String memory;
    private String memorySum;
    private String nodes;
    private String performance;
    private String performanceSum;
    private String scratchDisk;
    private String scratchDiskSum;
    private String load;
    private String jobsRunning;
    private String jobsQueued;
    private String jobsOther;
    private String institutionName;
    private String insitutionUrl;
    
    /** Creates a new instance of ComputeResourceBean */
    public ComputeResourceBean() {
        setResourceType(GpirProperties.COMPUTE);
        jobsRunning = "0";
        jobsQueued = "0";
        jobsOther = "0";
    }
    
    public ComputeResourceBean(AbstractResourceBean aBean) {
        resourceType = GpirProperties.COMPUTE;
        hostname = aBean.getHostname();
        name = aBean.getName();
        status = aBean.getStatus();
        system = aBean.getSystem();
        departmentName = aBean.getDepartmentName();
        departmentUrl = aBean.getDepartmentUrl();
        institutionName = aBean.getInstitutionName();
        institutionUrl = aBean.getInstitutionUrl();
    }

    public String getProcessors() {
        return processors;
    }

    public void setProcessors(String processors) {
        this.processors = processors;
    }

    public String getProcessorsSum() {
        return processorsSum;
    }

    public void setProcessorsSum(String processorsSum) {
        this.processorsSum = processorsSum;
    }

    public String getMemory() {
        return memory;
    }

    public void setMemory(String memory) {
        this.memory = memory;
    }

    public String getMemorySum() {
        return memorySum;
    }

    public void setMemorySum(String memorySum) {
        this.memorySum = memorySum;
    }

    public String getNodes() {
        return nodes;
    }

    public void setNodes(String nodes) {
        this.nodes = nodes;
    }

    public String getPerformance() {
        return performance;
    }

    public void setPerformance(String performance) {
        this.performance = performance;
    }

    public String getPerformanceSum() {
        return performanceSum;
    }

    public void setPerformanceSum(String performanceSum) {
        this.performanceSum = performanceSum;
    }

    public String getScratchDisk() {
        return scratchDisk;
    }

    public void setScratchDisk(String scratchDisk) {
        this.scratchDisk = scratchDisk;
    }

    public String getScratchDiskSum() {
        return scratchDiskSum;
    }

    public void setScratchDiskSum(String scratchDiskSum) {
        this.scratchDiskSum = scratchDiskSum;
    }

    public String getLoad() {
        return load;
    }

    public void setLoad(String load) {
        this.load = load;
    }

    public String getJobsRunning() {
        return jobsRunning;
    }

    public void setJobsRunning(String jobsRunning) {
        this.jobsRunning = jobsRunning;
    }

    public String getJobsQueued() {
        return jobsQueued;
    }

    public void setJobsQueued(String jobsQueued) {
        this.jobsQueued = jobsQueued;
    }

    public String getJobsOther() {
        return jobsOther;
    }

    public void setJobsOther(String jobsOther) {
        this.jobsOther = jobsOther;
    }

    public String getInstitutionName() {
        return institutionName;
    }

    public void setInstitutionName(String institutionName) {
        this.institutionName = institutionName;
    }

    public String getInsitutionUrl() {
        return insitutionUrl;
    }

    public void setInsitutionUrl(String insitutionUrl) {
        this.insitutionUrl = insitutionUrl;
    }

    
}
