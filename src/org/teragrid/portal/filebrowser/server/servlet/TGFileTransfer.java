/* 
 * Created on Dec 13, 2007
 * 
 * Developed by: Rion Dooley - dooley [at] tacc [dot] utexas [dot] edu
 * 				 TACC, Texas Advanced Computing Center
 * 
 * https://www.tacc.utexas.edu/
 */

package org.teragrid.portal.filebrowser.server.servlet;

import org.apache.xmlrpc.server.PropertyHandlerMapping;
import org.apache.xmlrpc.server.XmlRpcServer;
import org.apache.xmlrpc.server.XmlRpcServerConfigImpl;
import org.apache.xmlrpc.webserver.WebServer;
import org.teragrid.portal.filebrowser.server.servlet.impl.*;

/**
 * Class to start a light web server to host the TGFileTransferHistory servlet
 * 
 * @author Rion Dooley < dooley [at] tacc [dot] utexas [dot] edu >
 *
 */
public class TGFileTransfer {

    private static final int port = 9001;

    public static void main(String[] args) throws Exception {
       
        WebServer webServer = new WebServer(port);
      
        XmlRpcServer xmlRpcServer = webServer.getXmlRpcServer();
      
        PropertyHandlerMapping phm = new PropertyHandlerMapping();
        /* Load handler definitions from a property file.
         * The property file might look like:
         *   Calculator=org.apache.xmlrpc.demo.Calculator
         *   org.apache.xmlrpc.demo.proxy.Adder=org.apache.xmlrpc.demo.proxy.AdderImpl
         */
//        phm.load(Thread.currentThread().getContextClassLoader(),
//                 "MyHandlers.properties");

        /* You may also provide the handler classes directly,
         * like this:
         * phm.addHandler("Calculator",
         *     org.apache.xmlrpc.demo.Calculator.class);
         * phm.addHandler(org.apache.xmlrpc.demo.proxy.Adder.class.getName(),
         *     org.apache.xmlrpc.demo.proxy.AdderImpl.class);
         */
        phm.addHandler(TGFileTransferHistory.class.getName(),
                TGFileTransferHistoryImpl.class);
        phm.addHandler(TGNotification.class.getName(),
                TGNotificationImpl.class);
        phm.addHandler(TGResourceDiscovery.class.getName(),
                TGResourceDiscoveryImpl.class);
        phm.addHandler(TGProfile.class.getName(),
                TGProfileImpl.class);
        
        xmlRpcServer.setHandlerMapping(phm);
        
        XmlRpcServerConfigImpl serverConfig =
            (XmlRpcServerConfigImpl) xmlRpcServer.getConfig();
        
        serverConfig.setEnabledForExtensions(true);
        serverConfig.setEnabledForExceptions(true);
        
        webServer.start();
        
    }

}
