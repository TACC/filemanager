/*
 * SummaryXmlHandler.java
 *
 * Created on April 19, 2005, 2:19 PM
 */

package org.teragrid.portal.filebrowser.applet.gat.discovery.gpir.synch.beans;

import java.io.IOException;
import java.io.StringReader;
import java.util.Iterator;
import java.util.List;
import java.util.WeakHashMap;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.teragrid.portal.filebrowser.server.servlet.model.resource.DBUtil;

/**
 *
 * @author ericrobe
 */
@SuppressWarnings({"unchecked","unused"})
public class SummaryXmlHandler extends AbstractXmlHandler {
    
    private Document doc;
    private Element elRoot;
    private Namespace nsRoot;
    private List elResourceList;
    private Element elResource;
    private Namespace nsResource;
    //private ResourceBean currentBean;
    private ResourceFactory beanFactory;
    
    /** Creates a new instance of SummaryXmlHandler */
    public SummaryXmlHandler(String xml) {
        super(xml);
        //beans = new ResourceList();
    }
    
    private void iterate() {
        if(strXml == null) {
            System.out.println("String XML was null, returning...");
            beans = null;
            return;
        }
        SAXBuilder builder = new SAXBuilder();
        StringReader xmlResultStream = new StringReader(strXml);
        try {
            doc = builder.build(xmlResultStream);
        }catch(JDOMException e) {
            //e.printStackTrace();
            //TODO: is this the right way to handle this?
            beans = null;
            return;
        }catch(IOException e) {
            e.printStackTrace();
        }
        
        elRoot = doc.getRootElement();
        nsRoot = elRoot.getNamespace();
        elResourceList = elRoot.getChildren("ComputeResource", nsRoot);
        Iterator crIter = elResourceList.iterator();
        
        while (crIter.hasNext()) {
            elResource = (Element) crIter.next();
            Resource resource = null;  
            if(parseResourceType().equals(GpirProperties.COMPUTE)) {
                resource = new Resource(new ComputeResourceFactory());
            } else if(parseResourceType().equals(GpirProperties.STORAGE)) {
                resource = new Resource(new StorageResourceFactory());
            } else if(parseResourceType().equals(GpirProperties.VIZ)) {
                resource = new Resource(new VizResourceFactory());
            } else if(parseResourceType().equals(GpirProperties.PC_GRID)) {
                resource = new Resource(new PcGridResourceFactory());
            }
            
            if(elResource != null) {
                resource.build(elResource);
                beans.put(resource.getBean());
            }
        }
    }
    
    public GPIRCollection getBeans() {
        if(beans == null) {
            beans = new ResourceList();
            iterate();
//            resolveSystemAccounts(((ResourceList)beans).getAll());
            return beans;
        } else {
            return beans;
        }
    }
    
    private String parseResourceType() {
        String resourceType = "";
        if(elRoot.getName().equals(GpirProperties.SUMMARY)) {
            resourceType = elResource.getChildTextTrim("ResourceType");
        }
        return resourceType;
    }
    
    private void resolveSystemAccounts(List<AbstractResourceBean> resources) {
        
        String hostname,resourceName, userName, inst, type;
        
        WeakHashMap<String,String> resourceMap = DBUtil.getResourceMap();
        WeakHashMap<String,String> shortNameMap = DBUtil.getShortNameMap();
        WeakHashMap<String,String> institutionMap = DBUtil.getInstitutionMap();
        WeakHashMap<String,String> typeMap = DBUtil.getTypeMap();
        
        for (AbstractResourceBean bean: resources) {
            String origResName = bean.hostname;
            System.out.println("Resolving " + origResName);
            
            hostname = (String)resourceMap.get(origResName);
            System.out.println("hostname: " + hostname);

            //remove resource name from hash, that way we know if there are any left where user has no account
            resourceMap.remove(origResName);

            if (hostname == null) {
                            continue;
            } else if (hostname.equals(DBUtil.DEAD)) {
                            continue;
            } else {
   
                //add username and resource host to new system bean
                bean.setHostname(hostname);
                
                resourceName = (String)shortNameMap.get(origResName);
                System.out.println("resourceName: " + resourceName);
                bean.setName(((resourceName == null || hostname.equals(""))?hostname:resourceName));
                
                inst = (String)institutionMap.get(origResName);
                System.out.println("institution: " + inst);
                bean.setInstitutionName(inst);
                
                type = (String)typeMap.get(origResName);
                System.out.println("type: " + type);
                bean.setResourceType(type);
                
                //get current array list that belongs to institution or create a new one
                System.out.println("Resolved gpir bean " + bean.toString() + " to the results set.");
            }
        }
    }
    
}
