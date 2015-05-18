package com.xpos.api.param;

import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Pattern;

public class LatestRegisterMember {
	private Long id;
	
	private String name;

	@NotBlank(message="手机号码不能为空")
	@Pattern(regexp="^(1(([357][0-9])|(47)|[8][0-9]))\\d{8}$",message="手机号码不合规则")
	private String phone;
	
	private String registerDate;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getRegisterDate() {
		return registerDate;
	}

	public void setRegisterDate(String registerDate) {
		this.registerDate = registerDate;
	}
	
	
}
