package edu.utexas.tacc.wcs.filemanager.common.util;

import java.util.*;

//TODO: refactor this mapping into a bean.  inject the beans using spring.  run the service in tomcat.
public class DBUtil {
    
    // sentinel for a value that should not be displayed
    public static final String DEAD = "DEAD";
    public static final String VIZ = "viz";
    public static final String HPC = "hpc";
    public static final String ARCHIVE = "archive";
    
    
    public static WeakHashMap<String,String> getShortNameMap() {
        WeakHashMap<String,String> shortMap = new WeakHashMap<String,String>();

        shortMap.put("dtf.anl.teragrid","UC/ANL TeraGrid Cluster");
        //NCSA
        shortMap.put("dtf.ncsa.teragrid","NCSA TeraGrid Cluster");

        //this block was pulled directly from the DB
        shortMap.put("abe.ncsa.teragrid","Abe");
        shortMap.put("abe-queenbee.teragrid", "Abe");
        shortMap.put("login-abe.ncsa.teragrid", "Abe");
        shortMap.put("bigben.psc.teragrid", "BigBen");
        shortMap.put("bigred.iu.teragrid", "Big Red");
        shortMap.put("bluegene.sdsc.teragrid", "Blue Gene");
        shortMap.put("cobalt.ncsa.teragrid", "Cobalt");
        shortMap.put("collections.sdsc.teragrid", "Collections");
        shortMap.put("condor.purdue.teragrid", "Condor");
        shortMap.put("copper.ncsa.teragrid", "Copper");
        shortMap.put("database.sdsc.teragrid", "SDSC Database");
        shortMap.put("datastar-p655.sdsc.teragrid", "DataStar");
        shortMap.put("datastar.sdsc.teragrid", "DataStar");
        shortMap.put("frost.ncar.teragrid", "Frost");
        shortMap.put("gpfs-wan.teragrid", "GPFS WAN");
        shortMap.put("hpss.iu.teragrid", "IU HPSS");
        shortMap.put("lear.purdue.teragrid", "Lear");
        shortMap.put("lonestar.tacc.teragrid", "Lonestar");
        shortMap.put("maverick.tacc.teragrid", "Maverick");
        shortMap.put("mss.ncsa.teragrid", "NCSA Mass Storage");
        shortMap.put("queenbee.loni-lsu.teragrid", "Queen Bee");
        shortMap.put("rachel.psc.teragrid", "Rachel");
        shortMap.put("radon.purdue.teragrid", "Radon");
        shortMap.put("ranger.tacc.teragrid", "Ranger");
        shortMap.put("ranch.tacc.teragrid", "Ranch");
        shortMap.put("spur.tacc.teragrid", "Spur");
        shortMap.put("tape.sdsc.teragrid", "SDSC Tape");
        shortMap.put("steele.purdue.teragrid", "Steele");
        shortMap.put("tape.sdsc.teragrid", "SDSC HPSS");
        shortMap.put("teragrid_roaming", "TeraGrid Roaming");
        shortMap.put("tungsten.ncsa.teragrid", "Tungsten");
        shortMap.put("viz.anl.teragrid", "ANL Vis");
        shortMap.put("teradre.purdue.teragrid", "Teradre");
        shortMap.put("pople.psc.teragrid", "Pople");
        shortMap.put("brutus.purdue.teragrid", "Brutus");
        shortMap.put("lincoln.ncsa.teragrid","Lincoln");
        
        // ORNL
        shortMap.put("nstg.ornl.teragrid","NSTG");

        // SDSC
        shortMap.put("dslogin.sdsc.edu", "DataStar P655");
        shortMap.put("dtf.sdsc.teragrid","SDSC TeraGrid Cluster");  
        
        //NICS
        shortMap.put("kraken.nics.teragrid", "Kraken");

        // RETIRED
        shortMap.put("dtf.caltech.teragrid",DEAD);
        shortMap.put("radium.ncsa.teragrid",DEAD);
        shortMap.put("cloud.purdue.teragrid",DEAD);
        shortMap.put("lemieux.psc.teragrid",DEAD);
        shortMap.put("avidd-ia32.iu.teragrid",DEAD);
        shortMap.put("tiger.iu.teragrid",DEAD);

        return shortMap;
    }

    public static WeakHashMap<String,String> getTypeMap() {
        WeakHashMap<String,String> shortMap = new WeakHashMap<String,String>();

        shortMap.put("dtf.anl.teragrid",HPC);
        //NCSA
        shortMap.put("dtf.ncsa.teragrid",HPC);

        //this block was pulled directly from the DB
        shortMap.put("abe.ncsa.teragrid",HPC);
        shortMap.put("abe-queenbee.teragrid", HPC);
        shortMap.put("login-abe.ncsa.teragrid", HPC);
        shortMap.put("avidd-ia32.iu.teragrid", HPC);
        shortMap.put("bigben.psc.teragrid", HPC);
        shortMap.put("bigred.iu.teragrid", HPC);
        shortMap.put("bluegene.sdsc.teragrid", HPC);
        shortMap.put("cloud.purdue.teragrid", HPC);
        shortMap.put("cobalt.ncsa.teragrid", HPC);
        shortMap.put("collections.sdsc.teragrid", ARCHIVE);
        shortMap.put("condor.purdue.teragrid", HPC);
        shortMap.put("brutus.purdue.teragrid", HPC);
        shortMap.put("copper.ncsa.teragrid", HPC);
        shortMap.put("database.sdsc.teragrid", ARCHIVE);
        shortMap.put("datastar-p655.sdsc.teragrid", HPC);
        shortMap.put("datastar.sdsc.teragrid", HPC);
        shortMap.put("frost.ncar.teragrid", HPC);
        shortMap.put("gpfs-wan.teragrid", ARCHIVE);
        shortMap.put("hpss.iu.teragrid", ARCHIVE);
        shortMap.put("lear.purdue.teragrid", HPC);
        shortMap.put("lemieux.psc.teragrid", HPC);
        shortMap.put("lonestar.tacc.teragrid", HPC);
        shortMap.put("longhorn.tacc.teragrid", VIZ);
        shortMap.put("maverick.tacc.teragrid", VIZ);
        shortMap.put("mss.ncsa.teragrid", ARCHIVE);
        shortMap.put("queenbee.loni-lsu.teragrid", HPC);
        shortMap.put("rachel.psc.teragrid", HPC);
        shortMap.put("radium.ncsa.teragrid", HPC);
        shortMap.put("radon.purdue.teragrid", HPC);
        shortMap.put("ranch.tacc.teragrid", HPC);
        shortMap.put("ranger.tacc.teragrid", HPC);
        shortMap.put("spur.tacc.teragrid", VIZ);
        shortMap.put("steele.purdue.teragrid", HPC);
        shortMap.put("tape.sdsc.teragrid", ARCHIVE);
        shortMap.put("hpss.sdsc.teragrid", ARCHIVE);
        shortMap.put("teragrid_roaming", HPC);
        shortMap.put("tiger.iu.teragrid", HPC);
        shortMap.put("tungsten.ncsa.teragrid", HPC);
        shortMap.put("lincoln.ncsa.teragrid", HPC);
        shortMap.put("teradre.purdue.teragrid", VIZ);
        shortMap.put("viz.anl.teragrid", VIZ);
        shortMap.put("pople.psc.teragrid", HPC);
        // ORNL
        shortMap.put("nstg.ornl.teragrid",HPC);

        // SDSC
        shortMap.put("dslogin.sdsc.edu", HPC);
        shortMap.put("dtf.sdsc.teragrid", HPC);  
        
        //NICS
        shortMap.put("kraken.nics.teragrid", HPC);

        // RETIRED
        shortMap.put("dtf.caltech.teragrid",HPC);

        return shortMap;
    }
    
    /**
     * returns a hashtable containing the mapping of resource names
     * identified in the TGCDB to the actual ftp server hostnames of those resources.
     */
    public static WeakHashMap<String,String> getResourceMap() {
        WeakHashMap<String,String> resourceMap = new WeakHashMap<String,String>();
        //resourceMap.put("portal.teragrid","https://portal.teragrid.org");
        
        // ANL
        resourceMap.put("dtf.anl.teragrid","tg-gridftp.uc.teragrid.org");
        resourceMap.put("viz.anl.teragrid", "tg-gridftp.uc.teragrid.org");
    
        // IU
        resourceMap.put("bigred.iu.teragrid","gridftp.bigred.iu.teragrid.org");
    
        //NCSA
        resourceMap.put("dtf.ncsa.teragrid","gridftp-hg.ncsa.teragrid.org");
        resourceMap.put("cobalt.ncsa.teragrid","gridftp-co.ncsa.teragrid.org");
        resourceMap.put("copper.ncsa.teragrid","login-cu.ncsa.teragrid.org"); 
        resourceMap.put("tungsten.ncsa.teragrid","gridftp-w.ncsa.teragrid.org");  
        resourceMap.put("abe.ncsa.teragrid","gridftp-abe.ncsa.teragrid.org");
        resourceMap.put("lincoln.ncsa.teragrid", "gridftp-abe.ncsa.teragrid.org");
        resourceMap.put("mss.ncsa.teragrid", "mss.ncsa.uiuc.edu");
        
        //NCAR
        resourceMap.put("frost.ncar.teragrid","gridftp.frost.ncar.teragrid.org");
    
        // ORNL
        resourceMap.put("nstg.ornl.teragrid","tg-gridftp.ornl.teragrid.org");
    
        // PSC
        resourceMap.put("rachel.psc.teragrid","tg-login.rachel.psc.teragrid.org");
        resourceMap.put("bigben.psc.teragrid","gridftp.bigben.psc.teragrid.org");
        resourceMap.put("pople.psc.teragrid","gridftp.pople.psc.teragrid.org");
        
        // PURDUE
        resourceMap.put("lear.purdue.teragrid","tg-login.purdue.teragrid.org");
        resourceMap.put("teradre.purdue.teragrid", "tg-data.purdue.teragrid.org");
        resourceMap.put("condor.purdue.teragrid", "tg-data.purdue.teragrid.org");
        resourceMap.put("steele.purdue.teragrid","tg-data.purdue.teragrid.org");
        resourceMap.put("brutus.purdue.teragrid","tg-data.purdue.teragrid.org");
        
        // SDSC
        resourceMap.put("datastar.sdsc.teragrid","ds-gridftp.sdsc.edu");
        resourceMap.put("dtf.sdsc.teragrid","tg-gridftp.sdsc.teragrid.org");  
        resourceMap.put("bluegene.sdsc.teragrid","bg-login1.sdsc.edu");     
        resourceMap.put("datastar-p655.sdsc.teragrid", "ds-gridftp.sdsc.edu");
        resourceMap.put("tape.sdsc.teragrid", "hpss.sdsc.edu");
        
        // TACC
        resourceMap.put("maverick.tacc.teragrid","gridftp.maverick.tacc.teragrid.org");
        resourceMap.put("lonestar.tacc.teragrid","tg-gridftp2.lonestar.tacc.teragrid.org");    
        resourceMap.put("ranger.tacc.teragrid","tg-login.ranger.tacc.teragrid.org");
        resourceMap.put("ranch.tacc.teragrid", "gridftp1.ranch.tacc.teragrid.org");
        resourceMap.put("spur.tacc.teragrid", "tg-login.spur.tacc.teragrid.org");
        
        //LONI
        resourceMap.put("queenbee.loni-lsu.teragrid","qb1.loni.org");
        
        //NICS
        resourceMap.put("kraken.nics.teragrid", "gridftp.nics.utk.edu");
        
        // RETIRED
        resourceMap.put("dtf.caltech.teragrid",DEAD);
        resourceMap.put("radium.ncsa.teragrid",DEAD);
        resourceMap.put("cloud.purdue.teragrid",DEAD);
        resourceMap.put("lemieux.psc.teragrid",DEAD);
        resourceMap.put("avidd-ia32.iu.teragrid",DEAD);
        resourceMap.put("tiger.iu.teragrid",DEAD);
        
        return resourceMap;
    }
    
    /**
     * returns a hashtable containing the mapping of resource names
     * identified in the TGCDB to the actual head node hostnames of 
     * those resources.
     */
    public static WeakHashMap<String,String> getHostMap() {
        WeakHashMap<String,String> resourceMap = new WeakHashMap<String,String>();
        //resourceMap.put("portal.teragrid","https://portal.teragrid.org");
        
        // ANL
        resourceMap.put("dtf.anl.teragrid","tg-login.uc.teragrid.org");
        resourceMap.put("viz.anl.teragrid", "tg-viz-login.uc.teragrid.org");
    
        // IU
        resourceMap.put("bigred.iu.teragrid","login.bigred.iu.teragrid.org");
    
        //NCSA
        resourceMap.put("dtf.ncsa.teragrid","tg-login.ncsa.teragrid.org");
        resourceMap.put("cobalt.ncsa.teragrid","login-co.ncsa.teragrid.org");
        resourceMap.put("copper.ncsa.teragrid","login-cu.ncsa.teragrid.org"); 
        resourceMap.put("tungsten.ncsa.teragrid","login-w.ncsa.teragrid.org");  
        resourceMap.put("abe.ncsa.teragrid","login-abe.ncsa.teragrid.org");
        resourceMap.put("mss.ncsa.teragrid", "mss.ncsa.uiuc.edu");
        resourceMap.put("lincoln.ncsa.teragrid", "lincoln.ncsa.uiuc.edu");
        
        //NCAR
        resourceMap.put("frost.ncar.teragrid","tg-login.frost.ncar.teragrid.org");
    
        // ORNL
        resourceMap.put("nstg.ornl.teragrid","tg-login.ornl.teragrid.org");
    
        // PSC
        resourceMap.put("rachel.psc.teragrid","tg-login.rachel.psc.teragrid.org");
        resourceMap.put("bigben.psc.teragrid","tg-login.bigben.psc.teragrid.org");
        resourceMap.put("pople.psc.teragrid","tg-login.pople.psc.teragrid.org");
        
        // PURDUE
        resourceMap.put("lear.purdue.teragrid","tg-login.purdue.teragrid.org");
        resourceMap.put("teradre.purdue.teragrid", "tg-data.purdue.teragrid.org");
        resourceMap.put("condor.purdue.teragrid", "tg-data.purdue.teragrid.org");
        resourceMap.put("steele.purdue.teragrid","tg-data.purdue.teragrid.org");
        resourceMap.put("brutus.purdue.teragrid","portia.rcac.purdue.edu");
        
        // SDSC
        resourceMap.put("datastar.sdsc.teragrid","ds-login.sdsc.edu");
        resourceMap.put("dtf.sdsc.teragrid","tg-login.sdsc.teragrid.org");  
        resourceMap.put("bluegene.sdsc.teragrid","bg-login1.sdsc.edu");     
        resourceMap.put("datastar-p655.sdsc.teragrid", "ds-login.sdsc.edu");
        resourceMap.put("tape.sdsc.teragrid", "hpss.sdsc.edu");
        
        // TACC
        resourceMap.put("maverick.tacc.teragrid","tg-viz-login.tacc.teragrid.org");
        resourceMap.put("lonestar.tacc.teragrid","tg-login.lonestar.tacc.teragrid.org");    
        resourceMap.put("ranger.tacc.teragrid","ranger.tacc.teragrid.org");
        resourceMap.put("ranch.tacc.teragrid", "gridftp1.ranch.tacc.teragrid.org");
        resourceMap.put("spur.tacc.teragrid", "tg-login.spur.tacc.teragrid.org");
        
        //LONI
        resourceMap.put("queenbee.loni-lsu.teragrid","login1-qb.loni-lsu.teragrid.org ");
        
        //NICS
        resourceMap.put("kraken.nics.teragrid", "kraken.nics.teragrid.org");
        
        // RETIRED
        resourceMap.put("dtf.caltech.teragrid",DEAD);
        resourceMap.put("radium.ncsa.teragrid",DEAD);
        resourceMap.put("cloud.purdue.teragrid",DEAD);
        resourceMap.put("lemieux.psc.teragrid",DEAD);
        resourceMap.put("avidd-ia32.iu.teragrid",DEAD);
        resourceMap.put("tiger.iu.teragrid",DEAD);
        
        return resourceMap;
    
    }

//    /**
//     * returns a hashtable containing the mapping of resource names
//     * identified in the TGCDB to the actual pingable hostnames of those resources.
//     */
//    public static WeakHashMap<String,String> getResourceMap() {
//    WeakHashMap<String,String> resourceMap = new WeakHashMap<String,String>();
//    //resourceMap.put("portal.teragrid","https://portal.teragrid.org");
//    
//    // ANL
//    resourceMap.put("dtf.anl.teragrid","tg-login.uc.teragrid.org");
//    resourceMap.put("viz.anl.teragrid", "tg-viz-login.uc.teragrid.org");
//
//    // IU
//    resourceMap.put("bigred.iu.teragrid","login.bigred.iu.teragrid.org");
//
//    //NCSA
//    resourceMap.put("dtf.ncsa.teragrid","tg-login.ncsa.teragrid.org");
//    resourceMap.put("cobalt.ncsa.teragrid","login-co.ncsa.teragrid.org");
//    resourceMap.put("copper.ncsa.teragrid","login-cu.ncsa.teragrid.org"); 
//    resourceMap.put("tungsten.ncsa.teragrid","login-w.ncsa.teragrid.org");  
//    resourceMap.put("abe.ncsa.teragrid","login-abe.ncsa.teragrid.org");
//
//    //NCAR
//    resourceMap.put("frost.ncar.teragrid","tg-login.frost.ncar.teragrid.org");
//
//    // ORNL
//    resourceMap.put("nstg.ornl.teragrid","tg-login.ornl.teragrid.org");
//
//    // PSC
//    resourceMap.put("rachel.psc.teragrid","tg-login.rachel.psc.teragrid.org");
//    resourceMap.put("bigben.psc.teragrid","tg-login.bigben.psc.teragrid.org");
//    
//    // PURDUE
//    resourceMap.put("lear.purdue.teragrid","tg-login.purdue.teragrid.org");
//    resourceMap.put("teradre.purdue.teragrid", "tg-data.purdue.teragrid.org");
//    resourceMap.put("condor.purdue.teragrid", "tg-data.purdue.teragrid.org");
//    
//    // SDSC
//    resourceMap.put("datastar.sdsc.teragrid","ds-gridftp.sdsc.edu");
//    resourceMap.put("dtf.sdsc.teragrid","tg-login.sdsc.teragrid.org");  
//    resourceMap.put("bluegene.sdsc.teragrid","bglogin.sdsc.edu");     
//    resourceMap.put("datastar-p655.sdsc.teragrid", "ds-gridftp.sdsc.edu");
//    
//    // TACC
//    resourceMap.put("maverick.tacc.teragrid","tg-viz-login.tacc.teragrid.org");
//    resourceMap.put("lonestar.tacc.teragrid","tg-login.lonestar.tacc.teragrid.org");    
//    resourceMap.put("ranger.tacc.teragrid","tg-login.ranger.tacc.teragrid.org");
//
//    //LONI
//    resourceMap.put("queenbee.loni-lsu.teragrid","queenbee.loni-lsu.teragrid.org");
//    
//    // RETIRED
//    resourceMap.put("dtf.caltech.teragrid",DEAD);
//    resourceMap.put("radium.ncsa.teragrid",DEAD);
//    resourceMap.put("cloud.purdue.teragrid",DEAD);
//    resourceMap.put("lemieux.psc.teragrid",DEAD);
//    resourceMap.put("avidd-ia32.iu.teragrid",DEAD);
//    resourceMap.put("tiger.iu.teragrid",DEAD);
//    
//    return resourceMap;
//    }

    /**
     * returns a hashtable containing the mapping of resource names
     * identified in the TGCDB to the acronym of the institution that
     * the resource belongs to
     */
    public static WeakHashMap<String,String> getInstitutionMap() {
    
    WeakHashMap<String,String> instMap = new WeakHashMap<String,String>();
    instMap.put("portal.teragrid","User Portal");
    // ANL
    instMap.put("dtf.anl.teragrid","UC/ANL");
    instMap.put("viz.anl.teragrid", "UC/ANL");

    // IU
    instMap.put("avidd-ia32.iu.teragrid","IU");
    instMap.put("tiger.iu.teragrid","IU");
    instMap.put("bigred.iu.teragrid","IU");

    // NCSA
    instMap.put("dtf.ncsa.teragrid","NCSA");
    instMap.put("cobalt.ncsa.teragrid","NCSA");
    //instMap.put("copper.ncsa.teragrid","NCSA");
    instMap.put("tungsten.ncsa.teragrid","NCSA");
    instMap.put("radium.ncsa.teragrid","NCSA");
    instMap.put("abe.ncsa.teragrid","NCSA");
    instMap.put("mss.ncsa.teragrid","NCSA");
    instMap.put("lincoln.ncsa.teragrid","NCSA");

    //NCAR
    instMap.put("frost.ncar.teragrid","NCAR");

    // ORNL
    instMap.put("nstg.ornl.teragrid","ORNL");

    // PSC
    instMap.put("lemieux.psc.teragrid","PSC");
    instMap.put("rachel.psc.teragrid","PSC");
    instMap.put("bigben.psc.teragrid","PSC");
    instMap.put("pople.psc.teragrid", "PSC");
    
    // PURDUE
    instMap.put("cloud.purdue.teragrid","Purdue");
    instMap.put("radon.purdue.teragrid","Purdue");
    instMap.put("lear.purdue.teragrid","Purdue");
    instMap.put("teradre.purdue.teragrid", "Purdue");
    instMap.put("condor.purdue.teragrid", "Purdue");
    instMap.put("steele.purdue.teragrid", "Purdue");
    instMap.put("brutus.purdue.teragrid", "Purdue");
    
    // SDSC
    instMap.put("datastar.sdsc.teragrid","SDSC");
    instMap.put("dtf.sdsc.teragrid","SDSC");
    instMap.put("bluegene.sdsc.teragrid","SDSC");
    instMap.put("datastar-p655.sdsc.teragrid", "SDSC");
    instMap.put("tape.sdsc.teragrid","SDSC");
    
    // TACC
    instMap.put("maverick.tacc.teragrid","TACC");
    instMap.put("lonestar.tacc.teragrid","TACC");
    instMap.put("ranger.tacc.teragrid","TACC");
    instMap.put("ranch.tacc.teragrid","TACC");
    instMap.put("spur.tacc.teragrid","TACC");

    //LONI
    instMap.put("queenbee.loni-lsu.teragrid","LONI");
    
    //NICS
    instMap.put("kraken.nics.teragrid", "NICS");

    return instMap;
    }
    
    public static WeakHashMap<String,String> getMassStorageMap() {
        WeakHashMap<String,String> storageMap = new WeakHashMap<String,String>();
        
        storageMap.put("ncsa.teragrid.org", "mss.ncsa.teragrid");
        storageMap.put("sdsc.teragrid.org", "tape.sdsc.teragrid");
        storageMap.put("iu.teragrid.org", "NONE");
        storageMap.put("ornl.teragrid.org", "NONE");
        storageMap.put("psc.teragrid.org", "NONE");
        storageMap.put("purdue.teragrid.org", "NONE");
        storageMap.put("tacc.teragrid.org", "ranch.tacc.teragrid");
        storageMap.put("loni-lsu.teragrid.org", "NONE");
        storageMap.put("ncar.teragrid.org", "NONE");
        storageMap.put("uc.teragrid.org", "NONE");
        storageMap.put("User Portal", "NONE");
        storageMap.put("nics.teragrid.org", "NONE");
        
        return storageMap;
    }
}
