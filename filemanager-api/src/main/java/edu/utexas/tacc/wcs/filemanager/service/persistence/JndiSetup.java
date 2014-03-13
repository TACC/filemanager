package edu.utexas.tacc.wcs.filemanager.service.persistence;

import java.io.IOException;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.postgresql.ds.PGSimpleDataSource;


/**
 * Utility class to setup a jndi connection when there is not one otherwise
 * specified.
 * 
 * @author sterry1
 */
public class JndiSetup 
{
    static Properties props = new Properties();

    public static void init()
    {
        try {
            props.load(JndiSetup.class.getClassLoader().getResourceAsStream("jdbc.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Create initial context
        System.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.osjava.sj.memory.MemoryContextFactory");
        System.setProperty("org.osjava.sj.jndi.shared", "true");

        InitialContext ic = null;
        try {
            ic = new InitialContext();
            ic.createSubcontext("java:comp/env/jdbc");
            // Construct DataSource
            PGSimpleDataSource ds = new PGSimpleDataSource();
            ds.setServerName(props.getProperty("servername"));
            ds.setPortNumber(Integer.valueOf(props.getProperty("port"), 5432));
            ds.setDatabaseName(props.getProperty("dbname"));
            ds.setUser(props.getProperty("user"));
            ds.setPassword(props.getProperty("password"));

            // Put datasource in JNDI context
            ic.bind("java:comp/env/jdbc/" + props.getProperty("ds_name"), ds);
            
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }   
}
