/* 
 * Created on Apr 11, 2008
 * 
 * Developed by: Rion Dooley - dooley [at] tacc [dot] utexas [dot] edu
 * 				 TACC, Texas Advanced Computing Center
 * 
 * https://www.tacc.utexas.edu/
 */

package org.teragrid.portal.filebrowser.applet.transfer;

import java.awt.Frame;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.Provider;
import java.security.Security;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.SwingUtilities;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.globus.ftp.FileInfo;
import org.globus.ftp.exception.ClientException;
import org.globus.ftp.exception.FTPException;
import org.globus.ftp.exception.ServerException;
import org.globus.gsi.GlobusCredential;
import org.teragrid.portal.filebrowser.applet.util.LogManager;

import com.sshtools.common.authentication.KBIRequestHandlerDialog;
import com.sshtools.common.authentication.PasswordAuthenticationDialog;
import com.sshtools.common.authentication.PasswordChange;
import com.sshtools.common.authentication.PublicKeyAuthenticationPrompt;
import com.sshtools.common.configuration.SshToolsConnectionProfile;
import com.sshtools.j2ssh.SshClient;
import com.sshtools.j2ssh.authentication.AuthenticationProtocolState;
import com.sshtools.j2ssh.authentication.GSSAuthenticationClient;
import com.sshtools.j2ssh.authentication.KBIAuthenticationClient;
import com.sshtools.j2ssh.authentication.PasswordAuthenticationClient;
import com.sshtools.j2ssh.authentication.PublicKeyAuthenticationClient;
import com.sshtools.j2ssh.authentication.SshAuthenticationClient;
import com.sshtools.j2ssh.authentication.SshAuthenticationClientFactory;
import com.sshtools.j2ssh.configuration.ConfigurationLoader;
import com.sshtools.j2ssh.connection.Channel;
import com.sshtools.j2ssh.connection.ChannelEventAdapter;
import com.sshtools.j2ssh.session.SessionChannelClient;
import com.sshtools.j2ssh.transport.IgnoreHostKeyVerification;
import com.sshtools.j2ssh.transport.MessageStoreEOFException;
import com.sshtools.j2ssh.transport.TransportProtocolState;
import com.sshtools.sshterm.SshTerminalPanel;

@SuppressWarnings({"unused","unchecked"})
public class GsiSshClient {
    
    public final static int BANNER_TIMEOUT = 200;
    
    private SshClient ssh;
    private SessionChannelClient session;
    private SshToolsConnectionProfile cProfile;
    private Properties envProps;
    private String hostname;
    private int port = 22;
    private GlobusCredential cred;
    private GSSAuthenticationClient gssac;
    
    public GsiSshClient(String hostname, GlobusCredential cred) throws IOException {
        
        LogManager.debug("Starting gsissh session with " + hostname + ":");
        
        String name = new BouncyCastleProvider().getName();
        
        // remove the bc provider before the gsissh client fires up.
        Provider[] providers = Security.getProviders();
        
        for (int i=0;i<providers.length;i++) {
            LogManager.debug("Provider: " + providers[i].getName());
        }
//        
//        Security.removeProvider(name);
        
        this.hostname = hostname;
//        this.port = 22;
        this.cred = cred;
        
        ssh = new SshClient();
         // Implements an SSH client with methods to connect to a remote server and
         // perform all necessary SSH functions such as SCP, SFTP, executing commands,
         // starting the users shell and perform port forwarding.
        
//         There are several steps to perform prior to performing the desired task.
//         This involves the making the initial connection, authenticating the user
//         and creating a session to execute a command, shell or subsystem and/or
//         configuring the port forwarding manager.
        
//        To create a connection use the following code:<br>
         
//         Create a instance and connect SshClient
         
//         ssh = new SshClient();
         try {
             
             ConfigurationLoader.initialize(true);
             
             
             providers = Security.getProviders();
             
             for (int i=0;i<providers.length;i++) {
                 LogManager.debug("Provider: " + providers[i].getName());
             }
//             
//             Security.removeProvider(name);
             
//             ExtensionClassLoader ext = ConfigurationLoader.getExtensionClassLoader();
           //Get the System Classloader
//             ClassLoader extClassLoader = ext.getSystemClassLoader();
//             LogManager.debug("Extension classpath");
//             PrintClassLoader.print(ConfigurationLoader.getExtensionClassLoader().getSystemClassLoader());
//             LogManager.debug("Configuration Loader classpath");
//             PrintClassLoader.print(ConfigurationLoader.getContextClassLoader().getSystemClassLoader());             
//             LogManager.debug("GsiSshClient classpath");
//             PrintClassLoader.print(GsiSshClient.class.getClassLoader().getSystemClassLoader());                       
//             LogManager.debug("Establishing a connection to " + hostname);
             cProfile = new SshToolsConnectionProfile();
             cProfile.setHost(this.hostname);
             cProfile.setPort(this.port);
             cProfile.setApplicationProperty(SshTerminalPanel.PREF_SAVE_PROXY, false);
             gssac = new GSSAuthenticationClient();
             
             gssac.setProperties(cProfile);
             cProfile.addAuthenticationMethod(gssac);
             
             
             ssh.connect(cProfile, new IgnoreHostKeyVerification());
                     
    //         Once this code has executed and returned
    //         the connection is ready for authentication:<br>
//             SshConnectionProperties properties = new SshConnectionProperties();
//             properties.setHost(hostname);
//             properties.setPort(port);
//
//             GSSAuthenticationClient pwd = new GSSAuthenticationClient();
//             pwd.setProperties(properties);
             
             
    //         Authenticate the user
             boolean bAuthorized = authorize();
             if(bAuthorized) {
                 LogManager.debug("Successfully authenticated to lonestar.");
                 // Authentication complete
             } else 
                 throw new IOException("Failed to authorize to " + hostname);
             
             // Once authenticated the user's shell can be started:
             
//             LogManager.debug("Starting user shell.");
             
//             // Open a session channel
//             session = ssh.openSessionChannel();
//             session.addEventListener(new ChannelEventAdapter() {
//                 public void onChannelClose(Channel channel) {
//                   if (ssh != null) {
//                       LogManager.debug("Disconnecting...");
//                     ssh.disconnect();
//                   }
//                 }
//             });
//             
//             String searchCommand = "find . -name '*.*' -ls -maxdepth 1;echo meaningless_token\n";
//             
             // Request a pseudo terminal, if you do not you may not see the prompt
//             if(session.requestPseudoTerminal("ansi", 80, 24, 0, 0, "")) {
//                  // Start the users shell
//                  if(session.startShell()) {
////                      byte[] bIn = new byte[1024];
////                     session.bindInputStream(/*new InputStreamMonitor(*/
////                          new ByteArrayInputStream(bIn)/*)*/);
////                   DataOutputStream dos = new DataOutputStream(new ByteArrayOutputStream());
////                     session.bindOutputStream(/*new OutputStreamMonitor(*/
////                          dos/*)*/);
//                  
//                     // Do something with the session output
////                     dos.write("env\n".getBytes());
                     
//                      session.getOutputStream().write(searchCommand.getBytes());
//                      BufferedReader reader = new BufferedReader(
//                             new InputStreamReader(session.getInputStream()));
////                     
//                      String serversReply = reader.readLine();
//                     
//                      while ((serversReply = reader.readLine()) != null) {
////                         if (serversReply.trim().length() > 0) {
//                             LogManager.debug(serversReply);
////                         }
//                             if (serversReply.equals("meaningless_token")) break;
//                      }
//                      
////                      LogManager.debug("Finished find");
//                      
//                      session.getOutputStream().write("env;echo meaningless_token\n".getBytes());
//                      reader = new BufferedReader(
//                          new InputStreamReader(session.getInputStream()));
//                  
//                      serversReply = reader.readLine();
//                  
//                      while ((serversReply = reader.readLine()) != null) {
//                          LogManager.debug(serversReply);
//                          if (serversReply.equals("meaningless_token")) break;
//                      }
//                      
//                      LogManager.debug("Finished env");
//                   }
//             }
//             session.executeCommand(searchCommand);
//             ssh.disconnect();
             
         } catch (IOException e) {
             // TODO Auto-generated catch block
             e.printStackTrace();
             throw e;
         } finally {
//             ssh.disconnect();
         }
    }
    

    public String find(String sRootDir, String sFilter, int depth) throws IOException, ServerException,
            ClientException {
        Vector v = new Vector();
        
        String sDirPre = "";
//        String recursiveSearchPath = "." ;
//        for (int i=0;i<depth;i++) {
//            recursiveSearchPath += "/*";
//        }
        String key = "hello." + new Date().getTime();
        String searchCommand = "echo " + key + ";cd " + sRootDir + ";find . -name '" + sFilter + "' -ls; echo " + key;
//        String searchCommand = "find " + sRootDir + " -name '" + sFilter + "' -ls";
        String results = "";
        
        try {
            
            session = ssh.openSessionChannel();
            session.addEventListener(new ChannelEventAdapter() {
                public void onChannelClose(Channel channel) {
                  if (ssh != null) {
                      LogManager.debug("Disconnecting...");
                      ssh.disconnect();
                  }
                }
            });
            
//            LogManager.debug("Starting user shell.");
            
            if(session.requestPseudoTerminal("ansi", 80, 24, 0, 0, "")) {
                // Start the users shell
                if(session.startShell()) {
                    
                    session.getOutputStream().write((searchCommand +"\n").getBytes());
//                    session.executeCommand(searchCommand);
                    
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(session.getInputStream()));
                    
//                    session.getOutputStream().write(("echo " + key).getBytes());
                    
//                    session.getOutputStream().write(("echo " + key + "\n").getBytes());
                    
                    String serversReply = "";
                    String oldReply = "";
                    
                    // parse through banner and prompt
                    while ((serversReply = reader.readLine()) != null && 
                            !(serversReply.indexOf(key) > -1 && serversReply.indexOf("echo") == -1 )) {
                        
//                        LogManager.debug(serversReply);
//                        if (oldReply.equals(serversReply)) {
//                            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
//                            session.executeCommand(br.readLine()+"\n");
//                        }
//                        if ( && ) {
//                            reader.readLine();
//                            break;
//                        }
                    }
                    
//                    LogManager.debug("Parsed out banner");
//                    serversReply = reader.readLine();
                    // now parse the file listings
                    while ((serversReply = reader.readLine()) != null) {
        
//                        LogManager.debug(serversReply);
                        
                        if (serversReply.indexOf(key) > -1 && serversReply.indexOf("echo") == -1) break;
                 
                        
            //        String[] sLines = o.toString().split("\r\n");
                        
            //            for (int i = 0, l = sLines.length; i < l; i++) {
//                        if (serversReply.startsWith("total")) {
//                            continue;
//                        } else if (serversReply.startsWith("/")) {
//                            sDirPre = serversReply.substring(0, serversReply.length());
//                            continue;
//                        }
                        if (serversReply.indexOf("Permission denied") == -1)
                            results += serversReply + "\r\n";
                        
//                        try {
//                            FileInfo f = new FileInfo(serversReply);
//                            f.setName(sDirPre + f.getName());
//                            v.add(f);
//                        } catch (FTPException ex) {
//                        }
//                        }
                    }
                }
//                LogManager.debug("Finished find");
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            throw e;
        } finally {
//            ssh.disconnect();
        }
        
        return results;
        
    }
    
    public Properties env() throws IOException, ServerException,
        ClientException {
        Properties props = new Properties();
        
        String sEnv = "echo hello;resoft;env;echo meaningless_token";
        String results = "";
        try {
            
            
            session = ssh.openSessionChannel();
            
            session.addEventListener(new ChannelEventAdapter() {
                public void onChannelClose(Channel channel) {
                  if (ssh != null) {
                      LogManager.debug("Disconnecting...");
                    ssh.disconnect();
                  }
                }
            });
            
            LogManager.debug("Starting user shell.");
            
//            if(session.requestPseudoTerminal("ansi", 80, 24, 0, 0, "")) {
//                LogManager.debug("Got pseudoterminal");
//                // Start the users shell
//                if(session.startShell()) {
//                    LogManager.debug("User shell started.");
//                    
//                    session.getOutputStream().write((sEnv + "\n").getBytes());
            if (session.executeCommand("env")) {
                LogManager.debug("Wrote command.");
                
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(session.getInputStream()));
                
                LogManager.debug("Reading response...");
                
                String serversReply = "";
                
                
                // parse through banner and prompt
//                while ((serversReply = reader.readLine()) != null) {
//                    
//                    LogManager.debug(serversReply);
//                    
//                    if (serversReply.indexOf(sEnv) > -1) {
//                        reader.readLine();
//                        break;
//                    }
//                }
                
                // now parse the environment
                while ((serversReply = reader.readLine()) != null) {
                    
                  LogManager.debug(serversReply);
//                    results += serversReply + "\r\n";
//                    if (serversReply.equals("meaningless_token")) break;
                    
                    if (serversReply.indexOf("PS1") > -1) continue; 
                      
                    props.load(new ByteArrayInputStream(serversReply.getBytes()));
                  
                }
//                }
            }
            LogManager.debug("Finished env");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
//            ssh.disconnect();
            FileWriter fw = new FileWriter("props");
            fw.write(results);
            fw.close();

        }
        

        
        return props;
    }


    /**
     * @param args
     */
    public static void main(String[] args) {
        GsiSshClient client = null;
        try {
//            client = new GsiSshClient("cu.ncsa.uiuc.edu",22,GridFTP.authorize("/tmp/x509up_u501"));
//            try {
//                String[] sites = new String[]{
////                        "queenbee.loni-lsu.teragrid.org", 
////                        "tg-login.purdue.teragrid.org", 
////                        "login-co.ncsa.teragrid.org", 
////                        "bglogin.sdsc.edu", 
////                        "login.bigred.iu.teragrid.org", 
////                        "tg-login.uc.teragrid.org", 
////                        "tg-login.ncsa.teragrid.org", 
////                        "tg-login.sdsc.teragrid.org", 
////                        "tg-viz-login.uc.teragrid.org", 
////                        "login-w.ncsa.teragrid.org", 
////                        "ds001.sdsc.edu", 
////                        "dslogin.sdsc.edu", 
////                        "tg-viz-login.tacc.teragrid.org",
//                        "lonestar.tacc.utexas.edu"
//                };
//                for (String site: java.util.Arrays.asList(sites)){
//                    try { 
//                        client = new GsiSshClient(site,22,GridFTP.authorize("/tmp/x509up_u501"));
//                        LogManager.debug("Parsing file command for " + site + "...");
//                        parseCommand(client.find(".", "*.*", 1));
//                    } catch (Exception e) {
////                        e.printStackTrace();
//                        LogManager.debug(e.getMessage());
//                    }
//                    
//                    
//                    
//                }
//                
//            } catch (Exception e1) {
//                e1.printStackTrace();
//                throw new ResourceException("TG resource listing temporarily unavailable.",e1);
//            }
            
            
            
            
//            client = new GsiSshClient("lonestar.tacc.utexas.edu",22,GridFTP.authorize("/tmp/x509up_u501"));
//            client = new GsiSshClient("maverick.tacc.utexas.edu",22,GridFTP.authorize("/tmp/x509up_u501"));
            
            LogManager.debug("Retrieving user environment....");
            
            Properties props = client.env();
            
            LogManager.debug("Successfully retrieved " + props.size() + " entries from the environment.");
            
            for (Object key: props.keySet()) {
                LogManager.debug((String)key + "=" + (String)props.get((String)key));
            }
           
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            client.close();
        }
        
    }
    public static void parseCommand(String sResults) {
        
       
        LogManager.debug("Searching for all user files one level deep.");
        
        Vector v = new Vector();
        
        
//            LogManager.debug(sResults);
        String[] sLines = sResults.split("\r\n");
        
        for (int i=0;i<sLines.length;i++) {
            StringTokenizer tokens = new StringTokenizer(sLines[i]);
            String sLineItem = "";
            
            tokens.nextToken();
            tokens.nextToken();
            
            while (tokens.hasMoreTokens()) {
                sLineItem += tokens.nextToken() + " ";
            }
            
            LogManager.debug("Parsing line: " + sLineItem);
            
            try {
//                    if (!parseLine(sLineItem)) {
//                        break;
//                    }
                FileInfo f = new FileInfo(sLineItem);
                f.setName(f.getName());
                v.add(f);
                
                LogManager.debug(f.toString());
            } catch (Exception ex) {
//                ex.printStackTrace();
            }
        }
        
        LogManager.debug("Command returned " + v.size() + " results");
//            LogManager.debug(listing);
                
    }
    
    public static boolean parseLine(String reply) throws FTPException {
        byte UNKNOWN_TYPE   = 0;
        byte FILE_TYPE      = 1;
        byte DIRECTORY_TYPE = 2;
        byte SOFTLINK_TYPE  = 3;
        byte DEVICE_TYPE  = 4;

        String UNKNOWN_STRING = "?";  
        int UNKNOWN_NUMBER = -1;  

        long size = UNKNOWN_NUMBER;
        String name = UNKNOWN_STRING;
        String date = UNKNOWN_STRING;
        String time = UNKNOWN_STRING;
        byte fileType;
        int mode = 0;
          
        if (reply == null) return false;

        StringTokenizer tokens = new StringTokenizer(reply);
        String token, previousToken;

        int numTokens = tokens.countTokens();

        if (numTokens < 8) {
          throw new FTPException(FTPException.UNSPECIFIED,
                     "Invalid number of tokens in the list reply [" + 
                     reply + "]");
        }

        token = tokens.nextToken();

        // permissions
        switch( token.charAt(0) ) {
        case 'd':
          LogManager.debug("Permission: " + DIRECTORY_TYPE); break;
        case '-':
            LogManager.debug("Permission: " + FILE_TYPE); break;
        case 'l':
            LogManager.debug("Permission: " + SOFTLINK_TYPE); break;
        case 'c':
        case 'b':
        // do not try to parse device entries;
        // they aren't important anyway
            LogManager.debug("Permission: " + DEVICE_TYPE); 
            return false;
        default:
            LogManager.debug("Permission: " + UNKNOWN_TYPE);
        }

        try {
          for(int i=1;i<=9;i++) {
              if (token.charAt(i) != '-') {
                  mode += 1 << (9 - i);
              }
          }
        } catch (IndexOutOfBoundsException e) {
          throw new FTPException(FTPException.UNSPECIFIED,
                               "Could not parse access permission bits" + token);
        }

        
        // ??? can ignore
        tokens.nextToken();
                    
        // next token is the owner
        tokens.nextToken();
       
        // In ls from System V, next token is the group
        // In ls from Berkeley (BSD), group token is missing
        previousToken = tokens.nextToken();

        // size
        token = tokens.nextToken();

        /*
         * if the group is missing this will try to parse the date field
         * as an integer and will fail. if so, then the previous field is the size field
         * and the current token is part of the date. 
         */
        try {
            LogManager.debug("Size: " +  Long.parseLong(token) );
          token = null;
        } catch(NumberFormatException e) {
          // this might mean that the group is missing
          // and this token is part of date.
          try {
              LogManager.debug("Size: " +  Long.parseLong(previousToken) );
          } catch(NumberFormatException ee) {
          throw new FTPException(FTPException.UNSPECIFIED,
                       "Invalid size number in the ftp reply [" + 
                       previousToken + ", " + token + "]");
          }
        }

        // date - two fields together
        if (token == null) {
            token = tokens.nextToken();
        }
        String month = token;
        LogManager.debug("Date: " + token + " " + tokens.nextToken());

        //next token is either date or time
        token = tokens.nextToken();
        LogManager.debug("Time: " + token);
        time = token;
        // this is to handle spaces in the filenames
        // as well filenames with dates in them
        int ps = reply.indexOf(month);
        if (ps == -1) {
        // this should never happen
        throw new FTPException(FTPException.UNSPECIFIED,
                       "Could not find date token");
        } else {
            ps = reply.indexOf(time, ps+month.length());
            if (ps == -1) {
                // this should never happen
                throw new FTPException(FTPException.UNSPECIFIED,
                           "Could not find time token");
            } else {
                LogManager.debug("Name: " + reply.substring(1+ps+time.length()));
            }
        }
        
        return true;
    }
    
    public void close() {
        LogManager.debug("User chose to close the current gsissh session.");
        try {
            for(Object channel: ssh.getActiveChannels())
                ((Channel)channel).close();
        } catch (Exception e) {
            LogManager.error("Failed to close ssh channel",e);
        }
        
        ssh.disconnect();
            
        
    }
    
    public void abort() {
        LogManager.debug("User chose to abort the current gsissh session.");
        ssh.disconnect();
        
    }
    
    public SshToolsConnectionProfile getCurrentConnectionProfile() {
        return this.cProfile;
    }
    
    public boolean authorize() throws IOException{
        try {
            // We should now authenticate
            int result = AuthenticationProtocolState.READY;

            // Our authenticated flag
            boolean authenticated = false;

            // Get the supported authentication methods
            java.util.List auths = SshAuthenticationClientFactory
                .getSupportedMethods();
            
            LogManager.debug("Got supported methods.");

            // Get the available methods
            java.util.List supported = null;
            supported = ssh.getAvailableAuthMethods(getCurrentConnectionProfile()
                                .getUsername());
            
            LogManager.debug("Got available methods.");

            if(supported==null) throw new IOException("There are no authentication methods which both the server and client understand.\n Cannot connect.\n No shortcuts or searches will be allowed");

            // If the server supports external key exchange then we SHOULD use it --- Not really.  this is a TG File Manager.  Grid only
//            if (supported.contains("external-keyx")) {
//    
//                EKEYXAuthenticationClient aa = new EKEYXAuthenticationClient();
//                aa.setProperties(getCurrentConnectionProfile());
//                aa.setUsername(getCurrentConnectionProfile().getUsername());
//                result = ssh.authenticate(aa,getCurrentConnectionProfile().getHost());
//    
//                if (result == AuthenticationProtocolState.COMPLETE) {
//                    authenticationComplete(newProfile);
//                    return true;
//                }
//            }
            
            // If the server supports public key lets look for an agent and try --- Not really.  this is a TG File Manager.  Grid only
            // some of his keys
//            if (supported.contains("publickey")) {
//                if (System.getProperty("sshtools.agent") != null) {
//                    try {
//                    SshAgentClient agent = SshAgentClient.connectLocalAgent("SSHTerm",
//                                                System.getProperty("sshtools.agent") /*, 5*/);
//    
//                    AgentAuthenticationClient aac = new AgentAuthenticationClient();
//                    aac.setAgent(agent);
//                    aac.setUsername(getCurrentConnectionProfile().getUsername());
//                    result = ssh.authenticate(aac,getCurrentConnectionProfile().getHost());
//    
//                    agent.close();
//                    }
//                    catch (AgentNotAvailableException ex) {
//                    log.info("No agent was available for authentication");
//    
//                    // Just continue
//                    }
//    
//                    if (result == AuthenticationProtocolState.COMPLETE) {
//                    authenticationComplete(newProfile);
//    
//                    return true;
//                    }
//                }
//            }

            // Create a list for display that will contain only the
            // supported and available methods
            java.util.List display = new java.util.ArrayList();

            // Did we receive a banner from the remote computer
            final String banner = ssh.getAuthenticationBanner(BANNER_TIMEOUT);
//
            LogManager.debug("Got banner\n" + banner);
//            
//            if (banner != null) {
//            if (!banner.trim().equals("")) {
//                try {
//                SwingUtilities.invokeAndWait(new Runnable() {
//                    public void run() {
//                        BannerDialog.showBannerDialog(new Frame(),
//                                      banner);
//                    }
//                    });
//                }
//                catch (Exception e) {
//                    e.printStackTrace();
//                    LogManager.debug("Failed to invoke and wait on BannerDialog");
//                }
//            }
//            }

            // Are there any authentication methods within the properties file?
            // Iterate through selecting only the supported and available
            Iterator it = supported.iterator();

            LinkedList allowed = new LinkedList();

            while (it.hasNext() && !authenticated) {
                Object obj = it.next();
    
                if (auths.contains(obj)) {
                    display.add(obj);
                    allowed.add(obj);
                    LogManager.debug(obj.toString());
                }
            }

            // First look to see if we have any authenticaiton methods available
            // in the profile properties object as this will overide a manual selection
            java.util.Map authMethods = (Map) ( (HashMap) getCurrentConnectionProfile()
                            .getAuthenticationMethods())
            .clone();
            
            LogManager.debug("Profile properties has auth methods: ");
            for (Object o: authMethods.entrySet()) {
                LogManager.debug( o.toString());
                
            }
            
            it = authMethods.entrySet().iterator();

            //Iterator it2 = null;
            java.util.List selected;

            // Loop until the user either cancels or completes
            boolean completed = false;
            Map.Entry entry = (Map.Entry) it.next();
            SshAuthenticationClient auth = (SshAuthenticationClient) entry.getValue();
            
            String msg = null;

            while (!completed
               && (ssh.getConnectionState().getValue() !=
               TransportProtocolState.DISCONNECTED)) {
//                auth = null;
//                // Select an authentication method from the properties file or
//                // prompt the user to choose
//                if (it.hasNext()) {
//                    Object obj = it.next();
//    
//                    if (obj instanceof Map.Entry) {
//                        entry = (Map.Entry) obj;
//                        auth = (SshAuthenticationClient) entry.getValue();
//                    }
//                    else if (obj instanceof String) {
//                        LogManager.debug("Setting auth technique to be " + (String) obj);
//                        auth = SshAuthenticationClientFactory.newInstance( (String) obj, getCurrentConnectionProfile());
//                        auth.setUsername(getCurrentConnectionProfile().getUsername());
//                    }
//                    else {
//                        closeConnection(true);
//                        throw new IOException(
//                                  "Iterator of Map or List of String expected");
//                    }
//                }
//                else {
//                    LogManager.debug("Prompting for authentication dialog");
//                    selected = AuthenticationDialog.showAuthenticationDialog(new Frame(),
//                                                 display, ( (msg == null) ? "" : msg));
//                    LogManager.debug("Selected " + selected.size() + "auth techniques");
//                    if (selected.size() > 0) {
//                        for (Object o: selected) {
//                            LogManager.debug(o);
//                        }
//                        
//                        it = selected.iterator();
//                        
//                    }
//                    else {
//                        closeConnection(true);
//    
//                        return false;
//                    }
//                }
                if(auth!=null && !allowed.contains(auth.getMethodName())) auth=null;
                if (auth != null) {
                    // The password authentication client can act upon requests to change the password
    
                    /* if (auth instanceof PasswordAuthenticationClient) {
                       PasswordAuthenticationDialog dialog = new PasswordAuthenticationDialog(SshTerminalPanel.this);
                       ((PasswordAuthenticationClient) auth).setAuthenticationPrompt(dialog);
                       ( (PasswordAuthenticationClient) auth)
                       .setPasswordChangePrompt(PasswordChange.getInstance());
                       PasswordChange.getInstance().setParentComponent(
                       SshTerminalPanel.this);
                       }*/
    
                    // Show the implementations dialog
                    // if(auth.showAuthenticationDialog()) {
                    // Authentication with the details supplied
                    try {
    
                        result = showAuthenticationPrompt(auth); //ssh.authenticate(auth);
                        ((GSSAuthenticationClient)auth).getAuthenticationPrompt();
                    } catch(IllegalArgumentException e) {// Do not use this authentication method!
                        allowed.remove(auth);
                    }
    
                    if (result == AuthenticationProtocolState.FAILED) {
                        msg = auth.getMethodName()
                            + " authentication failed, try again?";
                    }
    
                    // If the result returned partial success then continue
                    if (result == AuthenticationProtocolState.PARTIAL) {
                        // We succeeded so add to the connections authenticaiton
                        // list and continue on to the next one
                        getCurrentConnectionProfile().addAuthenticationMethod(auth);
                        msg = auth.getMethodName()
                            + " authentication succeeded but another is required";
                    }
    
                    if (result == AuthenticationProtocolState.COMPLETE) {
                        authenticated = true;
        
                        //If successfull add to the connections list so we can save later
                        getCurrentConnectionProfile().addAuthenticationMethod(auth);
        
                        // Set the completed flag
                        completed = true;
//                        authenticationComplete(false);
                    }
    
                    if (result == AuthenticationProtocolState.CANCELLED) {
                        ssh.disconnect();
        
                        return false;
                    }
    
                    //   }
                    //  else {
                    // User has cancelled the authenticaiton
                    //       closeConnection(true);
                    //       return false;
                    //  }
                }
    
                // end of if
            }

            // end of while
            return authenticated;
        } catch(EOFException e) {
            throw new IOException("The remote host has closed the connection.\n\nAs you are authenticating this probably means the server has given up authenticating you.");
        } catch(MessageStoreEOFException e) {
            throw new IOException("The remote host has closed the connection.\n\nAs you are authenticating this probably means the server has given up authenticating you.");
        }
    }
    
    public Properties getProperties() throws ClientException, ServerException, IOException {
        if (this.envProps == null || this.envProps.size() == 0) 
            this.envProps = env();
        
        return this.envProps;
    }
    
    protected void closeConnection(boolean bSave) {
        close();
    }
    
    protected int showAuthenticationPrompt(SshAuthenticationClient instance) throws
    IOException, IllegalArgumentException {
      instance.setUsername(getCurrentConnectionProfile().getUsername());
    
      if (instance instanceof PasswordAuthenticationClient) {
        PasswordAuthenticationDialog dialog = new PasswordAuthenticationDialog( (
            Frame) SwingUtilities
            .getAncestorOfClass(Frame.class,
                                new Frame()));
        instance.setAuthenticationPrompt(dialog);
        ( (PasswordAuthenticationClient) instance).setPasswordChangePrompt(
            PasswordChange
            .getInstance());
        PasswordChange.getInstance().setParentComponent(
                new Frame());
      }
      else if (instance instanceof PublicKeyAuthenticationClient) {
        PublicKeyAuthenticationPrompt prompt = new PublicKeyAuthenticationPrompt(
                new Frame());
        instance.setAuthenticationPrompt(prompt);
      }
      else if (instance instanceof KBIAuthenticationClient) {
        KBIAuthenticationClient kbi = new KBIAuthenticationClient();
        ( (KBIAuthenticationClient) instance).setKBIRequestHandler(new
            KBIRequestHandlerDialog(
            (Frame) SwingUtilities.getAncestorOfClass(Frame.class,
                    new Frame())));
      }
      return ssh.authenticate(instance, getCurrentConnectionProfile().getHost());
    }
}

class PrintClassLoader {
    public static void print(ClassLoader cl) {
      //Get the URLs
        java.net.URL[] urls = ((java.net.URLClassLoader)cl).getURLs();

        for(int i=0; i< urls.length; i++)
        {
            LogManager.debug(urls[i].getFile());
        }      
    }
}
