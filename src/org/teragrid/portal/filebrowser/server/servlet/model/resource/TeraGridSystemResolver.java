/* 
 * Created on Jun 20, 2008
 * 
 * Developed by: Rion Dooley - dooley [at] tacc [dot] utexas [dot] edu
 * 				 TACC, Texas Advanced Computing Center
 * 
 * https://www.tacc.utexas.edu/
 */

package org.teragrid.portal.filebrowser.server.servlet.model.resource;

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

import org.apache.log4j.Logger;
import org.teragrid.portal.filebrowser.applet.util.ServletUtil;
import org.teragrid.portal.filebrowser.server.servlet.Settings;
import org.teragrid.service.profile.util.ResourceCache;
import org.teragrid.service.profile.wsclients.model.ComputeDTO;
import org.teragrid.service.profile.wsclients.model.Service;

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
    @SuppressWarnings("unchecked")
	public static List<TeraGridSystem> resolveResources(List<TeraGridSystem> dbSystems) {
       
       WeakHashMap<String,String> storageMap = DBUtil.getMassStorageMap();
       WeakHashMap<String,String> typeMap = DBUtil.getTypeMap();
       
       List<TeraGridSystem> resolvedSystems = new ArrayList<TeraGridSystem>();
       
       Set <ComputeDTO> cachedSystems = ResourceCache.getResources();
       
       for(TeraGridSystem dbSystem: dbSystems) {
   		for (ComputeDTO cachedSystem: cachedSystems) {
   			if (cachedSystem.getTgcdbName().equals(dbSystem.getResourceName())) {
   				if (cachedSystem.getStatus().equals(Service.UP)) {
	   				dbSystem.setResourceName(cachedSystem.getName());
	   				dbSystem.setSSHHostname(cachedSystem.getLoginHostname());
	   				dbSystem.setFTPHostname(cachedSystem.getGridftpHostname());
	   				dbSystem.setStatus(cachedSystem.getStatus());
	   				dbSystem.setResourceId(cachedSystem.getResourceId());
	   				String type = (String)typeMap.get(cachedSystem.getTgcdbName());
	   				if (!ServletUtil.isValid(type)) {
	   					dbSystem.setType(DBUtil.HPC);
	   				} else {
	   					dbSystem.setType(type);
	   				}
	   				dbSystem.setInstitution(cachedSystem.getSite());
	   				resolvedSystems.add(dbSystem);
	   				//logger.debug("Added system bean " + dbSystem.toString() + " to the results set.");
   				}
   				
   				// check to see if the last system site's storage server
                // needs to be added to the resource list
                String storageSystemName = storageMap.get(dbSystem.getInstitution());
                if (storageSystemName != null && 
                        !storageSystemName.equals("NONE")) {
                    TeraGridSystem storageSystem = getStorageSystem(storageSystemName,dbSystem.getInstitution());
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
       
//       for (TeraGridSystem system: dbSystems) {
//            String origResName = system.getResourceName();
////            System.out.println("Resolving " + system.toString());
//            
//            String hostname = (String)ftphostMap.get(origResName);
//            
//            if (hostname == null || hostname.equals("") || hostname.equals(DBUtil.DEAD)) {
//                continue;
//            } else {
//                
//                // remove the entry so there are no duplicate entries.
//                ftphostMap.remove(origResName);
//                
////                if (ftphostMap.containsValue(hostname)) {
////                    for(String key: ftphostMap.keySet()) {
////                        if (ftphostMap.get(key).equals(hostname)) {
////                            logger.debug("Deleting duplicate entry " + key + "->" + hostname);
////                            ftphostMap.remove(key);
////                            break;
////                        }
////                    }
////                }
//                
//                system.setFTPHostname(hostname);
//                system.setSSHHostname((String)sshhostMap.get(origResName));
//                system.setResourceName((String)shortNameMap.get(origResName));
//                system.setInstitution((String)institutionMap.get(origResName));
//                system.setType((String)typeMap.get(origResName));
//                
//                if (system.getResourceName() == null || system.getFTPHostname() == null || 
//                		system.getSSHHostname() == null) {
//                	logger.debug("Ignoring system " + system.toString() + " due to bad mapping tables.");
//                	continue;
//                } else {
//                	logger.debug("Parsing system " + system.toString());
//                }
//                
//                resolvedSystems.add(system);
//                
//                
//            }
//       }
       
       Collections.sort(resolvedSystems);
       
       return resolvedSystems;
   }

    @SuppressWarnings("unused")
    public static TeraGridSystem getStorageSystem(String systemName, String siteName) {
        
        WeakHashMap<String,String> resourceMap = DBUtil.getResourceMap();
        WeakHashMap<String,String> shortNameMap = DBUtil.getShortNameMap();
        WeakHashMap<String,String> institutionMap = DBUtil.getInstitutionMap();
        WeakHashMap<String,String> typeMap = DBUtil.getTypeMap();
        
        TeraGridSystem storageSystem = new TeraGridSystem();
        
        
        storageSystem.setSSHHostname(resourceMap.get(systemName));
        storageSystem.setFTPHostname(resourceMap.get(systemName));
        storageSystem.setResourceName(shortNameMap.get(systemName));
        storageSystem.setInstitution(siteName);
        storageSystem.setType(DBUtil.ARCHIVE);
        storageSystem.setStatus(Service.UP);
        return storageSystem;
           
    }

    public static void resolveResourceDowntimes(
            List<TeraGridSystem> teraGridSystems) {
        
        for(String hostname: getResourceDowntimes()) {
            for(TeraGridSystem system: teraGridSystems) {
                if (system.getSSHHostname().equals(hostname)) {
                    //logger.debug("Removing resource " + system.toString());
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
    private static Set<String> getResourceDowntimes() {
        
		byte[] bytes = new byte[32768];
        
        WeakHashMap<String,String> incaHostMap = ResourceMapping.getIncaHostMap();
        
        Set<String> downResources = new HashSet<String>();
        
        Properties props = new Properties();
        try {
            // get resource status from inca
            URL portalResourceStatusURL = new URL(Settings.INCA_SERVER);
            
            //logger.debug(portalResourceStatusURL.toString());
            // get page
            URLConnection conn = portalResourceStatusURL.openConnection();
            
    //        DataInputStream in = new DataInputStream ( conn.getInputStream (  )  ) ;
    //        BufferedReader d = new BufferedReader(new InputStreamReader(in));
    //        String statusPage = "";
    //        while(d.ready()) {
    //            statusPage += d.readLine();
    //        }
            props.load(conn.getInputStream ());
            
            for (Object key: props.keySet()) {
                String resolvedName = incaHostMap.get(key);
                if (resolvedName != null) {
                    downResources.add(resolvedName);
                } else {
                    logger.debug("Failed to resolve host " + key);
                }
            }
        } catch (IOException e) {
            logger.error( "Problem parsing resource downtimes.",e);
        }
        
        return downResources;
    }
}
