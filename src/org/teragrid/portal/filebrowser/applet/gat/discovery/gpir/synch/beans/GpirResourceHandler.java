package org.teragrid.portal.filebrowser.applet.gat.discovery.gpir.synch.beans;


import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;

/**
 * GpirResourceHandler.java
 *
 *
 * Created: Wed Nov 24 22:23:41 2004
 *
 * @author <a href="mailto:ericrobe@localhost.localdomain"></a>
 * @version 1.0
 */
@SuppressWarnings("unchecked")
public class GpirResourceHandler {
    
    Document doc;
    Element elRoot;
    Namespace nsRoot;
    List elComputeResourceList;
    Element elComputeResource;
    Namespace nsComputeResource;
    ArrayList gpirComputeBeans = new ArrayList();
    ArrayList gpirStorageBeans = new ArrayList();
    ArrayList gpirVisualizationBeans = new ArrayList();
    ArrayList gpirUnitedDevicesBeans = new ArrayList();
    ArrayList gpirCondorBeans = new ArrayList();
    GpirResourceBean currentBean;
    final String SUMMARY = "Summary";
    final String PC_GRID_DEVICE_GROUPS = "RoundupResourceDeviceGroups";
    final String PC_GRID_TYPE = "pcGrid";
    final String UNITED_DEVICES_ATTRIBUTE = "frio.tacc.utexas.edu";
    final String UNITED_DEVICES_TYPE = "unitedDevices";
    final String CONDOR_ATTRIBUTE = "lela.tacc.utexas.edu";
    final String CONDOR_TYPE = "condor";
    
    public GpirResourceHandler(String theXml) throws JDOMException, IOException {
        SAXBuilder builder = new SAXBuilder();
        StringReader xmlResultStream = new StringReader(theXml);
        doc = builder.build(xmlResultStream);
        
        elRoot = doc.getRootElement();
        nsRoot = elRoot.getNamespace();
        if(elRoot.getName().equals(SUMMARY)) {
            elComputeResourceList = elRoot.getChildren("ComputeResource", nsRoot);
        } else if(elRoot.getName().equals(PC_GRID_DEVICE_GROUPS)) {
            elComputeResourceList = elRoot.getChildren("PCGridGroups", nsRoot);
        }
        
    } // GpirResourceHandler constructor
    
    public void iterate() {
        Iterator crIter = elComputeResourceList.iterator();
        
        while (crIter.hasNext()) {
            elComputeResource = (Element) crIter.next();
            nsComputeResource = elComputeResource.getNamespace();
            if(getResourceType().equals(GpirResourceBean.COMPUTE)) {
                currentBean = new GpirResourceBean(GpirResourceBean.COMPUTE);
                handleComputeResource();
                gpirComputeBeans.add(currentBean);
            } else if(getResourceType().equals(GpirResourceBean.STORAGE)) {
                currentBean = new GpirResourceBean(GpirResourceBean.STORAGE);
                handleStorageResource();
                gpirStorageBeans.add(currentBean);
            } else if(getResourceType().equals(GpirResourceBean.VISUALIZATION)) {
                currentBean = new GpirResourceBean(GpirResourceBean.VISUALIZATION);
                handleVisualizationResource();
                gpirVisualizationBeans.add(currentBean);
            } else if(getResourceType().equals(GpirResourceBean.UNITED_DEVICES)) {
                List pcGridGroups = elComputeResource.getChildren("PCGridGroup", nsRoot);
                Iterator pcGridGroupItr = pcGridGroups.iterator();
                while (pcGridGroupItr.hasNext()) {
                    Element pcGridGroup = (Element)pcGridGroupItr.next();
                    List elAggregateDataList = pcGridGroup.getChildren("AggregateData");
                    Iterator it = elAggregateDataList.iterator();
                    while(it.hasNext()) {
                        Element elAggregateData = (Element)it.next();
                        if(!elAggregateData.getChildTextTrim("TotalPc").equals("0")) {
                            currentBean = new GpirResourceBean(GpirResourceBean.UNITED_DEVICES);
                            handlePCGridResource(elAggregateData);
                            gpirUnitedDevicesBeans.add(currentBean);
                        }
                    }
                }
            } else if(getResourceType().equals(GpirResourceBean.CONDOR)) {
                List pcGridGroups = elComputeResource.getChildren("PCGridGroup", nsRoot);
                Iterator pcGridGroupItr = pcGridGroups.iterator();
                while (pcGridGroupItr.hasNext()) {
                    Element pcGridGroup = (Element)pcGridGroupItr.next();
                    List elAggregateDataList = pcGridGroup.getChildren("AggregateData");
                    Iterator it = elAggregateDataList.iterator();
                    while(it.hasNext()) {
                        Element elAggregateData = (Element)it.next();
                        if(!elAggregateData.getChildTextTrim("TotalPc").equals("0")) {
                            currentBean = new GpirResourceBean(GpirResourceBean.CONDOR);
                            handlePCGridResource(elAggregateData);
                            gpirCondorBeans.add(currentBean);
                        }
                    }
                }
            }
        }
    }
    
    public void handleComputeResource() {
        handleHostname();
        handleStatus();
        handleName();
        handleSystem();
        handleDepartment();
        handleInstitution();
        handleNodes();
        handleProcessors();
        handlePerformance();
        handleMemory();
        handleScratchDisk();
        handleLoad();
        handleJobs();
    }
    
    public void handleStorageResource() {
        handleHostname();
        handleStatus();
        handleName();
        handleSystem();
        handleDepartment();
        handleInstitution();
        handleProcessors();
        handlePerformance();
        handleMemory();
        handleScratchDisk();
        handleLoad();
        handleOnlineStorage();
        handleOfflineStorage();
    }
    
    public void handleVisualizationResource() {
        handleHostname();
        handleStatus();
        handleName();
        handleSystem();
        handleDepartment();
        handleInstitution();
        handleProcessors();
        handlePerformance();
        handleMemory();
        handleScratchDisk();
        handleLoad();
        handlePeakPolygons();
        handleGraphicsHardware();
    }
    
    public void handlePCGridResource(Element elAggregateData) {
        handlePCGridHostname(elAggregateData);
        handlePCGridPlatform(elAggregateData);
        handlePCGridActivePC(elAggregateData);
        handlePCGridActiveProcessors(elAggregateData);
        handlePCGridCenter(elAggregateData);
        handlePCGridTotalPC(elAggregateData);
        handlePCGridTotalProcessors(elAggregateData);
        handlePCGridSystem(elAggregateData);
        handlePCGridMemory(elAggregateData);
        handlePCGridDisk(elAggregateData);
        handlePCGridStatus(elAggregateData);
    }
    
    public void handleCondorResource() {
    }
    
    public ArrayList getAllBeans() {
        ArrayList allBeans = new ArrayList();
        allBeans.addAll(getComputeBeans());
        allBeans.addAll(getStorageBeans());
        allBeans.addAll(getVisualizationBeans());
        return allBeans;
    }
    
    public ArrayList getComputeBeans() {
        return gpirComputeBeans;
    }
    
    public ArrayList getStorageBeans() {
        return gpirStorageBeans;
    }
    
    public ArrayList getVisualizationBeans() {
        return gpirVisualizationBeans;
    }
    
    public ArrayList getUnitedDevicesBeans() {
        return gpirUnitedDevicesBeans;
    }
    
    public ArrayList getCondorBeans() {
        return gpirCondorBeans;
    }
    
    protected String getResourceType() {
        String resourceType = "";
        if(elRoot.getName().equals(SUMMARY)) {
            resourceType = elComputeResource.getChildTextTrim("ResourceType");
        } else if(elRoot.getName().equals(PC_GRID_DEVICE_GROUPS)) {
            String deviceType = elComputeResource.getAttribute("hostname").getValue();
            if(deviceType.equals(UNITED_DEVICES_ATTRIBUTE)) {
                resourceType = GpirResourceBean.UNITED_DEVICES;
            } else if(deviceType.equals(CONDOR_ATTRIBUTE)) {
                resourceType = GpirResourceBean.CONDOR;
            }
            //.getChildren("PCGridGroup", nsRoot))
            //resourceType = PC_GRID_TYPE;
        }
        return resourceType;
    }
    
    protected void handleHostname() {
        currentBean.setHostname(elComputeResource.getAttribute("hostname").getValue());
    }
    
    protected void handleStatus() {
        currentBean.setStatus(elComputeResource.getAttribute("status").getValue());
    }
    
    protected void handleName() {
        currentBean.setName(elComputeResource.getChildTextTrim("Key"));
    }
    
    protected void handleSystem() {
        currentBean.setSystem(elComputeResource.getChildTextTrim("System"));
    }
    
    protected void handleDepartment() {
        Element elDepartment = elComputeResource.getChild("Department", nsComputeResource);
        Namespace nsDepartment = elDepartment.getNamespace();
        currentBean.setDepartmentName(elDepartment.getChildTextTrim("Name", nsDepartment));
        currentBean.setDepartmentUrl(elDepartment.getChildTextTrim("Url", nsDepartment));
    }
    
    protected void handleInstitution() {
        Element elInstitution = elComputeResource.getChild("Institution", nsComputeResource);
        Namespace nsInstitution = elInstitution.getNamespace();
        currentBean.setInstitutionName(elInstitution.getChildTextTrim("Name", nsInstitution));
        currentBean.setInstitutionUrl(elInstitution.getChildTextTrim("Url", nsInstitution));
    }
    
    protected void handleNodes() {
        currentBean.setNodes(elComputeResource.getChildTextTrim("NumNodes", nsComputeResource));
    }
    
    protected void handleProcessors() {
        currentBean.setProcessors(elComputeResource.getChildTextTrim("NumProcessors", nsComputeResource));
    }
    
    protected void handlePerformance() {
        currentBean.setPerformance(elComputeResource.getChildTextTrim("PeakPerformance", nsComputeResource));
    }
    
    protected void handleMemory() {
        currentBean.setMemory(elComputeResource.getChildTextTrim("Memory", nsComputeResource));
    }
    
    protected void handleScratchDisk() {
        currentBean.setScratchDisk(elComputeResource.getChildTextTrim("ScratchDisk", nsComputeResource));
    }
    
    //TODO: should null load be handled with -1?
    protected void handleLoad() {
        Element elLoadInfo = elComputeResource.getChild("LoadInfo", nsComputeResource);
        if (elLoadInfo != null) {
            Namespace nsLoadInfo = elLoadInfo.getNamespace();
            Element elLoad = elLoadInfo.getChild("Load", nsLoadInfo);
            if (elLoad != null) {
                Namespace nsLoad = elLoad.getNamespace();
                currentBean.setLoad(elLoad.getChildTextTrim("Value", nsLoad));
            }
        }else {
            currentBean.setLoad(null);
        }
    }
    
    protected void handleJobs() {
        
        Element elJobs = elComputeResource.getChild("Jobs", nsComputeResource);
        if (elJobs == null) {
            // there's no jobs element here, so job data is unavailable, not zero
        } else {
            Namespace nsJobs = elJobs.getNamespace();
            List elStatusList = elJobs.getChildren("Status", nsJobs);
            if (elStatusList != null) {
                Iterator sIter = elStatusList.iterator();
                while (sIter.hasNext()) {
                    Element elStatus = (Element) sIter.next();
                    Namespace nsStatus = elStatus.getNamespace();
                    String name = elStatus.getChildTextTrim("Name", nsStatus);
                    if (name != null && name.equals("Running")) {
                        currentBean.setJobsRunning(elStatus.getChildTextTrim("NumJobs", nsStatus));
                    } else if (name != null && name.equals("Queued")) {
                        currentBean.setJobsQueued(elStatus.getChildTextTrim("NumJobs", nsStatus));
                    } else if (name != null && name.equals("Other")) {
                        currentBean.setJobsOther(elStatus.getChildTextTrim("NumJobs", nsStatus));
                    }
                }
            }
        }
    }
    
    protected void handlePeakPolygons() {
        currentBean.setPeakPolygons(elComputeResource.getChildTextTrim("PeakPolygons", nsComputeResource));
    }
    
    protected void handleOnlineStorage() {
        currentBean.setOnlineStorage(elComputeResource.getChildTextTrim("OnlineStorage", nsComputeResource));
    }
    
    protected void handleOfflineStorage() {
        currentBean.setOfflineStorage(elComputeResource.getChildTextTrim("OfflineStorage", nsComputeResource));
    }
    
    protected void handleGraphicsHardware() {
        currentBean.setGraphicsHardware(elComputeResource.getChildTextTrim("GraphicsHW", nsComputeResource));
    }
    
    protected void handlePCGridCenter(Element elAggregateData) {
        currentBean.setPCGridCenter(elAggregateData.getParentElement().getAttribute("name").getValue());
    }
    
    protected void handlePCGridStatus(Element elAggregateData) {
        String status = "";
        String state = elAggregateData.getParentElement().getChildTextTrim("State");
        if(state.equals("Enabled")) {
            status = "up";
        } else {
            status = "down";
        }
        currentBean.setPCGridStatus(status);
    }
    
    protected void handlePCGridHostname(Element elAggregateData) {
        currentBean.setPCGridHostname(elComputeResource.getAttribute("hostname").getValue());
    }
    
    protected void handlePCGridPlatform(Element elAggregateData) {
        currentBean.setPCGridPlatform(elAggregateData.getChildTextTrim("Platform"));
    }
    
    protected void handlePCGridTotalPC(Element elAggregateData) {
        currentBean.setPCGridTotalPC(elAggregateData.getChildTextTrim("TotalPc"));
    }
    
    protected void handlePCGridActivePC(Element elAggregateData) {
        currentBean.setPCGridActivePC(elAggregateData.getChildTextTrim("ActivePc"));
    }
    
    protected void handlePCGridTotalProcessors(Element elAggregateData) {
        currentBean.setPCGridTotalProcessors(elAggregateData.getChildTextTrim("TotalCpu"));
    }
    
    protected void handlePCGridActiveProcessors(Element elAggregateData) {
        currentBean.setPCGridActiveProcessors(elAggregateData.getChildTextTrim("ActiveCpu"));
    }
    
    protected void handlePCGridMemory(Element elAggregateData) {
        int memory = Integer.parseInt(elAggregateData.getChildTextTrim("TotalMemory"));
        memory = memory / 1000;
        currentBean.setPCGridMemory("" + memory);
    }
    
    protected void handlePCGridDisk(Element elAggregateData) {
        int disk = Integer.parseInt(elAggregateData.getChildTextTrim("Disk"));
        disk = disk / 1000;
        currentBean.setPCGridDisk("" + disk);
    }
    
    protected void handlePCGridSystem(Element elAggregateData) {
        currentBean.setPCGridSystem("United Devices");
    }
    
} // GpirResourceHandler
