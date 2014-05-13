/* 
 * Created on Apr 11, 2008
 * 
 * Developed by: Rion Dooley - dooley [at] tacc [dot] utexas [dot] edu
 * 				 TACC, Texas Advanced Computing Center
 * 
 * https://www.tacc.utexas.edu/
 */

package edu.utexas.tacc.wcs.filemanager.client.transfer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;

import org.apache.airavata.gsi.ssh.api.CommandExecutor;
import org.apache.airavata.gsi.ssh.api.CommandInfo;
import org.apache.airavata.gsi.ssh.api.SSHApiException;
import org.apache.airavata.gsi.ssh.api.ServerInfo;
import org.apache.airavata.gsi.ssh.config.ConfigReader;
import org.apache.airavata.gsi.ssh.impl.RawCommandInfo;
import org.apache.airavata.gsi.ssh.impl.StandardOutReader;
import org.apache.airavata.gsi.ssh.impl.authentication.PropertyAuthenticationInfo;
import org.globus.common.CoGProperties;
import org.globus.ftp.FileInfo;
import org.globus.ftp.exception.ClientException;
import org.globus.ftp.exception.FTPException;
import org.globus.ftp.exception.ServerException;
import org.ietf.jgss.GSSCredential;

import edu.utexas.tacc.wcs.filemanager.client.ConfigOperation;
import edu.utexas.tacc.wcs.filemanager.client.exception.RemoteExecutionException;
import edu.utexas.tacc.wcs.filemanager.client.util.LogManager;
import edu.utexas.tacc.wcs.filemanager.common.model.enumeration.FileProtocolType;


@SuppressWarnings({"unused"})
public class GsiSshClient {
    
    public final static int BANNER_TIMEOUT = 200;
    
    private Properties envProps;
    private String hostname;
    private int port = 22;
    private GSSCredential cred;
    
    public GsiSshClient(String hostname, GSSCredential cred) 
    {   
        this(hostname, 22, cred);
    }
    
    public GsiSshClient(String hostname, int port, GSSCredential cred)
    {    
        LogManager.debug("Starting gsissh session with " + hostname + ":" + port);
        
        this.hostname = hostname;
        this.cred = cred;
        this.port = (Integer.valueOf(port) == null ? FileProtocolType.GRIDFTP.getDefaultPort() : port);
    }

    public String find(String sRootDir, String sFilter, int depth) 
    throws RemoteExecutionException
    {
        String sDirPre = "";
        String key = "hello." + new Date().getTime();
        String command = "echo " + key + ";cd " + sRootDir + ";find . -name '" + sFilter + "' -ls; echo " + key;
        
        return runCommand(command);
    }
    
    /**
     * Run an arbitrary command on a remote system using gsi auth.
     * 
     * @param command
     * @return
     * @throws RemoteExecutionException 
     * @throws Exception
     */
    private String runCommand(String command) throws RemoteExecutionException
	{
		LogManager.debug("Forking command " + command + " on " + hostname + ":" + port);
		
		PropertyAuthenticationInfo authenticationInfo = new PropertyAuthenticationInfo(cred, ConfigOperation.getCertificateDir());
        // Create command
        CommandInfo commandInfo = new RawCommandInfo(command);

        // Server info
        ServerInfo serverInfo = new ServerInfo("", hostname, port);
        
        // Output
        StandardOutReader commandOutput = new StandardOutReader();

        // Execute command
        try {
			CommandExecutor.executeCommand(commandInfo, serverInfo, authenticationInfo, commandOutput, new ConfigReader());
		} catch (SSHApiException e) {
			throw new RemoteExecutionException("Failed to execute command " + command + " on " + hostname + ":" + port, e);
		} catch (IOException e) {
			throw new RemoteExecutionException("Failed to execute command " + command + " on " + hostname + ":" + port, e);
		}
        
        return commandOutput.getStdOutputString();
	}
    
    public Properties env() 
    {
        Properties props = new Properties();
        ByteArrayInputStream in = null;
        try 
        {
            String response = runCommand("env");
            
            in = new ByteArrayInputStream(response.getBytes());
            props.load(in);
            
            LogManager.debug("Finished env");
        } 
        catch (Exception e) {
            LogManager.error("Failed to retrieve user environment", e);
        } finally {
            try { in.close(); } catch (Exception e) {}
        }
        
        return props;
    }


    /**
     * @param args
     */
    public static void main(String[] args) 
    {
        GsiSshClient client = null;
        try 
        {
            LogManager.debug("Retrieving user environment....");
            
            client = new GsiSshClient("lonestar.tacc.utexas.edu", (GSSCredential)GridFTP.authorize("/tmp/x509up_u503"));
            
            Properties props = client.env();
            
            LogManager.debug("Successfully retrieved " + props.size() + " entries from the environment.");
            
            for (Object key: props.keySet()) {
                LogManager.debug((String)key + "=" + (String)props.get((String)key));
            }
        } catch (Exception e) {
            LogManager.error("Failed to retrieve user environment.", e);
        } finally {
            client.close();
        }
        
    }
    public static void parseCommand(String sResults) {
        
       
        LogManager.debug("Searching for all user files one level deep.");
        
        Vector<FileInfo> v = new Vector<FileInfo>();
        
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
            
            try 
            {
                FileInfo f = new FileInfo(sLineItem);
                f.setName(f.getName());
                v.add(f);
            } 
            catch (Exception ex) {
            	LogManager.error("Failed to parse line: " + sLineItem, ex);
            }
        }
        
        LogManager.debug("Command returned " + v.size() + " results");  
    }
    
    public static boolean parseLine(String reply) throws FTPException 
    {
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
    
    public void close() {}
    
    public void abort() {}
    
    public boolean authorize() {
        return true;
    }
    
    public Properties getProperties() throws ClientException, ServerException, IOException {
        if (this.envProps == null || this.envProps.size() == 0) 
            this.envProps = env();
        
        return this.envProps;
    }
    
    protected void closeConnection(boolean bSave) {
        close();
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
