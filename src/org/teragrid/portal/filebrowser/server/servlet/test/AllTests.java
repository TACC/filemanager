package org.teragrid.portal.filebrowser.server.servlet.test;


import org.teragrid.portal.filebrowser.server.servlet.persistence.HibernateUtil;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

/**
 * Run all unit tests for TGFM middlewae service.
 *
 * @author Rion Dooley <dooley [at] cct [dot] lsu [dot] edu>
 */
public class AllTests {

	public static Test suite() {

		TestSuite suite = new TestSuite();

		// API tests
//        suite.addTest( UserTest.suite() );
        suite.addTest( ResourceTest.suite() );
//        suite.addTest( TransferTest.suite() );
//        suite.addTest( NotificationTest.suite() );
        
//		suite.addTest( InterfaceDNTest.suite() );
//		suite.addTest( ResourceInterfaceTest.suite() );
//		suite.addTest( FileTransferHistoryInterfaceTest.suite() );
//		suite.addTest( NotificationInterfaceTest.suite() );
		
		return suite;
	}

	public static void main(String args[]) {
		TestRunner.run( suite() );
		HibernateUtil.closeSession();
	}
}
