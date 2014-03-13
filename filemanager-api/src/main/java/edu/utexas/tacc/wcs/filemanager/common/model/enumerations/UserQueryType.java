package edu.utexas.tacc.wcs.filemanager.common.model.enumerations;

public enum UserQueryType 
{
	EMAIL("email"), NAME("name"), USERNAME("username"); //,ORGANIZATION,DEPARTMENT
	
	private String value;
	
	private UserQueryType(String value) {
		this.value = value;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}
}
