package org.teragrid.service.profile.wsclients;

import java.util.Map;

import org.teragrid.service.profile.wsclients.model.ComputeDTO;
import org.testng.Assert;
import org.testng.annotations.Test;

public class KitServicesClientTest {

	@Test
	public void getResources() {
		KitServicesClient client = new KitServicesClient(AbstractClientTest.KIT_SERVICES_SERVER);
		Map<String, ComputeDTO> systemMap = client.getResources();
		
		Assert.assertFalse(systemMap.isEmpty(), "Kit Services call returned no systems.");
		System.out.println("\n\nPrinting systems from kit services server");
		for(ComputeDTO system: systemMap.values())
		{
			System.out.println("\t" + system.toString());
		}
	}
}
