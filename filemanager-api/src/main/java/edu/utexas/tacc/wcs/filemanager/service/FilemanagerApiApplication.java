/**
 * Copyright (c) 2014, Texas Advanced Computing Center
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * * Neither the name of the University of Texas at Austin nor the names of its
 *   contributors may be used to endorse or promote products derived from
 *   this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS AS IS
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those
 * of the authors and should not be interpreted as representing official policies,
 * either expressed or implied, of the FreeBSD Project.
 */
package edu.utexas.tacc.wcs.filemanager.service;

import org.restlet.Application;
import org.restlet.Component;
import org.restlet.Restlet;
import org.restlet.Server;
import org.restlet.data.MediaType;
import org.restlet.data.Protocol;
import org.restlet.ext.jetty.AjpServerHelper;
import org.restlet.ext.jetty.HttpServerHelper;
import org.restlet.ext.jetty.JettyServerHelper;
import org.restlet.routing.Router;

import edu.utexas.tacc.wcs.filemanager.service.persistence.JndiSetup;
import edu.utexas.tacc.wcs.filemanager.service.resources.impl.BandwidthResourceImpl;
import edu.utexas.tacc.wcs.filemanager.service.resources.impl.BulkAddNotificationsResourceImpl;
import edu.utexas.tacc.wcs.filemanager.service.resources.impl.BulkDeleteNotificationsResourceImpl;
import edu.utexas.tacc.wcs.filemanager.service.resources.impl.BulkTransfersResourceImpl;
import edu.utexas.tacc.wcs.filemanager.service.resources.impl.ColleaguesResourceImpl;
import edu.utexas.tacc.wcs.filemanager.service.resources.impl.PartnersResourceImpl;
import edu.utexas.tacc.wcs.filemanager.service.resources.impl.SystemsResourceImpl;
import edu.utexas.tacc.wcs.filemanager.service.resources.impl.TransferNotificationsResourceImpl;
import edu.utexas.tacc.wcs.filemanager.service.resources.impl.TransfersResourceImpl;
import edu.utexas.tacc.wcs.filemanager.service.resources.impl.UsersResourceImpl;


public class FilemanagerApiApplication extends Application {

	private boolean standaloneMode = false;
	
	public FilemanagerApiApplication() {
		super();
		getMetadataService().setDefaultMediaType(MediaType.APPLICATION_JSON);
	}
	
	@Override
    public Restlet createInboundRoot() 
	{
        Router router = new Router(getContext());

        router.attach(getStandalonePrefix() + "/bandwidth", BandwidthResourceImpl.class); //done
        router.attach(getStandalonePrefix() + "/notifications/add", BulkAddNotificationsResourceImpl.class);
        router.attach(getStandalonePrefix() + "/notifications/delete", BulkDeleteNotificationsResourceImpl.class);
        router.attach(getStandalonePrefix() + "/users", UsersResourceImpl.class); //done
        router.attach(getStandalonePrefix() + "/colleagues", ColleaguesResourceImpl.class); //done
        router.attach(getStandalonePrefix() + "/partners", PartnersResourceImpl.class); //done
        router.attach(getStandalonePrefix() + "/systems", SystemsResourceImpl.class);
        router.attach(getStandalonePrefix() + "/transfers", BulkTransfersResourceImpl.class);
        router.attach(getStandalonePrefix() + "/transfers/{transferId}", TransfersResourceImpl.class);
        router.attach(getStandalonePrefix() + "/transfers/{transferId}/notifications/{type}", TransferNotificationsResourceImpl.class);
    
        return router;
    }
	
	protected String getStandalonePrefix() {
		return !standaloneMode ? "" : "/xfm";
	}
	
	public static void main(String[] args) throws Exception 
	{	
		JndiSetup.init();
		
		// Create a new Component.
        Component component = new Component();

        // Attach the AppsApplication
        FilemanagerApiApplication application = new FilemanagerApiApplication();
        component.getDefaultHost().attach(application);

       launchServer(component);
    }
	
	protected static void launchServer(Component component) throws Exception 
	{	
		 // create embedding jetty server
        Server embedingJettyServer = new Server(
	        component.getContext().createChildContext(),
	        Protocol.HTTP,
	        9090,
	        component
        );
        
        //construct and start JettyServerHelper
        JettyServerHelper jettyServerHelper = new HttpServerHelper(embedingJettyServer);
        jettyServerHelper.start();

        //create embedding AJP Server
        Server embedingJettyAJPServer=new Server(
            component.getContext().createChildContext(),
            Protocol.HTTP,
            9191,
            component
        );

        //construct and start AjpServerHelper
        AjpServerHelper ajpServerHelper = new AjpServerHelper(embedingJettyAJPServer);
        ajpServerHelper.start();
	}
}
