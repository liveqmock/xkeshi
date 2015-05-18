package com.xpos.common.entity;

import javax.persistence.Column;

public class Configuration extends BaseEntity{
	
	
	private static final long serialVersionUID = 4406344797450816431L;
	
	@Column
	private String name;
	
	@Column
	private String value;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	

}
