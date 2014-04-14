package org.teragrid.service.profile.util;

import java.util.Map;

import org.teragrid.service.profile.wsclients.model.ComputeDTO;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ResourceCacheTest {

	@Test(dependsOnMethods={"getResourceByResourceId"})
	public void getResourceByName() {
		throw new RuntimeException("Test not implemented");
	}

	@Test(dependsOnMethods={"getResourceByTgcdbName"})
	public void getResourceByResourceId() {
		throw new RuntimeException("Test not implemented");
	}

	@Test(dependsOnMethods={"getResources"})
	public void getResourceByTgcdbName() {
		throw new RuntimeException("Test not implemented");
	}

	@Test
	public void getResources() {
		Map<String, ComputeDTO> resources = ResourceCache.getResources();
		Assert.assertFalse(resources.isEmpty(), "No resources returned from ResourceCache");
		
		java.lang.System.out.println("\n\nResource cache returned " + resources.size() + " resources:");
		for (ComputeDTO system: resources.values()) {
			java.lang.System.out.println("\t" + system.toString());
		}
	}
}
