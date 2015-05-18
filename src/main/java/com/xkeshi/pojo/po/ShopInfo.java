package com.xkeshi.pojo.po;

import javax.persistence.Column;
import javax.persistence.Table;

/**
 * 
 * @author xk
 * 商户扩展信息
 */
@Table(name="shopinfo")
public class ShopInfo {
	
	@Column(name="shop_id")
	private Long  shopId;
	
	//短信后缀
	@Column(name="sms_suffix")
	private String smsSuffix;
	
	//短信通道
	@Column(name="smsChannel")
	private String smsChannel;

	public Long getShopId() {
		return shopId;
	}

	public void setShopId(Long shopId) {
		this.shopId = shopId;
	}

	public String getSmsSuffix() {
		return smsSuffix;
	}

	public void setSmsSuffix(String smsSuffix) {
		this.smsSuffix = smsSuffix;
	}

	public String getSmsChannel() {
		return smsChannel;
	}

	public void setSmsChannel(String smsChannel) {
		this.smsChannel = smsChannel;
	}
	 
	
	
}
