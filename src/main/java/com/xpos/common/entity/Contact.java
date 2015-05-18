package com.xpos.common.entity;

import javax.persistence.Column;

import com.xpos.common.entity.face.Business;
import com.xpos.common.entity.face.Business.BusinessType;

/**
 * 
 * 联系人(可以是商户联系人, 也可以是店铺联系人)
 * @author Johnny
 *
 */
public class Contact extends BaseEntity{

	private static final long serialVersionUID = -5550519158118542692L;

	//姓名
	@Column
	private String name;
	
	//职位
	@Column
	private String jobTitle;
	
	//联系电话
	@Column
	private String telephone;
	
	//手机
	@Column
	private String mobile;
	
	//电邮
	@Column
	private String email;
	
	@Column
	private Long businessId;
	
	@Column
	private BusinessType businessType;
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getJobTitle() {
		return jobTitle;
	}
	public void setJobTitle(String jobTitle) {
		this.jobTitle = jobTitle;
	}
	public String getTelephone() {
		return telephone;
	}
	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}

	public void setBusiness(Business business) {
		businessId = business.getAccessBusinessId(Business.BusinessModel.CONTACT);
		businessType = business.getAccessBusinessType(Business.BusinessModel.CONTACT);
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
}
