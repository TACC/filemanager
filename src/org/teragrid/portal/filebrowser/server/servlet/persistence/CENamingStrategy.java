package org.teragrid.portal.filebrowser.server.servlet.persistence;

import org.hibernate.cfg.NamingStrategy;
import org.hibernate.util.StringHelper;

/**
 * Prefix database table and column names with a CaveatEmptor handle.
 * <p>
 * This is the implementation of a Hibernate <tt>NamingStrategy</tt>.
 * Hibernate calls this class whenever it creates the database schema.
 * All table names are prefixed with "CE_" while keeping the
 * default Hibernate of uppercase property names. To enable this strategy,
 * set it as the default for the <tt>SessionFactory</tt> , eg. in
 * <tt>HibernateUtil</tt>:
 * <p>
 * <pre>
 *    configuration = new Configuration();
 *    configuration.setNamingStrategy(new CENamingStrategy());
 *    sessionFactory = configuration.configure().buildSessionFactory();
 *
 * </pre>
 * In general, <tt>NamingStrategy</tt> is a powerful concept that gives
 * you freedom to name your database tables and columns using whatever
 * pattern you like.
 *
 * @see HibernateUtil
 * @author Christian Bauer <christian@hibernate.org>
 */
public class CENamingStrategy implements NamingStrategy {

	public String classToTableName(String className) {
		return StringHelper.unqualify(className);
	}

	public String propertyToColumnName(String propertyName) {
	    return propertyName;
    }

	public String tableName(String tableName) {
		return "CE_" + tableName;
	}

	public String columnName(String columnName) {
	    return columnName;
    }

	public String propertyToTableName(String className, String propertyName) {
		return "CE_"
				+ classToTableName(className)
				+ '_'
				+ propertyToColumnName(propertyName);
	}

    public String collectionTableName(String arg0, String arg1, String arg2) {
        // TODO Auto-generated method stub
        return null;
    }

    public String joinKeyColumnName(String arg0, String arg1) {
        // TODO Auto-generated method stub
        return null;
    }

    public String foreignKeyColumnName(String arg0, String arg1, String arg2) {
        // TODO Auto-generated method stub
        return null;
    }

    public String logicalColumnName(String arg0, String arg1) {
        // TODO Auto-generated method stub
        return null;
    }

    public String logicalCollectionTableName(String arg0, String arg1, String arg2, String arg3) {
        // TODO Auto-generated method stub
        return null;
    }

    public String logicalCollectionColumnName(String arg0, String arg1, String arg2) {
        // TODO Auto-generated method stub
        return null;
    }

}
