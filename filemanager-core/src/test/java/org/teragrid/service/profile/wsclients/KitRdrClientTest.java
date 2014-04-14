package org.teragrid.service.profile.wsclients;

import java.util.Map;

import org.teragrid.service.profile.wsclients.model.ComputeDTO;
import org.testng.Assert;
import org.testng.annotations.Test;


public class KitRdrClientTest extends AbstractClientTest 
{
	
	@Test
	public void getResources() 
	{
		KitRdrClient client = new KitRdrClient(AbstractClientTest.RDR_SERVICES_SERVER);
		Map<String, ComputeDTO> systemMap = client.getResources();
		
		Assert.assertFalse(systemMap.isEmpty(), "RDR call returned no systems.");
		System.out.println("\n\nPrinting systems from rdr server");
		for(ComputeDTO system: systemMap.values())
		{
			System.out.println("\t" + system.toString());
		}
	}
}
