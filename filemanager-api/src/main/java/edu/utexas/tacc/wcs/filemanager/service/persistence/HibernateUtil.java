package edu.utexas.tacc.wcs.filemanager.service.persistence;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Interceptor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.ImprovedNamingStrategy;

import edu.utexas.tacc.wcs.filemanager.service.exception.InfrastructureException;


/**
 * Basic Hibernate helper class, handles SessionFactory, Session and Transaction.
 * <p>
 * Uses a static initializer for the initial SessionFactory creation
 * and holds Session and Transactions in thread local variables. All
 * exceptions are wrapped in an unchecked InfrastructureException.
 *
 * @author christian@hibernate.org
 */
public class HibernateUtil 
{
	private static final Logger logger = Logger.getLogger(HibernateUtil.class);
	
	private static Configuration configuration;
	private static SessionFactory sessionFactory;
	private static final ThreadLocal<Session> threadSession = new ThreadLocal<Session>();
	private static final ThreadLocal<Transaction> threadTransaction = new ThreadLocal<Transaction>();
	private static final ThreadLocal<Interceptor> threadInterceptor = new ThreadLocal<Interceptor>();

	// Create the initial SessionFactory from the default configuration files
	static {
		try {
			configuration = new Configuration().configure(
					HibernateUtil.class.getClassLoader().getResource("hibernate.cfg.xml"));
			
			configuration.setNamingStrategy(new ImprovedNamingStrategy());
			
			// uncomment to see hql of all schema export statments.
			// new SchemaExport(configuration).create(true, false);
			sessionFactory = configuration.buildSessionFactory();
			
		} catch (Throwable ex) {
			// We have to catch Throwable, otherwise we will miss
			// NoClassDefFoundError and other subclasses of Error
			logger.error("Building SessionFactory failed.", ex);
			throw new ExceptionInInitializerError(ex);
		}
	}

	/**
	 * Returns the SessionFactory used for this static class.
	 *
	 * @return SessionFactory
	 */
	public static SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	/**
	 * Returns the original Hibernate configuration.
	 *
	 * @return Configuration
	 */
	public static Configuration getConfiguration() {
		return configuration;
	}

	/**
	 * Rebuild the SessionFactory with the static Configuration.
	 *
	 */
	 public static void rebuildSessionFactory()
		throws InfrastructureException {
		synchronized(sessionFactory) {
			try {
				sessionFactory = getConfiguration().buildSessionFactory();
			} catch (Exception ex) {
				throw new InfrastructureException(ex);
			}
		}
	 }

	/**
	 * Rebuild the SessionFactory with the given Hibernate Configuration.
	 *
	 * @param cfg
	 */
	 public static void rebuildSessionFactory(Configuration cfg)
		throws InfrastructureException {
		synchronized(sessionFactory) {
			try {
				sessionFactory = cfg.buildSessionFactory();
				configuration = cfg;
			} catch (Exception ex) {
				throw new InfrastructureException(ex);
			}
		}
	 }

	/**
	 * Retrieves the current Session local to the thread.
	 * <p/>
	 * If no Session is open, opens a new Session for the running thread.
	 *
	 * @return Session
	 */
	public static Session getSession()
		throws InfrastructureException {
		Session s = threadSession.get();
        try {
			if (s == null || !s.isOpen()) {
				//log.debug("Opening new Session for this thread.");
				if (getInterceptor() != null) {
				    logger.debug("Using interceptor: " + getInterceptor().getClass());
					s = getSessionFactory().openSession(getInterceptor());
				} else {
					s = getSessionFactory().openSession();
				}
				threadSession.set(s);
            } else if (s.isConnected() == false) {
                reconnect(s);
            }
//            s.enableFilter("filterDeletedJobs");
//          s.enableFilter("limitJobs");
		} catch (HibernateException ex) {
			throw new InfrastructureException(ex);
		}
		return s;
	}

	/**
	 * Closes the Session local to the thread.
	 */
	public static void closeSession()
		throws InfrastructureException {
        try {
			Session s = threadSession.get();
			threadSession.set(null);
			if (s != null && s.isOpen()) {
				//log.debug("Closing Session of this thread.");
				s.close();
			}
		} catch (HibernateException ex) {
			throw new InfrastructureException(ex);
		}
	}

	/**
	 * Start a new database transaction.
	 */
	public static void beginTransaction()
		throws InfrastructureException {
		Transaction tx = threadTransaction.get();
		try {
			if (tx == null) {
			    logger.debug("Starting new database transaction in this thread.");
				tx = getSession().beginTransaction();
				threadTransaction.set(tx);
			}
		} catch (HibernateException ex) {
			throw new InfrastructureException(ex);
		}
	}

	/**
	 * Commit the database transaction.
	 */
	public static void commitTransaction()
		throws InfrastructureException {
		Transaction tx = threadTransaction.get();
		try {
			if ( tx != null && !tx.wasCommitted()
							&& !tx.wasRolledBack() ) {
			    logger.debug("Committing database transaction of this thread.");
				tx.commit();
			}
			threadTransaction.set(null);
		} catch (HibernateException ex) {
			rollbackTransaction();
			throw new InfrastructureException(ex);
		}
	}

	/**
	 * Commit the database transaction.
	 */
	public static void rollbackTransaction()
		throws InfrastructureException {
		Transaction tx = threadTransaction.get();
		try {
			threadTransaction.set(null);
			if ( tx != null && !tx.wasCommitted() && !tx.wasRolledBack() ) {
			    logger.debug("Trying to rollback database transaction of this thread.");
				tx.rollback();
			}
		} catch (HibernateException ex) {
			throw new InfrastructureException(ex);
		} finally {
			closeSession();
		}
	}

	/**
	 * Reconnects a Hibernate Session to the current Thread.
	 *
	 * @param session The Hibernate Session to be reconnected.
	 */
	@SuppressWarnings("deprecation")
	public static void reconnect(Session session)
		throws InfrastructureException {
		try {
			session.reconnect();
			threadSession.set(session);
		} catch (HibernateException ex) {
			throw new InfrastructureException(ex);
		}
	}

	/**
	 * Disconnect and return Session from current Thread.
	 *
	 * @return Session the disconnected Session
	 */
	public static Session disconnectSession()
		throws InfrastructureException {

		Session session = getSession();
		try {
			threadSession.set(null);
			if (session.isConnected() && session.isOpen())
				session.disconnect();
		} catch (HibernateException ex) {
			throw new InfrastructureException(ex);
		}
		return session;
	}

	/**
	 * Register a Hibernate interceptor with the current thread.
	 * <p>
	 * Every Session opened is opened with this interceptor after
	 * registration. Has no effect if the current Session of the
	 * thread is already open, effective on next close()/getSession().
	 */
	public static void registerInterceptor(Interceptor interceptor) {
		threadInterceptor.set(interceptor);
	}

	private static Interceptor getInterceptor() {
		Interceptor interceptor = threadInterceptor.get();
		return interceptor;
	}

}

