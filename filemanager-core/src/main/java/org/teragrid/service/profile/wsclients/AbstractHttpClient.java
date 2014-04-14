/**
 * 
 */
package org.teragrid.service.profile.wsclients;

import static bad.robot.http.HttpClients.anApacheClient;
import static bad.robot.http.configuration.BasicAuthCredentials.basicAuth;
import static bad.robot.http.configuration.Password.password;
import static bad.robot.http.configuration.Username.username;

import java.net.URL;

import bad.robot.http.CommonHttpClient;
import bad.robot.http.HttpResponse;
import bad.robot.http.configuration.AuthorisationCredentials;
import edu.utexas.tacc.wcs.filemanager.common.exception.ResourceException;

/**
 * @author dooley
 *
 */
public class AbstractHttpClient {
	
	protected String doGet(String endpoint) throws Exception
	{
		CommonHttpClient client = null;
		HttpResponse response = null;
		URL endpointUrl = null;
		try 
		{
			endpointUrl = new URL(endpoint);
		
			client = anApacheClient();
			
			response = client.withTrustingSsl()
						.withTrustingSsl()
						.get(endpointUrl);
			
			if (response.ok()) {
				return response.getContent().asString();
			} else {
				throw new ResourceException(response.getStatusMessage());
			}
		}
		catch (Throwable e)
		{	
			throw new ResourceException("Failed to contact remote service.", e);
		}
	}
	
	protected String doGet(String endpoint, String username, String password) throws Exception 
	{
		URL endpointUrl = null;
		try 
		{
			endpointUrl = new URL(endpoint);
		
			CommonHttpClient client = anApacheClient();
			HttpResponse response = null;
			
			
			AuthorisationCredentials basicAuthCredentials = basicAuth(
					username(username), password(password), endpointUrl);
			
			response = client.withTrustingSsl()
					.with(basicAuthCredentials)
					.get(endpointUrl);
		
			if (response.ok()) {
				return response.getContent().asString();
			} else {
				throw new ResourceException(response.getStatusMessage());
			}
		}
		catch (Throwable e)
		{	
			e.printStackTrace();
			throw new ResourceException("Failed to contact remote service.", e);
		}
	}
}
