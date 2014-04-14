package org.teragrid.service.profile.wsclients;

import java.util.Map;

import org.teragrid.service.profile.wsclients.model.ComputeDTO;
import org.testng.Assert;
import org.testng.annotations.Test;

public class CtssClientTest {

	@Test
	public void getResources() {
		CtssClient client = new CtssClient(AbstractClientTest.CTSS_SERVER);
		Map<String, ComputeDTO> systemMap = client.getResources();

		Assert.assertFalse(systemMap.isEmpty(), "CTSS call returned no systems.");
		System.out.println("\n\nPrinting systems from ctss server");
		for(ComputeDTO system: systemMap.values())
		{
			System.out.println("\t" + system.toString());
		}
	}
}
