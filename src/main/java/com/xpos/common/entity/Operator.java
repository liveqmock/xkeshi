package com.xpos.common.entity;

import javax.persistence.Column;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import com.xpos.common.entity.face.Person;

/**
 * 收银员账号
 * @author Johnny
 *
 */
public class Operator extends BaseEntity implements Person{
	public final static String SESSION_KEY = "_OPERATOR_";
	
	private static final long serialVersionUID = -5215308185277746697L;

	@Column
	@NotBlank(message="账号不能为空")
	@Length(max=32)
	private String username;
	
	
	@Column
	@NotBlank(message="姓名不能为空")
	@Length(max=32)
	private String realName;
	
	@Column
	@NotBlank(message="登陆密码不能为空")
	private String password;
	
	@Column
	private Shop shop;
	
	@Column(name = "level")
	private Level level;
	
	private enum Level {
		OPERATOR,MANAGER
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Shop getShop() {
		return shop;
	}

	public void setShop(Shop shop) {
		this.shop = shop;
	}

	public Level getLevel() {
		return level;
	}

	public void setLevel(Level level) {
		this.level = level;
	}
	
}
