package com.xkeshi.pojo.vo.param;

import com.xkeshi.pojo.vo.param.payment.PaymentParam;

/**
 * 刷卡支付请求参数
 * @author chengj
 */
public class POSPaymentRequestParam extends PaymentParam {
	private String registerMid; //第三方支付平台注册的商户号
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
