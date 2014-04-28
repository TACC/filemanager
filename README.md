# Overview 

The XSEDE File Manager (XFM) started out as a portlet adaptation of the SJTU GridFTP GUI Client(SGGC).
The SGGC is a Java desktop application providing graphical GridFTP access to resources.  It is currently 
listed as a Globus Incubator project with downloads available http://dev.globus.org/wiki/Incubator/SGGC. 
After porting the project to an applet and several months of interal review, the original SGGC interface 
was replaced with the current two panel approach resembling the FUGU (http://rsug.itd.umich.edu/software/fugu/) 
SFTP client.  The general feeling among the group was that a dual panel, drag-and-drop interface was much 
less imposing than a full blown desktop explorer clone.

The XFM manages the user's credential for them providing single sign on in the portal, then delegated 
authentication to the remote machines.  The method for doing this is essentially the same as in the 
GSI-SSHTerm applet.  "The portlet relies on the OGCE ProxyManager service for in-memory proxy credentials 
and passes these credentials to the applet in string form using the "sshterm.gsscredential" applet 
parameters.  So, as long as you can get a credential in string form you can use the applet in virtually 
any web setting.  The applet is also capable of contacting a MyPproxy server to retrieve a credential but 
for the single sign-on purposes of the XSEDE User Portal it is more desirable to not require the user to
authenticate at all."

Several other TGUP-specific features were added to the portlet as well. An XML-RPC sever was written to 
support the client applet and provide historical file logging and notifications.  When files transfers are initiated in the client, the transfer is recorded in the middleware and tracked to completion.  At the end
of the transfer, the user is optionally notified in their preferred method of the event. The service utilizes
the javamail library, a remote SMTP server (Gmail), the TGCDB (PostgreSQL), MSN messenger API, and an email-
to-text service (Teleflip.com) to deliver the notifications.

Finally, the server provides a listing of TG resources to which the user has access automatically upon login.
The mechanism used by the service is similar to that of the Accounting portlet.  The 'portal.usage table' is queried in the TGCDB by the user's person_id.  This yields a list of resources that are then mapped to a 
known list of valid hostnames and short names which is then displayed for the user.


<p style="border: thin solid red; padding: 3px;">NOTE: the applet used in this portlet has some 
configurations that are specific to XSEDE (e.g. the default MyProxy server is the XSEDE MyProxy 
server) so before using this applet in other projects you may want to speak to Jim Basney (NCSA), Rion 
Dooley or Maytal Dahan (TACC) about reconfiguring the portlet.</p>


## The Server Code 

The client and server code share a common source tree and build system.  The server runs in standalone 
mode out of the box on port 9001.  A couple common features have been included to make using the server a 
bit easier.  

* Ant build system.
  *  clean           Delete all build artifacts
  *  compile         Compile/generate all client- and server-side content
  *  deploy-eclipse  Deploy client to WEB-INF folder
  *  dist            Bundle the client and deploy it in the web folder
  *  gsissh          Test the gsissh env and find commands
  *  jar             Create client/server jar
  *  jarhelp         Jar up helpset files and copy to the build directory
  *  run             Test the gsissh env and find commands
  *  server          Compile/generate all client- and server-side content
  *  junitreport     Run the junit tests on the server.
* Rolling file logging provided by log4j.  Just add the "SERVER" keyword to the rootCategory declaration.
* Configuration files: 
  * sggc.properties for definitions of supporting services and run time options.
  * hibernate.cfg.xml to specify database connection. Currently there is not database init target for
the build system, but the sql file for creating the tables is in the etc folder.
  * test.properties for running the junit tests on server api
* Startup.sh and Shutdown scripts.sh

To run the server:

1. Download the checkout from cvs.
2. Edit the configuration file.  By default, the service has all the properties configured in cvs to 
run out of the box using the TGCDB.
3. At the command line, run 'startup.sh'.  This will build the service and start the server in a background 
service.  
4. Optional: to enable rolling output, edit the log4j.properties file, adding SERVER to the log4j.rootCategory declaration.

To stop the server:

1. At the command line, run 'shutdown.sh'.

To test the services:

1. Edit the 'test.configuration' file adding two valid dn's in the test.dn and test.baddn lines.
2. At the command line, run 'ant junitreport'.
3. View the results in the junit directory created during the test.


## The Portlet Code

The client and server code share a common source tree and build system.  To build and deploy the client 
and all needed libraries to a folder that can be deployed to a web server for use:

1. Download the checkout from cvs.
1. Edit the build.xml file, changing the 'todir' property in the "dist" target to your deployment folder.
1. At the command line, run 'ant dist'.  This will compile, bundle, sign, and deploy the client and an index.html file for you to run the client locally from your web browser.
1. Point your web browser to the directory the client was just deployed in.
1. The client will run a grid-proxy-init if no valid credentials are passed to it from the index.html file.
To avoid this, just copy a valid proxy file to the ~/xup_filemanager/proxies folder on your computer.
The XFM will automatically discover it there.

<p style="border: thin solid red; padding: 3px;">NOTE: the XFM is meant to be run as a client server 
application.  In the event the TG instance of the server is down, you can run it locally and point the 
client to it by editing src/org/XSEDE/portal/filemanager/applet/AppMain.java and changing the value 
of the 'HISTORY_SERVLET' variable to your server location.  If you don't do anything and no service is 
running, the XFM will simply prompt you to run in "loner" mode and get the resource listing from GPIR.  
It won't necessarily be an accurate list, but you should have several resources on there you can access.</p>


### Code Location

The code is currently standalone from the TG User Portal source tree and can be found in ''ROOT/tg-filemanagert''.


### Action Class

The portlet action class, ''edu/tacc/portlets/FileManagerPortlet.java'', is very simple and really only 
the ''doView'' method has any code.  This method uses the ''obtainUserCredential'' method of the 
''GridPortletUtil'' class from the ''gp-common.jar'' library to grab the proxy from the ProxyManager service.
It then converts the proxy to a ''GlobusGSSCredentialImpl'' object and calls the 
''GlobusGSSCredentialImpl.export(ExtendedGSSCredential.IMPEXP_OPAQUE)'' method to convert the proxy to a string.


### Applet Declaration

Here is how the applet is declared in the portlet:


	<applet codebase="http://localhost/~dooley/XFMRedux/" 
		code="com.sshtools.sshterm.SshTermApplet" width="900" height="700" archive="activation.jar, axis.jar, commons-codec-1.3.jar, commons-discovery.jar, commons-httpclient-3.1.jar, gms-xstream-1.1.3.jar, GSI-SSHTerm-XSEDE.jar, jaxrpc.jar, jce-jdk13-120.jar, jdom.jar, jhall.jar, saaj.jar, tgfb.jar, ws-commons-util-1.0.2.jar, xmlrpc-client-3.1.jar, xmlrpc-common-3.1.jar"
		style="border-style: solid; border-width: 1; padding-left: 4; padding-right: 4; padding-top: 1; padding-bottom: 1">
	<PARAM name="separate_jvm" value="true">
	<PARAM name="cache_archive" value="">
	<PARAM name="classloader_cache" value="false">
	<PARAM name="filebrowser.gsscredential" value="value="-----BEGIN CERTIFICATE-----
	MIICuzCCAaOgAwIBAgIDIDrDMA0GCSqGSIb3DQEBBQUAMGkxCzAJBgNVBAYTAlVT
	MTgwNgYDVQQKEy9OYXRpb25hbCBDZW50ZXIgZm9yIFN1cGVyY29tcHV0aW5nIEFw
	cGxpY2F0aW9uczEgMB4GA1UEAxMXQ2VydGlmaWNhdGlvbiBBdXRob3JpdHkwHhcN
	MDcwNTI5MjAxNjU3WhcNMDcwNTI5MjIyMTU3WjBeMQswCQYDVQQGEwJVUzE4MDYG
	A1UEChMvTmF0aW9uYWwgQ2VudGVyIGZvciBTdXBlcmNvbXB1dGluZyBBcHBsaWNh
	dGlvbnMxFTATBgNVBAMTDEVyaWMgUm9iZXJ0czBcMA0GCSqGSIb3DQEBAQUAA0sA
	MEgCQQCAAB/cmuxMri3kcpVr0/OVZrQQJ03An8ujHTlqrpGbU0nPYeCdD8c+P9ly
	R4YHVFHYWnIcDn4SJw17Kv+/5xaxAgMBAAGjPzA9MA4GA1UdDwEB/wQEAwIEsDAM
	BgNVHRMBAf8EAjAAMB0GA1UdDgQWBBQXX4Ovv7L9Wg9g7Q68diQdUxmy2TANBgkq
	hkiG9w0BAQUFAAOCAQEAAcfdjUYZq30vhddT5iSZNvAl2eYnJ/gEQTqfGhFDyWqE
	exqdUDkf2XJ3xMxCBJBTMO1cg//FrMM3LtiZiGBLd7Jmmzm8G2yMzDhZX8ZPPdG7
	JbZLmQtmSbAt4y9jtT5gFnx+0UKS9cnY5PxmP3Pl45wkLI6qqjogKNTZMzDvex6s
	0SNTSUQQhOnpl7PTxb9cpDnGOvy80oxzygC8m96MdmPxxd/dTsc4dmyuIhH+UEMH
	E0WTTPGLNvYq5s2O2RtI2BHExDdSiY7uQFQ/N2kpba/bYK/i8sT3po8SXTF1qBYQ
	W8Mlv+FSxCdwhREaRuf3Y8Lvf4c4zOFewE1WEPT/6g==
	-----END CERTIFICATE-----
	-----BEGIN RSA PRIVATE KEY-----
	MIIBOgIBAAJBAIAAH9ya7EyuLeRylWvT85VmtBAnTcCfy6MdOWqukZtTSc9h4J0P
	xz4/2XJHhgdUUdhachwOfhInDXsq/7/nFrECAwEAAQJAf80gAujgRJOomK7biATg
	8WvRH0vO8yPZc+xq9pyEH1tjhVa/Rtac0xUrUZWi2gOU9LKt3xfExsyp/LPht1I0
	AQIhAP9TGKnGv9nOEaTki8GB9P2yfk5Zhl8UOG1dnr8CNixBAiEAgFbOKKwjZjUh
	6k2vi0MhwuNFO6cvsTwPsmBiwPMSDnECICD3i8GwJelrkB+oWts7enSfbWuUZ6Mz
	Ky4D3pFvKauBAiBc+IVYSXtLa2oqRiTJCdTHTcS1tiOCjTQB0Hk5tdx6wQIhAMyg
	s20Jn53WZmkFeNyLVMf0CvUzgxX+enaYkD620yA/
	-----END RSA PRIVATE KEY-----
	"/>
	</applet>


<p style="border: thin solid red; padding: 3px;">NOTE: for this applet to run in harmony with the gsissh 
portlet, they must share idential codebase, archive, separate_jvm, classloader_cache, and cache_archive 
tags.  Without these being the same, the possibility of common classloader collisions increases dramatically 
and both portlets will be rendered useless for the majority of users...see more info below.</p>

### Applet Jars

The applet and server are bundled with their dependent libraries in descriptive directories within the 
'lib' folder.

- lib
  - client: client only libraries.  these are not needed by the server and not in it's classpath
  - common: libraries shared between the client and server.  the client, help and server directory 
contents are in the archive listing of the applet.
  - ext: bundled gsisshterm app and dependent libraries. distributed with client.
  - help: java help library
  - server: server only libraries


One important note is that the developers at NCSA who provided the GSI-SSHTerm library worked around 
many of their dependency issues by redeploying all of their application's dependent jar class files in 
their main GSI-SSHTerm-XSEDE.jar library.  This is generally not a problem unless you're running 
other applets that rely on the same classes.  The Java security model does not allow applets to share 
access to previously loaded classes.  Thus if one person starts up a grid enabled app then another person 
starts one up in another windows, say the gsissh and XFM portlets respectively, then the XFM will throw 
a fit and quite because the bouncycastle library was already in the classloader and thus, it cannot relaod 
the same class or remove the other class from the parent classloader.  Huge problem.  Luckily, tThis 
problem can be avoided by either sharing a common applet archive or hacking the AppletClassLoader by way 
of the Java Console (don't do this). For future reference, here are the bundled libraries in the 
gsi-sshterm jar: </p>

* puretls.jar // ssl implementation...that says not to repackage without giving credit :-)
* cryptix.jar //crypto library
* cryptix-32.jar //crypto library
* cryptix-ansi1.jar //crypto library
* log4j.jar //defacto logging libarary
* jglobus.jar // globus core java libraries
* java40.jar // netscape security classes commonly used to extend Java 1.1 functionality
 
 
### Building the Clients
 
To build the applet jar, simply run the 'build.client' ant task by executing
 
	$> ant build.client 
 
from the command line in the project root directory.
 
To build the desktop executable jar, simply run the 'build.exe' ant task. This is currently the default task,
so you can run it by just running
 
	$> ant 
 
from the command line in the project root directory.
