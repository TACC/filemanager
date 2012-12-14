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

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;

/**
 *
 * @author ericrobe
 */
@SuppressWarnings({"unchecked","unused"})
public class PcGridXmlHandler extends AbstractXmlHandler {
    
    private Document doc;
    private Element elRoot;
    private Namespace nsRoot;
    private List elGroupsList;
    private Element elPcGridResource;
    private Namespace nsPcGridResource;
    //private ResourceBean currentBean;
    private ResourceFactory beanFactory;
    
    /** Creates a new instance of SummaryXmlHandler */
    public PcGridXmlHandler(String xml) {
        super(xml);
        //resourceBeans = new ResourceList();
    }
    
    private void iterate() {
        SAXBuilder builder = new SAXBuilder();
        StringReader xmlResultStream = new StringReader(strXml);
        try {
            doc = builder.build(xmlResultStream);
        }catch(JDOMException e) {
            e.printStackTrace();
        }catch(IOException e) {
            e.printStackTrace();
        }
        
        elRoot = doc.getRootElement();
        nsRoot = elRoot.getNamespace();
        elGroupsList = elRoot.getChildren("PCGridGroups", nsRoot);
        Iterator crIter = elGroupsList.iterator();
        
        while (crIter.hasNext()) {
            elPcGridResource = (Element) crIter.next();
            nsPcGridResource = elPcGridResource.getNamespace(); 
            Resource resource = new Resource(new PcGridResourceFactory());
            resource.build(elPcGridResource);
            if(resource != null) {
                beans.put(resource.getBean());
            }
        }
    }
    
    public GPIRCollection getBeans() {
        if(beans == null) {
            beans = new ResourceList();
            iterate();
            return beans;
        } else {
            return beans;
        }
    }
    
}
