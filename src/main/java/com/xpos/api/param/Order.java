package com.xpos.api.param;

import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.NotBlank;

//"mid":"商户ID",   --商户ID,必需
//"mbid":"会员ID",   --会员ID
//"phone":1388888888,   --手机号
//"sum":"金额",   --订单金额, 必需
//"device_number": "客户端设备号",  --设备号
//"csrf_token":"xxxxxx"   --csrf_token

public class Order {
	
	@NotBlank(message="商户ID不能为空")
	private String mid;
	@NotBlank(message="第三方注册的商户ID不能为空")
	private String registerMid;
	@Min(message="商户第三方注册类别", value = 1)
	private Integer type; //1:联动优势， 2:中行， 3:盛付通
//	@NotBlank(message="商户手机号不能为空")
//	@Pattern(regexp="^(1(([35][0-9])|(47)|[8][012356789]))\\d{8}$",message="手机号码不合规则")
	private String phone;
	@NotBlank(message="交易金额不能为空")
	@Pattern(regexp="^\\d+$",message="交易金额必须是纯数字，单位为分")
	private String sum;
	
	private String deviceNumber;
	
	private String csrfToken;
	
	private Integer status;
	
	@NotBlank(message="操作员账号不能为空")
	private String operator; //POS机操作员账号
	
	@NotBlank(message="Operation ticket不能为空")
	private String ticket;
	
	private String token; //用户输入的密码经过加密后的密文
	
	public String getMid() {
		return mid;
	}
	public Long getMidLong() {
		return Long.valueOf(mid);
	}
	public void setMid(String mid) {
		this.mid = mid;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getSum() {
		return sum;
	}
	public Integer getSumInt(){
		return Integer.valueOf(sum);
	}
	public void setSum(String sum) {
		this.sum = sum;
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
	public String getOperator() {
		return operator;
	}
	public void setOperator(String operator) {
		this.operator = operator;
	}
	public String getTicket() {
		return ticket;
	}
	public void setTicket(String ticket) {
		this.ticket = ticket;
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
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}

}
