package com.xpos.api.param;

import javax.validation.constraints.Min;

import org.hibernate.validator.constraints.NotBlank;

//"mid":"商户ID",   --商户ID,必需
//"device_number": "客户端设备号",  --设备号
//"orderId":"xxxx",  --订单ID，必填项
//"status":1,  --交易状态，必填项，-2：失败，3：成功， 4：商户取消
//"csrf_token":"xxxxxx"   --csrf_token，必填项

public class OrderPayment {
	
	@NotBlank(message="商户ID不能为空")
	private String mid;
	
	@NotBlank(message="第三方注册的商户ID不能为空")
	private String registerMid;
	
	@Min(message="商户第三方注册类别", value = 1)
	private Integer type; //1:联动优势， 2:中行， 3:盛付通

	private String deviceNumber;
	
	@NotBlank(message="订单ID不能为空")
	private String serial;
	
	@Min(value=-1, message="状态值范围：-1 ~ 4")
	private Integer status;
	
	@NotBlank(message="csrfToken不能为空")
	private String csrfToken;
	
	
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
	public String getCsrfToken() {
		return csrfToken;
	}
	public void setCsrfToken(String csrfToken) {
		this.csrfToken = csrfToken;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public String getSerial() {
		return serial;
	}
	public void setSerial(String serial) {
		this.serial = serial;
	}
	public String getRegisterMid() {
		return registerMid;
	}
	public void setRegisterMid(String registerMid) {
		this.registerMid = registerMid;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	
}
