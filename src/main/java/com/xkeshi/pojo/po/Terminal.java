package com.xkeshi.pojo.po;

import java.util.Date;

/**
 * Created by david-y on 2015/2/4.
 */

public class Terminal {
    private Long id;
    private String deviceNumber;
    private String deviceSecret;
    private TerminalType terminalType;
    private String code;
    private Long shopId;
    private Date lastLogin;
    private Date modifyDate;
    private Date createDate;
    private Boolean deleted;
    
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

	public Long getShopId() {
        return shopId;
    }

    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }

    public Date getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Date lastLogin) {
        this.lastLogin = lastLogin;
    }

    public Date getModifyDate() {
        return modifyDate;
    }

    public void setModifyDate(Date modifyDate) {
        this.modifyDate = modifyDate;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }
}
