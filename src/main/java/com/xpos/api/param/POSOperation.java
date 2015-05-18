package com.xpos.api.param;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import org.hibernate.validator.constraints.NotBlank;

public class POSOperation {
	
	@NotBlank(message="商户ID不能为空")
	private String mid;
	
	private String deviceNumber;
	
	@Min(value=0)
	@Max(value=2)
	private int act;
	private String type;
	private String version;
	
	public String getMid() {
		return mid;
	}

	public Long getMidLong() {
		return Long.valueOf(mid);
	}

	public void setMid(String mid) {
		this.mid = mid;
	}

	public String getDeviceNumber() {
		return deviceNumber;
	}

	public void setDeviceNumber(String deviceNumber) {
		this.deviceNumber = deviceNumber;
	}

	public int getAct() {
		return act;
	}

	public void setAct(int act) {
		this.act = act;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}
	
}
