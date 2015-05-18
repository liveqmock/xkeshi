package com.xpos.common.entity;

import javax.persistence.Column;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.xpos.common.entity.face.Business;
import com.xpos.common.entity.face.Business.BusinessType;
import com.xpos.common.entity.face.EncryptId;

public class ItemCategory extends BaseEntity implements EncryptId{
	
	private static final long serialVersionUID = 7088127481080587584L;

	@Column
	@JsonIgnore
	private Long businessId;
	
	@Column
	@JsonIgnore
	private BusinessType businessType;
	
	@Column
	@Length(min=0,max=32)
	private String name;
	
	@Column
	@Max(value=100, message="sequence不能超过100")
	@Min(value=0, message="sequence不能小于0")
	private Integer sequence;//数字越大，排序越靠前
	
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

	public String getName() {
		return name;
	}



	public void setName(String name) {
		this.name = name;
	}

	public Integer getSequence() {
		return sequence;
	}

	public void setSequence(Integer sequence) {
		this.sequence = sequence;
	}

	public void setBusiness(Business business){
		businessId = business.getAccessBusinessId(Business.BusinessModel.MENU);
		businessType = business.getAccessBusinessType(Business.BusinessModel.MENU);
	}
	
	
}
