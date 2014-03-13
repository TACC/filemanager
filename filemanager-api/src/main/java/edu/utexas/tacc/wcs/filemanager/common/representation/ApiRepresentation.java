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
package edu.utexas.tacc.wcs.filemanager.common.representation;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.restlet.Request;
import org.restlet.data.CharacterSet;
import org.restlet.data.MediaType;
import org.restlet.representation.StringRepresentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Wrapper class for all responses from the api
 * 
 * @author dooley
 *
 */
public abstract class ApiRepresentation extends StringRepresentation {

	private boolean prettyPrint = false;
	
	/**
	 * Formats the reponse from iplant api calls into a json object with
	 * attributes status, message, and result where result contains the
	 * json output of the call.
	 * 
	 * @param jsonArray
	 */
	protected ApiRepresentation(String status, String message, String json) 
	{	
		super("", MediaType.APPLICATION_JSON, null, CharacterSet.UTF_8);
		
		try
		{
			ObjectMapper mapper = new ObjectMapper();
			
			ObjectNode jsonWrapper = mapper.createObjectNode()
				.put("status", status)
				.put("version","1");
			
			if (StringUtils.isEmpty(message)) {
				jsonWrapper.putNull("message");
			} else {
				jsonWrapper.put("message", message);
			}
			
			if (!StringUtils.isEmpty(json)) {
				jsonWrapper.set("result", mapper.readTree(json));
			}
			
			Boolean prettyPrint = BooleanUtils.toBoolean((String)Request.getCurrent().getAttributes().get("pretty"));
			
			if (prettyPrint) {
				setText(mapper.writer().withDefaultPrettyPrinter().writeValueAsString(jsonWrapper));
			} else {
				setText(jsonWrapper.toString());
			}
		} 
		catch (Exception e)
		{
			status = status.replaceAll("\"", "\\\"");
			
			if (message == null) {
				message = "";
			} else {
				message = message.replaceAll("\"", "\\\"");
			}
			
			StringBuilder builder = new StringBuilder();
			builder.append("{\"status\":\"" + status + "\",");
			builder.append("\"message\":\"" + message + "\",");
			builder.append("\"version\":\"" + "1" + "\",");
			builder.append("\"result\":" + json + "}");
			setText(builder.toString());
		}
		
	}

	/**
	 * @return the prettyPrint
	 */
	public boolean isPrettyPrint()
	{
		return prettyPrint;
	}

	/**
	 * @param prettyPrint the prettyPrint to set
	 */
	public void setPrettyPrint(boolean prettyPrint)
	{
		this.prettyPrint = prettyPrint;
	}
}
