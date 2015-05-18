package com.xkeshi.pojo.vo.param;

/**
 * 更新支付流水相关信息
 * @author chengj
 */
public class POSUpdateInfoParam {
	private String orderType;
	private String registerMid; //第三方支付平台注册的商户号
	private int channel; //支付通道
	private String mobile;
	
	public String getOrderType() {
		return orderType;
	}
	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}
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
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	
}
