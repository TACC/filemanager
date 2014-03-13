/* 
 * Created on Jun 22, 2008
 * 
 * Developed by: Rion Dooley - dooley [at] tacc [dot] utexas [dot] edu
 * 				 TACC, Texas Advanced Computing Center
 * 
 * https://www.tacc.utexas.edu/
 */

package edu.utexas.tacc.wcs.filemanager.service.dao;

import java.util.List;

import org.hibernate.Session;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import edu.utexas.tacc.wcs.filemanager.common.model.Task;
import edu.utexas.tacc.wcs.filemanager.common.model.Transfer;
import edu.utexas.tacc.wcs.filemanager.service.persistence.HibernateUtil;
import edu.utexas.tacc.wcs.filemanager.service.resources.TestCaseWithData;

/**
 * Test class for transfer persistence and management
 * 
 * @author Rion Dooley < dooley [at] tacc [dot] utexas [dot] edu >
 * 
 */

public class TransferDAOTest extends TestCaseWithData {

	private TransferDAO dao = new TransferDAO();
	
	/**
	 * Clears all transfers from the db prior before each test
	 */
	@AfterMethod
	@BeforeMethod
	public void afterMethod()
	{
		Session session = HibernateUtil.getSession();
		session.clear();
		session.createSQLQuery("delete Transfer").executeUpdate();
		session.flush();
		session.close();
	}
	
	@Test
	public void testTransferInsert() {

		Transfer transfer = createTransfer();

		dao.makePersistent(transfer);

		Assert.assertNotNull(transfer.getId(), "Transfer was not saved.");
	}

	@Test(dependsOnMethods={"testTransferInsert"})
	public void testTransferFind() {

		// save a transfer for the test
		Transfer transfer = createTransfer();
		dao.makePersistent(transfer);
		Assert.assertNotNull(transfer.getId(), "Transfer was not saved.");
		
		List<Transfer> transfers = dao.getAllTransfersByDN(dn);
		Assert.assertNotNull(transfers, "No transfers found.");
		Assert.assertEquals(transfers.size(), 1, "Incorrect number of transfers found.");
		Assert.assertEquals(transfers.get(0), transfer, "Wrong transfer record returned.");
	}

	@Test(dependsOnMethods={"testTransferFind"})
	public void testTransferCount() {

		// save a transfer for the test
		Transfer transfer = createTransfer();
		dao.makePersistent(transfer);
		Assert.assertNotNull(transfer.getId(), "Transfer was not saved.");
				
		int count = dao.getTransfersCount(dn);

		Assert.assertTrue(count > 0, "Transfer count was incorrect");
	}

	@Test(dependsOnMethods={"testTransferCount"})
	public void testTransferUpdate() {

		// save a transfer for the test
		Transfer transfer = createTransfer();
		dao.makePersistent(transfer);
		Assert.assertNotNull(transfer.getId(), "Transfer was not saved.");
		transfer.setStatus(Task.ONGOING);
		dao.makePersistent(transfer);

		List<Transfer> transfers = dao.getAllTransfersByDN(dn);
		Transfer savedTransfer = transfers.get(0);
		
		Assert.assertEquals(savedTransfer.getStatus(), Task.ONGOING, "Transfer status was not updated.");

	}
	
	@Test(dependsOnMethods={"testTransferUpdate"})
	public void testTransferExists() 
	{
		Transfer transfer = createTransfer();
		dao.makePersistent(transfer);
		Assert.assertNotNull(transfer.getId(), "Transfer was not saved.");

		Assert.assertTrue(dao.exists(transfer.getId()), "Transfer was not found.");
	}

	@Test(dependsOnMethods={"testTransferExists"})
	public void testTransferDelete() {

		Transfer transfer = createTransfer();
		dao.makePersistent(transfer);
		Assert.assertNotNull(transfer.getId(), "Transfer was not saved.");

		dao.delete(transfer.getId());

		Assert.assertFalse(dao.exists(transfer.getId()), "Transfer was not deleted.");
	}	
}
