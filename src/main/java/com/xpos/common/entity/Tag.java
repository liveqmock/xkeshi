package com.xpos.common.entity;

import javax.persistence.Column;

import com.xpos.common.entity.face.Business.BusinessType;


/**
 * 
 *  便签
 * @author xk
 *
 */
public class Tag extends BaseEntity {

	private static final long serialVersionUID = 7591991516296003378L;

	@Column
	private String name;     //tag名字
	 
	@Column
	private String group;    //tag分组
	
	@Column
	private Long businessId; //所属商户或集团，null为公用的
	
	@Column
	private BusinessType businessType; //
 
	@Column
	private Boolean published;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public Long getBusinessId() {
		return businessId;
	}

	public void setBusinessId(Long businessId) {
		this.businessId = businessId;
	}

	public BusinessType getBusinessType() {
		return businessType;
	}

	public void setBusinessType(BusinessType businessType) {
		this.businessType = businessType;
	}

	public Boolean getPublished() {
		return published;
	}

	public void setPublished(Boolean published) {
		this.published = published;
	} 
	 
}
