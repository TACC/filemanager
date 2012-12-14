/*
 * ResourceParser.java
 *
 * Created on April 19, 2005, 2:16 PM
 */

package org.teragrid.portal.filebrowser.applet.gat.discovery.gpir.synch.beans;

import org.jdom.Element;

/**
 * This interface allows client to implement their own parsing Strategy for parsing XML documents.
 * @author ericrobe
 */
public interface GenericParser {
    /**
     * Method that allows implementors of this interface to choose the algorithm for parsing given an XML Element object.  Follows the Strategy design pattern.
     * @param element XML <CODE>Element</CODE> object to parse
     */
    public void parse(Element element);
    /**
     * Returns a populated <CODE>AbstractJobBean</CODE>
     * @return Returns a populated AbstractJobBean object
     */
    public GPIRBean getBean();
}
