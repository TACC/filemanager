package org.teragrid.portal.filebrowser.applet.gat.discovery.gpir.synch.beans;

public class GpirSumBean {
    private int totalMemory;
    private int totalScratchDisk;
    private int totalPerformance;
    private int totalProcessors;
    
    private String totalPCGridActivePCs;
    private String totalPCGridActiveProcessors;
    private int totalPCGridMemory;
    private int totalPCGridDisk;
    
    private int totalPeakPolygons;
    
    private int totalOnlineStorage;
    private int totalOfflineStorage;
    
    public GpirSumBean() {
        
    }
    
    public String getTotalPCGridActivePCs() {
        return totalPCGridActivePCs;
    }
    
    public void setTotalPCGridActivePCs(String totalPCGridActivePCs) {
        this.totalPCGridActivePCs = totalPCGridActivePCs;
    }
    
    public String getTotalPCGridActiveProcessors() {
        return totalPCGridActiveProcessors;
    }
    
    public void setTotalPCGridActiveProcessors(String totalPCGridActiveProcessors) {
        this.totalPCGridActiveProcessors = totalPCGridActiveProcessors;
    }
    
    public int getTotalPCGridMemory() {
        return totalPCGridMemory;
    }
    
    public void setTotalPCGridMemory(int totalPCGridMemory) {
        this.totalPCGridMemory = totalPCGridMemory;
    }
    
    public int getTotalPCGridDisk() {
        return totalPCGridDisk;
    }
    
    public void setTotalPCGridDisk(int totalPCGridDisk) {
        this.totalPCGridDisk = totalPCGridDisk;
    }

    public int getTotalMemory() {
        return totalMemory;
    }

    public void setTotalMemory(int totalMemory) {
        this.totalMemory = totalMemory;
    }

    public int getTotalScratchDisk() {
        return totalScratchDisk;
    }

    public void setTotalScratchDisk(int totalScratchDisk) {
        this.totalScratchDisk = totalScratchDisk;
    }

    public int getTotalPerformance() {
        return totalPerformance;
    }

    public void setTotalPerformance(int totalPerformance) {
        this.totalPerformance = totalPerformance;
    }

    public int getTotalProcessors() {
        return totalProcessors;
    }

    public void setTotalProcessors(int totalProcessors) {
        this.totalProcessors = totalProcessors;
    }

    public int getTotalPeakPolygons() {
        return totalPeakPolygons;
    }

    public void setTotalPeakPolygons(int totalPeakPolygons) {
        this.totalPeakPolygons = totalPeakPolygons;
    }

    public int getTotalOnlineStorage() {
        return totalOnlineStorage;
    }

    public void setTotalOnlineStorage(int totalOnlineStorage) {
        this.totalOnlineStorage = totalOnlineStorage;
    }

    public int getTotalOfflineStorage() {
        return totalOfflineStorage;
    }

    public void setTotalOfflineStorage(int totalOfflineStorage) {
        this.totalOfflineStorage = totalOfflineStorage;
    }
    
    
}
