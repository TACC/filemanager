package edu.utexas.tacc.wcs.filemanager.service.resources;

import java.util.List;

import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.Put;

import edu.utexas.tacc.wcs.filemanager.common.model.BulkTransferRequest;
import edu.utexas.tacc.wcs.filemanager.common.model.Transfer;

public interface BulkTransfersResource {

	@Get
	public abstract List<Transfer> getAllTransfers();

	@Post
	public abstract List<Long> addMultipleTransfers(
			BulkTransferRequest bulkTransferRequest);
	
	@Put
	public abstract void update(BulkTransferRequest bulkTransferRequest);

	@Delete
	public abstract void clearAllTransfers();

}