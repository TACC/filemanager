/*
 * ResourceParser.java
 *
 * Created on April 19, 2005, 2:16 PM
 */

package org.teragrid.portal.filebrowser.applet.gat.discovery.gpir.synch.beans;

import org.jdom.Element;

/**
 *
 * @author ericrobe
 */
public class PcGridResourceParser extends AbstractSummaryResourceParser {
    Element elResource;
    PcGridResourceBean pcGridBean = new PcGridResourceBean();
    
    public PcGridResourceParser(Element element) {
        super(element);
        pcGridBean = new PcGridResourceBean(bean); 
        parse(element);
    }
    
    /**
     * 
     * @param element 
     */
    public void parse(Element element){
        elResource = element;
        handleTotalPc();
        handleActivePc();
        handleTotalCpu();
        handleActiveCpu();
        handleTotalMemory();
        handleTotalDisk();
    }
    public AbstractResourceBean getBean(){
        return pcGridBean;
    }
    
    protected void handleTotalDisk() {
        pcGridBean.setTotalDisk(elResource.getChildTextTrim("TotalDisk"));
    }
    
    protected void handleTotalMemory() {
        pcGridBean.setTotalMemory(elResource.getChildTextTrim("TotalMemory"));
    }
    
    protected void handleActiveCpu() {
        pcGridBean.setActiveCpu(elResource.getChildTextTrim("ActiveCpu"));
    }
    
    protected void handleTotalCpu() {
        pcGridBean.setTotalCpu(elResource.getChildTextTrim("TotalCpu"));
    }
    
    protected void handleTotalPc() {
        pcGridBean.setTotalPc(elResource.getChildTextTrim("TotalPc"));
    }
    
    protected void handleActivePc() {
        pcGridBean.setActivePc(elResource.getChildTextTrim("ActivePc"));
    }
    
}
