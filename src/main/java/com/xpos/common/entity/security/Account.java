package com.xpos.common.entity.security;

import javax.persistence.Column;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import com.xpos.common.entity.BaseEntity;
import com.xpos.common.entity.face.Business.BusinessType;
import com.xpos.common.entity.face.Person;


/**
 * 
 * 后台登录账号
 * @author Johnny
 *
 */
public class Account extends BaseEntity implements Person{
	
	public final static String SESSION_KEY = "_ACCOUNT_";
	
	private static final long serialVersionUID = -4172433368055554621L;
	
	@Column
	@NotBlank(message="商户账号不能为空")
	@Length(max=32)
	private String username;
	
	@Column
	@NotBlank(message="登陆密码不能为空")
	private String password;
	
	@Column
	private String salt;
		
	@Column
	private Long businessId;
	
	@Column
	private BusinessType businessType;
	
	@Column
	private Boolean enable;
	
//	@Column
//	private Boolean isInitPassword;
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getSalt() {
		return salt;
	}
	public void setSalt(String salt) {
		this.salt = salt;
	}
	public Boolean getEnable() {
		return enable;
	}
	public void setEnable(Boolean enable) {
		this.enable = enable;
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
//	public Boolean getIsInitPassword() {
//		return isInitPassword;
//	}
//	public void setIsInitPassword(Boolean isInitPassword) {
//		this.isInitPassword = isInitPassword;
//	}	
	
}
