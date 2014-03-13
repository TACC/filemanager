/*Copyright (c) 2004,University of Illinois at Urbana-Champaign.  All rights reserved.
 * 
 * Created on Feb 6, 2006
 * 
 * Developed by: CCT, Center for Computation and Technology, 
 *              NCSA, University of Illinois at Urbana-Champaign
 *              OSC, Ohio Supercomputing Center
 *              TACC, Texas Advanced Computing Center
 *              UKy, University of Kentucky
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

package edu.utexas.tacc.wcs.filemanager.service.resources;

import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.globus.ftp.FileInfo;
import org.restlet.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.utexas.tacc.wcs.filemanager.common.model.Task;
import edu.utexas.tacc.wcs.filemanager.common.model.Transfer;
import edu.utexas.tacc.wcs.filemanager.common.model.User;

/**
 * No actual test, but only test data initialization.  Sensitive parameters are
 * read from the properties file included in the checkout (GMS_HOME/gms.properties).
 *
 * @author Rion Dooley < dooley [at] cct [dot] lsu [dot] edu >
 */
public abstract class TestCaseWithData  
{    
	private static final Logger logger = Logger.getLogger(TestCaseWithData.class);
	
    protected static final String TEST_PROPERTIES_LOCATION = "test.properties";
    protected static final String DN_KEY = "test.dn";
    protected static final String BADDN_KEY = "test.baddn";
    protected static final String TRANSFER_ID_KEY = "test.transfer.id";
    protected static final String NOTIFICATION_ID_KEY = "test.notification.id";
    
    // properties for TGProfile service tests.
    protected static final String USERNAME_KEY = "test.username";
    protected static final String EMAIL_KEY = "test.email";
    protected static final String ORGANIZATION_KEY = "test.organization";
    protected static final String DEPARTMENT_KEY = "test.department";
    protected static final String FIRST_NAME_KEY = "test.first.name";
    protected static final String LAST_NAME_KEY = "test.last.name";
    
	// Keep references to domain objects
	protected static User user;
	
	protected static Transfer transfer = null;
	
	protected static Properties props = new Properties();
    
	protected static String dn;
	
	protected static String baddn;
	
	protected static String transferId;
	
	protected static String notificationId;
	
	protected static String firstName;
	
	protected static String lastName;
	
	protected static String email;
	
	protected static String organization;
	
	protected static String department;
	
	protected static String username; 

	// ********************************************************** //

//	protected void runTest() throws Throwable {
//        try {
//            System.out.println("Running test " + getName() + "...");
//            super.runTest();
//            // don't overwrite the properties file if the app bonks!
//            props.store(new FileOutputStream(Thread.currentThread().getContextClassLoader().getResource(TEST_PROPERTIES_LOCATION).getPath()),"");
//        } catch (Throwable e) {
//            e.printStackTrace();
//            HibernateUtil.rollbackTransaction();
//            throw e;
//        } finally{
////          HibernateUtil.closeSession();
//        }
//    }
	
	/**
     * Create test data for our domain model.
     * 
     * @throws Exception 
     * 
     */
    @BeforeClass
    protected void setUp() throws Exception {
        
        props.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(TEST_PROPERTIES_LOCATION));
        
        for(Object key: props.keySet()) {
            logger.debug("Property: " + key + ", " + props.getProperty((String)key));
        }
        
        dn = props.getProperty(DN_KEY);
        
        baddn = props.getProperty(BADDN_KEY);
        
        transferId = props.getProperty(TRANSFER_ID_KEY);
        
        notificationId = props.getProperty(NOTIFICATION_ID_KEY);
        
        username = props.getProperty(USERNAME_KEY);
        
        email = props.getProperty(EMAIL_KEY);
        
        organization = props.getProperty(ORGANIZATION_KEY);
        
        department = props.getProperty(DEPARTMENT_KEY);
        
        firstName = props.getProperty(FIRST_NAME_KEY);
        
        lastName = props.getProperty(LAST_NAME_KEY);
    }
    
	public Transfer createTransfer() {
        
        Transfer transfer = new Transfer();
        
        transfer.setEpr("");
        transfer.setDn(dn);
        transfer.setStatus(Task.WAITING);
        transfer.setPara(0);
        transfer.setParaId(0);
        transfer.setSpeed(100);
        transfer.setProgress(0);
        transfer.setSource("ftp://Local://Users/dooley/tmp1.txt");
        transfer.setDest("ftp://Local:-1//Users/dooley/wp-1/tmp1.txt");
        transfer.setFileName("tmp1.txt");
        transfer.setFileDate(new Date().toString());
        transfer.setFileType(FileInfo.FILE_TYPE);
        transfer.setFileSize(1024);
        transfer.setFileTime("0:0");
        transfer.setCreated(Calendar.getInstance());
        
        return transfer;
    }	
	
	protected JsonNode parseApiResponse(Response response) 
	{	
		ObjectMapper mapper = new ObjectMapper();
		JsonNode result = null;
		try 
		{
			JsonNode json = mapper.readTree(response.getEntityAsText());
			if (json.has("result")) {
				result = json.get("result");
			} 
		}
		catch (Exception e) {
			Assert.fail("Failed to parse resonse from API.", e);
		}
		
		return result;
	}

	
	
//	public FileTransferTask createFileTransferTask() {
//	    Transfer transfer = createTransfer();
//	    
//	    FileInfo fileInfo = new FileInfo();
//	    fileInfo.setName(transfer.getFileName());
//	    fileInfo.setSize(transfer.getFileSize());
//	    fileInfo.setTime(transfer.getFileTime());
//	    fileInfo.setDate(transfer.getFileDate());
//	    fileInfo.setFileType(FileInfo.FILE_TYPE);
//	    
//	    FileTransferTask fileTransferTask = new FileTransferTask(fileInfo, 
//	            FTPSettings.Local, FTPSettings.Local, "/Users/dooley/", "/Users/wp=1/");
//	    
//	    return fileTransferTask;
//	    
//	}
}
