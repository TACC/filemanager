/* 
 * Created on Dec 18, 2007
 * 
 * Developed by: Rion Dooley - dooley [at] tacc [dot] utexas [dot] edu
 * 				 TACC, Texas Advanced Computing Center
 * 
 * https://www.tacc.utexas.edu/
 */

package org.teragrid.portal.filebrowser.applet.util;

import java.io.File;

import org.teragrid.portal.filebrowser.applet.transfer.FTPLogin;
import org.teragrid.portal.filebrowser.applet.transfer.FTPSettings;
import org.teragrid.service.profile.wsclients.model.ComputeDTO;

import edu.utexas.tacc.wcs.filemanager.common.model.enumeration.FileProtocolType;
import edu.utexas.tacc.wcs.filemanager.common.model.enumeration.SystemType;

/**
 * Insert Template description here.
 * 
 * @author Rion Dooley < dooley [at] tacc [dot] utexas [dot] edu >
 *
 */
public class SiteUtil {
    
//    /**
//     * Converts GPIRResource beans to FTPSettings objects.
//     * 
//     * @param bean
//     * @return
//     */
//    public static FTPSettings convert(AbstractResourceBean bean) {
//        
//        FTPSettings site = new FTPSettings(bean.getName());
//        
//        // Now create the site for use in the GUI
//        site.name = bean.getName();
//        site.host = bean.getHostname();
//        site.port = 2811;
//        site.type = FTPType.GRIDFTP;
//        site.passiveMode = true;
//        site.available = true;
//        site.connRetry = 2;
//        site.connDelay = 0;
//        site.connParallel = 1;
//        site.connMaxNum = 2;
//        site.loginMode = FTPLogin.LOGIN_USEPROXYINIT;
//        if (bean.getResourceType().equals(ResourceType.STORAGE.name().toLowerCase())) {
//            site.hostType = DBUtil.ARCHIVE;
//        } else if (bean.getResourceType().equals(ResourceType.VIZ.name().toLowerCase())) {
//            site.hostType = DBUtil.VIZ;
//        } else 
//            site.hostType = DBUtil.HPC;
//        
//        site.listed = true;
//        
//        site.userName = "";
//        site.password = "";
//        
//        return site;
//    }
    
    /**
     * Converts TeraGrid Profile Service beans to FTPSettings objects.
     * 
     * @param system
     * @return
     */
    public static FTPSettings convert(ComputeDTO system) {
        
        FTPSettings site = new FTPSettings(system.getName());
        
        // Now create the site for use in the GUI
        site.name = system.getName();
        site.host = system.getGridftpHostname();
        site.filePort = 2811;
        site.resourceId = system.getResourceId();
        site.protocol = FileProtocolType.GRIDFTP;
        site.passiveMode = true;
        site.available = system.getStatus().equalsIgnoreCase("up");
        site.connRetry = 2;
        site.connDelay = 0;
        site.connParallel = 1;
        site.connMaxNum = 2;
        site.loginMode = FTPLogin.LOGIN_USEPROXYINIT;
        site.hostType = SystemType.HPC;
        
        site.listed = true;
        
        site.userName = "";
        site.password = "";
        
        return site;
    }
    
//    public static ArrayList<FTPSettings> convert(List<AbstractResourceBean> beans) {
//        ArrayList<FTPSettings> sites = new ArrayList<FTPSettings>();
//        
//        for (AbstractResourceBean bean: beans) {
//            sites.add(convert(bean));
//        }
//        
//        return sites;
//    }
    
    public static String separator(FTPSettings site) {
        if (isLocal(site)) 
            return File.separator;
        else 
            return "/";
    }
    
    public static boolean isLocal(FTPSettings site) {
        return (site.name.toLowerCase().indexOf("local") > -1);
    }
}
