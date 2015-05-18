package com.xpos.api.param;

import org.hibernate.validator.constraints.NotBlank;


public class UsedCoupon {
	
	@NotBlank(message="商户ID不能为空")
	private String mid;
	private String deviceNumber;
	private long cid; //coupon id
	private String couponInfoSerial;
	private String coupon; //coupon code
	private String phone; //mobile
	private String couponInfoName;
	private String couponInfoIntro;
	private int status;
	private String usedDate; //coupon使用日期
	private String csrfToken;
	private Long operatorId;
	
	public String getMid() {
		return mid;
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
	public long getCid() {
		return cid;
	}
	public void setCid(long cid) {
		this.cid = cid;
	}
	public String getCoupon() {
		return coupon;
	}
	public void setCoupon(String coupon) {
		this.coupon = coupon;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		if(phone != null){
			phone = phone.trim();
		}
		this.phone = phone;
	}

	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getCsrfToken() {
		return csrfToken;
	}
	public void setCsrfToken(String csrfToken) {
		this.csrfToken = csrfToken;
	}
	public String getCouponInfoName() {
		return couponInfoName;
	}
	public void setCouponInfoName(String couponInfoName) {
		this.couponInfoName = couponInfoName;
	}
	public String getCouponInfoIntro() {
		return couponInfoIntro;
	}
	public void setCouponInfoIntro(String couponInfoIntro) {
		this.couponInfoIntro = couponInfoIntro;
	}
	public String getUsedDate() {
		return usedDate;
	}
	public void setUsedDate(String usedDate) {
		this.usedDate = usedDate;
	}
	public String getCouponInfoSerial() {
		return couponInfoSerial;
	}
	public void setCouponInfoSerial(String couponInfoSerial) {
		this.couponInfoSerial = couponInfoSerial;
	}
	public Long getOperatorId() {
		return operatorId;
	}
	public void setOperatorId(Long operatorId) {
		this.operatorId = operatorId;
	}
	
}
