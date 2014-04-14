/**
 * 
 */
package org.teragrid.service.profile.wsclients.model;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author dooley
 *
 */
public class ComputeDTO extends ResourceDTO implements TgcdbDTO {
	
	private String id;
	private String tgcdbName;
	private String status = Service.UP;
	private String localUsername;
    private Load load;
//    private Set<Service> services = new HashSet<Service>();
	private String loginHostname;
	private Integer loginPort;
	private String gridftpHostname;
	private Integer gridftpPort;
	
	public ComputeDTO() {}
	
//	public ComputeDTO(TeraGridSystem resource) {
//		super();
//		this.tgcdbName = resource.getTgcdbName();
//		this.resourceId = resource.getResourceId();
//		this.name = resource.getName();
//		this.localUsername = resource.getLocalUsername();
//	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return the localUsername
	 */
	public String getLocalUsername() {
		return localUsername;
	}

	/**
	 * @param localUsername the localUsername to set
	 */
	public void setLocalUsername(String localUsername) {
		this.localUsername = localUsername;
	}

	/**
	 * @return the loads
	 */
	public Load getLoad() {
		return load;
	}

	/**
	 * @param loads the loads to set
	 */
	public void setLoad(Load load) {
		this.load = load;
	}

	/**
	 * @param loginHostname the loginHostname to set
	 */
	public void setLoginHostname(String loginHostname) {
		this.loginHostname = loginHostname;
	}

	/**
	 * @return the loginHostname
	 */
	public String getLoginHostname() {
		return loginHostname;
	}

	public Integer getLoginPort() {
		return loginPort;
	}

	public void setLoginPort(Integer loginPort) {
		this.loginPort = loginPort;
	}

	/**
	 * @param gridftpHostname the gridftpHostname to set
	 */
	public void setGridftpHostname(String gridftpHostname) {
		this.gridftpHostname = gridftpHostname;
	}

	/**
	 * @return the gridftpHostname
	 */
	public String getGridftpHostname() {
		return gridftpHostname;
	}

//	/**
//	 * @return the services
//	 */
//	public Set<Service> getServices() {
//		return services;
//	}
//
//	/**
//	 * @param services the services to set
//	 */
//	public void setServices(Set<Service> services) {
//		this.services = services;
//	}

	public Integer getGridftpPort() {
		return gridftpPort;
	}

	public void setGridftpPort(Integer gridftpPort) {
		this.gridftpPort = gridftpPort;
	}

	/**
	 * @return
	 */
	public String getTgcdbName() {
		return this.tgcdbName;
	}
	/**
	 * @param tgcdbName the tgcdbName to set
	 */
	public void setTgcdbName(String tgcdbName) {
		this.tgcdbName = tgcdbName;
	}



	/**
	 * @return the resourceId
	 */
	public String getResourceId() {
		return resourceId;
	}

	public boolean equals(Object o) {
		if (o instanceof String) {
			return (id.equals(o));
		} else if (o instanceof ComputeDTO) {
			ComputeDTO s = (ComputeDTO)o;
			if (!id.equals(s.id)) return false;
			if (!tgcdbName.equals(s.tgcdbName)) return false;
			if (!resourceId.equals(s.resourceId)) return false;
			if (!name.equals(s.name)) return false;
			return true;
		} else {
			return false;
		}
	}
	
	public String toJSONString() 
	{
		try {
			ObjectMapper mapper = new ObjectMapper();
		
			return mapper.createObjectNode()
				.put("site", this.getSite())
				.put("resource.id", this.resourceId)
				.put("tgcdb.name", this.tgcdbName)
				.put("resource.name", this.name)
				.put("name", this.name)
				.put("gsissh.host", this.loginHostname)
				.put("gridftp.host", this.gridftpHostname)
				.put("status", this.status)
				.put("localUsername", this.localUsername)
				.put("type", this.getType()).toString();
			
		} catch(Exception e)  {
			System.out.println("Error producing JSON output.");
			return null;
		}
	}
	
	public String toString() {
		return String.format("%s (%s)", resourceId, type);
	}

	public String toCsv() {
		return quote(getSite()) + "," + quote(resourceId) + "," + 
			quote(tgcdbName) + "," + quote(name) + "," +
			quote(loginHostname) + "," + quote(gridftpHostname) +
			quote(status) + "," + quote(localUsername) + "," +
			quote(getType());
//			+ load.toCsv() + ",";
	}

	private String quote(Object o) {
		return "\"" + (o == null?o:o.toString()) + "\"";
	}
	
	public String toHtml() {
		return "<tr>" + 
		formatColumn("<a href=\"http://info.teragrid.org/web-apps/html/ctss-resources-v1/SiteID/" + getSite() + "\">" + getSite() + "</a>") + 
		formatColumn(resourceId) + 
		formatColumn(tgcdbName) + formatColumn(name) +
		formatColumn(getLoginHostname()) + formatColumn(getGridftpHostname()) +
		formatColumn(status) + formatColumn(localUsername) +
		formatColumn(getType()) + "</tr>";// + load.toHtml();  
//		formatColumn(usedAllocation) + formatColumn(remainingAllocation) +
//		formatColumn(allocResourceName) + formatColumn(piFirstName) +
//		formatColumn(piLastName) + formatColumn(acctState) +
//		formatColumn(projState) + "</tr>";
	}
	
	public String toHtml(String url) {
		String path = "";
		if (url.contains("username")) {
			String username = url.substring(url.indexOf("username"));
			username = username.substring(username.indexOf("/")+1);
			username = username.substring(0,username.indexOf("/"));
			path = url.substring(0,(url.indexOf(username) + username.length()));
		} else {
			path = url.substring(0,url.indexOf("profile-v1")+"profile-v1".length());
		}
		return "<tr>" + 
		formatColumn("<a href=\"http://info.teragrid.org/web-apps/html/ctss-resources-v1/SiteID/" + getSite() + "\">" + getSite() + "</a>") + 
		formatColumn("<a href=\"" + path + "/resource/resourceid/" + resourceId + "\">" + resourceId + "</a>") + 
		formatColumn(tgcdbName) + formatColumn(name) +
		formatColumn(getLoginHostname()) + 
		formatColumn(getGridftpHostname()) +
		formatColumn(status) + 
		formatColumn("<a href=\"" + path + "\">" + localUsername + "</a>") +
		formatColumn(getType()) + "</tr>";
		// + load.toHtml();  
//		formatColumn(usedAllocation) + formatColumn(remainingAllocation) +
//		formatColumn(allocResourceName) + formatColumn(piFirstName) +
//		formatColumn(piLastName) + formatColumn(acctState) +
//		formatColumn(projState) + "</tr>";
	}

	private String formatColumn(Object o) {
		return "<td>" + (o == null?o:o.toString()) + "</td>";
	}
	
	public String toPerl() {
		//"id,requestId,project.title,,end.date," +
//		"base.allocation,used.allocation,remaining.allocation," +
//		"resource.name,pi.first.name,pi.last.name,account.state,project.state";
		String result = " {\n" + 
			"   site => '" + getSite() + "',\n" + 
			"   resource.id => '" + name + "',\n" + 
			"   tgcdb.name => '" + name + "',\n" +
			"   resource.name => '" + name + "',\n" +
			"   gsissh.host => '" + loginHostname + "',\n" + 
			"   gridftp.host => '" + gridftpHostname + "',\n" + 
			"   status => '" + status + "',\n" + 
			"   local.username => '" + localUsername + "',\n" + 
			"   site.name => '" + getSite() + "',\n" + 
			"   type => '" + getType() + "'}\n"; 
//			load.toPerl();
		
		return result;
	}
}
