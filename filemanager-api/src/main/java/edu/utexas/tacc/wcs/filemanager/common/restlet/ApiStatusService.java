/**
 * Copyright (c) 2014, Texas Advanced Computing Center
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * * Neither the name of the University of Texas at Austin nor the names of its
 *   contributors may be used to endorse or promote products derived from
 *   this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS AS IS
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those
 * of the authors and should not be interpreted as representing official policies,
 * either expressed or implied, of the FreeBSD Project.
 */
package edu.utexas.tacc.wcs.filemanager.common.restlet;

import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.service.StatusService;

import edu.utexas.tacc.wcs.filemanager.common.representation.ApiErrorRepresentation;
import edu.utexas.tacc.wcs.filemanager.common.representation.ApiRepresentation;
import edu.utexas.tacc.wcs.filemanager.common.representation.ApiSuccessRepresentation;

public class ApiStatusService extends StatusService 
{

	/* (non-Javadoc)
	 * @see org.restlet.service.StatusService#getStatus(java.lang.Throwable, org.restlet.data.Request, org.restlet.data.Response)
	 */
	@Override
	public Status getStatus(Throwable throwable, Request request,
			Response response)
	{
		return response.getStatus();
	}
	
	/* (non-Javadoc)
	 * @see org.restlet.service.StatusService#getRepresentation(org.restlet.data.Status, org.restlet.data.Request, org.restlet.data.Response)
	 */
	@Override
	public Representation getRepresentation(Status status,
			Request request, Response response)
	{
		try {
			Representation currentRepresentation = response.getEntity();
			Form form = request.getOriginalRef().getQueryAsForm();
			boolean prettyPrint = false;
			if (form != null) {
				prettyPrint = Boolean.parseBoolean(form.getFirstValue("pretty"));
			}
			if (currentRepresentation instanceof ApiRepresentation) {
				((ApiRepresentation)currentRepresentation).setPrettyPrint(prettyPrint);
				return currentRepresentation;
			} else if (status.isSuccess()) {
				return new ApiSuccessRepresentation();
			} else {
				String message = null;
				if (status.getCode() == 401) {
					if (request.getChallengeResponse() == null) {
						message = "Permission denied. No authentication credentials found.";
					} else {
						message = "Permission denied. Invalid authentication credentials";
					}
				} else {
					message = status.getDescription();
				}
				return new ApiErrorRepresentation(message);
			}
		} finally {
			//try { HibernateUtil.closeSession(); } catch(Exception e) {}
		}
	}
	
}