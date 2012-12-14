/*
 * VizResourceParser.java
 *
 * Created on April 19, 2005, 3:47 PM
 */

package org.teragrid.portal.filebrowser.applet.gat.discovery.gpir.synch.beans;

import org.jdom.Element;

/**
 *
 * @author ericrobe
 */
public class VizResourceParser extends AbstractSummaryResourceParser {
    
    VizResourceBean vizBean = null;
    
    public VizResourceParser(Element element) {
        super(element);
        vizBean = new VizResourceBean(bean);
        parse(element);
    }
    
    public void parse(Element element) {
        elResource = element;
        handleProcessors();
        handlePerformance();
        handleMemory();
        handleScratchDisk();
        handlePeakPolygons();
        handleGraphicsHardware();
    }
    
    public AbstractResourceBean getBean() {
        return vizBean;
    }
    
    protected void handlePerformance() {
        vizBean.setPerformance(elResource.getChildTextTrim("PeakPerformance", nsResource));
    }
    
    protected void handleMemory() {
        vizBean.setMemory(elResource.getChildTextTrim("Memory", nsResource));
    }
    
    protected void handleScratchDisk() {
        vizBean.setScratchDisk(elResource.getChildTextTrim("ScratchDisk", nsResource));
    }
    
    protected void handleProcessors() {
        vizBean.setProcessors(elResource.getChildTextTrim("NumProcessors", nsResource));
    }
    
    private void handlePeakPolygons() {
        vizBean.setPeakPolygons(elResource.getChildTextTrim("PeakPolygons", nsResource));
    }
    
    private void handleGraphicsHardware() {
        vizBean.setGraphicsHardware(elResource.getChildTextTrim("GraphicsHW", nsResource));
    }
    
}
