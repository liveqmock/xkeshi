package com.xpos.common.entity.security;

import javax.persistence.Column;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import com.xpos.common.entity.BaseEntity;
import com.xpos.common.entity.face.Business;
import com.xpos.common.entity.face.Business.BusinessType;
import com.xpos.common.entity.face.Person;


/**
 * 
 * 前台账号(登陆用)
 * @author Snoopy
 *
 */
public class User extends BaseEntity implements Person{
	
	public final static String SESSION_KEY = "_USER_";
	
	private static final long serialVersionUID = 6289729487285828174L;

	@Column
	@NotBlank(message="手机号不能为空")
	@Length(max=32)
	private String mobile;
	
	@Column
	@NotBlank(message="登陆密码不能为空")
	@Length(max=32)
	private String password;

	@Column
	private String name;
	
	@Column
	private String nickname;
	
	@Column
	private String sex;
	
	@Column
	private Long businessId;
	
	/*shop/merchant*/
	@Column
	private BusinessType businessType;

	public void setBusiness(Business business){
		this.businessId    = business.getSelfBusinessId();
		this.businessType  =  business.getSelfBusinessType();
	}
	
	@Column
	private String uniqueNo ; //用户的唯一识别号
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
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

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getUniqueNo() {
		return uniqueNo;
	}

	public void setUniqueNo(String uniqueNo) {
		this.uniqueNo = uniqueNo;
	}
	
	
}
