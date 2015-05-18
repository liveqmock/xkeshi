package com.xkeshi.pojo.vo.param;

import com.xkeshi.pojo.vo.param.payment.PaymentParam;

/**
 * 支付宝扫码付请求参数
 * @author Chengj
 */
public class AlipayQRCodePaymentParam extends PaymentParam{
	private String sellerAccount;
	private String dynamicCode;
	
	public String getSellerAccount() {
		return sellerAccount;
	}
	public void setSellerAccount(String sellerAccount) {
		this.sellerAccount = sellerAccount;
	}
	public String getDynamicCode() {
		return dynamicCode;
	}
	public void setDynamicCode(String dynamicCode) {
		this.dynamicCode = dynamicCode;
	}
	
}
