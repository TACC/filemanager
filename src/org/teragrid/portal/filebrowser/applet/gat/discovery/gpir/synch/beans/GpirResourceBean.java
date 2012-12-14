package org.teragrid.portal.filebrowser.applet.gat.discovery.gpir.synch.beans;


/**
 * GpirResourceBean.java
 *
 *
 * Created: Wed Nov 24 17:22:06 2004
 *
 * @author <a href="mailto:ericrobe@w-prc-251-118.public.utexas.edu"></a>
 * @version 1.0
 */
public class GpirResourceBean {
    
    String name;
    String resourceType;
    String system;
    String hostname;
    String status;
    String departmentName;
    String departmentUrl;
    String institutionName;
    String institutionUrl;
    String processors;
    String processorsSum;
    String memory;
    String memorySum;
    String nodes;
    String performance;
    String performanceSum;
    String scratchDisk;
    String scratchDiskSum;
    String load;
    String jobsRunning;
    String jobsQueued;
    String jobsOther;
    String peakPolygons;
    String graphicsHardware;
    String onlineStorage;
    String offlineStorage;
    
    //PCGrid
    String pcGridHostname;
    String pcGridCenter;
    String pcGridPlatform;
    String pcGridSystem;
    String pcGridTotalPC;
    String pcGridActivePC;
    String pcGridTotalProcessors;
    String pcGridActiveProcessors;
    String pcGridMemory;
    String pcGridDisk;
    String pcGridStatus;
    
    public static final String COMPUTE = "compute";
    public static final String STORAGE = "storage";
    public static final String VISUALIZATION = "visualization";
    public static final String UNITED_DEVICES = "unitedDevices";
    public static final String CONDOR = "condor";

    public static final String LESS_THAN_ONE = "< 1";

    // empty default constructor
    public GpirResourceBean() {

    }

    /**
     * Creates a new <code>GpirResourceBean</code> instance.
     *
     * @param resourceType a <code>String</code> value
     */
    public GpirResourceBean(String resourceType) {
        this.resourceType = resourceType;
    }

    /**
     * Get the OfflineStorage value.
     * @return the OfflineStorage value.
     */
    public String getOfflineStorage() {
        return offlineStorage;
    }

    /**
     * Set the OfflineStorage value.
     * @param newOfflineStorage The new OfflineStorage value.
     */
    public void setOfflineStorage(String newOfflineStorage) {
        if(newOfflineStorage == null || newOfflineStorage.equals("")) {
            this.offlineStorage = LESS_THAN_ONE;
        } else {
            this.offlineStorage = newOfflineStorage;
        }
    }
    
    /**
     * Get the OnlineStorage value.
     * @return the OnlineStorage value.
     */
    public String getOnlineStorage() {
        return onlineStorage;
    }

    /**
     * Set the OnlineStorage value.
     * @param newOnlineStorage The new OnlineStorage value.
     */
    public void setOnlineStorage(String newOnlineStorage) {
        if(newOnlineStorage == null || newOnlineStorage.equals("")) {
            this.onlineStorage = LESS_THAN_ONE;
        } else {
            this.onlineStorage = newOnlineStorage;
        }
    }
    
    /**
     * Get the PeakPolygons value.
     * @return the PeakPolygons value.
     */
    public String getPeakPolygons() {
        return peakPolygons;
    }

    /**
     * Set the PeakPolygons value.
     * @param newPeakPolygons The new PeakPolygons value.
     */
    public void setPeakPolygons(String newPeakPolygons) {
        if ( newPeakPolygons == null || newPeakPolygons.equals("")) {
            this.peakPolygons = LESS_THAN_ONE;
        } else {
        this.peakPolygons = newPeakPolygons;
        }
    }
    
    /**
     * Get the JobsOther value.
     * @return the JobsOther value.
     */
    public String getJobsOther() {
        return jobsOther;
    }

    /**
     * Set the JobsOther value.
     * @param newJobsOther The new JobsOther value.
     */
    public void setJobsOther(String newJobsOther) {
        this.jobsOther = newJobsOther;
    }
    
    /**
     * Get the JobsQueued value.
     * @return the JobsQueued value.
     */
    public String getJobsQueued() {
        return jobsQueued;
    }

    /**
     * Set the JobsQueued value.
     * @param newJobsQueued The new JobsQueued value.
     */
    public void setJobsQueued(String newJobsQueued) {
        this.jobsQueued = newJobsQueued;
    }
    
    /**
     * Get the JobsRunning value.
     * @return the JobsRunning value.
     */
    public String getJobsRunning() {
        return jobsRunning;
    }

    /**
     * Set the JobsRunning value.
     * @param newJobsRunning The new JobsRunning value.
     */
    public void setJobsRunning(String newJobsRunning) {
        this.jobsRunning = newJobsRunning;
    }
    
    /**
     * Get the Load value.
     * @return the Load value.
     */
    public String getLoad() {
        return load;
    }

    /**
     * Set the Load value.
     * @param newLoad The new Load value.
     */
    public void setLoad(String newLoad) {
        this.load = newLoad;
    }
    
    /**
     * Get the ScratchDisk value.
     * @return the ScratchDisk value.
     */
    public String getScratchDisk() {
        return scratchDisk;
    }

    /**
     * Set the ScratchDisk value.
     * @param newScratchDisk The new ScratchDisk value.
     */
    public void setScratchDisk(String newScratchDisk) {
        if ( newScratchDisk == null || newScratchDisk.equals("")) {
            this.scratchDisk = LESS_THAN_ONE;
        } else {
            this.scratchDisk = newScratchDisk;
        }
    }
    
    /**
     * Get the Memory value.
     * @return the Memory value.
     */
    public String getMemory() {
        return memory;
    }

    /**
     * Set the Memory value.
     * @param newMemory The new Memory value.
     */
    public void setMemory(String newMemory) {
        if ( newMemory == null || newMemory.equals("")) {
            this.memory = LESS_THAN_ONE;
        } else {
            this.memory = newMemory;
        }
    }
    
    /**
     * Get the Performance value.
     * @return the Performance value.
     */
    public String getPerformance() {
        return performance;
    }

    /**
     * Set the Performance value.
     * @param newPerformance The new Performance value.
     */
    public void setPerformance(String newPerformance) {
        if ( newPerformance == null || newPerformance.equals("")) {
            this.performance = LESS_THAN_ONE;
        } else {
            this.performance = newPerformance;
        }
    }
    
    /**
     * Get the Processors value.
     * @return the Processors value.
     */
    public String getProcessors() {
        return processors;
    }

    /**
     * Set the Processors value.
     * @param newProcessors The new Processors value.
     */
    public void setProcessors(String newProcessors) {
        this.processors = newProcessors;
    }
    
    /**
     * Get the Nodes value.
     * @return the Nodes value.
     */
    public String getNodes() {
        return nodes;
    }

    /**
     * Set the Nodes value.
     * @param newNodes The new Nodes value.
     */
    public void setNodes(String newNodes) {
        this.nodes = newNodes;
    }

    /**
     * Get the InstitutionUrl value.
     * @return the InstitutionUrl value.
     */
    public String getInstitutionUrl() {
        return institutionUrl;
    }

    /**
     * Set the InstitutionUrl value.
     * @param newInstitutionUrl The new InstitutionUrl value.
     */
    public void setInstitutionUrl(String newInstitutionUrl) {
        this.institutionUrl = newInstitutionUrl;
    }

    /**
     * Get the InstitutionName value.
     * @return the InstitutionName value.
     */
    public String getInstitutionName() {
        return institutionName;
    }

    /**
     * Set the InstitutionName value.
     * @param newInstitutionName The new InstitutionName value.
     */
    public void setInstitutionName(String newInstitutionName) {
        this.institutionName = newInstitutionName;
    }

    /**
     * Get the DepartmentUrl; value.
     * @return the DepartmentUrl; value.
     */
    public String getDepartmentUrl() {
        return departmentUrl;
    }

    /**
     * Set the DepartmentUrl; value.
     * @param newDepartmentUrl; The new DepartmentUrl; value.
     */
    public void setDepartmentUrl(String newDepartmentUrl) {
        this.departmentUrl = newDepartmentUrl;
    }

    /**
     * Get the DepartmentName value.
     * @return the DepartmentName value.
     */
    public String getDepartmentName() {
        return departmentName;
    }

    /**
     * Set the DepartmentName value.
     * @param newDepartmentName The new DepartmentName value.
     */
    public void setDepartmentName(String newDepartmentName) {
        this.departmentName = newDepartmentName;
    }
    
    /**
     * Get the Status value.
     * @return the Status value.
     */
    public String getStatus() {
        return status;
    }

    /**
     * Set the Status value.
     * @param newStatus The new Status value.
     */
    public void setStatus(String newStatus) {
        this.status = newStatus;
    }

    /**
     * Get the Hostname value.
     * @return the Hostname value.
     */
    public String getHostname() {
        return hostname;
    }

    /**
     * Set the Hostname value.
     * @param newHostname The new Hostname value.
     */
    public void setHostname(String newHostname) {
        this.hostname = newHostname;
    }

    /**
     * Get the System value.
     * @return the System value.
     */
    public String getSystem() {
        return system;
    }

    /**
     * Set the System value.
     * @param newSystem The new System value.
     */
    public void setSystem(String newSystem) {
        this.system = newSystem;
    }

    /**
     * Get the Type value.
     * @return the Type value.
     */
    public String getResourceType() {
        return resourceType;
    }

    /**
     * Set the Type value.
     * @param newType The new Type value.
     */
    public void setResourceType(String newResourceType) {
        this.resourceType = newResourceType;
    }

    /**
     * Get the Name value.
     * @return the Name value.
     */
    public String getName() {
        return name;
    }

    /**
     * Set the Name value.
     * @param newName The new Name value.
     */
    public void setName(String newName) {
        this.name = newName;
    }

    public String getPCGridCenter() {
        return pcGridCenter;
    }

    public void setPCGridCenter(String pcGridCenter) {
        this.pcGridCenter = pcGridCenter;
    }

    public void setPCGridPlatform(String pcGridPlatform) {
        this.pcGridPlatform = pcGridPlatform;
    }

    public String getPCGridPlatform() {
        return pcGridPlatform;
    }

    public String getPCGridTotalPC() {
        return pcGridTotalPC;
    }

    public void setPCGridTotalPC(String pcGridTotalPC) {
        this.pcGridTotalPC = pcGridTotalPC;
    }

    public String getPCGridActivePC() {
        return pcGridActivePC;
    }

    public void setPCGridActivePC(String pcGridActivePC) {
        this.pcGridActivePC = pcGridActivePC;
    }

    public String getPCGridActiveProcessors() {
        return pcGridActiveProcessors;
    }

    public void setPCGridActiveProcessors(String pcGridActiveProcessors) {
        this.pcGridActiveProcessors = pcGridActiveProcessors;
    }

    public String getPCGridTotalProcessors() {
        return pcGridTotalProcessors;
    }

    public void setPCGridTotalProcessors(String pcGridTotalProcessors) {
        this.pcGridTotalProcessors = pcGridTotalProcessors;
    }

    public String getPCGridMemory() {
        return pcGridMemory;
    }

    public void setPCGridMemory(String pcGridMemory) {
        this.pcGridMemory = pcGridMemory;
    }

    public String getPCGridDisk() {
        return pcGridDisk;
    }

    public void setPCGridDisk(String pcGridDisk) {
        this.pcGridDisk = pcGridDisk;
    }

    public String getPCGridSystem() {
        return pcGridSystem;
    }

    public void setPCGridSystem(String pcGridSystem) {
        this.pcGridSystem = pcGridSystem;
    }

    public String getPCGridStatus() {
        return pcGridStatus;
    }

    public void setPCGridStatus(String pcGridStatus) {
        this.pcGridStatus = pcGridStatus;
    }

    public String getMemorySum() {
        return memorySum;
    }

    public void setMemorySum(String memorySum) {
        this.memorySum = memorySum;
    }

    public String getPerformanceSum() {
        return performanceSum;
    }

    public void setPerformanceSum(String performanceSum) {
        this.performanceSum = performanceSum;
    }

    public String getScratchDiskSum() {
        return scratchDiskSum;
    }

    public void setScratchDiskSum(String scratchDiskSum) {
        this.scratchDiskSum = scratchDiskSum;
    }

    public String getProcessorsSum() {
        return processorsSum;
    }

    public void setProcessorsSum(String processorsSum) {
        this.processorsSum = processorsSum;
    }

    public String getGraphicsHardware() {
        return graphicsHardware;
    }

    public void setGraphicsHardware(String graphicsHardware) {
        this.graphicsHardware = graphicsHardware;
    }

    public String getPCGridHostname() {
        return pcGridHostname;
    }

    public void setPCGridHostname(String pcGridHostname) {
        this.pcGridHostname = pcGridHostname;
    }

    
}
