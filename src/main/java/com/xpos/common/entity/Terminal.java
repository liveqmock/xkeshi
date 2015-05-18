package com.xpos.common.entity;

import java.util.Date;

import javax.persistence.Column;

import org.hibernate.validator.constraints.NotBlank;

/**
 * 
 * POS终端机
 * @author Johnny
 *
 */
public class Terminal extends BaseEntity{
	
	private static final long serialVersionUID = 6320798188824718094L;
	
	@Column
	@NotBlank(message="设备号不能为空")
	private String deviceNumber;
	@Column
	private String deviceSecret;
	@Column
    private TerminalType terminalType;
	@Column
    private String code;
	@Column
	private Shop shop;
	@Column
	private Date lastLogin;
	
	//设备类型
    public enum TerminalType{
    	CASHIER("收银台"), //收银台
    	ELECTRONIC_COUPON_CONSUMER("核销设备"); //电子券核销设备
    	
    	String desc;
    	TerminalType(String desc){
    		this.desc = desc;
    	}
		public String getDesc() {
			return desc;
		}
		public void setDesc(String desc) {
			this.desc = desc;
		}
    }
	
	public String getDeviceNumber() {
		return deviceNumber;
	}
	public void setDeviceNumber(String deviceNumber) {
		this.deviceNumber = deviceNumber;
	}
	public String getDeviceSecret() {
		return deviceSecret;
	}
	public void setDeviceSecret(String deviceSecret) {
		this.deviceSecret = deviceSecret;
	}
	public TerminalType getTerminalType() {
		return terminalType;
	}
	public void setTerminalType(TerminalType terminalType) {
		this.terminalType = terminalType;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public Shop getShop() {
		return shop;
	}
	public void setShop(Shop shop) {
		this.shop = shop;
	}
	public Date getLastLogin() {
		return lastLogin;
	}
	public void setLastLogin(Date lastLogin) {
		this.lastLogin = lastLogin;
	}
	
	
}
