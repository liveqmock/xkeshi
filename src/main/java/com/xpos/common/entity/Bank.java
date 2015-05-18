package com.xpos.common.entity;

import javax.persistence.Column;

import com.xpos.common.entity.face.EncryptId;

/**
 * 银行
 * @author hk
 */
public class Bank extends BaseEntity implements EncryptId{
 
	private static final long serialVersionUID = 4421555953029085177L;

	@Column
	private String type;//类型(借记卡、信用卡)
	
	@Column
	private String name;//银行名称
	
	@Column
	private String shorthand;//银行英文简写,如CCB


	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getShorthand() {
		return shorthand;
	}

	public void setShorthand(String shorthand) {
		this.shorthand = shorthand;
	}
	

 
	
}
