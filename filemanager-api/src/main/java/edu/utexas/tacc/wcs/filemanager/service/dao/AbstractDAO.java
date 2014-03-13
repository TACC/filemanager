package edu.utexas.tacc.wcs.filemanager.service.dao;

import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;


public class AbstractDAO {

	public static Connection getConnection() throws NamingException, SQLException {
		Context context = new InitialContext();
		
		DataSource ds = (DataSource) context.lookup("java:/comp/env/jdbc/tgcdb");
		
		if (ds == null) {
            return null;
        } else {
        	return ds.getConnection();
        }
	}
	
	
}
