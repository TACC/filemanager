/* 
 * Created on Jun 20, 2008
 * 
 * Developed by: Rion Dooley - dooley [at] tacc [dot] utexas [dot] edu
 * 				 TACC, Texas Advanced Computing Center
 * 
 * https://www.tacc.utexas.edu/
 */

package org.teragrid.service.profile.util;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.WeakHashMap;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.teragrid.service.profile.wsclients.model.ComputeDTO;
import org.teragrid.service.profile.wsclients.model.Service;

import edu.utexas.tacc.wcs.filemanager.common.model.System;
import edu.utexas.tacc.wcs.filemanager.common.model.ResourceMapping;
import edu.utexas.tacc.wcs.filemanager.common.util.DBUtil;
import edu.utexas.tacc.wcs.filemanager.service.Settings;

/**
 * Utility class to resolve the TGCDB and inca short names with the
 * actual resource hostnames and gridftp endpoints.
 * 
 * @author Rion Dooley < dooley [at] tacc [dot] utexas [dot] edu >
 *
 */
public class TeraGridSystemResolver {

	private static final Logger logger = Logger.getLogger(TeraGridSystemResolver.class);
	
    /**
     * Adjusts the bad information coming from the db with accurate
     * hostname and user info.  
     * 
     * @param systems  List from TeraGridSystem accounts hydration
     * @return a list of valid TeraGridSystem objects
     */
    public static List<System> resolveResources(List<System> dbSystems) 
    {
       
    	WeakHashMap<String,String> storageMap = DBUtil.getMassStorageMap();
    	WeakHashMap<String,String> typeMap = DBUtil.getTypeMap();
       
    	List<System> resolvedSystems = new ArrayList<System>();
       
    	Set <ComputeDTO> cachedSystems = ResourceCache.getResources();
       
    	for(System dbSystem: dbSystems) 
    	{
    		for (ComputeDTO cachedSystem: cachedSystems) 
    		{
    			if (cachedSystem.getTgcdbName().equals(dbSystem.getResourceName())) 
    			{
    				if (cachedSystem.getStatus().equals(Service.UP)) 
    				{
    					dbSystem.setResourceName(cachedSystem.getName());
    					dbSystem.setSSHHostname(cachedSystem.getLoginHostname());
						dbSystem.setFTPHostname(cachedSystem.getGridftpHostname());
						dbSystem.setStatus(cachedSystem.getStatus());
						dbSystem.setResourceId(cachedSystem.getResourceId());
						String type = (String)typeMap.get(cachedSystem.getTgcdbName());
						if (!StringUtils.isEmpty(type)) {
							dbSystem.setType(DBUtil.HPC);
						} else {
							dbSystem.setType(type);
						}
						dbSystem.setInstitution(cachedSystem.getSite());
						resolvedSystems.add(dbSystem);
    				}
   				
	   				// check to see if the last system site's storage server
	                // needs to be added to the resource list
	                String storageSystemName = storageMap.get(dbSystem.getInstitution());
	                if (storageSystemName != null && !storageSystemName.equals("NONE")) 
	                {
	                	System storageSystem = getStorageSystem(storageSystemName,dbSystem.getInstitution());
	                    storageSystem.setId(dbSystem.getId());
	                    storageSystem.setUserId(dbSystem.getUserId());
	                    storageSystem.setUserName(dbSystem.getUserName());
	                    storageSystem.setUserState(dbSystem.getUserState());
	                    storageSystem.setStatus(Service.UP);
	                    storageSystem.setType(DBUtil.ARCHIVE);
	                    resolvedSystems.add(storageSystem);
	                    //logger.debug("added storage system: " + storageSystem.toString());
	                    storageMap.remove(dbSystem.getInstitution());
	                }
                
	                break;
    			}
    		}
       }
   
       Collections.sort(resolvedSystems);
   
       return resolvedSystems;
   }

    @SuppressWarnings("unused")
    public static System getStorageSystem(String systemName, String siteName) 
    {   
        WeakHashMap<String,String> resourceMap = DBUtil.getResourceMap();
        WeakHashMap<String,String> shortNameMap = DBUtil.getShortNameMap();
        WeakHashMap<String,String> institutionMap = DBUtil.getInstitutionMap();
        WeakHashMap<String,String> typeMap = DBUtil.getTypeMap();
        
        System storageSystem = new System();
        
        
        storageSystem.setSSHHostname(resourceMap.get(systemName));
        storageSystem.setFTPHostname(resourceMap.get(systemName));
        storageSystem.setResourceName(shortNameMap.get(systemName));
        storageSystem.setInstitution(siteName);
        storageSystem.setType(DBUtil.ARCHIVE);
        storageSystem.setStatus(Service.UP);
        return storageSystem;
    }

    public static void resolveResourceDowntimes(List<System> teraGridSystems) 
    {   
        for(String hostname: getResourceDowntimes()) 
        {
            for(System system: teraGridSystems) 
            {
                if (system.getSSHHostname().equals(hostname)) 
                {
                    teraGridSystems.remove(system);
                    break;
                }
            }
        }
        
        return;
    }
    
//    private static Set<String> getIncaResourceDowntimes() {
//        
//        byte[] bytes = new byte[32768];
//        
//        WeakHashMap<String,String> incaHostMap = ResourceMapping.getIncaHostMap();
//        
//        Hashtable<String,Date> downResourceTable= new Hashtable<String,Date>();
//        
//        String id = "";
//        try {
//         // get resource status from inca
//            URL portalResourceStatusURL = new URL(Settings.PORTAL_STATUS_URL);
//            
//            logger.debug(portalResourceStatusURL.toString());
//            // get page
//            URLConnection conn = portalResourceStatusURL.openConnection();
//            
//            ObjectDocument doc = ObjectDocument.Factory.parse(conn.getInputStream());
//            Rows rows = Rows.Factory.parse( doc.getObject().xmlText() );
//            for( Row row : rows.getRowArray() ) {
//                ReportSummaryDocument.ReportSummary summary =
//                    ReportSummaryDocument.Factory.parse(row.xmlText()).getReportSummary();
//
//                // check to see if result
//                if ( ! summary.isSetInstanceId() ) {
//                    continue;
//                }
//
//                // if the test did not complete, an error message will be set
//                String err = summary.isSetErrorMessage() ? summary.getErrorMessage():"";
//
//                // a "comparitor" checks the report against an expression
//                // (e.g., errorMessage=='') and if the comparitor fails, an email is
//                // sent.  The comparitor is optional and can also filter out Inca errors
//                //  so that an admin doesn't receive email if resource is not at fault
//                String cr = summary.getComparisonResult();
//
//                // use this for the key
//                id = summary.getHostname();
//                
//                if (incaHostMap.get(id) != null) {
//                    continue;
//                }
//                // the time the report was collected from the machine
//                Date collected = summary.getGmt().getTime();
//                
//                // real time tests are duplicated in other suites (running less often)
//                // we'll take the latest result only
//                if (downResourceTable.containsKey(id) ) {
//                    logger.error( "Duplicate " + id  + " @ " + collected );
//                    if (collected.before(downResourceTable.get(id))) {
//                        continue;
//                    }
//                }
//                
//                // should we remove a resource whose tests are old?
//    //            if (collected.after(summary.getGmtExpires().getTime())) {
//    //                downResourceList.add(id);
//                    
//                if ( err.startsWith("DOWNTIME") || err.contains( "Inca error" ) ){
//                    downResourceTable.put(id,collected);
//    //            } else if (cr != null && Pattern.matches("^Success.*$", cr)) {
//    //              testStatus.put( id, "success" );
//                } else if (cr != null && Pattern.matches("^Failure.*$", cr)) {
//                    downResourceTable.put(id,collected);
//    //            } else if ( summary.isSetBody() ) {
//    //              testStatus.put(id, "success");
//                } else {
//                    downResourceTable.put(id,collected);
//                }
//            }
//        } catch (Exception e) {
//            logger.error( "Problem parsing result " + id,e);
//        }
//
//        return downResourceTable.keySet();
//    }
    
    @SuppressWarnings("unused")
    private static Set<String> getResourceDowntimes() 
    {   
		byte[] bytes = new byte[32768];
        
        WeakHashMap<String,String> incaHostMap = ResourceMapping.getIncaHostMap();
        
        Set<String> downResources = new HashSet<String>();
        
        Properties props = new Properties();
        
        try 
        {
            // get resource status from inca
            URL portalResourceStatusURL = new URL(Settings.INCA_SERVER);
            
            URLConnection conn = portalResourceStatusURL.openConnection();
            
            props.load(conn.getInputStream ());
            
            for (Object key: props.keySet()) 
            {
                String resolvedName = incaHostMap.get(key);
                if (resolvedName != null) {
                    downResources.add(resolvedName);
                } else {
                    logger.debug("Failed to resolve host " + key);
                }
            }
        } 
        catch (IOException e) {
            logger.error( "Problem parsing resource downtimes.",e);
        }
        
        return downResources;
    }
}
