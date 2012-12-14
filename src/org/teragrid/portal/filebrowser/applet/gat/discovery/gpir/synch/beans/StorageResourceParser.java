/*
 * StorageResourceParser.java
 *
 * Created on April 19, 2005, 3:47 PM
 */

package org.teragrid.portal.filebrowser.applet.gat.discovery.gpir.synch.beans;

import org.jdom.Element;

/**
 *
 * @author ericrobe
 */
@SuppressWarnings("unused")
public class StorageResourceParser extends AbstractSummaryResourceParser { 
    
    private String onlineStorage;
    private String offlineStorage;
    
    private StorageResourceBean storageBean = null;
    
    /*
    public StorageResourceParser() { 
        super();
    }
    */
    public StorageResourceParser(Element element) {
        super(element);
        storageBean = new StorageResourceBean(bean);
        parse(element);
    }
    
    public void parse(Element element) {
        elResource = element;
        handleOnlineStorage();
        handleOfflineStorage();
    }
    
    public AbstractResourceBean getBean() {
        return storageBean;
    }
    
    protected void handleOnlineStorage() {
        storageBean.setOnlineStorage(elResource.getChildTextTrim("OnlineStorage", nsResource));
    }
    
    protected void handleOfflineStorage() {
        storageBean.setOfflineStorage(elResource.getChildTextTrim("OfflineStorage", nsResource));
    }
    
}
