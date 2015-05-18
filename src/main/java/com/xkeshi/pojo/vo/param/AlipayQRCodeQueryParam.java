package com.xkeshi.pojo.vo.param;

import org.apache.commons.lang3.StringUtils;

/**
 * 支付宝扫码付支付结果请求参数
 * @author chengj
 */
public class AlipayQRCodeQueryParam {
	private String orderType;
	private String sellerAccount;
	private String serial;
	
	public String getOrderType() {
		return orderType;
	}
	public void setOrderType(String orderType) {
		this.orderType = StringUtils.upperCase(orderType);
	}
	public String getSellerAccount() {
		return sellerAccount;
	}
	public void setSellerAccount(String sellerAccount) {
		this.sellerAccount = sellerAccount;
	}
	public String getSerial() {
		return serial;
	}
	public void setSerial(String serial) {
		this.serial = serial;
	}
	
}
