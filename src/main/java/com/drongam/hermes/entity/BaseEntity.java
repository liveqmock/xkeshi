package com.drongam.hermes.entity;

import java.io.Serializable;
import java.util.Date;


public abstract class BaseEntity implements Serializable {

	private static final long serialVersionUID = 1L;
	protected String key ;
	
	protected Date createDate; // 创建日期
	
 

	public BaseEntity() {
		super();
	}
	public BaseEntity(String key, Date createDate) {
		super();
		this.key = key;
		this.createDate = createDate;
	}

	@Override
	public String toString() {
		return "BaseEntity [key=" + key + ", createDate=" + createDate +"]";
	}
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}


}
