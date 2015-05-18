package com.xpos.api.param;

import org.hibernate.validator.constraints.NotBlank;

public class PartnerApplyCoupon {
	
	@NotBlank(message="channel不能为空")
	private String channel;
	
	@NotBlank(message="活动ID不能为空")
	private String coupon_info_id; //couponInfo id
	
	@NotBlank(message="token不能为空")
	private String token;
	
	public String getChannel() {
		return channel;
	}
	public void setChannel(String channel) {
		this.channel = channel;
	}
	public String getCoupon_info_id() {
		return coupon_info_id;
	}
	public void setCoupon_info_id(String coupon_info_id) {
		this.coupon_info_id = coupon_info_id;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	
}
