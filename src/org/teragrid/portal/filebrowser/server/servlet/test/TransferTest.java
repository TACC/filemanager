/* 
 * Created on Jun 22, 2008
 * 
 * Developed by: Rion Dooley - dooley [at] tacc [dot] utexas [dot] edu
 * 				 TACC, Texas Advanced Computing Center
 * 
 * https://www.tacc.utexas.edu/
 */

package org.teragrid.portal.filebrowser.server.servlet.test;

import java.io.FileOutputStream;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.teragrid.portal.filebrowser.applet.transfer.FileTransferTask;
import org.teragrid.portal.filebrowser.applet.transfer.Task;
import org.teragrid.portal.filebrowser.server.servlet.dao.TransferDAO;
import org.teragrid.portal.filebrowser.server.servlet.model.filetransfer.Transfer;

/**
 * Test class for transfer persistence and management
 * 
 * @author Rion Dooley < dooley [at] tacc [dot] utexas [dot] edu >
 *
 */
public class TransferTest extends TestCaseWithData {

    
    public void testTransferFind() {
        
        List<FileTransferTask> transfers = new TransferDAO().getAllTransfersByDN(dn);
        
        assertNotNull(transfers);
    }
    
    public void testTransferInsert() {
        
        Transfer transfer = createTransfer();
        
        new TransferDAO().makePersistent(transfer);
        
        assertNotNull(transfer.getId());
        
        props.put(TRANSFER_ID_KEY, transfer.getId().toString());
    }
    
    public void testTransferCount() {
        
        int count = new TransferDAO().getTransfersCount(dn);
        
        assertTrue(count > 0);
    }
    
    
    public void testTransferUpdate() {
        
        assertTrue(transferId != null && !transferId.equals(""));
        
        TransferDAO transferDAO = new TransferDAO();
        
        Transfer transfer = transferDAO.getTransferById(new Integer(transferId), true);
        
        transfer.setStatus(Task.ONGOING);
        
        transferDAO.makePersistent(transfer);
        
        assertEquals(transfer.getStatus(),Task.ONGOING);
        
    }
    
    public void testTransferDelete() {
        
        assertTrue(transferId != null && !transferId.equals(""));
        
        new TransferDAO().delete(new Integer(transferId));
        
        assertFalse(TransferDAO.exists(new Integer(transferId)));
        
        props.remove(TRANSFER_ID_KEY);
        
    }
    
       
    /* (non-Javadoc)
     * @see org.teragrid.portal.filebrowser.server.servlet.test.TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        
        props.store(new FileOutputStream(TEST_PROPERTIES_LOCATION),"");
    }

    public TransferTest(String x) {
        super(x);
    }

    public static Test suite() {
        return new TestSuite(TransferTest.class);
    }  
    
}
