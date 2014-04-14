package org.teragrid.service.profile.util;

import java.util.List;
import java.util.Map;

import org.teragrid.service.profile.wsclients.model.ComputeDTO;
import org.testng.Assert;
import org.testng.annotations.Test;

import edu.utexas.tacc.wcs.filemanager.common.model.System;
import edu.utexas.tacc.wcs.filemanager.common.model.User;
import edu.utexas.tacc.wcs.filemanager.service.dao.SystemDAO;

public class XsedeSystemResolverTest {

	@Test
	public void resolveResources() {
		User user = new User();
		user.setId((long)934);
//		user.setId((long)99);
		List<System> tgcdbResources = SystemDAO.findSystemAccounts(user);
		Assert.assertFalse(tgcdbResources.isEmpty(), "No resources returned from db for user " + user.getId());
		
		java.lang.System.out.println("\n\nDatabase returned " + tgcdbResources.size() + " resources:");
		for (System system: tgcdbResources) {
			java.lang.System.out.println("\t" + system.getResourceId());
		}
		
		Map<String, ComputeDTO> resources = ResourceCache.getResources();
		Assert.assertFalse(resources.isEmpty(), "No resources returned from ResourceCache");
		
		java.lang.System.out.println("\n\nResource cache returned " + resources.size() + " resources:");
		for (ComputeDTO system: resources.values()) {
			java.lang.System.out.println("\t" + system.getTgcdbName());
		}
		
		List<System> resolvedSystems = XsedeSystemResolver.resolveResources(tgcdbResources); 
		Assert.assertFalse(resolvedSystems.isEmpty(), "No resources returned after resolving");
		java.lang.System.out.println("\n\nResource cache returned " + resolvedSystems.size() + " resources:");
		for (System system: resolvedSystems) {
			java.lang.System.out.println("\t" + system.toString());
		}
	}
}
