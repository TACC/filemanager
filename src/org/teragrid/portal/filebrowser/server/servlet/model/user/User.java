/* 
 * Created on Dec 11, 2007
 * 
 * Developed by: Rion Dooley - dooley [at] tacc [dot] utexas [dot] edu
 * 				 TACC, Texas Advanced Computing Center
 * 
 * https://www.tacc.utexas.edu/
 */

package org.teragrid.portal.filebrowser.server.servlet.model.user;

/**
 * Holder for TGCDB user records
 * 
 * @author Rion Dooley < dooley [at] tacc [dot] utexas [dot] edu >
 * 
 */
public class User {
	private Long id;
	private String firstName = "";
	private String lastName = "";
	private String username = "";
	private String organization = "";
	private String department = "";
	private String dn = "";
	private String email = "";
	private String im = "";
	private String cell = "";

	public User() {
	}
	
	public User(String first, String last, String username) {
		this.firstName = first;
		this.lastName = last;
		this.username = username;
	}

	public User(Long id, String first, String last, String organization,
			String department, String username, String email, String cell,
			String dn) {
		this.id = id;
		this.firstName = first;
		this.lastName = last;
		this.organization = organization;
		this.department = department;
		this.username = username;
		this.email = email;
		this.cell = cell;
		this.dn = dn;
	}

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the cell
	 */
	public String getCell() {
		return cell;
	}

	/**
	 * @param cell
	 *            the cell to set
	 */
	public void setCell(String cell) {
		this.cell = cell;
	}

	/**
	 * @return the dn
	 */
	public String getDn() {
		return dn;
	}

	/**
	 * @param dn
	 *            the dn to set
	 */
	public void setDn(String dn) {
		this.dn = dn;
	}

	// /**
	// * @param dn
	// */
	// public void addDn(DN dn) {
	// this.dns.add(dn);
	// }

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email
	 *            the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return the firstName
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * @param firstName
	 *            the firstName to set
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/**
	 * @return the im
	 */
	public String getIm() {
		return im;
	}

	/**
	 * @param im
	 *            the im to set
	 */
	public void setIm(String im) {
		this.im = im;
	}

	/**
	 * @return the lastName
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * @param lastName
	 *            the lastName to set
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username
	 *            the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	public String getOrganization() {
		return organization;
	}

	public void setOrganization(String organization) {
		this.organization = organization;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public int hashCode() {
		return new String(username + id + dn.hashCode()).hashCode();
	}

	public int compareTo(Object o) {
		// CategorizedUsers are sorted by date
		if (o instanceof User)
			return new Long(id).compareTo(new Long(((User) o).id));
		return 0;
	}

	public boolean equals(Object o) {
		if (o instanceof User) {
			if (!dn.equals(((User) o).dn))
				return false;
			if (!username.equals(((User) o).username))
				return false;
			if (id != ((User) o).id)
				return false;
		}
		return false;
	}

	public String toString() {
//		return "[" + id + ", " + firstName + " " + lastName + ", " + username
//				+ ", " + dn + ", " + email + ", " + organization + ", "
//				+ department + ", " + cell + ", " + im + "]";
		return firstName + " " + lastName;
	}

	public User shallowCopy() {
		User user = new User(((id == null) ? null : new Long(id)), new String(
				firstName == null ? "" : firstName), new String(
				lastName == null ? "" : lastName), new String(
				organization == null ? "" : organization), new String(
				department == null ? "" : department), new String(
				username == null ? "" : username), new String(
				email == null ? "" : email), new String(cell == null ? ""
				: cell), new String(dn == null ? "" : dn));

		user.setIm(new String((im == null ? "" : im)));

		return user;
	}

	public String getWholeName() {
		return firstName + " " + lastName;
	}
	// public String listDns() {
	// String dnString = "";
	// for (DN dn: dns) {
	// dnString += "\n" + dn.getDn();
	// }
	// return dnString;
	// }

}
