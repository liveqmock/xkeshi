package com.xkeshi.pojo.po;

import javax.persistence.Column;


/**
 * 
* @author xiaohai
* @date 2015年3月5日 下午12:41:52
*
 */
public class Account {
	
	public final static String SESSION_KEY = "_ACCOUNT_";
	
	public static String ACCOUNT_FIRSTTIME_LOGIN = "_ACCOUNT_FIRSTTIME_LOGIN";
	
	@Column
	private Long id;

	@Column
	private String username;
	
	@Column
	private String password;
	
	/**是否是初始密码*/
	@Column(name="is_init_password")
	private Boolean isInitPassword;
	
	@Column
	private Long businessId;
	
	@Column
	private String businessType;
	
	@Column
	private Boolean deleted;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	

	public boolean isIsInitPassword() {
		return isInitPassword;
	}

	public void setIsInitPassword(boolean isInitPassword) {
		this.isInitPassword = isInitPassword;
	}

	public Long getBusinessId() {
		return businessId;
	}

	public void setBusinessId(Long businessId) {
		this.businessId = businessId;
	}

	public String getBusinessType() {
		return businessType;
	}

	public void setBusinessType(String businessType) {
		this.businessType = businessType;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	
}
