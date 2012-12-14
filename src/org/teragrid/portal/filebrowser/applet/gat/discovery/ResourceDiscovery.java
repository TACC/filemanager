/*Copyright (c) 2004,University of Illinois at Urbana-Champaign.  All rights reserved.
 * 
 * Created on Feb 1, 2006
 * 
 * Developed by: CCT, Center for Computation and Technology, 
 * 				NCSA, University of Illinois at Urbana-Champaign
 * 				OSC, Ohio Supercomputing Center
 * 				TACC, Texas Advanced Computing Center
 * 				UKy, University of Kentucky
 * 
 * https://www.gridchem.org/
 * 
 * Permission is hereby granted, free of charge, to any person 
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal with the Software without 
 * restriction, including without limitation the rights to use, 
 * copy, modify, merge, publish, distribute, sublicense, and/or 
 * sell copies of the Software, and to permit persons to whom 
 * the Software is furnished to do so, subject to the following conditions:
 * 1. Redistributions of source code must retain the above copyright notice, 
 *    this list of conditions and the following disclaimers.
 * 2. Redistributions in binary form must reproduce the above copyright notice, 
 *    this list of conditions and the following disclaimers in the documentation
 *    and/or other materials provided with the distribution.
 * 3. Neither the names of Chemistry and Computational Biology Group , NCSA, 
 *    University of Illinois at Urbana-Champaign, nor the names of its contributors 
 *    may be used to endorse or promote products derived from this Software without 
 *    specific prior written permission.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF 
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  
 * IN NO EVENT SHALL THE CONTRIBUTORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, 
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, 
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER 
 * DEALINGS WITH THE SOFTWARE.
*/

package org.teragrid.portal.filebrowser.applet.gat.discovery;

import java.io.File;
import java.util.List;

import org.teragrid.portal.filebrowser.applet.gat.discovery.gpir.synch.beans.*;
import org.teragrid.portal.filebrowser.applet.gat.discovery.gpir.query.*;
import org.teragrid.portal.filebrowser.applet.exception.SynchronizationException;


/**
 * Worker class which synchronizes the information persisted in our db with
 * the information in the information service.  In short, this class queries 
 * the info service by retrieving the xml, parsing the code, and resolving 
 * it against the data in the db.  This class should be called roughly every 
 * 10 minutes, which is the rate at which the info service refreshes.
 * 
 * @author Rion Dooley < dooley [at] cct [dot] lsu [dot] edu >
 * @see CCGResource
 */
public class ResourceDiscovery {
	
    public GPIRCollection collection = new ResourceList();
    
    private String gpirEndpoint;
    
	public ResourceDiscovery(String gpirEndpoint) {
		
		this.gpirEndpoint = gpirEndpoint;
		
		try {
			String resourceXML = retrieveXML("ccg_resources.xml");
			XmlHandler handler = null;
		    
		    if(resourceXML == null || resourceXML.equals("")) {
		        throw new SynchronizationException(
                    "No resources returned from information service");
		    } else {
		        handler = new SummaryXmlHandler(resourceXML);
				collection = handler.getBeans();
		    }
		    
		} catch (SynchronizationException e) {
    	    throw new SynchronizationException(
	            "Resource Synchronization failed: " + e.getMessage(), e);
		}
	}
	/**
	 * Get the XML data from the info service
	 * @param serviceHost
	 */
	private String retrieveXML(String xmlFilename) throws SynchronizationException {
		String resourceXML = "";
		try {
			resourceXML = new Query(gpirEndpoint).getQuery("vo",
					"summary",
                    "TeraGrid_Test",
					new File(xmlFilename));
//			if (!resourceXML.equals(""))
//				logger.debug("Received XML from GPIR!!");
		} catch (Exception e) {
		    throw new SynchronizationException(e);
		}
		return resourceXML;
	}
	
	@SuppressWarnings("unchecked")
	public List<AbstractResourceBean> getResources() {
	    return ((ResourceList)collection).getAll();
    }
   
}
