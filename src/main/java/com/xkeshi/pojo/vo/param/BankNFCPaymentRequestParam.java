package com.xkeshi.pojo.vo.param;

import com.xkeshi.pojo.vo.param.payment.PaymentParam;

/**
 * 银行卡NFC刷卡支付请求参数
 * @author chengj
 */
public class BankNFCPaymentRequestParam extends PaymentParam {
	private String registerMid; //商户在银行注册的商户号
	private int channel; //支付通道
	
	public String getRegisterMid() {
		return registerMid;
	}
	public void setRegisterMid(String registerMid) {
		this.registerMid = registerMid;
	}
	public int getChannel() {
		return channel;
	}
	public void setChannel(int channel) {
		this.channel = channel;
	}
	
}
