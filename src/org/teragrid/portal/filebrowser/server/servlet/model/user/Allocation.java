/**
 * 
 */
package org.teragrid.portal.filebrowser.server.servlet.model.user;

import java.math.BigDecimal;
import java.util.Date;


/**
 * @author dooley
 *
 */
public class Allocation {

	private Integer requestId;
	private String projectTitle;
	private Integer allocationId;
	private Date startDate;
	private Date endDate;
	private BigDecimal baseAllocation;
	private BigDecimal remainingAllocation;
	private String allocResourceName;
	private String projState;
	private String piLastName;
	private String piFirstName;
	private Integer personId;
	private String firstName;
	private String lastName;
	private Boolean isPi;
	private BigDecimal usedAllocation;
	private String acctState;
	

	public Allocation() {
	}

	public Allocation(Acctv acctv) {
		requestId = acctv.getRequestId();
		projectTitle = acctv.getProjectTitle();
		allocationId = acctv.getAllocationId();
		startDate = acctv.getStartDate();
		endDate = acctv.getEndDate();
		baseAllocation = acctv.getBaseAllocation();
		remainingAllocation = acctv.getRemainingAllocation();
		allocResourceName = acctv.getAllocResourceName();
		projState = acctv.getProjState();
		piLastName = acctv.getPiLastName();
		piFirstName = acctv.getPiFirstName();
		personId = acctv.getPersonId();
		firstName = acctv.getFirstName();
		lastName = acctv.getLastName();
		usedAllocation = acctv.getUsedAllocation();
		acctState = acctv.getAcctState();
		isPi = acctv.getIsPi();
	}

	public Integer getRequestId() {
		return requestId;
	}

	public void setRequestId(Integer requestId) {
		this.requestId = requestId;
	}

	public String getProjectTitle() {
		return projectTitle;
	}

	public void setProjectTitle(String projectTitle) {
		this.projectTitle = projectTitle;
	}

	public Integer getAllocationId() {
		return allocationId;
	}

	public void setAllocationId(Integer allocationId) {
		this.allocationId = allocationId;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public BigDecimal getBaseAllocation() {
		return baseAllocation;
	}

	public void setBaseAllocation(BigDecimal baseAllocation) {
		this.baseAllocation = baseAllocation;
	}

	public BigDecimal getRemainingAllocation() {
		return remainingAllocation;
	}

	public void setRemainingAllocation(BigDecimal remainingAllocation) {
		this.remainingAllocation = remainingAllocation;
	}

	public String getAllocResourceName() {
		return allocResourceName;
	}

	public void setAllocResourceName(String allocResourceName) {
		this.allocResourceName = allocResourceName;
	}

	public String getProjState() {
		return projState;
	}

	public void setProjState(String projState) {
		this.projState = projState;
	}

	public String getPiLastName() {
		return piLastName;
	}

	public void setPiLastName(String piLastName) {
		this.piLastName = piLastName;
	}

	public String getPiFirstName() {
		return piFirstName;
	}

	public void setPiFirstName(String piFirstName) {
		this.piFirstName = piFirstName;
	}

	public Integer getPersonId() {
		return personId;
	}

	public void setPersonId(Integer personId) {
		this.personId = personId;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public Boolean getIsPi() {
		return isPi;
	}

	public void setIsPi(Boolean isPi) {
		this.isPi = isPi;
	}

	public BigDecimal getUsedAllocation() {
		return usedAllocation;
	}

	public void setUsedAllocation(BigDecimal usedAllocation) {
		this.usedAllocation = usedAllocation;
	}

	public String getAcctState() {
		return acctState;
	}

	public void setAcctState(String acctState) {
		this.acctState = acctState;
	}

}
