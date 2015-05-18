package com.xpos.common.entity.member;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Transient;

import com.xpos.common.entity.BaseEntity;
import com.xpos.common.entity.face.Business;
import com.xpos.common.entity.face.Business.BusinessType;

public class MemberAttributeTemplate extends BaseEntity {

	private static final long serialVersionUID = -6770710049107742720L;
	
	@Column
	private String name;
	
	@Column
	private Boolean isEnabled;
	
	@Column
	private Long businessId;
	
	@Column
	private BusinessType businessType;
	
	@Transient
	private List<MemberAttribute> memberAttributeList;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Boolean isEnabled() {
		return isEnabled;
	}

	public void setEnabled(Boolean isEnabled) {
		this.isEnabled = isEnabled;
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

	public List<MemberAttribute> getMemberAttributeList() {
		return memberAttributeList;
	}

	public void setMemberAttributeList(List<MemberAttribute> memberAttributeList) {
		this.memberAttributeList = memberAttributeList;
	}

	public void setBusiness(Business business) {
		if(business != null){
			this.businessId = business.getSelfBusinessId();
			this.businessType = business.getSelfBusinessType();
		}
	}
	
}
