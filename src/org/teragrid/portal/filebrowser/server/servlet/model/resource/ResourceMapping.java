package org.teragrid.portal.filebrowser.server.servlet.model.resource;

import java.util.WeakHashMap;

public class ResourceMapping {
    
    private static WeakHashMap<String,String> resourceMap = new WeakHashMap<String,String>();;;
    
    public static WeakHashMap<String,String> getIncaHostMap() {
        
        
        /******************************************
         *          Inca Resource Mapping 
         ******************************************/
        
        // ANL
        resourceMap.put("anl-grid","tg-login.uc.teragrid.org-GRID");
        resourceMap.put("anl-ia64","tg-login.uc.teragrid.org");
        resourceMap.put("anl-ia64-rhel","tg-login.uc.teragrid.org-RHEL");
        resourceMap.put("anl-viz","tg-viz-login.uc.teragrid.org");

        // IU
        resourceMap.put("indiana-bigred","login.bigred.iu.teragrid.org");

    
        //NCSA
        resourceMap.put("ncsa-abe","login-abe.ncsa.teragrid.org");
        resourceMap.put("ncsa-cobalt","login-co.ncsa.teragrid.org");
        resourceMap.put("ncsa-grid-abe","login-abe.ncsa.teragrid.org");
        resourceMap.put("ncsa-grid-hg","tg-s037.ncsa.teragrid.org");
        resourceMap.put("ncsa-grid-tun","tune.ncsa.uiuc.edu");
        resourceMap.put("ncsa-ia64","tg-login.ncsa.teragrid.org");
        resourceMap.put("ncsa-tungsten","login-w.ncsa.teragrid.org");
    
        //NCAR
        resourceMap.put("ncar-frost","tg-login.frost.ncar.teragrid.org");
    
        // ORNL
        resourceMap.put("ornl-login","tg-login.ornl.teragrid.org");
    
        // PSC
        resourceMap.put("psc-bigben","tg-login.bigben.psc.teragrid.org");
        resourceMap.put("psc-rachel","tg-login.rachel.psc.teragrid.org");
        resourceMap.put("psc-pople","tg-login1.pople.psc.teragrid.org");

        // PURDUE
        resourceMap.put("purdue-condor","no-url-to-show");
        resourceMap.put("purdue-lear","tg-login.purdue.teragrid.org");
        resourceMap.put("purdue-grid","tg-gatekeeper.rcac.purdue.edu");
        resourceMap.put("purdue-steele","tg-steele.purdue.teragrid.org");

        // SDSC
        resourceMap.put("repo","repo.uc.teragrid.org");
        resourceMap.put("sapa","sapa.sdsc.edu");
        resourceMap.put("sdsc-bg","bglogin.sdsc.edu");
        resourceMap.put("sdsc-datastar","dslogin.sdsc.edu");
        resourceMap.put("sdsc-ia64","tg-login.sdsc.teragrid.org");

        // TACC
        resourceMap.put("tacc-lonestar","tg-login.lonestar.tacc.teragrid.org");
        resourceMap.put("tacc-ranger","ranger.tacc.teragrid.org");
        resourceMap.put("tacc-viz","tg-viz-login.tacc.teragrid.org");
   
        //LONI
        resourceMap.put("loni-lsu-qb","queenbee.loni-lsu.teragrid.org");
        
        return resourceMap;
    }   
}
