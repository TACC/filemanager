# Overview 

The TeraGrid File Manager (TGFM) started out as a portlet adaptation of the SJTU GridFTP GUI Client(SGGC).[[BR]]
The SGGC is a Java desktop application providing graphical GridFTP access to resources.  It is currently [[BR]]
listed as a Globus Incubator project with downloads available http://dev.globus.org/wiki/Incubator/SGGC. [[BR]]
After porting the project to an applet and several months of interal review, the original SGGC interface [[BR]]
was replaced with the current two panel approach resembling the FUGU (http://rsug.itd.umich.edu/software/fugu/) [[BR]]
SFTP client.  The general feeling among the group was that a dual panel, drag-and-drop interface was much [[BR]]
less imposing than a full blown desktop explorer clone.

The TGFM manages the user's credential for them providing single sign on in the portal, then delegated [[BR]]
authentication to the remote machines.  The method for doing this is essentially the same as in the [[BR]]
GSI-SSHTerm applet.  "The portlet relies on the OGCE ProxyManager service for in-memory proxy credentials [[BR]]
and passes these credentials to the applet in string form using the "sshterm.gsscredential" applet [[BR]]
parameters.  So, as long as you can get a credential in string form you can use the applet in virtually [[BR]]
any web setting.  The applet is also capable of contacting a MyPproxy server to retrieve a credential but [[BR]]
for the single sign-on purposes of the TeraGrid User Portal it is more desirable to not require the user to[[BR]]
authenticate at all."

Several other TGUP-specific features were added to the portlet as well. An XML-RPC sever was written to [[BR]]
support the client applet and provide historical file logging and notifications.  When files transfers are[[BR]] initiated in the client, the transfer is recorded in the middleware and tracked to completion.  At the end[[BR]]
of the transfer, the user is optionally notified in their preferred method of the event. The service utilizes[[BR]]
the javamail library, a remote SMTP server (Gmail), the TGCDB (PostgreSQL), MSN messenger API, and an email-[[BR]]
to-text service (Teleflip.com) to deliver the notifications.

Finally, the server provides a listing of TG resources to which the user has access automatically upon login.[[BR]]
The mechanism used by the service is similar to that of the Accounting portlet.  The 'portal.usage table' is[[BR]] queried in the TGCDB by the user's person_id.  This yields a list of resources that are then mapped to a [[BR]]
known list of valid hostnames and short names which is then displayed for the user.


{{{
#!html
<p style="border: thin solid red; padding: 3px;">NOTE: the applet used in this portlet has some [[BR]]
configurations that are specific to TeraGrid (e.g. the default MyProxy server is the TeraGrid MyProxy [[BR]]
server) so before using this applet in other projects you may want to speak to Jim Basney (NCSA), Rion [[BR]]
Dooley or Maytal Dahan (TACC) about reconfiguring the portlet.</p>
}}}


== The Server Code ==

The client and server code share a common source tree and build system.  The server runs in standalone [[BR]]
mode out of the box on port 9001.  A couple common features have been included to make using the server a [[BR]]
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
  * hibernate.cfg.xml to specify database connection. Currently there is not database init target for[[BR]]
the build system, but the sql file for creating the tables is in the etc folder.
  * test.properties for running the junit tests on server api
* Startup.sh and Shutdown scripts.sh

To run the server:

1. Download the checkout from cvs.
2. Edit the configuration file.  By default, the service has all the properties configured in cvs to [[BR]]
run out of the box using the TGCDB.
3. At the command line, run 'startup.sh'.  This will build the service and start the server in a background [[BR]]
service.  
4. Optional: to enable rolling output, edit the log4j.properties file, adding SERVER to the log4j.rootCategory declaration.

To stop the server:

1. At the command line, run 'shutdown.sh'.

To test the services:

1. Edit the 'test.configuration' file adding two valid dn's in the test.dn and test.baddn lines.
1. At the command line, run 'ant junitreport'.
1. View the results in the junit directory created during the test.


== The Portlet Code ==

The client and server code share a common source tree and build system.  To build and deploy the client [[BR]]
and all needed libraries to a folder that can be deployed to a web server for use:

1. Download the checkout from cvs.
1. Edit the build.xml file, changing the 'todir' property in the "dist" target to your deployment folder.
1. At the command line, run 'ant dist'.  This will compile, bundle, sign, and deploy the client and an index.html file for you to run the client locally from your web browser.
1. Point your web browser to the directory the client was just deployed in.
1. The client will run a grid-proxy-init if no valid credentials are passed to it from the index.html file.[[BR]]
To avoid this, just copy a valid proxy file to the ~/tgup_filemanager/proxies folder on your computer.[[BR]]
The TGFM will automatically discover it there.

{{{
#!html
<p style="border: thin solid red; padding: 3px;">NOTE: the TGFM is meant to be run as a client server [[BR]]
application.  In the event the TG instance of the server is down, you can run it locally and point the [[BR]]
client to it by editing src/org/teragrid/portal/filemanager/applet/AppMain.java and changing the value [[BR]]
of the 'HISTORY_SERVLET' variable to your server location.  If you don't do anything and no service is [[BR]]
running, the TGFM will simply prompt you to run in "loner" mode and get the resource listing from GPIR.  [[BR]]
It won't necessarily be an accurate list, but you should have several resources on there you can access.</p>
}}}

=== Code Location ===

The code is currently standalone from the TG User Portal source tree and can be found in ''ROOT/tg-filemanagert''.


=== Action Class ===

The portlet action class, ''edu/tacc/portlets/FileManagerPortlet.java'', is very simple and really only [[BR]]
the ''doView'' method has any code.  This method uses the ''obtainUserCredential'' method of the [[BR]]
''GridPortletUtil'' class from the ''gp-common.jar'' library to grab the proxy from the ProxyManager service.[[BR]]
It then converts the proxy to a ''GlobusGSSCredentialImpl'' object and calls the [[BR]]
''GlobusGSSCredentialImpl.export(ExtendedGSSCredential.IMPEXP_OPAQUE)'' method to convert the proxy to a string.


=== Applet Declaration ===
Here is how the applet is declared in the portlet:

{{{
<applet codebase="http://localhost/~dooley/TGFMRedux/" 
	code="com.sshtools.sshterm.SshTermApplet" width="900" height="700" archive="activation.jar, axis.jar, commons-codec-1.3.jar, commons-discovery.jar, commons-httpclient-3.1.jar, gms-xstream-1.1.3.jar, GSI-SSHTerm-teragrid.jar, jaxrpc.jar, jce-jdk13-120.jar, jdom.jar, jhall.jar, saaj.jar, tgfb.jar, ws-commons-util-1.0.2.jar, xmlrpc-client-3.1.jar, xmlrpc-common-3.1.jar"
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
}}}


{{{
#!html
<p style="border: thin solid red; padding: 3px;">NOTE: for this applet to run in harmony with the gsissh [[BR]]
portlet, they must share idential codebase, archive, separate_jvm, classloader_cache, and cache_archive [[BR]]
tags.  Without these being the same, the possibility of common classloader collisions increases dramatically [[BR]]
and both portlets will be rendered useless for the majority of users...see more info below.</p>
}}}

=== Applet Jars ===

The applet and server are bundled with their dependent libraries in descriptive directories within the [[BR]]
'lib' folder.

- lib
  - client: client only libraries.  these are not needed by the server and not in it's classpath
  - common: libraries shared between the client and server.  the client, help and server directory [[BR]]
contents are in the archive listing of the applet.
  - ext: bundled gsisshterm app and dependent libraries. distributed with client.
  - help: java help library
  - server: server only libraries


One important note is that the developers at NCSA who provided the GSI-SSHTerm library worked around [[BR]]
many of their dependency issues by redeploying all of their application's dependent jar class files in [[BR]]
their main GSI-SSHTerm-teragrid.jar library.  This is generally not a problem unless you're running [[BR]]
other applets that rely on the same classes.  The Java security model does not allow applets to share [[BR]]
access to previously loaded classes.  Thus if one person starts up a grid enabled app then another person [[BR]]
starts one up in another windows, say the gsissh and tgfm portlets respectively, then the tgfm will throw [[BR]]
a fit and quite because the bouncycastle library was already in the classloader and thus, it cannot relaod [[BR]]
the same class or remove the other class from the parent classloader.  Huge problem.  Luckily, tThis [[BR]]
problem can be avoided by either sharing a common applet archive or hacking the AppletClassLoader by way [[BR]]
of the Java Console (don't do this). For future reference, here are the bundled libraries in the [[BR]]
gsi-sshterm jar: </p>

 * puretls.jar // ssl implementation...that says not to repackage without giving credit :-)
 * cryptix.jar //crypto library
 * cryptix-32.jar //crypto library
 * cryptix-ansi1.jar //crypto library
 * log4j.jar //defacto logging libarary
 * jglobus.jar // globus core java libraries
 * java40.jar // netscape security classes commonly used to extend Java 1.1 functionality
 
 
=== Building the Clients ===
 
To build the applet jar, simply run the 'build.client' ant task by executing[[BR]]
 
$> ant build.client 
 
from the command line in the project root directory.[[BR]]
 
To build the desktop executable jar, simply run the 'build.exe' ant task. This is currently the default task,[[BR]]
so you can run it by just running
 
$> ant 
 
from the command line in the project root directory.[[BR]]
