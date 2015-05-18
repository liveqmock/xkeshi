package com.xpos.api.param;

import org.hibernate.validator.constraints.NotBlank;

public class POS {
	
	@NotBlank(message="token不能为空")
	private String token;
	private String deviceNumber;
	private String operatorSessionCode;
	
	
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getDeviceNumber() {
		return deviceNumber;
	}
	public void setDeviceNumber(String deviceNumber) {
		this.deviceNumber = deviceNumber;
	}
	public String getOperatorSessionCode() {
		return operatorSessionCode;
	}
	public void setOperatorSessionCode(String operatorSessionCode) {
		this.operatorSessionCode = operatorSessionCode;
	}
	
}
