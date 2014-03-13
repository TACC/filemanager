/**
 * 
 */
package edu.utexas.tacc.wcs.filemanager.common.model;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author dooley
 *
 */
public class Acctv {

	private Integer requestId;
	private String grantNumber;
	private String proposalNumber;
	private String projectTitle;
	private Integer fosId;
	private String fos;
	private Integer accountId;
	private String chargeNumber;
	private Integer allocationId;
	private Date startDate;
	private Date endDate;
	private BigDecimal baseAllocation;
	private BigDecimal remainingAllocation;
	private String allocType;
	private Integer allocResourceId;
	private String allocResourceName;
	private String projState;
	private String projStateComments;
	private Date projStateTs;
	private Integer piPersonId;
	private String piLastName;
	private String piFirstName;
	private Integer personId;
	private String firstName;
	private String lastName;
	private Boolean isPi;
	private BigDecimal usedAllocation;
	private String acctState;
	private Date acctStateTs;

	public Acctv() {
	}

	public Integer getRequestId() {
		return this.requestId;
	}

	public void setRequestId(Integer requestId) {
		this.requestId = requestId;
	}

	public String getGrantNumber() {
		return this.grantNumber;
	}

	public void setGrantNumber(String grantNumber) {
		this.grantNumber = grantNumber;
	}

	public String getProposalNumber() {
		return this.proposalNumber;
	}

	public void setProposalNumber(String proposalNumber) {
		this.proposalNumber = proposalNumber;
	}

	public String getProjectTitle() {
		return this.projectTitle;
	}

	public void setProjectTitle(String projectTitle) {
		this.projectTitle = projectTitle;
	}

	public Integer getFosId() {
		return this.fosId;
	}

	public void setFosId(Integer fosId) {
		this.fosId = fosId;
	}

	public String getFos() {
		return this.fos;
	}

	public void setFos(String fos) {
		this.fos = fos;
	}

	public Integer getAccountId() {
		return this.accountId;
	}

	public void setAccountId(Integer accountId) {
		this.accountId = accountId;
	}

	public String getChargeNumber() {
		return this.chargeNumber;
	}

	public void setChargeNumber(String chargeNumber) {
		this.chargeNumber = chargeNumber;
	}

	public Integer getAllocationId() {
		return this.allocationId;
	}

	public void setAllocationId(Integer allocationId) {
		this.allocationId = allocationId;
	}

	public Date getStartDate() {
		return this.startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return this.endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public BigDecimal getBaseAllocation() {
		return this.baseAllocation;
	}

	public void setBaseAllocation(BigDecimal baseAllocation) {
		this.baseAllocation = baseAllocation;
	}

	public BigDecimal getRemainingAllocation() {
		return this.remainingAllocation;
	}

	public void setRemainingAllocation(BigDecimal remainingAllocation) {
		this.remainingAllocation = remainingAllocation;
	}

	public String getAllocType() {
		return this.allocType;
	}

	public void setAllocType(String allocType) {
		this.allocType = allocType;
	}

	public Integer getAllocResourceId() {
		return this.allocResourceId;
	}

	public void setAllocResourceId(Integer allocResourceId) {
		this.allocResourceId = allocResourceId;
	}

	public String getAllocResourceName() {
		return this.allocResourceName;
	}

	public void setAllocResourceName(String allocResourceName) {
		this.allocResourceName = allocResourceName;
	}

	public String getProjState() {
		return this.projState;
	}

	public void setProjState(String projState) {
		this.projState = projState;
	}

	public String getProjStateComments() {
		return this.projStateComments;
	}

	public void setProjStateComments(String projStateComments) {
		this.projStateComments = projStateComments;
	}

	public Date getProjStateTs() {
		return this.projStateTs;
	}

	public void setProjStateTs(Date projStateTs) {
		this.projStateTs = projStateTs;
	}

	public Integer getPiPersonId() {
		return this.piPersonId;
	}

	public void setPiPersonId(Integer piPersonId) {
		this.piPersonId = piPersonId;
	}

	public String getPiLastName() {
		return this.piLastName;
	}

	public void setPiLastName(String piLastName) {
		this.piLastName = piLastName;
	}

	public String getPiFirstName() {
		return this.piFirstName;
	}

	public void setPiFirstName(String piFirstName) {
		this.piFirstName = piFirstName;
	}

	public Integer getPersonId() {
		return this.personId;
	}

	public void setPersonId(Integer personId) {
		this.personId = personId;
	}

	public String getFirstName() {
		return this.firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return this.lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public Boolean getIsPi() {
		return this.isPi;
	}

	public void setIsPi(Boolean isPi) {
		this.isPi = isPi;
	}

	public BigDecimal getUsedAllocation() {
		return this.usedAllocation;
	}

	public void setUsedAllocation(BigDecimal usedAllocation) {
		this.usedAllocation = usedAllocation;
	}

	public String getAcctState() {
		return this.acctState;
	}

	public void setAcctState(String acctState) {
		this.acctState = acctState;
	}

	public Date getAcctStateTs() {
		return this.acctStateTs;
	}

	public void setAcctStateTs(Date acctStateTs) {
		this.acctStateTs = acctStateTs;
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof Acctv))
			return false;
		Acctv castOther = (Acctv) other;

		return ((this.getRequestId() == castOther.getRequestId()) || (this
				.getRequestId() != null
				&& castOther.getRequestId() != null && this.getRequestId()
				.equals(castOther.getRequestId())))
				&& ((this.getGrantNumber() == castOther.getGrantNumber()) || (this
						.getGrantNumber() != null
						&& castOther.getGrantNumber() != null && this
						.getGrantNumber().equals(castOther.getGrantNumber())))
				&& ((this.getProposalNumber() == castOther.getProposalNumber()) || (this
						.getProposalNumber() != null
						&& castOther.getProposalNumber() != null && this
						.getProposalNumber().equals(
								castOther.getProposalNumber())))
				&& ((this.getProjectTitle() == castOther.getProjectTitle()) || (this
						.getProjectTitle() != null
						&& castOther.getProjectTitle() != null && this
						.getProjectTitle().equals(castOther.getProjectTitle())))
				&& ((this.getFosId() == castOther.getFosId()) || (this
						.getFosId() != null
						&& castOther.getFosId() != null && this.getFosId()
						.equals(castOther.getFosId())))
				&& ((this.getFos() == castOther.getFos()) || (this.getFos() != null
						&& castOther.getFos() != null && this.getFos().equals(
						castOther.getFos())))
				&& ((this.getAccountId() == castOther.getAccountId()) || (this
						.getAccountId() != null
						&& castOther.getAccountId() != null && this
						.getAccountId().equals(castOther.getAccountId())))
				&& ((this.getChargeNumber() == castOther.getChargeNumber()) || (this
						.getChargeNumber() != null
						&& castOther.getChargeNumber() != null && this
						.getChargeNumber().equals(castOther.getChargeNumber())))
				&& ((this.getAllocationId() == castOther.getAllocationId()) || (this
						.getAllocationId() != null
						&& castOther.getAllocationId() != null && this
						.getAllocationId().equals(castOther.getAllocationId())))
				&& ((this.getStartDate() == castOther.getStartDate()) || (this
						.getStartDate() != null
						&& castOther.getStartDate() != null && this
						.getStartDate().equals(castOther.getStartDate())))
				&& ((this.getEndDate() == castOther.getEndDate()) || (this
						.getEndDate() != null
						&& castOther.getEndDate() != null && this.getEndDate()
						.equals(castOther.getEndDate())))
				&& ((this.getBaseAllocation() == castOther.getBaseAllocation()) || (this
						.getBaseAllocation() != null
						&& castOther.getBaseAllocation() != null && this
						.getBaseAllocation().equals(
								castOther.getBaseAllocation())))
				&& ((this.getRemainingAllocation() == castOther
						.getRemainingAllocation()) || (this
						.getRemainingAllocation() != null
						&& castOther.getRemainingAllocation() != null && this
						.getRemainingAllocation().equals(
								castOther.getRemainingAllocation())))
				&& ((this.getAllocType() == castOther.getAllocType()) || (this
						.getAllocType() != null
						&& castOther.getAllocType() != null && this
						.getAllocType().equals(castOther.getAllocType())))
				&& ((this.getAllocResourceId() == castOther
						.getAllocResourceId()) || (this.getAllocResourceId() != null
						&& castOther.getAllocResourceId() != null && this
						.getAllocResourceId().equals(
								castOther.getAllocResourceId())))
				&& ((this.getAllocResourceName() == castOther
						.getAllocResourceName()) || (this
						.getAllocResourceName() != null
						&& castOther.getAllocResourceName() != null && this
						.getAllocResourceName().equals(
								castOther.getAllocResourceName())))
				&& ((this.getProjState() == castOther.getProjState()) || (this
						.getProjState() != null
						&& castOther.getProjState() != null && this
						.getProjState().equals(castOther.getProjState())))
				&& ((this.getProjStateComments() == castOther
						.getProjStateComments()) || (this
						.getProjStateComments() != null
						&& castOther.getProjStateComments() != null && this
						.getProjStateComments().equals(
								castOther.getProjStateComments())))
				&& ((this.getProjStateTs() == castOther.getProjStateTs()) || (this
						.getProjStateTs() != null
						&& castOther.getProjStateTs() != null && this
						.getProjStateTs().equals(castOther.getProjStateTs())))
				&& ((this.getPiPersonId() == castOther.getPiPersonId()) || (this
						.getPiPersonId() != null
						&& castOther.getPiPersonId() != null && this
						.getPiPersonId().equals(castOther.getPiPersonId())))
				&& ((this.getPiLastName() == castOther.getPiLastName()) || (this
						.getPiLastName() != null
						&& castOther.getPiLastName() != null && this
						.getPiLastName().equals(castOther.getPiLastName())))
				&& ((this.getPiFirstName() == castOther.getPiFirstName()) || (this
						.getPiFirstName() != null
						&& castOther.getPiFirstName() != null && this
						.getPiFirstName().equals(castOther.getPiFirstName())))
				&& ((this.getPersonId() == castOther.getPersonId()) || (this
						.getPersonId() != null
						&& castOther.getPersonId() != null && this
						.getPersonId().equals(castOther.getPersonId())))
				&& ((this.getFirstName() == castOther.getFirstName()) || (this
						.getFirstName() != null
						&& castOther.getFirstName() != null && this
						.getFirstName().equals(castOther.getFirstName())))
				&& ((this.getLastName() == castOther.getLastName()) || (this
						.getLastName() != null
						&& castOther.getLastName() != null && this
						.getLastName().equals(castOther.getLastName())))
				&& ((this.getIsPi() == castOther.getIsPi()) || (this.getIsPi() != null
						&& castOther.getIsPi() != null && this.getIsPi()
						.equals(castOther.getIsPi())))
				&& ((this.getUsedAllocation() == castOther.getUsedAllocation()) || (this
						.getUsedAllocation() != null
						&& castOther.getUsedAllocation() != null && this
						.getUsedAllocation().equals(
								castOther.getUsedAllocation())))
				&& ((this.getAcctState() == castOther.getAcctState()) || (this
						.getAcctState() != null
						&& castOther.getAcctState() != null && this
						.getAcctState().equals(castOther.getAcctState())))
				&& ((this.getAcctStateTs() == castOther.getAcctStateTs()) || (this
						.getAcctStateTs() != null
						&& castOther.getAcctStateTs() != null && this
						.getAcctStateTs().equals(castOther.getAcctStateTs())));
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result
				+ (getRequestId() == null ? 0 : this.getRequestId().hashCode());
		result = 37
				* result
				+ (getGrantNumber() == null ? 0 : this.getGrantNumber()
						.hashCode());
		result = 37
				* result
				+ (getProposalNumber() == null ? 0 : this.getProposalNumber()
						.hashCode());
		result = 37
				* result
				+ (getProjectTitle() == null ? 0 : this.getProjectTitle()
						.hashCode());
		result = 37 * result
				+ (getFosId() == null ? 0 : this.getFosId().hashCode());
		result = 37 * result
				+ (getFos() == null ? 0 : this.getFos().hashCode());
		result = 37 * result
				+ (getAccountId() == null ? 0 : this.getAccountId().hashCode());
		result = 37
				* result
				+ (getChargeNumber() == null ? 0 : this.getChargeNumber()
						.hashCode());
		result = 37
				* result
				+ (getAllocationId() == null ? 0 : this.getAllocationId()
						.hashCode());
		result = 37 * result
				+ (getStartDate() == null ? 0 : this.getStartDate().hashCode());
		result = 37 * result
				+ (getEndDate() == null ? 0 : this.getEndDate().hashCode());
		result = 37
				* result
				+ (getBaseAllocation() == null ? 0 : this.getBaseAllocation()
						.hashCode());
		result = 37
				* result
				+ (getRemainingAllocation() == null ? 0 : this
						.getRemainingAllocation().hashCode());
		result = 37 * result
				+ (getAllocType() == null ? 0 : this.getAllocType().hashCode());
		result = 37
				* result
				+ (getAllocResourceId() == null ? 0 : this.getAllocResourceId()
						.hashCode());
		result = 37
				* result
				+ (getAllocResourceName() == null ? 0 : this
						.getAllocResourceName().hashCode());
		result = 37 * result
				+ (getProjState() == null ? 0 : this.getProjState().hashCode());
		result = 37
				* result
				+ (getProjStateComments() == null ? 0 : this
						.getProjStateComments().hashCode());
		result = 37
				* result
				+ (getProjStateTs() == null ? 0 : this.getProjStateTs()
						.hashCode());
		result = 37
				* result
				+ (getPiPersonId() == null ? 0 : this.getPiPersonId()
						.hashCode());
		result = 37
				* result
				+ (getPiLastName() == null ? 0 : this.getPiLastName()
						.hashCode());
		result = 37
				* result
				+ (getPiFirstName() == null ? 0 : this.getPiFirstName()
						.hashCode());
		result = 37 * result
				+ (getPersonId() == null ? 0 : this.getPersonId().hashCode());
		result = 37 * result
				+ (getFirstName() == null ? 0 : this.getFirstName().hashCode());
		result = 37 * result
				+ (getLastName() == null ? 0 : this.getLastName().hashCode());
		result = 37 * result
				+ (getIsPi() == null ? 0 : this.getIsPi().hashCode());
		result = 37
				* result
				+ (getUsedAllocation() == null ? 0 : this.getUsedAllocation()
						.hashCode());
		result = 37 * result
				+ (getAcctState() == null ? 0 : this.getAcctState().hashCode());
		result = 37
				* result
				+ (getAcctStateTs() == null ? 0 : this.getAcctStateTs()
						.hashCode());
		return result;
	}	

}
