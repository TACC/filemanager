/*
 * ComputeResourceParser.java
 *
 * Created on April 19, 2005, 3:47 PM
 */

package org.teragrid.portal.filebrowser.applet.gat.discovery.gpir.synch.beans;

import java.util.Iterator;
import java.util.List;

import org.jdom.Element;
import org.jdom.Namespace;

/**
 *
 * @author ericrobe
 */
@SuppressWarnings("unchecked")
public class ComputeResourceParser extends AbstractSummaryResourceParser { 
    
    private ComputeResourceBean computeBean = null;
    
    public ComputeResourceParser(Element element) {
        super(element);
        computeBean = new ComputeResourceBean(bean); 
        parse(element);
    }
    
    public void parse(Element element) {
        elResource = element;
        handleProcessors();
        handlePerformance();
        handleMemory();
        handleScratchDisk();
        handleLoad();
        handleJobs();
        handleNodes();
    }
    
    public AbstractResourceBean getBean() {
        return computeBean;
    }
    
    protected void handlePerformance() {
    		computeBean.setPerformance(elResource.getChildTextTrim("PeakPerformance", nsResource));
    }
    
    protected void handleMemory() {
        computeBean.setMemory(elResource.getChildTextTrim("Memory", nsResource));
    }
    
    protected void handleScratchDisk() {
        computeBean.setScratchDisk(elResource.getChildTextTrim("ScratchDisk", nsResource));
    }
    
    protected void handleNodes() {
        computeBean.setNodes(elResource.getChildTextTrim("NumNodes", nsResource));
    }
    
    protected void handleProcessors() {
        computeBean.setProcessors(elResource.getChildTextTrim("NumProcessors", nsResource));
    }
    /*
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
    */
    
	protected void handleJobs() {
        
        Element elJobs = elResource.getChild("Jobs", nsResource);
        if (elJobs == null) {
            computeBean.setJobsRunning("0");
            computeBean.setJobsQueued("0");
            computeBean.setJobsOther("0");
        } else {
            Namespace nsJobs = elJobs.getNamespace();
            List elStatusList = elJobs.getChildren("Status", nsJobs);
            if (elStatusList != null) {
                Iterator sIter = elStatusList.iterator();
                while (sIter.hasNext()) {
                    Element elStatus = (Element) sIter.next();
                    Namespace nsStatus = elStatus.getNamespace();
                    String name = elStatus.getChildTextTrim("Name", nsStatus);
                    String numJobs = elStatus.getChildTextTrim("NumJobs", nsStatus);
                    if (name != null && name.equals("Running")) {
                        if(numJobs.equals("") || numJobs == null) 
                            computeBean.setJobsRunning("0");
                        else
                            computeBean.setJobsRunning(numJobs);
                    } else if (name != null && name.equals("Queued")) {
                        if(numJobs.equals("") || numJobs == null)
                            computeBean.setJobsQueued("0");
                        else
                            computeBean.setJobsQueued(numJobs);
                    } else if (name != null && name.equals("Other")) {
                        if(numJobs.equals("") || numJobs == null)
                            computeBean.setJobsOther("0");
                        else
                            computeBean.setJobsOther(numJobs);
                    }
                }
            }
        }
    }
    
    //TODO: should null load be handled with -1?
    protected void handleLoad() {
        Element elLoadInfo = elResource.getChild("LoadInfo", nsResource);
        if (elLoadInfo != null) {
        		Namespace nsLoadInfo = elLoadInfo.getNamespace();
        		List loads = elLoadInfo.getChildren("Load",nsLoadInfo);
        		for (Iterator iter = loads.iterator();iter.hasNext();) {
        			Element elLoad = (Element) iter.next();
        			Namespace nsLoad = elLoad.getNamespace();
        			computeBean.setLoad(elLoad.getChildTextTrim("Value", nsLoad) + ";" + computeBean.getLoad());
        			computeBean.setLoad(elLoad.getChildTextTrim("Type", nsLoad) + ":" + computeBean.getLoad());
        		}
        }else {
            computeBean.setLoad(null);
        }
    }
    
//    protected void handleInstitution() {
//        Element elInst = elResource.getChild("Institution");
//        System.out.println("computeBean: " + computeBean.toString());
//        computeBean.setInstitutionName(elInst.getChildTextTrim("Name"));
//        computeBean.setInsitutionUrl(elInst.getChildTextTrim("Url"));
//    }
//    
}
